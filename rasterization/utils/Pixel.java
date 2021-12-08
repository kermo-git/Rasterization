package rasterization.utils;

public class Pixel {
    public double pixelX, pixelY, zRec;

    public Pixel(double pixelX, double pixelY, double zReciprocal) {
        this.pixelX = pixelX;
        this.pixelY = pixelY;
        this.zRec = zReciprocal;
    }
}
