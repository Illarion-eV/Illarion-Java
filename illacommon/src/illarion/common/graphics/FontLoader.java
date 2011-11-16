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

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
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
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class FontLoader implements SlickRenderFontLoader {
    public static enum Fonts {
        menu("menuFont", "BlackChancery", 24.f, "normal"), small("smallFont",
            "Ubuntu", 14.f, "normal"), text("textFont", "Ubuntu", 16.f,
            "normal");

        private final String internalName;
        private final String fontName;
        private final float size;
        private final String style;

        private Fonts(final String name, final String font,
            final float fontSize, final String fontStyle) {
            internalName = name;
            fontName = font;
            size = fontSize;
            style = fontStyle;
        }

        public String getName() {
            return internalName;
        }

        public String getFontName() {
            return fontName;
        }

        public String getFontTTFName() {
            return fontName + ".ttf";
        }

        public String getFontDataName() {
            final TextBuilder builder = TextBuilder.newInstance();
            builder.append(fontName);
            builder.append(File.separator);
            builder.append(size, 0, false, false);
            if (style.equals("italic")) {
                builder.append("-italic");
            } else if (style.equals("bold")) {
                builder.append("-bold");
            }
            builder.append(".illaFont");
            String result = builder.toString();
            TextBuilder.recycle(builder);
            return result;
        }

        public float getFontSize() {
            return size;
        }

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
    private final Map<Fonts, SlickRenderFont> fonts;

    /**
     * Default constructor.
     */
    private FontLoader() {
        fonts = new FastMap<Fonts, SlickRenderFont>(3);
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
    public SlickRenderFont getFont(Fonts font) throws SlickLoadFontException {
        if (font == null) {
            font = Fonts.text;
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
    public SlickRenderFont getFontSave(Fonts font) {
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
    private static Fonts toFontEnum(final String name) {
        for (Fonts font : Fonts.values()) {
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
    private SlickRenderFont loadFont(final Fonts font)
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

            UnicodeFont uniFont = new UnicodeFont(javaFont);
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
