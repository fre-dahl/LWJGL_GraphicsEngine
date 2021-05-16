package v2.map.terrain;

import v2.core.exceptions.TerrainPropertyWeightException;
import v2.core.exceptions.TerrainTypeQueryException;
import v2.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public abstract class Terrain {

    private final TerrainPropertyList terrain;
    private final Map<String,TerrainProperty> properties;

    //todo: implement loading, saving from file.

    public Terrain() {
        this("Terrain");
    }

    public Terrain(String descriptor) {
        this(descriptor,Color.WHITE.copy(),0f);
    }

    public Terrain(String descriptor, Color color, float colorInfluence) {

        terrain = new TerrainPropertyList(
                descriptor,
                1f,
                color);
        terrain.setColorInfluence(colorInfluence);
        properties = new HashMap<>();
        terrain.setColorInfluence(0f);
        setUp();
        validate();
        initPropertyColorMixing();
    }

    public abstract void setUp();



    // This is done at runtime. We want to avoid to many checks for performance.
    // It's important to pass in arguments corresponding to levels of terrainProperties.

    public TerrainProperty query(float height)
            throws TerrainTypeQueryException{

        float sumWeight = 0;
        for (TerrainProperty t : terrain) {
            sumWeight += t.absoluteWeight();
            if (height < sumWeight) {
                return t;
            }
        }
        throw new TerrainTypeQueryException("Query could not find TerrainProperty");
    }

    public TerrainProperty query(float height, float humidity)
            throws TerrainTypeQueryException{

        TerrainPropertyList currentList = terrain;
        float sumWeight = 0;
        for (TerrainProperty t : currentList) {
            sumWeight += t.absoluteWeight();
            if (height < sumWeight) {
                currentList = (TerrainPropertyList) t;
                break;
            }
        }
        sumWeight = 0;
        for (TerrainProperty t : currentList) {
            sumWeight += t.absoluteWeight();
            if (humidity < sumWeight) {
                return t;
            }
        }
        throw new TerrainTypeQueryException("Query could not find TerrainProperty");
    }

    public TerrainProperty query(float height, float humidity, float rarity)
            throws TerrainTypeQueryException{

        TerrainPropertyList currentList = terrain;
        float sumWeight = 0;
        for (TerrainProperty t : currentList) {
            sumWeight += t.absoluteWeight();
            if (height < sumWeight) {
                currentList = (TerrainPropertyList) t;
                break;
            }
        }
        sumWeight = 0;
        for (TerrainProperty t : currentList) {
            sumWeight += t.absoluteWeight();
            if (humidity < sumWeight) {
                currentList = (TerrainPropertyList) t;
                break;
            }
        }
        sumWeight = 0;
        for (TerrainProperty t : currentList) {
            sumWeight += t.absoluteWeight();
            if (rarity < sumWeight) {
                return t;
            }
        }
        throw new TerrainTypeQueryException("Query could not find TerrainProperty");
    }

    public TerrainProperty query(float[] noise) throws

            ArrayIndexOutOfBoundsException,
            TerrainTypeQueryException {

        int index = 0;

        TerrainProperty current = null;
        TerrainPropertyList currentList = terrain;

        do {
            float sumWeight = 0;
            for (TerrainProperty t : currentList) {
                sumWeight += t.absoluteWeight();
                if (noise[index] < sumWeight) {
                    current = t;
                    break;
                }
            }
            index++;

            if (!(current instanceof TerrainPropertyList))
                return current;

            else currentList = (TerrainPropertyList) current;

        } while (index < noise.length);

        throw new TerrainTypeQueryException(
                "Out of bounds. Higher number of noise-array arguments than " +
                        "actual levels of TerrainProperties in Terrain");
    }

    private void validate() throws TerrainPropertyWeightException {
        if (!terrain.validate())
            throw new TerrainPropertyWeightException(
                    "All TerrainPropertyLists must be filled");
    }

    private void initPropertyColorMixing() {
        terrain.adjustColor();
    }

    public TerrainPropertyList get() {
        return terrain;
    }


}
