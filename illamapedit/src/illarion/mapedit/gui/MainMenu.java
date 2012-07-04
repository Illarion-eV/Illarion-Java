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
import org.apache.log4j.Logger;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * @author Tim
 */
public class MainMenu extends RibbonApplicationMenu {

    private static final Logger LOGGER = Logger.getLogger(MainMenu.class);

    public MainMenu() {
        super();


        final RibbonApplicationMenuEntryPrimary newScriptEntry =
                new RibbonApplicationMenuEntryPrimary(
                        Utils.getResizableIconFromResource("fileopen.png"),
                        Lang.getMsg("gui.mainmenu.Open"),
                        new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {

                                try {
                                    MapPanel.getInstance().setMap(MapChooser.getInstance().loadMap());
                                } catch (IOException e1) {
                                    LOGGER.warn("Can't load map", e1);
                                }

                            }
                        }, JCommandButton.CommandButtonKind.ACTION_ONLY);

        addMenuEntry(newScriptEntry);
    }
}
