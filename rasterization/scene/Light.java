package rasterization.scene;

import rasterization.Config;
import rasterization.utils.Camera;
import rasterization.utils.Color;
import rasterization.utils.Pixel;
import rasterization.utils.Vector;

public class Light {
    public Vector location = new Vector(0, 0, 0);
    public Vector target = new Vector(0, 0, 0);
    public Color color = new Color(0xFFFFFF);

    public Light() {}
    public Light(double x, double y, double z) {
        this.location = new Vector(x, y, z);
    }
    public Light(Vector location, int colorHEX) {
        this.location = location;
        this.color = new Color(colorHEX);
    }

    public Light lookAt(double x, double y, double z) {
        this.target = new Vector(x, y, z);
        return this;
    }

    double[][] shadowMap;
    Camera camera;

    public void initShadowBuffer() {
        int numPixels = Config.SHADOW_RESOLUTION;
        shadowMap = new double[numPixels][numPixels];
        camera = new Camera(numPixels, numPixels, Config.SHADOWMAP_FOV);
        camera.lookAt(location, target);
    }

    public boolean isIlluminating(Vector point) {
        Pixel p = camera.project(point);

        if (p != null) {
            double bufferValue = shadowMap[(int) p.pixelX][(int) p.pixelY];
            return p.zRec + Config.SHADOWMAP_BIAS > bufferValue;
        }
        return true;
    }
}