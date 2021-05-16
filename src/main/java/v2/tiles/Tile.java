package v2.tiles;

public class Tile {

    private static final int x_pos = 0x000000FF;
    private static final int y_pos = 0x0000FF00;
    private static final int mask  = 0x00FF0000;
    private static final int type  = 0xFF000000;

    private static final int water  = 0x01000000; // tmp

    public static int x(int tile) {
        return tile & x_pos;
    }

    public static int y(int tile) {
        return tile & y_pos;
    }

    public static int mask(int tile) {
        return tile & mask;
    }

    public static int type(int tile) {
        return tile & type;
    }

    public static boolean isWater(int tile) {   // tmp
        return (tile & water) == water;
    }
}
