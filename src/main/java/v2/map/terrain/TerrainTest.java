package v2.map.terrain;

import v2.graphics.Color;

public class TerrainTest extends Terrain {

    @Override
    public void setUp() {

        TerrainPropertyList lowTerrain = new TerrainPropertyList("Low Terrain",0.3f, Color.WHITE.copy());

        TerrainProperty water = new TerrainProperty("water",0.7f,Color.BLUE.copy());
        TerrainProperty shore = new TerrainProperty("shore",1,Color.YELLOW.copy());

        lowTerrain.addToFront(shore);
        lowTerrain.addToFront(water);

        TerrainPropertyList midTerrain = new TerrainPropertyList("Mid Terrain",1, Color.PINK);
        midTerrain.setColorInfluence(0.5f);
        TerrainProperty grass = new TerrainProperty("grass",1,Color.GREEN.copy());
        midTerrain.addToFront(grass);


        TerrainPropertyList highTerrain = new TerrainPropertyList("High Terrain",1, Color.BLUE.copy());
        TerrainProperty hills = new TerrainProperty("hills",1,Color.GREEN.copy());
        highTerrain.addToFront(hills);

        get().addToFront(highTerrain);
        get().addToFront(midTerrain);
        get().addToFront(lowTerrain);

        midTerrain.adjustRelativeWeight(2);
    }
}
