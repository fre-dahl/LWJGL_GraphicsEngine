package v2.graphics;

import v2.graphics.Shader;
import v2.graphics.Texture;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Assets {

    private static final Map<String, Shader> SHADER_MAP = new HashMap<>();
    private static final Map<String, Texture> TEXTURE_MAP = new HashMap<>();

    // If shader exist in v2.map -> return it, else -> create and compile then return it
    public static Shader getShader(String resourceName) {
        File file = new File(resourceName);
        if (SHADER_MAP.containsKey(file.getAbsolutePath())) {
            return SHADER_MAP.get(file.getAbsolutePath());
        } else {
            // Errors get catched in Shader.class
            Shader shader = new Shader(resourceName);
            shader.compile();
            SHADER_MAP.put(file.getAbsolutePath(),shader);
            return shader;
        }
    }

    // If texture exist in v2.map -> return it, else create it then return it
    public static Texture getTexture(String resourceName) {
        File file = new File(resourceName);
        if (TEXTURE_MAP.containsKey(file.getAbsolutePath())) {
            return TEXTURE_MAP.get(file.getAbsolutePath());
        }
        else {
            // Errors get catched in Texture.class
            Texture texture = new Texture(resourceName);
            TEXTURE_MAP.put(file.getAbsolutePath(),texture);
            return texture;
        }
    }

    // Todo; Do this right. Keeping track of texture users etc. Now you have to be sure you can delete it.
    //  delete all users first.
    public static void disposeTexture(String resourceName) {
        File file = new File(resourceName);
        if (TEXTURE_MAP.containsKey(file.getAbsolutePath())) {
            Texture tex = TEXTURE_MAP.get(file.getAbsolutePath());
            tex.freeMemory();
            TEXTURE_MAP.remove(file.getAbsolutePath());
        }
        else assert false : "Error: (AssetPool) Could not locate texture for disposal: '" + resourceName + "'";
    }



}
