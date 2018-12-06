package illarion.common.memory;

import java.util.HashMap;
import java.util.Map;

public class MemoryPools {

    protected static Map<Class<?>,MemoryPool<?>> pools = new HashMap<>();

    protected MemoryPools() {
        //
    }

    /**
    * get reused object from memory pool
     *
     * @param cls object type (class)
     *
     * @return instance of class from memory pool
    */
    public static <T extends Object> T get(Class<T> cls) {
        MemoryPool<T> pool = getPool(cls);
        return pool.get(cls);
    }

    /**
    * add object to memory pool again, so it can reused
     *
     * @param obj object
    */
    public static <T extends Object> void free(T obj) {
        MemoryPool<T> pool = (MemoryPool<T>) getPool(obj.getClass());
        pool.free(obj);
    }

    protected static <T extends Object> MemoryPool<T> getPool (Class<T> cls) {
        MemoryPool<T> pool = (MemoryPool<T>) pools.get(cls);

        if (pool == null) {
            // Pool doesn't exists, because it wasn't used before, so create a new one
            pool = new MemoryPool<>();
            pools.put(cls, pool);
        }

        return pool;
    }

}
