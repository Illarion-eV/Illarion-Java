package illarion.common.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
* A pool of objects that can be reused to avoid allocation
*/
public class MemoryPool<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryPool.class);

    protected Queue<T> freeObjs = new ConcurrentLinkedQueue<>();
    protected final int MAX_OBJECTS = 20;

    protected MemoryPool() {
        //
    }

    public T get (Class<T> cls) {
        T obj = freeObjs.poll();

        if (obj == null) {
            // There is no free object in queue --> allocate a new one
            try {
                obj = cls.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MemoryAllocatorException(cls, e);
            }
        }

        return obj;
    }

    public void free (T obj) {
        // Reset object, if possible
        if (obj instanceof Poolable) {
            ((Poolable) obj).reset();
        }

        if (freeObjs.size() >= MAX_OBJECTS) {
            // Queue is full, don't add another object, else memory usage will increase drastically
            LOGGER.warn("memory pool of type '" + obj.getClass().getSimpleName() + "' is full, cannot recycle object.");
            return;
        }

        freeObjs.add(obj);
    }

}
