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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Files;
import org.illarion.engine.EngineException;
import org.illarion.engine.assets.EffectManager;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.effects.FogEffect;
import org.illarion.engine.graphic.effects.HighlightEffect;
import org.illarion.engine.graphic.effects.MiniMapEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The effect manager of the libGDX backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxEffectManager implements EffectManager {
    /**
     * The file system handler used to load the effect data.
     */
    @Nonnull
    private final Files files;

    /**
     * The shared instance of the fog effect.
     */
    @Nullable
    private GdxFogEffect sharedFogEffect;

    /**
     * The shared instance of the highlight effect.
     */
    @Nullable
    private GdxHighlightEffect sharedHighlightEffect;

    /**
     * The shared instance of the mini map effect.
     */
    @Nullable
    private GdxMiniMapEffect sharedMiniMapEffect;

    /**
     * Create a new effect manager.
     *
     * @param files the file system handler that should be used to load the data
     */
    GdxEffectManager(@Nonnull final Files files) {
        this.files = files;
    }

    @Nonnull
    @Override
    public MiniMapEffect getMiniMapEffect(@Nonnull final WorldMap worldMap, final boolean sharedInstance) throws EngineException {
        if (sharedInstance) {
            if (sharedMiniMapEffect == null) {
                sharedMiniMapEffect = new GdxMiniMapEffect(files, worldMap);
            }
            return sharedMiniMapEffect;
        }
        return new GdxMiniMapEffect(files, worldMap);
    }

    @Nonnull
    @Override
    public HighlightEffect getHighlightEffect(final boolean sharedInstance) throws EngineException {
        if (sharedInstance) {
            if (sharedHighlightEffect == null) {
                sharedHighlightEffect = new GdxHighlightEffect(files);
            }
            return sharedHighlightEffect;
        }
        return new GdxHighlightEffect(files);
    }

    @Nonnull
    @Override
    public FogEffect getFogEffect(final boolean sharedInstance) throws EngineException {
        if (sharedInstance) {
            if (sharedFogEffect == null) {
                sharedFogEffect = new GdxFogEffect(files);
            }
            return sharedFogEffect;
        }
        return new GdxFogEffect(files);
    }
}
