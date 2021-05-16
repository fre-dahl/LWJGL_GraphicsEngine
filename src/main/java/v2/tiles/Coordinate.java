package v2.tiles;

public class Coordinate {

    public int x, y;


    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate() {
        this(0,0);
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != this.getClass()) return false;
        Coordinate g = (Coordinate)o;
        return this.x == g.x && this.y == g.y;
    }

    @Override
    public int hashCode () {
        final int prime = 53;
        int result = 1;
        result = prime * result + this.x;
        result = prime * result + this.y;
        return result;
    }

    @Override
    public String toString () {
        return "(" + x + ", " + y + ")";
    }
}
