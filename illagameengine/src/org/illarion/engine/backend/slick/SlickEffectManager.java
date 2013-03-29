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

import org.illarion.engine.EngineException;
import org.illarion.engine.assets.EffectManager;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.effects.HighlightEffect;
import org.illarion.engine.graphic.effects.MiniMapEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The Slick implementation of the effect manager.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickEffectManager implements EffectManager {
    /**
     * The shared instance of the mini map effect.
     */
    @Nullable
    private MiniMapEffect miniMapEffect;

    /**
     * The shared instance of the highlight effect.
     */
    @Nullable
    private HighlightEffect highlightEffect;

    @Nonnull
    @Override
    public MiniMapEffect getMiniMapEffect(@Nonnull final WorldMap worldMap,
                                          final boolean sharedInstance) throws SlickEngineException {
        if (sharedInstance) {
            if (miniMapEffect == null) {
                miniMapEffect = new SlickMiniMapEffect(worldMap);
            }
            return miniMapEffect;
        }
        return new SlickMiniMapEffect(worldMap);
    }

    @Override
    public HighlightEffect getHighlightEffect(final boolean sharedInstance) throws EngineException {
        if (sharedInstance) {
            if (highlightEffect == null) {
                highlightEffect = new SlickHighlightEffect();
            }
            return highlightEffect;
        }
        return new SlickHighlightEffect();
    }
}
