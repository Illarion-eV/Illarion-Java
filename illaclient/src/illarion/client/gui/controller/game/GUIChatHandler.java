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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.ElementBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.ScrollPanel;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.slick2d.render.font.SlickRenderFont;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.graphics.Avatar;
import illarion.client.graphics.Camera;
import illarion.client.graphics.FontLoader;
import illarion.client.input.InputReceiver;
import illarion.client.net.client.IntroduceCmd;
import illarion.client.net.client.SayCmd;
import illarion.client.net.server.events.BroadcastInformReceivedEvent;
import illarion.client.net.server.events.ScriptInformReceivedEvent;
import illarion.client.net.server.events.TextToInformReceivedEvent;
import illarion.client.util.ChatHandler;
import illarion.client.util.Lang;
import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.client.world.events.CharTalkingEvent;
import illarion.common.types.Rectangle;
import illarion.common.util.FastMath;
import javolution.text.TextBuilder;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.newdawn.slick.GameContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class takes care to receive chat input from the GUI and sends it to the server. Also it receives chat from the
 * server and takes care for displaying it on the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GUIChatHandler implements KeyInputHandler, ScreenController, UpdatableHandler {
    /**
     * This utility class is used to store texts that get shown in the chat log.
     */
    private class ChatBoxEntry implements Runnable {
        /**
         * The text of the entry.
         */
        private final String text;

        /**
         * The color of the entry.
         */
        private final Color color;

        /**
         * Constructor for the entry.
         *
         * @param msgText  the text stored in the entry
         * @param msgColor the color of the entry
         */
        ChatBoxEntry(final String msgText, final Color msgColor) {
            text = msgText;
            color = msgColor;
        }

        @Override
        public void run() {
            addChatLogText(text, color);
        }

        protected Color getColor() {
            return color;
        }
    }

    private class CharTalkEntry extends ChatBoxEntry {
        @Nonnull
        private final CharTalkingEvent talkingEvent;

        /**
         * Constructor for the entry.
         *
         * @param event    the event data
         * @param msgColor the color of the entry
         */
        CharTalkEntry(@Nonnull final CharTalkingEvent event, final Color msgColor) {
            super(event.getLoggedText(), msgColor);
            talkingEvent = event;
        }

        @Override
        public void run() {
            super.run();
            final String message;
            if (talkingEvent.getMode() == ChatHandler.SpeechMode.emote) {
                final Char talkingChar = talkingEvent.getCharacter();
                if (talkingChar == null) {
                    message = Lang.getMsg("chat.someone") + talkingEvent.getText();
                } else {
                    message = talkingChar.getName() + talkingEvent.getText();
                }
            } else if (talkingEvent.getMode() == ChatHandler.SpeechMode.ooc) {
                message = "((" + talkingEvent.getText() + "))";
            } else {
                message = talkingEvent.getText();
            }
            addMessageBubble(talkingEvent.getCharacter(), message, getColor());
        }
    }

    /**
     * This pattern is used to clean the text before its send to the server.
     */
    private static final Pattern REPEATED_SPACE_PATTERN = Pattern.compile("\\s+");

    /**
     * The default color of text entries.
     */
    private static final Color COLOR_DEFAULT = new Color("#ffffff");

    /**
     * The color of shouted or important messages
     */
    private static final Color COLOR_SHOUT = new Color("#ff0000");

    /**
     * The color of whispered text.
     */
    private static final Color COLOR_WHISPER = new Color("#c0c0c0");

    /**
     * The color of emoted.
     */
    private static final Color COLOR_EMOTE = new Color("#ffcc33");

    /**
     * The expanded height of the chat.
     */
    private static final SizeValue CHAT_EXPANDED_HEIGHT = SizeValue.px(500);

    /**
     * The collapsed size of the chat.
     */
    private static final SizeValue CHAT_COLLAPSED_HEIGHT = SizeValue.px(170);

    /**
     * The log that is used to display the text.
     */
    private ScrollPanel chatLog;

    /**
     * The input field that holds the text that is yet to be send.
     */
    private TextField chatMsg;

    /**
     * The layer used to show the chat bubbles.
     */
    private Element chatLayer;

    /**
     * The screen that displays the GUI.
     */
    private Screen screen;

    /**
     * The nifty instance of this chat handler.
     */
    private Nifty nifty;

    /**
     * The Queue of strings that yet need to be written to the GUI.
     */
    @Nonnull
    private final Queue<Runnable> messageQueue;

    /**
     * This flag shows of the chat log is dirty and needs to be cleaned up.
     */
    private boolean dirty;

    /**
     * The pattern used to detect the introduce command.
     */
    private final Pattern introducePattern = Pattern.compile("^\\s*[/#]i(ntroduce)?\\s*$", Pattern.CASE_INSENSITIVE);

    /**
     * The pattern to detect a whispered message.
     */
    private final Pattern whisperPattern = Pattern.compile("^\\s*[/#]w(hisper)?\\s*(.*)\\s*$", Pattern.CASE_INSENSITIVE);

    /**
     * The pattern to detect a shouted message.
     */
    private final Pattern shoutPattern = Pattern.compile("^\\s*[/#]s(hout)?\\s*(.*)\\s*$", Pattern.CASE_INSENSITIVE);

    /**
     * The pattern to detect a emote.
     */
    private final Pattern emotePattern = Pattern.compile("^\\s*[/#](me\\s*)(.*)\\s*$", Pattern.CASE_INSENSITIVE);

    /**
     * The pattern to detect a ooc.
     */
    private final Pattern oocPattern = Pattern.compile("^\\s*[/#]o(oc)?\\s*(.*)\\s*$", Pattern.CASE_INSENSITIVE);

    /**
     * The default constructor.
     */
    public GUIChatHandler() {
        messageQueue = new ConcurrentLinkedQueue<Runnable>();
    }

    @EventSubscriber
    public void onBroadcastInformReceived(@Nonnull final BroadcastInformReceivedEvent data) {
        final TextBuilder textBuilder = TextBuilder.newInstance();
        try {
            textBuilder.append(Lang.getMsg("chat.broadcast"));
            textBuilder.append(": ");
            textBuilder.append(data.getMessage());

            messageQueue.offer(new ChatBoxEntry(textBuilder.toString(), COLOR_DEFAULT));
        } finally {
            TextBuilder.recycle(textBuilder);
        }
    }

    @EventSubscriber
    public void onCharTalkingEvent(@Nonnull final CharTalkingEvent data) {
        Color usedColor = null;
        switch (data.getMode()) {
            case emote:
                usedColor = COLOR_EMOTE;
                break;
            case normal:
                usedColor = COLOR_DEFAULT;
                break;
            case ooc:
                usedColor = COLOR_WHISPER;
                break;
            case shout:
                usedColor = COLOR_SHOUT;
                break;
            case whisper:
                usedColor = COLOR_WHISPER;
                break;
        }

        messageQueue.offer(new CharTalkEntry(data, usedColor));
    }

    @EventSubscriber
    public void onScriptInformReceived(@Nonnull final ScriptInformReceivedEvent data) {
        if (data.getInformPriority() == 0) {
            return;
        }

        final TextBuilder textBuilder = TextBuilder.newInstance();
        try {
            final Color usedColor;
            if (data.getInformPriority() == 1) {
                usedColor = COLOR_DEFAULT;
            } else {
                usedColor = COLOR_SHOUT;
            }


            textBuilder.append(Lang.getMsg("chat.scriptInform"));
            textBuilder.append(": ");
            textBuilder.append(data.getMessage());

            messageQueue.offer(new ChatBoxEntry(textBuilder.toString(), usedColor));
        } finally {
            TextBuilder.recycle(textBuilder);
        }
    }

    @EventSubscriber
    public void onTextToInformReceived(@Nonnull final TextToInformReceivedEvent data) {
        final TextBuilder textBuilder = TextBuilder.newInstance();
        try {
            textBuilder.append(Lang.getMsg("chat.textto"));
            textBuilder.append(": ");
            textBuilder.append(data.getMessage());

            messageQueue.offer(new ChatBoxEntry(textBuilder.toString(), COLOR_DEFAULT));
        } finally {
            TextBuilder.recycle(textBuilder);
        }
    }

    @EventTopicSubscriber(topic = InputReceiver.EB_TOPIC)
    public void onInputEventReceived(@Nonnull final String topic, final String event) {
        if (topic.equals(InputReceiver.EB_TOPIC)) {
            if ("SelectChat".equals(event)) {
                chatMsg.setFocus();
            }
        }
    }

    @NiftyEventSubscriber(id = "expandTextLogBtn")
    public void onChatButtonClicked(final String topic, final ButtonClickedEvent data) {
        toggleChatLog();
    }

    /**
     * Change the expanded or collapsed state of the chat.
     */
    private void toggleChatLog() {
        final Element chatScroll = screen.findElementByName("chatPanel");

        if (chatScroll.getConstraintHeight().equals(CHAT_COLLAPSED_HEIGHT)) {
            chatScroll.setConstraintHeight(CHAT_EXPANDED_HEIGHT);
        } else {
            chatScroll.setConstraintHeight(CHAT_COLLAPSED_HEIGHT);
        }
        screen.findElementByName("mainLayer").layoutElements();
        chatScroll.getNiftyControl(ScrollPanel.class).setAutoScroll(ScrollPanel.AutoScroll.BOTTOM);
        chatScroll.getNiftyControl(ScrollPanel.class).setAutoScroll(ScrollPanel.AutoScroll.OFF);
    }

    @Override
    public void bind(final Nifty nifty, @Nonnull final Screen screen) {
        this.screen = screen;
        this.nifty = nifty;

        chatMsg = screen.findNiftyControl("chatMsg", TextField.class);
        chatLog = screen.findNiftyControl("chatPanel", ScrollPanel.class);

        chatMsg.getElement().addInputHandler(this);

        chatLayer = screen.findElementByName("chatLayer");
    }

    /**
     * Receive a Input event from the GUI and send a text in case this event applies.
     */
    @Override
    public boolean keyEvent(final NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyStandardInputEvent.SubmitText) {
            if (chatMsg.hasFocus()) {
                if (chatMsg.getDisplayedText().isEmpty()) {
                    screen.getFocusHandler().setKeyFocus(null);
                } else {
                    sendText(chatMsg.getDisplayedText());
                    chatMsg.setText("");
                }
            } else {
                chatMsg.setFocus();
            }

            return true;
        }
        return false;
    }

    /**
     * Send the text as talking text to the server.
     *
     * @param text the text to send
     */
    private void sendText(@Nonnull final String text) {
        if (introducePattern.matcher(text).matches()) {
            World.getNet().sendCommand(new IntroduceCmd());
            return;
        }

        final Matcher shoutMatcher = shoutPattern.matcher(text);
        if (shoutMatcher.find()) {
            cleanAndSendText("", shoutMatcher.group(2), ChatHandler.SpeechMode.shout);
            return;
        }

        final Matcher whisperMatcher = whisperPattern.matcher(text);
        if (whisperMatcher.find()) {
            cleanAndSendText("", whisperMatcher.group(2), ChatHandler.SpeechMode.whisper);
            return;
        }

        final Matcher emoteMatcher = emotePattern.matcher(text);
        if (emoteMatcher.find()) {
            final String cleanMe = REPEATED_SPACE_PATTERN.matcher(emoteMatcher.group(1)).replaceAll(" ").toLowerCase();
            cleanAndSendText("#" + cleanMe, emoteMatcher.group(2), ChatHandler.SpeechMode.normal);
            return;
        }

        final Matcher oocMatcher = oocPattern.matcher(text);
        if (oocMatcher.find()) {
            cleanAndSendText("#o ", oocMatcher.group(2), ChatHandler.SpeechMode.whisper);
            return;
        }

        cleanAndSendText("", text, ChatHandler.SpeechMode.normal);
    }

    /**
     * Cleanup the text and send it in case anything remains to send.
     *
     * @param prefix the prefix that is prepend to the text
     * @param text   the text to send
     * @param mode   the speech mode used to send the command
     */
    private static void cleanAndSendText(final String prefix, @Nonnull final String text, @Nonnull final ChatHandler.SpeechMode mode) {
        final String cleanText = REPEATED_SPACE_PATTERN.matcher(text.trim()).replaceAll(" ");
        if (cleanText.isEmpty()) {
            return;
        }

        World.getNet().sendCommand(new SayCmd(mode, prefix + text));
    }

    @Override
    public void onEndScreen() {
        nifty.unsubscribeAnnotations(this);
        AnnotationProcessor.unprocess(this);
    }

    @Override
    public void onStartScreen() {
        toggleChatLog();
        messageQueue.add(new Runnable() {
            @Override
            public void run() {
                keyEvent(NiftyStandardInputEvent.SubmitText);
                keyEvent(NiftyStandardInputEvent.SubmitText);
            }
        });
        AnnotationProcessor.process(this);
        nifty.subscribeAnnotations(this);
    }

    @Override
    public void update(final GameContainer container, final int delta) {
        while (true) {
            final Runnable task = messageQueue.poll();
            if (task == null) {
                break;
            }

            task.run();
        }

        cleanupChatLog();
    }

    /**
     * Remove all entries that do not belong in the list anymore from it. Also update the layout and the scrolling
     * position.
     */
    private void cleanupChatLog() {
        if (!dirty) {
            return;
        }

        dirty = false;

        final Element contentPane = chatLog.getElement().findElementByName("chatLog");

        final int entryCount = contentPane.getElements().size();
        for (int i = 0; i < (entryCount - 200); i++) {
            final Element elementToRemove = contentPane.getElements().get(i);
            if (i == (entryCount - 201)) {
                elementToRemove.markForRemoval(new EndNotify() {
                    @Override
                    public void perform() {
                        chatLog.getElement().layoutElements();
                    }
                });
            } else {
                elementToRemove.markForRemoval();
            }
        }

        chatLog.getElement().layoutElements();
        chatLog.setAutoScroll(ScrollPanel.AutoScroll.BOTTOM);
        chatLog.setAutoScroll(ScrollPanel.AutoScroll.OFF);
    }

    /**
     * Add a entry to the chat log.
     *
     * @param text  the text to add
     * @param color the color of the text to add
     */
    private void addChatLogText(final String text, final Color color) {
        final Element contentPane = chatLog.getElement().findElementByName("chatLog");

        final LabelBuilder label = new LabelBuilder();
        label.font("chatFont");
        label.text(text);
        label.color(color);
        label.textHAlign(ElementBuilder.Align.Left);
        label.wrap(true);
        label.width(contentPane.getConstraintWidth().toString());
        label.build(contentPane.getNifty(), screen, contentPane);

        dirty = true;
    }

    /**
     * The the chat bubble of a character talking on the map.
     *
     * @param character the character who is talking
     * @param message   the message to display
     * @param color     the color to show the text in
     */
    private void addMessageBubble(@Nullable final Char character, @Nonnull final String message, final Color color) {
        if (character == null) {
            return;
        }
        final Avatar charAvatar = character.getAvatar();
        if (charAvatar == null) {
            return;
        }
        final Rectangle charDisplayRect = charAvatar.getDisplayRect();

        final PanelBuilder panelBuilder = new PanelBuilder();
        panelBuilder.childLayoutHorizontal();
        panelBuilder.style("nifty-panel-hint");

        final LabelBuilder labelBuilder = new LabelBuilder();
        labelBuilder.style("nifty-label");

        final SlickRenderFont font = FontLoader.getInstance().getFontSave(FontLoader.Fonts.text);
        final int textWidth = font.getWidth(message);
        if (textWidth > 300) {
            labelBuilder.width("300px");
            labelBuilder.wrap(true);
        } else {
            labelBuilder.width(SizeValue.px(textWidth).toString());
            labelBuilder.wrap(false);
        }
        labelBuilder.font(FontLoader.Fonts.text.getFontName());
        labelBuilder.color(color);
        labelBuilder.text(message);

        panelBuilder.control(labelBuilder);

        final EffectBuilder hideEffectBuilder = new EffectBuilder("fade");
        hideEffectBuilder.startDelay(5000 + (message.length() * 100));
        hideEffectBuilder.length(200);
        hideEffectBuilder.effectParameter("start", "FF");
        hideEffectBuilder.effectParameter("end", "00");

        panelBuilder.onHideEffect(hideEffectBuilder);

        final Element bubble = panelBuilder.build(nifty, screen, chatLayer);

        final int charDisplayCenterX = charDisplayRect.getCenterX() - Camera.getInstance().getViewportOffsetX();
        final int charDisplayY = charDisplayRect.getBottom() - Camera.getInstance().getViewportOffsetY();

        final int bubblePosX = FastMath.clamp(charDisplayCenterX - (bubble.getWidth() / 2), 0,
                chatLayer.getWidth() - bubble.getWidth());
        final int bubblePosY = FastMath.clamp(charDisplayY - bubble.getHeight() - 5, 0,
                chatLayer.getHeight() - bubble.getHeight());

        bubble.setConstraintX(SizeValue.px(bubblePosX));
        bubble.setConstraintY(SizeValue.px(bubblePosY));

        chatLayer.layoutElements();

        bubble.hide();
    }
}
