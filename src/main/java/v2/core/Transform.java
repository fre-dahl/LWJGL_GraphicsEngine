package v2.core;

import org.joml.Vector2f;
import v2.core.adt.TransformListener;

import java.util.ArrayList;
import java.util.List;

public class Transform {

    private final Vector2f position;
    private final Vector2f scale;
    private final Vector2f tmp;
    private float rotation;

    private List<TransformListener> listeners;

    public Transform() {
        this(new Vector2f());
    }

    public Transform(Vector2f position){
        this(position, new Vector2f(1,1),0);
    }

    public Transform(Vector2f position, Vector2f scale, float rotation) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        tmp = new Vector2f();
    }

    public void addListener(TransformListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        listeners.add(listener);
    }

    public void removeListener(TransformListener listener) {
        if (listeners != null)
            listeners.remove(listener);
    }

    public void setPosition(Vector2f position) {
        this.position.set(position);
        if (listeners != null) {
            for (TransformListener listener: listeners) {
                listener.onSetPosition(position);
            }
        }
    }

    public void setPosition(float x, float y) {
        this.position.set(x,y);
        if (listeners != null) {
            for (TransformListener listener: listeners) {
                listener.onSetPosition(position);
            }
        }
    }

    public void translate(Vector2f translation) {
        if (translation.x == 0 && translation.y == 0)
            return;
        this.position.add(translation);
        if (listeners != null) {
            for (TransformListener listener: listeners) {
                listener.onTransformTranslation(translation);
            }
        }
    }

    public void translate(float xAmount, float yAmount) {
        if (xAmount == 0 && yAmount == 0)
            return;
        this.position.add(xAmount, yAmount);
        if (listeners != null) {
            tmp.set(xAmount,yAmount);
            for (TransformListener listener: listeners) {
                listener.onTransformTranslation(tmp);
            }
        }
    }

    public void rotateDeg(float amount) {
        if (amount == 0) return;
        rotation += amount;
        while (rotation > 360) rotation -= 360;
        while (rotation < 0) rotation += 360;
        if (listeners != null) {
            for (TransformListener listener: listeners) {
                listener.onTransformRotation(rotation);
            }
        }
    }

    public void setRotation(float deg) {
        if (deg == rotation) return;
        rotation = deg;
        while (rotation > 360) rotation -= 360;
        while (rotation < 0) rotation += 360;
        if (listeners != null) {
            for (TransformListener listener: listeners) {
                listener.onTransformRotation(rotation);
            }
        }
    }

    public void setScale(float scaleX, float scaleY) {
        scale.set(scaleX,scaleY);
        if (listeners != null) {
            for (TransformListener listener: listeners) {
                listener.onTransformScaling(scale);
            }
        }
    }

    public void setScale(float scale) {
        this.scale.set(scale,scale);
        if (listeners != null) {
            for (TransformListener listener: listeners) {
                listener.onTransformScaling(this.scale);
            }
        }
    }

    public void scaleBy(float amount) {
        if (amount == 0) return;
        this.scale.add(amount,amount);
        if (listeners != null) {
            for (TransformListener listener: listeners) {
                listener.onTransformScaling(this.scale);
            }
        }
    }


    public Vector2f position() {
        return position;
    }

    public Vector2f scale() {
        return scale;
    }

    public float rotation() {
        return rotation;
    }

    public boolean hasListeners() {
        if (listeners == null)
            return false;
        return !listeners.isEmpty();
    }

}
