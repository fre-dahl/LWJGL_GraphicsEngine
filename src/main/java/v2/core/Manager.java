package v2.core;

import java.lang.reflect.ParameterizedType;

public abstract class Manager<T extends Component> implements Comparable<Manager<T>>{

    @SuppressWarnings("unchecked")

    final Class<T> componentClass = ((Class<T>) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0]);

    public final void add(GameObject o) {

        T component = o.getComponent(componentClass);

        if (component != null)

            onAddComponent(component);
    }

    public final void remove(GameObject o) {

        T component = o.getComponent(componentClass);

        if (component != null)

            onRemoveComponent(component);
    }

    public abstract void start();

    public abstract void update(float dt);

    public abstract void onAddComponent(T component);

    public abstract void onRemoveComponent(T component);

    public abstract void cleanUp();

}
