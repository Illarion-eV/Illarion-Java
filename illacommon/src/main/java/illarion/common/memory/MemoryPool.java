package illarion.common.memory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MemoryPool<T> {

    protected Queue<T> freeObjs = new ConcurrentLinkedQueue<>();

    protected MemoryPool() {
        //
    }

    public T get (Class<T> cls) {
        T obj = freeObjs.poll();

        if (obj == null) {
            //there is no free object in queue --> allocate a new one
            try {
                obj = cls.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MemoryAllocatorException(cls, e);
            }
        }

        return obj;
    }

    public void free (T obj) {
        freeObjs.add(obj);
    }

}
