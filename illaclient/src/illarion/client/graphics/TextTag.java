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

import de.lessvoid.nifty.slick2d.render.SlickRenderUtils;
import de.lessvoid.nifty.slick2d.render.font.SlickRenderFont;
import illarion.common.types.Rectangle;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The text tags are the small texts over the heads of characters that display
 * the name of the character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public class TextTag implements Drawable {
    /**
     * The color of the background pane that is displayed behind the text.
     */
    private static final Color BACK_COLOR = new Color(0.f, 0.f, 0.f, 0.3f);

    /**
     * The font that is used to render texts of the text tags.
     */
    @Nullable
    private static final SlickRenderFont TEXT_TAG_FONT = FontLoader.getInstance().getFontSave(FontLoader.Fonts.Small);

    /**
     * This color is used as temporary color during the rendering process.
     */
    private static final de.lessvoid.nifty.tools.Color NIFTY_COLOR = new de.lessvoid.nifty.tools.Color(0.f, 0.f, 0.f,
            0.f);

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

        if (TEXT_TAG_FONT == null) {
            throw new IllegalStateException("Font of the text tag was not loaded.");
        }

        width = TEXT_TAG_FONT.getWidth(text);
        height = TEXT_TAG_FONT.getHeight();
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

    @Override
    public boolean draw(@Nonnull final Graphics g) {
        if (TEXT_TAG_FONT == null) {
            throw new IllegalStateException("Font of the text tag was not loaded.");
        }

        if (!Camera.getInstance().requiresUpdate(displayRect)) {
            return true;
        }

        final Rectangle parentDirtyArea = Camera.getInstance().getDirtyArea(displayRect);
        if (parentDirtyArea != null) {
            g.setWorldClip(parentDirtyArea.getX(), parentDirtyArea.getY(),
                    parentDirtyArea.getWidth(), parentDirtyArea.getHeight());
        }

        g.setColor(BACK_COLOR);
        g.fillRect(displayRect.getX(), displayRect.getY(), displayRect.getWidth(), displayRect.getHeight());
        TEXT_TAG_FONT.renderText(g, text, displayX - dX, displayY - dY,
                SlickRenderUtils.convertColorSlickNifty(color, NIFTY_COLOR), 1.f, 1.f);


        if (parentDirtyArea != null) {
            g.clearWorldClip();
        }

        Camera.getInstance().markAreaRendered(displayRect);

        return true;
    }

    private final Rectangle displayRect = new Rectangle();
    private final Rectangle oldDisplayRect = new Rectangle();

    @Nonnull
    @Override
    public Rectangle getLastDisplayRect() {
        return oldDisplayRect;
    }

    @Override
    public void update(@Nonnull final GameContainer container, final int delta) {
        if (dirty) {
            dirty = false;
            oldDisplayRect.set(displayRect);
            displayRect.set(displayX - dX - 1, displayY - dY - 1, width + 2, height + 2);

            Camera.getInstance().markAreaDirty(oldDisplayRect);
            Camera.getInstance().markAreaDirty(displayRect);
        }
    }
}
