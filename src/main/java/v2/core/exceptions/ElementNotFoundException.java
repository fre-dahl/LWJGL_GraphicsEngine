package v2.core.exceptions;

public class ElementNotFoundException extends RuntimeException{

    public ElementNotFoundException(String collection) {
        super("Target element not found in this collection: " + collection);
    }
}
