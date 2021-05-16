package v2.graphics;

public class TextureRegion {

    public String id;

    public int x0,y0;
    public int rows;
    public int cols;
    public int count;
    public int offset;
    public int spriteW;
    public int spriteH;

    public TextureRegion(int x0, int y0, int rows, int cols, int count, int offset, int spriteW, int spriteH, String id) {
        this.id = id;
        this.x0 = x0;
        this.y0 = y0;
        this.rows = rows;
        this.cols = cols;
        this.count = count;
        this.offset = offset;
        this.spriteW = spriteW;
        this.spriteH = spriteH;
    }
}
