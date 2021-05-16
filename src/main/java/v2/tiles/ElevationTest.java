package v2.tiles;

import v2.core.adt.GLObject;
import v2.graphics.Color;
import v2.graphics.FrameBuffer;
import v2.graphics.Shader;
import v2.graphics.Assets;
import v2.core.Window;


import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class ElevationTest extends GLObject {

    Shader shader;
    FrameBuffer fbo;
    ProceduralMap map;

    private final int pos_size = 2;
    private final int elev_size = 1;
    private final int vertex_size = 3;

    private final int elev_off_bytes = pos_size * Float.BYTES;
    private final int vertex_size_bytes = vertex_size * Float.BYTES;

    private final float[] vertices;
    private final int maxBatchSize;

    private int count = 0;
    private int vao, vbo, ebo;

    public ElevationTest(ProceduralMap map, int maxBatchSize) {

        this.fbo = new FrameBuffer(
                Assets.getShader("res/shaders/fboShader.glsl"),
                Window.viewportW(),
                Window.viewportH() );

        this.vertices = new float[maxBatchSize * 4 * vertex_size];
        this.shader = Assets.getShader("res/shaders/elevation.glsl");
        this.maxBatchSize = maxBatchSize;
        this.map = map;
    }

    public void init() {

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        ebo = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);



        glVertexAttribPointer(0, pos_size, GL_FLOAT, false, vertex_size_bytes, 0);
        glVertexAttribPointer(1, elev_size, GL_FLOAT, false, vertex_size_bytes, elev_off_bytes);
        //glBindVertexArray(0);

    }

    public void draw() {

        // ----- Batch begin ---------

        // could check in a renderer class if shader.equals(currentShader)
        // if not ->

        fbo.bind();
        fbo.useTextureViewport();

        shader.attach();
        // ------- UploadUniforms -----
        shader.uploadCombined();

        glBindVertexArray(vao);

        if(map.reUploadToGPU) {
            count = 0;
            loadVertexProperties();
            //glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        /// ------- Render -----------------

        // this shall be in the renderer class not in a batch;
        Window.clearColor(Color.GREEN); // have clear color as a method in batch class
        glClear(GL_COLOR_BUFFER_BIT);

        glDrawElements(GL_TRIANGLES, count * 6, GL_UNSIGNED_INT,0);

        // instead of detaching shaders all over the place.
        // do a check before using a shader instead.
        // if the shaders ar different, THEN detach the old.
        // Of course, when using a frame-buffer like we are here,
        // there will be some attaching / detaching of shaders.
        // that's inevitable.

        shader.detach();

        /*
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

         */


        fbo.unBind();

        fbo.drawTexture();

    }


    private void loadVertexProperties() {

        int index = 0;
        for (ProceduralMap.TileSegment segment : map.inView) {
            for (int r = 0; r < map.sizeSegment; r++) {
                for (int c = 0; c < map.sizeSegment; c++) {

                    count++;
                    int offset = index * vertex_size * 4;

                    float xPos = segment.position.x * map.combinedSize + c * map.sizeTile;
                    float yPos = segment.position.y * map.combinedSize + r * map.sizeTile;

                    // vertice 0 pos: x = 1 , y = 1
                    // vertice 1 pos: x = 1 , y = 0
                    // vertice 2 pos: x = 0 , y = 0
                    // vertice 3 pos: x = 0 , y = 1

                    // 0
                    vertices[offset     ] = xPos + map.sizeTile;
                    vertices[offset + 1 ] = yPos + map.sizeTile;
                    vertices[offset + 2 ] = segment.heightMap[r+1][c+1];
                    // 1
                    vertices[offset + 3 ] = xPos + map.sizeTile;
                    vertices[offset + 4 ] = yPos;
                    vertices[offset + 5 ] = segment.heightMap[r][c+1];
                    // 2
                    vertices[offset + 6 ] = xPos;
                    vertices[offset + 7 ] = yPos;
                    vertices[offset + 8 ] = segment.heightMap[r][c];
                    // 3
                    vertices[offset + 9 ] = xPos;
                    vertices[offset + 10] = yPos + map.sizeTile;
                    vertices[offset + 11] = segment.heightMap[r+1][c];

                    index++;
                }
            }
        }
    }


    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle) ------------
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            int offset = 4 * i;
            int offsetArrayIndex = 6 * i;
            //---------------------------------------------
            // quad 1               quad 2              ..n
            // 3, 2, 0, 0, 2, 1     7, 6, 4, 4, 6, 5    ..n
            //---------------------------------------------
            // Triangle 1
            elements[offsetArrayIndex] =     (offset + 3);
            elements[offsetArrayIndex + 1] = (offset + 2);
            elements[offsetArrayIndex + 2] = offset;
            // Triangle 2
            elements[offsetArrayIndex + 3] =  offset;
            elements[offsetArrayIndex + 4] = (offset + 2);
            elements[offsetArrayIndex + 5] = (offset + 1);
            //---------------------------------------------
        }
        return elements;
    }


    @Override
    public void freeMemory() {
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);

        fbo.freeMemory();
    }
}