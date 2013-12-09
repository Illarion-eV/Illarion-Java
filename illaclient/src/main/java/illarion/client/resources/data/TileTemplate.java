/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.resources.data;

import illarion.client.graphics.FrameAnimation;
import illarion.common.graphics.TileInfo;
import org.illarion.engine.graphic.Sprite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * This template contains the required data to display a tile on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
public class TileTemplate extends AbstractAnimatedEntityTemplate {
    /**
     * The general information about this tile.
     */
    @Nonnull
    private final TileInfo tileInfo;

    /**
     * All tiles of one type share a single animation.
     */
    @Nonnull
    private final FrameAnimation sharedAnimation;

    /**
     * The constructor of this class.
     *
     * @param id       the identification number of the entity
     * @param sprite   the sprite used to render the entity
     * @param frames   the total amount of frames
     * @param speed    the animation speed of the tile
     * @param tileInfo the general tile information on this tile
     */
    public TileTemplate(final int id, @Nonnull final Sprite sprite, final int frames, final int speed,
                        @Nonnull final TileInfo tileInfo) {
        super(id, sprite, frames, 0, speed, null, 0);
        this.tileInfo = tileInfo;
        sharedAnimation = new FrameAnimation(null);
        sharedAnimation.setup(frames, 0, speed, FrameAnimation.LOOPED);
    }

    @Nonnull
    public TileInfo getTileInfo() {
        return tileInfo;
    }

    @Nullable
    public FrameAnimation getSharedAnimation() {
        return sharedAnimation;
    }
}
