package v2.core.exceptions;

public class EmptyCollectionException extends RuntimeException {

    public EmptyCollectionException(String collection) {
        super ("The " + collection + " is empty.");
    }
}
