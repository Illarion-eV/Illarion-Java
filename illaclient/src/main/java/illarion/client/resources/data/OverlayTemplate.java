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
package illarion.client.resources.data;

import org.illarion.engine.graphic.Sprite;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This template contains the required data to display a tile overlay on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
public class OverlayTemplate extends AbstractAnimatedEntityTemplate {
    /**
     * The constructor of this class.
     *
     * @param id the identification number of the entity
     * @param sprite the sprite used to render the entity
     */
    public OverlayTemplate(final int id, @Nonnull final Sprite sprite) {
        super(id, sprite, 1, 0, 0, null, 0);
    }
}
