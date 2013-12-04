package illarion.download.gui.view;

import javafx.scene.Scene;

import javax.annotation.Nonnull;

/**
 * In case a view class implements this interface the main handler will give the class a chance to update the scene
 * itself.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface SceneUpdater {
    /**
     * This function is called to update the scene.
     *
     * @param scene the scene to be updated
     */
    void updateScene(@Nonnull Scene scene);
}
