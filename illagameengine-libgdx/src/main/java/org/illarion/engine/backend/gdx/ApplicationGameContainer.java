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

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.illarion.engine.DesktopGameContainer;
import org.illarion.engine.GameListener;
import org.illarion.engine.MouseCursor;
import org.illarion.engine.graphic.GraphicResolution;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * The game container that is using the libGDX backend to handle the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ApplicationGameContainer implements DesktopGameContainer {
    /**
     * The libGDX application that contains the game.
     */
    @Nullable
    private GdxLwjglApplication gdxApplication;

    /**
     * The configuration used to create the application.
     */
    @Nonnull
    private final LwjglApplicationConfiguration config;

    /**
     * The game listener that receives the updates regarding the game.
     */
    private final GameListener gameListener;

    /**
     * The graphic resolutions that can be applied to the game.
     */
    @Nullable
    private GraphicResolution[] graphicResolutions;

    /**
     * Get the engine that drives the game in this container.
     */
    @Nullable
    private GdxEngine engine;

    /**
     * The count of render calls that were performed during the rendering of the last frame.
     */
    private int lastFrameRenderCalls;

    /**
     * The width of the application in windowed mode.
     */
    private int windowWidth;

    /**
     * The height of the application in windowed mode.
     */
    private int windowHeight;

    /**
     * The graphic resolution that applied in full screen mode.
     */
    @Nullable
    private GraphicResolution fullScreenResolution;

    /**
     * Create a new desktop game that is drawn using libGDX.
     *
     * @param gameListener the game listener that receives the updates regarding the game
     * @param width the width of the game container
     * @param height the height of the game container
     * @param fullScreen the full screen flag of the container
     * @throws GdxEngineException in case the initialization goes wrong
     */
    public ApplicationGameContainer(
            final GameListener gameListener, final int width, final int height, final boolean fullScreen)
            throws GdxEngineException {
        this.gameListener = gameListener;
        config = new LwjglApplicationConfiguration();
        config.forceExit = false;
        config.useGL20 = true;
        config.vSyncEnabled = true;
        config.backgroundFPS = 10;
        config.foregroundFPS = 60;

        windowHeight = height;
        windowWidth = width;
        fullScreenResolution = getFittingFullScreenResolution(width, height);
        if (fullScreen) {
            config.height = fullScreenResolution.getHeight();
            config.width = fullScreenResolution.getWidth();
        } else {
            config.height = height;
            config.width = width;
        }
        config.fullscreen = fullScreen;
    }

    @Nullable
    private GraphicResolution getFittingFullScreenResolution(final int width, final int height) {
        final GraphicResolution[] resolutions = getFullScreenResolutions();
        int freq = 0;

        GraphicResolution targetDisplayMode = null;
        for (@Nonnull final GraphicResolution current : resolutions) {
            if ((current.getWidth() == width) && (current.getHeight() == height)) {
                if ((targetDisplayMode == null) ||
                        ((current.getRefreshRate() >= freq) && (current.getBPP() > targetDisplayMode.getBPP()))) {
                    targetDisplayMode = current;
                    freq = targetDisplayMode.getRefreshRate();
                }

                if ((current.getBPP() == LwjglApplicationConfiguration.getDesktopDisplayMode().bitsPerPixel) &&
                        (current.getRefreshRate() ==
                                LwjglApplicationConfiguration.getDesktopDisplayMode().refreshRate)) {
                    targetDisplayMode = current;
                    break;
                }
            }
        }

        if (targetDisplayMode == null) {
            final Graphics.DisplayMode mode = LwjglApplicationConfiguration.getDesktopDisplayMode();
            return new GraphicResolution(mode.width, mode.height, mode.bitsPerPixel, mode.refreshRate);
        }
        return targetDisplayMode;
    }

    @Override
    public int getHeight() {
        if (gdxApplication == null) {
            return config.height;
        }
        return gdxApplication.getGraphics().getHeight();
    }

    @Override
    public int getWidth() {
        if (gdxApplication == null) {
            return config.width;
        }
        return gdxApplication.getGraphics().getWidth();
    }

    @Nonnull
    @Override
    public GdxEngine getEngine() {
        if (engine == null) {
            throw new IllegalStateException("Game is not launched yet.");
        }
        return engine;
    }

    @Override
    public void setMouseCursor(@Nullable final MouseCursor cursor) {
        if (!Display.isCreated()) {
            throw new IllegalStateException("The game display was not yet created.");
        }
        try {
            if (cursor == null) {
                Mouse.setNativeCursor(null);
            } else if (cursor instanceof GdxLwjglCursor) {
                Mouse.setNativeCursor(((GdxLwjglCursor) cursor).getLwjglCursor());
            }
        } catch (@Nonnull final LWJGLException ignored) {
            // nothing to do
        }
    }

    @Override
    public void startGame() throws GdxEngineException {
        gdxApplication = new GdxLwjglApplication(new ListenerApplication(gameListener, this), config);
    }

    void createEngine() {
        assert gdxApplication != null;
        engine = new GdxEngine(gdxApplication, this);
    }

    @Override
    public void exitGame() {
        if (gdxApplication != null) {
            gdxApplication.shutdownGame();
        }
    }

    @Nonnull
    @Override
    public String getTitle() {
        return config.title;
    }

    @Override
    public int getFPS() {
        if (gdxApplication == null) {
            return 0;
        }
        return gdxApplication.getGraphics().getFramesPerSecond();
    }

    void setLastFrameRenderCalls(final int calls) {
        lastFrameRenderCalls = calls;
    }

    @Nonnull
    @Override
    public CharSequence[] getDiagnosticLines() {
        return new CharSequence[]{"Render calls: " + lastFrameRenderCalls};
    }

    @Override
    public void setIcons(@Nonnull final String[] icons) {
        for (@Nullable final String icon : icons) {
            config.addIcon(icon, Files.FileType.Internal);
        }
    }

    @Override
    public void setTitle(@Nonnull final String title) {
        config.title = title;
        if (gdxApplication != null) {
            gdxApplication.getGraphics().setTitle(title);
        }
    }

    @Override
    public void setWindowSize(final int width, final int height) throws GdxEngineException {
        windowWidth = width;
        windowHeight = height;
        if (!isFullScreen() && (gdxApplication != null)) {
            gdxApplication.getGraphics().setDisplayMode(width, height, false);
        }
    }

    @Override
    public void setFullScreenResolution(@Nonnull final GraphicResolution resolution) throws GdxEngineException {
        fullScreenResolution = resolution;
        if (isFullScreen() && (gdxApplication != null)) {
            gdxApplication.getGraphics().setDisplayMode(resolution.getWidth(), resolution.getHeight(), true);
        }
    }

    @Nonnull
    @Override
    public GraphicResolution[] getFullScreenResolutions() {
        if (graphicResolutions == null) {
            final List<GraphicResolution> resultResolutions = new ArrayList<>();
            final Graphics.DisplayMode[] displayModes;
            final boolean ignoreRefreshRate;
            if (gdxApplication == null) {
                displayModes = LwjglApplicationConfiguration.getDisplayModes();
                ignoreRefreshRate = true;
            } else {
                displayModes = gdxApplication.getGraphics().getDisplayModes();
                ignoreRefreshRate = false;
            }
            for (@Nullable final Graphics.DisplayMode mode : displayModes) {
                if (mode == null) {
                    continue;
                }
                if ((mode.width < 800) || (mode.height < 600) || (mode.bitsPerPixel < 24)) {
                    continue;
                }
                if (ignoreRefreshRate) {
                    resultResolutions.add(new GraphicResolution(mode.width, mode.height, mode.bitsPerPixel, -1));
                } else {
                    if (mode.refreshRate >= 50) {
                        resultResolutions.add(new GraphicResolution(mode.width, mode.height, mode.bitsPerPixel,
                                                                    mode.refreshRate));
                    }
                }
            }
            graphicResolutions = resultResolutions.toArray(new GraphicResolution[resultResolutions.size()]);
        }
        return graphicResolutions;
    }

    @Override
    public boolean isResizeable() {
        return config.resizable;
    }

    @Override
    public void setResizeable(final boolean resizeable) throws GdxEngineException {
        if (gdxApplication == null) {
            config.resizable = resizeable;
        }
    }

    @Override
    public boolean isFullScreen() {
        return config.fullscreen;
    }

    @Override
    public void setFullScreen(final boolean fullScreen) throws GdxEngineException {
        config.fullscreen = fullScreen;
        if (gdxApplication != null) {
            if (fullScreen) {
                setFullScreenResolution(fullScreenResolution);
            } else {
                setWindowSize(windowWidth, windowHeight);
            }
        }
    }
}
