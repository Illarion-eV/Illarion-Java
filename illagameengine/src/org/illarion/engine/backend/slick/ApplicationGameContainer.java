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
import org.illarion.engine.*;
import org.illarion.engine.graphic.GraphicResolution;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.renderer.Renderer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * This is the application game container that is using the Slick2D backend to render the graphics.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ApplicationGameContainer implements DesktopGameContainer {
    /**
     * The logger instance for this class.
     */
    @Nonnull
    private static final Logger LOGGER = Logger.getLogger(ApplicationGameContainer.class);

    /**
     * The engine instance for the Slick2D backend.
     */
    @Nonnull
    private final SlickEngine engine;

    /**
     * The Slick2D implementation of the application container.
     */
    @Nonnull
    private final AppGameContainer slickContainer;

    /**
     * The last applied title of the game.
     */
    @Nonnull
    private String title;

    /**
     * The height of the window (while its in the windowed mode.
     */
    private int windowHeight;

    /**
     * The width of the window.
     */
    private int windowWidth;

    /**
     * The full screen graphics resolution.
     */
    @Nonnull
    private GraphicResolution fullScreenResolution;


    /**
     * Create a new instance of this container.
     *
     * @param gameListener the listener
     * @throws SlickEngineException This exception is thrown in case creating the container failed badly
     */
    public ApplicationGameContainer(final GameListener gameListener) throws SlickEngineException {
        this(gameListener, 800, 600, false);
    }

    /**
     * Create a new instance of this container.
     *
     * @param gameListener the listener
     * @param width        the width of the window
     * @param height       the height of the window
     * @param fullScreen   {@code true} in case this application is supposed to show up as full screen application
     * @throws SlickEngineException This exception is thrown in case creating the container failed badly
     */
    public ApplicationGameContainer(final GameListener gameListener, final int width,
                                    final int height, final boolean fullScreen) throws SlickEngineException {
        try {
            slickContainer = new AppGameContainer(new ListenerGame(gameListener, this), width, height, fullScreen);
            slickContainer.setForceExit(false);
            slickContainer.setAlwaysRender(true);
            slickContainer.setUpdateOnlyWhenVisible(false);
            slickContainer.setShowFPS(false);
            Renderer.setRenderer(Renderer.VERTEX_ARRAY_RENDERER);
            Renderer.setLineStripRenderer(Renderer.QUAD_BASED_LINE_STRIP_RENDERER);
            engine = new SlickEngine(slickContainer);
            windowHeight = slickContainer.getHeight();
            windowWidth = slickContainer.getWidth();
            fullScreenResolution = new GraphicResolution(windowWidth, windowHeight, -1, -1);
        } catch (@Nonnull final SlickException e) {
            throw new SlickEngineException("Failed to create the application container.", e);
        }
    }


    @Override
    public void exitGame() {
        slickContainer.exit();
    }

    @Nonnull
    @Override
    public Engine getEngine() {
        return engine;
    }

    @Override
    public int getHeight() {
        return slickContainer.getHeight();
    }

    @Nonnull
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(@Nonnull final String title) {
        this.title = title;
        slickContainer.setTitle(title);
    }

    @Override
    public int getWidth() {
        return slickContainer.getWidth();
    }

    @Override
    public boolean isFullScreen() {
        return slickContainer.isFullscreen();
    }

    @Override
    public void setFullScreen(final boolean fullScreen) throws EngineException {
        try {
            if (fullScreen) {
                slickContainer.setDisplayMode(fullScreenResolution.getWidth(), fullScreenResolution.getHeight(), true);
                slickContainer.setTargetFrameRate(fullScreenResolution.getRefreshRate());
            } else {
                slickContainer.setDisplayMode(windowWidth, windowHeight, false);
            }
        } catch (@Nonnull final SlickException e) {
            throw new SlickEngineException(e);
        }
    }

    @Override
    public boolean isResizeable() {
        return slickContainer.isResizable();
    }

    @Override
    public void setResizeable(final boolean resizeable) throws EngineException {
        slickContainer.setResizable(resizeable);
    }

    @Override
    public void setIcons(@Nonnull final String[] icons) {
        try {
            slickContainer.setIcons(icons);
        } catch (@Nonnull final SlickException e) {
            LOGGER.error("Failed to set the application icons.", e);
        }
    }

    @Override
    public void setMouseCursor(@Nullable final MouseCursor cursor) {
        try {
            if (cursor instanceof SlickMouseCursor) {
                final SlickMouseCursor slickCursor = (SlickMouseCursor) cursor;
                slickContainer.setMouseCursor(slickCursor.getCursor(), slickCursor.getHotspotX(),
                        slickCursor.getHotspotY());
            } else {
                slickContainer.setMouseCursor((Cursor) null, 0, 0);
            }
        } catch (@Nonnull final SlickException ignored) {
            // ignore
        }
    }

    @Override
    public void setWindowSize(final int width, final int height) throws EngineException {
        try {
            if (!slickContainer.isFullscreen()) {
                slickContainer.setDisplayMode(width, height, false);
            }
            windowHeight = height;
            windowWidth = width;
        } catch (@Nonnull final SlickException e) {
            throw new SlickEngineException(e);
        }
    }

    @Override
    public void setFullScreenResolution(@Nonnull final GraphicResolution resolution) throws EngineException {
        try {
            if (slickContainer.isFullscreen()) {
                slickContainer.setDisplayMode(resolution.getWidth(), resolution.getHeight(), true);
                slickContainer.setTargetFrameRate(resolution.getRefreshRate());
            }
            fullScreenResolution = resolution;
        } catch (@Nonnull final SlickException e) {
            throw new SlickEngineException(e);
        }
    }

    @Nonnull
    @Override
    public GraphicResolution[] getFullScreenResolutions() {
        DisplayMode[] displayModes;
        try {
            displayModes = Display.getAvailableDisplayModes(800, 600, slickContainer.getScreenWidth(),
                    slickContainer.getScreenHeight(), 24, 32, 40, 120);
        } catch (@Nonnull final LWJGLException exc) {
            displayModes = new DisplayMode[1];
            displayModes[0] = new DisplayMode(800, 600);
        }

        Arrays.sort(displayModes, new DisplayModeSorter());

        final GraphicResolution[] result = new GraphicResolution[displayModes.length];

        for (int i = 0, displayModesLength = displayModes.length; i < displayModesLength; i++) {
            final DisplayMode mode = displayModes[i];
            result[i] = new GraphicResolution(mode.getWidth(), mode.getHeight(), mode.getBitsPerPixel(),
                    mode.getFrequency());
        }

        return result;
    }

    @Override
    public void startGame() throws SlickEngineException {
        try {
            slickContainer.start();
        } catch (@Nonnull final SlickException e) {
            throw new SlickEngineException(e);
        }
    }
}
