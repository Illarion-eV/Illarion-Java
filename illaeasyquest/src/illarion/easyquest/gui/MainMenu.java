package illarion.easyquest.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;

final class MainMenu extends RibbonApplicationMenu {

    public MainMenu() {
        super();
        
        final RibbonApplicationMenuEntryPrimary newQuest =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("res/filenew.png"),
                "Neues Quest",
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        //MainFrame.getInstance().addNewScript();
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(newQuest);

        final RibbonApplicationMenuEntryPrimary openQuest =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("res/fileopen.png"),
                "Quest öffnen",
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
                Utils.getResizableIconFromResource("res/filesave.png"),
                "Quest speichern",
                new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        //Utils.saveEasyNPC(MainFrame.getInstance()
                        //    .getCurrentScriptEditor());
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(saveQuest);
        
        addMenuSeparator();
        
        final RibbonApplicationMenuEntryPrimary exitButton =
            new RibbonApplicationMenuEntryPrimary(
                Utils.getResizableIconFromResource("res/exit.png"), "Beenden", new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        MainFrame.getInstance().closeWindow();
                    }
                }, CommandButtonKind.ACTION_ONLY);
        addMenuEntry(exitButton);
    }
}