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
package illarion.graphics.jogl;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;

import javolution.lang.Reflection;
import javolution.lang.Reflection.Constructor;
import javolution.util.FastList;

import org.apache.log4j.Logger;

import illarion.common.util.Rectangle;

import illarion.graphics.GraphicResolution;
import illarion.graphics.RenderDisplay;
import illarion.graphics.generic.AbstractSprite;

/**
 * This class defines a render display using the JOGL tool set to display
 * everything.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class RenderDisplayJOGL implements RenderDisplay {
    /**
     * This helper class is used as helper class to sort the graphic resolutions
     * properly.
     * 
     * @author Martin Karing
     * @since 2.00
     * @version 2.00
     */
    private static final class ComparatorGraphicResolutions implements
        Comparator<GraphicResolution> {
        /**
         * Public constructor to allow this class to be created.
         */
        public ComparatorGraphicResolutions() {
            // nothing to do
        }

        @Override
        public int compare(final GraphicResolution o1,
            final GraphicResolution o2) {
            if (o1.getBPP() < o2.getBPP()) {
                return -1;
            } else if (o1.getBPP() > o2.getBPP()) {
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
    }

    /**
     * The context that is used to render all graphics currently.
     */
    private static GLAutoDrawable activeContext;

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(RenderDisplayJOGL.class);

    /**
     * This is the key applied to the options to effect the use of the NEWT
     * canvas.
     */
    @SuppressWarnings("nls")
    private static final String OPTIONKEY_NEWT = "jogl.newt";

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
     * The canvas used to display the graphics.
     */
    private Component canvas;

    /**
     * The height canvas is supposed to have.
     */
    private int displayHeight = 768;

    /**
     * The width the canvas is supposed to have.
     */
    private int displayWidth = 1024;

    /**
     * The list of possible graphic resolutions to be used.
     */
    private GraphicResolution[] possibleResolutions;

    /**
     * The render area that is used to draw the graphics.
     */
    private Display renderArea;

    /**
     * The frames per second that are the target value.
     */
    private int targetFPS = 60;

    /**
     * This flag is turned true in case the display is expected to create a NEWT
     * canvas instead of a AWT canvas.
     */
    private boolean useNewtCanvas = false;

    /**
     * Get the context that is used to render the graphics.
     * 
     * @return the used GL context.
     */
    public static GLAutoDrawable getGLAutoDrawable() {
        return activeContext;
    }

    @Override
    public void addInputListener(final Object listener) {
        getRenderArea();
        renderArea.addInputListener(listener);
    }

    /**
     * Apply a object to the current render events.
     * 
     * @param offX the x share of the offset
     * @param offY the y share of the offset
     */
    @Override
    public void applyOffset(final float offX, final float offY) {
        if (canvas != null) {
            final GL gl = GLU.getCurrentGL();
            if (gl.isGL2ES1()) {
                final GL2ES1 gl2 = gl.getGL2ES1();
                gl2.glPushMatrix();
                gl2.glTranslatef(offX, offY, 0.f);
            }
        }
    }

    /**
     * Add a scaling factor to the display. All graphics are scaled with this
     * value.
     * 
     * @param scale new scaling value
     */
    @Override
    public void applyScaling(final float scale) {
        if (canvas == null) {
            return;
        }
        final GL gl = GLU.getCurrentGL();
        if (!gl.isGL2ES1()) {
            return;
        }
        final GL2ES1 gl2 = GLU.getCurrentGL().getGL2ES1();
        gl2.glPushMatrix();
        if (!areaLimits.isEmpty()) {
            final Rectangle lastRect = areaLimits.getLast();
            if (scale > 1.f) {
                gl2.glTranslatef(-lastRect.getWidth() / scale,
                    -lastRect.getHeight() / scale, 0);
            } else {
                gl2.glTranslatef(lastRect.getWidth() * scale,
                    lastRect.getHeight() * scale, 0);
            }
        }
        gl2.glScalef(scale, scale, 1.f);
    }

    /**
     * Get the height of the display.
     * 
     * @return the height of the display
     */
    @Override
    public int getHeight() {
        return getRenderArea().getHeight();
    }

    /**
     * Get the objects drawn during the last loop.
     * 
     * @return the objects drawn during the last loop
     */
    @Override
    public int getObjects() {
        return AbstractSprite.getDrawnObjects();
    }

    /**
     * Get the list of possible screen resolutions.
     * 
     * @return the list of possible screen resolutions
     */
    @Override
    public GraphicResolution[] getPossibleResolutions() {
        if (possibleResolutions == null) {
            final ArrayList<GraphicResolution> list =
                new ArrayList<GraphicResolution>();
            final GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
            final GraphicsDevice[] gs = ge.getScreenDevices();

            for (final GraphicsDevice element : gs) {
                final DisplayMode[] dms = element.getDisplayModes();
                for (final DisplayMode dm : dms) {
                    if ((dm.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI)
                        && (dm.getBitDepth() <= 16)) {
                        continue;
                    }
                    if ((dm.getWidth() < 800) || (dm.getHeight() < 600)) {
                        continue;
                    }

                    final GraphicResolution res =
                        new GraphicResolution(dm.getWidth(), dm.getHeight(),
                            dm.getBitDepth(), dm.getRefreshRate());

                    boolean addRes = true;

                    for (final GraphicResolution existingRes : list) {
                        if (existingRes.equals(res)) {
                            addRes = false;
                            break;
                        }
                    }

                    if (addRes) {
                        list.add(new GraphicResolution(dm.getWidth(), dm
                            .getHeight(), dm.getBitDepth(), dm
                            .getRefreshRate()));
                    }
                }

            }

            Collections.sort(list, new ComparatorGraphicResolutions());

            possibleResolutions =
                list.toArray(new GraphicResolution[list.size()]);

        }
        return possibleResolutions;
    }

    /**
     * Get the render area that is used to display everything on the screen.
     * 
     * @return the render area
     */
    @SuppressWarnings("nls")
    @Override
    public Component getRenderArea() {
        if (canvas == null) {
            final GLCapabilities capabilities =
                new GLCapabilities(GLProfile.get(new String[] { GLProfile.GL2,
                    GLProfile.GL2ES2, GLProfile.GL2ES1, GLProfile.GLES2,
                    GLProfile.GLES1 }));
            capabilities.setRedBits(8);
            capabilities.setBlueBits(8);
            capabilities.setGreenBits(8);
            capabilities.setAlphaBits(8);
            capabilities.setDoubleBuffered(true);
            capabilities.setHardwareAccelerated(true);
            capabilities.setOnscreen(true);
            capabilities.setBackgroundOpaque(true);
            capabilities.setSampleBuffers(true);
            capabilities.setStereo(false);

            Constructor constr;

            if (useNEWT()) {
                constr =
                    Reflection
                        .getInstance()
                        .getConstructor(
                            "illarion.graphics.jogl.DisplayNewtAWT(int, int, javax.media.opengl.GLCapabilities)");
            } else {
                constr =
                    Reflection
                        .getInstance()
                        .getConstructor(
                            "illarion.graphics.jogl.DisplayAWT(int, int, javax.media.opengl.GLCapabilities)");
            }

            renderArea =
                (Display) constr.newInstance(Integer.valueOf(displayWidth),
                    Integer.valueOf(displayHeight), capabilities);
            activeContext = renderArea.getDrawable();
            canvas = (Component) renderArea;
        }
        return canvas;
    }

    /**
     * Get the width of the display.
     * 
     * @return the width of the display
     */
    @Override
    public int getWidth() {
        return getRenderArea().getWidth();
    }

    @SuppressWarnings("nls")
    @Override
    public void hideCursor() {
        final int[] pixels = new int[16 * 16];
        final Image cursorImage =
            Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(16, 16, pixels, 0, 16));

        final Cursor cursor =
            Toolkit.getDefaultToolkit().createCustomCursor(cursorImage,
                new Point(0, 0), "invisible");
        getRenderArea().setCursor(cursor);

        if (!getRenderArea().getCursor().equals(cursor)) {
            LOGGER.error("Hiding the cursor failed.");
        }
    }

    /**
     * Check if the display is currently in full screen mode.
     * 
     * @return <code>false</code> always
     */
    @Override
    public boolean isFullscreen() {
        return false;
    }

    @Override
    public boolean isInputListenerSupported(final Object listener) {
        getRenderArea();
        return renderArea.isInputListenerSupported(listener);
    }

    /**
     * Check if a rectangle is inside the current render rectangle.
     * 
     * @param checkRect the rectangle to check
     * @return <code>true</code> in case the rectangle in inside the current
     *         render rectangle
     */
    public boolean isInsideRenderArea(final Rectangle checkRect) {
        if (areaLimits.isEmpty()) {
            return true;
        }
        return areaLimits.getLast().intersects(checkRect);
    }

    @Override
    public void removeInputListener(final Object listener) {
        getRenderArea();
        renderArea.removeInputListener(listener);
    }

    /**
     * Remove the last applied offset.
     */
    @Override
    public void resetOffset() {
        if (canvas == null) {
            return;
        }
        final GL gl = GLU.getCurrentGL();
        if (gl.isGL2ES1()) {
            final GL2ES1 gl2 = gl.getGL2ES1();
            gl2.glPopMatrix();
        }
    }

    /**
     * Remove the last added scaling effect.
     */
    @Override
    public void resetScaling() {
        if (canvas == null) {
            return;
        }
        final GL gl = GLU.getCurrentGL();
        if (gl.isGL2ES1()) {
            final GL2ES1 gl2 = gl.getGL2ES1();
            gl2.glPopMatrix();
        }
    }

    /**
     * Limit the render area to a specified area. After this function is called
     * the render origin 0 0 is located in the lower left edge of this area.
     * Rendering outside of this area is not possible.
     * <p>
     * <b>Important:</b> Calling this function twice results that the second
     * rectangle is reduced to the intersection of the current and the new
     * rectangle. Disabling the area limit using {@link #unsetAreaLimit()} will
     * cause a rollback to the last limit then and will not remove the limit
     * entirely.
     * 
     * @param x the x coordinate of the location of the rectangle defining the
     *            new render area
     * @param y the y coordinate of the location of the rectangle defining the
     *            new render area
     * @param width the width of the render area
     * @param height the height of the render area
     */
    @Override
    public void setAreaLimit(final int x, final int y, final int width,
        final int height) {
        if (canvas == null) {
            return;
        }
        final GL gl = GLU.getCurrentGL();
        if (!gl.isGL2ES1()) {
            return;
        }

        final GL2ES1 gl2 = gl.getGL2ES1();

        final Rectangle newRect = Rectangle.getInstance();
        final Rectangle newRectRaw = Rectangle.getInstance();

        newRect.set(x, y, width, height);
        newRectRaw.set(x, y, width, height);

        if (areaLimits.isEmpty()) {
            gl2.glEnable(GL.GL_SCISSOR_TEST);
        } else {
            newRect.intersect(areaLimits.getLast());
        }
        gl2.glPushMatrix();
        gl2.glScissor(newRect.getLeft(), newRect.getBottom(),
            newRect.getWidth(), newRect.getHeight());
        if (!areaLimits.isEmpty()) {
            final Rectangle lastRect = areaLimitsRaw.getLast();
            gl2.glTranslatef(newRectRaw.getX() - lastRect.getX(),
                newRectRaw.getY() - lastRect.getY(), 0);
        } else {
            gl2.glTranslatef(newRectRaw.getX(), newRectRaw.getY(), 0);
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
        displayHeight = resolution.getHeight();
        displayWidth = resolution.getWidth();
        targetFPS = resolution.getRefreshRate();

        if ((displayHeight == -1) || (displayWidth == -1)) {
            getRenderArea().setPreferredSize(null);
            getRenderArea().setSize(null);
        } else {
            final Dimension size = new Dimension(displayWidth, displayHeight);
            getRenderArea().setPreferredSize(size);
            getRenderArea().setSize(size);
        }
    }

    /**
     * Set the used screen resolution.
     * 
     * @param index the index of the screen resolution in the list returned by
     *            {@link #getPossibleResolutions()}
     */
    @Override
    @SuppressWarnings("nls")
    public void setDisplayMode(final int index) {
        try {
            final GraphicResolution res = getPossibleResolutions()[index];
            setDisplayMode(res);
        } catch (final ArrayIndexOutOfBoundsException ex) {
            LOGGER.error("Invalid Settings, Displaymode index not found");
        }
    }

    /**
     * Set the special options to this display.
     */
    @Override
    public void setDisplayOptions(final String key, final String value) {
        if (key.equals(OPTIONKEY_NEWT)) {
            useNewtCanvas = Boolean.parseBoolean(value);
        }
    }

    /**
     * Set the size of the display to a new value.
     * 
     * @param newWidth the new width of the display
     * @param newHeight the new height of the display
     */
    public void setDisplaySize(final int newWidth, final int newHeight) {
        displayWidth = newWidth;
        displayHeight = newHeight;
    }

    @Override
    public void showCursor() {
        getRenderArea().setCursor(null);
    }

    @Override
    public void shutdown() {
        if (renderArea != null) {
            renderArea.shutdown();
        }
    }

    /**
     * Start the rendering of the graphics.
     */
    @Override
    public void startRendering() {
        final Display cav = (Display) getRenderArea();
        cav.setFPS(targetFPS);
    }

    /**
     * Stop the rendering of the graphics.
     */
    @Override
    public void stopRendering() {
        final Display cav = (Display) getRenderArea();
        cav.setFPS(-1);
    }

    /**
     * Check if the display supports fullscreen.
     * 
     * @return <code>false</code> always
     */
    @Override
    public boolean supportsFullscreen() {
        return false;
    }

    /**
     * Toogle between windowed mode and fullscreen. Does not work currently with
     * JOGL.
     */
    @Override
    public void toogleFullscreen() {
        // nothing
    }

    /**
     * Remove the last added area limit.
     */
    @Override
    public void unsetAreaLimit() {
        if (canvas == null) {
            return;
        }
        final GL gl = GLU.getCurrentGL();
        if (!gl.isGL2ES1()) {
            return;
        }
        final GL2ES1 gl2 = gl.getGL2ES1();
        areaLimits.removeLast().recycle();
        areaLimitsRaw.removeLast().recycle();
        if (areaLimits.isEmpty()) {
            gl2.glDisable(GL.GL_SCISSOR_TEST);
        } else {
            final Rectangle lastRect = areaLimits.getLast();
            gl2.glScissor(lastRect.getLeft(), lastRect.getBottom(),
                lastRect.getWidth(), lastRect.getHeight());
        }
        gl2.glPopMatrix();
    }

    /**
     * This function returns true in case the NEWT rendering system is supposed
     * to be used.
     * 
     * @return <code>true</code> in case the system is supposed to use NEWT
     */
    private boolean useNEWT() {
        return useNewtCanvas;
    }
}
