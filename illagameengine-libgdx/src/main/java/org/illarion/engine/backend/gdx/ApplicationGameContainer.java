/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import org.illarion.engine.DesktopGameContainer;
import org.illarion.engine.GameListener;
import org.illarion.engine.MouseCursor;
import org.illarion.engine.graphic.GraphicResolution;
import org.lwjgl.system.Configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The game container that is using the libGDX backend to handle the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class ApplicationGameContainer implements DesktopGameContainer {
    /**
     * The configuration used to create the application.
     */
    @Nonnull
    private final Lwjgl3ApplicationConfiguration config;
    /**
     * The game listener that receives the updates regarding the game.
     */
    @Nonnull
    private final GameListener gameListener;
    /**
     * The listener application that contains the game listener.
     */
    @Nullable
    private ListenerApplication applicationListener;
    /**
     * The libGDX application that contains the game.
     */
    @Nullable
    private Application gdxApplication;
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
     * Shows if the application is running in fullscreen mode.
     */
    private boolean isFullscreen;

    /**
     * The graphic resolution that applied in full screen mode.
     */
    @Nonnull
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
            @Nonnull GameListener gameListener, int width, int height, boolean fullScreen, boolean background) throws GdxEngineException {
        this.gameListener = gameListener;
        config = new Lwjgl3ApplicationConfiguration();
        config.useVsync(isVsync);
        if (background){
            config.setIdleFPS(10);
        }else{
            config.setIdleFPS(60);
        }
        config.setForegroundFPS(60);
        config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL20, 3, 2);

        isFullscreen = fullScreen;
        config.setResizable(!fullScreen);

        windowHeight = height;
        windowWidth = width;
        fullScreenResolution = getFittingFullScreenResolution(width, height);
        if (fullScreen) {
            DisplayMode mode = getMatchingDisplayMode(fullScreenResolution, Lwjgl3ApplicationConfiguration.getDisplayModes());
            if (mode != null) {
                config.setFullscreenMode(mode);
            }
        } else {
            config.setWindowedMode(width, height);
        }

        config.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public boolean closeRequested() {
                if (gdxApplication != null && (applicationListener == null || applicationListener.isExitAllowed())) {
                    gdxApplication.exit();
                    return true;
                }
                return false;
            }
        });

    }

    @Nonnull
    private GraphicResolution getFittingFullScreenResolution(int width, int height) {
        GraphicResolution[] resolutions = getFullScreenResolutions();
        int freq = 0;

        @Nullable GraphicResolution targetDisplayMode = null;
        for (@Nonnull GraphicResolution current : resolutions) {
            if ((current.getWidth() == width) && (current.getHeight() == height)) {
                if ((targetDisplayMode == null) ||
                        ((current.getRefreshRate() >= freq) && (current.getBPP() > targetDisplayMode.getBPP()))) {
                    targetDisplayMode = current;
                    freq = targetDisplayMode.getRefreshRate();
                }

                if ((current.getBPP() == Lwjgl3ApplicationConfiguration.getDisplayMode().bitsPerPixel) &&
                        (current.getRefreshRate() ==
                                Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate)) {
                    targetDisplayMode = current;
                    break;
                }
            }
        }

        if (targetDisplayMode == null) {
            DisplayMode mode = Lwjgl3ApplicationConfiguration.getDisplayMode();
            return new GraphicResolution(mode.width, mode.height, mode.bitsPerPixel, mode.refreshRate);
        }
        return targetDisplayMode;
    }

    @Override
    public int getHeight() {
        if (gdxApplication == null) {
            return isFullScreen() ? fullScreenResolution.getHeight() : windowHeight;
        }
        return gdxApplication.getGraphics().getHeight();
    }

    @Override
    public int getWidth() {
        if (gdxApplication == null) {
            return isFullScreen() ? fullScreenResolution.getWidth() : windowWidth;
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
    public void setMouseCursor(@Nullable MouseCursor cursor) {
        if (engine == null) {
            return;
        }
        if (cursor instanceof GdxCursor) {
            engine.getGraphics().setCursor((GdxCursor) cursor);
        } else {
            engine.getGraphics().setCursor(null);
        }
    }

    /**
     * Starts the game.
     * Call setGdxApplication beforehand.
     */
    @Override
    public void startGame() throws GdxEngineException {
        applicationListener = new ListenerApplication(gameListener, this);
        new Lwjgl3Application(applicationListener, config);
    }

    /**
     * In Lwjgl3 new GdxLwjglApplication will no return until the main game loop is stopped.
     * Therefore, it is needed to set the GdxApplication manually as the return value of
     * the instance creation in startGame cannot be used.
     */
    public void setGdxApplication(@Nonnull Application application) {
        gdxApplication = application;
    }

    @Override
    public void exitGame() {
        if (gdxApplication != null) {
            gdxApplication.exit();
        }
    }

    @Override
    public int getFPS() {
        if (gdxApplication == null) {
            return 0;
        }
        return gdxApplication.getGraphics().getFramesPerSecond();
    }

    @Nonnull
    @Override
    public CharSequence[] getDiagnosticLines() {
        return new CharSequence[]{"Render calls: " + lastFrameRenderCalls};
    }

    @Override
    public void setTitle(@Nonnull String title) {
        config.setTitle(title);
        if (gdxApplication != null) {
            gdxApplication.getGraphics().setTitle(title);
        }
    }

    @Override
    public void setWindowSize(int width, int height) throws GdxEngineException {
        windowWidth = width;
        windowHeight = height;
        if (!isFullScreen() && (gdxApplication != null)) {
            gdxApplication.getGraphics().setWindowedMode(width, height);
        }
    }

    @Override
    public void setFullScreenResolution(@Nonnull GraphicResolution resolution) throws GdxEngineException {
        fullScreenResolution = resolution;
        if (isFullScreen() && (gdxApplication != null)) {
            DisplayMode[] modes = gdxApplication.getGraphics().getDisplayModes();
            DisplayMode mode = getMatchingDisplayMode(fullScreenResolution, modes);
            if (mode != null) {
                gdxApplication.getGraphics().setFullscreenMode(mode);
            }
        }
    }

    private DisplayMode getMatchingDisplayMode(@Nonnull GraphicResolution resolution, DisplayMode[] modes) {
        for (@Nullable DisplayMode mode : modes) {
            if (mode == null) {
                continue;
            }
            if ((mode.width != resolution.getWidth()) ||
                    (mode.height != resolution.getHeight()) ||
                    (mode.bitsPerPixel != resolution.getBPP())) {
                continue;
            }

            if ((resolution.getRefreshRate() == -1) || (resolution.getRefreshRate() == mode.refreshRate)) {
                return mode;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public GraphicResolution[] getFullScreenResolutions() {
        if (graphicResolutions == null) {
            List<GraphicResolution> resultResolutions = new ArrayList<>();
            DisplayMode[] displayModes;
            boolean ignoreRefreshRate;
            if (gdxApplication == null) {
                displayModes = Lwjgl3ApplicationConfiguration.getDisplayModes();
                ignoreRefreshRate = true;
            } else {
                displayModes = gdxApplication.getGraphics().getDisplayModes();
                ignoreRefreshRate = false;
            }
            graphicResolutions = Arrays.stream(displayModes).filter(it ->
                    it != null &&
                            it.width >= 800 &&
                            it.height >= 600 &&
                    it.bitsPerPixel >= 24 && (ignoreRefreshRate || it.refreshRate >= 50))
                    .map(it -> new GraphicResolution(it.width, it.height, it.bitsPerPixel, it.refreshRate))
                    .toArray(GraphicResolution[]::new);
        }
        return graphicResolutions;
    }

    @Override
    public boolean isResizeable() {
        return !isFullScreen();
    }

    @Override
    public void setResizeable(boolean resizeable) throws GdxEngineException {
        if (gdxApplication == null) {
            config.setResizable(resizeable);
        }
    }

    @Override
    public boolean isFullScreen() {
        return isFullscreen;
    }

    @Override
    public void setFullScreen(boolean fullScreen) throws GdxEngineException {
        isFullscreen = fullScreen;
        config.setResizable(!fullScreen);
        if (gdxApplication != null) {
            if (fullScreen) {
                setFullScreenResolution(fullScreenResolution);
            } else {
                setWindowSize(windowWidth, windowHeight);
            }
        }
    }

    void createEngine() {
        assert gdxApplication != null;
        engine = new GdxEngine(gdxApplication, this);
    }

    void setLastFrameRenderCalls(int calls) {
        lastFrameRenderCalls = calls;
    }

    @Override
    public void setIcons(@Nonnull String... icons) {
        config.setWindowIcon(FileType.Internal, icons);
    }
}
