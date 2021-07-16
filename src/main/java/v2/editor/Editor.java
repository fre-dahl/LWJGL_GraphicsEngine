package v2.editor;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;
import v2.core.*;
import v2.core.adt.MouseListener;
import v2.graphics.*;
import v2.graphics.SpriteSheet;
import v2.core.Transform;
import v2.utility.TmpMovement;
import v2.utility.U;
import v2.utility.io.JSONConstructor;


public class Editor extends Scene implements MouseListener {

    public Editor() {
        super("Editor",new Camera(new Vector2f()));
    }

    Texture colors;
    SpriteSheet sheet;
    Sprite sprite;
    GameObject object;
    double time;

    @Override
    public void load() {

        colors = Assets.getTexture("res/images/colors.png");


        byte b = (byte) 200;
        short s = Short.MAX_VALUE >> 2;




    }

    @Override
    public void create() {

        Mouse.setListener(this);
        RendererTest test = new RendererTest();
        addSystem(test);

        TextureRegion region = new TextureRegion(0,0,10,10,100,0,16,16,"colors");
        sheet = SpriteSheet.generate(colors,region);
        sprite = new Sprite(colors);



        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {

                createObject(new Vector2f(i * 16 , j * 16));

            }
        }



    }

    @Override
    public void handleInput(float dt) {
        TmpMovement.move(camera(), dt);

        /*
        time += 2*dt;
        float value = U.map((float) Math.sin(time),-1,1,0.0f,1f);

        /*
        for (GameObject obj : objects()) {
            obj.transform().setScale(value);
            obj.transform().setRotation((float) (Math.pow(value,2)*360));
        }

         */




    }

    @Override
    public void disposeResources() {
        Assets.disposeTexture(colors.filepath());
    }

    @Override
    public void save() {

    }

    void createObject(Vector2f pos) {

        GameObject obj = new GameObject(new Transform(new Vector2f(pos.x,pos.y)));
        int index = U.rndInt(0,99);
        SpriteComponent spriteComponent = new SpriteComponent(sheet.get(index), obj.transform(), 16,16, 8,8,0,0);
        //SpriteComponent spriteComponent = new SpriteComponent(sprite, obj.transform(),true);
        obj.addComponent(spriteComponent);

        addObject(obj);

    }

    @Override
    public void l_highlightBox(Rectanglef box) {

    }

    @Override
    public void r_highlightBox(Rectanglef box) {

    }

    @Override
    public void m_highlightBox(Rectanglef box) {

    }

    @Override
    public void l_highlightBoxReleased(Rectanglef box) {

    }

    @Override
    public void r_highlightBoxReleased(Rectanglef box) {

    }

    @Override
    public void m_highlightBoxReleased(Rectanglef box) {

    }

    @Override
    public void l_dragReleased(Vector2f vec) {

    }

    @Override
    public void r_dragReleased(Vector2f vec) {

    }

    @Override
    public void m_dragReleased(Vector2f vec) {

    }

    @Override
    public void l_drag(Vector2f vec) {

    }

    @Override
    public void r_drag(Vector2f vec) {

    }

    @Override
    public void m_drag(Vector2f vec) {

    }

    @Override
    public void rightClick_View(Vector2f pos) {
        System.out.println(pos.x + " " + pos.y);
    }

    @Override
    public void rightClick_World(Vector2f pos) {
        System.out.println(pos.x + " " + pos.y);


    }

    @Override
    public void leftClick_View(Vector2f pos) {

    }

    @Override
    public void leftClick_World(Vector2f pos) {
        createObject(pos);
    }

    @Override
    public void wheelClick_View(Vector2f pos) {

    }

    @Override
    public void wheelClick_World(Vector2f pos) {

    }

    @Override
    public void hover_View(Vector2f pos) {

    }

    @Override
    public void hover_World(Vector2f pos) {

    }

    @Override
    public void scrollDown() {
        camera().zoom(0.02f);
        System.out.println(camera().getZoom());
        System.out.println(U.log2(camera().getZoom()));
    }

    @Override
    public void scrollUp() {
        camera().zoom(-0.1f);
        System.out.println(camera().getZoom());
        System.out.println(U.log2(camera().getZoom()));
    }

    @Override
    public boolean isActiveMouseListener() {
        return false;
    }

    @Override
    public boolean ignoreDragAndHighlight() {
        return true;
    }
}
