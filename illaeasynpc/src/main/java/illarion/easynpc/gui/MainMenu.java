/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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
    public MainMenu(@Nonnull MainFrame frame) {

        RibbonApplicationMenuEntryPrimary newScriptEntry = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filenew.png"), Lang.getMsg(getClass(), "newScriptButton"),
                e -> frame.addNewScript(), CommandButtonKind.ACTION_AND_POPUP_MAIN_ACTION
        );
        newScriptEntry.addSecondaryMenuGroup(Lang.getMsg(MainMenu.class, "templates"),
                                             new RibbonApplicationMenuEntrySecondary(
                                                     Utils.getResizableIconFromResource("source.png"),
                                                     Lang.getMsg(MainMenu.class, "template1"), e -> frame.addNewScript(loadTemplate("template1")), CommandButtonKind.ACTION_ONLY
                                             )
        );
        addMenuEntry(newScriptEntry);

        RibbonApplicationMenuEntryPrimary openScriptEntry = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("fileopen.png"), Lang.getMsg(getClass(), "openScriptButton"),
                e -> Utils.selectAndOpenScript(frame), CommandButtonKind.ACTION_AND_POPUP_MAIN_ACTION
        );

        Path[] oldFiles = Config.getInstance().getLastOpenedFiles();
        RibbonApplicationMenuEntrySecondary[] workingEntries = new RibbonApplicationMenuEntrySecondary[oldFiles.length];
        int entryIndex = 0;
        for (@Nullable Path openFile : oldFiles) {
            if (openFile == null) {
                continue;
            }
            workingEntries[entryIndex] = new RibbonApplicationMenuEntrySecondary(
                    Utils.getResizableIconFromResource("source.png"), openFile.getFileName().toString(),
                    new ActionListener() {
                        @Nonnull
                        private final Path fileToOpen = openFile;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Utils.openScript(frame, fileToOpen);
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
                e -> Utils.saveEasyNPC(frame, frame.getCurrentScriptEditor()), CommandButtonKind.ACTION_ONLY
        );
        addMenuEntry(saveScript);

        RibbonApplicationMenuEntryPrimary saveAllScript = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("save_all.png"), Lang.getMsg(getClass(), "saveAllScriptButton"),
                e -> {
                    int count = frame.getOpenTabs();

                    for (int i = 0; i < count; i++) {
                        Utils.saveEasyNPC(frame, frame.getScriptEditor(i));
                    }
                }, CommandButtonKind.ACTION_ONLY
        );
        addMenuEntry(saveAllScript);

        RibbonApplicationMenuEntryPrimary saveAsScript = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filesaveas.png"), Lang.getMsg(getClass(), "saveAsScriptButton"),
                e -> Utils.selectAndSaveEasyNPC(frame, frame.getCurrentScriptEditor()), CommandButtonKind.ACTION_ONLY
        );
        addMenuEntry(saveAsScript);

        RibbonApplicationMenuEntryPrimary saveLuaScript = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filesavecompile.png"),
                Lang.getMsg(getClass(), "saveLuaScriptButton"), e -> Utils.saveLuaScript(frame, frame.getCurrentScriptEditor()), CommandButtonKind.ACTION_ONLY
        );
        addMenuEntry(saveLuaScript);

        RibbonApplicationMenuEntryPrimary uploadLuaScript = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("upload.png"), Lang.getMsg(getClass(), "uploadLuaScriptButton"),
                e -> Utils.uploadLuaScript(frame, frame.getCurrentScriptEditor()), CommandButtonKind.ACTION_ONLY
        );
        addMenuEntry(uploadLuaScript);

        addMenuSeparator();

        RibbonApplicationMenuEntryPrimary exitButton = new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("exit.png"), Lang.getMsg(getClass(), "exitButton"),
                e -> frame.closeWindow(), CommandButtonKind.ACTION_ONLY
        );
        addMenuEntry(exitButton);

        RibbonApplicationMenuEntryFooter settings = new RibbonApplicationMenuEntryFooter(
                Utils.getResizableIconFromResource("configure.png"), Lang.getMsg(getClass(), "settingsButton"),
                e -> {
                    ConfigDialog dialog = Config.getInstance().createDialog();
                    new ConfigDialogSwing(dialog);
                }
        );
        addFooterEntry(settings);
    }

    @Nullable
    private static String loadTemplate(@Nonnull String templateName) {
        String templateFileName = templateName + ".npc";
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            URL templateURL = cl.getResource(templateFileName);
            if (templateURL == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader stream = new BufferedReader(
                    new InputStreamReader(templateURL.openStream(), "ISO-8859-1"))) {
                String line = stream.readLine();
                while (line != null) {
                    sb.append(line);
                    line = stream.readLine();
                    if (line != null) {
                        sb.append('\n');
                    }
                }
            }
            return sb.toString();
        } catch (@Nonnull IOException e) {
            return null;
        }
    }
}
