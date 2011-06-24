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

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

import javolution.util.FastComparator;
import javolution.util.FastMap;

import org.apache.log4j.Logger;

import illarion.common.util.NoResourceException;

import illarion.graphics.Graphics;
import illarion.graphics.RenderableFont;

/**
 * Class to load Fonts for the usage as OpenGL Font.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class FontLoader {

    /**
     * The font name of the menu font.
     */
    @SuppressWarnings("nls")
    public static final String MENU_FONT = "menuFont";

    /**
     * The font name of the small font.
     */
    @SuppressWarnings("nls")
    public static final String SMALL_FONT = "smallFont";

    /**
     * The font name of the text font.
     */
    @SuppressWarnings("nls")
    public static final String TEXT_FONT = "textFont";

    /**
     * The root directory where the fonts are located.
     */
    @SuppressWarnings("nls")
    private static final String FONT_ROOT = "data/gui/";

    /**
     * Singleton instance of the FontLoader.
     */
    private static final FontLoader INSTANCE = new FontLoader();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(FontLoader.class);

    /**
     * Storage of the loaded GL Fonts.
     */
    private final Map<String, RenderableFont> fonts;

    /**
     * Default constructor.
     */
    private FontLoader() {
        final FastMap<String, RenderableFont> fontTable =
            new FastMap<String, RenderableFont>(3);
        fontTable.setKeyComparator(FastComparator.STRING);
        fontTable.put(MENU_FONT, loadFont(MENU_FONT));
        fontTable.put(SMALL_FONT, loadFont(SMALL_FONT));
        fontTable.put(TEXT_FONT, loadFont(TEXT_FONT));
        fonts = fontTable;
    }

    /**
     * Get instance of singleton.
     * 
     * @return the instance of the singleton
     */
    public static FontLoader getInstance() {
        return INSTANCE;
    }

    /**
     * Load a font, using the name stored in the configuration. The font is
     * loaded from the buffer of the class in case its loaded already. Else its
     * loaded from the resources.
     * 
     * @param cfgName the name of the config entry that holds the actual name of
     *            the font
     * @return the font itself
     */
    public RenderableFont getFont(final String cfgName) {
        RenderableFont font = fonts.get(cfgName);
        if (font == null) {
            font = loadFont(cfgName);
            fonts.put(cfgName, font);
        }

        return font;
    }

    /**
     * Load a font from the resources.
     * 
     * @param cfgName the name of the font
     * @return the font itself
     */
    @SuppressWarnings("nls")
    private RenderableFont loadFont(final String cfgName) {
        ObjectInputStream ois = null;
        Font font = null;
        try {
            ois =
                new ObjectInputStream(new BufferedInputStream(FontLoader.class
                    .getClassLoader().getResourceAsStream(
                        FONT_ROOT + cfgName + ".illaFont")));

            font = (Font) ois.readObject();
        } catch (final FileNotFoundException ex) {
            LOGGER.error("Can't find font file: " + cfgName, ex);
        } catch (final IOException ex) {
            LOGGER.error("Failed reading font file: " + cfgName, ex);
        } catch (final ClassNotFoundException ex) {
            LOGGER.error("Font file invalid: " + cfgName, ex);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (final IOException ex) {
                    LOGGER
                        .error("Failed to close input stream for font loading");
                }
            }
        }

        if (font == null) {
            return null;
        }

        try {
            font.prepareTextures(FONT_ROOT);
        } catch (final Exception e) {
            // Problem while preparing the textures.
            throw new NoResourceException("Error while loading font", e);
        }

        final RenderableFont loadedText = Graphics.getInstance().getFont(font);
        return loadedText;
    }
}
