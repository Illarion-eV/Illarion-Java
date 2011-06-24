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
package illarion.client.graphics;

import java.awt.Rectangle;

import illarion.client.world.GameFactory;

import illarion.common.util.RecycleObject;

import illarion.graphics.Drawer;
import illarion.graphics.Graphics;
import illarion.graphics.RenderableFont;
import illarion.graphics.SpriteColor;
import illarion.graphics.TextLine;
import illarion.graphics.common.Font;
import illarion.graphics.common.FontLoader;

/**
 * The text tags are the small texts over the heads of characters that display
 * the name of the character.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 */
public class TextTag implements RecycleObject {
    /**
     * The color of the background pane that is displayed behind the text.
     */
    private static final SpriteColor BACK_COLOR = Graphics.getInstance()
        .getSpriteColor();

    /**
     * The instance of the Drawer implementation that is used to draw the
     * rectangle behind the text of this text tag.
     */
    private static final Drawer DRAWER = Graphics.getInstance().getDrawer();

    /**
     * The font that is used to render texts of the text tags.
     */
    private static final RenderableFont TEXT_TAG_FONT;

    private static final Font TEXT_TAG_SOURCEFONT;

    static {
        BACK_COLOR.set(SpriteColor.COLOR_MIN);
        BACK_COLOR.setAlpha(0.3f);
        TEXT_TAG_FONT =
            FontLoader.getInstance().getFont(FontLoader.SMALL_FONT);
        TEXT_TAG_SOURCEFONT = (Font) TEXT_TAG_FONT.getSourceFont();
    }

    /**
     * The rectangle that describes the bounds of the text.
     */
    private Rectangle bounds;

    /**
     * The color implementation that is used to render the text.
     */
    private transient SpriteColor color;

    /**
     * The x coordinate of the offset of this text tag.
     */
    private int dX;

    /**
     * The y coordinate of the offset of this text tag.
     */
    private int dY;

    /**
     * The actual text that is displayed by this tag.
     */
    private String text;

    /**
     * The graphical text line that contains the text itself.
     */
    private transient TextLine textTag;

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
        textTag = Graphics.getInstance().getTextLine();
        textTag.setColor(color);
        textTag.setFont(TEXT_TAG_FONT);
    }

    public void addToCamera(final int x, final int y) {
        if (text == null) {
            return;
        }
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
     * Draw the text tag to the screen.
     * 
     * @param x the x coordinate of the text on the screen
     * @param y the y coordinate of the text on the screen
     */
    public void draw(final int x, final int y) {
        if (text == null) {
            return;
        }

        DRAWER.drawRectangle((x + dX) - 1, (y + dY) - 1, x + dX + bounds.width
            + 2, y + dY + bounds.height, BACK_COLOR);

        color.setAlpha(SpriteColor.COLOR_MAX);
        textTag.setLocation(x + dX, y + dY);
        textTag.render();
    }

    /**
     * Get the height of the text tag.
     * 
     * @return the height of the text tag
     */
    public int getHeight() {
        if (bounds == null) {
            return 0;
        }
        return bounds.height;
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
        if (bounds == null) {
            return 0;
        }
        return bounds.width;
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
        bounds = null;
        textTag = null;
    }

    /**
     * Set the color that is used to display the text tag.
     * 
     * @param newColor the color that is used to render the text tag.
     */
    public void setColor(final Colors newColor) {
        color = newColor.getColor();
        if (textTag != null) {
            textTag.setColor(color);
        }
    }

    /**
     * Set the offset of this text tag.
     * 
     * @param x the x offset of the text tag
     * @param y the y offset of the text tag
     */
    public void setOffset(final int x, final int y) {
        dX = x;
        dY = y;
    }

    /**
     * Set a new text that is shown from now on in the text tag.
     * 
     * @param newText the new text that is displayed from now on
     */
    public void setText(final String newText) {
        text = newText;
        bounds =
            TEXT_TAG_SOURCEFONT.getStringBounds(newText, 0, newText.length());
        textTag.setText(newText);
        textTag.layout();
    }
}
