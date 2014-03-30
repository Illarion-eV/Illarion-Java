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

import org.illarion.engine.GameListener;
import org.newdawn.slick.Game;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

import javax.annotation.Nonnull;

/**
 * This is the game implementation for Slick2D that is reporting to a default game listener.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class ListenerGame implements Game {
    /**
     * The listener that this game implementation reports to.
     */
    @Nonnull
    private final GameListener listener;

    @Nonnull
    private final ApplicationGameContainer engineContainer;

    /**
     * The last recorded height value of the game container.
     */
    private int lastHeight;

    /**
     * The last recorded width value of the game container.
     */
    private int lastWidth;

    /**
     * Create a new implementation of this game and set the listener that receives the game lifecycle.
     *
     * @param gameListener the game listener that receives the updates
     */
    ListenerGame(
            @Nonnull final GameListener gameListener, @Nonnull final ApplicationGameContainer engineContainer) {
        listener = gameListener;
        this.engineContainer = engineContainer;
    }

    @Override
    public void init(@Nonnull final GameContainer gameContainer) throws SlickException {
        if (!ShaderProgram.isSupported()) {
            throw new SlickException("Shader not supported.");
        }
        listener.create(engineContainer);
        engineContainer.getEngine().getInput().setInput(gameContainer.getInput());
        lastHeight = gameContainer.getHeight();
        lastWidth = gameContainer.getWidth();
    }

    @Override
    public void update(@Nonnull final GameContainer gameContainer, final int delta) throws SlickException {
        final int currentHeight = gameContainer.getHeight();
        final int currentWidth = gameContainer.getWidth();
        if ((lastHeight != currentHeight) || (lastWidth != currentWidth)) {
            listener.resize(engineContainer, currentWidth, currentHeight);
            lastHeight = currentHeight;
            lastWidth = currentWidth;
        }
        engineContainer.getEngine().getAssets().getTextureManager().update();
        listener.update(engineContainer, delta);
    }

    @Override
    public void render(final GameContainer gameContainer, @Nonnull final Graphics graphics) throws SlickException {
        final SlickGraphics slickGraphics = engineContainer.getEngine().getGraphics();
        slickGraphics.setSlickGraphicsImpl(graphics);
        listener.render(engineContainer);
        slickGraphics.clearSlickGraphicsImpl();
    }

    @Override
    public boolean closeRequested() {
        return listener.isClosingGame();
    }

    @Nonnull
    @Override
    public String getTitle() {
        return engineContainer.getTitle();
    }
}
