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

import org.illarion.engine.assets.Assets;
import org.illarion.engine.graphic.WorldMapDataProvider;
import org.newdawn.slick.GameContainer;

import javax.annotation.Nonnull;

/**
 * The asset provider for the Slick2D backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickAssets implements Assets {
    /**
     * The instance of the texture manager used by this backend.
     */
    @Nonnull
    private final SlickTextureManager textureManager;

    /**
     * The instance of the cursor manager used by this backend.
     */
    @Nonnull
    private final SlickCursorManager cursorManager;

    /**
     * The sprite factory of the Slick2D backend
     */
    @Nonnull
    private final SlickSpriteFactory spriteFactory;

    /**
     * The font manager of the Slick2D backend.
     */
    @Nonnull
    private final SlickFontManager fontManager;

    /**
     * The sounds manager of the Slick2D backend.
     */
    @Nonnull
    private final SlickSoundsManager soundsManager;

    /**
     * The effect manager of the Slick2D backend.
     */
    @Nonnull
    private final SlickEffectManager effectManager;

    /**
     * The Slick2D game container that displays the game.
     */
    @Nonnull
    private final GameContainer container;

    /**
     * Constructor of this assets handler.
     *
     * @param container the slick game container
     */
    SlickAssets(@Nonnull final GameContainer container) {
        textureManager = new SlickTextureManager();
        cursorManager = new SlickCursorManager();
        spriteFactory = new SlickSpriteFactory();
        fontManager = new SlickFontManager(textureManager);
        soundsManager = new SlickSoundsManager();
        effectManager = new SlickEffectManager();
        this.container = container;
    }

    @Nonnull
    @Override
    public SlickTextureManager getTextureManager() {
        return textureManager;
    }

    @Nonnull
    @Override
    public SlickFontManager getFontManager() {
        return fontManager;
    }

    @Nonnull
    @Override
    public SlickCursorManager getCursorManager() {
        return cursorManager;
    }

    @Nonnull
    @Override
    public SlickSoundsManager getSoundsManager() {
        return soundsManager;
    }

    @Nonnull
    @Override
    public SlickSpriteFactory getSpriteFactory() {
        return spriteFactory;
    }

    @Nonnull
    @Override
    public SlickScene createNewScene() {
        return new SlickScene(container);
    }

    @Nonnull
    @Override
    public SlickWorldMap createWorldMap(@Nonnull final WorldMapDataProvider provider) throws SlickEngineException {
        return new SlickWorldMap(provider);
    }

    @Nonnull
    @Override
    public SlickEffectManager getEffectManager() {
        return effectManager;
    }
}
