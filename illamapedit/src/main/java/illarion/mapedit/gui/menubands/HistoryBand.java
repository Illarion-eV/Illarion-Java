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
import illarion.mapedit.events.HistoryEvent;
import illarion.mapedit.resource.loaders.ImageLoader;
import org.bushe.swing.event.EventBus;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mid2Low;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mirror;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim
 */
public class HistoryBand extends JRibbonBand {

    public HistoryBand() {
        super(Lang.getMsg("gui.history"), ImageLoader.getResizableIcon("reload"));

        JCommandButton undo = new JCommandButton(Lang.getMsg("gui.history.undo"),
                                                       ImageLoader.getResizableIcon("undo"));
        JCommandButton redo = new JCommandButton(Lang.getMsg("gui.history.redo"),
                                                       ImageLoader.getResizableIcon("redo"));

        ActionListener undoListener = e -> EventBus.publish(new HistoryEvent(true));

        ActionListener redoListener = e -> EventBus.publish(new HistoryEvent(false));

        undo.addActionListener(undoListener);
        redo.addActionListener(redoListener);

        addCommandButton(undo, RibbonElementPriority.MEDIUM);
        addCommandButton(redo, RibbonElementPriority.MEDIUM);

        List<RibbonBandResizePolicy> policies = new ArrayList<>();
        policies.add(new Mirror(getControlPanel()));
        policies.add(new Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}
