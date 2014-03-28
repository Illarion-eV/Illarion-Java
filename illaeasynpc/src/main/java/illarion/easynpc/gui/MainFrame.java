/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyNPC Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyNPC Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.gui;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import illarion.common.bug.CrashReporter;
import illarion.common.util.DirectoryManager;
import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.Lang;
import illarion.easynpc.crash.AWTCrashHandler;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.tabbed.VetoableTabCloseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This is the Main Frame of the display of the easyNPC editor.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MainFrame extends JRibbonFrame { // NO_UCD
    /**
     * The instance of the MainFrame that is used by other parts of the GUI.
     */
    private static MainFrame instance;

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);

    /**
     * The serialization UID of this main frame.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The area where the error messages are displayed.
     */
    @Nonnull
    private final ErrorPane errorArea;

    /**
     * The main splitted panel. In the upper part the editor is displayed, the
     * lower part shows the error list.
     */
    @Nonnull
    private final JSplitPane mainPanel;

    /**
     * The Tab Pane the editors are displayed in.
     */
    @Nonnull
    private final JTabbedPane tabbedEditorArea;

    /**
     * Default constructor that creates the window and builds all required
     * elements into this window.
     */
    @SuppressWarnings("nls")
    private MainFrame() {
        super("easyNPC Scripteditor");

        final RibbonTask startTask = new RibbonTask(Lang.getMsg(getClass(), "ribbonTaskStart"), new ClipboardBand(),
                                                    new SearchBand(), new CompileBand());
        getRibbon().addTask(startTask);

        final JCommandButton saveButton = new JCommandButton(Utils.getResizableIconFromResource("filesave.png"));
        saveButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "saveButtonTooltipTitle"),
                                                        Lang.getMsg(getClass(), "saveButtonTooltip")));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Utils.saveEasyNPC(getCurrentScriptEditor());
            }
        });
        getRibbon().addTaskbarComponent(saveButton);

        getRibbon().addTaskbarComponent(UndoMonitor.getInstance().getUndoButton());
        getRibbon().addTaskbarComponent(UndoMonitor.getInstance().getRedoButton());

        getRibbon().setApplicationMenu(new MainMenu());

        getRibbon().configureHelp(Utils.getResizableIconFromResource("help.png"), new ActionListener() {
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
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(final WindowEvent e) {
                MainFrame.getInstance().dispose();
            }

            @Override
            public void windowClosing(final WindowEvent e) {
                MainFrame.getInstance().closeWindow();
            }
        });

        validate();

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("F1"), "displayHelpWindow");
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

            @Nonnull
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

        /*
      The listener that listens the editor tabs to be closed and ask the user
      to save the content of the tab in case its needed.
     */
        VetoableTabCloseListener editorTabListener = new VetoableTabCloseListener() {
            @Override
            public void tabClosed(
                    @Nonnull final JTabbedPane pane, @Nonnull final Component component) {
                ((Editor) component).cleanup();
                if (pane.getTabCount() == 0) {
                    addNewScript();
                }
            }

            @Override
            public void tabClosing(
                    final JTabbedPane pane, final Component component) {
                // nothing
            }

            @SuppressWarnings("nls")
            @Override
            public boolean vetoTabClosing(
                    final JTabbedPane pane, final Component component) {
                final Editor editor = (Editor) component;
                if (!editor.changedSinceSave()) {
                    return false;
                }

                final Object[] options = {Lang.getMsg(MainFrame.class, "UnsavedChanges.saveButton"),
                                          Lang.getMsg(MainFrame.class, "UnsavedChanges.discardButton"),
                                          Lang.getMsg(MainFrame.class, "UnsavedChanges.cancelButton")};
                final int result = JOptionPane.showOptionDialog(MainFrame.getInstance(),
                                                                Lang.getMsg(MainFrame.class, "UnsavedChanges.message"),
                                                                Lang.getMsg(MainFrame.class, "UnsavedChanges.title"),
                                                                JOptionPane.YES_NO_CANCEL_OPTION,
                                                                JOptionPane.WARNING_MESSAGE, null, options, options[0]);

                if (result == JOptionPane.YES_OPTION) {
                    Utils.saveEasyNPC(editor);
                    return false;
                }
                return (result == JOptionPane.CANCEL_OPTION);
            }
        };
        SubstanceLookAndFeel.registerTabCloseChangeListener(tabbedEditorArea, editorTabListener);

        setApplicationIcon(Utils.getResizableIconFromResource("easynpc256.png"));

        final String[] lastFiles = Config.getInstance().getOldFiles();
        if ((lastFiles.length == 0)) {
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
                    final EasyNpcScript easyScript = new EasyNpcScript(Paths.get(file));
                    addNewScript().loadScript(easyScript);
                    setCurrentTabTitle(easyScript.getSourceScriptFile().getFileName().toString());
                } catch (@Nonnull final IOException e1) {
                    LOGGER.warn("Originally opened file: " + file + " could not be opened.");
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
    public static void crashEditor(@Nullable final String message) {
        if (message != null) {
            JOptionPane.showMessageDialog(getInstance(), message, Lang.getMsg(MainFrame.class, "crashEditor.Title"),
                                          JOptionPane.ERROR_MESSAGE);
            LOGGER.error("Editor crashed! Fatal error: " + message);
        } else {
            LOGGER.error("Editor crashed!");
        }
        System.exit(-1);
    }

    /**
     * Start the GUI of the parser.
     *
     * @param args start arguments
     */
    public static void main(final String[] args) {
        initLogging();
        Config.getInstance().init();

        JFrame.setDefaultLookAndFeelDecorated(Config.getInstance().getUseWindowDecoration());
        JDialog.setDefaultLookAndFeelDecorated(Config.getInstance().getUseWindowDecoration());

        CrashReporter.getInstance().setConfig(Config.getInstance().getInternalCfg());
        CrashReporter.getInstance().setMessageSource(Lang.getInstance());
        AWTCrashHandler.init();

        SwingUtilities.invokeLater(new Runnable() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                try {
                    SubstanceLookAndFeel.setSkin(Config.getInstance().getLookAndFeel());
                } catch (@Nonnull final Exception e) {
                    SubstanceLookAndFeel.setSkin(Config.DEFAULT_LOOK_AND_FEEL);
                }

                instance = new MainFrame();
                getInstance().setVisible(true);

                System.out.println("Startup done.");
            }
        });
    }

    private static void initLogging() {
        System.out.println("Startup done.");
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        final Path userDir = DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.User);
        if (userDir == null) {
            return;
        }
        System.setProperty("log_dir", userDir.toAbsolutePath().toString());

        //Reload:
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ContextInitializer ci = new ContextInitializer(lc);
        lc.reset();
        try {
            ci.autoConfig();
        } catch (JoranException e) {
            e.printStackTrace();
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
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
    @Nonnull
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
    @Nonnull
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
    @Nonnull
    @SuppressWarnings("nls")
    protected Editor addNewScript() {
        final Editor editor = new Editor();
        editor.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, Boolean.TRUE);
        tabbedEditorArea
                .insertTab(Lang.getMsg(getClass(), "newScriptTab"), null, editor, null, tabbedEditorArea.getTabCount());
        tabbedEditorArea.setSelectedIndex(tabbedEditorArea.getTabCount() - 1);
        return editor;
    }

    /**
     * Check if a script file is already opened in a editor and return the index
     * of the editor in case it is.
     *
     * @param file the file that is to be opened
     * @return the index of the editor that opened this file or -1 in case its
     * not opened yet
     */
    protected int alreadyOpen(final Path file) {
        final int count = tabbedEditorArea.getComponentCount();
        Editor currentComp;
        for (int i = 0; i < count; i++) {
            currentComp = (Editor) tabbedEditorArea.getComponent(i);
            if ((currentComp.getScriptFile() != null) && currentComp.getScriptFile().equals(file)) {
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

        final java.util.List<Path> fileList = new ArrayList<>();
        for (int i = 0; i < tabCount; i++) {
            final Editor editor = getScriptEditor(i);
            if (editor.changedSinceSave()) {
                final Object[] options = new Object[]{Lang.getMsg(MainFrame.class, "UnsavedChanges.saveButton"),
                                                      Lang.getMsg(MainFrame.class, "UnsavedChanges.discardButton"),
                                                      Lang.getMsg(MainFrame.class, "UnsavedChanges.cancelButton")};
                final int result = JOptionPane.showOptionDialog(this, String.format(
                        Lang.getMsg(MainFrame.class, "UnsavedChanges.message2"), editor.getFileName()),
                                                                Lang.getMsg(MainFrame.class, "UnsavedChanges.title"),
                                                                JOptionPane.YES_NO_CANCEL_OPTION,
                                                                JOptionPane.WARNING_MESSAGE, null, options, options[0]);

                if (result == JOptionPane.YES_OPTION) {
                    Utils.saveEasyNPC(editor);
                    fileList.add(editor.getScriptFile());
                }
            } else {
                if (editor.getScriptFile() != null) {
                    fileList.add(editor.getScriptFile());
                }
            }
        }

        if (fileList.size() > 0) {
            Config.getInstance().setOldFiles(fileList);
        }
        Config.getInstance().setLastWindowValues(this);
        Config.getInstance()
                .setSplitPaneState((double) mainPanel.getDividerLocation() / (double) mainPanel.getHeight());

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
    @Nonnull
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

        UndoMonitor.getInstance().updateUndoRedo(getScriptEditor(index));
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
