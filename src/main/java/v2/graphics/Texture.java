package v2.graphics;

import org.lwjgl.BufferUtils;
import v2.core.adt.GLObject;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture extends GLObject {

    private final String filepath;
    private int width, height;
    private final int handle;

    public Texture(int width, int height) {

        this.width = width;
        this.height = height;
        this.filepath = "Generated";
        handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, handle);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, // See if i need GL_RGBA later instead
                0, GL_RGB, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
        unbind();
    }

    public Texture(String filepath) {

        this.filepath = filepath;
        // Generate texture on GPU
        handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, handle);

        // --------------- Set texture parameters: ---------------------------------
        // Repeat image in both directions
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // When stretching the image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // When shrinking the image, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        // --------------------------------------------------------------------------

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        // For some reason gabe choose to flip the image here
        stbi_set_flip_vertically_on_load(true);
        // RGBA / RGB data stuffed in image
        ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

        if(image != null) {

            // apparently the w and h are stored in the 0 index of the buffers
            this.width = width.get(0); this.height = height.get(0);

            // ---------------------------- Typical Cases ------------------------------
            if (channels.get(0) == 3) { // 3 channels RGB
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0),
                        0, GL_RGB, GL_UNSIGNED_BYTE, image);
            }
            else if(channels.get(0) == 4) { // 4 channels RGBA
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0),
                        0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            }
            // --------------------------------------------------------------------------
            // Other cases. Can't handle this atm. Could add later.
            else {
                assert false : "Error: (Texture) unknown number of channels '" + channels.get(0) + "' in '" + filepath + "'";
            }
            // --------------------------------------------------------------------------

        } else {
            assert false : "Error: (Texture) Could not load image '" + filepath + "'";
        }
        width.clear();
        height.clear();
        channels.clear();
        stbi_image_free(image); // This frees the bytebuffer 'image'
        unbind();
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, handle);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D,0);
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int handle() { return handle; }

    public String filepath() { return filepath; }

    @Override
    public void freeMemory() {
        glDeleteTextures(handle);
    }
}
