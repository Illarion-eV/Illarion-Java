/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui;

import illarion.mapedit.Lang;
import illarion.mapedit.MapEditor;
import illarion.mapedit.events.HistoryEvent;
import illarion.mapedit.events.map.MapPositionEvent;
import illarion.mapedit.events.menu.MapSaveEvent;
import illarion.mapedit.events.menu.ShowHelpDialogEvent;
import illarion.mapedit.events.util.ActionEventPublisher;
import illarion.mapedit.gui.actions.BandClickAction;
import illarion.mapedit.render.RendererManager;
import illarion.mapedit.resource.loaders.ImageLoader;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXStatusBar;
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
    private final HelpDialog helpDialog;
    @Nonnull
    private final MapPanel mapPanel;
    @Nonnull
    private final ToolSettingsPanel settingsPanel;
    private OpenMapPanel filePanel;

    public MainFrame(final GuiController controller) {
        mapPanel = new MapPanel(controller);
        settingsPanel = new ToolSettingsPanel();
        filePanel = new OpenMapPanel();
        instance = this;
        helpDialog = new HelpDialog(this);
    }

    public void initialize(final WindowListener controller) {
        addWindowListener(controller);
        setTitle(MapEditor.APPLICATION.getApplicationIdentifier());
        setSize(getSavedDimension());
        getRibbon().setApplicationMenu(new MainMenu());
        getRibbon().configureHelp(ImageLoader.getResizableIcon("help"), new ActionEventPublisher(new ShowHelpDialogEvent()));

        final JCommandButton saveBtn = getCommandButton("gui.mainmenu.Save","filesave", KeyEvent.VK_S, "Save");
        final JCommandButton undoBtn = getCommandButton("gui.history.undo","undo", KeyEvent.VK_Z, "Undo");
        final JCommandButton redoBtn = getCommandButton("gui.history.redo", "redo", KeyEvent.VK_Z, "Redo", true);

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

        final RibbonTask task = new RibbonTask(Lang.getMsg("gui.mainframe.ribbon"),
                new ClipboardBand(), new ViewBand(getRendererManager()), new ZoomBand(), new ToolBand());

        final JXStatusBar status = new JXStatusBar();
        status.setResizeHandleEnabled(true);
        final JXLabel mapCoordinates = new JXLabel();
        final JXLabel worldCoordinates = new JXLabel();
        EventBus.subscribeStrongly(MapPositionEvent.class, new EventSubscriber<MapPositionEvent>() {
            @Override
            public void onEvent(@Nonnull final MapPositionEvent event) {
                mapCoordinates.setText(Lang.getMsg("gui.mainframe.status.mapCoord") + ": " + event.getMapX() +
                        ',' + event.getMapY());
                worldCoordinates.setText(Lang.getMsg("gui.mainframe.status.worldCoord") + ": " + event.getWorldX() +
                        ',' + event.getWorldY() + ',' + event.getWorldZ());
            }
        });
        status.add(mapCoordinates, new JXStatusBar.Constraint());
        status.add(worldCoordinates, new JXStatusBar.Constraint());
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

    public RendererManager getRendererManager() {
        return mapPanel.getRenderManager();
    }

    @Nonnull
    private static Dimension getSavedDimension() {
        final Dimension windowSize = MapEditorConfig.getInstance().getWindowSize();
        if ((windowSize.getHeight() == 0) || (windowSize.getWidth() == 0)) {
            return WINDOW_SIZE;
        }
        return windowSize;
    }

    public static JCommandToggleButton getToggleButton(final String text, final String icon, final int key,
                                                final String action) {
        return getToggleButton(text, icon, key, action, false);
    }

    public static JCommandToggleButton getToggleButton(final String text, final String icon, final int key,
                                                final String action, final boolean shift) {
        final JCommandToggleButton commandButton = new JCommandToggleButton(Lang.getMsg(text),
                ImageLoader.getResizableIcon(icon));
        setAction(commandButton, key, action, shift);

        return commandButton;
    }

    private static void setAction(final AbstractCommandButton commandButton, final int key,
                                  final String action, final boolean shift) {
        final InputMap input = commandButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        if (shift) {
            mask |= InputEvent.SHIFT_DOWN_MASK;
        }

        final KeyStroke enter = KeyStroke.getKeyStroke(key, mask);
        input.put(enter, action);
        commandButton.getActionMap().put(action, new BandClickAction(commandButton));
    }

    public static JCommandButton getCommandButton(final String text, final String icon, final int key,
                                                final String action) {
        return getCommandButton(text, icon, key, action, false);
    }

    public static JCommandButton getCommandButton(final String text, final String icon, final int key,
                                                  final String action, final boolean shift) {
        final JCommandButton commandButton = new JCommandButton(Lang.getMsg(text),
                ImageLoader.getResizableIcon(icon));

        setAction(commandButton, key, action, shift);
        return commandButton;
    }
}
