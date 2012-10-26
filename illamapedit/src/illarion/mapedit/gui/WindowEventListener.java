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

import illarion.mapedit.MapEditor;
import org.apache.log4j.Logger;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Tim
 */
public class WindowEventListener extends WindowAdapter {
    private static final Logger LOGGER = Logger.getLogger(WindowEventListener.class);

    @Override
    public void windowClosing(final WindowEvent e) {
        LOGGER.debug("Closing window.");
        MainFrame.getInstance().dispose();
        MapEditor.exit();

    }


    @Override
    public void windowClosed(final WindowEvent e) {
        LOGGER.debug("Closed window.");
        System.exit(0);
    }

}
