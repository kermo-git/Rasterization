package rasterization.scene;

import rasterization.Config;
import rasterization.utils.Color;
import rasterization.utils.Pixel;
import rasterization.utils.Vector;


public class Triangle {
    public Vector normal;
    public Vertex v1, v2, v3;
    public Material material;

    public double sRec = 0;

    public Triangle(Material material, Vertex v1, Vertex v2, Vertex v3) {
        this.material = material;

        this.v1 = v1; v1.triangles.add(this);
        this.v2 = v2; v2.triangles.add(this);
        this.v3 = v3; v3.triangles.add(this);

        normal = new Vector(v1, v3).cross(new Vector(v1, v2));
        sRec = 1 / normal.length();

        v1.normal.add(normal);
        v2.normal.add(normal);
        v3.normal.add(normal);
        
        normal.normalize();
    }


    private static double min(double a, double b) {
        return (a < b) ? a : b;
    }
    private static double max(double a, double b) {
        return (a > b) ? a : b;
    }
    private static double abs(double a) {
        return (a < 0) ? -a : a;
    }


    public void rasterize(double[][] depthMap, Color[][] frameBuffer) {
        Pixel p1 = v1.projection;
        Pixel p2 = v2.projection;
        Pixel p3 = v3.projection;

        if (p1 == null || p2 == null || p3 == null) {
            return;
        }
        if (p1.zRec + Config.DEPTHMAP_BIAS < depthMap[(int) p1.pixelX][(int) p1.pixelY] &&
            p2.zRec + Config.DEPTHMAP_BIAS < depthMap[(int) p2.pixelX][(int) p2.pixelY] &&
            p3.zRec + Config.DEPTHMAP_BIAS < depthMap[(int) p3.pixelX][(int) p3.pixelY]) {
            return;
        }

        double p32y = p2.pixelY - p3.pixelY;
        double p32x = p2.pixelX - p3.pixelX;

        double p13y = p3.pixelY - p1.pixelY;
        double p13x = p3.pixelX - p1.pixelX;

        double p21y = p1.pixelY - p2.pixelY;
        double p21x = p1.pixelX - p2.pixelX;

        double s = p13x * p21y - p13y * p21x;

        int low_x = (int) min(p1.pixelX, min(p2.pixelX, p3.pixelX));
        int high_x = (int) max(p1.pixelX, max(p2.pixelX, p3.pixelX));
        int low_y = (int) min(p1.pixelY, min(p2.pixelY, p3.pixelY));
        int high_y = (int) max(p1.pixelY, max(p2.pixelY, p3.pixelY));

        int x, y;
        double pixelCenterX, pixelCenterY;
        double s1, s2, s3;

        double w1, w2, w3, zRec;

        for (x = low_x; x <= high_x; x++) {
            for (y = low_y; y <= high_y; y++) {

                pixelCenterX = x + 0.5;
                pixelCenterY = y + 0.5;

                s1 = (pixelCenterX - p3.pixelX) * p32y -
                     (pixelCenterY - p3.pixelY) * p32x;

                s2 = (pixelCenterX - p1.pixelX) * p13y -
                     (pixelCenterY - p1.pixelY) * p13x;

                s3 = (pixelCenterX - p2.pixelX) * p21y -
                     (pixelCenterY - p2.pixelY) * p21x;

                // TODO: if the triangle is facing away from the camera,
                // then the condition should be s1 > 0 && s2 > 0 && s3 > 0
                // and the normal should be reversed for shading calculations.
                
                if (s1 < 0 && s2 < 0 && s3 < 0) {
                    w1 = abs(s1 / s);
                    w2 = abs(s2 / s);
                    w3 = 1 - w1 - w2;
                    zRec = w1 * p1.zRec + w2 * p2.zRec + w3 * p3.zRec;

                    if (zRec > depthMap[x][y]) {
                        depthMap[x][y] = zRec;
                        if (frameBuffer != null) {
                            frameBuffer[x][y] = calculateColor(w1, w2, w3, zRec);
                        }
                    }
                }
            }
        }
    }


    protected Color calculateColor(double w1, double w2, double w3, double zRec) {
        Vector surfacePoint = new Vector();
        surfacePoint.add(w1 * v1.projection.zRec, v1);
        surfacePoint.add(w2 * v2.projection.zRec, v2);
        surfacePoint.add(w3 * v3.projection.zRec, v3);
        surfacePoint.scale(1.0 / zRec);

        return material.shade(surfacePoint, normal);
    };
}