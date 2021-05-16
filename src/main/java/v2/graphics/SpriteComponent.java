package v2.graphics;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;
import v2.core.Transform;
import v2.core.adt.TransformListener;
import v2.core.Component;
import v2.graphics.adt.BatchOBJ;

public class SpriteComponent extends Component implements BatchOBJ, TransformListener {



    // Use GameObject's passed in transform to transform sprite.
    // If you use this without GameObject transform, use methods directly.

    public static final int POS_SIZE = 2;
    public static final int TEX_COORD_SIZE = 2;
    public static final int COLOR_SIZE = 1;
    public static final int VERTEX_SIZE = POS_SIZE + TEX_COORD_SIZE + COLOR_SIZE;
    public static final int SPRITE_SIZE = 4 * VERTEX_SIZE;

    // upper right --------------------------------
    public static final int X1 = 0;   // x + width
    public static final int Y1 = 1;   // y + height
    public static final int U1 = 2;   // u2
    public static final int V1 = 3;   // v2
    public static final int C1 = 4;   // color
    // bottom right -------------------------------
    public static final int X2 = 5;   // x + width
    public static final int Y2 = 6;   // y
    public static final int U2 = 7;   // u2
    public static final int V2 = 8;   // v
    public static final int C2 = 9;   // color
    // bottom left --------------------------------
    public static final int X3 = 10;   // x
    public static final int Y3 = 11;   // y
    public static final int U3 = 12;   // u
    public static final int V3 = 13;   // v
    public static final int C3 = 14;   // color
    // upper left ---------------------------------
    public static final int X4 = 15;   // x
    public static final int Y4 = 16;   // y + height
    public static final int U4 = 17;   // u
    public static final int V4 = 18;   // v2
    public static final int C4 = 19;   // color
    // --------------------------------------------

    final float[] vertices = new float[SPRITE_SIZE];

    private Sprite sprite;
    private Rectanglef bounds;
    private final Color color;

    private float x, y;
    private float width;
    private float height;

    private float originX;
    private float originY;
    private float offsetX;
    private float offsetY;

    private float rotation;

    private float scaleX;
    private float scaleY;

    private boolean dirty;


    public SpriteComponent(Sprite sprite, Transform transform, boolean centered) {
        this(
                sprite,
                transform,
                sprite.spriteW,
                sprite.spriteH,
                sprite.spriteW / 2,
                sprite.spriteH / 2,
                centered ? -sprite.spriteW / 2 : 0,
                centered ? -sprite.spriteH / 2 : 0
        );
    }

    public SpriteComponent(Sprite sprite, Transform transform, float width, float height, float originX, float originY, float offsetX, float offsetY) {

        this.sprite = sprite;

        this.width = width;
        this.height = height;
        this.originX = originX;
        this.originY = originY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        Vector2f scale = transform.scale();
        scaleX = scale.x;
        scaleY = scale.y;

        Vector2f position = transform.position();
        x = position.x + offsetX;
        y = position.y + offsetY;

        rotation = transform.rotation();

        color = Color.WHITE.copy();
        updateColor();

        transform.addListener(this);

        dirty = true;
    }


    public void setSprite(Sprite sprite) {
        // check if spriteDimensions are the same? .. nah
        this.sprite = sprite;
    }


    public void setColor(Color c) {
        color.set(c);
        updateColor();
    }

    public void setAlpha (float a) {
        color.setA(a);
        updateColor();
    }

    // if modified externally, update must be called
    public Color color(){
        return color;
    }

    public void updateColor() {
        float f = color.floatBits();
        vertices[C1] = f;
        vertices[C2] = f;
        vertices[C3] = f;
        vertices[C4] = f;
    }

    public void setSize(float width, float height) {

        this.width = width;
        this.height = height;

        if (dirty) return;;
        if (rotation != 0 || scaleX != 1 || scaleY != 1) {
            dirty = true;
            return;
        }

        float x2 = x + width;
        float y2 = y + height;

        float[] vertices = this.vertices;

        vertices[X1] = x2;
        vertices[Y1] = y2;
        vertices[X2] = x2;
        vertices[Y2] = y;
        vertices[X3] = x;
        vertices[Y3] = y;
        vertices[X4] = x;
        vertices[Y4] = y2;
    }

