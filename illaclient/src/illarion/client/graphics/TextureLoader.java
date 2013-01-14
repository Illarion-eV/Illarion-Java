/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.graphics;

import de.lessvoid.nifty.slick2d.render.image.SlickLoadImageException;
import de.lessvoid.nifty.slick2d.render.image.SlickRenderImage;
import de.lessvoid.nifty.slick2d.render.image.loader.SlickRenderImageLoader;
import illarion.common.graphics.AbstractTextureLoader;
import org.apache.log4j.Logger;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.XMLPackedSheet;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Utility class to load textures. It loads up the Atlas textures and supplies
 * the information to the sprites that need to render this textures.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class TextureLoader extends AbstractTextureLoader<SlickTextureAtlas,
        Image> implements SlickRenderImageLoader {

    /**
     * The singleton instance of this texture loader.
     */
    private static final TextureLoader INSTANCE = new TextureLoader();

    /**
     * The error and debug logger of the client.
     */
    private static final Logger LOGGER = Logger.getLogger(TextureLoader.class);

    /**
     * Get the singleton instance of the texture loader.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static TextureLoader getInstance() {
        return INSTANCE;
    }

    /**
     * Create a new texture loader.
     */
    @SuppressWarnings("unchecked")
    private TextureLoader() {
    }

    @Nullable
    @Override
    protected SlickTextureAtlas createTextureAtlas(final String image, final String xmlDefinition) {
        try {
            return new SlickTextureAtlas(new XMLPackedSheet(image, xmlDefinition));
        } catch (@Nonnull final SlickException e) {
            LOGGER.error("Failed loading the texture atlas: " + image);
        }
        return null;
    }

    /**
     * Finish the texture loader and clean up.
     */
    public void cleanup() {
        // clean unneeded data
    }

    @Nonnull
    @Override
    public SlickRenderImage loadImage(final String filename,
                                      final boolean filterLinear) throws SlickLoadImageException {

        return new TextureRenderImage(
                AccessController.doPrivileged(new PrivilegedAction<Image>() {
                    @Override
                    public Image run() {
                        return getTexture(filename);
                    }
                }));
    }

    /**
     * This function simply adds enough calls to the loading list to load all texture atlas files.
     */
    public void enlistAllTextureAtlasLoadQueries() {
        final int loadedTextures = getTotalLoadedAtlasCount();
        final int totalTextures = getTotalAtlasCount();

        if (loadedTextures >= totalTextures) {
            return;
        }

        final DeferredResource resource = new DeferredResource() {
            @Override
            public void load() throws IOException {
                loadNextAtlas();
            }

            @Nonnull
            @Override
            public String getDescription() {
                return "Loading texture";
            }
        };

        for (int i = loadedTextures; i < totalTextures; i++) {
            LoadingList.get().add(resource);
        }
    }
}
