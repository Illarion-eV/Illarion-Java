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

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLPointerFunc;

import com.jogamp.opengl.util.glsl.fixedfunc.FixedFuncUtil;

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
public final class DriverSettingsJOGL {
    /**
     * The singleton instance of this class.
     */
    private static final DriverSettingsJOGL INSTANCE =
        new DriverSettingsJOGL();
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
    private DriverSettingsJOGL() {
        // nothing to do
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this helper class
     */
    public static DriverSettingsJOGL getInstance() {
        return INSTANCE;
    }

    /**
     * Setup OpenGL to render dots.
     * 
     * @param gl the openGL implementation used to render the graphics
     */
    public void enableDrawDot(final GL2ES1 gl) {
        if (currentMode != MODE_DRAWDOT) {
            disableLast(gl);
            final int quality = Graphics.getInstance().getQuality();

            if (quality >= Graphics.QUALITY_NORMAL) {
                gl.glEnable(GL2ES1.GL_POINT_SMOOTH);
                if (quality >= Graphics.QUALITY_HIGH) {
                    gl.glHint(GL2ES1.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
                } else {
                    gl.glHint(GL2ES1.GL_POINT_SMOOTH_HINT, GL.GL_FASTEST);
                }
            } else {
                gl.glDisable(GL2ES1.GL_POINT_SMOOTH);
            }
            gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            currentMode = MODE_DRAWDOT;
        }
    }

    /**
     * Setup OpenGL to render lines.
     * 
     * @param gl the openGL implementation used to render the graphics
     */
    public void enableDrawLine(final GL2ES1 gl) {
        if (currentMode != MODE_DRAWLINE) {
            disableLast(gl);
            final int quality = Graphics.getInstance().getQuality();
            if (quality >= Graphics.QUALITY_NORMAL) {
                gl.glEnable(GL.GL_LINE_SMOOTH);
                if (quality >= Graphics.QUALITY_HIGH) {
                    gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
                } else {
                    gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_FASTEST);
                }
            } else {
                gl.glDisable(GL.GL_LINE_SMOOTH);
            }
            gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            currentMode = MODE_DRAWLINE;
        }
    }

    /**
     * Setup OpenGL to render something generic.
     * 
     * @param gl the openGL implementation used to render the graphics
     */
    public void enableDrawOther(final GL2ES1 gl) {
        if (currentMode != MODE_DRAWOTHER) {
            disableLast(gl);
            gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            currentMode = MODE_DRAWOTHER;
        }
    }

    /**
     * Setup OpenGL to render polygons.
     * 
     * @param gl the openGL implementation used to render the graphics
     */
    public void enableDrawPoly(final GL2ES1 gl) {
        if (currentMode != MODE_DRAWPOLY) {
            disableLast(gl);
            final int quality = Graphics.getInstance().getQuality();
            if (quality == Graphics.QUALITY_MAX) {
                if (gl.isGL2()) {
                    gl.glEnable(GL2GL3.GL_POLYGON_SMOOTH);
                    gl.glHint(GL2GL3.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
                } else {
                    gl.glDisable(GL2GL3.GL_POLYGON_SMOOTH);
                }
            }
            gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            currentMode = MODE_DRAWPOLY;
        }
    }

    /**
     * Setup OpenGL to render a texture.
     * 
     * @param gl the openGL implementation used to render the graphics
     * @param textureID the ID of the texture that shall be bind
     */
    public void enableTexture(final GL gl, final int textureID) {
        if (currentMode != MODE_TEXTURE) {
            disableLast(gl);
            gl.glEnable(GL.GL_TEXTURE_2D);
            currentMode = MODE_TEXTURE;
        }

        if ((currentTexture != textureID) && (textureID > -1)) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, textureID);
        }
        currentTexture = textureID;
    }

    /**
     * Setup OpenGL to render a texture by using texture pointers.
     * 
     * @param gl the openGL implementation used to render the graphics
     * @param textureID the ID of the texture that shall be bind
     */
    public void enableTexturePointer(final GL2ES1 gl, final int textureID) {
        if (currentMode != MODE_TEXTUREPOINTER) {
            disableLast(gl);
            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
            currentMode = MODE_TEXTUREPOINTER;
        }

        if ((currentTexture != textureID) && (textureID > -1)) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, textureID);
        }
        currentTexture = textureID;
    }

    /**
     * Disable the last activated mode.
     */
    private void disableLast(final GL usedGL) {
        switch (currentMode) {
            case MODE_TEXTURE:
                usedGL.glDisable(GL.GL_TEXTURE_2D);
                break;

            case MODE_TEXTUREPOINTER:
                usedGL.glDisable(GL.GL_TEXTURE_2D);
                if (usedGL.isGL2ES1() || usedGL.hasGLSL()) {
                    final GL2ES1 gl;
                    if (usedGL.hasGLSL()) {
                        gl = FixedFuncUtil.getFixedFuncImpl(usedGL);
                    } else {
                        gl = usedGL.getGL2ES1();
                    }
                    gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
                    gl.glDisableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
                }
                break;

            case MODE_DRAWDOT:
                if (usedGL.isGL2ES1() || usedGL.hasGLSL()) {
                    final GL2ES1 gl;
                    if (usedGL.hasGLSL()) {
                        gl = FixedFuncUtil.getFixedFuncImpl(usedGL);
                    } else {
                        gl = usedGL.getGL2ES1();
                    }
                    gl.glDisable(GL2ES1.GL_POINT_SMOOTH);
                    gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
                }
                break;

            case MODE_DRAWLINE:
                usedGL.glDisable(GL.GL_LINE_SMOOTH);
                if (usedGL.isGL2ES1() || usedGL.hasGLSL()) {
                    final GL2ES1 gl;
                    if (usedGL.hasGLSL()) {
                        gl = FixedFuncUtil.getFixedFuncImpl(usedGL);
                    } else {
                        gl = usedGL.getGL2ES1();
                    }
                    gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
                }
                break;

            case MODE_DRAWPOLY:
                if (usedGL.isGL2ES1() || usedGL.hasGLSL()) {
                    final GL2ES1 gl;
                    if (usedGL.hasGLSL()) {
                        gl = FixedFuncUtil.getFixedFuncImpl(usedGL);
                    } else {
                        gl = usedGL.getGL2ES1();
                    }
                    if (gl.isGL2()) {
                        gl.glDisable(GL2GL3.GL_POLYGON_SMOOTH);
                    }
                    gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
                }
                break;

            case MODE_DRAWOTHER:
                if (usedGL.isGL2ES1() || usedGL.hasGLSL()) {
                    final GL2ES1 gl;
                    if (usedGL.hasGLSL()) {
                        gl = FixedFuncUtil.getFixedFuncImpl(usedGL);
                    } else {
                        gl = usedGL.getGL2ES1();
                    }
                    gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
                }
                break;
        }
    }
}
