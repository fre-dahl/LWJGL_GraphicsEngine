package v2.core;

import org.lwjgl.glfw.GLFWCharCallbackI;
import v2.editor.Editor;
import v2.graphics.Color;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    // https://www.lwjgl.org/guide

    private TimeCycle timer;

    private float dt;
    private float fps;
    private int viewportX;
    private int viewportY;
    private int width, height;
    private int viewportWidth;
    private int viewportHeight;
    private float aspectRatio;
    private float viewW_normalized;
    private float viewH_normalized;

    private boolean minimized;

    private long glfwWindow;
    private static Window window = null;

    private Scene currentScene = null;
    private int currentSceneID = -1;

    private Window() {}


    public static Window get() {

        if (Window.window == null) {
            Window.window = new Window();
        }
        return window;
    }

    public void start(WinConfig config) {
        initialize(config);
        mainLoop();
        terminate();
    }

    private void initialize(WinConfig config) {

        String platform = System.getProperty("os.name") + ", " + System.getProperty("os.arch") + ".";
        int numProcessors = Runtime.getRuntime().availableProcessors();
        int JREMemoryMb = (int)(Runtime.getRuntime().maxMemory() / 1000000L);
        String jre = System.getProperty("java.version");

        System.out.println("\nWelcome!\n");
        System.out.println("SYSTEM INFO\n");

        System.out.println("---Running on: " + platform);
        System.out.println("---JRE: " + jre);
        System.out.println("---Available processors: " + numProcessors);
        System.out.println("---Reserved memory: " + JREMemoryMb + " Mb");

        System.out.println("---LWJGL: " + Version.getVersion());
        System.out.println("---GLFW : " + glfwGetVersionString());

        System.out.println("\nINITIALIZING GLFW WINDOW");
        // Set up an Error Callback
        GLFWErrorCallback.createPrint(System.err).set();
        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to Initialize GLFW.");
        }
        // Configure GLFW
        //glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        int resizeable = config.resizable ? GL_TRUE : GL_FALSE;
        glfwWindowHint(GLFW_RESIZABLE, resizeable);

        // Create window
        long monitor = config.fullScreen ?  glfwGetPrimaryMonitor() : NULL;
        glfwWindow = glfwCreateWindow(config.width,config.height,config.windowTitle,monitor,NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }
        // Calls this function whenever there is a function callback
        //glfwSetWindowCloseCallback(window, window_close_callback);
        glfwSetCursorPosCallback(glfwWindow, Mouse::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, Mouse::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, Mouse::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetCharCallback(glfwWindow, new GLFWCharCallbackI() {
            @Override
            public void invoke(long window, int codepoint) {

                if (codepoint == GLFW_KEY_ENTER) System.out.println(44);
                if ((codepoint & 0x7F) == codepoint) {
                    System.out.println((char) codepoint);
                }
            }
        });

        // Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        if (config.enableVsync) { // Enable V-sync
            glfwSwapInterval(1); // Follows refresh rate of monitor
        }
        else glfwSwapInterval(0);



        // Get/set proper window dimensions after config
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetWindowSize(glfwWindow, width,height);

        this.width = width[0];
        this.height = height[0];
        this.viewportX = 0;
        this.viewportY = 0;
        this.viewportWidth = this.width;
        this.viewportHeight = this.height;
        this.viewW_normalized = 1f / this.width;
        this.viewH_normalized = 1f / this.height;

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.

        // Set resize callback after we make the current context.
        glfwSetWindowSizeCallback(glfwWindow,this::resizeCallback);
        glfwSetWindowIconifyCallback(glfwWindow,this::minimizeCallback);

        if(config.monitorAspectRatio) {
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vidMode != null) {
                int targetWidth = vidMode.width();
                int targetHeight = vidMode.height();
                aspectRatio = (float) targetWidth / (float) targetHeight;
            }
        }
        else aspectRatio = config.targetAspectRatio;


        GL.createCapabilities();

        resizeCallback(glfwWindow,width(),height());

        // Make the window visible
        glfwShowWindow(glfwWindow);



        // IMGUI
        //imguiLayer = new ImGuiLayer(glfwWindow);
        //imguiLayer.initImGui();


        // Starting scene
        System.out.println("\tSuccess");




    }


    public double getTime() { return System.nanoTime() / 1000000000.0; }

    private static final int TARGET_UPS = 30;
    private static final int TARGET_FPS = 60;

    private boolean quit;
    private boolean vsync;
    private boolean sleepOnSync;

    private void mainLoop() {

        Window.changeScene(0);

        glEnable(GL_BLEND); // Enabling alfa-blend
        // What blending function to use (very typical)
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        float beginTime = (float) glfwGetTime();
        float endTime;
        dt = 0.0f;



        while (!glfwWindowShouldClose(glfwWindow)) {
            // Tmp
            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE))
                glfwSetWindowShouldClose(glfwWindow,true);
            if (KeyListener.isKeyPressed(GLFW_KEY_E))
                glfwSwapInterval(1);

            // Poll events
            glfwPollEvents();



            if (!minimized) {
                currentScene.update(dt);
            }


            glfwSwapBuffers(glfwWindow); // Swap the color buffers
            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            fps = 1/dt;
            beginTime = endTime;
        }
    }

    public void sync(int targetFPS) {

        double pfs = timer.prevFrameSeconds();
        double now = timer.timeSeconds();
        float targetTime = 1f / targetFPS;

        while (now - pfs < targetTime) {

            if (sleepOnSync) {
                Thread.yield();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    //Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            now = timer.timeSeconds();
        }
    }

    public void quit() { quit = true; }

    public static void changeScene(int sceneID) {

        assert window != null : " (Window) Window not initialized";
        if (window.currentSceneID == sceneID) {
            System.out.println("(Window) Cannot change scene to 'currentScene'");
            return;
        }
        Scene newScene = null;

        switch (sceneID) {
            case 0:
                newScene = new Editor();
                break;
            case 1:
                // newScene = new MainMenu(sceneID, "Menu");
                newScene = new TestScene("");
                break;
            case 3:
                // newScene = new Game(sceneID, "Game");
            default:
                assert false : " (Window) Unknown scene ID: '" + sceneID + "'";
        }
        if (window.currentSceneID != -1) {
            String sceneClass = window.currentScene.getClass().getName();
            System.out.println("ENDING CURRENT SCENE: " + sceneClass + " '" + window.currentScene.getTitle() + "'");
            window.currentScene.endScene();
            System.out.println("\tSuccess");
        }
        String sceneClass = newScene.getClass().getName();
        System.out.println("INITIALIZING NEW SCENE: " + sceneClass + " " + newScene.getTitle());
        window.currentScene = newScene;
        window.currentScene.initalize();
        window.currentSceneID = sceneID;
        System.out.println("\tSuccess");
    }

    private void terminate() {
        // Cleaning up current scene
        String sceneClass = currentScene.getClass().getName();
        System.out.println("ENDING CURRENT SCENE: " + sceneClass);
        currentScene.endScene();
        currentScene = null;
        System.out.println("\tSuccess");
        System.out.println("TERMINATING GLFW WINDOW");
        // Terminate imgui
        //imguiLayer.destroyImGui();
        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
        System.out.println("\tTerminated");
    }

    private void resizeCallback(long glfwWindow, int screenWidth, int screenHeight) {
        glfwSetWindowSize(glfwWindow, screenWidth, screenHeight);


        // do the following block in the init()
        // where screen W and H are window W and H

        // Figure out the largest area that fits this target aspect ratio
        int aspectWidth = screenWidth;
        int aspectHeight = (int)((float)aspectWidth / window.aspectRatio);

        if (aspectHeight > screenHeight) {
            // it doesn't fit so we mush change to pillarbox
            aspectHeight = screenHeight;
            aspectWidth = (int)((float)aspectHeight * window.aspectRatio);
        }


        // Center rectangle
        int viewPortX = (int) (((float)screenWidth / 2f) - ((float)aspectWidth / 2f));
        int viewPortY = (int) (((float)screenHeight / 2f) - ((float)aspectHeight / 2f));

        window.viewportWidth = aspectWidth;
        window.viewportHeight = aspectHeight;
        window.viewW_normalized = 1f / aspectWidth;
        window.viewH_normalized = 1f / aspectHeight;
        window.viewportX = viewPortX;
        window.viewportY = viewPortY;
        window.width = screenWidth;
        window.height = screenHeight;

        //currentScene.camera().adjustTest(aspectWidth,aspectHeight);

        //System.out.println(aspectWidth + " " + aspectHeight);
        glViewport(viewPortX,viewPortY,aspectWidth,aspectHeight);
    }

    private void minimizeCallback(long glfwWindow, boolean iconified) {
        System.out.println("iconified");
        this.minimized = iconified;
    }

    public Scene scene() {
        if (window != null) {
            Scene s = window.currentScene;
            if (s == null) {
                System.out.println("(Window) 'currentScene' = null");
            }
            return s;
        }
        System.out.println("(Window) 'window' = null");
        return null;
    }

    public static void setTitle(String newTitle) {
        if (window != null) {
            if (window.glfwWindow != NULL) {
                glfwSetWindowTitle(window.glfwWindow, newTitle);
            }
        }
    }

    public static long glfwWindow() { return window.glfwWindow; }

    public static void defaultViewport() {
        glViewport(viewportX(),viewportY(),viewportW(),viewportH());
    }

    public static void clearColor(Color color) {
        glClearColor(color.r(), color.g(), color.b(), color.a());
    }

    public static float dt() { return window.dt; }

    public static float fps() { return window.fps; }

    public static int width() {
        return window.width;
    }

    public static int height() {
        return window.height;
    }

    public static int viewportW() {
        return window.viewportWidth;
    }

    public static int viewportH() {
        return window.viewportHeight;
    }

    public static int viewportX() {
        return window.viewportX;
    }

    public static int viewportY() {
        return window.viewportY;
    }

    public static float aspectRatio() { return window.aspectRatio; }

    public static float viewW_normalized() { return window.viewW_normalized; }

    public static float viewH_normalized() { return window.viewH_normalized; }
}
