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
import illarion.mapedit.events.ZoomEvent;
import org.bushe.swing.event.EventBus;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim
 */
public class ZoomBand extends JRibbonBand {

    public ZoomBand() {
        super(Lang.getMsg("gui.zoomband.Name"), Utils.getResizableIconFromResource("viewmag.png"));

        final JCommandButton zoomOriginal = new JCommandButton(Lang.getMsg("gui.zoomband.Original"),
                Utils.getResizableIconFromResource("viewmag1.png"));
        final JCommandButton zoomOut = new JCommandButton(Lang.getMsg("gui.zoomband.Out"),
                Utils.getResizableIconFromResource("viewmag-.png"));
        final JCommandButton zoomIn = new JCommandButton(Lang.getMsg("gui.zoomband.In"),
                Utils.getResizableIconFromResource("viewmag+.png"));

        final ActionListener zoomOutListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new ZoomEvent(.1f));
            }
        };

        final ActionListener zoomInListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new ZoomEvent(-.1f));
            }
        };

        final ActionListener zoomOriginalListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new ZoomEvent());
            }
        };

        zoomIn.addActionListener(zoomInListener);
        zoomOut.addActionListener(zoomOutListener);
        zoomOriginal.addActionListener(zoomOriginalListener);

        addCommandButton(zoomOriginal, RibbonElementPriority.TOP);
        addCommandButton(zoomOut, RibbonElementPriority.TOP);
        addCommandButton(zoomIn, RibbonElementPriority.TOP);

        final List<RibbonBandResizePolicy> policies = new ArrayList<RibbonBandResizePolicy>();
        policies.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        policies.add(new CoreRibbonResizePolicies.High2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}
