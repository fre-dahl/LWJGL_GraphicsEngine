package v2.graphics;


public class Sprite {

    private final Texture texture;
    public float u, v, u2, v2;
    public float spriteW;
    public float spriteH;

    public Sprite(Texture texture, float spriteW, float spriteH, float u, float v, float u2, float v2) {
        this.texture = texture;
        this.spriteW = spriteW;
        this.spriteH = spriteH;
        this.u = u;
        this.v = v;
        this.u2 = u2;
        this.v2 = v2;
    }

    public Sprite(Texture texture, float spriteW, float spriteH) {
        this(texture, spriteW, spriteH, 0,0,1,1);

        float invTxWidth = 1f / texture.width();
        float invTxHeight = 1f / texture.height();
        float fix = 0.001f;

        u +=  fix * invTxWidth;
        u2 -= fix * invTxWidth;
        v +=  fix * invTxHeight;
        v2 -= fix * invTxHeight;
    }

    public Sprite(Texture texture) {
        this(texture, texture.width(),texture.height());
    }

    public Texture texture() {
        return texture;
    }


}
