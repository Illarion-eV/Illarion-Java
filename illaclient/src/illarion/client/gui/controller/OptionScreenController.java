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

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import illarion.client.Game;
import illarion.client.IllaClient;
import illarion.client.graphics.DisplayModeSorter;
import illarion.client.gui.util.*;
import illarion.client.states.LoadingState;
import illarion.client.states.PlayingState;
import illarion.client.util.Lang;
import illarion.client.world.MapDimensions;
import illarion.client.world.People;
import illarion.client.world.Player;
import illarion.common.bug.CrashReporter;
import illarion.common.config.Config;
import illarion.common.config.ConfigSystem;
import illarion.common.graphics.GraphicResolution;

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
    private Element generalPanel;
    private Element graphicPanel;
    private Element audioPanel;

    private Config cfg;

    private ArrayList<Option> options = new ArrayList<Option>();

    private int ids = 0;

	private Nifty nifty;

    @Override
    public void bind(Nifty nifty, Screen screen) {
    	this.nifty = nifty;
        this.screen = screen;

        Element optionsPanel = screen.findElementByName("optionsPanel");
        this.generalPanel = new PanelBuilder("generalPanel"){{childLayoutVertical(); visible(false); valignTop();
            alignCenter();}}
                .build(nifty, screen, optionsPanel);

        this.graphicPanel = new PanelBuilder("graphicPanel"){{childLayoutVertical(); visible(false); valignTop();
            alignCenter();}}
                .build(nifty, screen, optionsPanel);

        this.audioPanel = new PanelBuilder("audioPanel"){{childLayoutVertical(); visible(false);
            valignTop();
            alignCenter();}}
                .build(nifty, screen, optionsPanel);


        this.cfg = IllaClient.getCfg();
        currentTab = TabType.None;

        initGeneralTab();
        initGraphicTab();
        initAudioTab();

        // TODO: For a next coding session, update all caption/text when language is changed.
        /*screen.findNiftyControl("general", Button.class).setText(Lang.getMsg("option.common"));
        screen.findNiftyControl("graphic", Button.class).setText(Lang.getMsg("option.graphics"));
        screen.findNiftyControl("audio", Button.class).setText(Lang.getMsg("option.audio"));
        screen.findNiftyControl("mouse", Button.class).setText(Lang.getMsg("option.mouse"));
        screen.findNiftyControl("saveBtn", Button.class).setText(Lang.getMsg("illarion.common.config.gui.Save"));
        screen.findNiftyControl("cancelBtn", Button.class).setText(Lang.getMsg("illarion.common.config.gui.Cancel"));*/


        screen.layoutLayers();
    }

    private void initGeneralTab()
    {
        addSpacer(generalPanel);

        DropDown<DropDownItem<Integer>> dropDownCharName = addDropDownOption("Characters Name Length: ",
                                                                             "showNameMode", Integer.class,
                                                                             options, generalPanel
        ).getControl();

        dropDownCharName.setWidth(new SizeValue("100px"));
        dropDownCharName.addItem(new DropDownItem<Integer>(People.NAME_SHORT, "Short"));
        dropDownCharName.addItem(new DropDownItem<Integer>(People.NAME_LONG, "Long"));

        selectByKey(cfg.getInteger("showNameMode"), dropDownCharName);

        addSpacer(generalPanel);

        CheckBox checkBox = addCheckBox("Show IDs: ", "showIDs", options, generalPanel).getControl();

        checkBox.setChecked(cfg.getBoolean("showIDs"));

        addSpacer(generalPanel);

        DropDown<DropDownItem<Integer>> dropDownCrash = addDropDownOption("Crash Reporting: ", CrashReporter.CFG_KEY,
                                                                          Integer.class, options, generalPanel
        ).getControl();

        dropDownCrash.addItem(new DropDownItem<Integer>(CrashReporter.MODE_ASK, "Ask"));
        dropDownCrash.addItem(new DropDownItem<Integer>(CrashReporter.MODE_ALWAYS, "Always"));
        dropDownCrash.addItem(new DropDownItem<Integer>(CrashReporter.MODE_NEVER, "Never"));

        dropDownCrash.setWidth(new SizeValue("100px"));

        selectByKey(cfg.getInteger(CrashReporter.CFG_KEY), dropDownCrash);

        addSpacer(generalPanel);

        DropDown<DropDownItem<String>> dropDownLanguage = addDropDownOption("Language: ", Lang.LOCALE_CFG,
                                                                            String.class, options, generalPanel
        ).getControl();

        dropDownLanguage.setWidth(new SizeValue("100px"));
        dropDownLanguage.addItem(new DropDownItem<String>(Lang.LOCALE_CFG_ENGLISH, "English"));
        dropDownLanguage.addItem(new DropDownItem<String>(Lang.LOCALE_CFG_GERMAN, "German"));

        selectByKey(cfg.getString(Lang.LOCALE_CFG), dropDownLanguage);
    }
    private void initGraphicTab() {
        addSpacer(graphicPanel);

        GameContainer container = IllaClient.getInstance().getContainer();

        DisplayMode[] displayModes;
        try{
            displayModes = Display.getAvailableDisplayModes(800, 600, container.getScreenWidth(),
                                                            container.getScreenHeight(), 24, 32, 60, 80);
        }
        catch(LWJGLException exc)
        {
            displayModes = new DisplayMode[1];
            displayModes[0] = new DisplayMode(800, 600);
        }


        Arrays.sort(displayModes, new DisplayModeSorter());

        DropDown<DropDownItem<String>> dropDownResolutions = addDropDownOption("Resolutions: ",
                                                                               "resolution",
                                                                               String.class,
                                                                               options, graphicPanel).getControl();

        dropDownResolutions.setWidth(new SizeValue("210px"));

        for(DisplayMode mode : displayModes)
        {
            GraphicResolution res = new GraphicResolution(mode.getWidth(), mode.getHeight(), mode.getBitsPerPixel(),
                                                          mode.getFrequency());
            dropDownResolutions.addItem(new DropDownItem<String>(res.toString(), res.toString()));
        }

        selectByKey(cfg.getString("resolution"), dropDownResolutions);



        addSpacer(graphicPanel);

        CheckBoxOption checkBoxFullscreen = addCheckBox("Fullscreen: ", "fullscreen", options, graphicPanel);

        checkBoxFullscreen.getControl().setChecked(cfg.getBoolean("fullscreen"));
    }

    private void initAudioTab()
    {
        addSpacer(audioPanel);

        CheckBoxOption soundCheckBoxOption = addCheckBox("Sound On: ", "soundOn", options, audioPanel);
        soundCheckBoxOption.getControl().setChecked(cfg.getBoolean("soundOn"));

        addSpacer(audioPanel);

        SliderOption soundSliderOption = addSlider("Sound Volume: ", "soundVolume", options, audioPanel);
        soundSliderOption.getControl().setButtonStepSize(1.0f);
        soundSliderOption.getControl().setMin(0.0f);
        soundSliderOption.getControl().setMax(Player.MAX_CLIENT_VOL);

        soundSliderOption.getControl().setValue(cfg.getInteger("soundVolume"));

        addSpacer(audioPanel);

        CheckBoxOption musicCheckBoxOption = addCheckBox("Music On: ", "musicOn", options, audioPanel);
        musicCheckBoxOption.getControl().setChecked(cfg.getBoolean("musicOn"));

        addSpacer(audioPanel);

        SliderOption musicSliderOption = addSlider("Music Volume: ", "musicVolume", options, audioPanel);
        musicSliderOption.getControl().setButtonStepSize(1.0f);
        musicSliderOption.getControl().setMin(0.0f);
        musicSliderOption.getControl().setMax(Player.MAX_CLIENT_VOL);

        musicSliderOption.getControl().setValue(cfg.getInteger("musicVolume"));
    }

    @Override
    public void onStartScreen() {

        onGeneralTabClick();
    }

    @Override
    public void onEndScreen() {

    }

    public void save() {

        ConfigSystem cfgs = (ConfigSystem)cfg;
        IllaClient client = IllaClient.getInstance();
        for(Option option : options)
        {
            cfgs.set(option.getKey(), option.getValue());
        }

        cfgs.save();

        final GraphicResolution res = new GraphicResolution(cfg.getString("resolution"));

        try{
            ((AppGameContainer)client.getContainer()).setDisplayMode(res.getWidth(), res.getHeight
                    (), cfg.getBoolean("fullscreen"));
            MapDimensions.getInstance().reportScreenSize(res.getWidth(), res.getHeight());
        }
        catch(SlickException exc)
        {
            // Warn the user that something wrong happened?
        }

        Screen screen = nifty.getScreen("login");
        LoginScreenController loginScreen = (LoginScreenController)screen.getScreenController();
        loginScreen.resolutionChanged();
        nifty.resolutionChanged();

        screen = nifty.getScreen("charSelect");

        CharScreenController charScreen = (CharScreenController)screen.getScreenController();
        charScreen.resolutionChanged();

        LoadingState loadingState = (LoadingState)client.getGameState(Game.STATE_LOADING);
        screen = loadingState.getNifty().getScreen("loading");
        LoadScreenController loadScreen = (LoadScreenController)screen.getScreenController();
        loadScreen.resolutionChanged();


        PlayingState playingState = (PlayingState)client.getGameState(Game.STATE_PLAYING);
        screen = playingState.getNifty().getScreen("gamescreen");
        GameScreenController gameScreen = (GameScreenController)screen.getScreenController();
        gameScreen.resolutionChanged();


        generalPanel.hide();
        graphicPanel.hide();
        audioPanel.hide();
        currentTab = TabType.None;
    	nifty.gotoScreen("login");

    }

    public void cancel() {
        generalPanel.hide();
        graphicPanel.hide();
        audioPanel.hide();

        currentTab = TabType.None;
    	nifty.gotoScreen("login");
    }

    public void onGraphicTabClick(){
    	if(currentTab == TabType.Graphic)
    		return;

        generalPanel.hideWithoutEffect();
        audioPanel.hideWithoutEffect();

        currentTab = TabType.Graphic;
        graphicPanel.showWithoutEffects();
    }

    public void onGeneralTabClick()
    {
        if(currentTab == TabType.General)
            return;

        graphicPanel.hideWithoutEffect();
        audioPanel.hideWithoutEffect();

        currentTab = TabType.General;
        generalPanel.showWithoutEffects();
    }

    public void onAudioTabClick(){
        if(currentTab == TabType.Audio)
            return;

        generalPanel.hideWithoutEffect();
        graphicPanel.hideWithoutEffect();

        currentTab = TabType.Audio;
        audioPanel.showWithoutEffects();
    }

    private Element addOptionLine(String labelString, Element parent)
    {
        PanelBuilder panelBuilderLine = new PanelBuilder(String.valueOf(ids++));

        panelBuilderLine.childLayoutHorizontal();

        Element panelLine = panelBuilderLine.build(nifty, screen, parent);

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

    private void addSpacer(Element parent)
    {
        PanelBuilder spacer = new PanelBuilder(String.valueOf(ids++));
        spacer.height("15px");
        spacer.build(nifty, screen, parent);
    }

    private <T> DropDownOption<T>  addDropDownOption(String labelString, String optionKey, Class<T> type,
                                                     ArrayList<Option> optionList, Element parent)
    {
        Element panel = addOptionLine(labelString, parent);

        DropDownOption<T> dropDownOption = new DropDownOption<T>(optionKey, panel, nifty, screen);
        optionList.add(dropDownOption);

        return dropDownOption;
    }

    private CheckBoxOption addCheckBox(String labelString, String optionKey, ArrayList<Option> optionList, Element parent)
    {
        Element panel = addOptionLine(labelString, parent);

        CheckBoxOption checkBoxOption = new CheckBoxOption(optionKey, panel, nifty, screen);
        optionList.add(checkBoxOption);

        return checkBoxOption;
    }

    private SliderOption addSlider(String labelString, String optionKey, ArrayList<Option> optionList, Element parent)
    {
        Element panel = addOptionLine(labelString, parent);

        SliderOption sliderOption = new SliderOption(optionKey, panel, nifty, screen);
        optionList.add(sliderOption);

        return sliderOption;
    }

    private void selectByKey(Integer key, DropDown<DropDownItem<Integer>> dropDown){
        for(DropDownItem<Integer> item : dropDown.getItems())
        {
            if(item.getKey().compareTo(key) == 0)
            {
                dropDown.selectItem(item);

                break;
            }
        }
    }

    private void selectByKey(String key, DropDown<DropDownItem<String>> dropDown){
        for(DropDownItem<String> item : dropDown.getItems())
        {
            if(item.getKey().compareTo(key) == 0)
            {
                dropDown.selectItem(item);

                break;
            }
        }
    }
}
