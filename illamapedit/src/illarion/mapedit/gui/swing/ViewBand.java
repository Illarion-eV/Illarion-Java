/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.gui.swing;

import java.util.ArrayList;
import java.util.List;

import org.pushingpixels.flamingo.api.common.CommandToggleButtonGroup;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import illarion.mapedit.Lang;

/**
 * This band contains the selection of the current view mode.
 * 
 * @author Martin Karing
 * @since 1.01
 * @version 1.01
 */
public final class ViewBand extends JRibbonBand {
    /**
     * The serialization ID of this ribbon band
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor of this view band.
     */
    @SuppressWarnings("nls")
    public ViewBand() {
        super(Lang.getMsg(ViewBand.class, "Title"), null, null);

        final JCommandToggleButton blockedView =
            new JCommandToggleButton(Lang.getMsg(ViewBand.class, "Blocked"),
                Utils.getResizableIconFromResource("viewBlocked.png"));
        final JCommandToggleButton lightView =
            new JCommandToggleButton(Lang.getMsg(ViewBand.class, "Light"),
                Utils.getResizableIconFromResource("viewLight.png"));
        final JCommandToggleButton musicView =
            new JCommandToggleButton(Lang.getMsg(ViewBand.class, "Music"),
                Utils.getResizableIconFromResource("viewMusic.png"));
        final JCommandToggleButton gridView =
            new JCommandToggleButton(Lang.getMsg(ViewBand.class, "Grid"),
                Utils.getResizableIconFromResource("viewGrid.png"));

        final CommandToggleButtonGroup placementGroup =
            new CommandToggleButtonGroup();
        placementGroup.add(blockedView);
        placementGroup.add(lightView);
        placementGroup.add(musicView);
        placementGroup.setAllowsClearingSelection(true);

        addCommandButton(blockedView, RibbonElementPriority.TOP);
        addCommandButton(lightView, RibbonElementPriority.TOP);
        addCommandButton(musicView, RibbonElementPriority.TOP);
        addCommandButton(gridView, RibbonElementPriority.TOP);

        final List<RibbonBandResizePolicy> policies =
            new ArrayList<RibbonBandResizePolicy>();
        policies.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        policies.add(new CoreRibbonResizePolicies.Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}
