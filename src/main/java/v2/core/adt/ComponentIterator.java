package v2.core.adt;

@FunctionalInterface
public interface ComponentIterator<T extends ManagerID> {
    void next(T item);
}
