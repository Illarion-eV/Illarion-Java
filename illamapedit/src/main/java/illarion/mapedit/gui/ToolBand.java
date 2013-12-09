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
import illarion.mapedit.gui.util.ToolMenuButton;
import illarion.mapedit.tools.*;
import javolution.util.FastTable;
import org.pushingpixels.flamingo.api.common.CommandToggleButtonGroup;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Tim
 * @author Fredrik K
 */
public class ToolBand extends JRibbonBand {

    /**
     * Default constructor for ToolBand
     */
    public ToolBand() {
        super(Lang.getMsg("gui.toolband.Name"), null);

        final Collection<AbstractTool> tools = new FastTable<AbstractTool>();
        final CommandToggleButtonGroup group = new CommandToggleButtonGroup();

        tools.add(new TileBrushTool());
        tools.add(new ItemBrushTool());
        tools.add(new MusicTool());
        tools.add(new TileEraserTool());
        tools.add(new ItemEraserTool());
        tools.add(new WarpTool());

        for (final AbstractTool tool : tools) {
            final JCommandToggleButton button =new ToolMenuButton(tool);
            addCommandButton(button, RibbonElementPriority.MEDIUM);
            group.add(button);
        }

        setResizePolicies(Arrays.<RibbonBandResizePolicy>asList(
                new CoreRibbonResizePolicies.Mirror(getControlPanel())));
    }
}
