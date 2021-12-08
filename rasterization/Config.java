package rasterization;

import java.awt.Toolkit;

public class Config {
    private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();
    public static final int SCREENSIZE_X = TOOLKIT.getScreenSize().width;
    public static final int SCREENSIZE_Y = TOOLKIT.getScreenSize().height;

    public static final double CAMERA_FOV = 70;
    public static final boolean ANTI_ALIASING = true;

    public static final boolean SPECULAR_HIGHLIGHTS = true;
    public static final boolean SHADOWS = true;
    public static final int SHADOW_RESOLUTION = ANTI_ALIASING ? 4 * SCREENSIZE_X : 2 * SCREENSIZE_X;
    
    public static final double 
        DEPTHMAP_BIAS = 0.001,
        SHADOWMAP_BIAS = 0.001,
        SHADOWMAP_FOV = 120;
}
