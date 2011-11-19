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
package illarion.common.graphics;

import illarion.common.util.NoResourceException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Map;

import javolution.text.TextBuilder;
import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.XMLPackedSheet;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;

import de.lessvoid.nifty.slick2d.render.image.ImageSlickRenderImage;
import de.lessvoid.nifty.slick2d.render.image.SlickLoadImageException;
import de.lessvoid.nifty.slick2d.render.image.SlickRenderImage;
import de.lessvoid.nifty.slick2d.render.image.loader.SlickRenderImageLoader;

/**
 * Utility class to load textures. It loads up the Atlas textures and supplies
 * the informations to the sprites that need to render this textures.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class TextureLoader implements SlickRenderImageLoader {
    /**
     * The base name of the atlas files.
     */
    @SuppressWarnings("nls")
    private static final String ATLAS_BASE_NAME = "atlas-";

    /**
     * The file name of the default image.
     */
    @SuppressWarnings("nls")
    private static final String DEFAULT_IMAGE = "data/gui/_default_";

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
    public static TextureLoader getInstance() {
        return INSTANCE;
    }

    /**
     * The index of the last atlas that was load. One array entry for each root
     * directory.
     */
    private final int[] lastAtlasIndex;

    /**
     * The list of all the sheets that got already loaded. One array entry for
     * each root directory. This is done to speed up the searching slightly as
     * there are no textures that contain images of multiple base directories.
     */
    private final Map<String, XMLPackedSheet>[] loadedSheets;

    /**
     * The current state of loading to ensure that the atlas files are loaded up
     * one by one.
     */
    private int loadingState = 0;

    /**
     * The list of known root directories. This list is used to locate
     */
    private final String[] rootDirectories;

    /**
     * Create a new texture loader.
     */
    @SuppressWarnings("unchecked")
    private TextureLoader() {
        rootDirectories =
            new String[] { "data/gui/", "data/chars/", "data/items/",
                "data/tiles/", "data/effects/" };
        Arrays.sort(rootDirectories);

        loadedSheets =
            (Map<String, XMLPackedSheet>[]) Array.newInstance(Map.class,
                rootDirectories.length);
        lastAtlasIndex = new int[rootDirectories.length];
        Arrays.fill(lastAtlasIndex, -1);
    }

    /**
     * Finish the texture loader and clean up.
     */
    public void cleanup() {
        // clean unneeded data
    }

    private int getAtlasCount(final String directory) {
        InputStream in = null;
        int result = 0;
        try {
            in =
                TextureLoader.class.getClassLoader().getResourceAsStream(
                    directory + "atlas.count");
            final BufferedInputStream oIn = new BufferedInputStream(in);
            result = oIn.read();
        } catch (final IOException e) {
            // no texture count file found ... what ever
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                // closing failed, does not matter
            }
        }
        return result;
    }

    private int getDirectoryIndex(final String resourceDir) {
        final int dirIndex = Arrays.binarySearch(rootDirectories, resourceDir);
        if (dirIndex < 0) {
            throw new IllegalArgumentException("Illegal resource directory.");
        }
        return dirIndex;
    }

    private String getResourceDirectory(final String sheetName) {
        for (final String dirName : rootDirectories) {
            if (sheetName.startsWith(dirName)) {
                return dirName;
            }
        }
        return null;
    }

    private Map<String, XMLPackedSheet> getSheetMapForDir(
        final String resourceDir) {
        final int dirIndex = getDirectoryIndex(resourceDir);

        Map<String, XMLPackedSheet> sheets = loadedSheets[dirIndex];
        if (sheets == null) {
            sheets = new FastMap<String, XMLPackedSheet>();
            loadedSheets[dirIndex] = sheets;
        }
        return sheets;
    }

    public static String cleanTextureName(final String name) {
        if (name.endsWith(".png")) {
            return name.substring(0, name.length() - 4);
        }
        return name;
    }

    private String getSheetName(final String resourceDir,
        final String resourceName) {
        final TextBuilder builder = TextBuilder.newInstance();
        builder.append(resourceDir);
        builder.append(resourceName);
        final String result = builder.toString();
        TextBuilder.recycle(builder);
        return result;
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
    public Image getTexture(final String resourceName) {
        final String resourceDir = getResourceDirectory(resourceName);

        if (resourceDir == null) {
            throw new IllegalArgumentException("Illegal Directory: "
                + resourceName);
        }

        return getTexture(resourceDir,
            resourceName.substring(resourceDir.length()));
    }

    /**
     * Get a texture instance pointing on a image upon a texture atlas.
     * 
     * @param resourceDir Directory the resource is located in
     * @param resourceName the name of the resource excluding the directory
     * @return the texture that marks the image.
     */
    @SuppressWarnings("nls")
    public Image getTexture(final String resourceDir,
        final String dirtyResourceName) {

        final String resourceName = cleanTextureName(dirtyResourceName);

        final Map<String, XMLPackedSheet> currSheetMap =
            getSheetMapForDir(resourceDir);

        // Check if there is a packed sheet with the exact name of the resource
        if (currSheetMap.containsKey(resourceName)) {
            return currSheetMap.get(resourceName).getSprite(resourceName);
        }

        // Scan all sheets in the directory and see of the resource is inside
        // one of them.
        Image result = null;
        for (final XMLPackedSheet sheet : currSheetMap.values()) {
            result = sheet.getSprite(resourceName);
            if (result != null) {
                return result;
            }
        }

        // Try to find a sheet with the exact name of this resource
        XMLPackedSheet localSheet =
            loadTextureSheet(resourceDir, resourceName);
        if (localSheet != null) {
            return localSheet.getSprite(resourceName);
        }

        // Now start loading the atlas files
        String atlasName;
        final TextBuilder builder = TextBuilder.newInstance();
        final int dirIndex = getDirectoryIndex(resourceDir);
        while (true) {
            lastAtlasIndex[dirIndex]++;

            atlasName =
                builder.append(ATLAS_BASE_NAME)
                    .append(lastAtlasIndex[dirIndex]).toString();
            localSheet = loadTextureSheet(resourceDir, atlasName);
            if (localSheet == null) {
                lastAtlasIndex[dirIndex]--;
                break;
            }

            result = localSheet.getSprite(resourceName);
            if (result != null) {
                return result;
            }
        }

        LOGGER.error("Unable to load texture: " + resourceDir + resourceName);
        if (DEFAULT_IMAGE.contains(resourceName)) {
            return null;
        }
        return getTexture(DEFAULT_IMAGE);
    }

    public int getTotalAtlasCount() {
        int result = 0;

        for (final String dir : rootDirectories) {
            result += getAtlasCount(dir);
        }
        return result;
    }

    /**
     * Load all atlas files from a directory. This does not actually load the
     * texture. But it generates a task and attaches it to the loading list.
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
    private boolean loadFolderAtlasTextures(final String resourceDir) {
        String atlasName;
        final TextBuilder builder = TextBuilder.newInstance();
        final int dirIndex = getDirectoryIndex(resourceDir);
        lastAtlasIndex[dirIndex]++;

        atlasName =
            builder.append(ATLAS_BASE_NAME).append(lastAtlasIndex[dirIndex])
                .toString();

        try {
            LoadingList.get().add(new DeferredTexture(resourceDir, atlasName));
        } catch (final NoResourceException e) {
            lastAtlasIndex[dirIndex]--;
            return true;
        }

        return false;
    }

    /**
     * This is the task that is attached to the loading list in order to load
     * the data of a texture atlas.
     * 
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private final class DeferredTexture implements DeferredResource {
        /**
         * The directory of the resource to load.
         */
        private final String dir;

        /**
         * The texture to load.
         */
        private final String texture;

        /**
         * Create a new deferred texture loading instance.
         * 
         * @param resDir the directory of the resource to load
         * @param resTex the name of the texture to load
         */
        public DeferredTexture(final String resDir, final String resTex) {
            dir = resDir;
            texture = resTex;

            final URL testRef =
                DeferredTexture.class.getClassLoader().getResource(
                    dir + texture + ".xml");
            if (testRef == null) {
                throw new NoResourceException();
            }
        }

        /**
         * Do the loading now.
         */
        @Override
        public void load() throws IOException {
            if (!getSheetMapForDir(dir).containsKey(texture)) {
                loadTextureSheet(dir, texture);
            }
        }

        /**
         * Get the description of this loading.
         */
        @Override
        public String getDescription() {
            return null;
        }

    }

    protected XMLPackedSheet loadTextureSheet(final String resourceDir,
        final String resourceName) {

        XMLPackedSheet result = null;
        final String sheetName = getSheetName(resourceDir, resourceName);
        if (TextureLoader.class.getClassLoader().getResource(
            sheetName + ".png") == null) {
            return null;
        }
        try {
            result =
                new XMLPackedSheet(sheetName + ".png", sheetName + ".xml");
        } catch (final SlickException e) {
            // resource does not appear to be available
        }
        if (result != null) {
            getSheetMapForDir(resourceDir).put(resourceName, result);
        }
        return result;
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
        if (loadingState >= rootDirectories.length) {
            return true;
        }

        if (loadFolderAtlasTextures(rootDirectories[loadingState])) {
            loadingState++;
        }

        return false;
    }

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
}
