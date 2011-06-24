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

import illarion.graphics.MaskUtil;

/**
 * This is the LWJGL implementation for the mask utility that can be used to
 * limit the render area in non-rectangle ways.
 * <p>
 * Drawing a mask basically works this way that you first start drawing a mask
 * and use the Drawer utility then to draw the shapes of the mask. After this is
 * done you finish the mask and then you are able to select if you want to draw
 * anything ON the mask or outside the mask. After you are done with all
 * operations that need the mask you just discard the mask again so the render
 * are acts normally again.
 * </p>
 * Note that drawing a mask takes about the same time as drawing shapes into the
 * color buffer.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class MaskUtilLWJGL implements MaskUtil {
    /**
     * Start defining a mask. After calling this you need to use the drawer
     * functions to mask out the needed areas.
     */
    @Override
    public void defineMask() {
        GL11.glDepthMask(true);
        GL11.glClearDepth(1);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDepthFunc(GL11.GL_ALWAYS);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glColorMask(false, false, false, false);
    }

    /**
     * Start drawing outside of the mask. All following drawing operations will
     * only become visible in case they are outside the area that was masked.
     */
    @Override
    public void drawOffMask() {
        GL11.glDepthFunc(GL11.GL_NOTEQUAL);
    }

    /**
     * Start drawing on the mask. All following drawing operation will only
     * become visible in case they are within the area that was masked.
     */
    @Override
    public void drawOnMask() {
        GL11.glDepthFunc(GL11.GL_EQUAL);
    }

    /**
     * Finish defining the mask. After this all render actions become visible on
     * the screen again. The mask is finished after this operation. Its not
     * possible anymore to mask out more areas.
     */
    @Override
    public void finishDefineMask() {
        GL11.glDepthMask(false);
        GL11.glColorMask(true, true, true, true);
    }

    /**
     * Discard the mask. All render operations will act in the normal way again.
     */
    @Override
    public void resetMask() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }
}
