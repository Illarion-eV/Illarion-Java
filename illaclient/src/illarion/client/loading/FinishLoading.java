package illarion.client.loading;

import illarion.graphics.common.SpriteBuffer;

import java.io.IOException;

import org.newdawn.slick.loading.DeferredResource;

/**
 * The finishing task for the loading sequence. This one should be called as
 * the last one during the loading sequence.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class FinishLoading implements DeferredResource {
    /**
     * Perform the finishing tasks of the texture loading.
     */
    @Override
    public void load() throws IOException {
        SpriteBuffer.getInstance().cleanup();
        System.gc();
    }

    /**
     * The human readable description of this task.
     */
    @Override
    public String getDescription() {
        return null;
    }

}
