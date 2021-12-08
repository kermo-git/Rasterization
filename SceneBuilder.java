import java.awt.image.BufferedImage;

import rasterization.noise.*;
import rasterization.scene.*;
import rasterization.utils.Color;
import rasterization.utils.Vector;

public class SceneBuilder {
    public static BufferedImage renderScene() {
        Scene.clearScene();
        Scene.camera.lookAt(new Vector(), new Vector(0, 0, 1));

        Material material = new Material(new Color(0xAAAAAA), 0.4 * 128);

        Scene.addLights(
            new Light(new Vector(-4, -1, 0), 0xFF99FF).lookAt(0, 0, 7),
            new Light(new Vector(4, 4, 0), 0x66FF66).lookAt(0, 0, 7)
        );
        Scene.addObjects(
            new SmoothMesh(material)
                .buildFunctionPlot(50, 50, 1, 20, new PerlinNoise())
                .rotate(144, 0, 0)
                .translate(0, 0, 12), 

            new SmoothMesh(material)
                .buildTorus(1, 2, 20)
                .rotate(-21.6, 0, 0)
                .translate(0, 0, 7), 

            new SmoothMesh(material)
                .buildSphere(1.5, 20)
                .rotate(-21.6, 0, 0)
                .translate(4, 2, 12), 

            new TriangleMesh(material)
                .buildAntiPrism(7, 4, 1)
                .rotate(0, 0, 90)
                .rotate(0, 18, 0)
                .translate(-5, 2, 12)
        );
        return Scene.render();
    }
}
