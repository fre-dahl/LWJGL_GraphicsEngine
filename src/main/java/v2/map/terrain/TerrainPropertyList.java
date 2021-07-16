package v2.map.terrain;


import v2.core.adt.UnorderedListADT;
import v2.utility.LinkedNode;
import v2.core.exceptions.ElementNotFoundException;
import v2.core.exceptions.EmptyCollectionException;
import v2.core.exceptions.TerrainPropertyWeightException;
import v2.graphics.Color;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class TerrainPropertyList extends TerrainProperty implements UnorderedListADT<TerrainProperty> {

    private int count;
    private int modCount;
    private int sumRelativeWeight;
    private float remainingSpace;
    private float colorInfluence;

    private LinkedNode<TerrainProperty> head, tail;

    public TerrainPropertyList(String descriptor, int relativeWeight, Color color) {

        super(descriptor, relativeWeight, color);
        sumRelativeWeight = 0;
        remainingSpace = 1f;
        head = tail = null;
        colorInfluence = 0f;
        modCount = 0;
        count = 0;
    }

    public TerrainPropertyList(String descriptor, float absoluteWeight, Color color) {

        super(descriptor, absoluteWeight, color);
        sumRelativeWeight = 0;
        remainingSpace = 1f;
        head = tail = null;
        colorInfluence = 0f;
        modCount = 0;
        count = 0;
    }

    // Also called from TerrainProperty.class
    protected void reBalanceWeights(int amount) {

        sumRelativeWeight += amount;
        LinkedNode<TerrainProperty> current = head;

        while (current != null) {

            TerrainProperty e = current.getElement();

            if (!e.hasFixedWeight()) {
                e.setAbsoluteWeight(
                        (float)e.relativeWeight() / sumRelativeWeight  * remainingSpace);
            }
            current = current.next();
        }
    }

    @Override
    public TerrainProperty removeFirst() throws EmptyCollectionException {

        if (isEmpty())
            throw new EmptyCollectionException("TerrainPropertyList");

        LinkedNode<TerrainProperty> resultNode = head;

        head = head.next();
        resultNode.setNext(null);

        if (head != null)
            head.setPrev(null);

        TerrainProperty result = resultNode.getElement();
        result.setParent(null);

        if (result.hasFixedWeight())
            remainingSpace += result.absoluteWeight();

        reBalanceWeights( - result.relativeWeight());

        modCount++;
        count--;

        return result;
    }

    @Override
    public TerrainProperty removeLast() throws EmptyCollectionException{

        if (isEmpty())
            throw new EmptyCollectionException("TerrainPropertyList");

        LinkedNode<TerrainProperty> resultNode = tail;

        tail = tail.prev();
        resultNode.setPrev(null);

        if (tail != null)
            tail.setNext(null);

        TerrainProperty result = resultNode.getElement();
        result.setParent(null);

        if (result.hasFixedWeight())
            remainingSpace += result.absoluteWeight();

        reBalanceWeights( - result.relativeWeight());

        modCount++;
        count--;

        return result;
    }

    @Override
    public TerrainProperty remove(TerrainProperty element) throws EmptyCollectionException, ElementNotFoundException {

        if (isEmpty())
            throw new EmptyCollectionException("TerrainPropertyList");

        boolean found = false;

        LinkedNode<TerrainProperty> current = head;

        while (current != null && !found) {

            if (element.equals(current.getElement())) {
                found = true;
            }
            else {
                current = current.next();
            }
        }
        if (!found)
            throw new ElementNotFoundException("TerrainPropertyList");

        if (size() == 1) {
            head = tail = null;
        }
        else if (current.equals(head)) {

            head = head.next();
            current.setNext(null);

            if (head != null)
                head.setPrev(null);
        }
        else if (current.equals(tail)) {

            tail = tail.prev();
            current.setPrev(null);

            if (tail != null)
                tail.setNext(null);
        }
        else {
            current.prev().setNext(current.next());
            current.next().setPrev(current.prev());
            current.setPrev(null);
            current.setNext(null);
        }

        TerrainProperty result = current.getElement();
        result.setParent(null);

        if (result.hasFixedWeight())
            remainingSpace += result.absoluteWeight();

        reBalanceWeights( - result.relativeWeight());

        modCount++;
        count--;

        return result;
    }

    @Override
    public void addToFront(TerrainProperty element) throws TerrainPropertyWeightException {

        LinkedNode<TerrainProperty> newNode = new LinkedNode<>(element);
        TerrainProperty e = newNode.getElement();

        if (isEmpty()) {
            if (e.hasFixedWeight()) {

                remainingSpace -= e.absoluteWeight();

                if (remainingSpace < 0)
                    throw new TerrainPropertyWeightException(
                            "On inserting fixed weight element. " +
                                    "Sum fixed-weight > 1.0f");
            }
            else {
                sumRelativeWeight = e.relativeWeight();
                e.setAbsoluteWeight(1.0f);
            }
            head = newNode;
            tail = newNode;
        }
        else {
            if (!hasRoom())
                throw new TerrainPropertyWeightException(
                        "On inserting element.Remaining space: 0.0f");

            if (e.hasFixedWeight()) {
                remainingSpace -= e.absoluteWeight();
                if (remainingSpace < 0)
                    throw new TerrainPropertyWeightException(
                            "On inserting fixed weight element. " +
                                    "Sum fixed-weight > 1.0f");

            }
            head.setPrev(newNode);
            newNode.setNext(head);
            head = newNode;

            reBalanceWeights(e.relativeWeight());
        }

        e.setParent(this);
        count++;
        modCount++;
    }

    @Override
    public void addToRear(TerrainProperty element) throws TerrainPropertyWeightException{

        LinkedNode<TerrainProperty> newNode = new LinkedNode<>(element);
        TerrainProperty e = newNode.getElement();

        if (isEmpty()) {

            if (e.hasFixedWeight()) {

                remainingSpace -= e.absoluteWeight();

                if (remainingSpace < 0)
                    throw new TerrainPropertyWeightException("" +
                            "On inserting fixed weight element. " +
                            "Sum fixed-weight > 1.0f");
            }
            else {
                sumRelativeWeight = e.relativeWeight();
                e.setAbsoluteWeight(1.0f);
            }

            head = newNode;
            tail = newNode;
        }
        else {
            if (!hasRoom())
                throw new TerrainPropertyWeightException(
                        "On inserting element. " +
                                "Remaining space: 0.0f");

            if (e.hasFixedWeight()) {
                remainingSpace -= e.absoluteWeight();
                if (remainingSpace < 0)
                    throw new TerrainPropertyWeightException(
                            "On inserting fixed weight element. " +
                                    "Sum fixed-weight > 1.0f");
            }
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;

            reBalanceWeights(e.relativeWeight());
        }

        e.setParent(this);
        count++;
        modCount++;
    }

    @Override
    public void addAfter(TerrainProperty element, TerrainProperty target) throws TerrainPropertyWeightException,
            EmptyCollectionException, ElementNotFoundException {

        if (isEmpty())
            throw new EmptyCollectionException("TerrainPropertyList");

        if (!hasRoom())
            throw new TerrainPropertyWeightException("On inserting element. Remaining space: 0.0f");

        LinkedNode<TerrainProperty> newNode = new LinkedNode<>(element);
        TerrainProperty e = newNode.getElement();

        LinkedNode<TerrainProperty> current = head;

        boolean found = false;

        while (current != null && !found) {

            if (target.equals(current.getElement())) {
                found = true;
            }
            else {
                current = current.next();
            }
        }
        if (!found)
            throw new ElementNotFoundException("TerrainPropertyList");

        if (e.hasFixedWeight()) {
            remainingSpace -= e.absoluteWeight();
            if (remainingSpace < 0)
                throw new TerrainPropertyWeightException("On inserting fixed weight element. Sum fixed-weight > 1.0f");
        }

        newNode.setNext(current.next());

        if (current.hasNext()) {
            current.next().setPrev(newNode);
        }
        current.setNext(newNode);
        newNode.setPrev(current);

        reBalanceWeights(e.relativeWeight());

        e.setParent(this);
        count++;
        modCount++;
    }

    @Override
    public TerrainProperty first() throws EmptyCollectionException {

        if (isEmpty())
            throw new EmptyCollectionException("TerrainPropertyList");

        return head.getElement();
    }

    @Override
    public TerrainProperty last() throws EmptyCollectionException {

        if (isEmpty())
            throw new EmptyCollectionException("TerrainPropertyList");

        return tail.getElement();
    }

    @Override
    public boolean contains(TerrainProperty element) throws EmptyCollectionException {

        if (isEmpty())
            throw new EmptyCollectionException("TerrainPropertyList");

        boolean found = false;
        LinkedNode<TerrainProperty> current = head;

        while (current != null && !found) {

            if (element.equals(current.getElement()))
                found = true;

            else
                current = current.next();
        }
        return found;
    }

    @Override
    public boolean isEmpty() {
        return (count == 0);
    }

    @Override
    public int size() {
        return count;
    }

    // call this after removal.
    protected boolean validate() {

        LinkedNode<TerrainProperty> currentNode = head;

        TerrainProperty current;

        while (currentNode != null) {

            current = currentNode.getElement();

            if (current instanceof TerrainPropertyList) {

                if (!((TerrainPropertyList) current).isFilled())
                    return false;

                ((TerrainPropertyList) current).validate();
            }
            currentNode = currentNode.next();
        }
        return true;
    }

    private boolean isFilled() {
        return (sumRelativeWeight > 0 || remainingSpace == 0);
    }

    private boolean hasRoom() {
        return (remainingSpace > 0);
    }

    // ---------------------------------------------------------------------
    // COLOR

    public float colorInfluence() {
        return colorInfluence;
    }

    public void setColorInfluence(float influence) {
        this.colorInfluence = influence;
        adjustColorChildren();
    }

    @Override
    public void adjustColor(Color color) {
        super.adjustColor(color);
        adjustColorChildren();
    }

    @Override
    public void resetToOriginalColor() {
        super.resetToOriginalColor();
        adjustColorChildren();
    }

    @Override
    public void newOriginalColor(Color color, boolean clearInputColor) {
        super.newOriginalColor(color, clearInputColor);
        adjustColorChildren();
    }

    protected void adjustColor() {
        super.adjustColor();
        adjustColorChildren();

    }

    private void adjustColorChildren() {

        if (isEmpty()) return;;

        LinkedNode<TerrainProperty> current = head;

        do {
            TerrainProperty e = current.getElement();

            e.adjustColor();

            current = current.next();
        }
        while (current != null);
    }

    // ----------------------------------------------------------------------

    @Override
    public Iterator<TerrainProperty> iterator() {
        return new TerrainPropertyListIterator();
    }

    private class TerrainPropertyListIterator implements Iterator<TerrainProperty>{

        private final int iteratorModCount;
        private LinkedNode<TerrainProperty> current;

        public TerrainPropertyListIterator() {
            current = head;
            iteratorModCount = modCount;
        }

        @Override
        public boolean hasNext() throws ConcurrentModificationException {
            if (iteratorModCount != modCount)
                throw new ConcurrentModificationException();
            return (current != null);
        }

        @Override
        public TerrainProperty next() throws ConcurrentModificationException {
            if (!hasNext())
                throw new NoSuchElementException();
            TerrainProperty result = current.getElement();
            current = current.next();
            return result;
        }

        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

    }

}
