package v2.graphics.adt;

public interface BatchADT<T extends BatchOBJ>{

    void init();

    void begin();

    void end();

    void draw(T obj);

    void render(int objects);

    void flush();

    boolean isDrawing();


}
