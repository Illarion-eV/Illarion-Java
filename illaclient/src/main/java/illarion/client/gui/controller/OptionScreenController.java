/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.controls.checkbox.CheckBoxView;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.ElementShowEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.IllaClient;
import illarion.client.util.AudioPlayer;
import illarion.client.util.translation.Translator;
import illarion.common.bug.CrashReporter;
import illarion.common.config.Config;
import org.illarion.engine.DesktopGameContainer;
import org.illarion.engine.graphic.GraphicResolution;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public final class OptionScreenController implements ScreenController {

    private Nifty nifty;
    private Screen screen;

    //private DropDown<String> charNameLength;
    //private CheckBox showCharId;
    @Nullable
    private CheckBox wasdWalk;
    @Nullable
    private CheckBox disableChatAfterSending;
    @Nullable
    private CheckBox showQuestsOnGameMap;
    @Nullable
    private CheckBox showQuestsOnMiniMap;

    @Nullable
    private DropDown<String> sendCrashReports;

    @Nullable
    private DropDown<String> resolutions;
    @Nullable
    private CheckBox fullscreen;
    @Nullable
    private CheckBox showFps;
    @Nullable
    private CheckBox showPing;
    @Nullable
    private DropDown<String> translationProviders;
    @Nullable
    private DropDown<String> translationDirections;

    @Nullable
    private CheckBox soundOn;
    @Nullable
    private Slider soundVolume;
    @Nullable
    private CheckBox musicOn;
    @Nullable
    private Slider musicVolume;

    @Nullable
    private TextField serverAddress;
    @Nullable
    private TextField serverPort;
    @Nullable
    private CheckBox serverAccountLogin;
    @Nullable
    private CheckBox serverResetSettings;

    @Nonnull
    public static List<String> getResolutionList() {
        DesktopGameContainer container = IllaClient.getInstance().getContainer();

        GraphicResolution[] resolutions = container.getFullScreenResolutions();

        List<String> resList = new ArrayList<>();

        for (GraphicResolution resolution : resolutions) {
            resList.add(resolution.toString());
        }

        return resList;
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        Element tabRoot = screen.findElementById("tabRoot");

        //charNameLength = screen.findNiftyControl("charNameLength", DropDown.class);
        //charNameLength.addItem("${options-bundle.charNameDisplay.short}");
        //charNameLength.addItem("${options-bundle.charNameDisplay.long}");

        //showCharId = screen.findNiftyControl("showCharId", CheckBox.class);

        wasdWalk = tabRoot.findNiftyControl("wasdWalk", CheckBox.class);

        disableChatAfterSending = tabRoot.findNiftyControl("disableChatAfterSending", CheckBox.class);
        showQuestsOnGameMap = tabRoot.findNiftyControl("showQuestsOnGameMap", CheckBox.class);
        showQuestsOnMiniMap = tabRoot.findNiftyControl("showQuestsOnMiniMap", CheckBox.class);

        //noinspection unchecked
        sendCrashReports = tabRoot.findNiftyControl("sendCrashReports", DropDown.class);
        sendCrashReports.addItem("${options-bundle.report.ask}");
        sendCrashReports.addItem("${options-bundle.report.always}");
        sendCrashReports.addItem("${options-bundle.report.never}");

        //noinspection unchecked
        resolutions = tabRoot.findNiftyControl("resolutions", DropDown.class);
        resolutions.addAllItems(getResolutionList());

        fullscreen = tabRoot.findNiftyControl("fullscreen", CheckBox.class);
        showFps = tabRoot.findNiftyControl("showFps", CheckBox.class);
        showPing = tabRoot.findNiftyControl("showPing", CheckBox.class);

        //noinspection unchecked
        translationProviders = tabRoot.findNiftyControl("translationProviders", DropDown.class);
        translationProviders.addItem("${options-bundle.translation.provider.none}");
        translationProviders.addItem("${options-bundle.translation.provider.mymemory}");
        //translationProviders.addItem("${options-bundle.translation.provider.yandex}");
        //noinspection unchecked
        translationDirections = tabRoot.findNiftyControl("translationDirections", DropDown.class);
        translationDirections.addItem("${options-bundle.translation.direction.default}");
        translationDirections.addItem("${options-bundle.translation.direction.enToDe}");
        translationDirections.addItem("${options-bundle.translation.direction.deToEn}");

        soundOn = tabRoot.findNiftyControl("soundOn", CheckBox.class);
        soundVolume = tabRoot.findNiftyControl("soundVolume", Slider.class);
        musicOn = tabRoot.findNiftyControl("musicOn", CheckBox.class);
        musicVolume = tabRoot.findNiftyControl("musicVolume", Slider.class);

        Element serverTab = tabRoot.findElementById("#serverTab");
        if (serverTab == null) {
            return;
        }
        if (IllaClient.IS_DEVELOP) {
            serverAddress = serverTab.findNiftyControl("serverAddress", TextField.class);
            serverPort = serverTab.findNiftyControl("serverPort", TextField.class);
            serverAccountLogin = serverTab.findNiftyControl("serverAccountLogin", CheckBox.class);
            serverResetSettings = serverTab.findNiftyControl("resetServerSettings", CheckBox.class);
        } else {
            tabRoot.getNiftyControl(TabGroup.class).removeTab(serverTab);
        }
    }

    @Override
    public void onStartScreen() {
        //charNameLength.selectItemByIndex(IllaClient.getCfg().getInteger(People.CFG_NAMEMODE_KEY) - 1);
        //showCharId.setChecked(IllaClient.getCfg().getBoolean(People.CFG_SHOWID_KEY));
        wasdWalk.setChecked(IllaClient.getCfg().getBoolean("wasdWalk"));
        disableChatAfterSending.setChecked(IllaClient.getCfg().getBoolean("disableChatAfterSending"));
        showQuestsOnGameMap.setChecked(IllaClient.getCfg().getBoolean("showQuestsOnGameMap"));
        showQuestsOnMiniMap.setChecked(IllaClient.getCfg().getBoolean("showQuestsOnMiniMap"));

        sendCrashReports.selectItemByIndex(IllaClient.getCfg().getInteger(CrashReporter.CFG_KEY));
        resolutions.selectItem(IllaClient.getCfg().getString(IllaClient.CFG_RESOLUTION));
        fullscreen.setChecked(IllaClient.getCfg().getBoolean(IllaClient.CFG_FULLSCREEN));
        showFps.setChecked(IllaClient.getCfg().getBoolean("showFps"));
        showPing.setChecked(IllaClient.getCfg().getBoolean("showPing"));

        translationProviders.selectItemByIndex(IllaClient.getCfg().getInteger(Translator.CFG_KEY_PROVIDER));
        translationDirections.selectItemByIndex(IllaClient.getCfg().getInteger(Translator.CFG_KEY_DIRECTION));

        soundOn.setChecked(IllaClient.getCfg().getBoolean("soundOn"));
        soundVolume.setValue(IllaClient.getCfg().getFloat("soundVolume"));
        musicOn.setChecked(IllaClient.getCfg().getBoolean("musicOn"));
        musicVolume.setValue(IllaClient.getCfg().getFloat("musicVolume"));

        if (serverAddress != null) {
            serverAddress.setText(IllaClient.getCfg().getString("customServer.domain"));
            serverPort.setText(Integer.toString(IllaClient.getCfg().getInteger("customServer.port")));
            serverAccountLogin.setChecked(IllaClient.getCfg().getBoolean("customServer.accountSystem"));
            serverResetSettings.setChecked(false);
        }
    }

    @Override
    public void onEndScreen() {
    }

    @NiftyEventSubscriber(pattern = "tabRoot#tab-content-panel#[a-z]+Tab")
    public void updateVisibility(String topic, ElementShowEvent event) {
        if ("tabRoot#tab-content-panel#generalTab".equals(topic)) {
            ((CheckBoxView) wasdWalk).update(wasdWalk.isChecked());
            ((CheckBoxView) disableChatAfterSending).update(disableChatAfterSending.isChecked());
            ((CheckBoxView) showQuestsOnGameMap).update(showQuestsOnGameMap.isChecked());
            ((CheckBoxView) showQuestsOnMiniMap).update(showQuestsOnMiniMap.isChecked());
        }

        if ("tabRoot#tab-content-panel#graphicsTab".equals(topic)) {
            ((CheckBoxView) showFps).update(showFps.isChecked());
            ((CheckBoxView) showPing).update(showPing.isChecked());
        }

        if ("tabRoot#tab-content-panel#soundTab".equals(topic)) {
            ((CheckBoxView) soundOn).update(soundOn.isChecked());
            ((CheckBoxView) musicOn).update(musicOn.isChecked());
        }

        if ("tabRoot#tab-content-panel#serverTab".equals(topic)) {
            ((CheckBoxView) serverAccountLogin).update(serverAccountLogin.isChecked());
        }
    }

    @NiftyEventSubscriber(id = "saveButton")
    public void onSaveButtonClickedEvent(String topic, ButtonClickedEvent event) {
        nifty.gotoScreen("login");
        Config configSystem = IllaClient.getCfg();

        //configSystem.set(People.CFG_NAMEMODE_KEY, charNameLength.getSelectedIndex() + 1);
        //configSystem.set(People.CFG_SHOWID_KEY, showCharId.isChecked());
        configSystem.set("wasdWalk", wasdWalk.isChecked());
        configSystem.set("disableChatAfterSending", disableChatAfterSending.isChecked());
        configSystem.set("showQuestsOnGameMap", showQuestsOnGameMap.isChecked());
        configSystem.set("showQuestsOnMiniMap", showQuestsOnMiniMap.isChecked());

        configSystem.set(CrashReporter.CFG_KEY, sendCrashReports.getSelectedIndex());

        String resolutionSelection = resolutions.getSelection();

        if (resolutionSelection != null) {
            configSystem.set(IllaClient.CFG_RESOLUTION, resolutionSelection);
        }

        configSystem.set(IllaClient.CFG_FULLSCREEN, fullscreen.isChecked());
        configSystem.set("showFps", showFps.isChecked());
        configSystem.set("showPing", showPing.isChecked());

        configSystem.set(Translator.CFG_KEY_PROVIDER, translationProviders.getSelectedIndex());
        configSystem.set(Translator.CFG_KEY_DIRECTION, translationDirections.getSelectedIndex());

        configSystem.set("soundOn", soundOn.isChecked());
        configSystem.set("soundVolume", soundVolume.getValue());
        configSystem.set("musicOn", musicOn.isChecked());
        configSystem.set("musicVolume", musicVolume.getValue());

        if (serverAddress != null) {
            if (serverResetSettings.isChecked()) {
                configSystem.set("customServer.domain", "localhost");
                configSystem.set("customServer.port", "13000");
                configSystem.set("customServer.accountSystem", false);
            } else {
                configSystem.set("customServer.domain", serverAddress.getRealText());
                configSystem.set("customServer.port", Integer.parseInt(serverPort.getRealText()));
                configSystem.set("customServer.accountSystem", serverAccountLogin.isChecked());
            }
        }

        configSystem.save();
    }

    @NiftyEventSubscriber (id = "musicVolume")
    public void onMusicVolumeSliderChangedEvent(String topic, SliderChangedEvent event){
        AudioPlayer audioPlayer = AudioPlayer.getInstance();
        if(audioPlayer.getMusicVolume() == 0){
            if(musicOn.isChecked() && !audioPlayer.isCurrentMusic(audioPlayer.getLastMusic())) {
                audioPlayer.setMusicVolume(musicVolume.getValue());
                audioPlayer.playLastMusic();
            }
        }else{
            audioPlayer.setMusicVolume(musicVolume.getValue());
        }

    }

    @NiftyEventSubscriber (id = "musicOn")
    public void onMusicOnChangedEvent(String topic, CheckBoxStateChangedEvent event){
        AudioPlayer audioPlayer = AudioPlayer.getInstance();
        if(musicOn.isChecked()) {
            audioPlayer.setMusicVolume(musicVolume.getValue());
            if (!audioPlayer.isCurrentMusic(audioPlayer.getLastMusic())) {
                audioPlayer.playLastMusic();
            }
        } else{
            audioPlayer.setMusicVolume(0.f);
        }
    }

    @NiftyEventSubscriber(id = "cancelButton")
    public void onCancelButtonClickedEvent(String topic, ButtonClickedEvent event) {
        nifty.gotoScreen("login");
    }
}
