package v2.map;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;
import v2.utility.FastNoiseLite;
import v2.map.terrain.Noise;
import v2.map.terrain.NoisePartition;
import v2.utility.U;
import v2.core.adt.CameraListener;
import v2.core.Camera;
import v2.core.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProceduralMap implements CameraListener {

    // I eventually want to set up imgui with adjustable parameters.

    // LEVELS OF DETAIL

    private final int levels;
    private final int topLevel;
    private final int bottomLevel;

    private int currentLevel;

    // DEBUGGING / COUNTERS

    int numActive;
    int numInView;
    int numFullyLoaded;
    int numCurrentLevelLoaded;
    int numMarkedForRemoval;

    // SEGMENT HASHMAP and FIELDS

    protected Map<Coordinate,Segment> activeSegments;
    protected Coordinate tmpKey = new Coordinate();
    protected Coordinate wvMin, wvMax;

    private final int segmentSize;
    private final float segmentSizeInv;
    private final int segmentSizeLog2;
    private final int segmentBinMask;

    private boolean useTileMasking;

    // SEGMENT LISTS, STACKS and QUEUES.

    private List<Segment> inView;

    // CAMERA

    private ZoomLog2 zoomLog2;
    protected Camera camera;
    float cameraDelta;
    private boolean worldViewChanged;

    // NOISE

    protected Noise elevation;
    protected Noise humidity;

    // -------



    // Will this work with one level?

    public ProceduralMap(int levels, int bottomLevel) {

        // LEVELS OF DETAIL
        this.levels = levels;
        this.bottomLevel = bottomLevel;
        this.topLevel = bottomLevel + levels - 1;
        this.currentLevel = bottomLevel;

        // SEGMENT RELATED FIELDS
        this.segmentSize = (int) Math.pow(4,topLevel);
        this.segmentSizeInv = 1f / segmentSize;
        this.segmentSizeLog2 = U.log2(segmentSize);
        this.segmentBinMask = segmentSize - 1;

        // COLLECTIONS
        this.activeSegments = new HashMap<>();
        this.inView = new ArrayList<>();

        // INITIAL SETUP
        setUpCameraAndZoom();
        setUpNoise();
        setUpSegments();

    }

    //-------------------------------------------------------------------------------------
    // LOOP


    private void update() {

        if (worldViewChanged) {

            inView.clear();



        }

        worldViewChanged = false;
    }





    //-------------------------------------------------------------------------------------
    // SETUP


    private void setUpSegments() {

        Rectanglef worldView = camera.worldView();

        int xMin = (int) (worldView.minX * segmentSizeInv);
        int yMin = (int) (worldView.minY * segmentSizeInv);
        int xMax = (int) (worldView.maxX * segmentSizeInv);
        int yMax = (int) (worldView.maxY * segmentSizeInv);

        this.wvMin = new Coordinate(xMin,yMin);
        this.wvMax = new Coordinate(xMax,yMax);

        Segment segment;

        // load segments (querying noise and setting type-related data)

        for (int r = (wvMin.y - 1); r <= (wvMax.y + 1); r++) {
            for (int c = (wvMin.x - 1); c <= (wvMax.x + 1); c++) {

                if (!existAsActiveSegment(c,r)) {
                    segment = new Segment(this, c,r);
                    addAsActiveSegment(segment);
                }
                else
                    segment = activeSegments.get(tmpKey);

                segment.loadTileTree(currentLevel);

            }
        }

        // masking (querying adjacent v2.tiles to set mask)

        if (this.isUsingMasking()) {

            for (int r = wvMin.y; r <= wvMax.y; r++) {
                for (int c = wvMin.x; c <= wvMax.x; c++) {

                    // atp, we know the segment exist
                    segment = getSegment(c,r);
                    segment.setMask(currentLevel);
                }
            }
        }

        worldViewChanged = true;

    }

    private void setUpCameraAndZoom() {

        camera = Window.get().scene().camera();
        camera.addListener(this);
        cameraDelta = 16f;

        float zoomOffset = 0f;
        int log2Differance = 1;
        int log2Bottom = 0;

        zoomLog2 = new ZoomLog2(this,
                log2Bottom,
                log2Differance,
                zoomOffset);

    }

    private void setUpNoise() {

        elevation = new Noise();
        FastNoiseLite fnl;

        NoisePartition primaryElevation = new NoisePartition(1, 1337);

        fnl = primaryElevation.fastNoiseLite();
        fnl.SetFractalOctaves(6);
        fnl.SetFractalLacunarity(0.6f);
        fnl.SetFrequency(0.00004f);

        NoisePartition secondaryElevation = new NoisePartition(0.1f, 1337);

        fnl = secondaryElevation.fastNoiseLite();
        fnl.SetSeed(123);
        fnl.SetFrequency(0.001f);
        fnl.SetFractalType(FastNoiseLite.FractalType.Ridged);

    }


    //-------------------------------------------------------------------------------------
    // TILES


    // USED OUTSIDE PACKAGE (SCENE)
    public Tile getTile(Vector2f v2) {

        int x = (int)v2.x;
        int y = (int)v2.y;

        Segment s = getSegment(
                x >> segmentSizeLog2,
                y >> segmentSizeLog2);

        if (s == null) {
            System.out.println("(ProceduralMap) getTile returns null");
            return null;
        }
        return s.getTile(
                x & segmentBinMask,
                y & segmentBinMask);
    }

    // USED INSIDE PACKAGE (SEGMENT)
    protected Tile getTile(int x, int y) {

        Segment s = getSegment(
                x >> segmentSizeLog2,
                y >> segmentSizeLog2);

        if (s == null) {
            System.out.println("(ProceduralMap) getTile returns null");
            return null;
        }
        return s.getTile(
                x & segmentBinMask,
                y & segmentBinMask);
    }


    //-------------------------------------------------------------------------------------
    // SEGMENTS


    protected boolean existAsActiveSegment(int x, int y) {

        tmpKey.set(x,y);

        return activeSegments.containsKey(tmpKey);
    }

    private Segment getSegment(int x, int y) {

        tmpKey.set(x,y);

        return activeSegments.get(tmpKey);
    }

    protected void addAsActiveSegment(Segment s) {
        activeSegments.put(s.pos,s);
    }

    protected void removeFromActiveSegments(Segment s) {
        activeSegments.remove(s.pos,s);
    }


    //-------------------------------------------------------------------------------------
    // CAMERA LISTENER METHODS (TRACKING "WORLD VIEW")


    @Override
    public void onCameraZoom(Rectanglef worldView, float zoom) {

        zoomLog2.onCameraZoom(zoom);

        int xMin = (int) (worldView.minX * segmentSizeInv);
        int yMin = (int) (worldView.minY * segmentSizeInv);
        int xMax = (int) (worldView.maxX * segmentSizeInv);
        int yMax = (int) (worldView.maxY * segmentSizeInv);

        wvMin.set(xMin,yMin);
        wvMax.set(xMax,yMax);

        worldViewChanged = true;
    }

    @Override
    public void onCameraTranslation(Rectanglef worldView, Vector2f position) {

        int xMin = (int) (worldView.minX * segmentSizeInv);
        int yMin = (int) (worldView.minY * segmentSizeInv);
        int xMax = (int) (worldView.maxX * segmentSizeInv);
        int yMax = (int) (worldView.maxY * segmentSizeInv);

        wvMin.set(xMin,yMin);
        wvMax.set(xMax,yMax);

        worldViewChanged = true;
    }

    // CALLBACK FROM CLASS: "MapZoomLog2"
    protected void zoomTransition(int toLevel) {

    }


    //-------------------------------------------------------------------------------------
    // TRIVIAL


    public int numLevels() {
        return levels;
    }

    public int topLevel() {
        return topLevel;
    }

    public int bottomLevel() {
        return bottomLevel;
    }

    public int currentLevel() {
        return currentLevel;
    }

    public int segmentSize() {
        return segmentSize;
    }

    public void toggleMasking(boolean b) {

        // update segment states and do reloading if necessary.
        // HERE

        useTileMasking = b;
    }

    public boolean isUsingMasking() {
        return useTileMasking;
    }
}
