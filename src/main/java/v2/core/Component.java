package v2.core;


import v2.core.adt.ManagerID;

public abstract class Component implements ManagerID {

    private GameObject gameObject;
    private int managerID = ManagerID.NONE;


    public GameObject gameObject() {
        return gameObject;
    }

    public void attach(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    @Override
    public int managerID() {
        return managerID;
    }

    @Override
    public void setManagerID(int id) {
        this.managerID = id;
    }
}
