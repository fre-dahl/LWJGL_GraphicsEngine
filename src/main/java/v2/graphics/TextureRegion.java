package v2.graphics;

public class TextureRegion {

    public String id;

    public int originX;
    public int originY;
    public int rows;
    public int cols;
    public int count;
    public int offset;
    public int spriteW;
    public int spriteH;

    public TextureRegion(int originX, int originY, int rows, int cols, int count, int offset, int spriteW, int spriteH, String id) {
        this.id = id;
        this.originX = originX;
        this.originY = originY;
        this.rows = rows;
        this.cols = cols;
        this.count = count;
        this.offset = offset;
        this.spriteW = spriteW;
        this.spriteH = spriteH;
    }

    public TextureRegion(int originX, int originY, int regionW, int regionH)  {

    }
}
