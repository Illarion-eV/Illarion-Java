/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.backend.slick;

import org.illarion.engine.GameContainer;
import org.illarion.engine.backend.shared.AbstractScene;
import org.illarion.engine.graphic.Graphics;

import javax.annotation.Nonnull;

/**
 * This is the Slick2D implementation of the game scene.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickScene extends AbstractScene {
    @Override
    public void update(@Nonnull final GameContainer container, final int delta) {
        updateScene(container, delta);
    }

    @Override
    public void render(@Nonnull final Graphics graphics, final int offsetX, final int offsetY) {
        if (graphics instanceof SlickGraphics) {
            final SlickGraphics slickGraphics = (SlickGraphics) graphics;
            final org.newdawn.slick.Graphics slickGraphicsImpl = slickGraphics.getSlickGraphicsImpl();
            if (slickGraphicsImpl == null) {
                throw new IllegalStateException("Rendering outside the render loop is not allowed.");
            }
            slickGraphicsImpl.pushTransform();
            slickGraphicsImpl.translate(offsetX, offsetY);
            renderScene(graphics);
            slickGraphicsImpl.popTransform();
        }
    }
}
