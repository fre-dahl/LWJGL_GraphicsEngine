package v2.core;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {

    // https://www.glfw.org/docs/3.3/input_guide.html#input_key

    private static KeyListener instance;
    // 350 covers the range of keybindings for GLFW (-1 = unknown)
    private final boolean[] keyPressed = new boolean[350];

    private KeyListener() {

    }

    public static KeyListener get() {
        if (instance == null) {
            instance = new KeyListener();
        }
        return instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        // Again, mods are secondary buttons down. for instance ctrl.
        if (key == GLFW_KEY_UNKNOWN) return;

        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;
        }
        else if (action == GLFW_RELEASE) {
            get().keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int keyCode) {
        // Todo Need to implement check
        return get().keyPressed[keyCode];

    }
}

