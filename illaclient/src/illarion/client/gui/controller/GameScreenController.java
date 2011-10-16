package illarion.client.gui.controller;

import javolution.text.TextBuilder;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.SayCmd;
import illarion.client.util.ChatHandler;
import illarion.client.util.Lang;
import illarion.client.util.ChatHandler.ChatReceiver;
import illarion.client.util.ChatHandler.SpeechMode;
import illarion.client.world.Char;
import illarion.client.world.World;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class GameScreenController implements ScreenController,
    KeyInputHandler, ChatReceiver {

    private Screen screen;
    private Label main;
    private TextField chatMsg;
    private ListBox<String> chatLog;

    @SuppressWarnings("unchecked")
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.screen = screen;
        chatMsg = screen.findNiftyControl("chatMsg", TextField.class);
        chatMsg.getElement().addInputHandler(this);
        chatLog =
            (ListBox<String>) screen
                .findNiftyControl("chatLog", ListBox.class);
        
        screen.findElementByName("chatLog#scrollpanel#panel").setFocusable(false);
        screen.findElementByName("chatLog#scrollpanel#vertical-scrollbar").setFocusable(false);
    }

    @Override
    public void onStartScreen() {
        World.getChatHandler().addChatReceiver(this);
    }

    @Override
    public void onEndScreen() {
        World.getChatHandler().removeChatReceiver(this);
    }

    @Override
    public boolean keyEvent(NiftyInputEvent inputEvent) {
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

    @Override
    public void handleText(String text, Char chara, SpeechMode talkMode) {
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

        chatLog.addItem(textBuilder.toString());
        chatLog.showItemByIndex(chatLog.itemCount() - 1);
        // send out the text
        TextBuilder.recycle(textBuilder);
    }

}
