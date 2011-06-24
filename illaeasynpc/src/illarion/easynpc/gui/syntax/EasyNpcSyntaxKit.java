/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.gui.syntax;

import java.awt.Color;
import java.io.IOException;
import java.util.Properties;

import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.util.Configuration;

import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

/**
 * This is the Syntax Kit used to handle the syntax highlighting of the easyNPC
 * language in the script editor.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.22
 */
public final class EasyNpcSyntaxKit extends DefaultSyntaxKit {
    /**
     * The serialization UID of this easyNPC Syntax Kit.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The configuration file of this syntax kit.
     */
    private final Configuration config;

    /**
     * The default constructor that prepares the {@link jsyntaxpane.Lexer}
     * needed for this syntax kit to work.
     */
    @SuppressWarnings({ "nls", "boxing" })
    public EasyNpcSyntaxKit() {
        super(new EasyNpcLexer());

        config = new Configuration(EasyNpcSyntaxKit.class);
        final Properties props = new Properties();
        try {
            props.load(EasyNpcSyntaxKit.class.getClassLoader()
                .getResourceAsStream(
                    "illarion/easynpc/gui/syntax/easyNpc.properties"));
            final SubstanceColorScheme scheme =
                SubstanceLookAndFeel.getCurrentSkin()
                    .getBackgroundColorScheme(DecorationAreaType.GENERAL);
            final Color background = scheme.getBackgroundFillColor();
            final Color foreground = scheme.getForegroundColor();
            Color comment = Color.GREEN;
            if (!scheme.isDark()) {
                comment = comment.darker().darker();
            } else {
                comment = comment.brighter().brighter();
            }

            Color string = Color.RED;
            if (!scheme.isDark()) {
                string = string.darker().darker();
            } else {
                string = string.brighter().brighter();
            }

            final String colorFormatString = "0x%1$02x%2$02x%3$02x";
            props.put("LineNumbers.Background", String.format(
                colorFormatString, background.getRed(), background.getGreen(),
                background.getBlue()));
            props.put("LineNumbers.Foreground", String.format(
                colorFormatString, foreground.getRed(), foreground.getGreen(),
                foreground.getBlue()));

            props.put(
                "Style.COMMENT",
                String.format(colorFormatString, comment.getRed(),
                    comment.getGreen(), comment.getBlue())
                    + ", 2");
            props.put(
                "Style.KEYWORD",
                String.format(colorFormatString, foreground.getRed(),
                    foreground.getGreen(), foreground.getBlue()) + ", 1");
            props.put(
                "Style.STRING",
                String.format(colorFormatString, string.getRed(),
                    string.getGreen(), string.getBlue())
                    + ", 0");
            props.put(CONFIG_TOOLBAR_BORDER, Boolean.FALSE.toString());
            props.put("RightMarginColumn", Integer.toString(0));
        } catch (final IOException e) {
            System.err
                .println("Failed to load needed property file for syntax highlighting.");
        }
        config.putAll(props);
    }

    @Override
    public Configuration getConfig() {
        return config;
    }
}
