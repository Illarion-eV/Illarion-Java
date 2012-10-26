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
import illarion.mapedit.events.map.RendererToggleEvent;
import illarion.mapedit.render.*;
import illarion.mapedit.resource.loaders.ImageLoader;
import javolution.util.FastList;
import org.bushe.swing.event.EventBus;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author Tim
 */
public class ViewBand extends JRibbonBand {

    public ViewBand() {
        super(Lang.getMsg("gui.viewband.Name"), null);

        final JCommandButton tileButton = new JCommandButton(
                Lang.getMsg("gui.viewband.button.Tile"),
                ImageLoader.getResizableIcon("file_tiles")
        );
        final JCommandButton itemButton = new JCommandButton(
                Lang.getMsg("gui.viewband.button.Item"),
                ImageLoader.getResizableIcon("file_items")
        );
        final JCommandButton infoButton = new JCommandButton(
                Lang.getMsg("gui.viewband.button.Info"),
                ImageLoader.getResizableIcon("messagebox_warning")
        );
        final JCommandButton gridButton = new JCommandButton(
                Lang.getMsg("gui.viewband.button.Grid"),
                ImageLoader.getResizableIcon("viewGrid")
        );
        final JCommandButton musicButton = new JCommandButton(
                Lang.getMsg("gui.viewband.button.Sound"),
                ImageLoader.getResizableIcon("sound")
        );

        final ActionListener tileListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new RendererToggleEvent(TileRenderer.class));
            }
        };

        final ActionListener itemListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new RendererToggleEvent(ItemRenderer.class));
            }
        };

        final ActionListener infoListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new RendererToggleEvent(InfoRenderer.class));
            }
        };

        final ActionListener gridListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new RendererToggleEvent(GridRenderer.class));
            }
        };

        final ActionListener musicListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new RendererToggleEvent(MusicRenderer.class));
            }
        };

        tileButton.addActionListener(tileListener);
        itemButton.addActionListener(itemListener);
        infoButton.addActionListener(infoListener);
        gridButton.addActionListener(gridListener);
        musicButton.addActionListener(musicListener);

        addCommandButton(tileButton, RibbonElementPriority.TOP);
        addCommandButton(itemButton, RibbonElementPriority.TOP);
        addCommandButton(infoButton, RibbonElementPriority.MEDIUM);
        addCommandButton(gridButton, RibbonElementPriority.MEDIUM);
        addCommandButton(musicButton, RibbonElementPriority.MEDIUM);

        final List<RibbonBandResizePolicy> resize = new FastList<RibbonBandResizePolicy>();
        resize.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        resize.add(new CoreRibbonResizePolicies.Mid2Low(getControlPanel()));
        resize.add(new CoreRibbonResizePolicies.High2Low(getControlPanel()));

        setResizePolicies(resize);
    }
}
