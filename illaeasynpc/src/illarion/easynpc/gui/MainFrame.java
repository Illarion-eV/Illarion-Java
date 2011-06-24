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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.tabbed.VetoableTabCloseListener;

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.Lang;
import illarion.easynpc.crash.AWTCrashHandler;

import illarion.common.bug.CrashReporter;

/**
 * This is the Main Frame of the display of the easyNPC editor.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public final class MainFrame extends JRibbonFrame { // NO_UCD
    /**
     * The instance of the MainFrame that is used by other parts of the GUI.
     */
    private static MainFrame instance;

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class);

    /**
     * The serialization UID of this main frame.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The listener that listens the editor tabs to be closed and ask the user
     * to save the content of the tab in case its needed.
     */
    private final VetoableTabCloseListener editorTabListener =
        new VetoableTabCloseListener() {
            @Override
            public void tabClosed(final JTabbedPane pane,
                final Component component) {
                ((Editor) component).cleanup();
                if (pane.getTabCount() == 0) {
                    addNewScript();
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
                    Utils.saveEasyNPC(editor);
                    return false;
                }
                return (result == JOptionPane.CANCEL_OPTION);
            }
        };

    /**
     * The area where the error messages are displayed.
     */
    private final ErrorPane errorArea;

    /**
     * The main splitted panel. In the upper part the editor is displayed, the
     * lower part shows the error list.
     */
    private final JSplitPane mainPanel;

    /**
     * The Tab Pane the editors are displayed in.
     */
    private final JTabbedPane tabbedEditorArea;

    /**
     * Default constructor that creates the window and builds all required
     * elements into this window.
     */
    @SuppressWarnings("nls")
    private MainFrame() {
        super("easyNPC Scripteditor");

        final RibbonTask startTask =
            new RibbonTask(Lang.getMsg(getClass(), "ribbonTaskStart"),
                new ClipboardBand(), new SearchBand(), new CompileBand());
        getRibbon().addTask(startTask);

        final JCommandButton saveButton =
            new JCommandButton(
                Utils.getResizableIconFromResource("filesave.png"));
        saveButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
            getClass(), "saveButtonTooltipTitle"), Lang.getMsg(getClass(),
            "saveButtonTooltip")));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Utils.saveEasyNPC(getCurrentScriptEditor());
            }
        });
        getRibbon().addTaskbarComponent(saveButton);

        getRibbon().addTaskbarComponent(
            UndoMonitor.getInstance().getUndoButton());
        getRibbon().addTaskbarComponent(
            UndoMonitor.getInstance().getRedoButton());

        getRibbon().setApplicationMenu(new MainMenu());

        getRibbon().configureHelp(
            Utils.getResizableIconFromResource("help.png"),
            new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    DocuBrowser.showDocuBrowser();
                }
            });

        final JPanel rootPanel = new JPanel(new BorderLayout());
        mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        tabbedEditorArea = new JTabbedPane(SwingConstants.TOP);
        tabbedEditorArea.addChangeListener(UndoMonitor.getInstance());

        final JTabbedPane footerPane = new JTabbedPane(SwingConstants.TOP);
        errorArea = new ErrorPane();
        footerPane.addTab(Lang.getMsg(getClass(), "errorTab"), errorArea); //$NON-NLS-1$
        mainPanel.setBottomComponent(footerPane);
        mainPanel.setTopComponent(tabbedEditorArea);
        footerPane.setPreferredSize(errorArea.getPreferredSize());

        rootPanel.add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(rootPanel);

        pack();

        Config.getInstance().getLastWindowValue(this);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowListener() {

            @Override
            public void windowActivated(final WindowEvent e) {
                // nothing
            }

            @Override
            public void windowClosed(final WindowEvent e) {
                MainFrame.getInstance().dispose();
            }

            @Override
            public void windowClosing(final WindowEvent e) {
                MainFrame.getInstance().closeWindow();
            }

            @Override
            public void windowDeactivated(final WindowEvent e) {
                // nothing
            }

            @Override
            public void windowDeiconified(final WindowEvent e) {
                // nothing
            }

            @Override
            public void windowIconified(final WindowEvent e) {
                // nothing
            }

            @Override
            public void windowOpened(final WindowEvent e) {
                // nothing
            }
        });

        validate();

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("F1"), "displayHelpWindow");
        getRootPane().getActionMap().put("displayHelpWindow", new Action() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                DocuBrowser.showDocuBrowser();
            }

            @Override
            public void addPropertyChangeListener(
                final PropertyChangeListener listener) {
                // nothing
            }

            @Override
            public Object getValue(final String key) {
                return "displayHelpWindow";
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public void putValue(final String key, final Object value) {
                // nothing
            }

            @Override
            public void removePropertyChangeListener(
                final PropertyChangeListener listener) {
                // nothing
            }

            @Override
            public void setEnabled(final boolean b) {
                // nothing
            }
        });

        mainPanel.setDividerLocation(Config.getInstance().getSplitPaneState());

        SubstanceLookAndFeel.registerTabCloseChangeListener(tabbedEditorArea,
            editorTabListener);

        setApplicationIcon(Utils
            .getResizableIconFromResource("easynpc256.png"));

        final String[] lastFiles = Config.getInstance().getOldFiles();
        if ((lastFiles == null) || (lastFiles.length == 0)) {
            addNewScript();
        } else {
            for (final String file : lastFiles) {
                if (file.length() < 3) {
                    continue;
                }
                if (file.equals("new")) { //$NON-NLS-1$
                    continue;
                }
                try {
                    final EasyNpcScript easyScript =
                        new EasyNpcScript(new File(file));
                    addNewScript().loadScript(easyScript);
                    setCurrentTabTitle(easyScript.getSourceScriptFile()
                        .getName());
                } catch (final IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        if (getOpenTabs() == 0) {
            addNewScript();
        }
    }

    /**
     * This function should be used to shutdown the entire editor instantly.
     * Only do this in case there is no other way. All data will be lost.
     * 
     * @param message the error message that is displayed.
     */
    @SuppressWarnings("nls")
    public static void crashEditor(final String message) {
        if (message != null) {
            JOptionPane.showMessageDialog(getInstance(), message,
                Lang.getMsg(MainFrame.class, "crashEditor.Title"),
                JOptionPane.ERROR_MESSAGE);
            LOGGER.fatal("Editor crashed! Fatal error: " + message);
        } else {
            LOGGER.fatal("Editor crashed!");
        }
        System.exit(-1);
    }

    /**
     * Start the GUI of the parser.
     * 
     * @param args start arguments
     */
    public static void main(final String[] args) {
        Config.getInstance().init();

        JFrame.setDefaultLookAndFeelDecorated(Config.getInstance()
            .getUseWindowDecoration());
        JDialog.setDefaultLookAndFeelDecorated(Config.getInstance()
            .getUseWindowDecoration());

        CrashReporter.getInstance().setConfig(
            Config.getInstance().getInternalCfg());
        CrashReporter.getInstance().setDisplay(CrashReporter.DISPLAY_SWING);
        CrashReporter.getInstance().setMessageSource(Lang.getInstance());
        AWTCrashHandler.init();

        SwingUtilities.invokeLater(new Runnable() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                try {
                    SubstanceLookAndFeel.setSkin(Config.getInstance()
                        .getLookAndFeel());
                } catch (final Exception e) {
                    SubstanceLookAndFeel.setSkin(Config.defaultLookAndFeel);
                }

                instance = new MainFrame();
                getInstance().setVisible(true);
            }
        });
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance
     */
    protected static MainFrame getInstance() {
        return instance;
    }

    /**
     * Get the script editor that is currently activated.
     * 
     * @return the currently activated script editor
     */
    public Editor getCurrentScriptEditor() {
        return getScriptEditor(tabbedEditorArea.getSelectedIndex());
    }

    /**
     * Get the index of the current tab.
     * 
     * @return the index of the current tab
     */
    public int getCurrentTab() {
        return tabbedEditorArea.getSelectedIndex();
    }

    /**
     * Get the area the errors are displayed in.
     * 
     * @return the area the errors are displayed in
     */
    public ErrorPane getErrorArea() {
        return errorArea;
    }

    /**
     * Get the amount of currently open tabs.
     * 
     * @return the amount of currently open tabs
     */
    public int getOpenTabs() {
        return tabbedEditorArea.getTabCount();
    }

    /**
     * Set the title of the currently selected tab.
     * 
     * @param title the title of the currently selected tab
     */
    public void setCurrentTabTitle(final String title) {
        setTabTitle(tabbedEditorArea.getSelectedIndex(), title);
    }

    /**
     * Add a new, empty script to the editors.
     * 
     * @return the editor that was now just created
     */
    @SuppressWarnings("nls")
    protected Editor addNewScript() {
        final Editor editor = new Editor();
        editor.putClientProperty(
            SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY,
            Boolean.TRUE);
        tabbedEditorArea.insertTab(Lang.getMsg(getClass(), "newScriptTab"),
            null, editor, null, tabbedEditorArea.getTabCount());
        tabbedEditorArea.setSelectedIndex(tabbedEditorArea.getTabCount() - 1);
        return editor;
    }

    /**
     * Check if a script file is already opened in a editor and return the index
     * of the editor in case it is.
     * 
     * @param file the file that is to be opened
     * @return the index of the editor that opened this file or -1 in case its
     *         not opened yet
     */
    protected int alreadyOpen(final File file) {
        final int count = tabbedEditorArea.getComponentCount();
        Editor currentComp;
        for (int i = 0; i < count; i++) {
            currentComp = (Editor) tabbedEditorArea.getComponent(i);
            if ((currentComp.getScriptFile() != null)
                && currentComp.getScriptFile().equals(file)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Close this window. This also causes to check the changed and opened
     * editors and save them in case its needed.
     */
    @SuppressWarnings("nls")
    protected void closeWindow() {
        final int tabCount = getOpenTabs();

        final String[] fileList = new String[tabCount];
        Editor editor;
        for (int i = 0; i < tabCount; i++) {
            editor = getScriptEditor(i);
            if (editor.getScriptFile() == null) {
                fileList[i] = "new";
            } else {
                fileList[i] = editor.getScriptFile().getAbsolutePath();
            }
            if (!editor.changedSinceSave()) {
                continue;
            }

            final Object[] options =
                new Object[] {
                    Lang.getMsg(MainFrame.class, "UnsavedChanges.saveButton"),
                    Lang.getMsg(MainFrame.class,
                        "UnsavedChanges.discardButton"),
                    Lang.getMsg(MainFrame.class, "UnsavedChanges.cancelButton") };
            final int result =
                JOptionPane.showOptionDialog(this, String.format(
                    Lang.getMsg(MainFrame.class, "UnsavedChanges.message2"),
                    editor.getFileName()), Lang.getMsg(MainFrame.class,
                    "UnsavedChanges.title"), JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, options, options[0]);

            if (result == JOptionPane.YES_OPTION) {
                Utils.saveEasyNPC(editor);
                fileList[i] = editor.getScriptFile().getAbsolutePath();
                continue;
            }
            if (result == JOptionPane.NO_OPTION) {
                continue;
            }
            return;
        }

        Config.getInstance().setOldFiles(fileList);
        Config.getInstance().setLastWindowValues(this);
        Config.getInstance().setSplitPaneState(
            (double) mainPanel.getDividerLocation()
                / (double) mainPanel.getHeight());

        setVisible(false);
        dispose();

        Config.getInstance().save();
        System.exit(0);
    }

    /**
     * Get the script editor that is attached to a specified tab.
     * 
     * @param index the tab index
     * @return the editor attached to this tab index
     */
    protected Editor getScriptEditor(final int index) {
        return (Editor) tabbedEditorArea.getComponentAt(index);
    }

    /**
     * Set the tab that should be displayed now by its index.
     * 
     * @param index the index of the tab to display
     */
    protected void setCurrentEditorTab(final int index) {
        tabbedEditorArea.setSelectedIndex(index);

        UndoMonitor.getInstance().updateUndoRedo(
            getScriptEditor(index).getUndoManager());
    }

    /**
     * Set the title of a tab that holds a specified component.
     * 
     * @param component the component in the tab
     * @param title the new title of the tab
     */
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

    /**
     * Set the title of a tab at the specified index.
     * 
     * @param index the index of the tab thats title shall change
     * @param title the new title for the tab
     */
    private void setTabTitle(final int index, final String title) {
        tabbedEditorArea.setTitleAt(index, title);
    }
}
