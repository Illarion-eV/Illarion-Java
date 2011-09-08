package illarion.client.gui.controller;

import illarion.client.Login;
import illarion.common.util.LoadingManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public final class LoginScreenController implements ScreenController {

	private Nifty nifty;
    private Screen screen;
    
	private TextField nameTxt;
	private TextField passwordTxt;
	private Button loginBtn;
	private ListBox<?> charList;
//	private Label errorText;
//	
//	private Element popupLogin;
//    private Element popupError;
	
    @Override
    public void bind(Nifty nifty, Screen screen) {
    	this.nifty = nifty;
    	this.screen = screen;
    	
    	nameTxt = screen.findNiftyControl("nameTxt", TextField.class);
    	passwordTxt = screen.findNiftyControl("passwordTxt", TextField.class);
    	loginBtn = screen.findNiftyControl("loginBtn", Button.class);
    	charList = nifty.getScreen("charSelect").findNiftyControl("myListBox", ListBox.class);
//    	errorText = screen.findNiftyControl("errorText", Label.class);
//    	
//    	popupLogin = screen.findElementByName("popupLoggingIn");
//        popupError = screen.findElementByName("error");
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public void onEndScreen() {

    }
    
    public void login() {
//        nifty.showPopup(screen, popupLogin.getId(), null);
        
        final Login login = Login.getInstance();
        login.setLoginData(nameTxt.getText(), passwordTxt.getText());
        login.requestCharacterList();
        
//        nifty.closePopup(popupLogin.getId());
        
        if (login.hasError()) {
//            nifty.showPopup(screen, popupError.getId(), null);
//            errorText.setText(login.getErrorText());
            
            return;
        }
    	nifty.gotoScreen("charSelect");
    }
    
    public void cancelLogin() {
//        nifty.closePopup(popupLogin.getId());
//        nifty.closePopup(popupError.getId());
    }
}
