package rasterization;

import java.awt.Toolkit;

public class Config {
    private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();
    
    public static final int 
        SCREENSIZE_X = TOOLKIT.getScreenSize().width,
        SCREENSIZE_Y = TOOLKIT.getScreenSize().height;

    public static final int SHADOW_RESOLUTION = 2 * SCREENSIZE_X;

    public static final boolean 
        SPECULAR_HIGHLIGHTS = true, 
        SHADOWS = true;
    
    public static final double
        CAMERA_FOV = 70, 
        SHADOWMAP_FOV = 120,
        DEPTHMAP_BIAS = 0.001,
        SHADOWMAP_BIAS = 0.001;
}
