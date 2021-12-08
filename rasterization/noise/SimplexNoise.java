package rasterization.noise;

import java.util.Random;

/*
 * http://staffwww.itn.liu.se/~stegu/simplexnoise/simplexnoise.pdf
 * https://www.csee.umbc.edu/~olano/s2002c36/ch02.pdf
 */
public class SimplexNoise extends Noise {
    private static final int SIZE = 256, MASK = 255;
    private final int[] hashTable = new int[2 * SIZE];

    public SimplexNoise() {
        Random random = new Random();
        int i;
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

    private int i, j, k;
    private double u, v, w;

    @Override
    public double signedNoise(double x, double y, double z) {
        double s = (x + y + z)/3.0;
        i = floor(x + s);
        j = floor(y + s);
        k = floor(z + s);
        
        s = (i + j + k)/6.0;
        u = x - i + s;
        v = y - j + s;
        w = z - k + s;
        
        int i1, j1, k1, i2, j2, k2;

        if (u > v) {
            if (v > w) {
                i1 = 1; j1 = 0; k1 = 0; i2 = 1; j2 = 1; k2 = 0;
            } else if (w > u) {
                i1 = 0; j1 = 0; k1 = 1; i2 = 1; j2 = 0; k2 = 1;
            } else {
                i1 = 1; j1 = 0; k1 = 0; i2 = 1; j2 = 0; k2 = 1;
            }
        } else {
            if (w < u) {
                i1 = 0; j1 = 1; k1 = 0; i2 = 1; j2 = 1; k2 = 0;
            } else if (w > v) {
                i1 = 0; j1 = 0; k1 = 1; i2 = 0; j2 = 1; k2 = 1;
            } else {
                i1 = 0; j1 = 1; k1 = 0; i2 = 0; j2 = 1; k2 = 1;
            }
        }
        
        double n0 = influence(0, 0, 0);
        double n1 = influence(i1, j1, k1);
        double n2 = influence(i2, j2, k2);
        double n3 = influence(1, 1, 1);
        
        return 3*(n0 + n1 + n2 + n3);
    }

    private static int floor(double t) {
        return (t >= 0)? (int)t : (int)t - 1;
    }

    private double influence(int di, int dj, int dk) {
        double s = (di + dj + dk)/6.0;
        double x = u - di + s;
        double y = v - dj + s;
        double z = w - dk + s;

        double t = 0.6 - (x*x + y*y + z*z);
        if (t < 0) return 0;

        int h = hash(i + di, j + dj, k + dk);
        return 8*t*t*t*t*grad(h, x, y, z);
    }

    private int hash(int k, int l, int m) {
        return hashTable[hashTable[k & MASK] + l & MASK] + m;
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
}