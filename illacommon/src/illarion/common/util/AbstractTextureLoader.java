/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import javolution.text.TextBuilder;
import javolution.util.FastComparator;
import javolution.util.FastMap;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

/**
 * This class contains the abstract implementation of all texture loaders. This implementation does not favor a
 * specified way to load the textures. It just provides the facilities to find and load the texture files that are
 * provided with the resources for all applications.
 *
 * @param <A> the texture atlas type that is used to maintain this class
 * @param <I> the texture type that is used
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
public abstract class AbstractTextureLoader<A extends TextureAtlas<I>, I> {
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
     * The logger that provides the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(AbstractTextureLoader.class);

    /**
     * The index of the last atlas that was load. One array entry for each root directory.
     */
    private final int[] lastAtlasIndex;

    /**
     * This array stores the amount of atlas textures expected for each root directory.
     */
    private final int[] expectedAtlasCount;

    /**
     * The list of all the sheets that got already loaded. One array entry for each root directory. This is done to
     * speed up the searching slightly as there are no textures that contain images of multiple base directories.
     */
    private final Map<String, A>[] loadedSheets;

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
     * The directory where the GUI graphics are stored in the resources.
     */
    protected static final String GUI_DIR = "data/gui/";

    /**
     * The directory where the character graphics are stored in the resources.
     */
    protected static final String CHARS_DIR = "data/chars/";

    /**
     * The directory where the item graphics are stored in the resources.
     */
    protected static final String ITEMS_DIR = "data/items/";

    /**
     * The directory where the tile graphics are stored in the resources.
     */
    protected static final String TILES_DIR = "data/tiles/";

    /**
     * The directory where the effect graphics are stored in the resources.
     */
    protected static final String EFFECTS_DIR = "data/effects/";

    /**
     * Create a new texture loader. This will try loading the textures from all resource directories.
     */
    protected AbstractTextureLoader() {
        this(GUI_DIR, CHARS_DIR, ITEMS_DIR, TILES_DIR, EFFECTS_DIR);
    }

    /**
     * Creates a new texture loader. This will try loading the textures from the specified resource directories.
     *
     * @param directories the directories the loader is supposed to load its textures from
     */
    @SuppressWarnings("unchecked")
    protected AbstractTextureLoader(final String... directories) {
        rootDirectories = Arrays.copyOf(directories, directories.length);
        Arrays.sort(rootDirectories);

        lastAtlasIndex = new int[rootDirectories.length];
        Arrays.fill(lastAtlasIndex, -1);

        expectedAtlasCount = new int[rootDirectories.length];
        Arrays.fill(expectedAtlasCount, -1);

        loadedSheets = (Map<String, A>[]) Array.newInstance(Map.class, getRootDirectoryCount());
    }

    /**
     * Get the amount of root directories that was specified in this loader.
     *
     * @return the amount of root directories that were specified
     */
    protected int getRootDirectoryCount() {
        return rootDirectories.length;
    }

    /**
     * Get the string or the root directory assigned to a specified index.
     *
     * @param directoryIndex the index of the directory
     * @return the path to the directory
     * @throws IndexOutOfBoundsException in case the index value is less then 0 or greater or equal to the amount of
     *                                   texture root directories set
     */
    protected String getRootDirectory(final int directoryIndex) {
        if ((directoryIndex < 0) || (directoryIndex >= rootDirectories.length)) {
            throw new IndexOutOfBoundsException("Directory index is not within valid range");
        }

        return rootDirectories[directoryIndex];
    }

    /**
     * Get the total amount of texture atlas files in all the resource files together.
     *
     * @return the amount of atlas textures in all root directories together
     */
    public int getTotalAtlasCount() {
        int totalCount = 0;
        for (int i = 0; i < rootDirectories.length; i++) {
            totalCount += getAtlasCount(i);
        }
        return totalCount;
    }

    /**
     * Get the total amount of loaded textures in each resource directory.
     *
     * @return the total amount of loaded textures
     */
    public int getTotalLoadedAtlasCount() {
        int totalCount = 0;
        for (int i = 0; i < rootDirectories.length; i++) {
            totalCount += getAtlasLoadedCount(i);
        }
        return totalCount;
    }

    /**
     * Check if all texture atlas resources were loaded.
     *
     * @return {@code true} in case all texture atlas resources were loaded
     */
    public boolean areAllAtlasLoaded() {
        return getTotalAtlasCount() <= getTotalLoadedAtlasCount();
    }

    /**
     * Get the amount of atlas textures already loaded for the specified directory.
     *
     * @param directoryIndex the index of the directory in the list or root directories
     * @return the amount of atlas textures already loaded from this directory
     * @throws IndexOutOfBoundsException in case the index value is less then 0 or greater or equal to the amount of
     *                                   texture root directories set
     */
    protected int getAtlasLoadedCount(final int directoryIndex) {
        if ((directoryIndex < 0) || (directoryIndex >= rootDirectories.length)) {
            throw new IndexOutOfBoundsException("Directory index is not within valid range");
        }

        return lastAtlasIndex[directoryIndex] + 1;
    }

    /**
     * Get the amount of atlas textures already loaded for the specified directory.
     *
     * @param directory the directory to check
     * @return the amount of atlas textures already loaded from this directory
     * @throws NullPointerException     in case the parameter is {@code null}
     * @throws IllegalArgumentException In case the directory selected is not listed as root directory in this loader
     */
    protected int getAtlasLoadedCount(final String directory) {
        return getAtlasLoadedCount(getDirectoryIndex(directory));
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
        if ((directoryIndex < 0) || (directoryIndex >= rootDirectories.length)) {
            throw new IndexOutOfBoundsException("Directory index is not within valid range");
        }
        if (expectedAtlasCount[directoryIndex] >= 0) {
            return expectedAtlasCount[directoryIndex];
        }

        final String directory = rootDirectories[directoryIndex];

        InputStream in = null;
        int result = 0;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(directory + "atlas.count");
            final DataInputStream dIn = new DataInputStream(in);
            result = dIn.readInt();
        } catch (final IOException e) {
            expectedAtlasCount[directoryIndex] = 0;
        } finally {
            closeQuietly(in);
        }
        expectedAtlasCount[directoryIndex] = result;
        return result;
    }

    /**
     * Get the amount of atlas texture files the loader expects to find in a specified directory.
     *
     * @param directory the directory to check
     * @return the amount of atlas textures that will be load from this root directory
     * @throws NullPointerException     in case the parameter is {@code null}
     * @throws IllegalArgumentException In case the directory selected is not listed as root directory in this loader
     */
    protected int getAtlasCount(final String directory) {
        return getAtlasCount(getDirectoryIndex(directory));
    }

    /**
     * Get the index of a root directory.
     *
     * @param resourceDir the root directory
     * @return the index of the root directory
     * @throws NullPointerException     in case the parameter is {@code null}
     * @throws IllegalArgumentException in case the directory searched is not listed as root directory
     */
    private int getDirectoryIndex(final String resourceDir) {
        if (resourceDir == null) {
            throw new NullPointerException("resourceDir must not be null");
        }
        final int dirIndex = Arrays.binarySearch(rootDirectories, resourceDir);
        if (dirIndex < 0) {
            throw new IllegalArgumentException("Directory was not listed as root directory.");
        }
        return dirIndex;
    }

    /**
     * Get the resource root directory the set texture sheet is supposed to be located in.
     *
     * @param sheetName the entire name of the texture sheet, containing also the path
     * @return the index of the root directory or -1 in case the directory couldn't be identified
     * @throws NullPointerException in case the argument is {@code null}
     */
    private int getResourceDirectory(final String sheetName) {
        if (sheetName == null) {
            throw new NullPointerException("sheetName must not be null");
        }
        for (int i = 0; i < rootDirectories.length; i++) {
            if (sheetName.startsWith(rootDirectories[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get the storage map of textures that is assigned to one specific resource directory.
     *
     * @param resourceDirIndex the index of the root directory
     * @return the texture storage of this resource directory
     * @throws IndexOutOfBoundsException in case the index value is less then 0 or greater or equal to the amount of
     *                                   texture root directories set
     */
    private Map<String, A> getSheetMapForDir(final int resourceDirIndex) {
        if ((resourceDirIndex < 0) || (resourceDirIndex >= loadedSheets.length)) {
            throw new IndexOutOfBoundsException("Directory index is not within valid range");
        }

        final Map<String, A> sheets = loadedSheets[resourceDirIndex];
        if (sheets == null) {
            final FastMap<String, A> newSheet = new FastMap<String, A>();
            newSheet.setKeyComparator(FastComparator.STRING);
            loadedSheets[resourceDirIndex] = newSheet;
            return newSheet;
        }
        return sheets;
    }

    /**
     * Get the storage map of textures that is assigned to one specific resource directory.
     *
     * @param resourceDir the root directory
     * @return the texture storage of this resource directory
     * @throws NullPointerException     in case the argument is {@code null}
     * @throws IllegalArgumentException in case the directory searched is not listed as root directory
     */
    private Map<String, A> getSheetMapForDir(final String resourceDir) {
        return getSheetMapForDir(getDirectoryIndex(resourceDir));
    }

    /**
     * This function has to create a texture atlas using the input streams supplied. No additional checks regarding are
     * needed in this function. Only reading the streams. The streams may not be closed in this function.
     *
     * @param image         the reference string to the image file
     * @param xmlDefinition the reference string to the XML file
     * @return the created texture atlas
     */
    protected abstract A createTextureAtlas(String image, String xmlDefinition);

    /**
     * This function tries to find and load a specific texture atlas. In case it works, the function will return the
     * created texture atlas. In case it does not work the function will return {@code null}.
     *
     * @param resourceDirIndex the index of the root directory of this resource
     * @param resource         the name of the resource
     * @return the created texture atlas or {@code null} in case it was impossible to load the texture with this data
     * @throws IndexOutOfBoundsException in case the index value is less then 0 or greater or equal to the amount of
     *                                   texture root directories set
     * @throws NullPointerException      in case the {@code resource} parameter is {@code null}
     */
    private A loadTextureSheet(final int resourceDirIndex, final String resource) {
        final String sheetName = buildSheetName(resourceDirIndex, resource);

        final URL image = Thread.currentThread().getContextClassLoader().getResource(sheetName + ".png");
        final URL xml = Thread.currentThread().getContextClassLoader().getResource(sheetName + ".xml");

        if ((image != null) && (xml != null)) {
            final A textureAtlas = createTextureAtlas(sheetName + ".png", sheetName + ".xml");
            if (textureAtlas == null) {
                LOGGER.error("Resources corrupted! Textures failed to load: " + sheetName);
                return null;
            }
            getSheetMapForDir(resourceDirIndex).put(resource, textureAtlas);
            return textureAtlas;
        }

        return null;
    }

    /**
     * Cleanup the name of the resource by removing any file extensions that might be added by mistake.
     *
     * @param resource the name of the resource
     * @return the cleaned name of the resource
     * @throws NullPointerException in case the resource parameter is {@code null}
     */
    private String cleanResourceName(final String resource) {
        if (resource == null) {
            throw new NullPointerException("resource must not be NULL");
        }
        if (resource.endsWith(".png") || resource.endsWith(".xml")) {
            return resource.substring(0, resource.length() - 4);
        }
        return resource;
    }

    /**
     * Get a texture from the resources.
     *
     * @param resourceDir the root directory of the resource
     * @param resource    the name of the resource itself
     * @return the texture or {@code null} in case there was a problem loading it
     * @throws NullPointerException     in case the at least one of the parameters is {@code null}
     * @throws IllegalArgumentException in case the directory searched is not listed as root directory
     */
    public final I getTexture(final String resourceDir, final String resource) {
        return getTexture(getDirectoryIndex(resourceDir), resource);
    }

    /**
     * Get a texture from the resources.
     *
     * @param resource the name of the resource itself, it has to contain the root directory where the resource is
     *                 located at as well
     * @return the texture or {@code null} in case there was a problem loading it
     * @throws NullPointerException     in case the parameter is {@code null}
     * @throws IllegalArgumentException in case the directory searched is not listed as root directory
     */
    public final I getTexture(final String resource) {
        final int resourceDirIndex = getResourceDirectory(resource);
        final String shortResource = resource.substring(getRootDirectory(resourceDirIndex).length(),
                resource.length());
        return getTexture(resourceDirIndex, shortResource);
    }

    /**
     * Get a texture from the resources.
     *
     * @param resourceDirIndex the index of the root directory
     * @param resource         the name of the resource
     * @return the texture or {@code null} in case there was a problem loading it
     * @throws NullPointerException      in case the resource parameter is {@code null}
     * @throws IndexOutOfBoundsException in case the index value is less then 0 or greater or equal to the amount of
     *                                   texture root directories set
     */
    public final I getTexture(final int resourceDirIndex, final String resource) {
        if ((resourceDirIndex < 0) || (resourceDirIndex >= loadedSheets.length)) {
            throw new IndexOutOfBoundsException("Directory index is not within valid range");
        }
        final String cleanResource = cleanResourceName(resource);

        A textureAtlas = getLoadedTextureAtlas(resourceDirIndex, cleanResource);
        if (textureAtlas != null) {
            return textureAtlas.getTexture(cleanResource);
        }

        textureAtlas = loadTextureSheet(resourceDirIndex, cleanResource);
        if (textureAtlas != null) {
            return textureAtlas.getTexture(cleanResource);
        }

        while (true) {
            textureAtlas = loadNextAtlas(resourceDirIndex);
            if (textureAtlas == null) {
                break;
            }

            final I result = textureAtlas.getTexture(cleanResource);
            if (result != null) {
                return result;
            }
        }

        LOGGER.error("Unable to load texture: " + getRootDirectory(resourceDirIndex) + cleanResource);
        if (DEFAULT_IMAGE.contains(resource)) {
            return null;
        }

        return getTexture(DEFAULT_IMAGE);
    }

    /**
     * Load the next texture atlas. This function won't do anything in case {@link #areAllAtlasLoaded()} returns
     * {@code true}.
     */
    public void loadNextAtlas() {
        for (int i = 0; i < rootDirectories.length; i++) {
            final A result = loadNextAtlas(i);
            if (result != null) {
                return;
            }
        }
    }

    /**
     * Load the next texture atlas inside the specified resource directory.
     *
     * @param resourceDirIndex the index of the root directory
     * @return the texture loaded or {@code null} in case there are no more texture atlas files to load
     * @throws IndexOutOfBoundsException in case the index value is less then 0 or greater or equal to the amount of
     *                                   texture root directories set
     */
    private A loadNextAtlas(final int resourceDirIndex) {
        if ((resourceDirIndex < 0) || (resourceDirIndex >= loadedSheets.length)) {
            throw new IndexOutOfBoundsException("Directory index is not within valid range");
        }

        if (getAtlasCount(resourceDirIndex) == getAtlasLoadedCount(resourceDirIndex)) {
            return null;
        }

        lastAtlasIndex[resourceDirIndex]++;

        final A textureAtlas = loadTextureSheet(resourceDirIndex,
                createAtlasResourceName(lastAtlasIndex[resourceDirIndex]));
        if (textureAtlas == null) {
            LOGGER.error("Corrupted resources detected. Not enough texture files in: " +
                    getRootDirectory(resourceDirIndex));
            lastAtlasIndex[resourceDirIndex]--;
        }
        return textureAtlas;
    }

    /**
     * Generate the name of the texture atlas resource to load.
     *
     * @param index the index of the atlas texture
     * @return the name of the atlas resource
     */
    private static String createAtlasResourceName(final int index) {
        TextBuilder builder = null;
        try {
            builder = TextBuilder.newInstance();
            builder.append(ATLAS_BASE_NAME);
            builder.append(index);
            return builder.toString();
        } finally {
            if (builder != null) {
                TextBuilder.recycle(builder);
            }
        }
    }

    /**
     * Close a stream no matter what. This function works even with {@code null} arguments. It will never crash, but it
     * will close the closeable in case its possible.
     *
     * @param closeable the object to close
     */
    private static void closeQuietly(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (final IOException ignored) {
                // ignore
            }
        }
    }

    /**
     * Generate the name of the sheet containing the directory and the name of the resource itself.
     *
     * @param resourceDirIndex the index of the root directory
     * @param resource         the name of the resource
     * @return the name of the texture sheet
     * @throws IndexOutOfBoundsException in case the index value is less then 0 or greater or equal to the amount of
     *                                   texture root directories set
     * @throws NullPointerException      in case the {@code resource} parameter is {@code null}
     */
    private String buildSheetName(final int resourceDirIndex, final String resource) {
        if (resource == null) {
            throw new NullPointerException("resource may not be NULL");
        }

        TextBuilder builder = null;
        try {
            builder = TextBuilder.newInstance();
            builder.append(getRootDirectory(resourceDirIndex));
            builder.append(resource);
            return builder.toString();
        } finally {
            if (builder != null) {
                TextBuilder.recycle(builder);
            }
        }
    }

    /**
     * Check if there is a texture atlas that contains the searched texture. In case the matching texture atlas is
     * found, it will be returned by this function.
     *
     * @param resourceDirIndex the index of the resource directory
     * @param resource         the resource name
     * @return the texture atlas or {@code null} in case none was found
     * @throws IndexOutOfBoundsException in case the index value is less then 0 or greater or equal to the amount of
     *                                   texture root directories set
     */
    protected final A getLoadedTextureAtlas(final int resourceDirIndex, final String resource) {
        if ((resourceDirIndex < 0) || (resourceDirIndex >= loadedSheets.length)) {
            throw new IndexOutOfBoundsException("Directory index is not within valid range");
        }
        final Map<String, A> sheets = getSheetMapForDir(resourceDirIndex);
        if (sheets.containsKey(resource)) {
            return sheets.get(resource);
        }

        for (final A atlas : sheets.values()) {
            if (atlas.containsTexture(resource)) {
                return atlas;
            }
        }

        return null;
    }

    /**
     * Check if there is a texture atlas that contains the searched texture. In case the matching texture atlas is
     * found, it will be returned by this function.
     *
     * @param resourceDir the root directory
     * @param resource    the resource name
     * @return the texture atlas or {@code null} in case none was found
     * @throws NullPointerException     in case the argument is {@code null}
     * @throws IllegalArgumentException in case the directory searched is not listed as root directory
     */
    protected final A getLoadedTextureAtlas(final String resourceDir, final String resource) {
        return getLoadedTextureAtlas(getDirectoryIndex(resourceDir), resource);
    }
}
