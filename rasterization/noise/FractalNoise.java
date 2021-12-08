package rasterization.noise;

public class FractalNoise extends Noise {
    Noise noise;
    protected int octaves = 1;
    protected double persistence = 1;

    public FractalNoise(Noise noise, int octaves, double persistence) {
        this.noise = noise;
        this.octaves = octaves;
        this.persistence = persistence;
    }

    @Override
    public double signedNoise(double x, double y, double z) {
        double total = 0;
        double frequency = 1;
        double amplitude = 1;
        double max_value = 0;

        for (int i = 0; i < octaves; i++) {
            total += amplitude * noise.signedNoise(frequency * x, frequency * y, frequency * z);
            max_value += amplitude;
            amplitude *= persistence;
            frequency *= 2;
        }
        return total/max_value;
    }
}
