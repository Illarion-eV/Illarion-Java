/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics.lwjgl;

import java.awt.Canvas;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import illarion.common.util.Rectangle;

import illarion.graphics.GraphicResolution;
import illarion.graphics.Graphics;
import illarion.graphics.RenderDisplay;
import illarion.graphics.generic.AbstractSprite;
import illarion.graphics.lwjgl.render.AbstractTextureRender;

/**
 * The LWJGL Render display implementation that allows accessing the LWJGL
 * render display and offers the possibility to toggle between full screen and
 * windowed mode.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class RenderDisplayLWJGL implements RenderDisplay { // NO_UCD
    /**
     * The logger instance that is used to log the error and debugging output.
     */
    private static final Logger LOGGER = Logger
        .getLogger(RenderDisplayLWJGL.class);

    /**
     * The maximal value for the synchronized frames per second.
     */
    private static final int MAX_FREQUENCY = 60;

    /**
     * The area limits that were set in place. They need to be stored in order
     * to roll them back one by one when resetting.
     */
    private final FastList<Rectangle> areaLimits = new FastList<Rectangle>();

    /**
     * This is the list of the raw area limits. This limits don't have the
     * intersection applied and are used for the proper calculation of the
     * offset.
     */
    private final FastList<Rectangle> areaLimitsRaw =
        new FastList<Rectangle>();

    /**
     * The list of found display types in the proper order.
     */
    private List<DisplayMode> displayModes;

    /**
     * selector if the full screen mode is possible or not.
     */
    private boolean fullscreenPossible = false;

    /**
     * The render area that is the render target.
     */
    private Canvas renderCanvas;

    /**
     * The height of the display that was selected.
     */
    private int resHeight = 0;

    /**
     * The width of the display that was selected.
     */
    private int resWidth = 0;

    @SuppressWarnings("nls")
    @Override
    public void addInputListener(final Object listener) {
        throw new GraphicsLWJGLException("Invalid input listener added");
    }

    /**
     * Apply a global offset to all following render actions.
     * 
     * @param offX the global x offset
     * @param offY the global y offset
     */
    @Override
    public void applyOffset(final float offX, final float offY) {
        GL11.glPushMatrix();
        GL11.glTranslatef(offX, offY, 0.f);
    }

    @Override
    public void applyScaling(final float scale) {
        GL11.glPushMatrix();
        if (!areaLimits.isEmpty()) {
            final Rectangle lastRect = areaLimits.getLast();
            if (scale > 1.f) {
                GL11.glTranslatef(-lastRect.getWidth() / scale,
                    -lastRect.getHeight() / scale, 0);
            } else {
                GL11.glTranslatef(lastRect.getWidth() * scale,
                    lastRect.getHeight() * scale, 0);
            }
        }
        GL11.glScalef(scale, scale, 1.f);
    }

    /**
     * Get the height of the render display.
     * 
     * @return the height of the render display
     */
    @Override
    public int getHeight() {
        return resHeight;
    }

    /**
     * Get the amount of objects drawn since the last call of that function.
     * 
     * @return the amount of objects drawn
     * @see illarion.graphics.RenderDisplay#getObjects()
     */
    @Override
    public int getObjects() {
        return AbstractSprite.getDrawnObjects();
    }

    /**
     * Get a list of possible resolutions the client is able to go along with.
     * 
     * @return a list of possible resolutions, the order is always the same at
     *         the same computer. The format is: width*height*bpp@freq
     */
    @Override
    public GraphicResolution[] getPossibleResolutions() {
        final List<DisplayMode> modesArray = getDisplayModes();

        final List<GraphicResolution> ret = new ArrayList<GraphicResolution>();
        for (final DisplayMode mode : modesArray) {
            if ((mode.getWidth() < 800) || (mode.getHeight() < 600)
                || (mode.getBitsPerPixel() <= 16)) {
                continue;
            }
            ret.add(new GraphicResolution(mode.getWidth(), mode.getHeight(),
                mode.getBitsPerPixel(), mode.getFrequency()));
        }
        return ret.toArray(new GraphicResolution[ret.size()]);
    }

    /**
     * Get the rendering area Canvas. It is create in case it was not before and
     * set to the size that was chosen for the display.
     * 
     * @return the display canvas that is the target of the rendering actions
     * @see illarion.graphics.RenderDisplay#getRenderArea()
     */
    @Override
    public Canvas getRenderArea() {
        if (renderCanvas == null) {
            renderCanvas = new Canvas() {
                private static final long serialVersionUID = 1L;
                private final RenderManagerLWJGL manager =
                    (RenderManagerLWJGL) Graphics.getInstance()
                        .getRenderManager();

                @Override
                public void paint(final java.awt.Graphics g) {
                    manager.paint();
                }

                @Override
                public void update(final java.awt.Graphics g) {
                    paint(g);
                }

                @Override
                public void validate() {
                    super.validate();
                    RenderDisplayLWJGL.this.setSize(getWidth(), getHeight());
                }
            };
            renderCanvas.setSize(resWidth, resHeight);
        }
        return renderCanvas;
    }

    /**
     * Get the width of the render display.
     * 
     * @return the width of the render display
     */
    @Override
    public int getWidth() {
        return resWidth;
    }

    @Override
    public void hideCursor() {
        // does nothing, cursor is always hidden
    }

    /**
     * Check if the display is currently working in full screen mode.
     * 
     * @return true in case the full screen mode is currently active
     * @see illarion.graphics.RenderDisplay#isFullscreen()
     */
    @Override
    public boolean isFullscreen() {
        return fullscreenPossible && Display.isFullscreen();
    }

    /**
     * LWJGL does not use listeners. So the check if the listener is supported
     * is done by checking the listener parameter against <code>null</code>.
     * 
     * @param listener the listener to test
     * @return <code>true</code> in case the listener parameter is
     *         <code>null</code>
     */
    @Override
    public boolean isInputListenerSupported(final Object listener) {
        return (listener == null);
    }

    @SuppressWarnings("nls")
    @Override
    public void removeInputListener(final Object listener) {
        throw new GraphicsLWJGLException("Invalid input listener removed");

    }

    /**
     * Reset the last applied global offset.
     */
    @Override
    public void resetOffset() {
        GL11.glPopMatrix();
    }

    @Override
    public void resetScaling() {
        GL11.glPopMatrix();
    }

    /**
     * Active the scissors mode to limit the render area.
     * 
     * @param x the x coordinate of the location of the render area
     * @param y the y coordinate of the location of the render area
     * @param width the width of the scissor area
     * @param height the height of the scissor area
     * @see illarion.graphics.RenderDisplay#setAreaLimit(int, int, int, int)
     */
    @Override
    public void setAreaLimit(final int x, final int y, final int width,
        final int height) {
        final Rectangle newRect = Rectangle.getInstance();
        final Rectangle newRectRaw = Rectangle.getInstance();

        newRect.set(x, y, width, height);
        newRectRaw.set(x, y, width, height);

        if (areaLimits.isEmpty()) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
        } else {
            newRect.intersect(areaLimits.getLast());
        }
        GL11.glPushMatrix();
        GL11.glScissor(newRect.getLeft(), newRect.getBottom(),
            newRect.getWidth(), newRect.getHeight());

        if (!areaLimits.isEmpty()) {
            final Rectangle lastRect = areaLimitsRaw.getLast();
            GL11.glTranslatef(newRectRaw.getX() - lastRect.getX(),
                newRectRaw.getY() - lastRect.getY(), 0);
        } else {
            GL11.glTranslatef(newRectRaw.getX(), newRectRaw.getY(), 0);
        }
        areaLimits.addLast(newRect);
        areaLimitsRaw.addLast(newRectRaw);
    }

    /**
     * Set the used screen resolution.
     * 
     * @param resolution the display settings to be used to render the screen.
     */
    @Override
    public void setDisplayMode(final GraphicResolution resolution) {
        resHeight = resolution.getHeight();
        resWidth = resolution.getWidth();
        ((RenderManagerLWJGL) Graphics.getInstance().getRenderManager())
            .setTargetFPS(resolution.getRefreshRate());
    }

    /**
     * Select a display mode by its index in the list of display modes. The list
     * can be fetched using {@link #getPossibleResolutions()}.
     * 
     * @param index the index of the display mode that shall be used in the list
     *            of existing display modes.
     */
    @Override
    public void setDisplayMode(final int index) {
        DisplayMode usedMode = null;
        try {
            usedMode = getDisplayModes().get(index);
        } catch (final Exception e1) {
            try {
                usedMode = getDisplayModes().get(0);
            } catch (final Exception e2) {
                fullscreenPossible = false;
                resWidth = 800;
                resHeight = 600;
                updateSyncFreq();
            }
        }

        if (usedMode != null) {
            resWidth = usedMode.getWidth();
            resHeight = usedMode.getHeight();
            updateSyncFreq();
            try {
                Display.setDisplayMode(usedMode);
                fullscreenPossible = true;
            } catch (final LWJGLException e) {
                fullscreenPossible = false;
            }
        }

        if (renderCanvas != null) {
            renderCanvas.setSize(resWidth, resHeight);
        }

        setupOpenGL();
    }

    /**
     * LWJGL does yet not include any special options, so this function does
     * nothing at all.
     */
    @Override
    public void setDisplayOptions(final String key, final String value) {
        // nothing to do
    }

    @Override
    public void showCursor() {
        // does nothing, cursor is always hidden
    }

    @Override
    public void shutdown() {
        if (renderCanvas != null) {
            renderCanvas.setVisible(false);
            if (Display.isCreated()) {
                Display.destroy();
            }
        }
    }

    /**
     * Start the rendering and and create the LWJGL display. This will try to
     * activate the display at all costs. In case its not possible to link with
     * the rendering canvas it will create a new window. Make sure the rendering
     * canvas returns true at its function
     * {@link java.awt.Canvas#isDisplayable()}.
     * 
     * @see illarion.graphics.RenderDisplay#startRendering()
     */
    @Override
    @SuppressWarnings("nls")
    public void startRendering() {
        if (Display.isCreated()) {
            return;
        }

        renderCanvas.setEnabled(true);
        renderCanvas.setVisible(true);
        try {
            Display.setParent(renderCanvas);
            Display.create();
            Display.makeCurrent();
        } catch (final LWJGLException e) {
            LOGGER.fatal("failed creating the display", e);
        }

        setupOpenGL();
        ((RenderManagerLWJGL) Graphics.getInstance().getRenderManager())
            .bind(this);
    }

    /**
     * Stop the rendering and destroy the LWJGL rendering window.
     * 
     * @see illarion.graphics.RenderDisplay#stopRendering()
     */
    @Override
    public void stopRendering() {
        // if (Display.isCreated()) {
        // Display.destroy();
        // }
        // renderCanvas.setEnabled(false);
    }

    /**
     * Check if the full screen mode is possible. That works only in case the
     * window mode that is set up is supported by the the graphic card.
     * 
     * @return true if the full screen mode is possible
     * @see illarion.graphics.RenderDisplay#supportsFullscreen()
     */
    @Override
    public boolean supportsFullscreen() {
        return fullscreenPossible;
    }

    /**
     * Switch between full screen mode and windowed mode in case its possible.
     * 
     * @see illarion.graphics.RenderDisplay#toogleFullscreen()
     */
    @Override
    @SuppressWarnings("nls")
    public void toogleFullscreen() {
        try {
            if (Display.isFullscreen()) {
                Display.setFullscreen(false);
                updateSyncFreq();
            } else if (fullscreenPossible) {
                // Display.setFullscreen(true);
                updateSyncFreq();
            }
        } catch (final LWJGLException e) {
            LOGGER.error("Failed switching to full screen.", e);
        }
    }

    /**
     * Disable the scissors mode that limits the render area.
     * 
     * @see illarion.graphics.RenderDisplay#unsetAreaLimit()
     */
    @Override
    public void unsetAreaLimit() {
        areaLimits.removeLast().recycle();
        areaLimitsRaw.removeLast().recycle();

        if (areaLimits.isEmpty()) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            final Rectangle lastRect = areaLimits.getLast();
            GL11.glScissor(lastRect.getLeft(), lastRect.getBottom(),
                lastRect.getWidth(), lastRect.getHeight());
        }
        GL11.glPopMatrix();
    }

    /**
     * Update the display so the last drawn picture gets visible.
     */
    public void update() {
        AbstractTextureRender.finish();

        Display.update();

        // clean display for next render loop.
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glLoadIdentity();
    }

    /**
     * Change the size of this display. The sole reason for this function is be
     * in existence is to avoid the creating of synthetic methods.
     * 
     * @param width the new width
     * @param height the new height
     */
    protected void setSize(final int width, final int height) {
        resWidth = width;
        resHeight = height;
    }

    /**
     * Setup the openGL render environment. Such as the view port the matrix for
     * the orthogonal view and so on.
     */
    protected void setupOpenGL() {
        if (!Display.isCreated()) {
            return;
        }

        // set the basic view port of the game. This should be the full size of
        // the client window
        GL11.glViewport(0, 0, resWidth, resHeight);

        // enable alpha blending based on the picture alpha channel
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // disable death test, we work in 2D anyway, there is no depth
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        // switch to projection matrix to set up the orthogonal view that we
        // need
        GL11.glMatrixMode(GL11.GL_PROJECTION);

        // load the identity matrix to we have a clean start
        GL11.glLoadIdentity();

        // setup the orthogonal view
        GLU.gluOrtho2D(0, resWidth, 0, resHeight);

        // set clear color to black
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // sync frame (only works on windows)
        if (Graphics.NO_SLOWDOWN) {
            Display.setVSyncEnabled(false);
        } else {
            Display.setVSyncEnabled(true);
        }

        // clean up the screen
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        // switch to model view matrix, so we can place the sprites correctly
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    /**
     * Get all available display modes ordered by size.
     * 
     * @return the list of display modes ordered by size
     */
    @SuppressWarnings("nls")
    private List<DisplayMode> getDisplayModes() {
        if (displayModes != null) {
            return displayModes;
        }
        try {
            final DisplayMode[] modes = Display.getAvailableDisplayModes();
            final List<DisplayMode> modesArray =
                new ArrayList<DisplayMode>(modes.length);
            for (final DisplayMode mode : modes) {
                modesArray.add(mode);
            }
            Collections.sort(modesArray, new Comparator<DisplayMode>() {
                @Override
                public int compare(final DisplayMode o1, final DisplayMode o2) {
                    if (o1.getBitsPerPixel() < o2.getBitsPerPixel()) {
                        return -1;
                    } else if (o1.getBitsPerPixel() > o2.getBitsPerPixel()) {
                        return 1;
                    }
                    final int o1Size = o1.getWidth() * o1.getHeight();
                    final int o2Size = o2.getWidth() * o2.getHeight();
                    if (o1Size < o2Size) {
                        return -1;
                    } else if (o1Size > o2Size) {
                        return 1;
                    }
                    return 0;
                }
            });
            displayModes = modesArray;
            return modesArray;
        } catch (final LWJGLException e) {
            LOGGER.error("Error fetching display modes", e);
            return new ArrayList<DisplayMode>();
        }
    }

    /**
     * After a update of the sync frequency, update it to a new proper value at
     * or below the {@link #MAX_FREQUENCY} value.
     */
    private void updateSyncFreq() {
        int syncFreq = Display.getDisplayMode().getFrequency();

        while (syncFreq > MAX_FREQUENCY) {
            syncFreq >>= 1;
        }

        ((RenderManagerLWJGL) Graphics.getInstance().getRenderManager())
            .setTargetFPS(syncFreq);
    }
}
