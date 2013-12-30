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

import illarion.common.types.Rectangle;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Color;
import org.illarion.engine.graphic.Font;
import org.illarion.engine.graphic.Graphics;
import org.illarion.engine.graphic.ImmutableColor;

import javax.annotation.Nonnull;

/**
 * The text tags are the small texts over the heads of characters that display
 * the name of the character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public class TextTag {
    /**
     * The color of the background pane that is displayed behind the text.
     */
    private static final Color BACK_COLOR = new ImmutableColor(0.f, 0.f, 0.f, 0.3f);

    /**
     * The font that is used to render texts of the text tags.
     */
    @Nonnull
    private final Font font;

    /**
     * The color implementation that is used to render the text.
     */
    @Nonnull
    private final Color color;

    /**
     * The x coordinate of the offset of this text tag.
     */
    private int dX;

    /**
     * The y coordinate of the offset of this text tag.
     */
    private int dY;

    /**
     * The x coordinate where the text is supposed to be displayed.
     */
    private int displayX;

    /**
     * The y coordinate where the text is supposed to be displayed.
     */
    private int displayY;

    /**
     * The actual text that is displayed by this tag.
     */
    @Nonnull
    private final String text;

    /**
     * This flag is set {@code true} in case the tag got changed.
     */
    private boolean dirty;

    public TextTag(@Nonnull final String text, @Nonnull final Color color) {
        this.text = text;
        this.color = color;
        font = FontLoader.getInstance().getFont(FontLoader.SMALL_FONT);

        width = font.getWidth(text);
        height = font.getLineHeight();
    }

    public void addToCamera(final int x, final int y) {
        if ((displayX == x) && (displayY == y)) {
            return;
        }

        displayX = x;
        displayY = y;
        dirty = true;
    }

    /**
     * The width of this tag. This value is generated once the text is set.
     */
    private int width;

    /**
     * The height of this tag. This value is generated once the text is set.
     */
    private int height;

    /**
     * Get the height of the text tag.
     *
     * @return the height of the text tag
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the width of the text tag.
     *
     * @return the width of the text tag
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the offset of this text tag.
     *
     * @param x the x offset of the text tag
     * @param y the y offset of the text tag
     */
    public void setOffset(final int x, final int y) {
        if ((dX == x) && (dY == y)) {
            return;
        }

        dX = x;
        dY = y;
        dirty = true;
    }

    public void render(@Nonnull final Graphics g) {
        if (!Camera.getInstance().requiresUpdate(displayRect)) {
            return;
        }

        g.drawRectangle(displayRect, BACK_COLOR);
        g.drawText(font, text, color, displayX - dX, displayY - dY);
    }

    private final Rectangle displayRect = new Rectangle();

    @Nonnull
    public Rectangle getDisplayRect() {
        return displayRect;
    }

    public void update(@Nonnull final GameContainer container, final int delta) {
        if (dirty) {
            dirty = false;
            displayRect.set(displayX - dX - 1, displayY - dY - 1, width + 2, height + 2);
        }
    }
}
