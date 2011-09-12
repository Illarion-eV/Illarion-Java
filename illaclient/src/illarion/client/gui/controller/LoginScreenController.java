package illarion.client.gui.controller;

import illarion.client.Login;
import illarion.common.util.LoadingManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.nifty.effects.impl.Hint;

public final class LoginScreenController implements ScreenController {

	private Nifty nifty;
    private Screen screen;
    
	private TextField nameTxt;
	private TextField passwordTxt;
	private CheckBox savePassword;
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
    	savePassword = screen.findNiftyControl("savePassword", CheckBox.class);
    	loginBtn = screen.findNiftyControl("loginBtn", Button.class);
    	charList = nifty.getScreen("charSelect").findNiftyControl("myListBox", ListBox.class);
//    	errorText = screen.findNiftyControl("errorText", Label.class);
//    	
//    	popupLogin = screen.findElementByName("popupLoggingIn");
//        popupError = screen.findElementByName("error");
    }

    @Override
    public void onStartScreen() {
        final Login login = Login.getInstance();
        login.restoreLoginData();
        nameTxt.setText(login.getLoginName());
        passwordTxt.setText(login.getPassword());
        savePassword.setChecked(login.storePassword());

        Element myLabel=screen.findElementByName("testHint");
        myLabel.setConstraintX(new SizeValue("100px"));
        myLabel.setConstraintY(new SizeValue("200px"));
        
        
    }

    @Override
    public void onEndScreen() {

    }
    
    public void login() {
//        nifty.showPopup(screen, popupLogin.getId(), null);
        
        final Login login = Login.getInstance();
        login.setLoginData(nameTxt.getText(), passwordTxt.getText());
        
        login.storeData(savePassword.isChecked());
        
        login.requestCharacterList();
        
//        nifty.closePopup(popupLogin.getId());
        
        if (login.hasError()) {
//            nifty.showPopup(screen, popupError.getId(), null);
//            errorText.setText(login.getErrorText());
            
            return;
        }
    	nifty.gotoScreen("charSelect");
    }
    
    public void displayLabel() {
    	int myX=nifty.getNiftyMouse().getX();
    	int myY=nifty.getNiftyMouse().getY();
    	
    }
    
    public void cancelLogin() {
//        nifty.closePopup(popupLogin.getId());
//        nifty.closePopup(popupError.getId());
    }	
    
    public void options() {
    	nifty.gotoScreen("options");
    }
}
