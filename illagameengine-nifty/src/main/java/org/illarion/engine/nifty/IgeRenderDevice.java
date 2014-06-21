/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
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
     * A instance of the color class for temporary use. This class is used to transfer the color values from
     * Nifty-GUI color instances to instances of the game engine.
     */
    @Nonnull
    private final org.illarion.engine.graphic.Color tempColor1;

    /**
     * A instance of the color class for temporary use. This class is used to transfer the color values from
     * Nifty-GUI color instances to instances of the game engine.
     */
    @Nonnull
    private final org.illarion.engine.graphic.Color tempColor2;

    /**
     * A instance of the color class for temporary use. This class is used to transfer the color values from
     * Nifty-GUI color instances to instances of the game engine.
     */
    @Nonnull
    private final org.illarion.engine.graphic.Color tempColor3;

    /**
     * A instance of the color class for temporary use. This class is used to transfer the color values from
     * Nifty-GUI color instances to instances of the game engine.
     */
    @Nonnull
    private final org.illarion.engine.graphic.Color tempColor4;

    /**
     * Create a new render device.
     *
     * @param container the container the GUI is displayed in
     * @param imageDirectory the directory the GUI is supposed to fetch its images from
     */
    public IgeRenderDevice(@Nonnull GameContainer container, @Nonnull String imageDirectory) {
        this.container = container;
        tempColor1 = new org.illarion.engine.graphic.Color(org.illarion.engine.graphic.Color.WHITE);
        tempColor2 = new org.illarion.engine.graphic.Color(org.illarion.engine.graphic.Color.WHITE);
        tempColor3 = new org.illarion.engine.graphic.Color(org.illarion.engine.graphic.Color.WHITE);
        tempColor4 = new org.illarion.engine.graphic.Color(org.illarion.engine.graphic.Color.WHITE);
    }

    @Override
    public void setResourceLoader(@Nonnull NiftyResourceLoader niftyResourceLoader) {
        // nothing to do
    }

    @Nullable
    @Override
    public RenderImage createImage(@Nonnull String filename, boolean filterLinear) {
        Texture targetTexture = container.getEngine().getAssets().getTextureManager().getTexture(filename);
        if (targetTexture == null) {
            return null;
        }
        return new IgeTextureRenderImage(targetTexture);
    }

    @Nullable
    @Override
    public RenderFont createFont(@Nonnull String filename) {
        Font requestedFont = container.getEngine().getAssets().getFontManager().getFont(filename);
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
    public void setBlendMode(@Nonnull BlendMode renderMode) {
        Graphics graphics = container.getEngine().getGraphics();
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
    public void renderQuad(int x, int y, int width, int height, @Nonnull Color color) {
        transferColor(color, tempColor1);
        container.getEngine().getGraphics().drawRectangle(x, y, width, height, tempColor1);
    }

    @Override
    public void renderQuad(
            int x,
            int y,
            int width,
            int height,
            @Nonnull Color topLeft,
            @Nonnull Color topRight,
            @Nonnull Color bottomRight,
            @Nonnull Color bottomLeft) {
        transferColor(topLeft, tempColor1);
        transferColor(topRight, tempColor2);
        transferColor(bottomLeft, tempColor3);
        transferColor(bottomRight, tempColor4);
        Graphics g = container.getEngine().getGraphics();
        g.drawRectangle(x, y, width, height, tempColor1, tempColor2, tempColor3, tempColor4);
    }

    @Override
    public void renderImage(
            @Nonnull RenderImage image, int x, int y, int width, int height, @Nonnull Color color, float imageScale) {
        if (image instanceof IgeRenderImage) {
            transferColor(color, tempColor1);
            ((IgeRenderImage) image)
                    .renderImage(container.getEngine().getGraphics(), x, y, width, height, tempColor1, imageScale);
        }
    }

    @Override
    public void renderImage(
            @Nonnull RenderImage image,
            int x,
            int y,
            int w,
            int h,
            int srcX,
            int srcY,
            int srcW,
            int srcH,
            @Nonnull Color color,
            float scale,
            int centerX,
            int centerY) {
        if (image instanceof IgeRenderImage) {
            transferColor(color, tempColor1);
            ((IgeRenderImage) image)
                    .renderImage(container.getEngine().getGraphics(), x, y, w, h, srcX, srcY, srcW, srcH, tempColor1,
                                 scale, centerX, centerY);
        }
    }

    @Override
    public void renderFont(
            @Nonnull RenderFont font,
            @Nonnull String text,
            int x,
            int y,
            @Nonnull Color fontColor,
            float sizeX,
            float sizeY) {
        if (font instanceof IgeRenderFont) {
            Graphics g = container.getEngine().getGraphics();
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
    private static void transferColor(
            @Nonnull Color source, @Nonnull org.illarion.engine.graphic.Color target) {
        target.setRedf(source.getRed());
        target.setGreenf(source.getGreen());
        target.setBluef(source.getBlue());
        target.setAlphaf(source.getAlpha());
    }

    @Override
    public void enableClip(int x0, int y0, int x1, int y1) {
        container.getEngine().getGraphics().setClippingArea(x0, y0, x1 - x0, y1 - y0);
    }

    @Override
    public void disableClip() {
        container.getEngine().getGraphics().unsetClippingArea();
    }

    @Nullable
    @Override
    public MouseCursor createMouseCursor(@Nonnull String filename, int hotspotX, int hotspotY)
            throws IOException {
        CursorManager cursorManager = container.getEngine().getAssets().getCursorManager();
        org.illarion.engine.MouseCursor cursor = cursorManager.getCursor(filename, hotspotX, hotspotY);
        if (cursor == null) {
            return null;
        }
        return new IgeMouseCursor(this, cursor);
    }

    @Override
    public void enableMouseCursor(@Nonnull MouseCursor mouseCursor) {
        if (mouseCursor instanceof IgeMouseCursor) {
            container.setMouseCursor(((IgeMouseCursor) mouseCursor).getCursor());
        }
    }

    @Override
    public void disableMouseCursor() {
        container.setMouseCursor(null);
    }
}
