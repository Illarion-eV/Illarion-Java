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
package illarion.client.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ScrollPanel;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import illarion.client.input.InputReceiver;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.SayCmd;
import illarion.client.util.ChatHandler;
import illarion.client.util.ChatHandler.ChatReceiver;
import illarion.client.util.ChatHandler.SpeechMode;
import illarion.client.util.Lang;
import illarion.client.world.Char;
import javolution.text.TextBuilder;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventTopicSubscriber;

/**
 * This class takes care to receive chat input from the GUI and sends it to the
 * server. Also it receives chat from the server and takes care for displaying
 * it on the GUI.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GUIChatHandler implements KeyInputHandler, ChatReceiver, EventTopicSubscriber<String> {
    /**
     * The log that is used to display the text.
     */
    private final ScrollPanel chatLog;
    
    /**
     * The input field that holds the text that is yet to be send.
     */
    private final TextField chatMsg;
    
    /**
     * The screen that displays the GUI.
     */
    private final Screen screen;
    
    /**
     * Create a new GUI chat handler and set the required references.
     * 
     * @param screen the screen that displays the GUI
     * @param chatLog the log that displays written text
     * @param chatMsg the input field holding the text to be send
     */
    public GUIChatHandler(final Screen screen, final ScrollPanel chatLog, final TextField chatMsg) {
        this.screen = screen;
        this.chatMsg = chatMsg;
        this.chatLog = chatLog;
        
        EventBus.subscribe(InputReceiver.EB_TOPIC, this);
    }

    /**
     * Receive a Input event from the GUI and send a text in case this event
     * applies.
     */
    @Override
    public boolean keyEvent(final NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyInputEvent.SubmitText) {
            if (chatMsg.hasFocus()) {
                if (chatMsg.getText().length() == 0) {
                    screen.getFocusHandler().setKeyFocus(null);
                } else {
                    sendText(chatMsg.getText());
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
    private void sendText(final String text) {
        final SayCmd cmd =
            CommandFactory.getInstance().getCommand(CommandList.CMD_SAY,
                SayCmd.class);
        cmd.setText(text);
        cmd.send();
    }
    
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
     * Receive text send from the server and display it in the chat log.
     */
    @Override
    public void handleText(final String text, final Char chara, final SpeechMode talkMode) {
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
            textBuilder.append(text);
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
            textBuilder.append(text);
        }
        Element contentPane = chatLog.getElement().findElementByName("chatLog");
        LabelBuilder label = new LabelBuilder() {{
            text(textBuilder.toString());
            textHAlign(Align.Left);
            parameter("wrap", "true");
            width(percentage(100));
        }};
        label.build(contentPane.getNifty(), screen, contentPane);

        TextBuilder.recycle(textBuilder);
    }

    /**
     * Handle the events this handler subscribed to.
     * 
     * @param topic the event topic
     * @param data the data that was delivered along with this event
     */
    @Override
    public void onEvent(final String topic, final String data) {
        if (topic.equals(InputReceiver.EB_TOPIC)) {
            if (data.equals("SelectChat")) {
                chatMsg.setFocus();
            }
        }
    }
}
