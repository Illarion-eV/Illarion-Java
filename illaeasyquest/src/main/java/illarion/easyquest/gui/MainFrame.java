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
package illarion.easyquest.gui;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import illarion.common.util.AppIdent;
import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import illarion.easyquest.Lang;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.OfficeSilver2007Skin;
import org.pushingpixels.substance.api.tabbed.VetoableTabCloseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("serial")
public class MainFrame extends JRibbonFrame {
    /**
     * The identification of this application.
     */
    @Nonnull
    public static final AppIdent APPLICATION = new AppIdent("Illarion easyQuest Editor"); //$NON-NLS-1$

    public static final int CREATE_NOTHING = 0;
    public static final int CREATE_STATUS = 1;
    public static final int CREATE_TRIGGER = 2;

    /**
     * The error and debug logger of the client.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(MainFrame.class);

    private int createType;

    private final VetoableTabCloseListener editorTabListener = new VetoableTabCloseListener() {
        @Override
        public void tabClosed(@Nonnull JTabbedPane pane, Component component) {
            //((Editor) component).cleanup();
            if (pane.getTabCount() == 0) {
                addNewQuest();
            }
        }

        @Override
        public void tabClosing(JTabbedPane pane, Component component) {
            // nothing
        }

        @Override
        public boolean vetoTabClosing(JTabbedPane pane, Component component) {
            Editor editor = (Editor) component;
            if (!editor.changedSinceSave()) {
                return false;
            }

            Object[] options = {Lang.getMsg(MainFrame.class, "UnsavedChanges.saveButton"),
                                                  Lang.getMsg(MainFrame.class, "UnsavedChanges.discardButton"),
                                                  Lang.getMsg(MainFrame.class, "UnsavedChanges.cancelButton")};
            int result = JOptionPane.showOptionDialog(getInstance(),
                                                            Lang.getMsg(MainFrame.class, "UnsavedChanges" + ".message"),
                                                            Lang.getMsg(MainFrame.class, "UnsavedChanges.title"),
                                                            JOptionPane.YES_NO_CANCEL_OPTION,
                                                            JOptionPane.WARNING_MESSAGE, null, options, options[0]);

            if (result == JOptionPane.YES_OPTION) {
                Utils.saveEasyQuest(editor);
                return false;
            }
            return result == JOptionPane.CANCEL_OPTION;
        }
    };

    private static MainFrame instance;

    @Nonnull
    private final JTabbedPane tabbedEditorArea;

    public MainFrame() {
        super("easyQuest Editor");

        createType = CREATE_NOTHING;

        setApplicationIcon(Utils.getResizableIconFromResource("easyquest.png"));

        RibbonTask graphTask = new RibbonTask(Lang.getMsg(getClass(), "ribbonTaskQuest"), new ClipboardBand(),
                                                    new GraphBand(), new ServerBand());
        getRibbon().addTask(graphTask);

        getRibbon().setApplicationMenu(new MainMenu());

        JCommandButton saveButton = new JCommandButton(Utils.getResizableIconFromResource("filesave.png"));
        saveButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "saveButtonTooltipTitle"),
                                                        Lang.getMsg(getClass(), "saveButtonTooltip")));
        saveButton.addActionListener(e -> Utils.saveEasyQuest(getCurrentQuestEditor()));
        getRibbon().addTaskbarComponent(saveButton);

        JCommandButton undoButton = new JCommandButton(Utils.getResizableIconFromResource("undo.png"));
        undoButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "undoButtonTooltipTitle"),
                                                        Lang.getMsg(getClass(), "undoButtonTooltip")));
        undoButton.addActionListener(e -> getCurrentQuestEditor().getUndoManager().undo());
        getRibbon().addTaskbarComponent(undoButton);

        JCommandButton redoButton = new JCommandButton(Utils.getResizableIconFromResource("redo.png"));
        redoButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "redoButtonTooltipTitle"),
                                                        Lang.getMsg(getClass(), "redoButtonTooltip")));
        redoButton.addActionListener(e -> getCurrentQuestEditor().getUndoManager().redo());
        getRibbon().addTaskbarComponent(redoButton);

        JPanel rootPanel = new JPanel(new BorderLayout());
        tabbedEditorArea = new JTabbedPane(SwingConstants.TOP);
        rootPanel.add(tabbedEditorArea, BorderLayout.CENTER);
        SubstanceLookAndFeel.registerTabCloseChangeListener(tabbedEditorArea, editorTabListener);

        getContentPane().add(rootPanel);

        pack();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowListener() {

            @Override
            public void windowActivated(WindowEvent e) {
                // nothing
            }

            @Override
            public void windowClosed(WindowEvent e) {
                getInstance().dispose();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                getInstance().closeWindow();
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // nothing
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // nothing
            }

            @Override
            public void windowIconified(WindowEvent e) {
                // nothing
            }

            @Override
            public void windowOpened(WindowEvent e) {
                // nothing
            }
        });

        validate();

        if (getOpenTabs() == 0) {
            addNewQuest();
        }
    }

    public static void main(String... args) {
        try {
            initLogging();
        } catch (IOException e) {
            System.err.println("Failed to setup logging system!");
            e.printStackTrace(System.err);
        }
        Config.getInstance().init();

        JRibbonFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        SwingUtilities.invokeLater(() -> {
            SubstanceLookAndFeel.setSkin(new OfficeSilver2007Skin());

            instance = new MainFrame();
            getInstance().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            getInstance().setSize(1204, 768);
            getInstance().setLocationRelativeTo(null);
            getInstance().setVisible(true);

            System.out.println("Startup done.");
        });
    }

    /**
     * Prepare the proper output of the log files.
     */
    @SuppressWarnings("Duplicates")
    private static void initLogging() throws IOException {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        Path userDir = DirectoryManager.getInstance().getDirectory(Directory.User);
        if (!Files.isDirectory(userDir)) {
            if (Files.exists(userDir)) {
                Files.delete(userDir);
            }
            Files.createDirectories(userDir);
        }
        System.setProperty("log_dir", userDir.toAbsolutePath().toString());

        //Reload:
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ContextInitializer ci = new ContextInitializer(lc);
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL resource = cl.getResource("logback-to-file.xml");
            if (resource != null) {
                ci.configureByResource(resource);
            }
        } catch (JoranException ignored) {
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

        //noinspection UseOfSystemOutOrSystemErr
        System.out.println("Startup done.");
        log.info("{} started.", APPLICATION.getApplicationIdentifier());
        log.info("VM: {}", System.getProperty("java.version"));
        log.info("OS: {} {} {}", System.getProperty("os.name"), System.getProperty("os.version"),
                System.getProperty("os.arch"));
    }

