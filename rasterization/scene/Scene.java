package rasterization.scene;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rasterization.Config;
import rasterization.utils.Camera;
import rasterization.utils.Color;

import java.awt.image.BufferedImage;

public class Scene {
    public static Camera camera;
    public static List<Light> lights;
    public static List<TriangleMesh> objects;

    public static void clearScene() {
        camera = new Camera(Config.SCREENSIZE_X, Config.SCREENSIZE_Y, Config.CAMERA_FOV);

        lights = new ArrayList<>();
        objects = new ArrayList<>();
    }
    static { clearScene(); }

    public static void addObjects(TriangleMesh ...newObjects) {
        objects.addAll(Arrays.asList(newObjects));
    }
    public static void addLights(Light ...newLights) {
        lights.addAll(Arrays.asList(newLights));
    }


    public static BufferedImage renderDepthMap() {
        double[][] depthMap = new double[camera.numPixelsX][camera.numPixelsY];

        for (TriangleMesh obj : objects) {
            for (Vertex vertex : obj.vertices) {
                vertex.projection = camera.project(vertex);
            }
            for (Triangle triangle : obj.triangles) {
                triangle.rasterize(depthMap, null);
            }
        }
        return Color.toImage(depthMap);
    }


    public static BufferedImage render() {
        if (Config.SHADOWS) {
            for (Light light : lights) {
                light.initShadowBuffer();
    
                for (TriangleMesh obj : objects) {
                    for (Vertex vertex : obj.vertices) {
                        vertex.projection = light.camera.project(vertex);
                    }
                    for (Triangle triangle : obj.triangles) {
                        triangle.rasterize(light.shadowMap, null);
                    }
                }
            }
        }
        Color[][] frameBuffer = Color.getArray(camera.numPixelsX, camera.numPixelsY);
        double[][] depthMap = new double[camera.numPixelsX][camera.numPixelsY];

        for (TriangleMesh obj : objects) {
            for (Vertex vertex : obj.vertices) {
                vertex.projection = camera.project(vertex);
            }
            for (Triangle triangle : obj.triangles) {
                triangle.rasterize(depthMap, frameBuffer);
            }
        }
        return Color.toImage(frameBuffer);
    }
}
