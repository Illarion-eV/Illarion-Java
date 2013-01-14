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
import illarion.mapedit.render.*;
import javolution.util.FastList;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import javax.annotation.Nonnull;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author Tim
 */
public class ViewBand extends JRibbonBand {

    public ViewBand(@Nonnull final RendererManager manager) {
        super(Lang.getMsg("gui.viewband.Name"), null);

        final List<AbstractMapRenderer> r = new FastList<AbstractMapRenderer>();

        r.add(new TileRenderer(manager));
        r.add(new ItemRenderer(manager));
        r.add(new GridRenderer(manager));
        r.add(new MusicRenderer(manager));
        r.add(new WarpRenderer(manager));

        for (final AbstractMapRenderer re : r) {
            final JCommandToggleButton btn = new JCommandToggleButton(
                    re.getLocalizedName(), re.getRendererIcon()
            );
            btn.getActionModel().setSelected(re.isDefaultOn());
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (btn.getActionModel().isSelected()) {
                        manager.addRenderer(re);
                    } else {
                        manager.removeRenderer(re);
                    }
                }
            });
            if (re.isDefaultOn()) {
                manager.addRenderer(re);
            }
            addCommandButton(btn, re.getPriority());
        }

        final List<RibbonBandResizePolicy> resize = new FastList<RibbonBandResizePolicy>();
        resize.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        resize.add(new CoreRibbonResizePolicies.Mid2Low(getControlPanel()));
        resize.add(new CoreRibbonResizePolicies.High2Low(getControlPanel()));

        setResizePolicies(resize);
    }
}
