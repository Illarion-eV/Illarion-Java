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
                        //MainFrame.getInstance().addNewScript();
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
                        //Utils.selectAndOpenScript();
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
                        //Utils.saveEasyNPC(MainFrame.getInstance()
                        //    .getCurrentScriptEditor());
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