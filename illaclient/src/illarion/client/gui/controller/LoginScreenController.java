package illarion.client.gui.controller;

import illarion.client.LoginDialog;
import illarion.client.Servers;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class LoginScreenController implements ScreenController {

	private Nifty nifty;
	private TextField nameTxt;
	private TextField passwordTxt;
	private ListBox charList;
	
    @Override
    public void bind(Nifty nifty, Screen screen) {
    	this.nifty = nifty;
    	nameTxt = screen.findNiftyControl("nameTxt", TextField.class);
    	passwordTxt = screen.findNiftyControl("passwordTxt", TextField.class);
    	charList = nifty.getScreen("charSelect").findNiftyControl("myListBox", ListBox.class);
    }

    @Override
    public void onStartScreen() {

    }

    @Override
    public void onEndScreen() {

    }
    
    public void login() {
    	//updateCharacters();
    	nifty.gotoScreen("charSelect");
    }
}
