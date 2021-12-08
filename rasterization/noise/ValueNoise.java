package rasterization.noise;

import java.util.Random;

public class ValueNoise extends Noise {
    private static final int SIZE = 256, MASK = 255;
    private final int[] hashTable = new int[2 * SIZE];
    private final double[] values = new double[2 * SIZE];

    public ValueNoise() {
        Random random = new Random();
        int i, k, j;

        for (i = 0; i < SIZE; i++) {
            values[i] = random.nextBoolean() ? random.nextDouble() : -random.nextDouble();
            hashTable[i] = i;
        }
        for (i = 0; i < SIZE; i++) {
            k = hashTable[i];
            j = random.nextInt() & MASK;
            hashTable[i] = hashTable[j];
            hashTable[j] = k;
        }
        for (i = 0; i < SIZE; i++) {
            hashTable[SIZE + i] = hashTable[i];
            values[SIZE + i] = values[i];
        }
    }

    private static double fade(double t) {
        return t*t*t*(t*(t*6 - 15) + 10);
    }

    private static double lerp(double t, double a, double b) {
        return a + t*(b - a);
    }

    private static int floor(double t) {
        return (t >= 0)? (int) t : (int) t - 1;
    }

    private double value(int x, int y, int z) {
        return values[hashTable[hashTable[x] + y] + z];
    }

    @Override
    public double signedNoise(double x, double y, double z) {
        int fx = floor(x);
        int fy = floor(y);
        int fz = floor(z);
        
        int x0 = fx & MASK;
        int y0 = fy & MASK;
        int z0 = fz & MASK;
        int x1 = (x0 + 1) & MASK;
        int y1 = (y0 + 1) & MASK;
        int z1 = (z0 + 1) & MASK;

        double a = value(x0, y0, z0);
        double b = value(x1, y0, z0);
        double c = value(x0, y1, z0);
        double d = value(x1, y1, z0);
        double e = value(x0, y0, z1);
        double f = value(x1, y0, z1);
        double g = value(x0, y1, z1);
        double h = value(x1, y1, z1);

        double Sx = fade(x - fx);
        double Sy = fade(y - fy);
        double Sz = fade(z - fz);

        return lerp(Sz, lerp(Sy, lerp(Sx, a, b), 
                                 lerp(Sx, c, d)),
                        lerp(Sy, lerp(Sx, e, f), 
                                 lerp(Sx, g, h)));
    }
}
