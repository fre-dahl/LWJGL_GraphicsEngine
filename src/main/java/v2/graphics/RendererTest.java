package v2.graphics;

import org.lwjgl.system.MemoryUtil;
import v2.core.Window;
import v2.core.Manager;
import v2.utility.Array;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class RendererTest extends Manager<SpriteComponent> {

    // find some good way to manage assets. refactor Assets.class

    Texture texture1;
    Shader shader1;
    FrameBuffer fbo1;
    SpriteBatch spriteBatch;
    Array<SpriteComponent> sprites;
    ArrayList<SpriteComponent> sprites2;


    @Override
    public void start() {

        sprites = new Array<>(2501);
        //sprites2 = new ArrayList<>();
        texture1 = Assets.getTexture("res/images/colors.png");
        shader1 = Assets.getShader("res/shaders/default.glsl");
        spriteBatch = new SpriteBatch(2600);
        Shader fboShader = Assets.getShader("res/shaders/fboShader.glsl");
        fbo1 = new FrameBuffer(fboShader,Window.viewportW(), Window.viewportH());
        spriteBatch.init();
        texture1.bind();
        glActiveTexture(GL_TEXTURE0);
        shader1.attach();
        shader1.uploadTexture("uTex",0);
    }

    @Override
    public void update(float dt) {

        long begin = System.nanoTime();
        Window.clearColor(Color.BLACK);
        glClear(GL_COLOR_BUFFER_BIT);

        fbo1.bind();
        fbo1.useTextureViewport();

        shader1.attach();
        shader1.uploadCombined();


        texture1.bind();
        glActiveTexture(GL_TEXTURE0);
        shader1.uploadTexture("uTex",0);

        Window.clearColor(Color.BLACK);
        glClear(GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();

        sprites.iterate(spriteBatch::draw);


        spriteBatch.end();

        shader1.detach();

        fbo1.unBind();
        fbo1.drawTexture();



        System.out.println(((System.nanoTime() - begin) ));
        //System.out.println(1.291E-4);
    }

    @Override
    public void onAddComponent(SpriteComponent component) {

        //sprites2.add(component);
        sprites.add(component);
    }

    @Override
    public void onRemoveComponent(SpriteComponent component) {

        //sprites2.remove(component);
        sprites.remove(component);
    }

    @Override
    public void cleanUp() {
        fbo1.freeMemory();
        shader1.freeMemory();
        Assets.disposeTexture("res/images/colors.png");
        spriteBatch.freeMemory();
    }


    @Override
    public int compareTo(Manager<SpriteComponent> o) {
        return 0;
    }
}
