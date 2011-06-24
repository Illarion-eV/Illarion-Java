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
package illarion.client.guiNG.elements;

import illarion.graphics.Graphics;
import illarion.graphics.RenderableFont;
import illarion.graphics.SpriteColor;
import illarion.graphics.TextLine;
import illarion.graphics.common.Font;

/**
 * A widget that shows one line of text. It does not support line breaks. But it
 * is able to align the one line of text in a area.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class Text extends Widget {
    /**
     * The constant for aligning the text to the center.
     */
    public static final int ALIGN_CENTER = 2;

    /**
     * The constant for aligning the text to the left.
     */
    public static final int ALIGN_LEFT = 0;

    /**
     * The constant for aligning the text to the right.
     */
    public static final int ALIGN_RIGHT = 1;

    /**
     * Time in milliseconds for toggling the state of the cursor.
     */
    private static final int CURSOR_TOGGLE_TIME = 500;

    /**
     * Current serialization UID of the text class.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The currently set align value that is used to display the text.
     */
    private int align = ALIGN_LEFT;

    /**
     * The time in milliseconds since the last toggle.
     */
    private transient int cursorDelta;

    /**
     * The position where the cursor is displayed.
     */
    private transient int cursorPos;

    /**
     * The font that is used to display the text.
     */
    private transient RenderableFont font;

    /**
     * The text line implementation that is used to render the text.
     */
    private transient TextLine graphicalText;

    /**
     * The offset of the text placement that is calculated by the align.
     */
    private transient int offsetX = 0;

    /**
     * The source font object that is needed to calculate some values.
     */
    private transient Font sourceFont;

    /**
     * The text that widget shall display.
     */
    private String text;

    /**
     * Create a instance of this text widget and prepare the required values to
     * be set.
     */
    public Text() {
        super();
        reset();
    }

    /**
     * Draw the text.
     */
    @Override
    public void draw(final int delta) {
        if (!isVisible()) {
            return;
        }
        if (text != null) {
            if (cursorPos > -1) {
                cursorDelta += delta;
                if (cursorDelta >= CURSOR_TOGGLE_TIME) {
                    graphicalText.toogleCursor();
                    cursorDelta %= CURSOR_TOGGLE_TIME;
                }
            }

            graphicalText.setLocation(getRelX() + offsetX, getRelY());
            graphicalText.render();
        }
        super.draw(delta);
    }

    /**
     * Get the current position of the cursor.
     * 
     * @return the current cursor position or -1 in case the cursor is disabled
     */
    public int getCursorPos() {
        return cursorPos;
    }

    /**
     * Get the text that is currently displayed by this widget.
     * 
     * @return the string that is displayed by this widget or <code>null</code>
     *         in case none is set
     */
    public String getText() {
        return text;
    }

    /**
     * Prepare the default values of the widget.
     */
    @Override
    public void initWidget() {
        reset();
        super.initWidget();
        updateOffset();
    }

    /**
     * Set the align of the text within the area of this widget.
     * 
     * @param newAlign the new text align
     */
    public void setAlign(final int newAlign) {
        align = newAlign;
        updateOffset();
    }

    /**
     * Set the color that is used to render this text. This function does not
     * copy the color into a local value. It just stores the object reference.
     * So if the color object is changed else where it will effect the color of
     * this text.
     * 
     * @param newColor the new color that is used to render this text.
     */
    public void setColor(final SpriteColor newColor) {
        graphicalText.setColor(newColor);
    }

    /**
     * Set the new location of the cursor.
     * 
     * @param newPos the new position of the cursor or -1 to disable the cursor
     */
    public void setCursorPos(final int newPos) {
        cursorPos = newPos;
        if (cursorPos > -1) {
            graphicalText.setCursorPosition(cursorPos);
            graphicalText.setCursorVisible(true);
        } else {
            graphicalText.setCursorVisible(false);
        }
    }

    /**
     * Set the font that shall be used to render the text.
     * 
     * @param newFont the font used to render the text
     */
    public void setFont(final RenderableFont newFont) {
        if (font != newFont) {
            font = newFont;
            sourceFont = (Font) font.getSourceFont();
            graphicalText.setFont(newFont);
            graphicalText.layout();
            updateOffset();
        }
    }

    /**
     * Set the size of this widget to a new value. This function causes in
     * addition a update of the offset values of this text.
     * 
     * @param newWidth the new width of this widget
     * @param newHeight the new height of this widget
     */
    @Override
    public void setSize(final int newWidth, final int newHeight) {
        super.setSize(newWidth, newHeight);
        updateOffset();
    }

    /**
     * Set a new text that shall be displayed by this text widget.
     * 
     * @param newText the new text that shall be shown.
     */
    public void setText(final String newText) {
        final String cleanedText = newText.replace(Font.NEWLINE, ' ');
        if (!cleanedText.equals(text)) {
            text = cleanedText;
            graphicalText.setText(cleanedText);
            graphicalText.layout();
            updateOffset();
        }
    }

    /**
     * Set the width of the widget to a new value.
     * 
     * @param newWidth the new width of the widget
     */
    @Override
    public void setWidth(final int newWidth) {
        super.setWidth(newWidth);
        updateOffset();
    }

    /**
     * Reset the values of this widget to its original values. This is done when
     * the widget is created and when its initialized.
     */
    private void reset() {
        cursorPos = -1;
        cursorDelta = 0;

        if (graphicalText == null) {
            graphicalText = Graphics.getInstance().getTextLine();
        }
    }

    /**
     * Update the offset value regarding the set align.
     */
    private void updateOffset() {
        if (font == null) {
            return;
        }
        if (text == null) {
            return;
        }
        if (align == ALIGN_LEFT) {
            offsetX = 0;
        } else if (align == ALIGN_RIGHT) {
            offsetX =
                super.getWidth()
                    - sourceFont.getStringBounds(text, 0, text.length()).width;
        } else if (align == ALIGN_CENTER) {
            offsetX =
                (super.getWidth() - sourceFont.getStringBounds(text, 0,
                    text.length()).width) / 2;
        }
        graphicalText.setLocation(super.getRelX() + offsetX, super.getRelY());
    }
}
