package v2.core;

import static java.lang.System.nanoTime;

public class TimeCycle {

    private static final double SECOND = 1D;
    private double initTime;
    private double prevFrameSeconds;
    private float timeAccumulator;
    private float frameTimeLimit;
    private int fpsCount;
    private int upsCount;
    private int fps;
    private int ups;

    public TimeCycle() { this(0.25f); }

    public TimeCycle(float frameTimeLimit) { this.frameTimeLimit = frameTimeLimit; }

    public void init() {
        initTime = nanoTime();
        prevFrameSeconds = timeSeconds();
    }

    public float getFrameTime() {

        double timeSeconds = timeSeconds();
        float frameTime = (float) (timeSeconds - prevFrameSeconds);
        frameTime = Math.min(frameTime, frameTimeLimit);
        prevFrameSeconds = timeSeconds;
        timeAccumulator += frameTime;
        return frameTime;
    }

    public void update() {

        if (timeAccumulator > SECOND) {
            fps = fpsCount;
            ups = upsCount;
            fpsCount = upsCount = 0;
            timeAccumulator -= SECOND;
        }
    }

    public double timeSeconds() { return nanoTime() / 1_000_000_000.0; }

    public double runTime() { return nanoTime() - initTime; }

    public void incFpsCount() { fpsCount++; }

    public void incUpsCount() { upsCount++; }

    public int fps() { return fps > 0 ? fps : fpsCount; }

    public int ups() { return ups > 0 ? ups : upsCount; }

    public double prevFrameSeconds() { return prevFrameSeconds; }

    public float frameTimeLimit() { return frameTimeLimit; }

    public void setFrameTimeLimit(float limit) { frameTimeLimit = limit; }

}
