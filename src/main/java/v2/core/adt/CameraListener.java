package v2.core.adt;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

public interface CameraListener {

    void onCameraZoom(Rectanglef worldView, float zoom);

    void onCameraTranslation(Rectanglef worldView, Vector2f position);

}
