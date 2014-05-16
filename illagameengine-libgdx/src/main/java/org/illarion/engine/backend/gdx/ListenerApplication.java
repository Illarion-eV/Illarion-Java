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

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.illarion.engine.GameListener;

import javax.annotation.Nonnull;

/**
 * This is the listener application that forwards the application events of libGDX to the {@link GameListener} that
 * is defined by this game engine.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class ListenerApplication extends ApplicationAdapter {
    /**
     * This is the game listener of the engine that has to receive the information regarding the game.
     */
    @Nonnull
    private final GameListener listener;

    /**
     * The game container of the game engine.
     */
    @Nonnull
    private final ApplicationGameContainer container;

    /**
     * Create a new listener application that forwards the events of libGDX to the engine listener.
     *
     * @param listener the listener of the game engine
     * @param container the game container
     */
    ListenerApplication(@Nonnull GameListener listener, @Nonnull ApplicationGameContainer container) {
        this.listener = listener;
        this.container = container;
    }

    @Override
    public void create() {
        container.createEngine();
        listener.create(container);
    }

    @Override
    public void resize(int width, int height) {
        listener.resize(container, width, height);
    }

    @Override
    public void render() {
        listener.update(container, Math.round(Gdx.graphics.getDeltaTime() * 1000.f));

        GdxGraphics graphics = container.getEngine().getGraphics();
        graphics.beginFrame();
        container.getEngine().getAssets().getTextureManager().update();
        listener.render(container);
        graphics.endFrame();

        SpriteBatch batch = container.getEngine().getGraphics().getSpriteBatch();
        container.setLastFrameRenderCalls(batch.totalRenderCalls);
        batch.totalRenderCalls = 0;
    }

    /**
     * This function is called to check if closing the game directly is allowed at this point.
     *
     * @return {@code true} in case the game may be closed now
     */
    public boolean isExitAllowed() {
        return listener.isClosingGame();
    }

    @Override
    public void dispose() {
        listener.dispose();
    }
}
