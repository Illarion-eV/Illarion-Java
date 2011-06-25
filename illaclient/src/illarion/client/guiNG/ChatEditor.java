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
package illarion.client.guiNG;

import java.awt.Rectangle;

import javolution.text.TextBuilder;

import illarion.client.graphics.AnimationUtility;
import illarion.client.graphics.Colors;
import illarion.client.graphics.MarkerFactory;
import illarion.client.guiNG.elements.Image;
import illarion.client.guiNG.elements.ImageRepeated;
import illarion.client.guiNG.elements.SolidColor;
import illarion.client.guiNG.elements.TextArea;
import illarion.client.guiNG.elements.Widget;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.SayCmd;
import illarion.client.util.Lang;
import illarion.client.world.Game;

import illarion.graphics.Graphics;
import illarion.graphics.RenderableFont;
import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;
import illarion.graphics.common.Font;
import illarion.graphics.common.FontLoader;

/**
 * The chat editor displays on the screen on case the player wants to type
 * anything.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class ChatEditor extends Widget implements TextCursorTarget {
    /**
     * The alternative introduction character of a command.
     */
    @SuppressWarnings("nls")
    private static final String altCommandLead = "/";

    /**
     * The intro character of a command.
     */
    @SuppressWarnings("nls")
    private static final String commandLead = "#";

    /**
     * The introduction sequence of the emote command.
     */
    @SuppressWarnings("nls")
    private static final String emoteCommand = commandLead + "me";

    /**
     * The maximal length allowed for this chat editor.
     */
    private static final int MAX_CHARS = 255;

    /**
     * The introduction sequence of the ooc command.
     */
    @SuppressWarnings("nls")
    private static final String oocCommand = commandLead + "o";

    /**
     * The serialization UID of the chat editor.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The introduction sequence of the shout command.
     */
    @SuppressWarnings("nls")
    private static final String shoutCommand = commandLead + "s";

    /**
     * The intro sequence of the whisper command.
     */
    @SuppressWarnings("nls")
    private static final String whisperCommand = commandLead + "w";

    /**
     * The background of the editor.
     */
    private final Widget background;

    /**
     * The bottom border of the editor.
     */
    private final Widget bottomBorder;

    /**
     * The bottom left edge of the editor.
     */
    private final Widget bottomLeftEdge;

    /**
     * The bottom right edge of the editor.
     */
    private final Widget bottomRightEdge;

    /**
     * The offset to the cursor location.
     */
    private int cursorOffset = 0;

    /**
     * A flag that stores if the text written was changed and the rendered text
     * requires a update.
     */
    private boolean dirtyText;

    /**
     * Flag if set to true the next render run causes a recalculation of the
     * size and location of the widget.
     */
    private boolean forceUpdate;

    /**
     * The introduction text in case something is emoted.
     */
    @SuppressWarnings("nls")
    private final String introTextEmote = Lang.getMsg("editor.intro.emote");

    /**
     * The introduction text in case something is said ooc.
     */
    @SuppressWarnings("nls")
    private final String introTextOOC = Lang.getMsg("editor.intro.ooc");

    /**
     * The introduction text in case something is said.
     */
    @SuppressWarnings("nls")
    private final String introTextSay = Lang.getMsg("editor.intro.say");

    /**
     * The introduction text in case something is shouted.
     */
    @SuppressWarnings("nls")
    private final String introTextShout = Lang.getMsg("editor.intro.shout");

    /**
     * The introduction text in case something is whispered.
     */
    @SuppressWarnings("nls")
    private final String introTextWhisper = Lang
        .getMsg("editor.intro.whisper");

    /**
     * The height of the editor that is the animation target.
     */
    private int targetHeight;

    /**
     * The width of the editor that is the animation target.
     */
    private int targetWidth;

    /**
     * The font used to render the text.
     */
    private transient RenderableFont textFont = FontLoader.getInstance()
        .getFont(FontLoader.TEXT_FONT);

    /**
     * The source font object needed for some of the calculations.
     */
    private transient Font textFontSource = (Font) FontLoader.getInstance()
        .getFont(FontLoader.TEXT_FONT).getSourceFont();

    /**
     * The widget used to display the text.
     */
    private final TextArea textLine;

    /**
     * The additional pixels to the height of the text line.
     */
    private final int textLineHeightBorder;

    /**
     * The additional pixels to the width of the text line
     */
    private final int textLineWidthBorder;

    /**
     * The top border of the editor.
     */
    private final Widget topBorder;

    /**
     * The top left edge of the editor.
     */
    private final Widget topLeftEdge;

    /**
     * The top right edge of the editor.
     */
    private final Widget topRightEdge;

    /**
     * The text written currently into the chat editor.
     */
    private transient final StringBuffer writtenText = new StringBuffer();

    /**
     * Setup the chat editor correctly. That creates all required elements
     * automatically.
     */
    public ChatEditor() {
        super();

        dirtyText = false;

        topBorder = new ImageRepeated();
        Sprite sprite =
            MarkerFactory.getInstance()
                .getPrototype(MarkerFactory.GUI_HORZ_BORDER).getSprite();
        sprite.setAlign(Sprite.HAlign.left, Sprite.VAlign.bottom);
        ((ImageRepeated) topBorder).setImage(sprite);
        topBorder.setHeight(3);
        topBorder.setWidth(getWidth() - 6);
        topBorder.setRelPos(3, getHeight() - topBorder.getHeight());

        bottomBorder = new ImageRepeated();
        ((ImageRepeated) bottomBorder).setImage(sprite);
        bottomBorder.setHeight(3);
        bottomBorder.setWidth(getWidth() - 6);
        bottomBorder.setRelPos(3, 0);

        topLeftEdge = new Image();
        sprite =
            MarkerFactory.getInstance()
                .getPrototype(MarkerFactory.GUI_HORZ_LIMIT).getSprite();
        sprite.setAlign(Sprite.HAlign.left, Sprite.VAlign.bottom);
        ((Image) topLeftEdge).setImage(sprite);
        ((Image) topLeftEdge).setSizeToImage();
        topLeftEdge.setRelPos(0, getHeight() - topLeftEdge.getHeight());

        bottomLeftEdge = new Image();
        ((Image) bottomLeftEdge).setImage(sprite);
        ((Image) bottomLeftEdge).setSizeToImage();
        bottomLeftEdge.setRelPos(0, 0);

        topRightEdge = new Image();
        ((Image) topRightEdge).setImage(sprite);
        ((Image) topRightEdge).setSizeToImage();
        topRightEdge.setRelPos(getWidth() - topRightEdge.getWidth(),
            getHeight() - topRightEdge.getHeight());

        bottomRightEdge = new Image();
        ((Image) bottomRightEdge).setImage(sprite);
        ((Image) bottomRightEdge).setSizeToImage();
        bottomRightEdge.setRelPos(getWidth() - bottomRightEdge.getWidth(), 0);

        textLineHeightBorder =
            topBorder.getHeight() + bottomBorder.getHeight() + 6;
        textLineWidthBorder =
            topLeftEdge.getWidth() + topRightEdge.getWidth() + 6;

        textLine = new TextArea();
        textLine.setFont(textFont);
        textLine.setWidth(getWidth() - textLineWidthBorder);
        textLine.setHeight(getHeight() - textLineHeightBorder);
        textLine.setRelPos(textLineWidthBorder / 2, textLineHeightBorder / 2);
        textLine.setText("");
        textLine.setColor(Colors.blue.getColor());

        final SpriteColor backColor = Graphics.getInstance().getSpriteColor();
        backColor.set(0.f);
        backColor.setAlpha(0.7f);

        background = new SolidColor();
        ((SolidColor) background).setColor(backColor);
        background.setHeight(getHeight() - 6);
        background.setWidth(getWidth() - 6);
        background.setRelPos(3, 3);

        addChild(background);
        addChild(topBorder);
        addChild(bottomBorder);
        addChild(topLeftEdge);
        addChild(topRightEdge);
        addChild(bottomLeftEdge);
        addChild(bottomRightEdge);
        addChild(textLine);

        forceUpdate = true;
    }

    /**
     * Stop the input and close the editor.
     */
    @Override
    public void cancelInput() {
        setVisible(false);
    }

    /**
     * Cleanup the editor before saving. That results in removing the editor
     * from the tree because its location is not user defined anyway.
     */
    @Override
    public void cleanup() {
        super.cleanup();
        if (hasParent()) {
            getParent().removeChild(this);
        }
    }

    /**
     * Remove all written text from the storage.
     */
    @Override
    public void clear() {
        writtenText.setLength(0);
        setCursorPosition(0);
        dirtyText = true;
    }

    /**
     * Disable the text cursor.
     */
    @Override
    public void disableCursor() {
        clear();
        textLine.setCursorPos(-1);
    }

    /**
     * Draw the editor properly and perform the needed animations.
     * 
     * @param delta the time in milliseconds since the last render
     */
    @Override
    public void draw(final int delta) {
        if (!isVisible()) {
            return;
        }

        updateText();
        doAnimations(delta);

        super.draw(delta);
    }

    /**
     * Send the written text to have the character saying it.
     */
    @Override
    public void executeEnter() {
        if (getTextLength() == 0) {
            setVisible(false);
        } else {
            send();
            clear();
        }
    }

    /**
     * Get the current location of the cursor.
     * 
     * @return current cursor location
     */
    public int getCursorPosition() {
        return textLine.getCursorPos() - cursorOffset;
    }

    /**
     * Get the maximal amount of characters this text accepts.
     * 
     * @return the maximal amount of characters
     */
    @Override
    public int getMaxLength() {
        return MAX_CHARS;
    }

    /**
     * Get the written text.
     * 
     * @return the text the player typed in
     */
    @Override
    public CharSequence getText() {
        return writtenText.toString();
    }

    /**
     * Returns the textFontSource of the chat editor
     * 
     * @return the textFontSource of the chat editor
     */
    @Override
    public Font getTextFontSource() {
        return textFontSource;
    }

    /**
     * The length of the text written into the editor.
     * 
     * @return the length of the text
     */
    @Override
    public int getTextLength() {
        return writtenText.length();
    }

    /**
     * Prepare the chat layer for proper usage and put the transient variables
     * back into place.
     */
    @Override
    public void initWidget() {
        textFontSource =
            (Font) FontLoader.getInstance().getFont(FontLoader.TEXT_FONT)
                .getSourceFont();
        textFont = FontLoader.getInstance().getFont(FontLoader.TEXT_FONT);
        super.initWidget();
    }

    /**
     * Insert a character at the current position of the text cursor.
     * 
     * @param character the character to insert
     */
    @Override
    @SuppressWarnings("nls")
    public void insertCharacter(final char character) {
        if ((textLine.getCursorPos() > -1)
            && (writtenText.length() < MAX_CHARS)) {
            try {
                writtenText.insert(getCursorPosition(), character);
                dirtyText = true;
            } catch (final Exception e) {
                throw new RuntimeException("Insert Character failed at pos "
                    + getCursorPosition(), e);
            }
        }
    }

    /**
     * Insert a sequence of characters at the current cursor position.
     * 
     * @param characters the characters to be insert
     */
    @Override
    public void insertCharacters(final CharSequence characters) {
        if ((textLine.getCursorPos() > -1)
            && ((writtenText.length() + characters.length()) < MAX_CHARS)) {
            writtenText.insert(getCursorPosition(), characters);
            dirtyText = true;
        }
    }

    /**
     * Remove one character from the current cursor location.
     */
    @Override
    public void remove() {
        if (getCursorPosition() > 0) {
            removeAt(getCursorPosition() - 1);
        }
    }

    /**
     * Remove a character at a given position.
     * 
     * @param idx the position of the character which should be removed
     */
    @Override
    public void removeAt(final int idx) {
        final int removeCount = Character.charCount(writtenText.codePointAt(idx));
        writtenText.delete(idx - removeCount + 1, idx + 1);
        dirtyText = true;
    }

    /**
     * Send the text to the server.
     */
    public void send() {
        final SayCmd cmd =
            (SayCmd) CommandFactory.getInstance().getCommand(
                CommandList.CMD_SAY);

        String output = writtenText.toString().trim();
        if (output.startsWith(altCommandLead)) {
            output = commandLead + output.substring(altCommandLead.length());
        }
        cmd.setText(output);
        cmd.send();
    }

    /**
     * Set the cursor to a new position.
     * 
     * @param pos the new position of the cursor
     */
    @Override
    public void setCursorPosition(final int pos) {
        textLine.setCursorPos(pos + cursorOffset);
    }

    /**
     * Change the visibility of the editor. In case its set to invisible the
     * size will be updated.
     * 
     * @param newVisible the new value for the visible flag
     */
    @Override
    public void setVisible(final boolean newVisible) {
        super.setVisible(newVisible);
        if (!isVisible()) {
            setHeight(topLeftEdge.getHeight() * 2);
            setWidth(textFontSource.getStringBounds(introTextSay, 0,
                introTextSay.length()).width + textLineWidthBorder);

            if (TextCursor.getInstance().isTarget(this)) {
                TextCursor.getInstance().disable();
            } else {
                clear();
            }
        } else {
            forceUpdate = true;
            dirtyText = true;
        }
    }

    /**
     * Perform the animations needed in case the size of the editor got changed.
     * 
     * @param delta the time since the last rendering run
     */
    private void doAnimations(final int delta) {
        if ((targetWidth != getWidth()) || forceUpdate) {
            setWidth(AnimationUtility.approach(getWidth(), targetWidth, 0,
                getParent().getWidth() - 20, delta));
            Utility.centerWidgetX(this);

            topBorder.setWidth(getWidth() - 6);
            bottomBorder.setWidth(getWidth() - 6);
            topRightEdge.setRelX(getWidth() - topRightEdge.getWidth());
            bottomRightEdge.setRelX(getWidth() - bottomRightEdge.getWidth());
            background.setWidth(getWidth() - 6);
            textLine.setWidth(getWidth() - textLineWidthBorder);
        }
        if ((targetHeight != getHeight()) || forceUpdate) {
            setHeight(AnimationUtility.approach(getHeight(), targetHeight, 0,
                getParent().getHeight(), delta));

            final int newY =
                (int) (getParent().getHeight() * 0.25f) - getHeight();
            setRelY(Math.max(getHeight() + 10, newY));

            topBorder.setRelY(getHeight() - topBorder.getHeight());
            topLeftEdge.setRelY(getHeight() - topLeftEdge.getHeight());
            topRightEdge.setRelY(getHeight() - topRightEdge.getHeight());
            background.setHeight(getHeight() - 6);
            textLine.setHeight(getHeight() - textLineHeightBorder);
        }
        forceUpdate = false;
    }

    /**
     * Update the rendered text in case its needed.
     */
    private void updateText() {
        if (!dirtyText) {
            return;
        }

        dirtyText = false;
        String text = writtenText.toString();
        if (text.startsWith(altCommandLead)) {
            text = commandLead + text.substring(1);
        }
        final int oldCursorPos = getCursorPosition();
        if (text.startsWith(emoteCommand)) {
            final TextBuilder helpBuffer = TextBuilder.newInstance();
            helpBuffer.append(introTextEmote);
            helpBuffer.append(' ');
            helpBuffer.append(Game.getPlayer().getCharacter().getName());
            cursorOffset = helpBuffer.length() - emoteCommand.length();
            helpBuffer.append(text.substring(emoteCommand.length()));
            text = helpBuffer.toString();
            textLine.setColor(Colors.yellow.getColor());
            TextBuilder.recycle(helpBuffer);
        } else if (text.startsWith(whisperCommand)) {
            final TextBuilder helpBuffer = TextBuilder.newInstance();
            helpBuffer.append(introTextWhisper);
            helpBuffer.append(' ');
            cursorOffset = helpBuffer.length() - whisperCommand.length();
            helpBuffer.append(text.substring(whisperCommand.length()));
            text = helpBuffer.toString();
            textLine.setColor(Colors.gray.getColor());
            TextBuilder.recycle(helpBuffer);
        } else if (text.startsWith(shoutCommand)) {
            final TextBuilder helpBuffer = TextBuilder.newInstance();
            helpBuffer.append(introTextShout);
            helpBuffer.append(' ');
            cursorOffset = helpBuffer.length() - shoutCommand.length();
            helpBuffer.append(text.substring(shoutCommand.length()));
            text = helpBuffer.toString();
            textLine.setColor(Colors.red.getColor());
            TextBuilder.recycle(helpBuffer);
        } else if (text.startsWith(oocCommand)) {
            final TextBuilder helpBuffer = TextBuilder.newInstance();
            helpBuffer.append(introTextOOC);
            helpBuffer.append(' ');
            cursorOffset = helpBuffer.length() - oocCommand.length();
            helpBuffer.append(text.substring(oocCommand.length()));
            text = helpBuffer.toString();
            textLine.setColor(Colors.gray.getColor());
            TextBuilder.recycle(helpBuffer);
        } else {
            final TextBuilder helpBuffer = TextBuilder.newInstance();
            helpBuffer.append(introTextSay);
            helpBuffer.append(' ');
            cursorOffset = helpBuffer.length();
            helpBuffer.append(text);
            text = helpBuffer.toString();
            textLine.setColor(Colors.blue.getColor());
            TextBuilder.recycle(helpBuffer);
        }
        setCursorPosition(oldCursorPos);
        textLine.setText(text);
        textLine.setMaximalWidth(Math.min(getParent().getWidth() - 40, 500));
        final Rectangle bounds = textLine.getBounds();
        targetWidth = bounds.width + textLineWidthBorder;
        targetHeight = bounds.height + textLineHeightBorder;
    }
}
