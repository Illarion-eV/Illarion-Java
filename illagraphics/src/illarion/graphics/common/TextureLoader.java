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
package illarion.graphics.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.List;
import java.util.Map;

import javolution.util.FastComparator;
import javolution.util.FastMap;
import javolution.util.FastTable;

import org.apache.log4j.Logger;

import illarion.graphics.Texture;
import illarion.graphics.TextureAtlas;
import illarion.graphics.TextureAtlasListener;

/**
 * Utility class to load textures. It loads up the Atlas textures and supplies
 * the informations to the sprites that need to render this textures.
 * 
 * @author Martin Karing
 * @since 2.00
 * @version 2.00
 */
public final class TextureLoader implements TextureAtlasListener {

    /**
     * The file name of the default image.
     */
    @SuppressWarnings("nls")
    private static final String DEFAULT_IMAGE = "data/gui/_default_";

    /**
     * A empty string.
     */
    @SuppressWarnings("nls")
    private static final String EMPTY_STRING = "".intern();

    /**
     * The singleton instance of this texture loader.
     */
    private static final TextureLoader INSTANCE = new TextureLoader();

    /**
     * In case {@link #loadingState} contains this value the character were
     * loaded last time completely.
     */
    private static final int LOAD_CHARS = 1;

    /**
     * In case {@link #loadingState} contains this value the effects were loaded
     * last time completely.
     */
    private static final int LOAD_EFFECTS = 5;

    /**
     * In case {@link #loadingState} contains this value the GUI were loaded
     * last time completely.
     */
    private static final int LOAD_GUI = 3;

    /**
     * In case {@link #loadingState} contains this value the items were loaded
     * last time completely.
     */
    private static final int LOAD_ITEMS = 2;

    /**
     * In case {@link #loadingState} contains this value the tiles were loaded
     * last time completely.
     */
    private static final int LOAD_TILES = 4;

    /**
     * The error and debug logger of the client.
     */
    private static final Logger LOGGER = Logger.getLogger(TextureLoader.class);

    /**
     * The string each texture file ends with.
     */
    @SuppressWarnings("nls")
    private static final String TEXTURE_FILENAME_ENDING = ".itx";

    /**
     * The current state of loading to ensure that the atlas files are loaded up
     * one by one.
     */
    private int loadingState = 0;

    /**
     * Storage of all loaded textures. This is needed to keep track over the
     * textures atlases that are already loaded.
     */
    private FastMap<String, List<TextureAtlas>> textureAtlases;

    /**
     * The current index of the atlas texture. This is used to improve the speed
     * of texture loading.
     */
    private int textureIndex = 0;

    /**
     * The list of textures available in every folder. This list is used to
     * increase the loading speed.
     */
    private FastMap<String, Map<String, Texture>> textures;

    /**
     * Create a new texture loader.
     */
    private TextureLoader() {
        textureAtlases =
            new FastMap<String, List<TextureAtlas>>()
                .setKeyComparator(FastComparator.STRING);
        textures =
            new FastMap<String, Map<String, Texture>>()
                .setKeyComparator(FastComparator.STRING);
    }

    /**
     * Get the singleton instance of the texture loader.
     * 
     * @return the singleton instance of this class
     */
    public static TextureLoader getInstance() {
        return INSTANCE;
    }

    /**
     * Finish the texture loader and clean up the table that was used to load
     * the textures. This should be done after all data is loaded from the
     * texture loader and it does not any longer need to store references to all
     * textures.
     * <p>
     * Requesting another texture after this was called, will result in a error.
     * </p>
     */
    public void cleanup() {
        List<TextureAtlas> directory;
        for (FastMap.Entry<String, List<TextureAtlas>> e =
            textureAtlases.head(), end = textureAtlases.tail(); (e =
            e.getNext()) != end;) {
            directory = e.getValue();
            for (int i = directory.size() - 1; i >= 0; --i) {
                directory.get(i).finish();
            }
        }
        textureAtlases = null;
        textures = null;
    }

    /**
     * Get a texture instance pointing on a image upon a texture atlas.
     * 
     * @param resourceName the name of the resource
     * @param smooth true in case the textures shall be rendered with expensive
     *            smoothing techniques. Use this for textures that are scaled to
     *            different sizes.
     * @param compress allow the system to compress the image in order to speed
     *            up the application
     * @return the texture that marks the image.
     */
    public Texture getTexture(final String resourceName, final boolean smooth,
        final boolean compress) {
        final int seperator = resourceName.lastIndexOf('/') + 1;

        return getTexture(resourceName.substring(0, seperator),
            resourceName.substring(seperator), smooth, compress, true);
    }

