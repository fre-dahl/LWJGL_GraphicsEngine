package v2.core;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;
import v2.core.adt.MouseListener;
import v2.tiles.ElevationTest;
import v2.tiles.ProceduralMap;
import v2.utility.FastNoiseLite;
import v2.utility.TmpMovement;
import v2.utility.U;

public class TestScene extends Scene implements MouseListener {

    float lastZoomValue = camera().getZoom();
    int zoomFactor = 0;
    int bottomLevel = 2;
    int topLevel = 4;
    int currentLevel = 2;

    //public TileRenderer renderer;
    public v2.map.ProceduralMap map2;
    public ElevationTest elevTest;
    public ProceduralMap map;
    public TestScene(String title) {
        super(title, new Camera(new Vector2f()));
    }

    @Override
    public void load() {

        /*
        Matrix4f matrix = new Matrix4f();
        System.out.println(matrix.toString());
        System.out.println();
        matrix.translate(100,100,0);
        System.out.println(matrix.toString());
        System.out.println();
        //matrix.scale(2,2,1);
        //System.out.println(matrix.toString());
        //System.out.println();
        matrix.rotateZ((float) Math.toRadians(90));
        System.out.println(matrix.toString());
        System.out.println();

        matrix.translate(100,100,0);
        System.out.println(matrix.toString());
        System.out.println();

        Vector3f position = new Vector3f(1,1,0);

        position = position.mulPosition(matrix);

        System.out.println();
        System.out.println(position);

        // translate(0, 0, -radius).rotateX(pitch).rotateY(yaw).translate(-centerPosition)

         */

        /*
        Matrix4f transform = new Matrix4f();
        float scale = 2;

        Vector3f rectBottomLeft = new Vector3f(1,1,0);
        Vector3f rectTopRight = new Vector3f(4,4,0);

        float baryCenterX = (rectBottomLeft.x + rectTopRight.x) / 2;
        float baryCenterY = (rectBottomLeft.y + rectTopRight.y) / 2;

        Vector3f baryCenter = new Vector3f(baryCenterX,baryCenterY,0);

        transform.translate(baryCenter).scale(scale,scale,1).translate(-baryCenter.x,-baryCenter.y,0);

        rectBottomLeft.mulProject(transform);
        rectTopRight.mulProject(transform);

         */


        /*
        float width = 3;
        float height = 4;

        Vector3f center = new Vector3f(3,3,0);
        Matrix4f transform = new Matrix4f();

        transform.translate(center).rotateZ((float)Math.toRadians(90)).scale(2,2,1).translate(-center.x,-center.y,0);

        center.mulProject(transform);

        System.out.println(center);

        Vector3f position = new Vector3f(300,300,0);
        Vector2f scale = new Vector2f(100,100);
        Vector3f point = new Vector3f(50,50,0);
        Vector3f offset = new Vector3f(-50,-50,0);

        transform.identity().
                translate(position).
                rotateZ((float)Math.toRadians(90)).
                translate(-offset.x,-offset.y, 0);

        point.mulProject(transform);

        System.out.println();
        System.out.println(point);

         */




    }

    @Override
    public void create() {
        Mouse.setListener(this);
        map = new ProceduralMap(new FastNoiseLite(),8,16);
        map.init();
        elevTest = new ElevationTest(map,80000);
        elevTest.init();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        map.update();
        elevTest.draw();


    }

    @Override
    public void handleInput(float dt) {
        TmpMovement.move(camera(), dt);

    }

    @Override
    public void save() {

    }


    @Override
    public void disposeResources() {
        //renderer.delete();
        elevTest.freeMemory();
        map.dispose();
    }

    @Override
    public void imgui() {
        //ImGui.begin("Test Window");
        //ImGui.text("Some random text");
        //ImGui.end();
    }

    public void onZoomInn() {

    }


    public void onZoom() {

        float zoomValue = camera().getZoom();

        if (lastZoomValue < zoomValue) {

            if (zoomValue >= Math.pow(2,zoomFactor + 1)) {


                if (currentLevel != topLevel) {
                    System.out.println();
                    zoomFactor++;
                    currentLevel++;
                    System.out.println("ZoomFactor: " + zoomFactor);
                    System.out.println("CurrentLevel: " + currentLevel);

                }

            }
        }
        else if (lastZoomValue > zoomValue) {

            if (zoomValue <= Math.pow(2,zoomFactor - 1)) {


                if (currentLevel != bottomLevel) {
                    System.out.println();
                    zoomFactor--;
                    currentLevel--;
                    System.out.println("ZoomFactor: " + zoomFactor);
                    System.out.println("CurrentLevel: " + currentLevel);
                }

            }

        }

        lastZoomValue = zoomValue;

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
        System.out.println("Drag left released  " + (int)vec.x + " " + (int)vec.y);
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
        //System.out.println("Drag right  " + (int)vec.x + " " + (int)vec.y);
    }

    @Override
    public void m_drag(Vector2f vec) {
        //System.out.println("Drag wheel  " + (int)vec.x + " " + (int)vec.y);
    }

    @Override
    public void rightClick_View(Vector2f pos) {
        System.out.println("viewport right:  " + (int)pos.x + " " + (int)pos.y);
    }

    @Override
    public void rightClick_World(Vector2f pos) {
        System.out.println("world right:  " + (int)pos.x + " " + (int)pos.y);
    }

    @Override
    public void leftClick_View(Vector2f pos) {
        System.out.println("viewport left:  " + (int)pos.x + " " + (int)pos.y);
    }

    @Override
    public void leftClick_World(Vector2f pos) {
        System.out.println("world left:  " + (int)pos.x + " " + (int)pos.y);

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
        return true;
    }

    @Override
    public boolean ignoreDragAndHighlight() {
        return false;
    }
}
