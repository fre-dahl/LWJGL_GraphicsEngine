package v2.core.adt;

import org.joml.Vector2f;

public interface TransformListener {

    void onTransformScaling(Vector2f scale);

    void onTransformTranslation(Vector2f translation);

    void onSetPosition(Vector2f position);

    void onTransformRotation(float rotation);

}
