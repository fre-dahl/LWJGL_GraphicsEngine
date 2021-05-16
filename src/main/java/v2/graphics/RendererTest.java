package v2.graphics;

import v2.core.Window;
import v2.core.Manager;
import v2.core.utility.ComponentArray;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class RendererTest extends Manager<SpriteComponent> {

    // find some good way to manage assets. refactor Assets.class

    Texture texture1;
    Shader shader1;
    FrameBuffer fbo1;
    SpriteBatch spriteBatch1;
    ComponentArray<SpriteComponent> sprites;


    @Override
    public void start() {
        sprites = new ComponentArray<>(50);
        texture1 = Assets.getTexture("res/images/colors.png");
        shader1 = Assets.getShader("res/shaders/default.glsl");
        spriteBatch1 = new SpriteBatch(100);
        spriteBatch1.init();


        texture1.bind();
        glActiveTexture(GL_TEXTURE0);
        shader1.attach();
        shader1.uploadTexture("uTex",0);
    }

    @Override
    public void update(float dt) {

        Window.clearColor(Color.BLACK);
        glClear(GL_COLOR_BUFFER_BIT);

        spriteBatch1.begin();

        shader1.uploadCombined();

        //System.out.println(sprites2.toString());
        sprites.iterate(spriteBatch1::draw);


        spriteBatch1.end();


    }

    @Override
    public void onAddComponent(SpriteComponent component) {
        sprites.add(component);
        //sprites.add(component);
    }

    @Override
    public void onRemoveComponent(SpriteComponent component) {
        sprites.remove(component);
        //sprites.remove(component);
    }

    @Override
    public void cleanUp() {
        shader1.freeMemory();
        Assets.disposeTexture("res/images/colors.png");
        spriteBatch1.freeMemory();
    }


    @Override
    public int compareTo(Manager<SpriteComponent> o) {
        return 0;
    }
}
