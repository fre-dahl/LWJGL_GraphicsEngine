package v2.graphics;

import v2.core.adt.ComponentIterator;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.*;
import static v2.graphics.SpriteComponent.*;

public class SpriteBatch extends Batch<SpriteComponent> implements ComponentIterator<SpriteComponent> {


    // Max SpriteBatch size = 8191 (since we are using short values for our indices)
    // if we want more simply use integers.

    public SpriteBatch(int size) {
        super(Math.min(size, (Short.MAX_VALUE >> 2) ));
    }

    @Override
    public void init() {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        vertices = new float[SPRITE_SIZE * size];
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        final int len = size * 6;
        final short[] indices = new short[len];
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = j;
        }
        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        final int vertex_bytes = VERTEX_SIZE * Float.BYTES;
        final int offset1_bytes = POS_SIZE * Float.BYTES;
        final int offset2_bytes = offset1_bytes + TEX_COORD_SIZE * Float.BYTES;

        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, vertex_bytes, 0);
        glVertexAttribPointer(1, TEX_COORD_SIZE, GL_FLOAT, false, vertex_bytes, offset1_bytes);
        glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE,true,vertex_bytes,offset2_bytes);
    }

    @Override
    public void draw(SpriteComponent sprite) {
        if(size - count == 0) flush();
        System.arraycopy(
                sprite.vertices(),
                0,
                vertices,
                count * SPRITE_SIZE,
                SPRITE_SIZE);
        count++;
    }

    @Override
    public void render(int count) {
        glBindVertexArray(vao);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glDrawElements(GL_TRIANGLES, count * 6, GL_UNSIGNED_SHORT,0);
    }

    @Override
    public void freeMemory() {
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
    }

    @Override
    public void next(SpriteComponent item) {

    }
}
