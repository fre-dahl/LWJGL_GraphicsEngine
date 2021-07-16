package v2.graphics;

import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import v2.core.adt.GLObject;
import v2.core.Window;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

public class Shader extends GLObject {

    private int handle;

    private String vertexSource;
    private String fragmentSource;
    private final String filepath;

    private boolean beingUsed = false;
    private boolean compiled = false;

    // todo: Dispose shaders in Renderer after all batches are disposed. Batches can share shaders.

    public Shader(String filepath) {
        this.filepath = filepath;
        try{
            // https://www.youtube.com/watch?v=ucpi06deiyY&list=PLtrSb4XxIVbp8AKuEAlwNXDxr99e3woGE&index=8
            // Mange måter å gjøre dette på i java. Kan se på det senere.
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first pattern after #type 'pattern'
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index); // End of the line
            String firstPattern = source.substring(index,eol).trim(); // "vertex" eller "fragment"

            // Find the second pattern after #type 'pattern'
            index = source.indexOf("#type", eol) + 6; // find next. start at eol.
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            }
            else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            }
            else {
                throw new IOException("Unexpected token '" + firstPattern + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            }
            else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            }
            else {
                throw new IOException("Unexpected token '" + secondPattern + "'");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: " + filepath;
        }

    }

    public void compile() {

        if (!compiled) {

            System.out.println("COMPILING SHADER '" + filepath + "'");
            // ==============================================================
            // Compile and link the shaders
            // ==============================================================

            //glBindFragDataLocation(2,2,"df");
            int fragmentID;
            int vertexID;

            // Load and compile the vertex shader
            vertexID = glCreateShader(GL_VERTEX_SHADER);
            // Pass the shader source code to the GPU
            glShaderSource(vertexID, vertexSource);
            glCompileShader(vertexID);

            // Check if errors in compilation process
            int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
            if (success == GL_FALSE) {
                int len = glGetShaderi(vertexID,GL_INFO_LOG_LENGTH);
                System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed.");
                System.out.println(glGetShaderInfoLog(vertexID, len));
                assert false : "";
                // todo Enable assertions (Done. Gjør det i edit configurations: -ea i VM Options)
            }

            // Load and compile the fragment shader
            fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
            // Pass the shader source code to the GPU
            glShaderSource(fragmentID, fragmentSource);
            glCompileShader(fragmentID);

            // Check if errors in compilation process
            success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
            if (success == GL_FALSE) {
                // C trenger lengden av strings
                int len = glGetShaderi(fragmentID,GL_INFO_LOG_LENGTH);
                System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed.");
                System.out.println(glGetShaderInfoLog(fragmentID, len));
                assert false : "";
            }

            // Link shaders and check for errors
            handle = glCreateProgram();
            glAttachShader(handle, vertexID);
            glAttachShader(handle, fragmentID);
            glLinkProgram(handle);

            // After linking it should be appropriate to delete the shaders.
            glDeleteShader(vertexID);
            glDeleteShader(fragmentID);

            // Check for linking errors
            success = glGetProgrami(handle, GL_LINK_STATUS);
            if (success == GL_FALSE) {
                int len = glGetShaderi(handle,GL_INFO_LOG_LENGTH);
                System.out.println("ERROR: '" + filepath + "'\n\tLinking of shaders failed.");
                System.out.println(glGetProgramInfoLog(handle, len));
                assert false : "";
            }
            System.out.println("\tSuccess");
            compiled = true;
        }
    }

    public void attach() {
        if (!beingUsed) {
            glUseProgram(handle);
            beingUsed = true;
        }
    }

    public void detach() {
        if (beingUsed) {
            glUseProgram(0);
            beingUsed = false;
        }
    }

    @Override
    public void freeMemory() {
        detach();
        glDeleteProgram(handle);
    }

    public void uploadTexture(String varName, int slot) { // texture slot
        int varLocation = glGetUniformLocation(handle,varName);
        glUniform1i(varLocation,slot);
    }

    public void uploadIntArray(String varName, int[] array) {
        int varLocation = glGetUniformLocation(handle,varName);
        glUniform1iv(varLocation,array);
    }

    //todo: use the MemoryStack instead of BufferUtils
    public void uploadCombined() {
        int varLocation = glGetUniformLocation(handle,"uCombined");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16); // 4x4 matrix
            Window.get().scene().camera().combined().get(fb); // uploads matrix to buffer
            glUniformMatrix4fv(varLocation, false, fb);
        }


    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(handle,varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16); // 4x4 matrix
        mat4.get(matBuffer); // uploads matrix to buffer
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(handle,varName);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9); // 3x3 matrix
        mat3.get(matBuffer); // uploads matrix to buffer
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec4) {
        int varLocation = glGetUniformLocation(handle,varName);
        glUniform4f(varLocation,vec4.x,vec4.y,vec4.z,vec4.w);
    }

    public void uploadVec3f(String varName, Vector3f vec3) {
        int varLocation = glGetUniformLocation(handle,varName);
        glUniform3f(varLocation,vec3.x,vec3.y,vec3.z);
    }

    public void uploadVec2f(String varName, Vector2f vec2) {
        int varLocation = glGetUniformLocation(handle,varName);
        glUniform2f(varLocation,vec2.x,vec2.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(handle,varName);
        glUniform1f(varLocation,val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(handle,varName);
        glUniform1i(varLocation,val);
    }

}
