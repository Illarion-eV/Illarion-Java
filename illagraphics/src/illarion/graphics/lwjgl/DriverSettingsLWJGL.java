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

import org.lwjgl.opengl.GL11;

import illarion.graphics.Graphics;

/**
 * This is a helper class for the LWJGL render to switch the current render
 * mode. It ensures that the driver calls are only called in case its really
 * needed.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class DriverSettingsLWJGL {
    /**
     * The singleton instance of this class.
     */
    private static final DriverSettingsLWJGL INSTANCE =
        new DriverSettingsLWJGL();

    /**
     * Driver settings constant for the modus drawing dots.
     */
    private static final int MODE_DRAWDOT = 1;

    /**
     * Driver settings constant for the modus drawing lines.
     */
    private static final int MODE_DRAWLINE = 2;

    /**
     * Driver settings constant for the modus drawing other stuff.
     */
    private static final int MODE_DRAWOTHER = 4;

    /**
     * Driver settings constant for the modus drawing polygons.
     */
    private static final int MODE_DRAWPOLY = 3;

    /**
     * Driver settings constant for the modus drawing textures.
     */
    private static final int MODE_TEXTURE = 0;

    /**
     * Driver settings constant for the modus drawing texture by pointers.
     */
    private static final int MODE_TEXTUREPOINTER = 5;

    /**
     * The currently used modus.
     */
    private int currentMode = -1;

    /**
     * The currently activated texture.
     */
    private int currentTexture = -1;

    /**
     * Private constructor, to avoid any instances but the singleton instance.
     */
    private DriverSettingsLWJGL() {
        // nothing to do
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this helper class
     */
    public static DriverSettingsLWJGL getInstance() {
        return INSTANCE;
    }

    /**
     * Setup OpenGL to render dots.
     */
    public void enableDrawDot() {
        if (currentMode != MODE_DRAWDOT) {
            disableLast();
            final int quality = Graphics.getInstance().getQuality();
            if (quality >= Graphics.QUALITY_NORMAL) {
                GL11.glEnable(GL11.GL_POINT_SMOOTH);
                if (quality >= Graphics.QUALITY_HIGH) {
                    GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_NICEST);
                } else {
                    GL11.glHint(GL11.GL_POINT_SMOOTH_HINT, GL11.GL_FASTEST);
                }
            } else {
                GL11.glDisable(GL11.GL_POINT_SMOOTH);
            }
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            currentMode = MODE_DRAWDOT;
        }
    }

    /**
     * Setup OpenGL to render lines.
     */
    public void enableDrawLine() {
        if (currentMode != MODE_DRAWLINE) {
            disableLast();
            final int quality = Graphics.getInstance().getQuality();
            if (quality >= Graphics.QUALITY_NORMAL) {
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                if (quality >= Graphics.QUALITY_HIGH) {
                    GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
                } else {
                    GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_FASTEST);
                }
            } else {
                GL11.glDisable(GL11.GL_LINE_SMOOTH);
            }
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            currentMode = MODE_DRAWLINE;
        }
    }

    /**
     * Setup OpenGL to render something generic.
     */
    public void enableDrawOther() {
        if (currentMode != MODE_DRAWOTHER) {
            disableLast();
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            currentMode = MODE_DRAWOTHER;
        }
    }

    /**
     * Setup OpenGL to render polygons.
     */
    public void enableDrawPoly() {
        if (currentMode != MODE_DRAWPOLY) {
            disableLast();
            final int quality = Graphics.getInstance().getQuality();
            if (quality == Graphics.QUALITY_MAX) {
                GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
                GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
            } else {
                GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
            }
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            currentMode = MODE_DRAWPOLY;
        }
    }

    /**
     * Setup OpenGL to render a texture.
     * 
     * @param textureID the ID of the texture that shall be bind
     */
    public void enableTexture(final int textureID) {
        if (currentMode != MODE_TEXTURE) {
            disableLast();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            currentMode = MODE_TEXTURE;
        }

        if ((currentTexture != textureID) && (textureID > -1)) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        }
        currentTexture = textureID;
    }

    /**
     * Setup OpenGL to render a texture by using texture pointers.
     * 
     * @param textureID the ID of the texture that shall be bind
     */
    public void enableTexturePointer(final int textureID) {
        if (currentMode != MODE_TEXTUREPOINTER) {
            disableLast();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            currentMode = MODE_TEXTUREPOINTER;
        }

        if ((currentTexture != textureID) && (textureID > -1)) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        }
        currentTexture = textureID;
    }

    /**
     * Disable the last activated mode.
     */
    private void disableLast() {
        if (currentMode == MODE_TEXTURE) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
        } else if (currentMode == MODE_TEXTUREPOINTER) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        } else if (currentMode == MODE_DRAWDOT) {
            GL11.glDisable(GL11.GL_POINT_SMOOTH);
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        } else if (currentMode == MODE_DRAWLINE) {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        } else if (currentMode == MODE_DRAWPOLY) {
            GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        } else if (currentMode == MODE_DRAWPOLY) {
            GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
            GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        }
    }
}
