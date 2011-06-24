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

import java.util.List;

import javolution.util.FastTable;

import illarion.common.util.Rectangle;

import illarion.graphics.RenderableFont;
import illarion.graphics.SpriteColor;
import illarion.graphics.common.Font;

/**
 * The text area is a meta widget that combines multiple text widgets in order
 * to support text with more then one line.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class TextArea extends Widget {
    /**
     * The serialization UID of the text area.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The color used to render the font.
     */
    private transient SpriteColor color;

    /**
     * The lines currently used by this widget.
     */
    private transient int currentLines;

    /**
     * The text displayed in the text area.
     */
    private String currentText;

    /**
     * The cursor position. If this is set to <code>-1</code> the cursor is not
     * displayed at all.
     */
    private transient int cursorPos = -1;

    /**
     * Flag that is set to <code>true</code> in case the cursor position was
     * changed and its needed to recalculate the location of the cursor.
     */
    private transient boolean dirtyCursor;

    /**
     * Flag that is set <code>true</code> in case the text requires to be layout
     * again.
     */
    private transient boolean dirtyText;

    /**
     * The font used to display the text.
     */
    private transient RenderableFont font;

    /**
     * The maximal width that is supposed to be used by the text area. By
     * default the size of the widget is used.
     */
    private int maximalWidth;

    /**
     * The source font object that is needed to perform some calculations.
     */
    private transient Font sourceFont;

    /**
     * The text elements used to display the text. The amount of elements in
     * this list is not by all means the amount of lines really displayed.
     */
    private transient List<Text> textLines;

    /**
     * Constructor to setup the required data.
     */
    public TextArea() {
        currentLines = 0;
        currentText = null;
        maximalWidth = -1;
        dirtyCursor = true;
    }

    /**
     * This cleanup function removes all automatically added children from this
     * class.
     */
    @Override
    public void cleanup() {
        removeAllChildren();
        super.cleanup();
    }

    /**
     * Draw the text box.
     * 
     * @param delta the time since the last drawing
     */
    @Override
    public void draw(final int delta) {
        layout();
        calculateCursorPos();
        super.draw(delta);
    }

    /**
     * Get the bounding box of the text area.
     * 
     * @return the bounding box of the text area
     */
    public java.awt.Rectangle getBounds() {
        layout();
        if (currentLines == 0) {
            return new java.awt.Rectangle(0, 0, 0, 0);
        }

        final Rectangle resultRect = Rectangle.getInstance();
        resultRect.set(0, 0, 0, 0);
        final Rectangle tempRect = Rectangle.getInstance();
        for (int i = currentLines - 1; i >= 0; --i) {
            final Text line = textLines.get(i);
            tempRect.set(line.getRelX(), line.getRelY(), line.getWidth(),
                line.getHeight());
            resultRect.add(tempRect);
        }

        tempRect.set(0, 0, 1, 1);
        resultRect.add(tempRect);
        tempRect.recycle();

        final java.awt.Rectangle nativeReturn = resultRect.toNative();
        resultRect.recycle();

        return nativeReturn;
    }

    /**
     * Get the current location of the cursor
     * 
     * @return the index of the cursor
     */
    public int getCursorPos() {
        return cursorPos;
    }

    /**
     * Prepare the widget for proper execution.
     */
    @Override
    public void initWidget() {
        cursorPos = -1;
        dirtyCursor = true;
        dirtyText = true;
        super.initWidget();
    }

    /**
     * Set the color used to render the text.
     * 
     * @param newColor the color used to render the text.
     */
    public void setColor(final SpriteColor newColor) {
        color = newColor;
        if (textLines != null) {
            for (int i = textLines.size() - 1; i >= 0; --i) {
                textLines.get(i).setColor(color);
            }
        }
    }

    /**
     * Set the location of the cursor in the text.
     * 
     * @param newCursorPos the location of the cursor in the text
     */
    public void setCursorPos(final int newCursorPos) {
        cursorPos = newCursorPos;
        dirtyCursor = true;
    }

    /**
     * Set the font used to render the text.
     * 
     * @param newFont the new font used to render the text
     */
    public void setFont(final RenderableFont newFont) {
        font = newFont;
        sourceFont = (Font) font.getSourceFont();
        dirtyText = true;
    }

    /**
     * Set the maximal width this text area is supposed to use.
     * 
     * @param newWidth the new maximal width in pixel, in case its set to -1 the
     *            size of the widget holds valid
     */
    public void setMaximalWidth(final int newWidth) {
        maximalWidth = newWidth;
        dirtyText = true;
    }

    /**
     * Set the new text that is supposed to be displayed in the text box.
     * 
     * @param newText the new text to be displayed
     */
    public void setText(final String newText) {
        currentText = newText;
        dirtyText = true;
    }

    /**
     * Calculate the position of the cursor in case its needed.
     */
    private void calculateCursorPos() {
        if (!dirtyCursor || (textLines == null)) {
            return;
        }

        dirtyCursor = false;
        int tempCursorPos = cursorPos;
        final int textLineCount = textLines.size();
        boolean foundPos = false;
        Text line;
        for (int i = 0; i < textLineCount; i++) {
            line = textLines.get(i);
            final int textLength = line.getText().length();
            if ((textLength >= tempCursorPos) && !foundPos) {
                line.setCursorPos(tempCursorPos);
                foundPos = true;
            } else {
                line.setCursorPos(-1);
            }
            tempCursorPos -= textLength;
        }
    }

    /**
     * Refresh the graphical text in case its needed. This results in placing
     * the text and recalculating the needed lines.
     */
    private void layout() {
        if (!dirtyText) {
            return;
        }

        dirtyText = false;
        dirtyCursor = true;

        if ((currentText == null) || (font == null) || (color == null)) {
            removeAllChildren();
            currentLines = 0;
            return;
        }

        int targetWidth = maximalWidth;
        if (targetWidth < 0) {
            targetWidth = getWidth();
        }

        int currentIndex = 0;
        final int textLength = currentText.length();
        int endPos;
        currentLines = 0;
        removeAllChildren();

        if (textLines == null) {
            textLines = FastTable.newInstance();
        }

        while (currentIndex < textLength) {
            endPos =
                sourceFont.getStringWrap(currentText, currentIndex,
                    textLength, targetWidth);
            Text usedTextLine;
            if (textLines.size() > currentLines) {
                usedTextLine = textLines.get(currentLines);
            } else {
                usedTextLine = new Text();
                textLines.add(currentLines, usedTextLine);
            }
            usedTextLine.setFont(font);
            usedTextLine.setColor(color);
            usedTextLine.setText(currentText.substring(currentIndex,
                endPos + 1));
            usedTextLine.setCursorPos(-1);

            final java.awt.Rectangle lineBounds =
                sourceFont.getStringBounds(currentText, currentIndex,
                    endPos + 1);
            usedTextLine.setWidth(lineBounds.width);
            usedTextLine.setHeight(lineBounds.height);
            addChild(usedTextLine);

            currentIndex = endPos + 1;
            currentLines++;
        }

        while (textLines.size() > currentLines) {
            textLines.remove(textLines.size() - 1);
        }

        int currentPen = sourceFont.getDescent();
        for (int i = currentLines - 1; i >= 0; --i) {
            textLines.get(i).setRelPos(0, currentPen);
            currentPen += sourceFont.getAscent() + sourceFont.getDescent();
        }
    }
}
