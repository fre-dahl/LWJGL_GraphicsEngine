package v2.core;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;
import v2.core.adt.MouseListener;
import v2.tiles.ElevationTest;
import v2.tiles.ProceduralMap;
import v2.utility.FastNoiseLite;
import v2.utility.TmpMovement;
import v2.utility.U;

import java.util.ArrayList;

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