    public void setPosition (float x, float y) {
        this.x = x + offsetX;
        this.y = y + offsetY;
        dirty = true;
    }

    public void translate (float xAmount, float yAmount) {

        x += xAmount;
        y += yAmount;

        if (dirty) return;
        if (rotation != 0 || scaleX != 1 || scaleY != 1) {
            dirty = true;
            return;
        }
        float[] vertices = this.vertices;

        vertices[X1] += xAmount;
        vertices[Y1] += yAmount;
        vertices[X2] += xAmount;
        vertices[Y2] += yAmount;
        vertices[X3] += xAmount;
        vertices[Y3] += yAmount;
        vertices[X4] += xAmount;
        vertices[Y4] += yAmount;
    }

    public float[] vertices() {

        if (dirty) {

            dirty = false;

            final float worldOriginX = x + originX;// + offsetX;
            final float worldOriginY = y + originY;// + offsetY;

            float fx = -originX;
            float fy = -originY;
            float fx2 = width - originX;
            float fy2 = height - originY;

            if (scaleX != 1 || scaleY != 1) {
                fx *= scaleX;
                fy *= scaleY;
                fx2 *= scaleX;
                fy2 *= scaleY;
            }

            final float p1x = fx2;
            final float p1y = fy2;

            final float p2x = fx2;
            final float p2y = fy;

            final float p3x = fx;
            final float p3y = fy;

            final float p4x = fx;
            final float p4y = fy2;

            float x1,y1, x2,y2, x3,y3, x4,y4;

            if (rotation != 0) {

                final double radians = Math.toRadians(rotation);
                final float cos = (float) Math.cos(radians);
                final float sin = (float) Math.sin(radians);

                x1 = cos * p1x - sin * p1y;
                y1 = sin * p1x + cos * p1y;

                x2 = cos * p2x - sin * p2y;
                y2 = sin * p2x + cos * p2y;

                x3 = cos * p3x - sin * p3y;
                y3 = sin * p3x + cos * p3y;
                // clever:
                x4 = x1 + (x3 - x2);
                y4 = y3 - (y2 - y1);

            } else {

                x1 = p1x;
                y1 = p1y;

                x2 = p2x;
                y2 = p2y;

                x3 = p3x;
                y3 = p3y;

                x4 = p4x;
                y4 = p4y;
            }

            vertices[X1] = x1 + worldOriginX;
            vertices[Y1] = y1 + worldOriginY;

            vertices[X2] = x2 + worldOriginX;
            vertices[Y2] = y2 + worldOriginY;

            vertices[X3] = x3 + worldOriginX;
            vertices[Y3] = y3 + worldOriginY;

            vertices[X4] = x4 + worldOriginX;
            vertices[Y4] = y4 + worldOriginY;

            vertices[U1] = sprite.u2;
            vertices[V1] = sprite.v2;

            vertices[U2] = sprite.u2;
            vertices[V2] = sprite.v;

            vertices[U3] = sprite.u;
            vertices[V3] = sprite.v;

            vertices[U4] = sprite.u;
            vertices[V4] = sprite.v2;
        }

        return vertices;
    }

    public void setRotation (float degrees) {
        this.rotation = degrees;
        dirty = true;
    }

    public void setScale (float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        dirty = true;
    }

    public void setOrigin (float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
        dirty = true;
    }

    public void setOffset(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        dirty = true;
    }

    public Texture texture() {
        return sprite.texture();
    }


    @Override
    public void onTransformScaling(Vector2f scale) {
        setScale(scale.x,scale.y);
    }

    @Override
    public void onTransformTranslation(Vector2f translation) {
        translate(translation.x, translation.y);
    }

    @Override
    public void onSetPosition(Vector2f position) {
        setPosition(position.x, position.y);
    }

    @Override
    public void onTransformRotation(float rotation) {
        setRotation(rotation);
    }
}
