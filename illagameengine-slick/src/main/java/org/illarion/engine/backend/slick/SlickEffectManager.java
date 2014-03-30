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

import org.illarion.engine.EngineException;
import org.illarion.engine.assets.EffectManager;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.effects.FogEffect;
import org.illarion.engine.graphic.effects.GrayScaleEffect;
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
    private SlickMiniMapEffect miniMapEffect;

    /**
     * The shared instance of the highlight effect.
     */
    @Nullable
    private SlickHighlightEffect highlightEffect;

    /**
     * The shared instance of the fog effect.
     */
    @Nullable
    private SlickFogEffect fogEffect;

    /**
     * The shared instance of the gray scale effect.
     */
    @Nullable
    private SlickGrayScaleEffect grayScaleEffect;

    @Nonnull
    @Override
    public MiniMapEffect getMiniMapEffect(
            @Nonnull final WorldMap worldMap, final boolean sharedInstance) throws SlickEngineException {
        if (sharedInstance) {
            if (miniMapEffect == null) {
                miniMapEffect = new SlickMiniMapEffect(worldMap);
            }
            return miniMapEffect;
        }
        return new SlickMiniMapEffect(worldMap);
    }

    @Nonnull
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

    @Nonnull
    @Override
    public FogEffect getFogEffect(final boolean sharedInstance) throws EngineException {
        if (sharedInstance) {
            if (fogEffect == null) {
                fogEffect = new SlickFogEffect();
            }
            return fogEffect;
        }
        return new SlickFogEffect();
    }

    @Nonnull
    @Override
    public GrayScaleEffect getGrayScaleEffect(final boolean sharedInstance) throws EngineException {
        if (sharedInstance) {
            if (grayScaleEffect == null) {
                grayScaleEffect = new SlickGrayScaleEffect();
            }
            return grayScaleEffect;
        }
        return new SlickGrayScaleEffect();
    }
}
