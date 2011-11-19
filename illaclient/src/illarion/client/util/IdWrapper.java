package illarion.client.util;

/**
 * This utility class is used to wrap a object together with a ID.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class IdWrapper<T> {
    /**
     * The object that is stored in this instance.
     */
    private final T object;
    
    /**
     * The ID that is connected with the object.
     */
    private final int id;
    
    /**
     * Constructor that allows to set the ID and the object that are supposed to
     * be linked together.
     * 
     * @param id the ID
     * @param object the object
     */
    public IdWrapper(final int id, final T object) {
        this.id = id;
        this.object = object;
    }
    
    /**
     * Get the ID of this object.
     * 
     * @return the Id
     */
    public int getId() {
        return id;
    }
    
    /**
     * Get the wrapped object.
     * 
     * @return the object
     */
    public T getObject() {
        return object;
    }
}
