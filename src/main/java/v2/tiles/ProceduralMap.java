package v2.tiles;

import v2.graphics.Assets;
import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;
import v2.utility.FastNoiseLite;
import v2.core.Camera;
import v2.core.Window;

import java.util.*;

import static v2.tiles.ProceduralMap.TileSegment.*;

public class ProceduralMap {


    int sizeTile;
    int sizeSegment;
    int combinedSize;
    float camera_dt;
    float deSpawnDist2 = Window.width() * 4; // og denne
    float timeToRemove = 10f; // lek med denne

    boolean masking = false;
    boolean reUploadToGPU; // Gets checked in renderer

    Map<Coordinate, TileSegment> segments;
    Queue<TileSegment> loadQueue;
    Stack<TileSegment> borderSegments;
    Stack<TileSegment> toDiscardStack;
    Stack<TileSegment> discardStack;
    Stack<TileSegment> removeFromMarked;
    List<TileSegment> discardedSegments;
    List<TileSegment> markedForRemoval;
    List<TileSegment> inView;
    Vector2f lastCameraPos;
    FastNoiseLite noise1;
    FastNoiseLite noise2;
    Coordinate tmp;
    Camera camera;


    public ProceduralMap(FastNoiseLite noise1, int sizeTile, int sizeSegment) {
        this.segments = new HashMap<>();
        this.lastCameraPos = new Vector2f();
        this.camera = Window.get().scene().camera();
        this.noise1 = noise1;
        this.sizeTile = sizeTile;
        this.sizeSegment = sizeSegment;
        this.combinedSize = sizeTile * sizeSegment;
        this.camera_dt = sizeTile/2f;
        this.markedForRemoval = new ArrayList<>();
        this.discardedSegments = new ArrayList<>();
        this.loadQueue = new LinkedList<>();
        this.borderSegments = new Stack<>();
        this.removeFromMarked = new Stack<>();
        this.toDiscardStack = new Stack<>();
        this.discardStack = new Stack<>();
        this.inView = new ArrayList<>();
        this.tmp = new Coordinate();
        this.noise2 = new FastNoiseLite();
    }

    public void dispose() {
        Assets.disposeTexture("res/images/tileTest.png");
    }

    public void init() {

        noise2.SetSeed(123);
        noise2.SetFrequency(0.001f);
        noise2.SetFractalType(FastNoiseLite.FractalType.Ridged);
        noise1.SetFractalOctaves(6);
        noise1.SetFractalLacunarity(0.6f);
        noise1.SetFrequency(0.00004f);
        int x = (int)(camera.position().x / combinedSize);
        int y = (int)(camera.position().y / combinedSize);
        segments.clear();
        loadQueue.clear();
        borderSegments.clear();
        discardStack.clear();
        markedForRemoval.clear();
        discardedSegments.clear();
        inView.clear();
        new TileSegment(x,y).init();
        loadSegments();
        reUploadToGPU = true;

    }

    public void update(){

        loadSegments();

        if (cameraMoved()) {
            reUploadToGPU = true;
            inView.clear();
            for (Map.Entry<Coordinate,TileSegment> entry : segments.entrySet()) {
                TileSegment segment = entry.getValue();

                if (segment.visible()) {
                    if (segment.check(READY)) {
                        inView.add(segment);
                    }
                    if (!segment.fullyVisible()) {
                        borderSegments.push(segment);
                    }
                }
                else {
                    if (!segment.check(MARKED_FOR_REMOVAL)) {
                        if (segment.deSpawnDistReached()) {
                            segment.set(MARKED_FOR_REMOVAL);
                            markedForRemoval.add(segment);
                        }
                    }
                }
            }

            while (!borderSegments.isEmpty()) {
                borderSegments.pop().checkAdjacent();
            }
            while (!discardStack.isEmpty()) {
                TileSegment segment = discardStack.pop();
                segments.remove(segment.position);
                //passToPool(segment);
            }
            lastCameraPos.set(camera.position());
        }
        else {
            reUploadToGPU = false;
        }

        for (TileSegment segment : markedForRemoval) {
            // use stream for this later
            if (segment.visible()) {
                segment.reset(MARKED_FOR_REMOVAL);
                segment.resetTimer();
                removeFromMarked.push(segment);
            }
            else {
                if (segment.timerFull()) {
                    segment.reset(MARKED_FOR_REMOVAL);
                    segment.check(REMOVE);
                    removeFromMarked.push(segment);
                    discardStack.push(segment);
                }
                else {
                    segment.incTimer();
                }
            }
        }

        while (!removeFromMarked.isEmpty()) {
            markedForRemoval.remove(removeFromMarked.pop());
        }

    }


    private void loadSegments() {
        // could check. if loaded add to masking
        if (!loadQueue.isEmpty()) {
            while (!loadQueue.isEmpty()) {
                TileSegment segment = loadQueue.remove();
                queryNoise2(segment);
                if (masking) {
                    setMask(segment);
                }
                segment.set(READY);
            }
        }
    }

