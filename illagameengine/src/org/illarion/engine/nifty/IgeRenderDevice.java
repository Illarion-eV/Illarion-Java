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
package org.illarion.engine.nifty;

import de.lessvoid.nifty.render.BlendMode;
import de.lessvoid.nifty.spi.render.MouseCursor;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.render.RenderFont;
import de.lessvoid.nifty.spi.render.RenderImage;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;
import org.illarion.engine.GameContainer;
import org.illarion.engine.assets.CursorManager;
import org.illarion.engine.graphic.BlendingMode;
import org.illarion.engine.graphic.Font;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.Texture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * This is the implementation of the render device that makes use of the game engine to render its graphics.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class IgeRenderDevice implements RenderDevice {
    /**
     * The the container this GUI is displayed in.
     */
    @Nonnull
    private final GameContainer container;

    /**
     * Create a new render device.
     *
     * @param container      the container the GUI is displayed in
     * @param imageDirectory the directory the GUI is supposed to fetch its images from
     */
    public IgeRenderDevice(@Nonnull final GameContainer container, @Nonnull final String imageDirectory) {
        this.container = container;
        tempColor1 = new org.illarion.engine.graphic.Color(org.illarion.engine.graphic.Color.WHITE);
        tempColor2 = new org.illarion.engine.graphic.Color(org.illarion.engine.graphic.Color.WHITE);
        tempColor3 = new org.illarion.engine.graphic.Color(org.illarion.engine.graphic.Color.WHITE);
        tempColor4 = new org.illarion.engine.graphic.Color(org.illarion.engine.graphic.Color.WHITE);
    }

    @Override
    public void setResourceLoader(final NiftyResourceLoader niftyResourceLoader) {
        // nothing to do
    }

    @Nullable
    @Override
    public RenderImage createImage(final String filename, final boolean filterLinear) {
        final Texture targetTexture = container.getEngine().getAssets().getTextureManager().getTexture(filename);
        if (targetTexture == null) {
            return null;
        }
        return new IgeRenderImage(targetTexture);
    }

    @Nullable
    @Override
    public RenderFont createFont(final String filename) {
        final Font requestedFont = container.getEngine().getAssets().getFontManager().getFont(filename);
        if (requestedFont == null) {
            return null;
        }
        return new IgeRenderFont(requestedFont);
    }

    @Override
    public int getWidth() {
        return container.getWidth();
    }

    @Override
    public int getHeight() {
        return container.getHeight();
    }

    @Override
    public void beginFrame() {
        setBlendMode(BlendMode.BLEND);
    }

    @Override
    public void endFrame() {
        // nothing
    }

    @Override
    public void clear() {
        container.getEngine().getGraphics().clear();
    }

    @Override
    public void setBlendMode(final BlendMode renderMode) {
        final Graphics graphics = container.getEngine().getGraphics();
        switch (renderMode) {
            case MULIPLY:
                graphics.setBlendingMode(BlendingMode.Multiply);
                break;
            case BLEND:
                graphics.setBlendingMode(BlendingMode.AlphaBlend);
                break;
        }
    }

    @Override
    public void renderQuad(final int x, final int y, final int width, final int height, final Color color) {
        transferColor(color, tempColor1);
        container.getEngine().getGraphics().drawRectangle(x, y, width, height, tempColor1);
    }

    @Override
    public void renderQuad(final int x, final int y, final int width, final int height, final Color topLeft,
                           final Color topRight, final Color bottomRight, final Color bottomLeft) {
        transferColor(topLeft, tempColor1);
        transferColor(topRight, tempColor2);
        transferColor(bottomLeft, tempColor3);
        transferColor(bottomRight, tempColor4);
        final Graphics g = container.getEngine().getGraphics();
        g.drawRectangle(x, y, width, height, tempColor1, tempColor2, tempColor3, tempColor4);
    }

    @Override
    public void renderImage(final RenderImage image, final int x, final int y, final int width, final int height, final Color color, final float imageScale) {
        if (image instanceof IgeRenderImage) {
            final Texture texture = ((IgeRenderImage) image).getTexture();
            final Graphics g = container.getEngine().getGraphics();
            transferColor(color, tempColor1);
            g.drawTexture(texture, x, y, Math.round(width * imageScale), Math.round(height * imageScale), tempColor1);
        }
    }

    @Override
    public void renderImage(final RenderImage image, final int x, final int y, final int w, final int h, final int srcX, final int srcY, final int srcW, final int srcH, final Color color, final float scale, final int centerX, final int centerY) {
        if (image instanceof IgeRenderImage) {
            final Texture texture = ((IgeRenderImage) image).getTexture();
            final Graphics g = container.getEngine().getGraphics();
            transferColor(color, tempColor1);
            g.drawTexture(texture, x, y, Math.round(w * scale), Math.round(h * scale), srcX, srcY, srcW, srcH,
                    tempColor1);
        }
    }

    /**
     * A instance of the color class for temporary use. This class is used to transfer the color values from
     * Nifty-GUI color instances to instances of the game engine.
     */
    private final org.illarion.engine.graphic.Color tempColor1;

    /**
     * A instance of the color class for temporary use. This class is used to transfer the color values from
     * Nifty-GUI color instances to instances of the game engine.
     */
    private final org.illarion.engine.graphic.Color tempColor2;

    /**
     * A instance of the color class for temporary use. This class is used to transfer the color values from
     * Nifty-GUI color instances to instances of the game engine.
     */
    private final org.illarion.engine.graphic.Color tempColor3;

    /**
     * A instance of the color class for temporary use. This class is used to transfer the color values from
     * Nifty-GUI color instances to instances of the game engine.
     */
    private final org.illarion.engine.graphic.Color tempColor4;

    @Override
    public void renderFont(final RenderFont font, final String text, final int x, final int y, final Color fontColor, final float sizeX, final float sizeY) {
        if (font instanceof IgeRenderFont) {
            final Graphics g = container.getEngine().getGraphics();
            transferColor(fontColor, tempColor1);
            g.drawText(((IgeRenderFont) font).getFont(), text, tempColor1, x, y, sizeX, sizeY);
        }
    }

    /**
     * Transfer color values from a instance of the Nifty-GUI color to a instance of a game engine color.
     *
     * @param source the Nifty-GUI source color
     * @param target the game engine target color
     */
    private static void transferColor(@Nonnull final Color source,
                                      @Nonnull final org.illarion.engine.graphic.Color target) {
        target.setRedf(source.getRed());
        target.setGreenf(source.getGreen());
        target.setBluef(source.getBlue());
        target.setAlphaf(source.getAlpha());
    }

    @Override
    public void enableClip(final int x0, final int y0, final int x1, final int y1) {
        container.getEngine().getGraphics().setClippingArea(x0, y0, x1 - x0, y1 - y0);
    }

    @Override
    public void disableClip() {
        container.getEngine().getGraphics().unsetClippingArea();
    }

    @Nullable
    @Override
    public MouseCursor createMouseCursor(final String filename, final int hotspotX, final int hotspotY) throws IOException {
        final CursorManager cursorManager = container.getEngine().getAssets().getCursorManager();
        final org.illarion.engine.MouseCursor cursor = cursorManager.getCursor(filename, hotspotX, hotspotY);
        if (cursor == null) {
            return null;
        }
        return new IgeMouseCursor(cursor);
    }

    @Override
    public void enableMouseCursor(final MouseCursor mouseCursor) {
        if (mouseCursor instanceof IgeMouseCursor) {
            container.setMouseCursor(((IgeMouseCursor) mouseCursor).getCursor());
        }
    }

    @Override
    public void disableMouseCursor() {
        container.setMouseCursor(null);
    }
}
