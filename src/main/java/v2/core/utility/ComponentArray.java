package v2.core.utility;

import v2.core.adt.ComponentIterator;
import v2.core.adt.ManagerID;
import v2.core.exceptions.ElementNotFoundException;
import v2.core.exceptions.EmptyCollectionException;


@SuppressWarnings("unchecked")

// auto resizable V2 array for O(1) removal
// requires item class to implement ManagerID

public class ComponentArray<T extends ManagerID> {

    T[] items;
    int count = 0;
    int targetCapacity;


    public ComponentArray(int targetCapacity) {
        this.targetCapacity = Math.max(10,targetCapacity);
        items = (T[]) new ManagerID[this.targetCapacity];
    }


    public void iterate(final ComponentIterator<T> iterator) {

        for (int i = 0; i < count; i++)

            iterator.next(items[i]);
    }


    public void add(T item) {

        if (count == items.length)

            resize(Math.max(10,(int)(count * 1.75f)));

        item.setManagerID(count);

        items[count++] = item;
    }

    public void remove(T item) throws ElementNotFoundException, EmptyCollectionException {

        if (count == 0)

            throw new EmptyCollectionException("Empty array");

        int key = item.managerID();

        if (key < 0 || key >= count) {

            if (key == ManagerID.NONE)

                throw new ElementNotFoundException("ID = NONE");

            else if (key < ManagerID.NONE)

                throw new ElementNotFoundException("Invalid ID");

            else throw new ElementNotFoundException("ID out of bounds");
        }

        T requestedItem = items[key];

        if (!item.equals(requestedItem))

            throw new ElementNotFoundException("Items does not match");

        requestedItem.setManagerID(ManagerID.NONE);

        int last = count - 1;

        if (key == last) {

            items[last] = null;
        }

        else {

            T lastItem = items[last];

            items[last] = null;

            lastItem.setManagerID(key);

            items[key] = lastItem;
        }

        count--;

        if (isEmpty()) {

            if (targetCapacity < capacity()) {

                items = (T[]) new ManagerID[targetCapacity];

            }
        }
    }


    public void clear () {

        if (isEmpty()) return;

        for (int i = 0; i < count; i++) {
            items[i].setManagerID(ManagerID.NONE);
            items[i] = null;
        }

        if (targetCapacity < capacity()) {

            items = (T[]) new ManagerID[targetCapacity];

        }

        count = 0;
    }

    private void resize(int size) {

        T[] items = this.items;

        T[] newItems = (T[])new ManagerID[size];

        System.arraycopy(
                items,
                0,
                newItems,
                0,
                Math.min(count, newItems.length));

        this.items = newItems;
    }

    public int size() {

        return count;
    }

    public int capacity() {

        return items.length;
    }

    public float loadFactor() {

        return ((float)count / capacity());
    }

    public boolean isEmpty() {

        return count == 0;
    }

    @Override
    public String toString() {
        return "Size: " + count + " | Capacity: " + capacity() + " | LoadFactor: " + loadFactor();
    }
}
