/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.client.graphics;

import org.illarion.engine.assets.Assets;
import org.illarion.engine.assets.FontManager;
import org.illarion.engine.graphic.Font;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Class to load Fonts for the usage as OpenGL Font.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class FontLoader {
    /**
     * The font that is supposed to be used for chat bubbles.
     */
    @Nonnull
    public static final String BUBBLE_FONT = "bubbleFont";
    /**
     * The font that is supposed to be used as the out line of the font for chat bubbles.
     */
    @Nonnull
    public static final String BUBBLE_FONT_OUTLINE = "bubbleFontOutline";
    /**
     * The key for the menu font.
     */
    @Nonnull
    public static final String MENU_FONT = "menuFont";
    /**
     * The key for the caption font.
     */
    @Nonnull
    public static final String CAPTION_FONT = "captionFont";
    /**
     * The key for the small font.
     */
    public static final String SMALL_FONT = "smallFont";
    /**
     * The key for the text font.
     */
    @Nonnull
    public static final String TEXT_FONT = "textFont";
    /**
     * The key for the chat font.
     */
    @Nonnull
    public static final String CHAT_FONT = "chatFont";
    /**
     * The key for the console font.
     */
    @Nonnull
    public static final String CONSOLE_FONT = "consoleFont";
    /**
     * Singleton instance of the FontLoader.
     */
    @Nonnull
    private static final FontLoader INSTANCE = new FontLoader();
    @Nonnull
    private static final String FONT_IMAGE_DIR = "gui/";
    /**
     * The font manager that is used to load the fonts.
     */
    @Nullable
    private FontManager fontManager;

    /**
     * Default constructor.
     */
    private FontLoader() {
    }

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
     * Load a font, using the name stored in the configuration. The font is loaded from the buffer of the class in case
     * its loaded already. Else its loaded from the resources.
     *
     * @param cfgName the name of the font, this has to be load before hand
     * @return the font itself
     */
    @Nonnull
    public Font getFont(@Nonnull String cfgName) {
        if (fontManager == null) {
            throw new IllegalStateException("Fonts not loaded yet");
        }
        Font loadedFont = fontManager.getFont(cfgName);
        if (loadedFont == null) {
            throw new IllegalStateException("Something is wrong with the fonts!");
        }
        return loadedFont;
    }

    /**
     * This function loads all fonts that where yet not loaded.
     */
    public void prepareAllFonts(@Nonnull Assets assets) throws IOException {
        fontManager = assets.getFontManager();
        Font outline = fontManager.createFont(BUBBLE_FONT_OUTLINE, "gui/bubbleFontOutline.fnt", FONT_IMAGE_DIR);
        fontManager.createFont(BUBBLE_FONT, "gui/bubbleFont.fnt", FONT_IMAGE_DIR, outline);
        fontManager.createFont(MENU_FONT, "gui/menuFont.fnt", FONT_IMAGE_DIR);
        fontManager.createFont(CAPTION_FONT, "gui/captionFont.fnt", FONT_IMAGE_DIR);
        fontManager.createFont(SMALL_FONT, "gui/smallFont.fnt", FONT_IMAGE_DIR);
        fontManager.createFont(TEXT_FONT, "gui/textFont.fnt", FONT_IMAGE_DIR);
        fontManager.createFont(CHAT_FONT, "gui/chatFont.fnt", FONT_IMAGE_DIR);
        fontManager.createFont(CONSOLE_FONT, "gui/consoleFont.fnt", FONT_IMAGE_DIR);

        fontManager.setDefaultFont(TEXT_FONT);
    }
}
