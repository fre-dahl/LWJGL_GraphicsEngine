package v2.map;


import v2.utility.U;
import v2.core.Camera;
import v2.core.Window;

public class ZoomLog2 {

    private final float offset;
    private final ProceduralMap map;
    private Node current;


    public ZoomLog2(ProceduralMap map, int bottomLevelZoom, int differance, float offset) {

        this.map = map;
        this.offset = offset;

        Camera camera = Window.get().scene().camera();

        assert (Math.pow(2,bottomLevelZoom) >= camera.zoomFloor() - offset) :
                "ERROR (MapZoomLog2) constructor : bottomLevelZoom < scene camera zoomFloor";

        assert (Math.pow(2,(bottomLevelZoom + (map.numLevels() * differance))) < camera.zoomCeil() - offset) :
                "ERROR (MapZoomLog2) constructor : scene camera zoomCeil cannot contain zoom-space";

        current = new Node(bottomLevelZoom, map.bottomLevel());
        Node thisNode = current;

        int i = 1;

        while (i < map.numLevels()) {
            Node nextNode = new Node(thisNode.value + differance, thisNode.level + 1);
            thisNode.next = nextNode;
            thisNode = nextNode;
            i++;
        }

    }

    public void onCameraZoom(float zoom) {

        int log2Zoom = U.log2(zoom + offset);

        if (log2Zoom != current.value) {

            Node node = current;

            if (log2Zoom > node.value && node.hasNext()) {
                do {
                    if (node.next.value == log2Zoom) {
                        node = node.next;
                        break;
                    }
                    node = node.next;
                }
                while (node.hasNext());

                current = node;

                map.zoomTransition(current.level);
            }
            else if (log2Zoom < node.value && node.hasPrev()) {
                do {
                    if (node.prev.value == log2Zoom) {
                        node = node.prev;
                        break;
                    }
                    node = node.prev;
                }
                while (node.hasPrev());

                current = node;

                map.zoomTransition(current.level);
            }
        }
    }

    private class Node {

        int value;
        int level;

        Node next;
        Node prev;

        public Node(int value, int level) {
            this.value = value;
            this.level = level;
        }

        boolean hasNext() { return next != null; }

        boolean hasPrev() { return prev != null; }

    }
}
