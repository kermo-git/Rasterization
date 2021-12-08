package rasterization.scene;

import rasterization.Config;
import rasterization.utils.Color;
import rasterization.utils.Vector;

public class Material {
    Color ambient = new Color(0x222222);
    Color diffuse = new Color(0xFFFFFF);
    Color specular = new Color(0xFFFFFF);
    double shininess = 0.25 * 128;

    
    public Material() {}
    public Material(Color ambient, Color diffuse, Color specular, double shininess) {
        this.ambient = new Color(ambient);
        this.diffuse = new Color(diffuse);
        this.specular = new Color(specular);
        this.shininess = shininess;
    }
    public Material(Color color, double shininess) {
        ambient = new Color(color);
        ambient.scale(0.05);
        diffuse = new Color(color);
        this.shininess = shininess;
    }


    public static Vector getReflectedRay(Vector ray, Vector normal) {
        double dot = 2 * ray.dot(normal);
        
        return new Vector(
            ray.x - dot * normal.x,
            ray.y - dot * normal.y,
            ray.z - dot * normal.z
        );
    }


    public Color shade(Vector surfacePoint, Vector normal) {
        Color result = new Color(ambient);
        Vector lightVector, reflectionVector, viewVector;
        double diffuseIntensity, specularIntensity;

        for (Light light : Scene.lights) {
            lightVector = new Vector(light.location, surfacePoint);
            lightVector.normalize();

            if (Config.SHADOWS) {
                if (!light.isIlluminating(surfacePoint))
                    continue;
            }
            diffuseIntensity = -lightVector.dot(normal);
            
            if (diffuseIntensity > 0) {
                result.add(diffuseIntensity, diffuse, light.color);

                if (Config.SPECULAR_HIGHLIGHTS) {
                    reflectionVector = getReflectedRay(lightVector, normal);
                    viewVector = new Vector(Scene.camera.location, surfacePoint);
                    viewVector.normalize();
                    specularIntensity = -reflectionVector.dot(viewVector);

                    if (specularIntensity > 0) {
                        specularIntensity = Math.pow(specularIntensity, shininess);
                        result.add(specularIntensity, specular, light.color);
                    }
                }
            }
        }
        return result;
    }
}