    /**
     * Get a texture instance pointing on a image upon a texture atlas.
     * 
     * @param resourceName the name of the resource
     * @param smooth true in case the textures shall be rendered with expensive
     *            smoothing techniques. Use this for textures that are scaled to
     *            different sizes.
     * @param compress allow the system to compress the image in order to speed
     *            up the application
     * @param discardTexData set that the texture shall store the buffer with
     *            the texture data
     * @return the texture that marks the image.
     */
    public Texture getTexture(final String resourceName, final boolean smooth,
        final boolean compress, final boolean discardTexData) {
        final int seperator = resourceName.lastIndexOf('/') + 1;

        return getTexture(resourceName.substring(0, seperator),
            resourceName.substring(seperator), smooth, compress,
            discardTexData);
    }

    /**
     * Get a texture instance pointing on a image upon a texture atlas.
     * 
     * @param resourceDir Directory the resource is located in
     * @param resourceName the name of the resource excluding the directory
     * @param smooth true in case the textures shall be rendered with expensive
     *            smoothing techniques. Use this for textures that are scaled to
     *            different sizes.
     * @param compress allow the system to compress the image in order to reduce
     *            the hardware requirements
     * @return the texture that marks the image.
     */
    public Texture getTexture(final String resourceDir,
        final String resourceName, final boolean smooth, final boolean compress) {
        return getTexture(resourceDir, resourceName, smooth, compress, true);
    }

    /**
     * Get a texture instance pointing on a image upon a texture atlas.
     * 
     * @param resourceDir Directory the resource is located in
     * @param resourceName the name of the resource excluding the directory
     * @param smooth true in case the textures shall be rendered with expensive
     *            smoothing techniques. Use this for textures that are scaled to
     *            different sizes.
     * @param compress allow the system to compress the image in order to reduce
     *            the hardware requirements
     * @param discardTexData set that the texture shall store the buffer with
     *            the texture data
     * @return the texture that marks the image.
     */
    @SuppressWarnings("nls")
    public Texture getTexture(final String resourceDir,
        final String resourceName, final boolean smooth,
        final boolean compress, final boolean discardTexData) {

        if (textureAtlases == null) {
            throw new IllegalStateException("The texture loader is finished "
                + "and can't provide more textures");
        }
        Texture tex = null;
        String atlasName = null;
        Map<String, Texture> folderTex = textures.get(resourceDir);
        final String texName =
            resourceName.replace(TEXTURE_FILENAME_ENDING, EMPTY_STRING);
        if (folderTex != null) {
            tex = folderTex.get(texName);

            if (tex != null) {
                tex.reportUsed();
                return tex;
            }
        }
        List<TextureAtlas> folderAtlas = textureAtlases.get(resourceDir);

        // not found in one of the atlas files, lets look around a little
        // further.
        InputStream textureInput = null;
        if (!resourceName.endsWith(TEXTURE_FILENAME_ENDING)) {
            textureInput =
                getClass().getClassLoader().getResourceAsStream(
                    resourceDir + resourceName + TEXTURE_FILENAME_ENDING);
        } else {
            textureInput =
                getClass().getClassLoader().getResourceAsStream(
                    resourceDir + resourceName);
        }

        if (textureInput != null) {
            try {
                final TextureAtlas newTexture =
                    TextureIO.readTexture(Channels.newChannel(textureInput));
                newTexture.setFileName(resourceDir + resourceName);
                newTexture.setKeepTextureData(!discardTexData);

                folderAtlas = getTextureAtlasList(resourceDir);
                folderTex = getTextureMap(resourceDir);
                if (folderAtlas == null) {
                    folderAtlas =
                        new FastTable<TextureAtlas>()
                            .setValueComparator(FastComparator.IDENTITY);
                    textureAtlases.put(resourceDir, folderAtlas);

                    folderTex =
                        new FastMap<String, Texture>()
                            .setKeyComparator(FastComparator.STRING);
                    textures.put(resourceDir, folderTex);
                }
                newTexture.activateTexture(smooth, compress);
                newTexture.setListener(this);
                folderAtlas.add(newTexture);
                newTexture.getAllTextures(folderTex);

                return folderTex.get(texName);

            } catch (final IOException e) {
                LOGGER.error("Unable to load texture: " + resourceName, e);
                if (DEFAULT_IMAGE.contains(resourceName)) {
                    return null;
                }
                return getTexture(DEFAULT_IMAGE, false, true);
            }
        }

        int atlasCounter = -1;
        String existingAtlasName;
        while (true) {
            ++atlasCounter;

            atlasName =
                resourceDir + "atlas-" + atlasCounter
                    + TEXTURE_FILENAME_ENDING;
            boolean alreadyUsed = false;

            folderAtlas = getTextureAtlasList(resourceDir);
            folderTex = getTextureMap(resourceDir);
            if (folderAtlas == null) {
                folderAtlas =
                    new FastTable<TextureAtlas>()
                        .setValueComparator(FastComparator.IDENTITY);
                textureAtlases.put(resourceDir, folderAtlas);

                folderTex =
                    new FastMap<String, Texture>()
                        .setKeyComparator(FastComparator.STRING);
                textures.put(resourceDir, folderTex);
            }

            for (int i = folderAtlas.size() - 1; i >= 0; --i) {
                existingAtlasName = folderAtlas.get(i).getFileName();

                if (existingAtlasName.equals(atlasName)) {
                    alreadyUsed = true;
                    break;
                }
            }

            if (alreadyUsed) {
                continue;
            }

            textureInput =
                getClass().getClassLoader().getResourceAsStream(atlasName);

            if (textureInput == null) {
                break;
            }

            try {
                final TextureAtlas newTexture =
                    TextureIO.readTexture(Channels.newChannel(textureInput));
                newTexture.setFileName(atlasName);
                newTexture.setKeepTextureData(!discardTexData);
                newTexture.activateTexture(smooth, compress);
                newTexture.setListener(this);
                folderAtlas.add(newTexture);
                newTexture.getAllTextures(folderTex);

                tex = folderTex.get(texName);

                if (tex != null) {
                    return tex;
                }
            } catch (final IOException e) {
                LOGGER.error("Unable to load texture: " + resourceName, e);
                if (DEFAULT_IMAGE.contains(resourceName)) {
                    return null;
                }
                return getTexture(DEFAULT_IMAGE, false, true);
            }
        }

        LOGGER.error("Unable to load texture: " + resourceName);
        if (DEFAULT_IMAGE.contains(resourceName)) {
            return null;
        }
        return getTexture(DEFAULT_IMAGE, false, true);
    }

