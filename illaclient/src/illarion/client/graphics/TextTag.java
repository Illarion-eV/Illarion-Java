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
import illarion.client.world.GameFactory;
import illarion.common.annotation.NonNull;
import illarion.common.types.Rectangle;
import illarion.common.util.RecycleObject;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * The text tags are the small texts over the heads of characters that display
 * the name of the character.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public class TextTag implements Drawable, RecycleObject {
    /**
     * The color of the background pane that is displayed behind the text.
     */
    private static final Color BACK_COLOR = new Color(0.f, 0.f, 0.f, 0.3f);

    /**
     * The font that is used to render texts of the text tags.
     */
    private static final SlickRenderFont TEXT_TAG_FONT = FontLoader
            .getInstance().getFontSave(FontLoader.Fonts.small);

    /**
     * This color is used as temporary color during the rendering process.
     */
    private static final de.lessvoid.nifty.tools.Color NIFTY_COLOR =
            new de.lessvoid.nifty.tools.Color(0.f, 0.f, 0.f, 0.f);

    /**
     * The color implementation that is used to render the text.
     */
    private transient Color color;

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
    private String text;

    /**
     * This flag is set {@code true} in case the tag got changed.
     */
    private boolean dirty;

    /**
     * Create a new instance of the text tag. This instance is created by the
     * game factory. Its better to create the instance using this because in
     * this case it gets activated properly.
     *
     * @return the new text tag instance created by the game factory
     */
    public static TextTag create() {
        return (TextTag) GameFactory.getInstance().getCommand(
                GameFactory.OBJ_TAG);
    }

    /**
     * Activate the text tag and prepare the values of this tag.
     *
     * @param id the ID the tag got activated with
     */
    @Override
    public void activate(final int id) {
        // nothing to do
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
     * Create a duplicate of this text tag. That does not copy the actual
     * content, it just creates a new instance of this class.
     */
    @Override
    public TextTag clone() {
        return new TextTag();
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
     * Get the object ID of this class that was used to create this text tag in
     * the game factory.
     */
    @Override
    public int getId() {
        return GameFactory.OBJ_TAG;
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
     * Put the instance of the text tag back into the recycler for using again
     * later. After this function was called the text tag should not be used or
     * rendered anymore.
     */
    @Override
    public void recycle() {
        GameFactory.getInstance().recycle(this);
    }

    /**
     * Clean up the text tag before put it back into the recycle factory.
     */
    @Override
    public void reset() {
        text = null;
        color = null;
    }

    /**
     * Set the color that is used to display the text tag.
     *
     * @param newColor the color that is used to render the text tag.
     */
    public void setColor(final Color newColor) {
        if ((newColor == null) || newColor.equals(color)) {
            return;
        }

        color = newColor;
        dirty = true;
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

    /**
     * Set a new text that is shown from now on in the text tag.
     *
     * @param newText the new text that is displayed from now on
     */
    public void setText(final String newText) {
        if (newText.equals(text)) {
            return;
        }

        text = newText;
        width = TEXT_TAG_FONT.getWidth(newText);
        height = TEXT_TAG_FONT.getHeight();

        dirty = true;
    }

    @Override
    public boolean draw(@NonNull final Graphics g) {
        if (text == null) {
            return true;
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

    @NonNull
    @Override
    public Rectangle getLastDisplayRect() {
        return oldDisplayRect;
    }

    @Override
    public void update(@NonNull final GameContainer container, final int delta) {
        if (dirty) {
            dirty = false;
            oldDisplayRect.set(displayRect);
            displayRect.set(displayX - dX - 1, displayY - dY - 1, width + 2, height + 2);

            Camera.getInstance().markAreaDirty(oldDisplayRect);
            Camera.getInstance().markAreaDirty(displayRect);
        }
    }
}
