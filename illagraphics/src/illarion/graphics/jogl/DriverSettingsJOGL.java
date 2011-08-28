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

import illarion.graphics.Graphics;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.fixedfunc.GLPointerFunc;

import com.jogamp.opengl.util.glsl.fixedfunc.FixedFuncUtil;

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
     * The different modes that are supported by this driver settings.
     * 
     * @author Martin Karing
     * @since 2.00
     * @version 2.00
     */
    public enum Modes {
        /**
         * This mode should be used in case single dots are supposed to be
         * drawn.
         */
        DRAWDOT(new SettingsHandler() {
            @Override
            public void disableSettings(final GL usedGL) {
                if (!usedGL.isGL2ES1() && !usedGL.hasGLSL()) {
                    return;
                }

                final GL2ES1 gl = getGL2ES1(usedGL);
                gl.glDisable(GL2ES1.GL_POINT_SMOOTH);
                gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            }

            @Override
            public void enableSettings(final GL usedGL) {
                if (!usedGL.isGL2ES1() && !usedGL.hasGLSL()) {
                    return;
                }

                final GL2ES1 gl = getGL2ES1(usedGL);
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
            }
        }),

        /**
         * This mode should be used in case lines are supposed to be drawn.
         */
        DRAWLINE(new SettingsHandler() {
            @Override
            public void disableSettings(final GL usedGL) {
                if (!usedGL.isGL2ES1() && !usedGL.hasGLSL()) {
                    return;
                }

                final GL2ES1 gl = getGL2ES1(usedGL);
                gl.glDisable(GL.GL_LINE_SMOOTH);
                gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            }

            @Override
            public void enableSettings(final GL usedGL) {
                if (!usedGL.isGL2ES1() && !usedGL.hasGLSL()) {
                    return;
                }

                final GL2ES1 gl = getGL2ES1(usedGL);
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
            }
        }),

        /**
         * This mode should be used for any vertex array based shapes to draw.
         * The difference to the polygon mode is that the smoothing is disabled.
         */
        DRAWOTHER(new SettingsHandler() {
            @Override
            public void disableSettings(final GL usedGL) {
                if (!usedGL.isGL2ES1() && !usedGL.hasGLSL()) {
                    return;
                }

                final GL2ES1 gl = getGL2ES1(usedGL);
                gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            }

            @Override
            public void enableSettings(final GL usedGL) {
                if (!usedGL.isGL2ES1() && !usedGL.hasGLSL()) {
                    return;
                }

                final GL2ES1 gl = getGL2ES1(usedGL);
                gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            }
        }),

        /**
         * This mode should be used for any vertex array based shapes to draw.
         * The difference to the polygon mode is that the smoothing is disabled.
         * In addition this mode also enables the color array.
         */
        DRAWOTHERCOLOR(new SettingsHandler() {
            @Override
            public void disableSettings(final GL usedGL) {
                if (!usedGL.isGL2ES1() && !usedGL.hasGLSL()) {
                    return;
                }

                final GL2ES1 gl = getGL2ES1(usedGL);
                gl.glDisableClientState(GLPointerFunc.GL_COLOR_ARRAY);
                gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            }

            @Override
            public void enableSettings(final GL usedGL) {
                if (!usedGL.isGL2ES1() && !usedGL.hasGLSL()) {
                    return;
                }

                final GL2ES1 gl = getGL2ES1(usedGL);
                gl.glEnableClientState(GLPointerFunc.GL_COLOR_ARRAY);
                gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            }
        }),

        /**
         * This mode should be used to draw a polygone with line smoothing.
         */
        DRAWPOLY(new SettingsHandler() {
            @Override
            public void disableSettings(final GL usedGL) {
                if (!usedGL.isGL2ES1() && !usedGL.hasGLSL()) {
                    return;
                }

                final GL2ES1 gl = getGL2ES1(usedGL);
                gl.glDisable(GL2GL3.GL_POLYGON_SMOOTH);
                gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
            }

            @Override
            public void enableSettings(final GL usedGL) {
                if (!usedGL.isGL2ES1() && !usedGL.hasGLSL()) {
                    return;
                }

                final GL2ES1 gl = getGL2ES1(usedGL);
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
            }
        }),

        /**
         * This mode should be used to draw a texture directly.
         */
        DRAWTEXTURE(new SettingsHandler() {
            @Override
            public void disableSettings(final GL gl) {
                gl.glDisable(GL.GL_TEXTURE_2D);
            }

            @Override
            public void enableSettings(final GL gl) {
                gl.glEnable(GL.GL_TEXTURE_2D);
            }
        }),

        /**
         * This mode should be used to drawn a texture using a texture pointer.
         */
        DRAWTEXTUREPOINTER(new SettingsHandler() {
            @Override
            public void disableSettings(final GL usedGL) {
                if (!usedGL.isGL2ES1() && !usedGL.hasGLSL()) {
                    return;
                }

                final GL2ES1 gl = getGL2ES1(usedGL);
                gl.glDisable(GL.GL_TEXTURE_2D);
                gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
                gl.glDisableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
            }

            @Override
            public void enableSettings(final GL usedGL) {
                if (!usedGL.isGL2ES1() && !usedGL.hasGLSL()) {
                    return;
                }

                final GL2ES1 gl = getGL2ES1(usedGL);
                gl.glEnable(GL.GL_TEXTURE_2D);
                gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
                gl.glEnableClientState(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
            }
        });

        /**
         * The settings handler of this instance.
         */
        private final SettingsHandler setHandler;

        /**
         * Constructor of a modes instance that takes the required settings
         * handler as parameter.
         * 
         * @param handler the handler
         */
        private Modes(final SettingsHandler handler) {
            setHandler = handler;
        }

        /**
         * Disable a mode.
         * 
         * @param gl the reference to the openGL interface required to disable
         *            the mode
         */
        protected void disable(final GL gl) {
            setHandler.disableSettings(gl);
        }

        /**
         * Enable a mode.
         * 
         * @param gl the reference to the openGL interface required to enable
         *            the mode
         */
        protected void enable(final GL gl) {
            setHandler.enableSettings(gl);
        }
    }

    /**
     * This interface is used to enable and disable the different modes of this
     * driver.
     * 
     * @author Martin Karing
     * @since 2.00
     * @version 2.00
     */
    private interface SettingsHandler {
        /**
         * Disable the settings that are controlled by this handler.
         * 
         * @param gl the reference to the openGL interface
         */
        public void disableSettings(GL gl);

        /**
         * Enable the settings that are controlled by this handler.
         * 
         * @param gl the reference to the openGL interface
         */
        public void enableSettings(GL gl);
    }

    /**
     * The singleton instance of this class.
     */
    private static final DriverSettingsJOGL INSTANCE =
        new DriverSettingsJOGL();

    /**
     * This helper function is used to get a GL2ES1 instance from a normal GL
     * reference in case its possible in any way.
     * 
     * @param gl the reference to the openGL interface
     * @return the GL2ES1 instance its case its possible to transform the
     *         parameter into one.
     */
    protected static GL2ES1 getGL2ES1(final GL gl) {
        if (gl.hasGLSL()) {
            return FixedFuncUtil.getFixedFuncImpl(gl);
        } else if (gl.isGL2ES1()) {
            return gl.getGL2ES1();
        } else {
            return null;
        }
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
     * The currently used modus.
     */
    private Modes currentMode = null;

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
     * Bind a new texture. Make sure to enable a texture mode before you call
     * this function.
     * 
     * @param gl the reference to the openGL interface
     * @param textureID the ID of the new texture
     */
    public void bindTexture(final GL gl, final int textureID) {
        if (currentTexture == textureID) {
            return;
        }

        gl.glBindTexture(GL.GL_TEXTURE_2D, textureID);
        currentTexture = textureID;
    }

    /**
     * Activate a new driver mode.
     * 
     * @param gl the instance of the openGL interface
     * @param newMode the new mode to enable
     */
    public void enableMode(final GL gl, final Modes newMode) {
        if (currentMode == newMode) {
            return;
        }
        if (currentMode != null) {
            currentMode.disable(gl);
        }
        if (newMode != null) {
            newMode.enable(gl);
        }
        currentMode = newMode;
    }
}
