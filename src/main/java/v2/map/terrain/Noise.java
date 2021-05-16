package v2.map.terrain;

import java.util.ArrayList;
import java.util.List;

public class Noise {

    private final List<NoisePartition> list;
    private float normal;

    public Noise() {
        list = new ArrayList<>();
        normal = 0;
    }

    public void add(NoisePartition partition) {
        list.add(partition);
        float sumOfWeight = 0;
        for (NoisePartition p : list) {
            sumOfWeight += p.weight();;
        }
        normal = 1 / sumOfWeight;
    }

    public float query(float x, float y) {
        float noise = 0;
        for (NoisePartition p : list) {
            noise += p.query(x,y);
        }
        return noise * normal;
    }

    public float querySingle(float x, float y) {
        return list.get(0).query(x,y);
    }


}
