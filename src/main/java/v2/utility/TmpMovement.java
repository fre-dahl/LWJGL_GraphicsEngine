package v2.utility;

import org.joml.Vector2f;
import v2.core.Camera;
import v2.core.KeyListener;

import static org.lwjgl.glfw.GLFW.*;

public class TmpMovement {

    private static final float velocity = 400f;
    private static final Vector2f position = new Vector2f();
    private static final Vector2f direction = new Vector2f();

    public static void move(Camera cam, float dt) {

        direction.set(0,0);
        position.set(0);

        if (KeyListener.isKeyPressed(GLFW_KEY_W)) direction.y = 1;
        if (KeyListener.isKeyPressed(GLFW_KEY_D)) direction.x = 1;
        if (KeyListener.isKeyPressed(GLFW_KEY_S)) direction.y = -1;
        if (KeyListener.isKeyPressed(GLFW_KEY_A)) direction.x = -1;
        if (KeyListener.isKeyPressed(GLFW_KEY_W) && KeyListener.isKeyPressed(GLFW_KEY_S)) direction.y = 0;
        if (KeyListener.isKeyPressed(GLFW_KEY_D) && KeyListener.isKeyPressed(GLFW_KEY_A)) direction.x = 0;

        float xdt = 0; float ydt = 0;
        if (direction.x != 0) {
            xdt = direction.normalize().x * velocity * dt;
            position.x = xdt;
        }
        if (direction.y != 0) {
            ydt = direction.normalize().y * velocity * dt;
            position.y = ydt;
        }

        cam.translate(position);

    }
}
