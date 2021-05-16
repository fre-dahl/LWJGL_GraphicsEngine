package v2.map;

public class Tile {

    // If memory for some reason becomes a bottleneck
    // I could concentrate the fields to a single int,
    // and cut storing humidity after type-setting.

    private static final int NO_TYPE = 0;

    protected int x,y;
    protected int type;
    protected int mask;
    protected int level;
    protected float elevation;
    protected float humidity;

    protected Tile[] adjacent;
    protected Tile[][] tiles;

    public Tile(int x, int y, int level) {
        tiles = new Tile[4][4];
        this.level = level;
        this.x = x;
        this.y = y;
    }

    // init tree-structure, query noise and determine type

    protected void load(Segment segment, int level) {

        if (this.level == level) {
            segment.queryNoise(this);
            segment.determineType(this);
        }
        else {
            int nextLevel = this.level - 1;

            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {

                    int x = this.x | (1 << (nextLevel << 1)) * c;
                    int y = this.y | (1 << (nextLevel << 1)) * r;

                    Tile tile;

                    if (nextLevel < segment.lowestLevelLoaded) {

                        tile = new Tile(x,y,nextLevel);
                        tiles[r][c] = tile;
                    }
                    else tile = tiles[r][c];

                    tile.load(segment,level);
                }
            }

            // todo:
            //  check if type is not already set.
            //  at this point, we are not querying noise of "parent" v2.tiles,
            //  as we use the [0][0] "child" noise.
            //  So it is needed to call the "determineType" method in "segment"
            //  explicitly. This will happen to all "parents" as lower levels are loaded.
            //  There is no need to set Type data more than once.

            if (this.type == NO_TYPE) {
                this.elevation = tiles[0][0].elevation;
                this.humidity = tiles[0][0].humidity;
                segment.determineType(this);
            }

        }

    }

    // query adjacent v2.tiles for masking
    // should be called after all adjacent segments are loaded.

    protected void setAdjacent(Segment segment, int level) {
        if (this.level == level) {
            segment.setTileAdjacent(this);
        }
        else {
            if (level < segment.lowestLevelAdjacentSet) {
                segment.setTileAdjacent(this);

                int nextLevel = this.level - 1;

                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 4; c++) {
                        tiles[r][c].setAdjacent(segment,nextLevel);
                    }
                }
            }
        }
    }

    public Tile get(int x, int y, int level) {
        if (this.level == level) return this;
        int nextLevel = this.level - 1;
        int c = (x >> (nextLevel << 1)) & 0x03;
        int r = (y >> (nextLevel << 1)) & 0x03;
        return tiles[r][c].get(x, y, level);
    }

    public int size() {
        return 1 << (level << 1);
    }


}
