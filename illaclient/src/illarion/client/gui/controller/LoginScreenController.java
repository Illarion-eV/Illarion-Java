package illarion.client.gui.controller;

import org.newdawn.slick.state.StateBasedGame;

import illarion.client.Login;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.loaderv2.types.PanelType;
import de.lessvoid.nifty.builder.PanelBuilder;


public final class LoginScreenController implements ScreenController, KeyInputHandler {

	private Nifty nifty;
    private Screen screen;
    private PanelType panel;
    
	private TextField nameTxt;
	private TextField passwordTxt;
	private CheckBox savePassword;
	private Button loginBtn;
	private ListBox<?> charList;
	
	private final StateBasedGame game;
	
    public LoginScreenController(StateBasedGame game) {
        this.game = game;
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
    	this.nifty = nifty;
    	this.screen = screen;
    	
    	nameTxt = screen.findNiftyControl("nameTxt", TextField.class);
    	passwordTxt = screen.findNiftyControl("passwordTxt", TextField.class);
    	savePassword = screen.findNiftyControl("savePassword", CheckBox.class);
    	loginBtn = screen.findNiftyControl("loginBtn", Button.class);
    	charList = nifty.getScreen("charSelect").findNiftyControl("myListBox", ListBox.class);
    	
    	nameTxt.getElement().addInputHandler(this);
    	passwordTxt.getElement().addInputHandler(this);
    	
    	final Login login = Login.getInstance();
        login.restoreLoginData();
        nameTxt.setText(login.getLoginName());
        passwordTxt.setText(login.getPassword());
        savePassword.setChecked(login.storePassword());
    	
//    	errorText = screen.findNiftyControl("errorText", Label.class);
//    	
//    	popupLogin = screen.findElementByName("popupLoggingIn");
//        popupError = screen.findElementByName("error");
    }

    @Override
    public void onStartScreen() {
       /* Element myLabel=screen.findElementByName("testHint");
        myLabel.setConstraintX(new SizeValue("100px"));
        myLabel.setConstraintY(new SizeValue("200px"));*/
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
   
    public void createLabel() {
        PanelBuilder builder = new PanelBuilder() {{
        	width("200px");
            height("200px");
            backgroundColor("#f00f");
            valignCenter();
            alignCenter();
        }};
        String myX=Integer.toString(nifty.getNiftyMouse().getX()) + "px";
        String myY=Integer.toString(nifty.getNiftyMouse().getY()) + "px";
        builder.x(myX);
        builder.y(myY);
        Element parent = screen.findElementByName("windows");
        builder.build(nifty, screen, parent);
        
    }
    
    public void cancelLogin() {
//        nifty.closePopup(popupLogin.getId());
//        nifty.closePopup(popupError.getId());
    }	
    
    public void options() {
    	nifty.gotoScreen("options");
    }
    
    @Override
    public boolean keyEvent(NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyInputEvent.SubmitText) {
            login();
            return true;
        }
        return false;
    }
}
