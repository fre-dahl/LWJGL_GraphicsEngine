package v2.core;


import java.util.ArrayList;
import java.util.List;

// Todo: Implement pools

public class GameObject {

    private Transform transform;
    private List<Component> components;

    public GameObject() {
        this(new Transform());
    }

    public GameObject(Transform transform) {
        this.components = new ArrayList<>();
        this.transform = transform;
    }

    public void start() {

    }

    public void remove() {
        transform = null;
        components = null;
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass) {

        for(Component c : components) {

            if (componentClass.isAssignableFrom(c.getClass()))

                return true;
        }
        return false;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {

        for (Component c : components) {

            if (componentClass.isAssignableFrom(c.getClass())) {

                try {
                    return componentClass.cast(c);

                } catch (ClassCastException e) {

                    e.printStackTrace();

                    assert false : "Error: (GameObject) Casting component.";
                }
            }
        }
        return null;
    }

    public void addComponent(Component component) {

        components.add(component);

        component.attach(this);
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {

        for (int i = 0; i < components.size(); i++) {

            Component component = components.get(i);

            if (componentClass.isAssignableFrom(component.getClass())) {

                components.remove(i);

                return;
            }
        }
    }

    public Transform transform() {
        return transform;
    }

    // tmp imgui
    public void imgui() {

    }



}
