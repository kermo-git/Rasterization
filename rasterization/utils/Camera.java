package rasterization.utils;

public class Camera {
    public Matrix cameraToWorld, worldToCamera;
    public Vector location;

    public int numPixelsX, numPixelsY;
    double halfImageWidth, halfImageHeight;
    double numPixelsX_imageWidth_ratio;
    double numPixelsY_imageHeight_ratio;


    public Camera(int numPixelsX, int numPixelsY, double FOVdegrees) {
        this.numPixelsX = numPixelsX;
        this.numPixelsY = numPixelsY;

        double halfFOVRadians = Math.toRadians(FOVdegrees / 2);

        halfImageWidth = Math.tan(halfFOVRadians);
        halfImageHeight = halfImageWidth * numPixelsY / numPixelsX;

        numPixelsX_imageWidth_ratio = this.numPixelsX / (2 * halfImageWidth);
        numPixelsY_imageHeight_ratio = this.numPixelsY / (2 * halfImageHeight);
    }


    public void lookAt(Vector from, Vector to) {
        location = from;
        Vector forward = new Vector(from, to);
        forward.normalize();

        Vector posYAxis = new Vector(0, 1, 0);
        Vector negYAxis = new Vector(0, -1, 0);
        Vector right = null;

        if (forward.equals(posYAxis) || forward.equals(negYAxis)) {
            right = new Vector(0, 0, 1);
        } else {
            right = posYAxis.cross(forward);
        }
        Vector up = forward.cross(right);
        cameraToWorld = Matrix.coordinateSystem(right, up, forward, from);
        worldToCamera = cameraToWorld.inverse();
    }


    public Pixel project(Vector v) {
        Vector result = worldToCamera.getTransformation(v);
        if (result.z < 0)
            return null;

        double depth = result.z;
        double imageX = result.x / depth;
        double imageY = result.y / depth;
        
        if (Math.abs(imageX) > halfImageWidth ||
            Math.abs(imageY) > halfImageHeight)
            return null;
        
        imageX += halfImageWidth;
        imageY += halfImageHeight;

        double pixelX = numPixelsX_imageWidth_ratio * imageX;
        double pixelY = numPixelsY - numPixelsY_imageHeight_ratio * imageY;

        return new Pixel(pixelX, pixelY, 1.0 / depth);
    }


    public Vector generateRay(int pixelX, int pixelY) {
        Vector start = new Vector();
        Vector end = new Vector(
            2 * halfImageWidth * ((pixelX + 0.5) / numPixelsX) - halfImageWidth, 
            2 * halfImageHeight * (1 - ((pixelY + 0.5) / numPixelsY)) - halfImageHeight, 
            1
        );
        end.normalize();
        
        cameraToWorld.transform(start);
        cameraToWorld.transform(end);
        return new Vector(start, end);
    }
}
