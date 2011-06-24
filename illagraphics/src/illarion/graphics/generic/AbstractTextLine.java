/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics.generic;

import illarion.graphics.FontData;
import illarion.graphics.Graphics;
import illarion.graphics.RenderableFont;
import illarion.graphics.SpriteColor;
import illarion.graphics.TextLine;
import illarion.graphics.jogl.GraphicsJOGLException;

/**
 * Generic text line implementation that implements the parts of the text line
 * that is shared by all library specific implementations.
 * 
 * @author Martin Karing
 * @since 2.00
 * @version 2.00
 */
public abstract class AbstractTextLine implements TextLine {
    /**
     * The color that is used to render the text.
     */
    private SpriteColor color;

    /**
     * The dirty flag for the cursor. In case the text or the cursor got changed
     * and the calculation of the cursor x offset was not done again this value
     * is <code>true</code>.
     */
    private boolean cursorDirty = false;

    /**
     * The position where the cursor is displayed.
     * 
     * @see #showCursor
     */
    private int cursorPos = 0;

    /**
     * The calculated x offset of the cursor.
     */
    private int cursorX = 0;

    /**
     * The dirty flag in case anything got changed and its needed to calculate
     * the glyphes and their coordinates again.
     */
    private boolean dirty = false;

    /**
     * The font that is used to render this text line.
     */
    private AbstractRenderableFont font;

    /**
     * The glyphes that are displayed with this text line.
     */
    private FontData.GlyphData[] glyphes;

    /**
     * This color is the inverted color to {@link #color}. Its used when
     * rendering marked text.
     */
    private SpriteColor invertColor;

    /**
     * The x coordinate of this text line.
     */
    private int lineX;

    /**
     * The y coordinate of this text line.
     */
    private int lineY;

    /**
     * The dirty flag of the marked area. In case this is <code>true</code> its
     * needed to recalculate the positions of the marked area.
     */
    private boolean markedDirty = false;

    /**
     * The index where the marked text ends. This is the first glyph index that
     * is not marked anymore.
     * 
     * @see #showMarked
     */
    private int markedEnd = 0;

    /**
     * The x coordinate of the end of the marked area.
     */
    private int markedEndX = 0;

    /**
     * The index where the marked text starts.
     * 
     * @see #showMarked
     */
    private int markedStart = 0;

    /**
     * The x coordinate of the begin of the marked area.
     */
    private int markedStartX = 0;

    /**
     * Show or hide the cursor within this text. If this variable is set to
     * <code>true</code> the cursor is displayed at the position before the
     * glyph with index {@link #cursorPos}.
     */
    private boolean showCursor = false;

    /**
     * Show a marked area on the text. If this variable is set to
     * <code>true</code> the marked text area starts before the glyph with index
     * {@link #markedStart} and ends before the glyph with index
     * {@link #markedEnd}.
     */
    private boolean showMarked = false;

    /**
     * The text that is shown with this text line.
     */
    private String text;

    /**
     * The x coordinates of all glyphes of this text line.
     */
    private int[] x;

    /**
     * Update the layout in case its dirty. That updates the needed glyphes and
     * their coordinates.
     */
    @Override
    public final void layout() {
        if (!dirty) {
            return;
        }
        if (font == null) {
            return;
        }
        if (text == null) {
            return;
        }

        dirty = false;

        font.getSourceFont().getGlyphes(text, 0, text.length(), glyphes, x);
    }

    /**
     * Render the text to its location on the screen.
     */
    @Override
    @SuppressWarnings("nls")
    public final void render() {
        try {
            calculateCursorPos();
            calculateMarkedPos();

            renderMarked();
            final int length = text.length();
            for (int i = 0; i < length; i++) {
                if (glyphes[i] == null) {
                    continue;
                }
                font.renderGlyph(glyphes[i], lineX + x[i], lineY,
                    getFontColor(i));
            }
            renderCursor();
        } catch (final Exception e) {
            throw new GraphicsJOGLException("Failed at drawing text:" + text,
                e);
        }
    }

    /**
     * Set the color that is used to render the line. Note that this does not
     * copy the instance of the color. It just stores the reference. So if the
     * color of the instance is changed later this color will change as well.
     * 
     * @param newColor the color that shall be used for the render actions from
     *            now on
     */
    @Override
    public void setColor(final SpriteColor newColor) {
        color = newColor;
        if (invertColor != null) {
            invertColor.set(color);
            invertColor.setAlpha(color.getAlphai());
            invertColor.invert();
        }
    }

    /**
     * Set the cursor to a specified location in the text. The cursor will be
     * displayed right before the glyph at this position.
     * 
     * @param newCursorPos 0 is the first position, length value of the string
     *            the last
     */
    @Override
    public final void setCursorPosition(final int newCursorPos) {
        if (cursorPos != newCursorPos) {
            cursorPos = newCursorPos;
            cursorDirty = true;
        }
    }

    /**
     * Set the visible state of the cursor. This is used to hide or show the
     * cursor directly.
     * 
     * @param newVisible <code>true</code> in case the cursor how shall be
     *            visible
     */
    @Override
    public final void setCursorVisible(final boolean newVisible) {
        if (showCursor != newVisible) {
            showCursor = newVisible;
            if (showCursor) {
                cursorDirty = true;
            }
        }
    }