    /**
     * Trigger preloading the atlas textures of all folders.
     * 
     * @return false in case there is still something left to load, true in case
     *         everything is loaded up
     */
    public boolean preloadAtlasTextures() {
        return preloadAtlasTextures(true);
    }

    /**
     * Trigger preloading the atlas textures of all folders.
     * 
     * @param discardTexData in case this is set true the texture data is
     *            deleted once the texture is load to OpenGL in order to
     *            preserve memory space
     * @return false in case there is still something left to load, true in case
     *         everything is loaded up
     */
    @SuppressWarnings("nls")
    public boolean preloadAtlasTextures(final boolean discardTexData) {
        ++loadingState;

        switch (loadingState) {
            case LOAD_CHARS:
                // load characters, need to be scaled
                if (!loadFolderAtlasTextures("data/chars/", true, true,
                    discardTexData)) {
                    --loadingState;
                    textureIndex++;
                } else {
                    textureIndex = 0;
                }
                return false;
            case LOAD_ITEMS:
                // items, some of them need scaling as well
                if (!loadFolderAtlasTextures("data/items/", true, true,
                    discardTexData)) {
                    --loadingState;
                    textureIndex++;
                } else {
                    textureIndex = 0;
                }
                return false;
            case LOAD_GUI:
                // the gui, no scaling for this one
                if (!loadFolderAtlasTextures("data/gui/", false, false,
                    discardTexData)) {
                    --loadingState;
                    textureIndex++;
                } else {
                    textureIndex = 0;
                }
                return false;
            case LOAD_TILES:
                // the tiles, no scaling needed at all
                if (!loadFolderAtlasTextures("data/tiles/", false, false,
                    discardTexData)) {
                    --loadingState;
                    textureIndex++;
                } else {
                    textureIndex = 0;
                }
                return false;
            case LOAD_EFFECTS:
                // the effects, no scaling needed at all
                if (!loadFolderAtlasTextures("data/effects/", false, true,
                    discardTexData)) {
                    --loadingState;
                    textureIndex++;
                } else {
                    textureIndex = 0;
                }
                return false;
            default:
                return true;
        }
    }

