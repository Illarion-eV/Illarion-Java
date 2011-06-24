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
package illarion.graphics.lwjgl.render;

import org.apache.log4j.Logger;

import illarion.graphics.SpriteColor;
import illarion.graphics.lwjgl.GraphicsLWJGLException;
import illarion.graphics.lwjgl.TextureLWJGL;

/**
 * The abstract texture render chooses the real texture render to use. The
 * different texture render differ in terms of performance and requirements.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public abstract class AbstractTextureRender {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(AbstractTextureRender.class);

    /**
     * Constant for {@link #usedRender}. In case this constant is set the
     * display list texture render is used.
     */
    private static final int RENDER_DISPLAYLIST = 1;

    /**
     * Constant for {@link #usedRender}. In case this constant is set the
     * immediate texture render is used.
     */
    private static final int RENDER_IMMEDIATE = 0;

    /**
     * Constant for {@link #usedRender}. In case this constant is set the array
     * pointer texture render is used.
     */
    private static final int RENDER_POINTER = 2;

    /**
     * The priority list for the renderer usable. As they are written in the
     * array they will be checked to find a fitting one.
     */
    private static final int[] RENDER_PRIORITY = new int[] { RENDER_POINTER,
        RENDER_DISPLAYLIST, RENDER_IMMEDIATE };

    /**
     * The selected render that is used to render the textures.
     */
    private static int usedRender = -1;

    /**
     * This renderer finishes the texture render operations in case the current
     * render requires it.
     */
    public static void finish() {
        // no renderer requires the finish action
    }

    /**
     * Get the texture render that is now free to use. The kind of texture
     * render can differ from type. Some return a new instance at each request
     * that works for one sprite exclusive. Some return a singleton instance
     * that collect the data of all render events.
     * 
     * @return the implementation of the texture render to use
     */
    @SuppressWarnings("nls")
    public static AbstractTextureRender getInstance() {
        switch (usedRender) {
            case RENDER_IMMEDIATE:
                return TextureRenderImmediate.getInstance();
            case RENDER_DISPLAYLIST:
                return TextureRenderDisplaylist.getInstance();
            case RENDER_POINTER:
                return TextureRenderPointer.getInstance();
            default:
                for (final int element : RENDER_PRIORITY) {
                    AbstractTextureRender retRender = null;
                    usedRender = element;
                    retRender = getInstance();
                    if (retRender != null) {
                        LOGGER.debug("Used texture render: "
                            + retRender.toString());
                        return retRender;
                    }
                }
        }
        throw new GraphicsLWJGLException("Can't find working texture renderer");
    }

    /**
     * Draw a texture at a specified location.
     * 
     * @param x the x coordinate of the texture
     * @param y the y coordinate of the texture
     * @param z the z coordinate (so the layer) of the texture
     * @param width the width of the area the texture shall be rendered on
     * @param height the height of the area the texture shall be rendered on
     * @param texture the texture that shall be drawn
     * @param color the color that is supposed to be used with that texture
     * @param mirror mirror the texture horizontal
     * @param rotation the degree the texture is rotated by
     */
    public abstract void drawTexture(float x, float y, float z, float width,
        float height, TextureLWJGL texture, SpriteColor color, boolean mirror,
        float rotation);
}
