package illarion.download.gui;

import java.io.IOException;

/**
 * The story board allows control over the scenes that are shown to the user one by one.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Storyboard {
    /**
     * Check if there is a next scene to go to.
     *
     * @return {@code true} in case there is a next scene
     */
    boolean hasNextScene();

    /**
     * Go to the next scene.
     */
    void nextScene() throws IOException;

    /**
     * Show the options.
     */
    void showOptions();

    /**
     * Show the normal storyboard.
     */
    void showNormal() throws IOException;
}
