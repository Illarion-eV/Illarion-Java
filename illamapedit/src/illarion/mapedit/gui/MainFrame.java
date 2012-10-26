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
import illarion.mapedit.render.RendererManager;
import illarion.mapedit.resource.loaders.ImageLoader;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import java.awt.*;

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

    public void initialize() {
        addWindowListener(new WindowEventListener());
        setTitle(Lang.getMsg("application.Name") + MapEditor.getVersion());
        setSize(WINDOW_SIZE);
        getRibbon().setApplicationMenu(new MainMenu());

        add(mapPanel, BorderLayout.CENTER);
        add(settingsPanel, BorderLayout.EAST);
        final RibbonTask task = new RibbonTask(Lang.getMsg("gui.mainframe.ribbon"),
                new ClipboardBand(), new HistoryBand(), new ZoomBand(), new ViewBand(), new MapFileBand(config),
                new ToolBand());


        getRibbon().addTask(task);
        setApplicationIcon(ImageLoader.getResizableIcon("mapedit64"));
        new ToolSettingsPanel();
    }

    public static MainFrame getInstance() {
        return instance;

    }

    public void exit() {
        dispose();
    }

    public RendererManager getRendererManager() {
        return mapPanel.getRenderManager();
    }
}
