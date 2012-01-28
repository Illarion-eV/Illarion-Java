package illarion.client.loading;

import illarion.client.resources.*;
import illarion.client.resources.loaders.*;
import javolution.context.ConcurrentContext;
import org.newdawn.slick.loading.DeferredResource;

import java.io.IOException;

/**
 * This class is used to allow the loading sequence of the client to load
 * the resource tables.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ResourceTableLoading implements DeferredResource {
    /**
     * This support class is used to perform the actual loading.
     * 
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class TableFactoryInitTask implements Runnable {
        /**
         * The resource loader that is triggered by this class.
         */
        private final ResourceLoader<?> loader;
    
        /**
         * Create a new instance of this task and set the loader that is
         * triggered once it has to run.
         * 
         * @param load the resource loader to run by this task
         */
        public TableFactoryInitTask(final ResourceLoader<?> load) {
            loader = load;
        }
    
        /**
         * Execute the task.
         */
        @Override
        public void run() {
            loader.load();
        }
    }
    
    /**
     * Perform the loading.
     */
    @Override
    public void load() throws IOException {
        ConcurrentContext.execute(
            new TableFactoryInitTask(new TileLoader().setTarget(TileFactory
                .getInstance())),
            new TableFactoryInitTask(new OverlayLoader()
                .setTarget(OverlayFactory.getInstance())),
            new TableFactoryInitTask(new ItemLoader().setTarget(ItemFactory
                .getInstance())),
            new TableFactoryInitTask(new CharacterLoader()
                .setTarget(CharacterFactory.getInstance())),
            new TableFactoryInitTask(new ClothLoader()
                .setTarget(new ClothFactoryRelay())),
            new TableFactoryInitTask(new EffectLoader()
                .setTarget(EffectFactory.getInstance())),
            new TableFactoryInitTask(new RuneLoader().setTarget(RuneFactory
                .getInstance())));
    }

    /**
     * Get a human readable description for this task.
     */
    @Override
    public String getDescription() {
        return null;
    }

}
