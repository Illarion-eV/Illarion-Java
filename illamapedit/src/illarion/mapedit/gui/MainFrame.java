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

import illarion.common.config.Config;
import illarion.mapedit.Lang;
import illarion.mapedit.MapEditor;
import illarion.mapedit.events.HistoryEvent;
import illarion.mapedit.events.menu.MapSaveEvent;
import illarion.mapedit.events.util.ActionEventPublisher;
import illarion.mapedit.render.RendererManager;
import illarion.mapedit.resource.loaders.ImageLoader;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import java.awt.*;
import java.awt.event.WindowListener;

/**
 * This class represents the whole gui.
 *
 * @author Tim
 */
public class MainFrame extends JRibbonFrame {
    private static final Dimension WINDOW_SIZE = new Dimension(900, 700);
    private static MainFrame instance;


    private final MapPanel mapPanel;
    private final ToolSettingsPanel settingsPanel;
    private final Config config;

    public MainFrame(final GuiController controller, final Config config) {
        this.config = config;
        mapPanel = new MapPanel(controller);
        settingsPanel = new ToolSettingsPanel();
        instance = this;
    }

    public void initialize(final WindowListener controller) {
        addWindowListener(controller);
        setTitle(Lang.getMsg("application.Name") + MapEditor.getVersion());
        setSize(getSavedDimension());
        getRibbon().setApplicationMenu(new MainMenu());

        final JCommandButton saveBtn = new JCommandButton(ImageLoader.getResizableIcon("filesave"));
        final JCommandButton undoBtn = new JCommandButton(ImageLoader.getResizableIcon("undo"));
        final JCommandButton redoBtn = new JCommandButton(ImageLoader.getResizableIcon("redo"));

        saveBtn.addActionListener(new ActionEventPublisher(new MapSaveEvent()));
        undoBtn.addActionListener(new ActionEventPublisher(new HistoryEvent(true)));
        redoBtn.addActionListener(new ActionEventPublisher(new HistoryEvent(false)));

        getRibbon().addTaskbarComponent(saveBtn);
        getRibbon().addTaskbarComponent(undoBtn);
        getRibbon().addTaskbarComponent(redoBtn);

        add(mapPanel, BorderLayout.CENTER);
        add(settingsPanel, BorderLayout.EAST);
        final RibbonTask task = new RibbonTask(Lang.getMsg("gui.mainframe.ribbon"),
                /*new ClipboardBand(),*/  new ViewBand(getRendererManager()), new ZoomBand(), new MapFileBand(config),
                new ToolBand());


        getRibbon().addTask(task);
        setApplicationIcon(ImageLoader.getResizableIcon("mapedit64"));
        //new ToolSettingsPanel();
    }

    public static MainFrame getInstance() {
        return instance;

    }

    public void exit() {
        dispose();
        config.set("windowSizeW", getSize().width);
        config.set("windowSizeH", getSize().height);
    }

    public RendererManager getRendererManager() {
        return mapPanel.getRenderManager();
    }

    private Dimension getSavedDimension() {
        final int w = config.getInteger("windowSizeW");
        final int h = config.getInteger("windowSizeH");
        if ((w != 0) && (h != 0)) {
            return new Dimension(w, h);
        }
        return WINDOW_SIZE;
    }
}
