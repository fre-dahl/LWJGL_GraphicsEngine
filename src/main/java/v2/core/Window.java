package v2.core;

import v2.editor.Editor;
import v2.editor.ImGuiLayer;
import v2.graphics.Color;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    // https://www.lwjgl.org/guide

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
    private ImGuiLayer imguiLayer;
    private int currentSceneID = -1;

    private Window() {}


    public static Window get() {

        if (Window.window == null) {
            System.out.println();
            System.out.println("LWJGL version:" + Version.getVersion());
            System.out.println("GLFW version:" + glfwGetVersionString());
            System.out.println();
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

        System.out.println("INITIALIZING GLFW WINDOW");
        // Set up an Error Callback
        GLFWErrorCallback.createPrint(System.err).set();
        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to Initialize GLFW.");
        }
        // Configure GLFW
        glfwDefaultWindowHints();
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
        glfwSetCursorPosCallback(glfwWindow, Mouse::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, Mouse::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, Mouse::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        if (config.enableVsync) { // Enable V-sync
            glfwSwapInterval(1); // Follows refresh rate of monitor
        }
        else glfwSwapInterval(0);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // Get/set proper window dimensions after config
        int[] width = new int[2];
        int[] height = new int[2];
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
        GL.createCapabilities();
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

        glEnable(GL_BLEND); // Enabling alfa-blend
        // What blending function to use (very typical)
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);


        // IMGUI
        //imguiLayer = new ImGuiLayer(glfwWindow);
        //imguiLayer.initImGui();


        // Starting scene
        System.out.println("\tSuccess");
        Window.changeScene(0);
    }

    private void mainLoop() {

        float beginTime = (float) glfwGetTime();
        float endTime;
        dt = -1.0f;

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Tmp
            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE))
                glfwSetWindowShouldClose(glfwWindow,true);

            // Poll events
            glfwPollEvents();

            if (!minimized) {
                currentScene.update(dt);
            }

            // IMGUI
            //imguiLayer.update(dt,currentScene);
            // Swap the color buffers
            glfwSwapBuffers(glfwWindow);
            // Time
            endTime = (float) glfwGetTime();
            dt = endTime - beginTime;
            fps = 1/dt;
            beginTime = endTime;
        }
    }

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
        glfwSetErrorCallback(null).free();
        System.out.println("\tSuccess");
    }

    private void resizeCallback(long glfwWindow, int screenWidth, int screenHeight) {
        glfwSetWindowSize(glfwWindow, screenWidth, screenHeight);

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
