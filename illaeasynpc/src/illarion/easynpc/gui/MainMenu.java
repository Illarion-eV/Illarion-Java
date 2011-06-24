/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryFooter;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntrySecondary;

import illarion.easynpc.Lang;

/**
 * This class prepares the application menu of the editor that offers access to
 * some basic functions.
 * 
 * @author Martin Karing
 * @since 1.01
 */
final class MainMenu extends RibbonApplicationMenu {
    /**
     * Constructor of the main menu that loads up the menu.
     */
    public MainMenu() {
        super();

        final RibbonApplicationMenuEntryPrimary newScriptEntry =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filenew.png"),
                Lang.getMsg(getClass(), "newScriptButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        MainFrame.getInstance().addNewScript();
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(newScriptEntry);

        final RibbonApplicationMenuEntryPrimary openScriptEntry =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("fileopen.png"),
                Lang.getMsg(getClass(), "openScriptButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Utils.selectAndOpenScript();
                    }
                }, CommandButtonKind.ACTION_ONLY);

        final File[] oldFiles = Config.getInstance().getLastOpenedFiles();
        final RibbonApplicationMenuEntrySecondary[] workingEntries =
            new RibbonApplicationMenuEntrySecondary[oldFiles.length];
        int entryIndex = 0;
        for (final File openFile : oldFiles) {
            if (openFile == null) {
                continue;
            }
            workingEntries[entryIndex] =
                new RibbonApplicationMenuEntrySecondary(
                    Utils.getResizableIconFromResource("source.png"),
                    openFile.getName(), new ActionListener() {
                        private final File fileToOpen = openFile;

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            Utils.openScript(fileToOpen);
                        }

                    }, CommandButtonKind.ACTION_ONLY);
            entryIndex++;
        }

        if (entryIndex > 0) {
            final RibbonApplicationMenuEntrySecondary[] entries =
                new RibbonApplicationMenuEntrySecondary[entryIndex];
            System.arraycopy(workingEntries, 0, entries, 0, entryIndex);
            openScriptEntry.addSecondaryMenuGroup(
                Lang.getMsg(getClass(), "oldFilesHead"), entries);
        }
        addMenuEntry(openScriptEntry);

        addMenuSeparator();

        final RibbonApplicationMenuEntryPrimary saveScript =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filesave.png"),
                Lang.getMsg(getClass(), "saveScriptButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Utils.saveEasyNPC(MainFrame.getInstance()
                            .getCurrentScriptEditor());
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(saveScript);

        final RibbonApplicationMenuEntryPrimary saveAllScript =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("save_all.png"),
                Lang.getMsg(getClass(), "saveAllScriptButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final int count =
                            MainFrame.getInstance().getOpenTabs();

                        for (int i = 0; i < count; i++) {
                            Utils.saveEasyNPC(MainFrame.getInstance()
                                .getScriptEditor(i));
                        }
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(saveAllScript);

        final RibbonApplicationMenuEntryPrimary saveAsScript =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filesaveas.png"),
                Lang.getMsg(getClass(), "saveAsScriptButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Utils.selectAndSaveEasyNPC(MainFrame.getInstance()
                            .getCurrentScriptEditor());
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(saveAsScript);

        final RibbonApplicationMenuEntryPrimary saveLuaScript =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filesavecompile.png"),
                Lang.getMsg(getClass(), "saveLuaScriptButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Utils.saveLuaScript(MainFrame.getInstance()
                            .getCurrentScriptEditor());
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(saveLuaScript);

        final RibbonApplicationMenuEntryPrimary uploadLuaScript =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("upload.png"), Lang.getMsg(
                    getClass(), "uploadLuaScriptButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Utils.uploadLuaScript(MainFrame.getInstance()
                            .getCurrentScriptEditor());
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(uploadLuaScript);

        addMenuSeparator();

        final RibbonApplicationMenuEntryPrimary exitButton =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("exit.png"), Lang.getMsg(
                    getClass(), "exitButton"), new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        MainFrame.getInstance().closeWindow();
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(exitButton);

        final RibbonApplicationMenuEntryFooter settings =
            new RibbonApplicationMenuEntryFooter(
                Utils.getResizableIconFromResource("configure.png"),
                Lang.getMsg(getClass(), "settingsButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Config.getInstance().createDialog().show();
                    }
                });
        addFooterEntry(settings);
    }
}
