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
package org.illarion.engine.assets;

import org.illarion.engine.EngineException;
import org.illarion.engine.graphic.Scene;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.WorldMapDataProvider;

import javax.annotation.Nonnull;

/**
 * This interface defines how the assets that need to be managed by the game engine can be accessed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface Assets {
    /**
     * Get the manager for the texture assets.
     *
     * @return the texture asset manager
     */
    @Nonnull
    TextureManager getTextureManager();

    /**
     * Get the manager for the font assets.
     *
     * @return the font asset manager
     */
    @Nonnull
    FontManager getFontManager();

    /**
     * Get the manager for the mouse cursor assets.
     *
     * @return the mouse cursor asset manager
     */
    @Nonnull
    CursorManager getCursorManager();

    /**
     * Get the manager for the sound assets.
     *
     * @return the sound asset manager
     */
    @Nonnull
    SoundsManager getSoundsManager();

    /**
     * Get the factory that is used to create sprites.
     *
     * @return the sprite factory
     */
    @Nonnull
    SpriteFactory getSpriteFactory();

    /**
     * Create a new scene instance that should be used for rendering the game.
     *
     * @return the newly created scene
     */
    @Nonnull
    Scene createNewScene();

    /**
     * Create a new world map instance. This class is then used to create the world map texture that is displayed in
     * the game.
     *
     * @param provider the provider that will supply the world map with the required data
     * @return the newly created world map texture creator
     * @throws EngineException in case creating the world map fails for some reason
     */
    @Nonnull
    WorldMap createWorldMap(@Nonnull WorldMapDataProvider provider) throws EngineException;

    /**
     * Get the manager for the graphical effects.
     *
     * @return the graphic effect manager
     */
    @Nonnull
    EffectManager getEffectManager();
}
