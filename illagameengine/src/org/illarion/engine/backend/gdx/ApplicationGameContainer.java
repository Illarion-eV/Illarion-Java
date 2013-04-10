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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.illarion.engine.DesktopGameContainer;
import org.illarion.engine.Engine;
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
    private Application gdxApplication;

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
     * Create a new desktop game that is drawn using libGDX.
     *
     * @param gameListener the game listener that receives the updates regarding the game
     * @param width        the width of the game container
     * @param height       the height of the game container
     * @param fullScreen   the full screen flag of the container
     * @throws GdxEngineException in case the initialization goes wrong
     */
    public ApplicationGameContainer(final GameListener gameListener, final int width, final int height,
                                    final boolean fullScreen) throws GdxEngineException {
        this.gameListener = gameListener;
        config = new LwjglApplicationConfiguration();
        config.forceExit = false;
        config.height = height;
        config.width = width;
        config.fullscreen = fullScreen;
        config.useGL20 = true;
        config.vSyncEnabled = false;
        config.useCPUSynch = false;

        windowHeight = height;
        windowWidth = width;
        fullScreenResolution = new GraphicResolution(width, height, -1, -1);
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
    public Engine getEngine() {
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
        gdxApplication = new LwjglApplication(new ListenerApplication(gameListener, this), config);
    }

    void createEngine() {
        assert gdxApplication != null;
        engine = new GdxEngine(gdxApplication, this);
    }

    @Override
    public void exitGame() {
        if (gdxApplication != null) {
            gdxApplication.exit();
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
    private GraphicResolution fullScreenResolution;

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
        if (gdxApplication == null) {
            return new GraphicResolution[0];
        }
        if (graphicResolutions == null) {
            final List<GraphicResolution> resultResolutions = new ArrayList<GraphicResolution>();
            final Graphics.DisplayMode[] displayModes = gdxApplication.getGraphics().getDisplayModes();
            for (@Nullable Graphics.DisplayMode mode : displayModes) {
                if (mode == null) {
                    continue;
                }
                if ((mode.width < 800) || (mode.height < 600) || (mode.bitsPerPixel < 24) || (mode.refreshRate < 50)) {
                    continue;
                }
                resultResolutions.add(new GraphicResolution(mode.width, mode.height, mode.bitsPerPixel,
                        mode.refreshRate));
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
