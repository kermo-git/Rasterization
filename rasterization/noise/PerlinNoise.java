package rasterization.noise;

import java.util.Random;

/*
 * https://mzucker.github.io/html/perlin-noise-math-faq.html
 * https://www.scratchapixel.com/lessons/procedural-generation-virtual-worlds/perlin-noise-part-2
 */
public class PerlinNoise extends Noise {
    private static final int SIZE = 256, MASK = 255;
    private final int[] hashTable = new int[2 * SIZE];

    public PerlinNoise() {
        Random random = new Random();
        int i, k, j;

        for (i = 0; i < SIZE; i++) {
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

    private int hash(int x, int y, int z) {
        return hashTable[hashTable[x] + y] + z;
    }

    private double grad(int hash, double x, double y, double z) {
        switch (hash & 15) {
            case 0:  return  x + y;
            case 1:  return -x + y;
            case 2:  return  x - y;
            case 3:  return -x - y;
            case 4:  return  x + z;
            case 5:  return -x + z;
            case 6:  return  x - z;
            case 7:  return -x - z;
            case 8:  return  y + z;
            case 9:  return -y + z;
            case 10: return  y - z;
            case 11: return -y - z;
            case 12: return  x + y;
            case 13: return -x + y;
            case 14: return -y + z;
            case 15: return -y - z;
        }
        return 0;
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

        double px = x - fx;
        double py = y - fy;
        double pz = z - fz;

        double a = grad(hash(x0, y0, z0), px,   py,   pz  );
        double b = grad(hash(x1, y0, z0), px-1, py,   pz  );
        double c = grad(hash(x0, y1, z0), px,   py-1, pz  );
        double d = grad(hash(x1, y1, z0), px-1, py-1, pz  );
        double e = grad(hash(x0, y0, z1), px,   py,   pz-1);
        double f = grad(hash(x1, y0, z1), px-1, py,   pz-1);
        double g = grad(hash(x0, y1, z1), px,   py-1, pz-1);
        double h = grad(hash(x1, y1, z1), px-1, py-1, pz-1);

        double Sx = fade(px);
        double Sy = fade(py);
        double Sz = fade(pz);

        return lerp(Sz, lerp(Sy, lerp(Sx, a, b), 
                                 lerp(Sx, c, d)),
                        lerp(Sy, lerp(Sx, e, f), 
                                 lerp(Sx, g, h)));
    }
}
