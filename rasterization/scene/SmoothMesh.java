package rasterization.scene;

public class SmoothMesh extends TriangleMesh {
    public SmoothMesh(Material material) {
        super(material);
    }
    @Override
    public void addTriangle(Vertex v1, Vertex v2, Vertex v3) {
        triangles.add(new SmoothTriangle(material, v1, v2, v3));
    }
}
