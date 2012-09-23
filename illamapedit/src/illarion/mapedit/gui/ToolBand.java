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
import illarion.mapedit.events.SelectToolEvent;
import illarion.mapedit.resource.loaders.ImageLoader;
import illarion.mapedit.tools.SingleTileTool;
import illarion.mapedit.util.MouseButton;
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
public class ToolBand extends JRibbonBand {

    public ToolBand() {
        super(Lang.getMsg("gui.toolband.Name"), null);

        final JCommandButton singleTileToolBtn = new JCommandButton(
                Lang.getMsg("gui.toolband.SingleTileTool"),
                ImageLoader.getResizableIcon("singleSelect")
        );

        final ActionListener singleTileToolListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new SelectToolEvent(new SingleTileTool(), MouseButton.LeftButton));
            }
        };

        singleTileToolBtn.addActionListener(singleTileToolListener);

        addCommandButton(singleTileToolBtn, RibbonElementPriority.TOP);

        final List<RibbonBandResizePolicy> resize = new FastList<RibbonBandResizePolicy>();
        resize.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        resize.add(new CoreRibbonResizePolicies.Mid2Low(getControlPanel()));
        resize.add(new CoreRibbonResizePolicies.High2Low(getControlPanel()));

        setResizePolicies(resize);
    }
}
