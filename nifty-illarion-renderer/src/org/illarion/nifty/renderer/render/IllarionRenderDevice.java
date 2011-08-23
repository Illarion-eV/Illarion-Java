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

import illarion.graphics.BlendingMode;
import illarion.graphics.Drawer;
import illarion.graphics.Graphics;
import illarion.graphics.RenderDisplay;
import illarion.graphics.SpriteColor;

import java.io.IOException;

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
     * This display that is used for rendering the graphics into.
     */
    private final RenderDisplay display;

    /**
     * The drawer instance that is used to draw primitives on the screen.
     */
    private final Drawer drawer;

    /**
     * Constructor of the render device that takes the source of any new sprites
     * as parameter.
     * 
     * @param disp the display that is used to render the GUI in
     * @param imgFactory the factory that supplies this device with the
     *            renderable images
     */
    public IllarionRenderDevice(final RenderDisplay disp,
        final RenderImageFactory imgFactory) {
        imageFactory = imgFactory;
        display = disp;
        drawer = Graphics.getInstance().getDrawer();

        TEMP_COLOR =
            new SpriteColor[] { Graphics.getInstance().getSpriteColor(),
                Graphics.getInstance().getSpriteColor(),
                Graphics.getInstance().getSpriteColor(),
                Graphics.getInstance().getSpriteColor() };
    }

    /**
     * Fetch a new image that is renderable on this render device.
     * 
     * @param filename the filename of the image requested
     * @param filterLinear <code>true</code> for low quality linear filtering,
     *            else high-quality cubic filtering is used
     */
    @Override
    public RenderImage createImage(final String filename,
        final boolean filterLinear) {
        return imageFactory.getImage(filename, filterLinear);
    }

    /**
     * Get a font that is supposed to be used to render text in the GUI.
     * 
     * @param filename the name of the font
     * @return the font
     */
    @Override
    public RenderFont createFont(final String filename) {
        return IllarionRenderFont.getFont(filename);
    }

    /**
     * Get the width of the area the GUI is rendered into.
     * 
     * @return the width in pixels
     */
    @Override
    public int getWidth() {
        return display.getWidth();
    }

    /**
     * Get the height of the area the GUI is rendered into.
     * 
     * @return the height in pixels
     */
    @Override
    public int getHeight() {
        return display.getHeight();
    }

    /**
     * Start rendering a new frame.
     */
    @Override
    public void beginFrame() {
        // nothing needs to be done
    }

    /**
     * Finish rendering the frame.
     */
    @Override
    public void endFrame() {
        disableClip();
    }

    /**
     * Clear the screen.
     */
    @Override
    public void clear() {
        display.clearScreen();
    }

    /**
     * Set the blending mode.
     * 
     * @param renderMode the new blending mode
     */
    @Override
    public void setBlendMode(final BlendMode renderMode) {
        switch (renderMode) {
            case BLEND:
                display.setBlendingMode(BlendingMode.BLEND);
                break;
            case MULIPLY:
                display.setBlendingMode(BlendingMode.MULTIPLY);
                break;
        }
    }

    /**
     * Some instances of sprite colors that are required to send the proper
     * color values to the illarion graphic engine.
     */
    private final SpriteColor TEMP_COLOR[];

    /**
     * Render a rectangle.
     * 
     * @param x the x origin of the rectangle
     * @param y the y origin of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color the color of the rectangle
     */
    @Override
    public void renderQuad(final int x, final int y, final int width,
        final int height, final Color color) {
        transferColorValues(color, TEMP_COLOR[0]);
        drawer.drawRectangle(x, y, x + width, y + height, TEMP_COLOR[0]);
    }

    /**
     * Render a rectangle.
     * 
     * @param x the x origin of the rectangle
     * @param y the y origin of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param topLeft the color in the top left edge of the rectangle
     * @param topRight the color in the top right edge of the rectangle
     * @param bottomRight the color in the bottom right edge of the rectangle
     * @param bottomLeft the color in the bottom left edge of the rectangle
     */
    @Override
    public void renderQuad(final int x, final int y, final int width,
        final int height, final Color topLeft, final Color topRight,
        final Color bottomRight, final Color bottomLeft) {
        transferColorValues(topLeft, TEMP_COLOR[0]);
        transferColorValues(topRight, TEMP_COLOR[1]);
        transferColorValues(bottomRight, TEMP_COLOR[2]);
        transferColorValues(bottomLeft, TEMP_COLOR[3]);

        drawer.drawRectangle(x, y, x + width, y + height, TEMP_COLOR[0],
            TEMP_COLOR[1], TEMP_COLOR[3], TEMP_COLOR[2]);

    }
    
    /**
     * This helper function is used to transfer the color values of a NiftyGUI
     * color to a Illarion sprite color.
     * 
     * @param src the source nifty color
     * @param tar the target Illarion color
     */
    private static void transferColorValues(final Color src, final SpriteColor tar) {
        tar.set(src.getRed(), src.getGreen(), src.getBlue());
        tar.setAlpha(src.getAlpha());
    }

    /*
     * (non-Javadoc)
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

    /**
     * Render a line of text to the screen using a specified font.
     * 
     * @param font the font to be used for the rendering
     * @param text the text to be rendered
     * @param x the x coordinate where the text it supposed to begin
     * @param y the y coordinate where the text is supposed to begin
     * @param fontColor the color the font is supposed to be rendered in
     * @param size the size of the font
     */
    @Override
    public void renderFont(final RenderFont font, final String text, final int x, final int y,
        final Color fontColor, final float size) {
        
        ((IllarionRenderFont) font).renderString(text, x, y, fontColor, size);
    }
    
    /**
     * This variable stores if the clipping is currently enabled.
     */
    private boolean clippingActive = false;

    /*
     * (non-Javadoc)
     * @see de.lessvoid.nifty.spi.render.RenderDevice#enableClip(int, int, int,
     * int)
     */
    @Override
    public void enableClip(int x0, int y0, int x1, int y1) {
        disableClip();
        display.setAreaLimit(x0, y0, x1 - x0, y1 - y0);
        clippingActive = true;
    }

    /*
     * (non-Javadoc)
     * @see de.lessvoid.nifty.spi.render.RenderDevice#disableClip()
     */
    @Override
    public void disableClip() {
        if (clippingActive) {
            display.unsetAreaLimit();
            clippingActive = false;
        }
    }

    /*
     * (non-Javadoc)
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
     * @see de.lessvoid.nifty.spi.render.RenderDevice#disableMouseCursor()
     */
    @Override
    public void disableMouseCursor() {
        // TODO Auto-generated method stub

    }

}
