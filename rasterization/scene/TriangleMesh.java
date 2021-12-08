package rasterization.scene;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import rasterization.noise.Noise;
import rasterization.utils.Matrix;

public class TriangleMesh {
    public Material material;
    public List<Triangle> triangles = new ArrayList<>();
    public List<Vertex> vertices = new ArrayList<>();
    
    public TriangleMesh(Material material) {
        this.material = material;
    }

    /* * * * * * * * * *
     * TRANSFORMATIONS *
     * * * * * * * * * */

    public TriangleMesh rotate(double degX, double degY, double degZ) {
        Matrix rotation = Matrix.rotateDeg(degX, degY, degZ);
        
        for (Vertex vertex : vertices) {
            rotation.transform(vertex);
            rotation.transform(vertex.normal);
        }
        for (Triangle triangle : triangles) {
            rotation.transform(triangle.normal);
        }
        return this;
    }

    public TriangleMesh translate(double x, double y, double z) {
        Matrix translation = Matrix.translate(x, y, z);

        for (Vertex vertex : vertices) {
            translation.transform(vertex);
        }
        return this;
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * *
     * HELPER METHODS FOR CONSTRUCTING A TRIANGLE MESH *
     * * * * * * * * * * * * * * * * * * * * * * * * * */

    private List<Vertex> XYregularPolygon(int n, double radius) {
        List<Vertex> result = new ArrayList<>();
        double alpha = (2 * Math.PI) / n;

        for (int i = 0; i < n; i++) {
            Vertex v = new Vertex(0, radius, 0);
            Matrix.rotateAroundZ(alpha * i).transform(v);
            result.add(v);
        }
        vertices.addAll(result);
        return result;
    }


    private List<Vertex> YZregularPolygon(int n, double radius) {
        List<Vertex> result = new ArrayList<>();
        double alpha = (2 * Math.PI) / n;

        for (int i = 0; i < n; i++) {
            Vertex v = new Vertex(0, radius, 0);
            Matrix.rotateAroundX(alpha * i).transform(v);
            result.add(v);
        }
        vertices.addAll(result);
        return result;
    }


    private List<Vertex> XZregularPolygon(int n, double radius) {
        List<Vertex> result = new ArrayList<>();
        double alpha = (2 * Math.PI) / n;

        for (int i = 0; i < n; i++) {
            Vertex v = new Vertex(radius, 0, 0);
            Matrix.rotateAroundY(alpha * i).transform(v);
            result.add(v);
        }
        vertices.addAll(result);
        return result;
    }


    public void addTriangle(Vertex v1, Vertex v2, Vertex v3) {
        triangles.add(new Triangle(material, v1, v2, v3));
    }


    private void buildSurface(List<Vertex> row1, List<Vertex> row2, boolean isLoop) {
        Iterator<Vertex> it1 = row1.iterator();
        Iterator<Vertex> it2 = row2.iterator();

        Vertex start_1 = it1.next();
        Vertex start_2 = it2.next();

        Vertex prev_1 = start_1;
        Vertex prev_2 = start_2;

        Vertex next_1, next_2;

        while (it1.hasNext() && it2.hasNext()) {
            next_1 = it1.next();
            next_2 = it2.next();

            addTriangle(next_1, prev_2, prev_1);
            addTriangle(next_1, next_2, prev_2);

            prev_1 = next_1;
            prev_2 = next_2;
        }
        if (isLoop) {
            addTriangle(start_1, prev_2, prev_1);
            addTriangle(start_1, start_2, prev_2);
        }
    }
    private void buildPrismSurface(List<Vertex> circle1, List<Vertex> circle2) {
        buildSurface(circle1, circle2, true);
    }
    private void buildRibbon(List<Vertex> row1, List<Vertex> row2) {
        buildSurface(row1, row2, false);
    }


    public void buildPyramidSurface(Vertex apex, List<Vertex> circle) {
        vertices.add(apex);
        Iterator<Vertex> it = circle.iterator();

        Vertex start = it.next();
        Vertex prev = start, next;

        while (it.hasNext()) {
            next = it.next();
            addTriangle(apex, prev, next);
            prev = next;
        }
        addTriangle(apex, prev, start);
    }


    public void normalizeVertexNormals() {
        for (Vertex v : vertices) {
            v.normal.normalize();
        }
    }


    /* * * * * * * * * * * * * * *
     * BUILDING DIFFERENT SHAPES *
     * * * * * * * * * * * * * * */


    public TriangleMesh buildPrism(int n, double height, double radius) {
        Vertex apex1 = new Vertex(0, 0.5*height, 0);
        Vertex apex2 = new Vertex(0, -0.5*height, 0);

        Matrix moveDown = Matrix.translate(0, 0.5 * height, 0);
        Matrix moveUp = Matrix.translate(0, -0.5 * height, 0);

        List<Vertex> circle1 = XZregularPolygon(n, radius);
        for (Vertex v : circle1) {
            moveDown.transform(v);
        }

        List<Vertex> circle2 = XZregularPolygon(n, radius);
        for (Vertex v : circle2) {
            moveUp.transform(v);
        }

        buildPyramidSurface(apex2, circle2);
        buildPrismSurface(circle1, circle2);

        Collections.reverse(circle1);
        buildPyramidSurface(apex1, circle1);
        normalizeVertexNormals();

        return this;
    }


    public TriangleMesh buildAntiPrism(int n, double height, double radius) {
        Vertex apex1 = new Vertex(0, -0.5*height, 0);
        Vertex apex2 = new Vertex(0, 0.5*height, 0);

        List<Vertex> circle1 = XZregularPolygon(n, radius);
        Matrix moveDown = Matrix.translate(0, 0.5 * height, 0);
        Matrix moveUpAndRotate =
            Matrix.translate(0, -0.5 * height, 0).combine(
            Matrix.rotateAroundY(Math.PI / n));

        for (Vertex v : circle1) {
            moveUpAndRotate.transform(v);
        }

        List<Vertex> circle2 = XZregularPolygon(n, radius);
        for (Vertex v : circle2) {
            moveDown.transform(v);
        }

        buildPyramidSurface(apex1, circle1);
        buildPrismSurface(circle2, circle1);

        Collections.reverse(circle2);
        buildPyramidSurface(apex2, circle2);
        normalizeVertexNormals();

        return this;
    }


    public TriangleMesh buildBipyramid(int n, double length, double baseRadius) {
        List<Vertex> circle = XZregularPolygon(n, baseRadius);
        buildPyramidSurface(new Vertex(0, -0.5*length, 0), circle);

        Collections.reverse(circle);
        buildPyramidSurface(new Vertex(0, 0.5*length, 0), circle);
        normalizeVertexNormals();

        return this;
    }


    public TriangleMesh buildSphere(double radius, int num_meridians) {
        int num_parallels = num_meridians - 1;
        int num_corners = 2*num_meridians;

        double rotation_angle = 2*Math.PI/num_corners;

        List<List<Vertex>> meridians = new ArrayList<>();

        for (int i = 0; i < num_meridians; i++) {
            List<Vertex> meridian = XYregularPolygon(num_corners, radius);
            Matrix rotation = Matrix.rotateAroundY(i * rotation_angle);

            for (Vertex v : meridian) {
                rotation.transform(v);
            }
            meridians.add(meridian);
        }

        List<List<Vertex>> parallels = new ArrayList<>();

        for (int i = 0; i < num_parallels; i++) {
            parallels.add(new ArrayList<>());
        }

        for (List<Vertex> meridian : meridians) {
            List<Vertex> half_arc = meridian.subList(1, num_meridians);

            int i = 0;
            for (List<Vertex> parallel : parallels) {
                parallel.add(half_arc.get(i));
                i++;
            }
        }

        for (List<Vertex> meridian : meridians) {
            List<Vertex> half_arc = meridian.subList(num_meridians + 1, num_corners);
            Collections.reverse(half_arc);

            int i = 0;
            for (List<Vertex> parallel : parallels) {
                parallel.add(half_arc.get(i));
                i++;
            }
        }

        Vertex south_pole = new Vertex(0, radius, 0);
        Vertex north_pole = new Vertex(0, -radius, 0);

        List<Vertex> circle1 = parallels.get(0), circle2;
        Collections.reverse(circle1);
        buildPyramidSurface(south_pole, circle1);
        Collections.reverse(circle1);

        for (int i = 1; i < num_parallels; i++) {
            circle2 = parallels.get(i);
            buildPrismSurface(circle1, circle2);
            circle1 = circle2;
        }
        buildPyramidSurface(north_pole, circle1);
        normalizeVertexNormals();

        return this;
    }


    public TriangleMesh buildTorus(double innerRadius, double outerRadius, int level_of_detail) {
        double mean_radius = (outerRadius + innerRadius)/2;
        double tube_radius = (outerRadius - innerRadius)/2;

        double twist_rotation = Math.PI/level_of_detail;

        double triangle_side = 2*tube_radius*Math.tan(twist_rotation);
        int num_circles = (int) (2*Math.PI*outerRadius/triangle_side);

        if (num_circles%2 == 1)
            num_circles += 1;

        double curve_rotation = 2*Math.PI/num_circles;

        List<Vertex> first_circle = YZregularPolygon(level_of_detail, tube_radius);
        Matrix translation = Matrix.translate(0, 0, mean_radius);

        for (Vertex v : first_circle) {
            translation.transform(v);
        }

        List<Vertex> circle1 = first_circle, circle2 = null;

        for (int i = 1; i <= num_circles - 1; i++) {
            circle2 = YZregularPolygon(level_of_detail, tube_radius);
            Matrix twist = Matrix.rotateAroundX(i * twist_rotation);
            Matrix curve = Matrix.rotateAroundY(-i * curve_rotation);
            Matrix matrix = twist.combine(translation).combine(curve);

            for (Vertex v : circle2) {
                matrix.transform(v);
            }

            buildPrismSurface(circle1, circle2);
            circle1 = circle2;
        }
        buildPrismSurface(circle2, first_circle);
        normalizeVertexNormals();

        return this;
    }

    public TriangleMesh buildFunctionPlot(
        double unitsX, double unitsY,
        double unitSize, int density,
        Noise noise) {

        List<Vertex> prev = new ArrayList<>();
        List<Vertex> current = new ArrayList<>();

        double step = 1.0 / density;
        double rangeX = unitsX / 2;
        double rangeY = unitsY / 2;

        double x, y, z;

        for (x = -rangeX; x < rangeX; x += step) {
            for (y = -rangeY; y < rangeY; y += step) {
                z = noise.noise(x, y, 0);
                Vertex v = new Vertex(
                    x * unitSize,
                    z * unitSize,
                    y * unitSize
                );
                current.add(v);
                vertices.add(v);
            }
            if (!prev.isEmpty()) {
                buildRibbon(prev, current);
            }
            prev = new ArrayList<>(current);
            current = new ArrayList<>();
        }
        normalizeVertexNormals();

        return this;
    }
}