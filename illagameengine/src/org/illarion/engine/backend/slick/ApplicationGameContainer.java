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

import org.apache.log4j.Logger;
import org.illarion.engine.Engine;
import org.illarion.engine.GameContainer;
import org.illarion.engine.GameListener;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import javax.annotation.Nonnull;

/**
 * This is the application game container that is using the Slick2D backend to render the graphics.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ApplicationGameContainer implements GameContainer {
    /**
     * The logger instance for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ApplicationGameContainer.class);

    /**
     * The Slick2D implementation of the application container.
     */
    private final AppGameContainer slickContainer;

    /**
     * The engine instance for the Slick2D backend.
     */
    private final SlickEngine engine;

    /**
     * Create a new instance of this container.
     *
     * @param gameListener the listener
     * @throws SlickEngineException This exception is thrown in case creating the container failed badly
     */
    public ApplicationGameContainer(final GameListener gameListener) throws SlickEngineException {
        try {
            slickContainer = new AppGameContainer(new ListenerGame(gameListener));
            engine = new SlickEngine();
        } catch (@Nonnull final SlickException e) {
            throw new SlickEngineException("Failed to create the application container.", e);
        }
    }

    @Override
    public int getHeight() {
        return slickContainer.getHeight();
    }

    @Override
    public int getWidth() {
        return slickContainer.getWidth();
    }

    @Nonnull
    @Override
    public Engine getEngine() {
        return engine;
    }

    @Override
    public void setIcons(@Nonnull String[] icons) {
        try {
            slickContainer.setIcons(icons);
        } catch (@Nonnull final SlickException e) {
            LOGGER.error("Failed to set the application icons.", e);
        }
    }

    @Override
    public void setTitle(@Nonnull final String title) {
        slickContainer.setTitle(title);
    }
}
