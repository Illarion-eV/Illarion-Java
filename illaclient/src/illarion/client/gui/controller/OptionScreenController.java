package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.*;

import java.awt.*;
import java.util.ArrayList;

import illarion.client.IllaClient;
import illarion.client.gui.util.*;
import illarion.client.util.Lang;
import illarion.client.world.People;
import illarion.common.bug.CrashReporter;
import illarion.common.config.Config;

public final class OptionScreenController implements ScreenController {
	
	private enum TabType
	{
        None,
		General,
		Graphic,
		Audio,
		Mouse
	}
	
	private TabType currentTab;
    private Screen screen;
    private Element optionsPanel;
    private Config cfg;

    private ArrayList<Option> options = new ArrayList<Option>();
    
    private int ids = 0;

	private Nifty nifty;
	
    @Override
    public void bind(Nifty nifty, Screen screen) {
    	this.nifty = nifty;
        this.screen = screen;
        this.optionsPanel = screen.findElementByName("optionsPanel");
        this.cfg = IllaClient.getCfg();
        currentTab = TabType.None;
        //nifty.setDebugOptionPanelColors(true);
    }

    @Override
    public void onStartScreen() {
    	onGeneralTabClick();
    }

    @Override
    public void onEndScreen() {

    }
    
    public void save() {

        // General

        // Graphic

        // Music

        // Mouse
    	nifty.gotoScreen("login");
    }
    
    public void cancel() {
    	nifty.gotoScreen("login");
    }
    
    public void onGraphicTabClick()
    {
    	if(currentTab == TabType.Graphic)
    		return;    	
    	
    	currentTab = TabType.Graphic;

        

    }
    
    public void onGeneralTabClick()
    {
        if(currentTab == TabType.General)
            return;


        currentTab = TabType.General;

        addSpacer();

        DropDown<DropDownItem<Integer>> dropDownCharName = addDropDownOption("Characters Name Length: ",
                                                                             "showNameMode", Integer.class).getControl();

        dropDownCharName.setWidth(new SizeValue("100px"));
        dropDownCharName.addItem(new DropDownItem<Integer>(People.NAME_SHORT, "Short"));
        dropDownCharName.addItem(new DropDownItem<Integer>(People.NAME_LONG, "Long"));

        selectByKey(cfg.getInteger("showNameMode"), dropDownCharName);

        addSpacer();
        
        CheckBox checkBox = addCheckBox("Show IDs: ", "showIDs").getControl();

        checkBox.setChecked(cfg.getBoolean("showIDs"));

        addSpacer();

        DropDown<DropDownItem<Integer>> dropDownCrash = addDropDownOption("Crash Reporting: ", CrashReporter.CFG_KEY,
                                                                          Integer.class).getControl();

        dropDownCrash.addItem(new DropDownItem<Integer>(CrashReporter.MODE_ASK, "Ask"));
        dropDownCrash.addItem(new DropDownItem<Integer>(CrashReporter.MODE_ALWAYS, "Always"));
        dropDownCrash.addItem(new DropDownItem<Integer>(CrashReporter.MODE_NEVER, "Never"));

        dropDownCrash.setWidth(new SizeValue("100px"));

        selectByKey(cfg.getInteger(CrashReporter.CFG_KEY), dropDownCrash);
        
        addSpacer();
        
        DropDown<DropDownItem<String>> dropDownLanguage = addDropDownOption("Language: ", Lang.LOCALE_CFG, 
                                                                            String.class).getControl();
        
        dropDownLanguage.addItem(new DropDownItem<String>(Lang.LOCALE_CFG_ENGLISH, "English"));
        dropDownLanguage.addItem(new DropDownItem<String>(Lang.LOCALE_CFG_GERMAN, "German"));

        selectByKey(cfg.getString(Lang.LOCALE_CFG), dropDownLanguage);

        optionsPanel.layoutElements();

        dropDownCharName.getElement().getParent().layoutElements();
        dropDownCrash.getElement().getParent().layoutElements();
        dropDownLanguage.getElement().getParent().layoutElements();
    }
    
    private Element addOptionLine(String labelString)
    {
        PanelBuilder panelBuilderLine = new PanelBuilder(String.valueOf(ids++));

        panelBuilderLine.childLayoutHorizontal();

        Element panelLine = panelBuilderLine.build(nifty, screen, optionsPanel);

        PanelBuilder panelBuilderLeft = new PanelBuilder(String.valueOf(ids++));
        panelBuilderLeft.paddingRight("10" + SizeValue.PIXEL);
        panelBuilderLeft.childLayoutCenter();

        PanelBuilder panelBuilderRight = new PanelBuilder(String.valueOf(ids++));
        panelBuilderRight.childLayoutCenter();

        Element panelLeft = panelBuilderLeft.build(nifty, screen, panelLine);
        Element panelRight = panelBuilderRight.build(nifty, screen, panelLine);

        new LabelBuilder(String.valueOf(ids++), labelString).build(nifty, screen, panelLeft);

        return panelRight;
    }

    private void addSpacer()
    {
        PanelBuilder spacer = new PanelBuilder(String.valueOf(ids++));
        spacer.height("10px");
        spacer.build(nifty, screen, optionsPanel);
    }
    
    private <T> DropDownOption<T>  addDropDownOption(String labelString, String optionKey, Class<T> type)
    {
        Element panel = addOptionLine(labelString);

        DropDownOption<T> dropDownOption = new DropDownOption<T>(optionKey, panel, nifty, screen);
        options.add(dropDownOption);

        return dropDownOption;
    }
    
    private CheckBoxOption addCheckBox(String labelString, String optionKey)
    {
        Element panel = addOptionLine(labelString);

        CheckBoxOption checkBoxOption = new CheckBoxOption(optionKey, panel, nifty, screen);
        options.add(checkBoxOption);

        return checkBoxOption;
    }
    
    private <T> void selectByKey(T key, DropDown<DropDownItem<T>> dropDown){
        for(DropDownItem<T> item : dropDown.getItems())
        {
            if(item.getKey() == key)
            {
                dropDown.selectItem(item);

                break;
            }
        }
    }
}
