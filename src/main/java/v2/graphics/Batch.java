package v2.graphics;

import v2.core.adt.GLObject;
import v2.graphics.adt.BatchADT;
import v2.graphics.adt.BatchOBJ;

import java.nio.FloatBuffer;

public abstract class Batch<T extends BatchOBJ> extends GLObject implements BatchADT<T> {


    // when rendering tiles
    // do not check idx for every tile, but after every segment rendered.
    // there should therefore be extra room for a plus 1 segment.
    // and we do checks after a segment is finished.


    protected float[] vertices;
    protected FloatBuffer buffer;

    protected int vao, vbo, ebo;

    protected int size;
    protected int count;
    protected int renderCalls;
    protected int totalRenderCalls;
    // max rendercalls

    private boolean drawing;

    public Batch(int size) {
        this.size = size;
        totalRenderCalls = 0;
        renderCalls = 0;
        count = 0;
    }

    @Override
    public void flush() {
        if (count == 0) return;
        buffer.flip();
        totalRenderCalls++;
        renderCalls++;
        render(count);
        buffer.clear();
        count = 0;
    }

    @Override
    public void begin() {
        if (drawing) throw new IllegalStateException("Batch.end must be called before begin.");
        renderCalls = 0;
        drawing = true;
    }

    @Override
    public void end() {
        if (!drawing) throw new IllegalStateException("Batch.begin must be called before end.");
        if (count > 0) flush();
        drawing = false;
    }

    @Override
    public boolean isDrawing() {
        return drawing;
    }

    public int size() {
        return size;
    }

    public int renderCalls() {
        return renderCalls;
    }

    public int totalRenderCalls() {
        return totalRenderCalls;
    }
}
