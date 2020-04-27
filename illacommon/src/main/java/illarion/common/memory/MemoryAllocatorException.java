package illarion.common.memory;

public class MemoryAllocatorException extends RuntimeException {

    public MemoryAllocatorException (Class<?> cls, Throwable e) {
        super("Couldn't create a new instance of class " + cls.getCanonicalName() + ", maybe constructor is not accessible (private / protected) or there isn't a default constructor?", e);
    }

}
