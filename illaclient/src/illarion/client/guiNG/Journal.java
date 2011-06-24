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
import java.awt.event.KeyEvent;

import javolution.text.TextBuilder;
import javolution.util.FastList;
import javolution.util.FastList.Node;

import illarion.client.IllaClient;
import illarion.client.graphics.Colors;
import illarion.client.guiNG.elements.ScrollArea;
import illarion.client.guiNG.elements.TextArea;
import illarion.client.guiNG.messages.Message;
import illarion.client.guiNG.messages.WindowMessage;
import illarion.client.util.ChatHandler;
import illarion.client.util.Lang;
import illarion.client.world.Char;

import illarion.common.config.Config;
import illarion.common.config.ConfigChangeListener;

import illarion.graphics.common.FontLoader;

import illarion.input.KeyboardEvent;

/**
 * This class implements the journal that displays the last lines of text that
 * were spoken by the players on the screen. The journal is a list of text lines
 * that allows scrolling this lines.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class Journal extends ScrollArea implements ConfigChangeListener {
    /**
     * This constant is used in the configuration and declares the kind of font
     * that is used. Either a small or a large font.
     */
    @SuppressWarnings("nls")
    public static final String CFG_JOURNAL_FONT = "journalFont";

    /**
     * The value of the font configuration for the large font.
     */
    public static final int CFG_JOURNAL_FONT_LARGE = 0;

    /**
     * The value of the font configuration for the small font.
     */
    public static final int CFG_JOURNAL_FONT_SMALL = 1;

    /**
     * This constant is used in the configuration and declares the maximal
     * amount of entries stored in the journal.
     */
    @SuppressWarnings("nls")
    public static final String CFG_JOURNAL_LENGTH = "journalLength";

    /**
     * This constant holds the space to the border left and right.
     */
    private static final int BORDER_SPACE = 5;

    /**
     * The amount of additional space in pixel between two separated text
     * blocks.
     */
    private static final int ENTRY_SPACE = 3;

    /**
     * The serialization UID of this journal widget.
     */
    private static final long serialVersionUID = 1L;

    /**
     * This flag is set true in case the text that is displayed changed and the
     * layout needs to be updated.
     */
    private boolean dirtyText;

    /**
     * The key for a chat entry in case you hear someone saying something but
     * not the source of the voice.
     */
    @SuppressWarnings("nls")
    private final String KEY_DISTANCE = "chat.distantShout";

    /**
     * The key for normal speech.
     */
    @SuppressWarnings("nls")
    private final String KEY_SAY = "log.say";

    /**
     * The key for shouting.
     */
    @SuppressWarnings("nls")
    private final String KEY_SHOUT = "log.shout";

    /**
     * The key for the language file for the generic "someone" name in the chat.
     */
    @SuppressWarnings("nls")
    private final String KEY_SOMEONE = "chat.someone";

    /**
     * The key for whispering.
     */
    @SuppressWarnings("nls")
    private final String KEY_WHISPER = "log.whisper";

    /**
     * The maximal amount of entries.
     */
    private int maxEntries;

    /**
     * The list of texts that is displayed on this journal.
     */
    private final FastList<TextArea> textList;

    /**
     * The font that is used to display the journal.
     */
    private transient String usedFont = FontLoader.TEXT_FONT;

    /**
     * The public constructor for the journal that prepares the builds up all
     * the needed variables for this class to work properly.
     */
    public Journal() {
        super();

        textList = new FastList<TextArea>();
        dirtyText = false;

        IllaClient.getCfg().addListener(CFG_JOURNAL_LENGTH, this);
        IllaClient.getCfg().addListener(CFG_JOURNAL_FONT, this);
        configChanged(IllaClient.getCfg(), CFG_JOURNAL_LENGTH);
        configChanged(IllaClient.getCfg(), CFG_JOURNAL_FONT);

        setSmoothSlowdown(true);
        setHorizontalScrollbarVisibility(SCROLLBAR_NEVER);
        setVerticalScrollbarVisibility(SCROLLBAR_NEVER);
        setDrag(true);
    }

    /**
     * Add a chat entry to this journal. This causes that the added line will
     * displayed in addition in future.
     * 
     * @param message the message added to the list
     * @param chara the char that spoke this message
     * @param talkMode the method used to say the mesage
     */
    @SuppressWarnings("nls")
    public void addText(final String message, final Char chara,
        final ChatHandler.SpeechMode talkMode) {
        final TextBuilder textBuilder = TextBuilder.newInstance();

        // get player's name
        String name = null;
        if (chara != null) {
            name = chara.getName();
        }

        if (talkMode == ChatHandler.SpeechMode.emote) {
            // we need some kind of name
            if (name == null) {
                name = Lang.getMsg(KEY_SOMEONE);
            }

            textBuilder.append(name);
            textBuilder.append(message);
        } else {
            // normal text hears a shout from the distance
            if ((name == null) && (chara == null)) {
                name = Lang.getMsg(KEY_DISTANCE);
            } else if ((name == null) && (chara != null)) {
                name =
                    Lang.getMsg(KEY_SOMEONE) + " ("
                        + Long.toString(chara.getCharId()) + ")";
            }

            textBuilder.append(name);
            if (chara != null) {
                if (talkMode == ChatHandler.SpeechMode.shout) {
                    textBuilder.append(' ').append(Lang.getMsg(KEY_SHOUT));
                } else if (talkMode == ChatHandler.SpeechMode.whisper) {
                    textBuilder.append(' ').append(Lang.getMsg(KEY_WHISPER));
                } else {
                    textBuilder.append(' ').append(Lang.getMsg(KEY_SAY));
                }
            }

            textBuilder.append(':').append(' ');
            textBuilder.append(message);
        }

        // send out the text
        final TextArea textWidget = new TextArea();
        textWidget.setCursorPos(-1);
        textWidget.setText(textBuilder.toString());
        textWidget.setFont(FontLoader.getInstance().getFont(usedFont));
        textWidget.setColor(Colors.black.getColor());
        textWidget.setVisible(false);
        addChild(textWidget);

        textList.addLast(textWidget);
        dirtyText = true;

        TextBuilder.recycle(textBuilder);
    }

    /**
     * This method is triggered in case a value of the configuration that is
     * monitored by this class changes.
     */
    @Override
    public void configChanged(final Config cfg, final String key) {
        if (CFG_JOURNAL_LENGTH.equals(key)) {
            maxEntries = cfg.getInteger(CFG_JOURNAL_LENGTH);
        } else if (CFG_JOURNAL_FONT.equals(key)) {
            final int fontID = cfg.getInteger(CFG_JOURNAL_FONT);
            String newFont;
            if (fontID == CFG_JOURNAL_FONT_LARGE) {
                newFont = FontLoader.TEXT_FONT;
            } else {
                newFont = FontLoader.SMALL_FONT;
            }

            if (!newFont.equals(usedFont)) {
                usedFont = newFont;
                dirtyText = true;
            }
        }
    }

    /**
     * This overwritten drawing function layouts the text as needed before
     * drawing the widget.
     */
    @Override
    public void draw(final int delta) {
        layoutText();
        super.draw(delta);
    }

    @Override
    public boolean handleKeyboardEvent(final KeyboardEvent event) {
        if (event.getKey() == KeyEvent.VK_J) {
            if ((event.getEvent() != KeyboardEvent.EVENT_KEY_UP)
                || event.isRepeated()) {
                return true;
            }

            if (isVisible()) {
                GUI.getInstance().hideJournal();
            } else {
                GUI.getInstance().showJournal();
            }

            return true;
        }

        return super.handleKeyboardEvent(event);
    }

    @Override
    public void handleMessage(final Message msg) {
        if (msg instanceof WindowMessage) {
            final WindowMessage winMsg = (WindowMessage) msg;
            if (winMsg.getMessageType() == WindowMessage.WINDOW_CLOSED) {
                setVisible(false);
            } else {
                setVisible(true);
            }
        }
    }

    /**
     * This function is called upon the initialization of the GUI and registers
     * the class properly to the configuration system in addition to its default
     * behavior.
     * <p>
     * {@inheritDoc}
     * </p>
     */
    @Override
    public void initWidget() {
        super.initWidget();

        IllaClient.getCfg().addListener(CFG_JOURNAL_LENGTH, this);
        IllaClient.getCfg().addListener(CFG_JOURNAL_FONT, this);
        configChanged(IllaClient.getCfg(), CFG_JOURNAL_LENGTH);
        configChanged(IllaClient.getCfg(), CFG_JOURNAL_FONT);
    }

    /**
     * Layout the text on the widget, also add and remove the text widgets as
     * needed. This function also handles the proper scrolling of the new
     * entries.
     */
    private void layoutText() {
        if (!dirtyText) {
            return;
        }

        /* Limit amount of texts */
        while (textList.size() > maxEntries) {
            removeChild(textList.removeFirst());
        }

        final int width = getViewportWidth() - (BORDER_SPACE << 1);
        final int currentOffsetY = getScrollOffsetY();

        Node<TextArea> currentText = textList.tail().getPrevious();
        TextArea currentTextArea = currentText.getValue();
        int cursor = 0;
        boolean oldEntryFound = false;
        Rectangle entryBounds;

        while (currentTextArea != null) {
            currentTextArea.setMaximalWidth(width);

            if (currentTextArea.isVisible()) {
                if (!oldEntryFound) {
                    oldEntryFound = true;
                    setScrollOffset(0, -cursor - currentOffsetY);
                }
            } else {
                currentTextArea.setVisible(true);
            }

            currentTextArea.refreshLayout();
            entryBounds = currentTextArea.getBounds();

            currentTextArea.setSize(width, entryBounds.height);

            currentTextArea.setRelPos(BORDER_SPACE, cursor);

            cursor += entryBounds.height + ENTRY_SPACE;

            currentText = currentText.getPrevious();
            currentTextArea = currentText.getValue();
        }

        setVirtualSize(getViewportWidth(), cursor - ENTRY_SPACE);

        setScrollOffset(0, currentOffsetY, true);

        dirtyText = false;
    }
}
