package rasterization.scene;

import rasterization.utils.Color;
import rasterization.utils.Vector;

public class SmoothTriangle extends Triangle {
    public SmoothTriangle(Material material, Vertex v1, Vertex v2, Vertex v3) {
        super(material, v1, v2, v3);
    }

    @Override
    protected Color calculateColor(double w1, double w2, double w3, double zRec) {
        double z = 1.0 / zRec;
        double m1 = w1 * v1.projection.zRec;
        double m2 = w2 * v2.projection.zRec;
        double m3 = w3 * v3.projection.zRec;

        Vector surfacePoint = new Vector();
        surfacePoint.add(m1, v1);
        surfacePoint.add(m2, v2);
        surfacePoint.add(m3, v3);
        surfacePoint.scale(z);

        Vector smoothNormal = new Vector();
        smoothNormal.add(m1, v1.normal);
        smoothNormal.add(m2, v2.normal);
        smoothNormal.add(m3, v3.normal);
        smoothNormal.scale(z);
        smoothNormal.normalize();

        return material.shade(
            surfacePoint, 
            smoothNormal
        );
    }
}