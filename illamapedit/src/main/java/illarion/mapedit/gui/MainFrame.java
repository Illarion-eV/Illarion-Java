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
package illarion.mapedit.gui;

import illarion.common.config.ConfigChangedEvent;
import illarion.mapedit.Lang;
import illarion.mapedit.MapEditor;
import illarion.mapedit.events.HistoryEvent;
import illarion.mapedit.events.map.MapPositionEvent;
import illarion.mapedit.events.menu.MapLoadErrorEvent;
import illarion.mapedit.events.menu.MapSaveEvent;
import illarion.mapedit.events.menu.ShowHelpDialogEvent;
import illarion.mapedit.events.util.ActionEventPublisher;
import illarion.mapedit.gui.actions.BandClickAction;
import illarion.mapedit.gui.menubands.ClipboardBand;
import illarion.mapedit.gui.menubands.ToolBand;
import illarion.mapedit.gui.menubands.ViewBand;
import illarion.mapedit.gui.menubands.ZoomBand;
import illarion.mapedit.gui.util.MapKeyEventPostProcessor;
import illarion.mapedit.render.RendererManager;
import illarion.mapedit.resource.loaders.ImageLoader;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventTopicSubscriber;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXStatusBar.Constraint;
import org.pushingpixels.flamingo.api.common.AbstractCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;

/**
 * This class represents the whole gui.
 *
 * @author Tim
 */
public class MainFrame extends JRibbonFrame {
    private static final Dimension WINDOW_SIZE = new Dimension(1000, 700);
    private static MainFrame instance;

    @Nonnull
    private final MapPanel mapPanel;
    @Nonnull
    private final ToolSettingsPanel settingsPanel;
    private final OpenMapPanel filePanel;

    public MainFrame(GuiController controller) {
        AnnotationProcessor.process(this);
        mapPanel = new MapPanel(controller);
        settingsPanel = new ToolSettingsPanel();
        filePanel = new OpenMapPanel();
        instance = this;
    }

    public void initialize(WindowListener controller) {
        addWindowListener(controller);
        setTitle(MapEditor.APPLICATION.getApplicationIdentifier());
        setSize(getSavedDimension());
        getRibbon().setApplicationMenu(new MainMenu());
        getRibbon().configureHelp(ImageLoader.getResizableIcon("help"),
                                  new ActionEventPublisher(new ShowHelpDialogEvent()));

        JCommandButton saveBtn = getCommandButton("gui.mainmenu.Save", "filesave", KeyEvent.VK_S, "Save");
        JCommandButton undoBtn = getCommandButton("gui.history.undo", "undo", KeyEvent.VK_Z, "Undo");
        JCommandButton redoBtn = getCommandButton("gui.history.redo", "redo", KeyEvent.VK_Z, "Redo", true);

        saveBtn.addActionListener(new ActionEventPublisher(new MapSaveEvent()));
        undoBtn.addActionListener(new ActionEventPublisher(new HistoryEvent(true)));
        redoBtn.addActionListener(new ActionEventPublisher(new HistoryEvent(false)));

        getRibbon().addTaskbarComponent(saveBtn);
        getRibbon().addTaskbarComponent(undoBtn);
        getRibbon().addTaskbarComponent(redoBtn);

        filePanel.init();
        add(mapPanel, BorderLayout.CENTER);
        add(settingsPanel, BorderLayout.EAST);
        add(filePanel, BorderLayout.LINE_START);

        RibbonTask task = new RibbonTask(Lang.getMsg("gui.mainframe.ribbon"), new ClipboardBand(),
                                               new ViewBand(getRendererManager()), new ZoomBand(), new ToolBand());

        JXStatusBar status = new JXStatusBar();
        status.setResizeHandleEnabled(true);
        JXLabel mapCoordinates = new JXLabel();
        JXLabel worldCoordinates = new JXLabel();
        EventBus.subscribeStrongly(MapPositionEvent.class, new EventSubscriber<MapPositionEvent>() {
            @Override
            public void onEvent(@Nonnull MapPositionEvent event) {
                mapCoordinates.setText(Lang.getMsg("gui.mainframe.status.mapCoord") + ": " + event.getMapX() +
                                               ',' + event.getMapY());
                worldCoordinates.setText(Lang.getMsg("gui.mainframe.status.worldCoord") + ": " + event.getWorldX() +
                                                 ',' + event.getWorldY() + ',' + event.getWorldZ());
            }
        });
        status.add(mapCoordinates, new Constraint());
        status.add(worldCoordinates, new Constraint());
        add(status, BorderLayout.SOUTH);

        getRibbon().addTask(task);
        setApplicationIcon(ImageLoader.getResizableIcon("mapedit64"));

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(new MapKeyEventPostProcessor());
    }

