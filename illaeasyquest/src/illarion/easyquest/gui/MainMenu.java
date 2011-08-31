/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright 2011 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyQuest Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyQuest Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easyquest.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;

import illarion.easyquest.Lang;

final class MainMenu extends RibbonApplicationMenu {

    public MainMenu() {
        super();
        
        final RibbonApplicationMenuEntryPrimary newQuest =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filenew.png"),
                Lang.getMsg(getClass(), "newQuestButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        MainFrame.getInstance().addNewQuest();
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(newQuest);

        final RibbonApplicationMenuEntryPrimary openQuest =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("fileopen.png"),
                Lang.getMsg(getClass(), "openQuestButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Utils.selectAndOpenQuest();
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(openQuest);

        addMenuSeparator();
        
        final RibbonApplicationMenuEntryPrimary saveQuest =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filesave.png"),
                Lang.getMsg(getClass(), "saveQuestButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Utils.saveEasyQuest(MainFrame.getInstance()
                            .getCurrentQuestEditor());
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(saveQuest);
        
        final RibbonApplicationMenuEntryPrimary saveAllQuest =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("save_all.png"),
                Lang.getMsg(getClass(), "saveAllQuestButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(saveAllQuest);

        final RibbonApplicationMenuEntryPrimary saveAsQuest =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("filesaveas.png"),
                Lang.getMsg(getClass(), "saveAsQuestButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(saveAsQuest);
        
        addMenuSeparator();
        
        final RibbonApplicationMenuEntryPrimary exportQuest =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("fileexport.png"),
                Lang.getMsg(getClass(), "exportQuestButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Utils.exportEasyQuest(MainFrame.getInstance()
                            .getCurrentQuestEditor());
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(exportQuest);
        
        addMenuSeparator();
        
        final RibbonApplicationMenuEntryPrimary exitButton =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("exit.png"),
                Lang.getMsg(getClass(), "exitButton"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        MainFrame.getInstance().closeWindow();
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(exitButton);
    }
}