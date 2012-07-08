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
package illarion.client.graphics;

import java.awt.Font;
import java.io.File;
import java.util.EnumMap;
import java.util.Map;

import javolution.text.TextBuilder;
import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.util.ResourceLoader;

import de.lessvoid.nifty.slick2d.render.font.SlickLoadFontException;
import de.lessvoid.nifty.slick2d.render.font.SlickRenderFont;
import de.lessvoid.nifty.slick2d.render.font.UnicodeSlickRenderFont;
import de.lessvoid.nifty.slick2d.render.font.loader.SlickRenderFontLoader;

/**
 * Class to load Fonts for the usage as OpenGL Font.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class FontLoader implements SlickRenderFontLoader {
    /**
     * The enumerator of available fonts.
     */
    public enum Fonts {
        /**
         * Menu font - a large font with fancy characters.
         */
        menu("menuFont", "BlackChancery", 24.f, "normal"),

        /**
         * Small font - a small font that is easily readable.
         */
        small("smallFont", "Ubuntu", 14.f, "normal"),

        /**
         * Text font - the default font for text, larger then the small font but also easily readable.
         */
        text("textFont", "Ubuntu", 16.f, "normal"),

        /**
         * Console font - mono-spaced font suiting console output.
         */
        console("consoleFont", "Inconsolata", 12.f, "normal");

        /**
         * The internal name of the font.
         */
        private final String internalName;
        
        /**
         * The name of the font fitting the filename of the file that stores this font.
         */
        private final String fontName;

        /**
         * The size the font should be rendered as.
         */
        private final float size;

        /**
         * The style of the font.
         */
        private final String style;

        /**
         * Default constructor for font definitions.
         * 
         * @param name the internal name of the font
         * @param font the name of the font
         * @param fontSize the size of the font
         * @param fontStyle the style of the font
         */
        Fonts(final String name, final String font, final float fontSize, final String fontStyle) {
            internalName = name;
            fontName = font;
            size = fontSize;
            style = fontStyle;
        }

        /**
         * Get the name of internal usage of this font.
         * 
         * @return the name of the font
         */
        public String getName() {
            return internalName;
        }

        /**
         * Get the real name of the font.
         * 
         * @return the real font name
         */
        public String getFontName() {
            return fontName;
        }

        /**
         * Get the name of the TTF-font file of this font.
         * 
         * @return the name of the TTF-font file
         */
        public String getFontTTFName() {
            return getFontName() + ".ttf";
        }

        /**
         * Get the size of the font.
         * 
         * @return the size of the font
         */
        public float getFontSize() {
            return size;
        }

        /**
         * Get the style of the font.
         * 
         * @return the font style
         */
        public String getFontStyle() {
            return style;
        }
    }

    /**
     * The root directory where the fonts are located.
     */
    @SuppressWarnings("nls")
    private static final String FONT_ROOT = "data/fonts/";

    /**
     * Singleton instance of the FontLoader.
     */
    private static final FontLoader INSTANCE = new FontLoader();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(FontLoader.class);

    /**
     * Get instance of singleton.
     * 
     * @return the instance of the singleton
     */
    public static FontLoader getInstance() {
        return INSTANCE;
    }

    /**
     * Storage of the loaded GL Fonts.
     */
    private final Map<FontLoader.Fonts, SlickRenderFont> fonts;

    /**
     * Default constructor.
     */
    private FontLoader() {
        fonts = new EnumMap<FontLoader.Fonts, SlickRenderFont>(FontLoader.Fonts.class);
    }

    /**
     * Load a font, using the name stored in the configuration. The font is
     * loaded from the buffer of the class in case its loaded already. Else its
     * loaded from the resources.
     * 
     * @param cfgName the name of the config entry that holds the actual name of
     *            the font
     * @return the font itself
     * @throws SlickLoadFontException in case loading the font fails
     */
    public SlickRenderFont getFont(final String cfgName)
        throws SlickLoadFontException {
        return getFont(toFontEnum(cfgName));
    }

    /**
     * Load a font, using the name stored in the configuration. The font is
     * loaded from the buffer of the class in case its loaded already. Else its
     * loaded from the resources.
     * 
     * @param font the font to load
     * @return the font itself
     * @throws SlickLoadFontException in case loading the font fails
     */
    public SlickRenderFont getFont(FontLoader.Fonts font) throws SlickLoadFontException {
        if (font == null) {
            font = FontLoader.Fonts.text;
        }
        SlickRenderFont renderableFont = fonts.get(font);
        if (renderableFont == null) {
            renderableFont = loadFont(font);
            fonts.put(font, renderableFont);
        }

        return renderableFont;
    }
    
    /**
     * This function receives a slick render font or NULL in case loading the
     * font fails.
     * 
     * @param font the requested font
     * @return the loaded font
     */
    public SlickRenderFont getFontSave(final FontLoader.Fonts font) {
        try {
            return getFont(font);
        } catch (final SlickLoadFontException e) {
            return null;
        }
    }

    /**
     * This function transforms a name of a font into the fitting enumerator.
     * 
     * @param name the name of the font
     * @return the fitting enumerator entry or <code>null</code> in case no
     *         fitting entry was found
     */
    private static FontLoader.Fonts toFontEnum(final String name) {
        for (FontLoader.Fonts font : FontLoader.Fonts.values()) {
            if (font.getName().equals(name)) {
                return font;
            }
        }
        return null;
    }

    /**
     * Load a font from the resources.
     * 
     * @param font the name of the font
     * @return the font itself
     * @throws SlickLoadFontException in case loading the font fails
     */
    @SuppressWarnings("nls")
    private SlickRenderFont loadFont(final FontLoader.Fonts font)
        throws SlickLoadFontException {
        try {
            Font javaFont =
                Font.createFont(
                    Font.TRUETYPE_FONT,
                    ResourceLoader.getResourceAsStream(FONT_ROOT
                        + font.getFontTTFName()));

            if (font.getFontStyle().equals("normal")) {
                javaFont = javaFont.deriveFont(Font.PLAIN, font.getFontSize());
            } else if (font.getFontStyle().equals("italic")) {
                javaFont =
                    javaFont.deriveFont(Font.ITALIC, font.getFontSize());
            } else if (font.getFontStyle().equals("bold")) {
                javaFont = javaFont.deriveFont(Font.BOLD, font.getFontSize());
            }

            final UnicodeFont uniFont = new UnicodeFont(javaFont);
            uniFont.addAsciiGlyphs();
            uniFont.addGlyphs("•");
            uniFont.getEffects().add(new ColorEffect());
            return new UnicodeSlickRenderFont(uniFont, javaFont);
        } catch (Exception e) {
            throw new SlickLoadFontException(e);
        }
    }

    @Override
    public SlickRenderFont loadFont(final Graphics g, final String filename)
        throws SlickLoadFontException {
        return getFont(filename);
    }
}
