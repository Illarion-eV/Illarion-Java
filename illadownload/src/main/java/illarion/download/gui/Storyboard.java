/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
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
    void showOptions() throws IOException;

    /**
     * Show the uninstall dialog.
     */
    void showUninstall() throws IOException;

    /**
     * Show the normal storyboard.
     */
    void showNormal() throws IOException;
}
