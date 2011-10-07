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

import javax.swing.JOptionPane;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.CommandToggleButtonGroup;
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
    
    private final JCommandButton propertiesButton;
    private final JCommandToggleButton nodeButton;
    private final JCommandToggleButton transitionButton;

    /**
     * Default constructor that prepares the buttons displayed on this band.
     */
    @SuppressWarnings("nls")
    public GraphBand() {
        super(Lang.getMsg(GraphBand.class, "title"), null);

        propertiesButton = 
        	new JCommandButton(Lang.getMsg(getClass(), "properties"),
                    Utils.getResizableIconFromResource("properties.png"));
        nodeButton =
            new JCommandToggleButton(Lang.getMsg(getClass(), "state"),
                Utils.getResizableIconFromResource("state.png"));
        transitionButton =
            new JCommandToggleButton(Lang.getMsg(getClass(), "transition"),
                Utils.getResizableIconFromResource("transition.png"));

        propertiesButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
                getClass(), "propertiesTooltipTitle"), Lang.getMsg(
                getClass(), "propertiesTooltip")));
        nodeButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
            getClass(), "nodeTooltipTitle"), Lang.getMsg(
            getClass(), "nodeTooltip")));
        transitionButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
            getClass(), "transitionTooltipTitle"), Lang.getMsg(
            getClass(), "transitionTooltip")));

        final String idRequestTitle = Lang.getMsg(getClass(), "idRequestTitle");
        final String idRequest = Lang.getMsg(getClass(), "idRequest");
        final ActionListener propertiesAction = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
            	boolean validID = false;
            	int id = MainFrame.getInstance().getCurrentQuestEditor().getQuestID();
            	while (!validID) {
            		validID = true;
	            	String input = 
	                    (String) JOptionPane.showInputDialog(null, idRequest,
						idRequestTitle, JOptionPane.QUESTION_MESSAGE,
						null, null, id);
	            	if (input != null) {
		            	try {
		            		id = Integer.parseInt(input);
		            		MainFrame.getInstance().getCurrentQuestEditor().setQuestID(id);
		            	} catch (NumberFormatException exc) {
		            		validID = false;
		            	}
	            	}
            	}
            }
        };
        
        final ActionListener nodeAction = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (nodeButton.getActionModel().isSelected()) {
                	MainFrame.getInstance().setCreateType(MainFrame.CREATE_STATUS);
                } else {
                	MainFrame.getInstance().setCreateType(MainFrame.CREATE_NOTHING);
                }
            }
        };

        final ActionListener transitionAction = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
            	if (transitionButton.getActionModel().isSelected()) {
                	MainFrame.getInstance().setCreateType(MainFrame.CREATE_TRIGGER);
                } else {
                	MainFrame.getInstance().setCreateType(MainFrame.CREATE_NOTHING);
                }
            }
        };
        
        propertiesButton.addActionListener(propertiesAction);
        nodeButton.addActionListener(nodeAction);
        transitionButton.addActionListener(transitionAction);
        
        CommandToggleButtonGroup graphElements = new CommandToggleButtonGroup();
        graphElements.add(nodeButton);
        graphElements.add(transitionButton);

        addCommandButton(propertiesButton, RibbonElementPriority.TOP);
        addCommandButton(nodeButton, RibbonElementPriority.TOP);
        addCommandButton(transitionButton, RibbonElementPriority.TOP);

        final List<RibbonBandResizePolicy> policies =
            new ArrayList<RibbonBandResizePolicy>();
        policies.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        policies.add(new CoreRibbonResizePolicies.Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}