    /**
     * Set the font that is used to render this text line.
     * 
     * @param newFont the new font that is used
     */
    @Override
    @SuppressWarnings("nls")
    public void setFont(final RenderableFont newFont) {
        if (newFont != null) {
            if (newFont instanceof AbstractRenderableFont) {
                font = (AbstractRenderableFont) newFont;
                dirty = true;
                cursorDirty = true;
                markedDirty = true;
            } else {
                throw new IllegalArgumentException(
                    "RenderableFont uses a wrong implementation");
            }
        } else {
            throw new IllegalArgumentException(
                "RenderableFont must not be NULL");
        }
    }

    /**
     * Set the location of the lower left position of this text line.
     * 
     * @param newX the x coordinate of the lower left dot of the text line
     * @param newY the y coordinate of the lower left dot of the text line
     */
    @Override
    public final void setLocation(final int newX, final int newY) {
        lineX = newX;
        lineY = newY;
    }

    /**
     * Set the start and the end of the marked text.
     * 
     * @param start the index of the first glyph that is marked
     * @param end the index of the first glyph that is not marked anymore
     */
    @Override
    public final void setMarkedRange(final int start, final int end) {
        markedStart = start;
        markedEnd = end;
        markedDirty = true;
    }

    /**
     * Set the visible state of the marked text.
     * 
     * @param newVisible <code>true</code> in case marked text shall be shown at
     *            this line
     */
    @Override
    public final void setMarkedVisible(final boolean newVisible) {
        showMarked = newVisible;
    }

    /**
     * Set the text that is displayed with this line. This also causes that the
     * arrays for the glyphes and the coordinates are increased in case its
     * needed.
     * 
     * @param newText the text that shall be displayed with this line
     */
    @Override
    public final void setText(final String newText) {
        if ((text != null) && text.equals(newText)) {
            return;
        }

        text = newText;

        if (glyphes == null) {
            glyphes = new FontData.GlyphData[text.length()];
        } else if (glyphes.length < text.length()) {
            final FontData.GlyphData[] newGlyphes =
                new FontData.GlyphData[text.length()];
            System.arraycopy(glyphes, 0, newGlyphes, 0, glyphes.length);
            glyphes = newGlyphes;
        }

        if (x == null) {
            x = new int[text.length()];
        } else if (x.length < text.length()) {
            final int[] newX = new int[text.length()];
            System.arraycopy(x, 0, newX, 0, x.length);
            x = newX;
        }

        dirty = true;
        markedDirty = true;
        cursorDirty = true;
    }

    /**
     * Toggle the display state of the cursor. So if its was shown its hidden
     * after.
     */
    @Override
    public final void toogleCursor() {
        showCursor = !showCursor;
    }

    /**
     * Calculate the location of the cursor.
     */
    private void calculateCursorPos() {
        if (!cursorDirty) {
            return;
        }
        cursorDirty = false;

        final int length = text.length();
        int currX = 0;
        for (int i = 0; i < length; i++) {
            if (i == cursorPos) {
                cursorX = currX;
                return;
            }
            if (glyphes[i] == null) {
                font.getSourceFont().getGlyph(' ').getAdvance();
            } else {
                currX += glyphes[i].getAdvance();
            }
        }

        if (cursorPos == length) {
            cursorX = currX;
            return;
        }
    }

    /**
     * Calculate the marked area start and end coordinates in case its needed.
     */
    private void calculateMarkedPos() {
        if (!markedDirty || !showMarked) {
            return;
        }
        markedDirty = false;

        final int length = text.length();
        int currX = 0;
        for (int i = 0; i < length; i++) {
            if (i == markedStart) {
                markedStartX = currX;
            } else if (i == markedEnd) {
                markedEndX = currX;
            }
            currX += glyphes[i].getAdvance();
        }

        if (length == markedEnd) {
            markedEndX = currX;
        }
    }

    /**
     * Get the color that needs to be used when rendering the font.
     * 
     * @param index the glyph index that is currently rendered
     * @return the sprite color that has to be used for rendering
     */
    private SpriteColor getFontColor(final int index) {
        if (!showMarked) {
            return color;
        }

        if ((index < markedStart) || (index >= markedEnd)) {
            return color;
        }

        if (invertColor == null) {
            invertColor = Graphics.getInstance().getSpriteColor();
            invertColor.set(color);
            invertColor.setAlpha(color.getAlphai());
            invertColor.invert();
        }
        return invertColor;
    }

    /**
     * Draw the cursor to the needed location in case it needs to be displayed.
     */
    private void renderCursor() {
        if (!showCursor) {
            return;
        }
        Graphics
            .getInstance()
            .getDrawer()
            .drawLine(lineX + cursorX,
                lineY - font.getSourceFont().getDescent(), lineX + cursorX,
                lineY + font.getSourceFont().getAscent(), 1.f, color);
    }

    /**
     * Draw the marked area.
     */
    private void renderMarked() {
        if (!showMarked) {
            return;
        }

        final int oldAlpha = color.getAlphai();
        color.setAlpha(0.7f);
        Graphics
            .getInstance()
            .getDrawer()
            .drawRectangle(lineX + markedStartX,
                lineY - font.getSourceFont().getDescent(), lineX + markedEndX,
                lineY + font.getSourceFont().getAscent(), color);
        color.setAlpha(oldAlpha);
    }
}
