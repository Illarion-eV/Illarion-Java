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
package illarion.mapedit.gui.menubands;

import illarion.mapedit.Lang;
import illarion.mapedit.events.HistoryEvent;
import illarion.mapedit.resource.loaders.ImageLoader;
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
public class HistoryBand extends JRibbonBand {

    public HistoryBand() {
        super(Lang.getMsg("gui.history"), ImageLoader.getResizableIcon("reload"));

        final JCommandButton undo = new JCommandButton(Lang.getMsg("gui.history.undo"),
                                                       ImageLoader.getResizableIcon("undo"));
        final JCommandButton redo = new JCommandButton(Lang.getMsg("gui.history.redo"),
                                                       ImageLoader.getResizableIcon("redo"));

        final ActionListener undoListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new HistoryEvent(true));
            }
        };

        final ActionListener redoListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new HistoryEvent(false));
            }
        };

        undo.addActionListener(undoListener);
        redo.addActionListener(redoListener);

        addCommandButton(undo, RibbonElementPriority.MEDIUM);
        addCommandButton(redo, RibbonElementPriority.MEDIUM);

        final List<RibbonBandResizePolicy> policies = new ArrayList<>();
        policies.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        policies.add(new CoreRibbonResizePolicies.Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}
