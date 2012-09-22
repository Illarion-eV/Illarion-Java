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
import illarion.mapedit.Utils;
import illarion.mapedit.crash.exceptions.FormatCorruptedException;
import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapIO;
import org.apache.log4j.Logger;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * This class represents the main menu.
 *
 * @author Tim
 */
public class MainMenu extends RibbonApplicationMenu {

    private static final Logger LOGGER = Logger.getLogger(MainMenu.class);

    private final JRibbonFrame parent;

    public MainMenu(final JRibbonFrame parent) {
        this.parent = parent;

        final RibbonApplicationMenuEntryPrimary menuOpenMap =
                new RibbonApplicationMenuEntryPrimary(
                        Utils.getResizableIconFromResource("fileopen.png"),
                        Lang.getMsg("gui.mainmenu.Open"),
                        new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {

                                try {
                                    MapPanel.getInstance().setMap(MapDialogs.showOpenMapDialog(parent));
                                } catch (FormatCorruptedException ex) {
                                    LOGGER.warn("Format wrong.", ex);
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                            ex.getMessage(),
                                            Lang.getMsg("gui.error"),
                                            JOptionPane.ERROR_MESSAGE,
                                            Utils.getIconFromResource("messagebox_critical.png"));
                                } catch (IOException e1) {
                                    LOGGER.warn("Can't load map", e1);
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                            Lang.getMsg("gui.error.LoadMap"),
                                            Lang.getMsg("gui.error"),
                                            JOptionPane.ERROR_MESSAGE,
                                            Utils.getIconFromResource("messagebox_critical.png"));
                                }

                            }
                        }, JCommandButton.CommandButtonKind.ACTION_ONLY);
        final RibbonApplicationMenuEntryPrimary menuNewMap =
                new RibbonApplicationMenuEntryPrimary(
                        Utils.getResizableIconFromResource("filenew.png"),
                        Lang.getMsg("gui.mainmenu.New"),
                        new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                MapPanel.getInstance().setMap(MapDialogs.showNewMapDialog(parent));
                            }
                        }, JCommandButton.CommandButtonKind.ACTION_ONLY
                );
        final RibbonApplicationMenuEntryPrimary menuSave =
                new RibbonApplicationMenuEntryPrimary(
                        Utils.getResizableIconFromResource("filesave.png"),
                        Lang.getMsg("gui.mainmenu.Save"),
                        new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                try {
                                    final Map m = MapPanel.getInstance().getMap();
                                    if (m != null) {
                                        MapIO.saveMap(m);
                                    }

                                } catch (IOException e1) {
                                    LOGGER.warn("Can't save map", e1);
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                            Lang.getMsg("gui.error.SaveMap"),
                                            Lang.getMsg("gui.error"),
                                            JOptionPane.ERROR_MESSAGE,
                                            Utils.getIconFromResource("messagebox_critical.png"));
                                }
                            }
                        }, JCommandButton.CommandButtonKind.ACTION_ONLY
                );

        addMenuEntry(menuOpenMap);
        addMenuEntry(menuNewMap);
        addMenuEntry(menuSave);
        addMenuSeparator();
    }
}
