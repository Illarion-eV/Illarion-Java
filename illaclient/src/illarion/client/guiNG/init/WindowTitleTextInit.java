/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.guiNG.init;

import illarion.client.guiNG.elements.Text;
import illarion.client.guiNG.elements.Widget;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;
import illarion.graphics.common.FontLoader;

/**
 * This initialization script takes care for loading the font and the color for
 * the title text of a window.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class WindowTitleTextInit implements WidgetInit {
    /**
     * The instance used used for all requested instances of this class.
     */
    private static WindowTitleTextInit instance = null;

    /**
     * The serialization UID this initialization script.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The private constructor to avoid instances created uncontrolled.
     */
    private WindowTitleTextInit() {
        // private constructor to avoid instances created uncontrolled.
    }

    /**
     * Get a new instance of this initialization script. This either creates a
     * new instance of this class or returns always the same, depending on what
     * is needed for this script.
     * 
     * @return the instance of this initialization script that is to be used
     *         from now on
     */
    public static WindowTitleTextInit getInstance() {
        if (instance == null) {
            instance = new WindowTitleTextInit();
        }
        return instance;
    }

    /**
     * Load the horizontal border.
     */
    @Override
    @SuppressWarnings("nls")
    public void initWidget(final Widget widget) {
        if (!(widget instanceof Text)) {
            throw new IllegalArgumentException(
                "Init Class requires a Text widget");
        }
        final Text text = (Text) widget;
        text.setFont(FontLoader.getInstance().getFont(FontLoader.SMALL_FONT));
        final SpriteColor color = Graphics.getInstance().getSpriteColor();
        color.set(221, 198, 135);
        text.setColor(color);
    }
}
