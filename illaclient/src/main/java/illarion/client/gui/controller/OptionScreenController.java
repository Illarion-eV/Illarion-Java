/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.IllaClient;
import illarion.client.Servers;
import illarion.common.bug.CrashReporter;
import illarion.common.config.Config;
import org.illarion.engine.DesktopGameContainer;
import org.illarion.engine.graphic.GraphicResolution;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class OptionScreenController implements ScreenController {

    private Nifty nifty;
    private Screen screen;

    //private DropDown<String> charNameLength;
    //private CheckBox showCharId;
    private CheckBox wasdWalk;
    private CheckBox classicWalk;
    private CheckBox disableChatAfterSending;
    private CheckBox showQuestsOnGameMap;
    private CheckBox showQuestsOnMiniMap;

    private DropDown<String> sendCrashReports;

    private DropDown<String> resolutions;
    private CheckBox fullscreen;
    private CheckBox showFps;
    private CheckBox showPing;

    private CheckBox soundOn;
    private Slider soundVolume;
    private CheckBox musicOn;
    private Slider musicVolume;

    private TextField serverAddress;
    private TextField serverPort;
    private TextField clientVersion;
    private CheckBox serverAccountLogin;
    private CheckBox serverResetSettings;
    private CheckBox walkAsDefault;
    private CheckBox mouseFollowAutoRun;

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
        classicWalk = tabRoot.findNiftyControl("classicWalk", CheckBox.class);
        walkAsDefault = tabRoot.findNiftyControl("walkAsDefault", CheckBox.class);
        mouseFollowAutoRun = tabRoot.findNiftyControl("mouseFollowAutoRun", CheckBox.class);

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

        soundOn = tabRoot.findNiftyControl("soundOn", CheckBox.class);
        soundVolume = tabRoot.findNiftyControl("soundVolume", Slider.class);
        musicOn = tabRoot.findNiftyControl("musicOn", CheckBox.class);
        musicVolume = tabRoot.findNiftyControl("musicVolume", Slider.class);

        Element serverTab = tabRoot.findElementById("#serverTab");
        if (serverTab == null) {
            return;
        }
        if (IllaClient.DEFAULT_SERVER == Servers.realserver) {
            tabRoot.getNiftyControl(TabGroup.class).removeTab(serverTab);
        } else {
            serverAddress = serverTab.findNiftyControl("serverAddress", TextField.class);
            serverPort = serverTab.findNiftyControl("serverPort", TextField.class);
            clientVersion = serverTab.findNiftyControl("clientVersion", TextField.class);
            serverAccountLogin = serverTab.findNiftyControl("serverAccountLogin", CheckBox.class);
            serverResetSettings = serverTab.findNiftyControl("resetServerSettings", CheckBox.class);
        }
    }

    @Override
    public void onStartScreen() {
        //charNameLength.selectItemByIndex(IllaClient.getCfg().getInteger(People.CFG_NAMEMODE_KEY) - 1);
        //showCharId.setChecked(IllaClient.getCfg().getBoolean(People.CFG_SHOWID_KEY));
        wasdWalk.setChecked(IllaClient.getCfg().getBoolean("wasdWalk"));
        classicWalk.setChecked(IllaClient.getCfg().getBoolean("classicWalk"));
        walkAsDefault.setChecked(IllaClient.getCfg().getBoolean("walkAsDefault"));
        mouseFollowAutoRun.setChecked(IllaClient.getCfg().getBoolean("mouseFollowAutoRun"));
        disableChatAfterSending.setChecked(IllaClient.getCfg().getBoolean("disableChatAfterSending"));
        showQuestsOnGameMap.setChecked(IllaClient.getCfg().getBoolean("showQuestsOnGameMap"));
        showQuestsOnMiniMap.setChecked(IllaClient.getCfg().getBoolean("showQuestsOnMiniMap"));

        sendCrashReports.selectItemByIndex(IllaClient.getCfg().getInteger(CrashReporter.CFG_KEY));
        resolutions.selectItem(IllaClient.getCfg().getString(IllaClient.CFG_RESOLUTION));
        fullscreen.setChecked(IllaClient.getCfg().getBoolean(IllaClient.CFG_FULLSCREEN));
        showFps.setChecked(IllaClient.getCfg().getBoolean("showFps"));
        showPing.setChecked(IllaClient.getCfg().getBoolean("showPing"));

        soundOn.setChecked(IllaClient.getCfg().getBoolean("soundOn"));
        soundVolume.setValue(IllaClient.getCfg().getFloat("soundVolume"));
        musicOn.setChecked(IllaClient.getCfg().getBoolean("musicOn"));
        musicVolume.setValue(IllaClient.getCfg().getFloat("musicVolume"));

        if (serverAddress != null) {
            serverAddress.setText(IllaClient.getCfg().getString("serverAddress"));
            serverPort.setText(Integer.toString(IllaClient.getCfg().getInteger("serverPort")));
            clientVersion.setText(Integer.toString(IllaClient.getCfg().getInteger("clientVersion")));
            serverAccountLogin.setChecked(IllaClient.getCfg().getBoolean("serverAccountLogin"));
            serverResetSettings.setChecked(false);
        }
    }

    @NiftyEventSubscriber(id = "saveButton")
    public void onSaveButtonClickedEvent(String topic, ButtonClickedEvent event) {
        nifty.gotoScreen("login");
        Config configSystem = IllaClient.getCfg();

        //configSystem.set(People.CFG_NAMEMODE_KEY, charNameLength.getSelectedIndex() + 1);
        //configSystem.set(People.CFG_SHOWID_KEY, showCharId.isChecked());
        configSystem.set("wasdWalk", wasdWalk.isChecked());
        configSystem.set("classicWalk", classicWalk.isChecked());
        configSystem.set("walkAsDefault", walkAsDefault.isChecked());
        configSystem.set("mouseFollowAutoRun", mouseFollowAutoRun.isChecked());
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

        configSystem.set("soundOn", soundOn.isChecked());
        configSystem.set("soundVolume", soundVolume.getValue());
        configSystem.set("musicOn", musicOn.isChecked());
        configSystem.set("musicVolume", musicVolume.getValue());

        if (serverAddress != null) {
            if (serverResetSettings.isChecked()) {
                configSystem.set("serverAddress", Servers.customserver.getServerHost());
                configSystem.set("serverPort", Servers.customserver.getServerPort());
                configSystem.set("clientVersion", Servers.customserver.getClientVersion());
                configSystem.set("serverAccountLogin", true);
            } else {
                configSystem.set("serverAddress", serverAddress.getRealText());
                configSystem.set("serverPort", Integer.parseInt(serverPort.getRealText()));
                configSystem.set("clientVersion", Integer.parseInt(clientVersion.getRealText()));
                configSystem.set("serverAccountLogin", serverAccountLogin.isChecked());
            }
        }

        configSystem.save();
    }

    @NiftyEventSubscriber(id = "cancelButton")
    public void onCancelButtonClickedEvent(String topic, ButtonClickedEvent event) {
        nifty.gotoScreen("login");
    }

    @Override
    public void onEndScreen() {
    }

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
}
