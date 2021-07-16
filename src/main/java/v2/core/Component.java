package v2.core;


import v2.utility.adt.ArrayID;

public abstract class Component implements ArrayID {

    private GameObject gameObject;
    private int arrayID = ArrayID.NONE;


    public GameObject gameObject() {
        return gameObject;
    }

    public void attach(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    @Override
    public int arrayID() {
        return arrayID;
    }

    @Override
    public void setArrayID(int id) {
        arrayID = id;
    }
}
