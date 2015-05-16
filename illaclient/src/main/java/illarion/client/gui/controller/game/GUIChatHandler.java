/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.ElementBuilder.Align;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ScrollPanel;
import de.lessvoid.nifty.controls.ScrollPanel.AutoScroll;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMousePrimaryClickedEvent;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.IllaClient;
import illarion.client.graphics.Avatar;
import illarion.client.graphics.Camera;
import illarion.client.graphics.FontLoader;
import illarion.client.gui.ChatGui;
import illarion.client.net.client.IntroduceCmd;
import illarion.client.net.client.SayCmd;
import illarion.client.util.ChatHandler.SpeechMode;
import illarion.client.util.Lang;
import illarion.client.util.UpdateTask;
import illarion.client.util.translation.Translator;
import illarion.client.util.translation.TranslatorCallback;
import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.common.types.Rectangle;
import illarion.common.util.FastMath;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Font;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class takes care to receive Chat input from the GUI and sends it to the server. Also it receives Chat from the
 * server and takes care for displaying it on the GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GUIChatHandler implements ChatGui, KeyInputHandler, ScreenController, UpdatableHandler {
    @Override
    public void activateChatBox() {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                if (chatMsg != null) {
                    chatMsg.setFocus();
                }
            }
        });
    }

    @Override
    public void deactivateChatBox(final boolean clear) {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                if (chatMsg != null) {
                    if (chatMsg.hasFocus()) {
                        assert screen != null;
                        screen.getFocusHandler().setKeyFocus(null);
                    }
                    if (clear) {
                        chatMsg.setText("");
                    }
                }
            }
        });
    }

    @Override
    public boolean isChatBoxActive() {
        return (chatMsg != null) && chatMsg.hasFocus();
    }

    /**
     * Add a entry to the chat box.
     *
     * @param message the message to add to the chat box
     * @param color the color of the message
     */
    @Override
    public void addChatMessage(@Nonnull String message, @Nonnull Color color) {
        World.getUpdateTaskManager().addTask(new ChatBoxEntry(message, color));
    }

    /**
     * Display a chat bubble on the screen.
     *
     * @param character the character this chat message is assigned to
     * @param message the message that is displayed
     * @param color the color of the message
     */
    @Override
    public void showChatBubble(
            @Nullable Char character, @Nonnull String message, @Nonnull Color color) {
        World.getUpdateTaskManager().addTask(new CharTalkEntry(character, message, color));
    }

    /**
     * This utility class is used to store texts that get shown in the Chat log.
     */
    private class ChatBoxEntry implements UpdateTask {
        /**
         * The text of the entry.
         */
        @Nonnull
        private final String text;

        /**
         * The color of the entry.
         */
        @Nonnull
        private final Color color;

        /**
         * Constructor for the entry.
         *
         * @param msgText the text stored in the entry
         * @param msgColor the color of the entry
         */
        ChatBoxEntry(@Nonnull String msgText, @Nonnull Color msgColor) {
            text = msgText;
            color = msgColor;
        }

        @Nonnull
        protected Color getColor() {
            return color;
        }

        @Override
        public void onUpdateGame(@Nonnull GameContainer container, int delta) {
            addChatLogText(text, color);
        }
    }

    private class CharTalkEntry implements UpdateTask {
        @Nullable
        private final Char targetChar;
        /**
         * The text of the entry.
         */
        @Nonnull
        private final String text;

        /**
         * The color of the entry.
         */
        private final Color color;

        /**
         * Constructor for the entry.
         *
         * @param character the character that spoke this text
         * @param message the text stored in the entry
         * @param msgColor the color of the entry
         */
        CharTalkEntry(@Nullable Char character, @Nonnull String message, Color msgColor) {
            text = message;
            color = msgColor;
            targetChar = character;
        }

        @Override
        public void onUpdateGame(@Nonnull GameContainer container, int delta) {
            addMessageBubble(targetChar, text, color);
        }
    }

    /**
     * This pattern is used to clean the text before its send to the server.
     */
    @Nonnull
    private static final Pattern REPEATED_SPACE_PATTERN = Pattern.compile("\\s+");

    /**
     * The expanded height of the Chat.
     */
    @Nonnull
    private static final SizeValue CHAT_EXPANDED_HEIGHT = SizeValue.px(500);

    /**
     * The collapsed size of the Chat.
     */
    @Nonnull
    private static final SizeValue CHAT_COLLAPSED_HEIGHT = SizeValue.px(170);

    /**
     * The log that is used to display the text.
     */
    @Nullable
    private ScrollPanel chatLog;

    /**
     * The input field that holds the text that is yet to be send.
     */
    @Nullable
    private TextField chatMsg;

    /**
     * The layer used to show the Chat bubbles.
     */
    @Nullable
    private Element chatLayer;

    /**
     * The screen that displays the GUI.
     */
    @Nullable
    private Screen screen;

    /**
     * The nifty instance of this Chat handler.
     */
    @Nullable
    private Nifty nifty;

    /**
     * This flag shows of the Chat log is dirty and needs to be cleaned up.
     */
    private boolean dirty;

    /**
     * The pattern used to detect the introduce command.
     */
    private final Pattern introducePattern = Pattern.compile("^\\s*[/#]i(ntroduce)?\\s*$", Pattern.CASE_INSENSITIVE);

    /**
     * The pattern to detect a whispered message.
     */
    private final Pattern whisperPattern = Pattern
            .compile("^\\s*[/#]w(hisper)?\\s*(.*)\\s*$", Pattern.CASE_INSENSITIVE);

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

    @NiftyEventSubscriber(id = "expandTextLogBtn")
    public void onChatButtonClicked(String topic, ButtonClickedEvent data) {
        toggleChatLog();
    }

    /**
     * Change the expanded or collapsed state of the Chat.
     */
    private void toggleChatLog() {
        if (screen == null) {
            return;
        }

        if (isChatLogExpanded()) {
            setHeightOfChatLog(CHAT_COLLAPSED_HEIGHT);
        } else {
            setHeightOfChatLog(CHAT_EXPANDED_HEIGHT);
        }
    }

    private boolean isChatLogExpanded() {
        if (screen == null) {
            return false;
        }

        Element chatScroll = screen.findElementById("chatPanel");
        return (chatScroll != null) && chatScroll.getConstraintHeight().equals(CHAT_EXPANDED_HEIGHT);
    }

    private void setHeightOfChatLog(@Nonnull SizeValue value) {
        if (screen == null) {
            return;
        }
        Element chatScroll = screen.findElementById("chatPanel");
        if (chatScroll == null) {
            return;
        }
        chatScroll.setConstraintHeight(value);
        chatScroll.getParent().setConstraintHeight(SizeValue.def());
        chatScroll.getParent().getParent().setConstraintHeight(SizeValue.def());
        chatScroll.getParent().getParent().getParent().layoutElements();
        ScrollPanel scrollPanel = chatScroll.getNiftyControl(ScrollPanel.class);
        if (scrollPanel != null) {
            scrollPanel.setAutoScroll(AutoScroll.BOTTOM);
            scrollPanel.setAutoScroll(AutoScroll.OFF);
        }
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.screen = screen;
        this.nifty = nifty;

        chatMsg = screen.findNiftyControl("chatMsg", TextField.class);
        chatLog = screen.findNiftyControl("chatPanel", ScrollPanel.class);

        chatMsg.getElement().addInputHandler(this);

        chatLayer = screen.findElementById("chatLayer");
    }

    /**
     * Receive a Input event from the GUI and send a text in case this event applies.
     */
    @Override
    public boolean keyEvent(@Nonnull NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyStandardInputEvent.SubmitText) {
            assert chatMsg != null;
            if (chatMsg.hasFocus()) {
                if (chatMsg.getDisplayedText().isEmpty()) {
                    assert screen != null;
                    screen.getFocusHandler().setKeyFocus(null);
                } else {
                    sendText(chatMsg.getDisplayedText());
                    chatMsg.setText("");
                    if (IllaClient.getCfg().getBoolean("disableChatAfterSending")) {
                        assert screen != null;
                        screen.getFocusHandler().setKeyFocus(null);
                    }
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
    private void sendText(@Nonnull String text) {
        if (introducePattern.matcher(text).matches()) {
            World.getNet().sendCommand(new IntroduceCmd());
            return;
        }

        Matcher shoutMatcher = shoutPattern.matcher(text);
        if (shoutMatcher.find()) {
            cleanAndSendText("", shoutMatcher.group(2), SpeechMode.Shout);
            return;
        }

        Matcher whisperMatcher = whisperPattern.matcher(text);
        if (whisperMatcher.find()) {
            cleanAndSendText("", whisperMatcher.group(2), SpeechMode.Whisper);
            return;
        }

        Matcher emoteMatcher = emotePattern.matcher(text);
        if (emoteMatcher.find()) {
            String cleanMe = REPEATED_SPACE_PATTERN.matcher(emoteMatcher.group(1)).replaceAll(" ").toLowerCase();
            cleanAndSendText('#' + cleanMe, emoteMatcher.group(2), SpeechMode.Normal);
            return;
        }

        Matcher oocMatcher = oocPattern.matcher(text);
        if (oocMatcher.find()) {
            cleanAndSendText("#o ", oocMatcher.group(2), SpeechMode.Whisper);
            return;
        }

        cleanAndSendText("", text, SpeechMode.Normal);
    }

    /**
     * Cleanup the text and send it in case anything remains to send.
     *
     * @param prefix the prefix that is prepend to the text
     * @param text the text to send
     * @param mode the speech mode used to send the command
     */
    private static void cleanAndSendText(
            String prefix, @Nonnull String text, @Nonnull SpeechMode mode) {
        String cleanText = REPEATED_SPACE_PATTERN.matcher(text.trim()).replaceAll(" ");
        if (cleanText.isEmpty()) {
            return;
        }

        World.getNet().sendCommand(new SayCmd(mode, prefix + text));
    }

    @Override
    public void onEndScreen() {
        assert nifty != null;
        nifty.unsubscribeAnnotations(this);
        AnnotationProcessor.unprocess(this);

        clearChatLog();
        clearChatBubbles();
    }

    @Override
    public void onStartScreen() {
        setHeightOfChatLog(CHAT_COLLAPSED_HEIGHT);
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                keyEvent(NiftyStandardInputEvent.SubmitText);
                keyEvent(NiftyStandardInputEvent.SubmitText);
            }
        });
        AnnotationProcessor.process(this);
        assert nifty != null;
        nifty.subscribeAnnotations(this);
    }

    @Override
    public void update(GameContainer container, int delta) {
        cleanupChatLog();
        updateChatBubbleLocations();
    }

    private void clearChatLog() {
        if (chatLog == null) {
            return;
        }
        Element chatLogElement = chatLog.getElement();
        if (chatLogElement != null) {
            Element contentPane = chatLogElement.findElementById("chatLog");
            if (contentPane != null) {
                int entryCount = contentPane.getChildren().size();
                for (int i = 0; i < entryCount; i++) {
                    contentPane.getChildren().get(i).markForRemoval();
                }
            }
        }
    }

    private void clearChatBubbles() {
        for (Element element : activeBubbles.values()) {
            element.markForRemoval();
        }
        activeBubbles.clear();
    }

    /**
     * Remove all entries that do not belong in the list anymore from it. Also update the layout and the scrolling
     * position.
     */
    private void cleanupChatLog() {
        if (!dirty || (chatLog == null)) {
            return;
        }

        dirty = false;

        Element contentPane = chatLog.getElement().findElementById("chatLog");

        int entryCount = contentPane.getChildren().size();
        for (int i = 0; i < (entryCount - 400); i++) {
            Element elementToRemove = contentPane.getChildren().get(i);
            if (i == (entryCount - 401)) {
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

        contentPane.setConstraintHeight(SizeValue.def());
        chatLog.getElement().layoutElements();
        chatLog.setAutoScroll(AutoScroll.BOTTOM);
        chatLog.setAutoScroll(AutoScroll.OFF);
    }

    private void updateChatBubbleLocations() {
        if (chatLayer == null) {
            return;
        }

        boolean layoutRequired = false;
        for (Entry<Char, Element> charBubbleEntry : activeBubbles.entrySet()) {
            if (updateChatBubbleLocation(charBubbleEntry.getKey(), charBubbleEntry.getValue())) {
                layoutRequired = true;
            }
        }
        if (cleanOverlappingBubbles()) {
            layoutRequired = true;
        }
        if (layoutRequired) {
            chatLayer.layoutElements();
        }
    }

    @Nonnull
    private final AtomicLong chatLineCounter = new AtomicLong(0L);

    /**
     * Add a entry to the Chat log.
     *
     * @param text the text to add
     * @param color the color of the text to add
     */
    private void addChatLogText(@Nonnull String text, @Nonnull Color color) {
        if (chatLog == null) {
            return;
        }
        Element contentPane = chatLog.getElement().findElementById("chatLog");

        long index = chatLineCounter.getAndIncrement();

        LabelBuilder label = new LabelBuilder();
        label.id("chatLog#chatLine-" + index);
        label.font("chatFont");
        label.text(text);
        label.color(color);
        label.textHAlign(Align.Left);
        label.wrap(true);
        label.width(contentPane.getConstraintWidth().toString());
        label.visibleToMouse(true);
        label.build(nifty, screen, contentPane);

        LabelBuilder translationLabel = new LabelBuilder();
        translationLabel.id("chatLog#transChatLine-" + index);
        translationLabel.font("chatFont");
        translationLabel.text("");
        translationLabel.color(color);
        translationLabel.textHAlign(Align.Left);
        translationLabel.wrap(true);
        translationLabel.visible(false);
        translationLabel.height(SizeValue.px(0));
        translationLabel.width(contentPane.getConstraintWidth().toString());
        Element translationElement = translationLabel.build(nifty, screen, contentPane);
        translationElement.setConstraintHeight(SizeValue.px(0));

        dirty = true;
    }

    @Nonnull
    private final Translator translator = new Translator();

    @NiftyEventSubscriber(pattern = "chatLog#chatLine-[0-9]+")
    public void onChatLineDoubleClick(@Nonnull String id, @Nonnull NiftyMousePrimaryClickedEvent event) {
        if ((screen == null) || !translator.isServiceEnabled() || (chatLog == null)) {
            return;
        }

        final Element panel = chatLog.getElement().findElementById("chatLog");
        if (panel == null) {
            return;
        }

        String strId = id.substring("chatLog#chatLine-".length());
        String translateId = "chatLog#transChatLine-" + strId;

        Element sourceElement = panel.findElementById(id);
        Label sourceLabel = (sourceElement != null) ? sourceElement.getNiftyControl(Label.class) : null;

        final Element translationElement = panel.findElementById(translateId);
        final Label translationLabel = (translationElement != null) ? translationElement.getNiftyControl(Label.class) :
                null;

        if ((sourceLabel == null) || (sourceLabel.getText() == null) || (translationLabel == null)) {
            return;
        }

        if ((translationLabel.getText() == null) || translationLabel.getText().isEmpty()) {
            translationElement.setConstraintHeight(SizeValue.def());
            translationLabel.setText(Lang.getMsg("chat.translating"));
            translationElement.setVisible(true);
            sourceElement.setVisibleToMouseEvents(false);
            dirty = true;
            translator.translate(sourceLabel.getText(), new TranslatorCallback() {
                @Override
                public void sendTranslation(@Nullable final String translation) {
                    World.getUpdateTaskManager().addTask(new UpdateTask() {
                        @Override
                        public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                            if (translation == null) {
                                translationLabel.setText("");
                                translationElement.setVisible(false);
                                translationElement.setConstraintHeight(SizeValue.px(0));
                            } else {
                                translationElement.setConstraintHeight(SizeValue.def());
                                translationLabel.setText(translation);
                            }
                            dirty = true;
                        }
                    });
                }
            });
        }
    }

    private final Map<Char, Element> activeBubbles = new HashMap<>();

    /**
     * The the Chat bubble of a character talking on the map.
     *
     * @param character the character who is talking
     * @param message the message to display
     * @param color the color to show the text in
     */
    private void addMessageBubble(@Nullable final Char character, @Nonnull String message, @Nonnull Color color) {
        if ((character == null) || (chatLayer == null)) {
            return;
        }

        @Nullable Element oldBubble = activeBubbles.remove(character);
        if (oldBubble != null) {
            nifty.removeElement(screen, oldBubble);
        }

        LabelBuilder labelBuilder = new LabelBuilder();
        labelBuilder.style("nifty-label");

        Font font = FontLoader.getInstance().getFont(FontLoader.BUBBLE_FONT);
        int textWidth = font.getWidth(message);
        if (textWidth > 300) {
            labelBuilder.width("300px");
            labelBuilder.wrap(true);
        } else {
            labelBuilder.width(SizeValue.px(textWidth).toString());
            labelBuilder.wrap(false);
        }
        labelBuilder.font(FontLoader.BUBBLE_FONT);
        labelBuilder.color(color);
        labelBuilder.text(message);

        EffectBuilder hideEffectBuilder = new EffectBuilder("fade");
        hideEffectBuilder.startDelay(3500 + (message.length() * 50));
        hideEffectBuilder.length(200);
        hideEffectBuilder.effectParameter("start", "FF");
        hideEffectBuilder.effectParameter("end", "00");

        labelBuilder.onHideEffect(hideEffectBuilder);

        final Element bubble = labelBuilder.build(nifty, screen, chatLayer);

        if (updateChatBubbleLocation(character, bubble)) {
            chatLayer.layoutElements();
        }

        bubble.hide(new EndNotify() {
            @Override
            public void perform() {
                nifty.removeElement(screen, bubble);
                activeBubbles.remove(character);
            }
        });
        activeBubbles.put(character, bubble);
    }

    private boolean updateChatBubbleLocation(@Nonnull Char character, @Nonnull Element bubble) {
        if (chatLayer == null) {
            return false;
        }
        Avatar charAvatar = character.getAvatar();
        if (charAvatar == null) {
            return false;
        }

        Rectangle charDisplayRect = charAvatar.getDisplayRect();
        if (charDisplayRect.isEmpty()) {
            return false;
        }

        int charDisplayCenterX = charDisplayRect.getCenterX() - Camera.getInstance().getViewportOffsetX();
        int charDisplayY = charDisplayRect.getBottom() - Camera.getInstance().getViewportOffsetY();

        int bubblePosX = FastMath
                .clamp(charDisplayCenterX - (bubble.getWidth() / 2), 0, chatLayer.getWidth() - bubble.getWidth());
        int bubblePosY = FastMath
                .clamp(charDisplayY - bubble.getHeight() - 5, 0, chatLayer.getHeight() - bubble.getHeight());

        SizeValue newConstraintX = SizeValue.px(bubblePosX);
        SizeValue newConstraintY = SizeValue.px(bubblePosY);
        if (!bubble.getConstraintX().equals(newConstraintX) || !bubble.getConstraintY().equals(newConstraintY)) {
            bubble.setConstraintX(newConstraintX);
            bubble.setConstraintY(newConstraintY);
            return true;
        }
        return false;
    }

    private boolean cleanOverlappingBubbles() {
        if ((chatLayer == null) || (nifty == null) || (screen == null)) {
            return false;
        }

        int elementCount = chatLayer.getChildrenCount();
        if (elementCount <= 1) {
            return false;
        }

        Collection<Element> elementsToRemove = new ArrayList<>();
        Collection<Rectangle> coveredAreas = new ArrayList<>();
        for (int i = elementCount - 1; i >= 0; i--) {
            Element child = chatLayer.getChildren().get(i);
            Rectangle elementArea = new Rectangle(child.getConstraintX().getValueAsInt(1.f),
                                                  child.getConstraintY().getValueAsInt(1.f), child.getWidth(),
                                                  child.getHeight());

            boolean childWillBeRemoved = false;
            for (@Nonnull Rectangle coveredArea : coveredAreas) {
                if (coveredArea.intersects(elementArea)) {
                    elementsToRemove.add(child);
                    childWillBeRemoved = true;
                    break;
                }
            }

            if (!childWillBeRemoved) {
                coveredAreas.add(elementArea);
            }
        }

        for (@Nonnull Element elementToRemove : elementsToRemove) {
            nifty.removeElement(screen, elementToRemove);
        }

        return !elementsToRemove.isEmpty();
    }
}
