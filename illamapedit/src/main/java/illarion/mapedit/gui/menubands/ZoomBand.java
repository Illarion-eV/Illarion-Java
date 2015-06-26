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
package illarion.mapedit.gui.menubands;

import illarion.mapedit.Lang;
import illarion.mapedit.events.map.ZoomEvent;
import illarion.mapedit.resource.loaders.ImageLoader;
import org.bushe.swing.event.EventBus;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.High2Low;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mirror;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim
 */
public class ZoomBand extends JRibbonBand {

    public static final float ZOOM_STEP = .1f;

    public ZoomBand() {
        super(Lang.getMsg("gui.zoomband.Name"), ImageLoader.getResizableIcon("viewmag"));

        JCommandButton zoomOriginal = new JCommandButton(Lang.getMsg("gui.zoomband.Original"),
                                                               ImageLoader.getResizableIcon("viewmag1"));
        JCommandButton zoomOut = new JCommandButton(Lang.getMsg("gui.zoomband.Out"),
                                                          ImageLoader.getResizableIcon("viewmag-"));
        JCommandButton zoomIn = new JCommandButton(Lang.getMsg("gui.zoomband.In"),
                                                         ImageLoader.getResizableIcon("viewmag+"));

        ActionListener zoomOutListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventBus.publish(new ZoomEvent(-ZOOM_STEP, null));
            }
        };

        ActionListener zoomInListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventBus.publish(new ZoomEvent(ZOOM_STEP, null));
            }
        };

        ActionListener zoomOriginalListener = e -> EventBus.publish(new ZoomEvent());

        zoomIn.addActionListener(zoomInListener);
        zoomOut.addActionListener(zoomOutListener);
        zoomOriginal.addActionListener(zoomOriginalListener);

        addCommandButton(zoomOriginal, RibbonElementPriority.TOP);
        addCommandButton(zoomOut, RibbonElementPriority.TOP);
        addCommandButton(zoomIn, RibbonElementPriority.TOP);

        List<RibbonBandResizePolicy> policies = new ArrayList<>();
        policies.add(new Mirror(getControlPanel()));
        policies.add(new High2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}
