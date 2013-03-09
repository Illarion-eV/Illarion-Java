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
import org.illarion.engine.graphic.Texture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * This is the implementation of the render device that makes use of the game engine to render its graphics.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class IllarionRenderDevice implements RenderDevice {
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
    public IllarionRenderDevice(@Nonnull final GameContainer container, @Nonnull final String imageDirectory) {
        this.container = container;
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
        return new TextureRenderImage(targetTexture);
    }

    @Override
    public RenderFont createFont(final String filename) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
        // nothing
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void renderQuad(final int x, final int y, final int width, final int height, final Color color) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void renderQuad(final int x, final int y, final int width, final int height, final Color topLeft, final Color topRight, final Color bottomRight, final Color bottomLeft) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void renderImage(final RenderImage image, final int x, final int y, final int width, final int height, final Color color, final float imageScale) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void renderImage(final RenderImage image, final int x, final int y, final int w, final int h, final int srcX, final int srcY, final int srcW, final int srcH, final Color color, final float scale, final int centerX, final int centerY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void renderFont(final RenderFont font, final String text, final int x, final int y, final Color fontColor, final float sizeX, final float sizeY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enableClip(final int x0, final int y0, final int x1, final int y1) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void disableClip() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MouseCursor createMouseCursor(final String filename, final int hotspotX, final int hotspotY) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enableMouseCursor(final MouseCursor mouseCursor) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void disableMouseCursor() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
