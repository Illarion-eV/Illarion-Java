/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
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
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class OptionScreenController implements ScreenController {

    private enum TabType {
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

    private DropDown<String> charNameLength;
    private CheckBox showCharId;
    private DropDown<String> sendCrashReports;
    private DropDown<String> resolutions;
    private CheckBox fullscreen;

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        charNameLength = screen.findNiftyControl("charNameLength", DropDown.class);
        charNameLength.addItem("${options-bundle.charNameDisplay.short}");
        charNameLength.addItem("${options-bundle.charNameDisplay.long}");

        showCharId = screen.findNiftyControl("showCharId", CheckBox.class);

        sendCrashReports = screen.findNiftyControl("sendCrashReports", DropDown.class);
        sendCrashReports.addItem("${options-bundle.report.ask}");
        sendCrashReports.addItem("${options-bundle.report.always}");
        sendCrashReports.addItem("${options-bundle.report.never}");

        resolutions = screen.findNiftyControl("resolutions", DropDown.class);
        resolutions.addAllItems(getResolutionList());

        fullscreen = screen.findNiftyControl("fullscreen", CheckBox.class);
    }

    @Override
    public void onStartScreen() {
        charNameLength.selectItemByIndex(IllaClient.getCfg().getInteger(People.CFG_NAMEMODE_KEY) - 1);
        showCharId.setChecked(IllaClient.getCfg().getBoolean(People.CFG_SHOWID_KEY));
        sendCrashReports.selectItemByIndex(IllaClient.getCfg().getInteger(CrashReporter.CFG_KEY));
        resolutions.selectItem(IllaClient.getCfg().getString(IllaClient.CFG_RESOLUTION));
        fullscreen.setChecked(IllaClient.getCfg().getBoolean(IllaClient.CFG_FULLSCREEN));
    }

    @Override
    public void onEndScreen() {

    }

    private List<String> getResolutionList() {
        final GameContainer container = IllaClient.getInstance().getContainer();

        DisplayMode[] displayModes;
        try {
            displayModes = Display.getAvailableDisplayModes(800, 600, container.getScreenWidth(),
                    container.getScreenHeight(), 24, 32, 40, 120);
        } catch (LWJGLException exc) {
            displayModes = new DisplayMode[1];
            displayModes[0] = new DisplayMode(800, 600);
        }

        Arrays.sort(displayModes, new DisplayModeSorter());

        final List<String> resList = new ArrayList<String>();

        for (final DisplayMode mode : displayModes) {
            resList.add(new GraphicResolution(mode.getWidth(), mode.getHeight(), mode.getBitsPerPixel(),
                    mode.getFrequency()).toString());
        }

        return resList;
    }

    private void initGeneralTab() {
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
        try {
            displayModes = Display.getAvailableDisplayModes(800, 600, container.getScreenWidth(),
                    container.getScreenHeight(), 24, 32, 60, 80);
        } catch (LWJGLException exc) {
            displayModes = new DisplayMode[1];
            displayModes[0] = new DisplayMode(800, 600);
        }


        Arrays.sort(displayModes, new DisplayModeSorter());

        DropDown<DropDownItem<String>> dropDownResolutions = addDropDownOption("Resolutions: ",
                "resolution",
                String.class,
                options, graphicPanel).getControl();

        dropDownResolutions.setWidth(new SizeValue("210px"));

        for (DisplayMode mode : displayModes) {
            GraphicResolution res = new GraphicResolution(mode.getWidth(), mode.getHeight(), mode.getBitsPerPixel(),
                    mode.getFrequency());
            dropDownResolutions.addItem(new DropDownItem<String>(res.toString(), res.toString()));
        }

        selectByKey(cfg.getString("resolution"), dropDownResolutions);


        addSpacer(graphicPanel);

        CheckBoxOption checkBoxFullscreen = addCheckBox("Fullscreen: ", "fullscreen", options, graphicPanel);

        checkBoxFullscreen.getControl().setChecked(cfg.getBoolean("fullscreen"));

        CheckBoxOption checkBoxLegacy = addCheckBox("Legacy Renderer:", "legacyRenderer", options, graphicPanel);
        checkBoxLegacy.getControl().setChecked(cfg.getBoolean("legacyRenderer"));
    }

    private void initAudioTab() {
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

    public void save() {

        ConfigSystem cfgs = (ConfigSystem) cfg;
        IllaClient client = IllaClient.getInstance();
        for (Option option : options) {
            cfgs.set(option.getKey(), option.getValue());
        }

        cfgs.save();

        final GraphicResolution res = new GraphicResolution(cfg.getString("resolution"));

        try {
            ((AppGameContainer) client.getContainer()).setDisplayMode(res.getWidth(), res.getHeight
                    (), cfg.getBoolean("fullscreen"));
            MapDimensions.getInstance().reportScreenSize(res.getWidth(), res.getHeight());
        } catch (SlickException exc) {
            // Warn the user that something wrong happened?
        }

        Screen screen = nifty.getScreen("login");
        LoginScreenController loginScreen = (LoginScreenController) screen.getScreenController();
        loginScreen.resolutionChanged();
        nifty.resolutionChanged();

        screen = nifty.getScreen("charSelect");

        CharScreenController charScreen = (CharScreenController) screen.getScreenController();
        charScreen.resolutionChanged();

        LoadingState loadingState = (LoadingState) client.getGameState(Game.STATE_LOADING);
        screen = loadingState.getNifty().getScreen("loading");
        LoadScreenController loadScreen = (LoadScreenController) screen.getScreenController();
        loadScreen.resolutionChanged();


        PlayingState playingState = (PlayingState) client.getGameState(Game.STATE_PLAYING);
        screen = playingState.getNifty().getScreen("gamescreen");
        GameScreenController gameScreen = (GameScreenController) screen.getScreenController();
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

    public void onGraphicTabClick() {
        if (currentTab == TabType.Graphic)
            return;

        generalPanel.hideWithoutEffect();
        audioPanel.hideWithoutEffect();

        currentTab = TabType.Graphic;
        graphicPanel.showWithoutEffects();
    }

    public void onGeneralTabClick() {
        if (currentTab == TabType.General)
            return;

        graphicPanel.hideWithoutEffect();
        audioPanel.hideWithoutEffect();

        currentTab = TabType.General;
        generalPanel.showWithoutEffects();
    }

    public void onAudioTabClick() {
        if (currentTab == TabType.Audio)
            return;

        generalPanel.hideWithoutEffect();
        graphicPanel.hideWithoutEffect();

        currentTab = TabType.Audio;
        audioPanel.showWithoutEffects();
    }

    private Element addOptionLine(String labelString, Element parent) {
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

    private void addSpacer(Element parent) {
        PanelBuilder spacer = new PanelBuilder(String.valueOf(ids++));
        spacer.height("15px");
        spacer.build(nifty, screen, parent);
    }

    private <T> DropDownOption<T> addDropDownOption(String labelString, String optionKey, Class<T> type,
                                                    ArrayList<Option> optionList, Element parent) {
        Element panel = addOptionLine(labelString, parent);

        DropDownOption<T> dropDownOption = new DropDownOption<T>(optionKey, panel, nifty, screen);
        optionList.add(dropDownOption);

        return dropDownOption;
    }

    private CheckBoxOption addCheckBox(String labelString, String optionKey, ArrayList<Option> optionList, Element parent) {
        Element panel = addOptionLine(labelString, parent);

        CheckBoxOption checkBoxOption = new CheckBoxOption(optionKey, panel, nifty, screen);
        optionList.add(checkBoxOption);

        return checkBoxOption;
    }

    private SliderOption addSlider(String labelString, String optionKey, ArrayList<Option> optionList, Element parent) {
        Element panel = addOptionLine(labelString, parent);

        SliderOption sliderOption = new SliderOption(optionKey, panel, nifty, screen);
        optionList.add(sliderOption);

        return sliderOption;
    }

    private void selectByKey(Integer key, DropDown<DropDownItem<Integer>> dropDown) {
        for (DropDownItem<Integer> item : dropDown.getItems()) {
            if (item.getKey().compareTo(key) == 0) {
                dropDown.selectItem(item);

                break;
            }
        }
    }

    private void selectByKey(String key, DropDown<DropDownItem<String>> dropDown) {
        for (DropDownItem<String> item : dropDown.getItems()) {
            if (item.getKey().compareTo(key) == 0) {
                dropDown.selectItem(item);

                break;
            }
        }
    }
}
