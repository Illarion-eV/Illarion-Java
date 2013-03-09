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
package org.illarion.engine.backend.shared;

import org.apache.log4j.Logger;
import org.illarion.engine.assets.TextureAssetManager;
import org.illarion.engine.graphic.Texture;
import org.xmlpull.mxp1.MXParserFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillClose;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the shared code of the texture manager that is used by all backend implementations in a similar way.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public abstract class AbstractTextureManager implements TextureAssetManager {
    /**
     * The base name of the atlas files.
     */
    @SuppressWarnings("nls")
    private static final String ATLAS_BASE_NAME = "atlas-";

    private static final String IMAGE_EXTENSION = ".png";
    private static final String ATLAS_DEF_EXTENSION = ".xml";

    /**
     * The logger that provides the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractTextureManager.class);

    /**
     * The index of the last atlas that was load. One list entry for each root directory.
     */
    @Nonnull
    private final List<Integer> lastAtlasIndex;

    /**
     * This list stores the amount of atlas textures expected for each root directory.
     */
    @Nonnull
    private final List<Integer> expectedAtlasCount;

    /**
     * The list of known root directories. This list is used to locate
     */
    @Nonnull
    private final List<String> rootDirectories;

    /**
     * The textures that are known to this manager.
     */
    private final Map<String, Texture> textures;

    /**
     * Creates a new texture loader.
     */
    @SuppressWarnings("unchecked")
    protected AbstractTextureManager() {
        lastAtlasIndex = new ArrayList<Integer>();
        expectedAtlasCount = new ArrayList<Integer>();
        rootDirectories = new ArrayList<String>();
        textures = new HashMap<String, Texture>();
    }

    @Override
    public final void addTextureDirectory(@Nonnull final String directory) {
        lastAtlasIndex.add(-1);
        expectedAtlasCount.add(-1);
        if (directory.endsWith("/")) {
            rootDirectories.add(directory);
        } else {
            rootDirectories.add(directory + '/');
        }
    }

    @Nullable
    @Override
    public Texture getTexture(@Nonnull final String name) {
        for (int i = 0; i < rootDirectories.size(); i++) {
            if (name.startsWith(rootDirectories.get(i))) {
                return getTexture(i, name);
            }
        }
        return null;
    }

    @Nullable
    public Texture getTexture(final int directoryIndex, @Nonnull final String name) {
        if (directoryIndex == -1) {
            return null;
        }

        final String cleanName = cleanTextureName(name);

        @Nullable final Texture loadedTexture = textures.get(cleanName);
        if (loadedTexture != null) {
            return loadedTexture;
        }

        @Nullable final Texture directTexture = loadTexture(cleanName + IMAGE_EXTENSION);
        if (directTexture != null) {
            textures.put(cleanName, directTexture);
            return directTexture;
        }

        while (loadNextTextureAtlas(directoryIndex)) {
            @Nullable final Texture newLoadedTexture = textures.get(cleanName);
            if (newLoadedTexture != null) {
                return newLoadedTexture;
            }
        }

        return null;
    }

    @Nonnull
    private static String cleanTextureName(@Nonnull final String name) {
        if (name.endsWith(".png")) {
            return name.substring(0, name.length() - 4);
        }
        return name;
    }

    @Nonnull
    private static String mergePath(@Nonnull final String directory, @Nonnull final String name) {
        if (directory.endsWith("/")) {
            return directory + name;
        }
        return directory + '/' + name;
    }

    @Nullable
    @Override
    public Texture getTexture(@Nonnull final String directory, @Nonnull final String name) {
        return getTexture(getDirectoryIndex(directory), mergePath(directory, name));
    }

    /**
     * Load the texture from a specific resource.
     *
     * @param resource the path to the resource
     * @return the texture loaded or {@code null} in case loading is impossible
     */
    @Nullable
    protected abstract Texture loadTexture(@Nonnull String resource);

    /**
     * Get the index of a root directory.
     *
     * @param directory the directory
     * @return the index of the root directory or {@code -1} in case the directory could not be assigned to a root
     *         directory
     */
    private int getDirectoryIndex(@Nonnull final String directory) {
        return rootDirectories.indexOf(directory);
    }

    private boolean loadNextTextureAtlas(final int directoryIndex) {
        if ((directoryIndex < 0) || (directoryIndex >= rootDirectories.size())) {
            throw new IndexOutOfBoundsException("Directory index is not within valid range");
        }

        final int totalAmount = getAtlasCount(directoryIndex);
        final int lastLoadedIndex = lastAtlasIndex.get(directoryIndex);
        if (lastLoadedIndex < (totalAmount - 1)) {
            lastAtlasIndex.set(directoryIndex, lastLoadedIndex + 1);
            loadTextureAtlas(directoryIndex, lastLoadedIndex + 1);
            return true;
        }

        return false;
    }

    private void loadTextureAtlas(final int directoryIndex, final int atlasIndex) {
        if ((directoryIndex < 0) || (directoryIndex >= rootDirectories.size())) {
            throw new IndexOutOfBoundsException("Directory index is not within valid range");
        }

        final String directory = rootDirectories.get(directoryIndex);
        final String atlasTextureRes = directory + ATLAS_BASE_NAME + atlasIndex;

        final Texture atlasTexture = loadTexture(atlasTextureRes + IMAGE_EXTENSION);
        if (atlasTexture == null) {
            LOGGER.warn("Error loading texture atlas: " + atlasTextureRes);
            return;
        }

        textures.put(atlasTextureRes, atlasTexture);

        final MXParserFactory parserFactory = new MXParserFactory();
        parserFactory.setNamespaceAware(false);
        parserFactory.setValidating(false);
        InputStream xmlStream = null;
        try {
            final XmlPullParser parser = parserFactory.newPullParser();

            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            xmlStream = classLoader.getResourceAsStream(atlasTextureRes + ATLAS_DEF_EXTENSION);
            parser.setInput(xmlStream, "UTF-8");

            int currentEvent = parser.nextTag();
            while (currentEvent != XmlPullParser.END_DOCUMENT) {
                if ((currentEvent == XmlPullParser.START_TAG) && "sprite".equals(parser.getName())) {
                    parseXmlTag(directory, atlasTexture, parser);
                }
                currentEvent = parser.nextTag();
            }

        } catch (@Nonnull final XmlPullParserException e) {
            LOGGER.error("Failed to create a new instance of the pull parser.");
        } catch (@Nonnull final IOException e) {
            LOGGER.error("Error while reading the XML definition of the atlas.");
        } finally {
            closeQuietly(xmlStream);
        }
    }

    private void parseXmlTag(@Nonnull final String directory, @Nonnull final Texture parentTexture,
                             @Nonnull final XmlPullParser parser) {
        final int attributeCount = parser.getAttributeCount();
        if (attributeCount >= 5) {
            int height = 0;
            int width = 0;
            int y = 0;
            int x = 0;
            @Nullable String name = null;
            for (int i = 0; i < attributeCount; i++) {
                @Nonnull final String attribName = parser.getAttributeName(i);
                @Nonnull final String attribValue = parser.getAttributeValue(i);
                if ("x".equals(attribName)) {
                    x = Integer.parseInt(attribValue);
                } else if ("y".equals(attribName)) {
                    y = Integer.parseInt(attribValue);
                } else if ("height".equals(attribName)) {
                    height = Integer.parseInt(attribValue);
                } else if ("width".equals(attribName)) {
                    width = Integer.parseInt(attribValue);
                } else if ("name".equals(attribName)) {
                    name = attribValue;
                }
            }

            if ((height > 0) && (width > 0) && (name != null)) {
                @Nonnull final Texture subTexture = parentTexture.getSubTexture(x, y, width, height);
                textures.put(directory + name, subTexture);
            }

        }
    }

    /**
     * Get the amount of atlas texture files the loader expects to find in a specified directory.
     *
     * @param directoryIndex the index of the directory in the list or root directories
     * @return the amount of atlas textures that will be load from this root directory
     * @throws IndexOutOfBoundsException in case the index value is less then 0 or greater or equal to the amount of
     *                                   texture root directories set
     */
    protected int getAtlasCount(final int directoryIndex) {
        if ((directoryIndex < 0) || (directoryIndex >= rootDirectories.size())) {
            throw new IndexOutOfBoundsException("Directory index is not within valid range");
        }
        if (expectedAtlasCount.get(directoryIndex) >= 0) {
            return expectedAtlasCount.get(directoryIndex);
        }

        final String directory = rootDirectories.get(directoryIndex);

        InputStream in = null;
        int result = 0;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(directory + "atlas.count");
            if (in == null) {
                return 0;
            }

            final DataInputStream dIn = new DataInputStream(in);
            result = dIn.readInt();
        } catch (@Nonnull final IOException e) {
            expectedAtlasCount.set(directoryIndex, 0);
        } finally {
            closeQuietly(in);
        }
        expectedAtlasCount.set(directoryIndex, result);
        return result;
    }

    /**
     * Close a stream no matter what. This function works even with {@code null} arguments. It will never crash, but it
     * will close the closeable in case its possible.
     *
     * @param closeable the object to close
     */
    private static void closeQuietly(@Nullable @WillClose final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (@Nonnull final IOException ignored) {
                // ignore
            }
        }
    }

    @Override
    public float loadRemaining() {
        final int directories = rootDirectories.size();

        int totalAtlasTextures = 0;
        int loadAtlasTextures = 0;
        for (int i = 0; i < directories; i++) {
            totalAtlasTextures += getAtlasCount(i);
            loadAtlasTextures += lastAtlasIndex.get(i) + 1;
        }

        if (totalAtlasTextures <= loadAtlasTextures) {
            return 1.f;
        }

        for (int i = 0; i < directories; i++) {
            if (lastAtlasIndex.get(i) < (getAtlasCount(i) - 1)) {
                if (loadNextTextureAtlas(i)) {
                    return (float) (loadAtlasTextures + 1) / (float) totalAtlasTextures;
                }
                lastAtlasIndex.set(i, getAtlasCount(i) - 1);
                LOGGER.warn("Inconsistent resources. Found less texture atlas files then expected.");
            }
        }

        LOGGER.warn("Reached unexptected loading state.");
        return 1.f;
    }
}