    private void queryNoise2(TileSegment segment) {
        float e;
        float e2;
        float n;
        for (int row = 0; row < sizeSegment+1; row++) {
            for (int col = 0; col < sizeSegment+1; col++) {
                float nx = (col * sizeTile + combinedSize * segment.position.x);
                float ny = (row * sizeTile + combinedSize * segment.position.y);

                e2 = Math.abs(noise2.GetNoise(nx, ny));
                e = Math.abs(noise1.GetNoise(nx, ny));
                n = (e + e2*0.10f) * 0.9090909090909091f;

                segment.heightMap[row][col] = n;
            }
        }
    }

    private void queryNoise(TileSegment segment) {
        float e;
        float e2;
        float n;
        for (int row = 0; row < sizeSegment; row++) {
            for (int col = 0; col < sizeSegment; col++) {
                float nx = (col*sizeTile+combinedSize*segment.position.x);
                float ny = (row*sizeTile+combinedSize*segment.position.y);

                e2 = noise2.GetNoise(nx,ny);
                e = noise1.GetNoise(nx,ny);

                n = (e + e2) * 0.5f;
                if (n<0.1f) {
                    segment.tiles[row][col] = 8;
                }
                else if (n < 0.2){
                    segment.tiles[row][col] = 7;
                }
                else if (n < 0.3){
                    segment.tiles[row][col] = 6;
                }
                else if (n < 0.4){
                    segment.tiles[row][col] = 5;
                }
                else if (n < 0.5){
                    segment.tiles[row][col] = 4;
                }
                else if (n < 0.6){
                    segment.tiles[row][col] = 3;
                }
                else if (n < 0.8){
                    segment.tiles[row][col] = 2;
                }
                else {
                    segment.tiles[row][col] = 1;
                }

            }
        }
    }

    private void setMask(TileSegment segment) {

    }

    private void passToPool(TileSegment segment) {
        // clear the segment.
        // have a method for create, that utilize pool
        discardedSegments.add(segment);
    }

    private boolean cameraMoved() {
        return !camera.position().equals(lastCameraPos,camera_dt);
    }

    class TileSegment{

        static final int MARKED_FOR_REMOVAL = 1;
        static final int VISITED = 2;
        static final int LOADED = 4;
        static final int MASK_SET = 8;
        static final int REMOVE = 16;
        static final int READY = 32;

        Vector2f center;
        Rectanglef bounds;
        Coordinate position;
        int[][] tiles;
        float[][] heightMap;

        float timer;
        int state;

        TileSegment(int x, int y) {
            tiles = new int[sizeSegment][sizeSegment];
            heightMap = new float[sizeSegment+1][sizeSegment+1];
            bounds = new Rectanglef();
            bounds.setMin(x*combinedSize,y*combinedSize);
            bounds.setMax(x*combinedSize + combinedSize, y*combinedSize + combinedSize);
            center = new Vector2f(x+(combinedSize/2f),y+(combinedSize/2f));
            position = new Coordinate(x,y);
            timer = 0f;
            state = 0;
            segments.put(position,this);
            loadQueue.add(this);
        }

        void init() {
            if (visible()) {
                for (int x = -1; x < 2; x++) {
                    for (int y = -1; y < 2; y++) {
                        if (x == 0 && y == 0) continue;
                        int nX = position.x + x;
                        int nY = position.y + y;
                        if (!exist(nX,nY)) {
                            TileSegment segment = new TileSegment(nX,nY);
                            addToSegments(segment);
                            segment.init();
                        }
                    }
                }
            }
        }

        void checkAdjacent() {
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    if (x == 0 && y == 0) continue;
                    int nX = position.x + x;
                    int nY = position.y + y;
                    if (!exist(nX,nY)) {
                        addToSegments(new TileSegment(nX,nY));
                    }
                }
            }
        }

        boolean visible() {
            return camera.worldView().intersectsRectangle(bounds);
        }

        boolean fullyVisible() {
            return camera.worldView().containsRectangle(bounds);
        }

        boolean exist(int x, int y) {
            tmp.set(x,y);
            return segments.containsKey(tmp);
        }

        void addToSegments(TileSegment segment) {
            segments.put(segment.position, segment);
        }

        boolean same(Coordinate position) {
            return this.position.equals(position);
        }

        void incTimer() {
            timer += Window.dt();
        }

        void resetTimer() {
            timer = 0;
        }

        boolean timerFull() {
            return timer >= timeToRemove;
        }

        boolean deSpawnDistReached() {
            float dX = camera.x() - center.x();
            float dY = camera.y() - center.y();
            return (dX*dX+dY*dY) > deSpawnDist2;
        }

        boolean check(int state) {
            return (this.state & state) != 0;
        }

        void set(int state) {
            this.state |= state;
        }

        void reset(int state) {
            this.state ^= state;
        }


    }


}