    public static MainFrame getInstance() {
        return instance;
    }

    @Nonnull
    public Editor getCurrentQuestEditor() {
        return getQuestEditor(tabbedEditorArea.getSelectedIndex());
    }

    @Nonnull
    protected Editor getQuestEditor(int index) {
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

    public void setCurrentTabTitle(String title) {
        setTabTitle(tabbedEditorArea.getSelectedIndex(), title);
    }

    @Nonnull
    protected Editor addNewQuest(@Nullable Path quest) {
        Editor editor = Editor.loadQuest(quest);
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

    protected int alreadyOpen(@Nonnull Path file) {
        int count = tabbedEditorArea.getComponentCount();
        Editor currentComp;
        for (int i = 0; i < count; i++) {
            currentComp = (Editor) tabbedEditorArea.getComponent(i);
            if ((currentComp.getQuestFile() != null) && currentComp.getQuestFile().equals(file)) {
                return i;
            }
        }
        return -1;
    }

    protected void setCurrentEditorTab(int index) {
        tabbedEditorArea.setSelectedIndex(index);

        //UndoMonitor.getInstance().updateUndoRedo(
        //    getScriptEditor(index).getUndoManager());
    }

    protected void setTabTitle(Editor component, String title) {
        int count = tabbedEditorArea.getComponentCount();
        Editor currentComp;
        for (int i = 0; i < count; i++) {
            currentComp = (Editor) tabbedEditorArea.getComponent(i);
            if (currentComp.equals(component)) {
                setTabTitle(i, title);
            }
        }
    }

    private void setTabTitle(int index, String title) {
        tabbedEditorArea.setTitleAt(index, title);
    }

    public void setCreateType(int createType) {
        this.createType = createType;
    }

    public int getCreateType() {
        return createType;
    }
}
