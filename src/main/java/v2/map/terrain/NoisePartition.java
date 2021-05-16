package v2.map.terrain;


import v2.utility.FastNoiseLite;

public class NoisePartition {

    public NoisePartition(float weight, int seed) {
        this.noise = new FastNoiseLite(seed);
        this.weight = weight;
    }

    private final FastNoiseLite noise;
    private final float weight;


    public float query(float x, float y) {
        return Math.abs(noise.GetNoise(x, y) * weight);
    }

    public FastNoiseLite fastNoiseLite() {
        return noise;
    }

    public float weight() {
        return weight;
    }
}
