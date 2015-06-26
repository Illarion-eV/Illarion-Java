/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.download.gui.view;

import javafx.scene.Scene;

import javax.annotation.Nonnull;

/**
 * In case a view class implements this interface the main handler will give the class a chance to update the scene
 * itself.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@FunctionalInterface
public interface SceneUpdater {
    /**
     * This function is called to update the scene.
     *
     * @param scene the scene to be updated
     */
    void updateScene(@Nonnull Scene scene);
}
