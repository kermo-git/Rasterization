package rasterization.scene;

import java.util.List;
import java.util.ArrayList;

import rasterization.utils.Pixel;
import rasterization.utils.Vector;

public class Vertex extends Vector {
    public List<Triangle> triangles = new ArrayList<>();
    public Vector normal = new Vector();
    public Pixel projection;

    public Vertex(double x, double y, double z) {
        super(x, y, z);
    }
}