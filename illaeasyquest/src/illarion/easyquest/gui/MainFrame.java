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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.tabbed.VetoableTabCloseListener;

import illarion.easyquest.Lang;

@SuppressWarnings("serial")
public class MainFrame extends JRibbonFrame
{
    
    private final VetoableTabCloseListener editorTabListener =
        new VetoableTabCloseListener() {
            @Override
            public void tabClosed(final JTabbedPane pane,
                final Component component) {
                //((Editor) component).cleanup();
                if (pane.getTabCount() == 0) {
                    addNewQuest();
                }
            }

            @Override
            public void tabClosing(final JTabbedPane pane,
                final Component component) {
                // nothing
            }

            @SuppressWarnings("nls")
            @Override
            public boolean vetoTabClosing(final JTabbedPane pane,
                final Component component) {
                final Editor editor = (Editor) component;
                if (!editor.changedSinceSave()) {
                    return false;
                }

                final Object[] options =
                    new Object[] {
                        Lang.getMsg(MainFrame.class,
                            "UnsavedChanges.saveButton"),
                        Lang.getMsg(MainFrame.class,
                            "UnsavedChanges.discardButton"),
                        Lang.getMsg(MainFrame.class,
                            "UnsavedChanges.cancelButton") };
                final int result =
                    JOptionPane
                        .showOptionDialog(MainFrame.getInstance(),
                            Lang.getMsg(MainFrame.class,
                                "UnsavedChanges.message"), Lang.getMsg(
                                MainFrame.class, "UnsavedChanges.title"),
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE, null, options,
                            options[0]);

                if (result == JOptionPane.YES_OPTION) {
                    Utils.saveEasyQuest(editor);
                    return false;
                }
                return (result == JOptionPane.CANCEL_OPTION);
            }
        };

    private static MainFrame instance;
    
    private final JTabbedPane tabbedEditorArea;

	public MainFrame()
	{
		super("easyQuest Editor");
		
		setApplicationIcon(Utils
            .getResizableIconFromResource("easyquest.png"));
		
		@SuppressWarnings("unused")
		final RibbonTask graphTask =
            new RibbonTask(Lang.getMsg(getClass(), "ribbonTaskQuest"),
                new ClipboardBand(), new GraphBand());
        // getRibbon().addTask(graphTask);

        getRibbon().setApplicationMenu(new MainMenu());

        final JCommandButton saveButton =
            new JCommandButton(Utils.getResizableIconFromResource("filesave.png"));
        saveButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
            getClass(), "saveButtonTooltipTitle"), Lang.getMsg(getClass(),
            "saveButtonTooltip")));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Utils.saveEasyQuest(getCurrentQuestEditor());
            }
        });
        getRibbon().addTaskbarComponent(saveButton);
        
        final JCommandButton undoButton =
            new JCommandButton(Utils.getResizableIconFromResource("undo.png"));
        undoButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
            getClass(), "undoButtonTooltipTitle"), Lang.getMsg(getClass(),
            "undoButtonTooltip")));
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                getCurrentQuestEditor().getUndoManager().undo();
            }
        });
        getRibbon().addTaskbarComponent(undoButton);
        
        final JCommandButton redoButton =
            new JCommandButton(Utils.getResizableIconFromResource("redo.png"));
        redoButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
            getClass(), "redoButtonTooltipTitle"), Lang.getMsg(getClass(),
            "redoButtonTooltip")));
        redoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
            	getCurrentQuestEditor().getUndoManager().redo();
            }
        });
        getRibbon().addTaskbarComponent(redoButton);

        final JPanel rootPanel = new JPanel(new BorderLayout());
        tabbedEditorArea = new JTabbedPane(SwingConstants.TOP);
        rootPanel.add(tabbedEditorArea, BorderLayout.CENTER);
        SubstanceLookAndFeel.registerTabCloseChangeListener(tabbedEditorArea,
            editorTabListener);

        getContentPane().add(rootPanel);
        
        pack();
		
		if (getOpenTabs() == 0) {
            addNewQuest();
        }
	}

	public static void main(String[] args)
	{
	    JRibbonFrame.setDefaultLookAndFeelDecorated(true);
	    JDialog.setDefaultLookAndFeelDecorated(true);
	    
	    SwingUtilities.invokeLater(new Runnable() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
        		SubstanceLookAndFeel.setSkin(
        		    "org.pushingpixels.substance.api.skin.OfficeSilver2007Skin");	    
        		    
        	    instance = new MainFrame();
        		getInstance().setDefaultCloseOperation(JRibbonFrame.EXIT_ON_CLOSE);
        		getInstance().setSize(1204, 768);
        		getInstance().setLocationRelativeTo(null);
        		getInstance().setVisible(true);
        	}
        });
	}

    protected static MainFrame getInstance() {
        return instance;
    }
    
    public Editor getCurrentQuestEditor() {
        return getQuestEditor(tabbedEditorArea.getSelectedIndex());
    }
    
    protected Editor getQuestEditor(final int index) {
        return (Editor) tabbedEditorArea.getComponentAt(index);
    }

    protected void closeWindow() {

        setVisible(false);
        dispose();

        System.exit(0);
    }

    public int getOpenTabs() {
        return tabbedEditorArea.getTabCount();
    }
    
    public void setCurrentTabTitle(final String title) {
        setTabTitle(tabbedEditorArea.getSelectedIndex(), title);
    }
    

    protected Editor addNewQuest(String quest) {
        final Editor editor = Editor.loadQuest(quest);
        editor.putClientProperty(
            SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY,
            Boolean.TRUE);
        tabbedEditorArea.insertTab(Lang.getMsg(getClass(), "newQuestTab"),
            null, editor, null, tabbedEditorArea.getTabCount());
        tabbedEditorArea.setSelectedIndex(tabbedEditorArea.getTabCount() - 1);
        return editor;
    }
    protected Editor addNewQuest() {
        return addNewQuest("");
    }
    
    protected int alreadyOpen(final File file) {
        final int count = tabbedEditorArea.getComponentCount();
        Editor currentComp;
        for (int i = 0; i < count; i++) {
            currentComp = (Editor) tabbedEditorArea.getComponent(i);
            if ((currentComp.getQuestFile() != null)
                && currentComp.getQuestFile().equals(file)) {
                return i;
            }
        }
        return -1;
    }
    
    protected void setCurrentEditorTab(final int index) {
        tabbedEditorArea.setSelectedIndex(index);

        //UndoMonitor.getInstance().updateUndoRedo(
        //    getScriptEditor(index).getUndoManager());
    }

    protected void setTabTitle(final Editor component, final String title) {
        final int count = tabbedEditorArea.getComponentCount();
        Editor currentComp;
        for (int i = 0; i < count; i++) {
            currentComp = (Editor) tabbedEditorArea.getComponent(i);
            if (currentComp.equals(component)) {
                setTabTitle(i, title);
            }
        }
    }

    private void setTabTitle(final int index, final String title) {
        tabbedEditorArea.setTitleAt(index, title);
    }
}
