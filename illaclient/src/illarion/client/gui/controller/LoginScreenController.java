package illarion.client.gui.controller;

import illarion.client.Login;
import illarion.common.util.LoadingManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyIdCreator;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.controls.Button;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.nifty.effects.impl.Hint;
import de.lessvoid.nifty.effects.Effect;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.loaderv2.types.PanelType;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.loaderv2.NiftyLoader;
import de.lessvoid.nifty.loaderv2.types.NiftyType;


public final class LoginScreenController implements ScreenController, KeyInputHandler {

	private Nifty nifty;
    private Screen screen;
    private PanelType panel;
    
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
<<<<<<< HEAD
        final Login login = Login.getInstance();
        login.restoreLoginData();
        nameTxt.setText(login.getLoginName());
        passwordTxt.setText(login.getPassword());
        savePassword.setChecked(login.storePassword());

       /* Element myLabel=screen.findElementByName("testHint");
        myLabel.setConstraintX(new SizeValue("100px"));
        myLabel.setConstraintY(new SizeValue("200px"));*/
        
        
=======

>>>>>>> e03234034b8c675d888fbfc5a33b4debf4758a8b
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
 /*   
    public void createLabel() {
        final String hintControl = "nifty-default-hint";
        final String hintStyle = "illarion-hint";
        final String hintText = "dynamical hint";
        final int hintDelay = 0;
        final int offsetX = 200;
        final int offsetY = 300;

        // Create new Layer
        final String hintLayerId = NiftyIdCreator.generate();
        final String hintPanelId = hintLayerId + "-hint-panel";

        Element myElement = new LayerBuilder(hintLayerId) {{
          childLayoutAbsoluteInside();
          visible(false);
          control(new ControlBuilder(hintPanelId, hintControl) {{
            parameter("hintText", hintText);
            if (hintStyle != null) {
              style(hintStyle);
            }
          }});
        }}.build(nifty, nifty.getCurrentScreen(), nifty.getCurrentScreen().getRootElement());

        layer(new LayerBuilder("content") {{
        	backgroundColor("#fff0");
        	childLayoutVertical();
        	
        	onStartScreenEffect(new FadeEffectBuilder() {{
	        	startColor("#fff0");
	        	endColor("#ffff");
	        	length(1000);
	        	startDelay(0);
	        	inherit(true);
	        	post(false);
        	}});
        	
	        panel(new PanelBuilder() {{
	            childLayoutVertical();
	            backgroundColor("#8001");
	            paddingLeft("7px");
	            paddingRight("7px");
	            paddingTop("4px");
	            paddingBottom("4px");
	            width("105px");
	            onActiveEffect(new EffectBuilder("hint") {{
	              effectParameter("color", "#0008");
	        }});
	    }});
        
    }*/
    
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
