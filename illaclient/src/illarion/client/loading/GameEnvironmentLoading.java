package illarion.client.loading;

import illarion.client.world.GameFactory;
import illarion.client.world.World;

import java.io.IOException;

import org.newdawn.slick.loading.DeferredResource;

/**
 * This loading task takes care for loading the components of the game
 * environment that still need to be loaded.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GameEnvironmentLoading implements DeferredResource {
    /**
     * Load the game environment.
     */
    @Override
    public void load() throws IOException {
        GameFactory.getInstance().init();
        World.initMissing();
    }

    /**
     * The human readable description of this loading task.
     */
    @Override
    public String getDescription() {
        return null;
    }
}
