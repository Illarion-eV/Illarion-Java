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
import org.apache.log4j.Logger;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import java.awt.*;

/**
 * This class represents the whole gui.
 *
 * @author Tim
 */
public class MainFrame extends JRibbonFrame {
    private static final MainFrame INSTANCE = new MainFrame();
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class);

    private final MapPanel map;

    private MainFrame() {
        addWindowListener(new WindowEventListener());
        setTitle(Lang.getMsg("application.Name") + MapEditor.getVersion());
        setSize(new Dimension(400, 500));
        getRibbon().setApplicationMenu(new MainMenu());

        System.out.println(getLayout());

        map = MapPanel.getInstance();

        add(map, BorderLayout.CENTER);
        add(new TileSelector(), BorderLayout.EAST);

        RibbonTask task = new RibbonTask(Lang.getMsg("gui.mainframe.ribbon"),
                new ClipboardBand(), new ViewBand());


        getRibbon().addTask(task);
        setApplicationIcon(Utils.getResizableIconFromResource("mapedit64.png"));

    }

    public static MainFrame getInstance() {

        return INSTANCE;

    }

    public MapPanel getMapPanel() {
        return map;
    }

    public void exit() {
        dispose();
    }
}
