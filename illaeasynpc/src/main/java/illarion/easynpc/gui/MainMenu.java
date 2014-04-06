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
package illarion.easynpc.gui;

import illarion.common.config.ConfigDialog;
import illarion.common.config.gui.ConfigDialogSwing;
import illarion.easynpc.Lang;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryFooter;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntrySecondary;

import javax.annotation.Nullable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;

/**
 * This class prepares the application menu of the editor that offers access to
 * some basic functions.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class MainMenu extends RibbonApplicationMenu {
    /**
     * Constructor of the main menu that loads up the menu.
     */
    public MainMenu() {

        RibbonApplicationMenuEntryPrimary newScriptEntry = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filenew.png"), Lang.getMsg(getClass(), "newScriptButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        MainFrame.getInstance().addNewScript();
                    }
                }, CommandButtonKind.ACTION_ONLY
        );
        addMenuEntry(newScriptEntry);

        RibbonApplicationMenuEntryPrimary openScriptEntry = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("fileopen.png"), Lang.getMsg(getClass(), "openScriptButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Utils.selectAndOpenScript();
                    }
                }, CommandButtonKind.ACTION_ONLY
        );

        Path[] oldFiles = Config.getInstance().getLastOpenedFiles();
        RibbonApplicationMenuEntrySecondary[] workingEntries = new RibbonApplicationMenuEntrySecondary[oldFiles.length];
        int entryIndex = 0;
        for (@Nullable final Path openFile : oldFiles) {
            if (openFile == null) {
                continue;
            }
            workingEntries[entryIndex] = new RibbonApplicationMenuEntrySecondary(
                    Utils.getResizableIconFromResource("source.png"), openFile.getFileName().toString(),
                    new ActionListener() {
                        @Nullable
                        private final Path fileToOpen = openFile;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Utils.openScript(fileToOpen);
                        }
                    }, CommandButtonKind.ACTION_ONLY
            );
            entryIndex++;
        }

        if (entryIndex > 0) {
            RibbonApplicationMenuEntrySecondary[] entries = new RibbonApplicationMenuEntrySecondary[entryIndex];
            System.arraycopy(workingEntries, 0, entries, 0, entryIndex);
            openScriptEntry.addSecondaryMenuGroup(Lang.getMsg(getClass(), "oldFilesHead"), entries);
        }
        addMenuEntry(openScriptEntry);

        addMenuSeparator();

        RibbonApplicationMenuEntryPrimary saveScript = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filesave.png"), Lang.getMsg(getClass(), "saveScriptButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Utils.saveEasyNPC(MainFrame.getInstance().getCurrentScriptEditor());
                    }
                }, CommandButtonKind.ACTION_ONLY
        );
        addMenuEntry(saveScript);

        RibbonApplicationMenuEntryPrimary saveAllScript = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("save_all.png"), Lang.getMsg(getClass(), "saveAllScriptButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int count = MainFrame.getInstance().getOpenTabs();

                        for (int i = 0; i < count; i++) {
                            Utils.saveEasyNPC(MainFrame.getInstance().getScriptEditor(i));
                        }
                    }
                }, CommandButtonKind.ACTION_ONLY
        );
        addMenuEntry(saveAllScript);

        RibbonApplicationMenuEntryPrimary saveAsScript = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filesaveas.png"), Lang.getMsg(getClass(), "saveAsScriptButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Utils.selectAndSaveEasyNPC(MainFrame.getInstance().getCurrentScriptEditor());
                    }
                }, CommandButtonKind.ACTION_ONLY
        );
        addMenuEntry(saveAsScript);

        RibbonApplicationMenuEntryPrimary saveLuaScript = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filesavecompile.png"),
                Lang.getMsg(getClass(), "saveLuaScriptButton"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Utils.saveLuaScript(MainFrame.getInstance().getCurrentScriptEditor());
            }
        }, CommandButtonKind.ACTION_ONLY
        );
        addMenuEntry(saveLuaScript);

        RibbonApplicationMenuEntryPrimary uploadLuaScript = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("upload.png"), Lang.getMsg(getClass(), "uploadLuaScriptButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Utils.uploadLuaScript(MainFrame.getInstance().getCurrentScriptEditor());
                    }
                }, CommandButtonKind.ACTION_ONLY
        );
        addMenuEntry(uploadLuaScript);

        addMenuSeparator();

        RibbonApplicationMenuEntryPrimary exitButton = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("exit.png"), Lang.getMsg(getClass(), "exitButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        MainFrame.getInstance().closeWindow();
                    }
                }, CommandButtonKind.ACTION_ONLY
        );
        addMenuEntry(exitButton);

        RibbonApplicationMenuEntryFooter settings = new RibbonApplicationMenuEntryFooter(
                Utils.getResizableIconFromResource("configure.png"), Lang.getMsg(getClass(), "settingsButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ConfigDialog dialog = Config.getInstance().createDialog();
                        new ConfigDialogSwing(dialog);
                    }
                }
        );
        addFooterEntry(settings);
    }
}
