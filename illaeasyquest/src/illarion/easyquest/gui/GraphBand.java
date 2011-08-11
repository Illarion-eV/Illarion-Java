/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyQuest Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyQuest Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easyquest.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import illarion.easyquest.Lang;

final class GraphBand extends JRibbonBand {
    /**
     * The serialization UID of this ribbon band.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor that prepares the buttons displayed on this band.
     */
    @SuppressWarnings("nls")
    public GraphBand() {
        super(Lang.getMsg(GraphBand.class, "title"), null);

        final JCommandToggleButton nodeButton =
            new JCommandToggleButton(Lang.getMsg(getClass(), "node"),
                Utils.getResizableIconFromResource("filenew.png"));
        final JCommandToggleButton transitionButton =
            new JCommandToggleButton(Lang.getMsg(getClass(), "transition"),
                Utils.getResizableIconFromResource("filenew.png"));

        nodeButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
            getClass(), "nodeTooltipTitle"), Lang.getMsg(
            getClass(), "nodeTooltip")));
        transitionButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
            getClass(), "transitionTooltipTitle"), Lang.getMsg(
            getClass(), "transitionTooltip")));

        final ActionListener nodeAction = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {

            }
        };

        final ActionListener transitionAction = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {

            }
        };
        
        nodeButton.addActionListener(nodeAction);
        transitionButton.addActionListener(transitionAction);

        addCommandButton(nodeButton, RibbonElementPriority.TOP);
        addCommandButton(transitionButton, RibbonElementPriority.TOP);

        final List<RibbonBandResizePolicy> policies =
            new ArrayList<RibbonBandResizePolicy>();
        policies.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        policies.add(new CoreRibbonResizePolicies.Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}
