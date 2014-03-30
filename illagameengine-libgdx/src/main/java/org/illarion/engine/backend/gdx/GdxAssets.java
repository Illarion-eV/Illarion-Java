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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Application;
import org.illarion.engine.EngineException;
import org.illarion.engine.GameContainer;
import org.illarion.engine.assets.*;
import org.illarion.engine.graphic.Scene;
import org.illarion.engine.graphic.WorldMap;
import org.illarion.engine.graphic.WorldMapDataProvider;

import javax.annotation.Nonnull;

/**
 * This is the asset manager that fetches its data from libGDX.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxAssets implements Assets {
    /**
     * The texture manager that is used.
     */
    @Nonnull
    private final GdxTextureManager textureManager;

    /**
     * The font manager that is used.
     */
    @Nonnull
    private final GdxFontManager fontManager;

    /**
     * The cursor manager used to load the mouse cursors.
     */
    @Nonnull
    private final CursorManager cursorManager;

    /**
     * The sounds manager used to load and store the sound effects.
     */
    @Nonnull
    private final GdxSoundsManager soundsManager;

    /**
     * The sprite factory of the libGDX backend.
     */
    @Nonnull
    private final GdxSpriteFactory spriteFactory;

    /**
     * The game container that shows the game.
     */
    @Nonnull
    private final GameContainer container;

    /**
     * The effect manager that creates the graphic effects.
     */
    @Nonnull
    private final GdxEffectManager effectManager;

    /**
     * Create a new instance of the libGDX assets management.
     *
     * @param gdxApplication the libGDX application this asset manager is bound to
     * @param container the game container
     */
    GdxAssets(@Nonnull final Application gdxApplication, @Nonnull final GameContainer container) {
        this.container = container;
        textureManager = new GdxTextureManager();
        fontManager = new GdxFontManager(gdxApplication.getFiles(), textureManager);
        cursorManager = new GdxLwjglCursorManager(gdxApplication.getFiles());
        soundsManager = new GdxSoundsManager(gdxApplication.getFiles(), gdxApplication.getAudio());
        spriteFactory = new GdxSpriteFactory();
        effectManager = new GdxEffectManager(gdxApplication.getFiles());
    }

    @Nonnull
    @Override
    public GdxTextureManager getTextureManager() {
        return textureManager;
    }

    @Nonnull
    @Override
    public FontManager getFontManager() {
        return fontManager;
    }

    @Nonnull
    @Override
    public CursorManager getCursorManager() {
        return cursorManager;
    }

    @Nonnull
    @Override
    public SoundsManager getSoundsManager() {
        return soundsManager;
    }

    @Nonnull
    @Override
    public GdxSpriteFactory getSpriteFactory() {
        return spriteFactory;
    }

    @Nonnull
    @Override
    public Scene createNewScene() {
        return new GdxScene(container);
    }

    @Nonnull
    @Override
    public WorldMap createWorldMap(@Nonnull final WorldMapDataProvider provider) throws EngineException {
        return new GdxWorldMap(provider);
    }

    @Nonnull
    @Override
    public EffectManager getEffectManager() {
        return effectManager;
    }
}
