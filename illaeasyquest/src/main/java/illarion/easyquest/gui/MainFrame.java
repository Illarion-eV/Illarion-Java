/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyQuest Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyQuest Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easyquest.gui;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import illarion.common.util.DirectoryManager;
import illarion.easyquest.Lang;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.tabbed.VetoableTabCloseListener;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.nio.file.Path;

@SuppressWarnings("serial")
public class MainFrame extends JRibbonFrame {
    public static final int CREATE_NOTHING = 0;
    public static final int CREATE_STATUS = 1;
    public static final int CREATE_TRIGGER = 2;

    private int createType;

    private final VetoableTabCloseListener editorTabListener = new VetoableTabCloseListener() {
        @Override
        public void tabClosed(@Nonnull final JTabbedPane pane, final Component component) {
            //((Editor) component).cleanup();
            if (pane.getTabCount() == 0) {
                addNewQuest();
            }
        }

        @Override
        public void tabClosing(final JTabbedPane pane, final Component component) {
            // nothing
        }

        @SuppressWarnings("nls")
        @Override
        public boolean vetoTabClosing(final JTabbedPane pane, final Component component) {
            final Editor editor = (Editor) component;
            if (!editor.changedSinceSave()) {
                return false;
            }

            final Object[] options = new Object[]{Lang.getMsg(MainFrame.class, "UnsavedChanges.saveButton"),
                                                  Lang.getMsg(MainFrame.class, "UnsavedChanges.discardButton"),
                                                  Lang.getMsg(MainFrame.class, "UnsavedChanges.cancelButton")};
            final int result = JOptionPane.showOptionDialog(MainFrame.getInstance(),
                                                            Lang.getMsg(MainFrame.class, "UnsavedChanges" + ".message"),
                                                            Lang.getMsg(MainFrame.class, "UnsavedChanges.title"),
                                                            JOptionPane.YES_NO_CANCEL_OPTION,
                                                            JOptionPane.WARNING_MESSAGE, null, options, options[0]);

            if (result == JOptionPane.YES_OPTION) {
                Utils.saveEasyQuest(editor);
                return false;
            }
            return (result == JOptionPane.CANCEL_OPTION);
        }
    };

    private static MainFrame instance;

    @Nonnull
    private final JTabbedPane tabbedEditorArea;

    public MainFrame() {
        super("easyQuest Editor");

        createType = CREATE_NOTHING;

        setApplicationIcon(Utils.getResizableIconFromResource("easyquest.png"));

        final RibbonTask graphTask = new RibbonTask(Lang.getMsg(getClass(), "ribbonTaskQuest"), new ClipboardBand(),
                                                    new GraphBand(), new ServerBand());
        getRibbon().addTask(graphTask);

        getRibbon().setApplicationMenu(new MainMenu());

        final JCommandButton saveButton = new JCommandButton(Utils.getResizableIconFromResource("filesave.png"));
        saveButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "saveButtonTooltipTitle"),
                                                        Lang.getMsg(getClass(), "saveButtonTooltip")));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Utils.saveEasyQuest(getCurrentQuestEditor());
            }
        });
        getRibbon().addTaskbarComponent(saveButton);

        final JCommandButton undoButton = new JCommandButton(Utils.getResizableIconFromResource("undo.png"));
        undoButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "undoButtonTooltipTitle"),
                                                        Lang.getMsg(getClass(), "undoButtonTooltip")));
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                getCurrentQuestEditor().getUndoManager().undo();
            }
        });
        getRibbon().addTaskbarComponent(undoButton);

        final JCommandButton redoButton = new JCommandButton(Utils.getResizableIconFromResource("redo.png"));
        redoButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "redoButtonTooltipTitle"),
                                                        Lang.getMsg(getClass(), "redoButtonTooltip")));
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
        SubstanceLookAndFeel.registerTabCloseChangeListener(tabbedEditorArea, editorTabListener);

        getContentPane().add(rootPanel);

        pack();

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

        if (getOpenTabs() == 0) {
            addNewQuest();
        }
    }

    public static void main(String[] args) {
        initLogging();
        Config.getInstance().init();

        JRibbonFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        SwingUtilities.invokeLater(new Runnable() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                SubstanceLookAndFeel.setSkin(new org.pushingpixels.substance.api.skin.OfficeSilver2007Skin());

                instance = new MainFrame();
                getInstance().setDefaultCloseOperation(JRibbonFrame.EXIT_ON_CLOSE);
                getInstance().setSize(1204, 768);
                getInstance().setLocationRelativeTo(null);
                getInstance().setVisible(true);

                System.out.println("Startup done.");
            }
        });
    }

    private static void initLogging() {
        System.out.println("Startup done.");
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        Path userDir = DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.User);
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

    public static MainFrame getInstance() {
        return instance;
    }

    @Nonnull
    public Editor getCurrentQuestEditor() {
        return getQuestEditor(tabbedEditorArea.getSelectedIndex());
    }

    @Nonnull
    protected Editor getQuestEditor(final int index) {
        return (Editor) tabbedEditorArea.getComponentAt(index);
    }

    protected void closeWindow() {
        //Config.getInstance().setOldFiles(fileList);

        setVisible(false);
        dispose();

        Config.getInstance().save();

        System.exit(0);
    }

    public int getOpenTabs() {
        return tabbedEditorArea.getTabCount();
    }

    public void setCurrentTabTitle(final String title) {
        setTabTitle(tabbedEditorArea.getSelectedIndex(), title);
    }

    @Nonnull
    protected Editor addNewQuest(@Nullable Path quest) {
        final Editor editor = Editor.loadQuest(quest);
        editor.putClientProperty(SubstanceLookAndFeel.TABBED_PANE_CLOSE_BUTTONS_PROPERTY, Boolean.TRUE);
        tabbedEditorArea
                .insertTab(Lang.getMsg(getClass(), "newQuestTab"), null, editor, null, tabbedEditorArea.getTabCount());
        tabbedEditorArea.setSelectedIndex(tabbedEditorArea.getTabCount() - 1);
        return editor;
    }

    @Nonnull
    public Editor addNewQuest() {
        return addNewQuest(null);
    }

    protected int alreadyOpen(@Nonnull final Path file) {
        final int count = tabbedEditorArea.getComponentCount();
        Editor currentComp;
        for (int i = 0; i < count; i++) {
            currentComp = (Editor) tabbedEditorArea.getComponent(i);
            if ((currentComp.getQuestFile() != null) && currentComp.getQuestFile().equals(file)) {
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

    public void setCreateType(int createType) {
        this.createType = createType;
    }

    public int getCreateType() {
        return createType;
    }
}