    public static MainFrame getInstance() {
        return instance;
    }

    public void exit() {
        dispose();
        MapEditorConfig.getInstance().setWindowSize(getSize());
    }

    @Nonnull
    public RendererManager getRendererManager() {
        return mapPanel.getRenderManager();
    }

    @Nonnull
    private static Dimension getSavedDimension() {
        Dimension windowSize = MapEditorConfig.getInstance().getWindowSize();
        if ((windowSize.getHeight() == 0) || (windowSize.getWidth() == 0)) {
            return WINDOW_SIZE;
        }
        return windowSize;
    }

    @Nonnull
    public static JCommandToggleButton getToggleButton(
            String text, String icon, int key, String action) {
        return getToggleButton(text, icon, key, action, false);
    }

    @Nonnull
    public static JCommandToggleButton getToggleButton(
            String text, String icon, int key, String action, boolean shift) {
        JCommandToggleButton commandButton = new JCommandToggleButton(Lang.getMsg(text),
                                                                            ImageLoader.getResizableIcon(icon));
        setAction(commandButton, key, action, shift);

        return commandButton;
    }

    private static void setAction(
            @Nonnull AbstractCommandButton commandButton,
            int key,
            String action,
            boolean shift) {
        InputMap input = commandButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        if (shift) {
            mask |= InputEvent.SHIFT_DOWN_MASK;
        }

        KeyStroke enter = KeyStroke.getKeyStroke(key, mask);
        input.put(enter, action);
        commandButton.getActionMap().put(action, new BandClickAction(commandButton));
    }

    @Nonnull
    public static JCommandButton getCommandButton(
            String text, String icon, int key, String action) {
        return getCommandButton(text, icon, key, action, false);
    }

    @Nonnull
    public static JCommandButton getCommandButton(
            String text, String icon, int key, String action, boolean shift) {
        JCommandButton commandButton = new JCommandButton(Lang.getMsg(text), ImageLoader.getResizableIcon(icon));

        setAction(commandButton, key, action, shift);
        return commandButton;
    }

    @org.bushe.swing.event.annotation.EventSubscriber
    public void onMapLoadError(@Nonnull MapLoadErrorEvent e) {
        showMessageDialog(e.getMessage());
    }

    public static void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(getInstance(), message, Lang.getMsg("gui.error"), JOptionPane.ERROR_MESSAGE,
                                      ImageLoader.getImageIcon("messagebox_critical"));
    }

    @EventTopicSubscriber(topic = MapEditorConfig.USED_LANGUAGE)
    public void onConfigLanguageChanged(String topic, @Nonnull ConfigChangedEvent event) {
        JOptionPane optionPane = new JOptionPane(Lang.getMsg("gui.LocaleChanged"),
                                                       JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog(getInstance(), Lang.getMsg("gui.info"));
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    @EventTopicSubscriber(topic = MapEditorConfig.USE_WINDOW_DECO)
    public void onConfigWindowDecoChanged(String topic, @Nonnull ConfigChangedEvent event) {
        JOptionPane optionPane = new JOptionPane(Lang.getMsg("gui.LocaleChanged"),
                                                       JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog(getInstance(), Lang.getMsg("gui.info"));
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }
}