    /**
     * This function is called in case all references to a texture got removed.
     * When this is done the textures get removed from the system.
     * 
     * @param atlas the texture atlas that is not in use anymore
     */
    @Override
    public void reportDeath(final TextureAtlas atlas) {
        atlas.removeTexture();

        if (textureAtlases == null) {
            return;
        }

        for (FastMap.Entry<String, List<TextureAtlas>> e =
            textureAtlases.head(), end = textureAtlases.tail(); (e =
            e.getNext()) != end;) {

            final List<TextureAtlas> textureStorage = e.getValue();
            final int index = textureStorage.indexOf(atlas);
            if (index > -1) {
                textureStorage.remove(index);
                if (textureStorage.isEmpty()) {
                    textureAtlases.remove(e.getKey());
                }
                return;
            }
        }
    }

    /**
     * Support function that receives the texture atlas list from the
     * {@link #textureAtlases} map. The returned list is supposed to contain all
     * texture atlas objects bound to this directory.
     * 
     * @param folder the directory
     * @return the list of texture atlas objects
     */
    private List<TextureAtlas> getTextureAtlasList(final String folder) {
        List<TextureAtlas> folderAtlas = textureAtlases.get(folder);
        if (folderAtlas == null) {
            folderAtlas =
                new FastTable<TextureAtlas>()
                    .setValueComparator(FastComparator.IDENTITY);
            textureAtlases.put(folder, folderAtlas);
        }
        return folderAtlas;
    }

    /**
     * Support function that receives the map of all textures that are stored at
     * a specified directory. The returned map contains all textures that were
     * load from the texture atlas objects.
     * 
     * @param folder the directory
     * @return the map of texture objects
     */
    private Map<String, Texture> getTextureMap(final String folder) {
        Map<String, Texture> folderTex = textures.get(folder);
        if (folderTex == null) {
            folderTex =
                new FastMap<String, Texture>()
                    .setKeyComparator(FastComparator.STRING);
            textures.put(folder, folderTex);
        }
        return folderTex;
    }

    /**
     * Load all atlas files from a directory.
     * 
     * @param resourceDir the directory the atlas files are searched in
     * @param smooth true in case the atlas files shall be scaled smoothly,
     *            false if not
     * @param compress allow the system to compress the image in order to save
     *            storage space
     * @return false if there is still a atlas left to load, true in case all
     *         are loaded up
     */
    @SuppressWarnings("nls")
    private boolean loadFolderAtlasTextures(final String resourceDir,
        final boolean smooth, final boolean compress,
        final boolean discardTexData) {
        String existingAtlasName;
        String atlasName;
        atlasName =
            resourceDir + "atlas-" + textureIndex + TEXTURE_FILENAME_ENDING;
        boolean alreadyUsed = false;

        final List<TextureAtlas> folderAtlas =
            getTextureAtlasList(resourceDir);
        final Map<String, Texture> folderTex = getTextureMap(resourceDir);

        for (int i = folderAtlas.size() - 1; i >= 0; --i) {
            existingAtlasName = folderAtlas.get(i).getFileName();

            if (existingAtlasName.equals(atlasName)) {
                alreadyUsed = true;
                break;
            }
        }

        if (alreadyUsed) {
            return false;
        }

        try {
            TextureLoader.class.getClassLoader().getResources("data");
        } catch (final IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        final InputStream textureInput =
            TextureLoader.class.getClassLoader()
                .getResourceAsStream(atlasName);

        if (textureInput == null) {
            return true;
        }

        try {
            final TextureAtlas newTexture =
                TextureIO.readTexture(Channels.newChannel(textureInput));
            newTexture.setFileName(atlasName);
            newTexture.setKeepTextureData(!discardTexData);
            newTexture.activateTexture(smooth, compress);
            newTexture.setListener(this);
            folderAtlas.add(newTexture);
            newTexture.getAllTextures(folderTex);
        } catch (final IOException e) {
            LOGGER.error("Unable to load texture: " + atlasName, e);
        }
        return false;
    }
}
