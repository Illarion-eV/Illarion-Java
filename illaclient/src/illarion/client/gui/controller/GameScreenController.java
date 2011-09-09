package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class GameScreenController implements ScreenController, KeyInputHandler {

	private Screen screen;
	private Label main;
	private TextField chatMsg;
	private ListBox<String> chatLog;
	
	@SuppressWarnings("unchecked")
    @Override
    public void bind(Nifty nifty, Screen screen) {
    	this.screen = screen;
    	main = screen.findNiftyControl("main", Label.class);
    	main.setFocusable(true);
    	main.setFocus();
    	chatMsg = screen.findNiftyControl("chatMsg", TextField.class);
    	chatMsg.getElement().addInputHandler(this);
    	chatLog = (ListBox<String>) screen.findNiftyControl("chatLog", ListBox.class);
    }

    @Override
    public void onStartScreen() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEndScreen() {
        // TODO Auto-generated method stub

    }

	@Override
	public boolean keyEvent(NiftyInputEvent inputEvent) {
		if (inputEvent == NiftyInputEvent.SubmitText) {
			if (chatMsg.hasFocus()) {
				if (chatMsg.getText().length() == 0) {
					main.setFocus();
				} else {
					chatMsg.setText("");
					sendText(chatMsg.getText());
				}
			} else {
				chatMsg.setFocus();
			}
            
            return true;
        }
        return false;
	}
	
	private void sendText(String text) {
		
	}

}
