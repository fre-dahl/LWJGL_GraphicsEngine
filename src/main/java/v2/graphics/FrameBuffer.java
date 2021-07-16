package v2.graphics;

import org.lwjgl.BufferUtils;
import v2.core.adt.GLObject;
import v2.core.Window;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer extends GLObject {

    private int handle = NOT_INITIALIZED;
    private Texture texture = null;
    private Shader shader;

    private int vao, vbo, ebo;


    public FrameBuffer(Shader shader, int width, int height) {
        this.shader = shader;
        init(width, height);
    }

    public void init(int width, int height) {

        if (handle != NOT_INITIALIZED)
            freeMemory();

        handle = glGenFramebuffers();

        bind();
        // Create the texture to render to, and attach it to the fbo
        this.texture = new Texture(width,height);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,texture.handle(),0);

        // Create the render buffer to store the depth info (check depth to understand it)
        int rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER,rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32,width,height);

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER,rbo);
        assert glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE
                : "ERROR: FrameBuffer is not complete";
        unBind();

        generateBuffers();

    }

    private void generateBuffers() {

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        final float[] vertexArray = {

                // position       // texCoord
                 1.0f, -1.0f,     1.0f, 0.0f, // Bottom right 0
                -1.0f,  1.0f,     0.0f, 1.0f, // Top left     1
                 1.0f,  1.0f ,    1.0f, 1.0f, // Top right    2
                -1.0f, -1.0f,     0.0f, 0.0f, // Bottom left  3
        };

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        final int[] elementArray = {

                2, 1, 0, // Top right triangle
                0, 1, 3  // bottom left triangle
        };

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Set up Shader attributes

        final int fboPosSize = 2;
        final int fboTexCoordSize = 2;
        final int fboVertexSizeBytes = (fboPosSize + fboTexCoordSize) * Float.BYTES;

        glVertexAttribPointer(0, fboPosSize, GL_FLOAT, false, fboVertexSizeBytes, 0);
        glVertexAttribPointer(1, fboTexCoordSize, GL_FLOAT, false, fboVertexSizeBytes, fboPosSize * Float.BYTES);
        //glBindVertexArray(0);
    }

    public void drawTexture() {


        shader.attach();
        texture.bind();

        glBindVertexArray(vao);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        Window.defaultViewport();

        final int elementArrLength = 6;

        glDrawElements(GL_TRIANGLES, elementArrLength, GL_UNSIGNED_INT, 0);

        //glDisableVertexAttribArray(0);
        //glDisableVertexAttribArray(1);
        //glBindVertexArray(0);

        // instead of detaching shaders all over the place.
        // do a check before using a shader instead.
        // if the shaders ar different, THEN detach the old.

        shader.detach();
        texture.unbind();

    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, handle);
    }

    public void unBind() {
        glBindFramebuffer(GL_FRAMEBUFFER,0);
    }

    public void useTextureViewport() {
        glViewport(0,0, texture.width(),texture.height());
    }

    @Override
    public void freeMemory() {
        deleteTexture();
        deleteBuffers();
    }

    private void deleteTexture() {
        texture.freeMemory();
    }

    private void deleteBuffers() {
        glDeleteFramebuffers(handle);
        glDeleteBuffers(ebo);
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
}
