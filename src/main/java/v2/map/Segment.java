package v2.map;

import org.joml.primitives.Rectanglef;

public class Segment {

    // The STATES can be used for debugging later when i get to fonts.
    // While iterating the hashmap in "v2.map" main loop, count each and display.

    // On changing level in v2.map. need to check if new level < segment.lowestLevel,
    // First check if segment is FULLY_LOADED.
    // if not set CURRENT_LEVEL_LOADED = false

    public static final int NO_STATE = 0;
    public static final int MARKED_FOR_REMOVAL = 1;
    public static final int FULLY_LOADED = 2;
    public static final int CURRENT_LEVEL_LOADED = 4;
    public static final int CURRENT_LEVEL_MASKED = 8;
    public static final int REMOVE = 16;
    public static final int COMPLETE = 32;
    public static final int IN_VIEW = 64;
    public static final int ALL_LEVELS_MASKED = 128;

    // On the COMPLETE state:
    // COMPLETE is set when ALL the levels of a segment is masked AND loaded.
    // There is a boolean in ProceduralMap "useMasking".
    // ...

    // I am using a 4*4 tree structure for v2.tiles. I think this is a nice solution.
    // I am using a 16px tileSet. a 2*2 structure is also easily possible. This
    // would allow for all 2^n tileSizes. But it would mean deeper search.
    // compare performance after implementing debug visualization

    protected Coordinate pos;
    private final ProceduralMap map;
    private Rectanglef bounds;
    private Tile tileTree;
    private int state;

    protected int lowestLevelLoaded;
    protected int lowestLevelAdjacentSet;


    public Segment(ProceduralMap map, int x, int y) {

        this.pos = new Coordinate(x,y);
        this.map = map;
        this.state = 0;

        this.tileTree = new Tile(
                0,
                0,
                map.topLevel());

        float x0 = x * map.segmentSize();
        float y0 = y * map.segmentSize();

        this.bounds = new Rectanglef(x0, y0,
                x0 + map.segmentSize(),
                y0 + map.segmentSize()
        );

        this.lowestLevelLoaded = map.topLevel() + 1;
        this.lowestLevelAdjacentSet = map.topLevel() + 1;
    }

    // ----------------------------------------------------------

    // setting noise and type related data;
    protected void loadTileTree(int level) {

        if (!check(FULLY_LOADED)) {

            if (level < lowestLevelLoaded) {

                tileTree.load(this,level);

                lowestLevelLoaded = level;

                set(CURRENT_LEVEL_LOADED);

                if (lowestLevelLoaded == map.bottomLevel()) {

                    set(FULLY_LOADED);
                }
            }
        }
    }
    // Called from within TILE on loading
    protected void queryNoise(Tile tile) {

        tile.elevation = map.elevation.query(
                bounds.minX + tile.x,
                bounds.minY + tile.y
        );

    }

    // Called from within TILE on masking
    protected void queryMask(Tile tile) {

        // Might just have this inside TILE

    }

    protected void setTileAdjacent(Tile tile) {

        if (tile.type == 1) {
            queryMask(tile);
        }

    }

    protected void determineType(Tile tile) {
        tile.type = 1;
        // Here we
    }

    // ----------------------------------------------------------

    // masking only done on the the v2.tiles that needs masking.
    // also, we may utilise separate masking algorithms on various
    // Tile types. Like water-v2.tiles.

    protected void setMask(int level) {

        // Use check in Map and set in Segment maybe
        if (!check(ALL_LEVELS_MASKED)) {

            if (level < lowestLevelAdjacentSet) {

                tileTree.setAdjacent(this,level);

                lowestLevelAdjacentSet = level;

                set(CURRENT_LEVEL_MASKED);

                if (lowestLevelAdjacentSet == map.bottomLevel()) {

                    set(ALL_LEVELS_MASKED);
                }
            }
        }
    }



    // ----------------------------------------------------------

    // UTILITY METHODS

    public boolean inView() {
        return map.
                camera.worldView().
                intersectsRectangle(bounds);
    }

    public boolean fullyVisible() {
        return map.
                camera.worldView().
                containsRectangle(bounds);
    }


    // ----------------------------------------------------------


    protected Tile getTile(int x, int y) {
        return tileTree.get(x, y, map.currentLevel());
    }

    // only used if there is a need to get a tile v2.map.other than the currentLevel
    protected Tile getTile(int x, int y, int level) {
        if (level < lowestLevelLoaded || level > map.topLevel()) {
            System.out.println("(Segment) -> getTile()");
            return null;
        }
        return tileTree.get(x, y, level);
    }
    // ----------------------------------------------------------

    // FLAGGING

    public boolean check(int state) {
        return (this.state & state) != 0;
    }

    protected void set(int state) {
        this.state |= state;
    }

    protected void reset(int state) {
        this.state ^= state;
    }

    protected void clearState() {
        state = NO_STATE;
    }
}
