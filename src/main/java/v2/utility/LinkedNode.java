package v2.utility;

public class LinkedNode<E> {

    private E element;

    private LinkedNode<E> next;
    private LinkedNode<E> prev;

    public LinkedNode(E element) {
        this.element = element;
    }

    public boolean hasNext() { return next != null; }
    public boolean hasPrev() { return prev != null; }

    public LinkedNode<E> next() {return next;}
    public LinkedNode<E> prev() {return prev;}

    public E getElement() { return element; }

    public void setElement(E element) { this.element = element; }

    public void setNext(LinkedNode<E> next) {
        this.next = next;
    }

    public void setPrev(LinkedNode<E> prev) {
        this.prev = prev;
    }
}
