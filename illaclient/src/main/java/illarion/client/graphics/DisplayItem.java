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
package illarion.client.graphics;

import org.illarion.engine.graphic.SceneElement;

/**
 * Interface for a object that can be rendered on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public interface DisplayItem extends SceneElement {
    /**
     * Remove object from display list.
     */
    void hide();

    /**
     * Show object by adding it to the display list.
     */
    void show();
}
