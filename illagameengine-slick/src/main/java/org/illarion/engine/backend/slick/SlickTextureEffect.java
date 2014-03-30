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
package org.illarion.engine.backend.slick;

import org.newdawn.slick.Graphics;

import javax.annotation.Nonnull;

/**
 * This is the interface implemented by all texture effects implemented by the Slick2D render backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
interface SlickTextureEffect {
    /**
     * Activate this effect.
     */
    void activateEffect(@Nonnull Graphics graphics);

    /**
     * Disable the effect.
     */
    void disableEffect(@Nonnull Graphics graphics);
}
