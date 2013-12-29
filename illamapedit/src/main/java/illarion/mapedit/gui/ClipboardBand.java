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
import illarion.mapedit.resource.loaders.ImageLoader;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the clipboard section in the ribbon.
 *
 * @author Tim
 */
public class ClipboardBand extends JRibbonBand {
    public ClipboardBand() {
        super(Lang.getMsg("gui.clipboardband.Name"), null);

        final JCommandButton copy = new JCommandButton(Lang.getMsg("gui.clipboardband.Copy"),
                                                       ImageLoader.getResizableIcon("editcopy"));
        final JCommandButton paste = new JCommandButton(Lang.getMsg("gui.clipboardband.Paste"),
                                                        ImageLoader.getResizableIcon("editpaste"));
        final JCommandButton cut = new JCommandButton(Lang.getMsg("gui.clipboardband.Cut"),
                                                      ImageLoader.getResizableIcon("editcut"));

        addCommandButton(paste, RibbonElementPriority.TOP);
        addCommandButton(copy, RibbonElementPriority.MEDIUM);
        addCommandButton(cut, RibbonElementPriority.MEDIUM);

        final List<RibbonBandResizePolicy> policies = new ArrayList<>();
        policies.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        policies.add(new CoreRibbonResizePolicies.High2Mid(getControlPanel()));
        setResizePolicies(policies);
    }
}
