package illarion.client.loading;

import illarion.client.resources.CharacterFactory;
import illarion.client.resources.ClothFactoryRelay;
import illarion.client.resources.EffectFactory;
import illarion.client.resources.ItemFactory;
import illarion.client.resources.OverlayFactory;
import illarion.client.resources.RuneFactory;
import illarion.client.resources.SongFactory;
import illarion.client.resources.SoundFactory;
import illarion.client.resources.TileFactory;
import illarion.client.resources.loaders.CharacterLoader;
import illarion.client.resources.loaders.ClothLoader;
import illarion.client.resources.loaders.EffectLoader;
import illarion.client.resources.loaders.ItemLoader;
import illarion.client.resources.loaders.OverlayLoader;
import illarion.client.resources.loaders.ResourceLoader;
import illarion.client.resources.loaders.RuneLoader;
import illarion.client.resources.loaders.SongLoader;
import illarion.client.resources.loaders.SoundLoader;
import illarion.client.resources.loaders.TileLoader;

import java.io.IOException;

import javolution.context.ConcurrentContext;

import org.newdawn.slick.loading.DeferredResource;

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
