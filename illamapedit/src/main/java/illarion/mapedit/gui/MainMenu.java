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

import illarion.common.config.ConfigDialog;
import illarion.common.config.gui.ConfigDialogSwing;
import illarion.mapedit.Lang;
import illarion.mapedit.events.menu.MapLoadErrorEvent;
import illarion.mapedit.events.menu.MapNewEvent;
import illarion.mapedit.events.menu.MapSaveEvent;
import illarion.mapedit.events.menu.SetFolderEvent;
import illarion.mapedit.resource.loaders.ImageLoader;
import org.bushe.swing.event.EventBus;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryFooter;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;

/**
 * This class represents the main menu.
 *
 * @author Tim
 */
public class MainMenu extends RibbonApplicationMenu {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainMenu.class);

    public MainMenu() {
        RibbonApplicationMenuEntryPrimary menuOpenMap = new RibbonApplicationMenuEntryPrimary(
                ImageLoader.getResizableIcon("fileopen"), Lang.getMsg("gui.mainmenu.Open"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Path file = null;
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
        }, CommandButtonKind.ACTION_ONLY
        );
        RibbonApplicationMenuEntryPrimary menuNewMap = new RibbonApplicationMenuEntryPrimary(
                ImageLoader.getResizableIcon("filenew"), Lang.getMsg("gui.mainmenu.New"), e -> EventBus.publish(new MapNewEvent()), CommandButtonKind.ACTION_ONLY
        );
        RibbonApplicationMenuEntryPrimary menuSave = new RibbonApplicationMenuEntryPrimary(
                ImageLoader.getResizableIcon("filesave"), Lang.getMsg("gui.mainmenu.Save"), e -> EventBus.publish(new MapSaveEvent()), CommandButtonKind.ACTION_ONLY
        );

        RibbonApplicationMenuEntryFooter settings = new RibbonApplicationMenuEntryFooter(
                ImageLoader.getResizableIcon("configure"), Lang.getMsg("gui.mainmenu.MapEditorConfig"),
                e -> {
                    ConfigDialog dialog = MapEditorConfig.getInstance().createDialog();
                    new ConfigDialogSwing(dialog);
                }
        );
        addFooterEntry(settings);

        addMenuEntry(menuOpenMap);
        addMenuEntry(menuNewMap);
        addMenuEntry(menuSave);
        addMenuSeparator();
    }
}
