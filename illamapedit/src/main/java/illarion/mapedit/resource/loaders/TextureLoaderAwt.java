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
package illarion.mapedit.resource.loaders;

import illarion.mapedit.resource.Resource;
import org.illarion.engine.backend.shared.AbstractTextureManager;
import org.illarion.engine.graphic.Texture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * This texture loader fetches textures as AWT images.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public final class TextureLoaderAwt extends AbstractTextureManager<BufferedImage> implements Resource {
    public static final class AwtTexture implements Texture {
        @Nonnull
        private final BufferedImage image;

        AwtTexture(@Nonnull BufferedImage image) {
            this.image = image;
        }

        @Nonnull
        @Override
        public Texture getSubTexture(int x, int y, int width, int height) {
            return new AwtTexture(image.getSubimage(x, y, width, height));
        }

        @Override
        public int getHeight() {
            return image.getHeight();
        }

        @Override
        public int getWidth() {
            return image.getWidth();
        }

        @Override
        public void dispose() {
        }

        public Image getImage() {
            return image;
        }
    }

    /**
     * The singleton instance of this class.
     */
    private static final TextureLoaderAwt INSTANCE = new TextureLoaderAwt();

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static TextureLoaderAwt getInstance() {
        return INSTANCE;
    }

    /**
     * The private constructor to prevent the creation of additional instances of this class.
     */
    private TextureLoaderAwt() {
        super();
        addTextureDirectory("items");
        addTextureDirectory("tiles");
    }

    @Nullable
    @Override
    protected BufferedImage loadTextureData(@Nonnull String textureName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream in = cl.getResourceAsStream(textureName)) {
            if (in == null) {
                return null;
            }
            BufferedImage orgImage = ImageIO.read(in);

            GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration();

            if (orgImage.getColorModel().equals(gfxConfig.getColorModel())) {
                return orgImage;
            }

            BufferedImage newImage = gfxConfig
                    .createCompatibleImage(orgImage.getWidth(), orgImage.getHeight(), orgImage.getTransparency());

            Graphics2D g2d = (Graphics2D) newImage.getGraphics();

            g2d.drawImage(orgImage, 0, 0, null);
            g2d.dispose();

            return newImage;
        } catch (IOException e) {
            return null;
        }
    }

    @Nullable
    @Override
    protected Texture loadTexture(@Nonnull String resource, @Nonnull BufferedImage preLoadData) {
        return new AwtTexture(preLoadData);
    }

    @Override
    public void load() throws IOException {
        startLoading();
        while (!isLoadingDone()) {
            update();
        }
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Textures";
    }
}
