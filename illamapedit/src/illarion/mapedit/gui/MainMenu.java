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
import illarion.mapedit.events.menu.MapLoadErrorEvent;
import illarion.mapedit.events.menu.MapNewEvent;
import illarion.mapedit.events.menu.MapSaveEvent;
import illarion.mapedit.events.menu.SetFolderEvent;
import illarion.mapedit.resource.loaders.ImageLoader;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryFooter;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * This class represents the main menu.
 *
 * @author Tim
 */
public class MainMenu extends RibbonApplicationMenu {

    private static final Logger LOGGER = Logger.getLogger(MainMenu.class);


    public MainMenu() {
        final RibbonApplicationMenuEntryPrimary menuOpenMap =
                new RibbonApplicationMenuEntryPrimary(
                        ImageLoader.getResizableIcon("fileopen"),
                        Lang.getMsg("gui.mainmenu.Open"),
                        new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                File file = null;
                                try {
                                    file = MapDialogs.showSetFolderDialog();
                                } catch (IOException ex) {
                                    LOGGER.warn("Can't set folder", ex);
                                    EventBus.publish(new MapLoadErrorEvent(Lang.getMsg("gui.error.LoadMap")));
                                }
                                if (file != null) {
                                    EventBus.publish(new SetFolderEvent(file));
                                }
                            }
                        }, JCommandButton.CommandButtonKind.ACTION_ONLY);
        final RibbonApplicationMenuEntryPrimary menuNewMap =
                new RibbonApplicationMenuEntryPrimary(
                        ImageLoader.getResizableIcon("filenew"),
                        Lang.getMsg("gui.mainmenu.New"),
                        new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                EventBus.publish(new MapNewEvent());
                            }
                        }, JCommandButton.CommandButtonKind.ACTION_ONLY
                );
        final RibbonApplicationMenuEntryPrimary menuSave =
                new RibbonApplicationMenuEntryPrimary(
                        ImageLoader.getResizableIcon("filesave"),
                        Lang.getMsg("gui.mainmenu.Save"),
                        new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                EventBus.publish(new MapSaveEvent());
                            }
                        }, JCommandButton.CommandButtonKind.ACTION_ONLY
                );

        final RibbonApplicationMenuEntryFooter settings =
                new RibbonApplicationMenuEntryFooter(
                        ImageLoader.getResizableIcon("configure"),
                        Lang.getMsg("gui.mainmenu.MapEditorConfig"),
                        new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                MapEditorConfig.getInstance().createDialog().show();
                            }
                        });
        addFooterEntry(settings);

        addMenuEntry(menuOpenMap);
        addMenuEntry(menuNewMap);
        addMenuEntry(menuSave);
        addMenuSeparator();
    }
}
