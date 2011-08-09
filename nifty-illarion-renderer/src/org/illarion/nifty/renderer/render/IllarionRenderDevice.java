/*
 * This file is part of the Illarion Nifty-GUI binding.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Nifty-GUI binding is free software: you can redistribute i
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * The Illarion Nifty-GUI binding is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Nifty-GUI binding. If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.renderer.render;

import java.io.IOException;

import illarion.common.util.ObjectSource;

import illarion.graphics.Sprite;

import de.lessvoid.nifty.render.BlendMode;
import de.lessvoid.nifty.spi.render.MouseCursor;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.render.RenderFont;
import de.lessvoid.nifty.spi.render.RenderImage;
import de.lessvoid.nifty.tools.Color;

/**
 * This is the render device that is used to display graphics on the screen
 * using the Illarion Graphic engine.
 * 
 * @author Martin Karing
 * @since 1.22/1.3
 * @version 1.22/1.3
 */
public final class IllarionRenderDevice implements RenderDevice {
    /**
     * The factory that supplies this render device with the needed renderable
     * images.
     */
    private final RenderImageFactory imageFactory;

    /**
     * Constructor of the render device that takes the source of any new sprites
     * as parameter.
     * 
     * @param imgFactory the factory that supplies this device with the
     *            renderable images
     */
    public IllarionRenderDevice(final RenderImageFactory imgFactory) {
        imageFactory = imgFactory;
    }

    /**
     * Fetch a new image that is renderable on this render device.
     * 
     * @param filename the filename of the image requested
     * @param filterLinear <code>true</code> for low quality linear filtering,
     *            else high-quality cubic filtering is used
     */
    @Override
    public RenderImage createImage(final String filename, final boolean filterLinear) {
        return imageFactory.getImage(filename, filterLinear);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.lessvoid.nifty.spi.render.RenderDevice#createFont(java.lang.String)
     */
    @Override
    public RenderFont createFont(String filename) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.lessvoid.nifty.spi.render.RenderDevice#getWidth()
     */
    @Override
    public int getWidth() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.lessvoid.nifty.spi.render.RenderDevice#getHeight()
     */
    @Override
    public int getHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.lessvoid.nifty.spi.render.RenderDevice#beginFrame()
     */
    @Override
    public void beginFrame() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.lessvoid.nifty.spi.render.RenderDevice#endFrame()
     */
    @Override
    public void endFrame() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.lessvoid.nifty.spi.render.RenderDevice#clear()
     */
    @Override
    public void clear() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.lessvoid.nifty.spi.render.RenderDevice#setBlendMode(de.lessvoid.nifty
     * .render.BlendMode)
     */
    @Override
    public void setBlendMode(BlendMode renderMode) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.lessvoid.nifty.spi.render.RenderDevice#renderQuad(int, int, int,
     * int, de.lessvoid.nifty.tools.Color)
     */
    @Override
    public void renderQuad(int x, int y, int width, int height, Color color) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.lessvoid.nifty.spi.render.RenderDevice#renderQuad(int, int, int,
     * int, de.lessvoid.nifty.tools.Color, de.lessvoid.nifty.tools.Color,
     * de.lessvoid.nifty.tools.Color, de.lessvoid.nifty.tools.Color)
     */
    @Override
    public void renderQuad(int x, int y, int width, int height, Color topLeft,
        Color topRight, Color bottomRight, Color bottomLeft) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.lessvoid.nifty.spi.render.RenderDevice#renderImage(de.lessvoid.nifty
     * .spi.render.RenderImage, int, int, int, int,
     * de.lessvoid.nifty.tools.Color, float)
     */
    @Override
    public void renderImage(RenderImage image, int x, int y, int width,
        int height, Color color, float imageScale) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.lessvoid.nifty.spi.render.RenderDevice#renderImage(de.lessvoid.nifty
     * .spi.render.RenderImage, int, int, int, int, int, int, int, int,
     * de.lessvoid.nifty.tools.Color, float, int, int)
     */
    @Override
    public void renderImage(RenderImage image, int x, int y, int w, int h,
        int srcX, int srcY, int srcW, int srcH, Color color, float scale,
        int centerX, int centerY) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.lessvoid.nifty.spi.render.RenderDevice#renderFont(de.lessvoid.nifty
     * .spi.render.RenderFont, java.lang.String, int, int,
     * de.lessvoid.nifty.tools.Color, float)
     */
    @Override
    public void renderFont(RenderFont font, String text, int x, int y,
        Color fontColor, float size) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.lessvoid.nifty.spi.render.RenderDevice#enableClip(int, int, int,
     * int)
     */
    @Override
    public void enableClip(int x0, int y0, int x1, int y1) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.lessvoid.nifty.spi.render.RenderDevice#disableClip()
     */
    @Override
    public void disableClip() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.lessvoid.nifty.spi.render.RenderDevice#createMouseCursor(java.lang
     * .String, int, int)
     */
    @Override
    public MouseCursor createMouseCursor(String filename, int hotspotX,
        int hotspotY) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.lessvoid.nifty.spi.render.RenderDevice#enableMouseCursor(de.lessvoid
     * .nifty.spi.render.MouseCursor)
     */
    @Override
    public void enableMouseCursor(MouseCursor mouseCursor) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.lessvoid.nifty.spi.render.RenderDevice#disableMouseCursor()
     */
    @Override
    public void disableMouseCursor() {
        // TODO Auto-generated method stub

    }

}
