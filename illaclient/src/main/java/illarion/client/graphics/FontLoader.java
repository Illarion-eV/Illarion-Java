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

import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.FontManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;

/**
 * Class to load Fonts for the usage as OpenGL Font.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class FontLoader {
    /**
     * Singleton instance of the FontLoader.
     */
    private static final FontLoader INSTANCE = new FontLoader();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    @SuppressWarnings("UnusedDeclaration")
    private static final Logger LOGGER = LoggerFactory.getLogger(FontLoader.class);

    /**
     * Get instance of singleton.
     *
     * @return the instance of the singleton
     */
    @Nonnull
    public static FontLoader getInstance() {
        return INSTANCE;
    }

    /**
     * Default constructor.
     */
    private FontLoader() {
    }

    /**
     * Load a font, using the name stored in the configuration. The font is loaded from the buffer of the class in
     * case its loaded already. Else its loaded from the resources.
     *
     * @param cfgName the name of the font, this has to be load before hand
     * @return the font itself
     */
    @Nonnull
    public org.illarion.engine.graphic.Font getFont(@Nonnull final String cfgName) {
        if (fontManager == null) {
            throw new IllegalStateException("Fonts not loaded yet");
        }
        final org.illarion.engine.graphic.Font loadedFont = fontManager.getFont(cfgName);
        if (loadedFont == null) {
            throw new IllegalStateException("Something is wrong with the fonts!");
        }
        return loadedFont;
    }

    /**
     * The font manager that is used to load the fonts.
     */
    @Nullable
    private FontManager fontManager;

    /**
     * The key for the menu font.
     */
    public static final String MENU_FONT = "menuFont";

    /**
     * The key for the small font.
     */
    public static final String SMALL_FONT = "smallFont";

    /**
     * The key for the text font.
     */
    public static final String TEXT_FONT = "textFont";

    /**
     * The key for the chat font.
     */
    public static final String CHAT_FONT = "chatFont";

    /**
     * The key for the console font.
     */
    public static final String CONSOLE_FONT = "consoleFont";

    private static final String FONT_IMAGE_DIR = "gui/";

    /**
     * This function loads all fonts that where yet not loaded.
     */
    public void prepareAllFonts(@Nonnull final Assets assets) throws IOException {
        fontManager = assets.getFontManager();
        fontManager
                .createFont(MENU_FONT, "fonts/BlackChancery.ttf", 24.f, Font.PLAIN, "gui/menuFont.fnt", FONT_IMAGE_DIR);
        fontManager.createFont(SMALL_FONT, "fonts/Ubuntu.ttf", 14.f, Font.BOLD, "gui/smallFont.fnt", FONT_IMAGE_DIR);
        fontManager.createFont(TEXT_FONT, "fonts/Ubuntu.ttf", 16.f, Font.PLAIN, "gui/textFont.fnt", FONT_IMAGE_DIR);
        fontManager.createFont(CHAT_FONT, "fonts/LiberationSansNarrow-Bold.ttf", 16.f, Font.PLAIN, "gui/chatFont.fnt",
                               FONT_IMAGE_DIR);
        fontManager.createFont(CONSOLE_FONT, "fonts/Inconsolata.ttf", 14.f, Font.PLAIN, "gui/consoleFont.fnt",
                               FONT_IMAGE_DIR);

        fontManager.setDefaultFont(TEXT_FONT);
    }
}
