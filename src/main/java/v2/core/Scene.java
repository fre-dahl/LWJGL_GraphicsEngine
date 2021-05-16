package v2.core;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    private String title;
    private List<Manager<Component>> managers = new ArrayList<>();
    private List<GameObject> objects = new ArrayList<>();
    private final Camera camera;

    private boolean initialized;
    private boolean running;


    public Scene(String title, Camera camera) {
        this.title = title;
        this.camera = camera;
        Window.setTitle(title);
    }

    public final void initalize() {

        if (!initialized) {

            load();

            create();

            start();

            initialized = true;
        }
    }

    private void start() {

        for (GameObject obj : objects)

            obj.start();

        for (Manager<Component> manager : managers) {

            manager.start();

            for (GameObject obj : objects)

                manager.add(obj);
        }
        running = true;
    }

    public void update(float dt) {

        handleInput(dt);

        for (Manager<Component> manager : managers)

            manager.update(dt);

    }

    public abstract void save();

    public abstract void create();

    public abstract void load();

    public abstract void handleInput(float dt);

    public abstract void disposeResources();

    @SuppressWarnings({"rawtypes", "unchecked"})

    public final void addSystem(Manager cs) throws IllegalStateException{

        if (running) throw new IllegalStateException("Adding system at runtime not allowed");

        managers.add(cs);

        // add sort
    }

    public final void addObject(GameObject gameObject) {

        objects.add(gameObject);

        if (running) {

            gameObject.start();

            for (Manager<Component> cs : managers)

                cs.add(gameObject);
        }
    }

    // tmp imgui

    public void sceneImgui() {
        /*
        if (activeGameObject != null) {
            ImGui.begin("inspector");
            activeGameObject.imgui();
            ImGui.end();
        }
        imgui();

         */
    }

    public void imgui() {

    }

    public final void endScene() {

        initialized = false;

        running = false;

        save();

        for (Manager<Component> system : managers)

            system.cleanUp();

        managers = null;

        for (GameObject o : objects)

            o.remove();

        objects = null;

        disposeResources();
    }


    public List<GameObject> objects() {
        return objects;
    }

    public Camera camera() {
        return camera;
    }

    public final int numGameObjects() {
        return objects.size();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        Window.setTitle(title);
    }

}
