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

import java.awt.Adjustable;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javolution.text.TextBuilder;

import org.pushingpixels.flamingo.api.common.CommandToggleButtonGroup;
import org.pushingpixels.flamingo.api.common.HorizontalAlignment;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import illarion.mapedit.Lang;
import illarion.mapedit.tools.AbstractTool;
import illarion.mapedit.tools.ToolArea;
import illarion.mapedit.tools.ToolRandomArea;
import illarion.mapedit.tools.ToolSingle;

/**
 * This ribbon band allows to configure the way how now objects are placed on
 * the map.
 * 
 * @author Martin Karing
 * @since 1.01
 * @version 1.01
 */
public final class PlacementBand extends JRibbonBand {
    /**
     * This button listener is used for the three placement buttons and
     * activates the assigned tool upon a click.
     * 
     * @author Martin Karing
     * @since 1.01
     * @version 1.01
     */
    private static final class ButtonPlacementListener implements
        ActionListener {
        /**
         * The tool that is activated in case this button is pressed.
         */
        private final AbstractTool tool;

        /**
         * Public constructor to create a instance of this class properly. Also
         * this constructor takes the tool that is activated by this button.
         * 
         * @param usedTool the tool that is activated by this button
         */
        public ButtonPlacementListener(final AbstractTool usedTool) {
            tool = usedTool;
        }

        /**
         * Called when the button is pressed. This causes that the tool is
         * activated.
         */
        @Override
        public void actionPerformed(final ActionEvent e) {
            tool.activateTool();
        }
    }

    /**
     * This scroll listener is used to update the display and the tool that
     * handles random area placement. This is used to setup the probability that
     * a item is placed.
     * 
     * @author Martin Karing
     * @since 1.01
     * @version 1.01
     */
    private static final class RandomChanceScrollListener implements
        ChangeListener {
        /**
         * The label that is updated when the scroll bar changes its value.
         */
        private final JLabel textDisplay;

        /**
         * The tool that is updated when the scroll bar changes its value.
         */
        private final ToolRandomArea tool;

        /**
         * Create a new instance of the scroll bar listener.
         * 
         * @param display the display that is updated when the scroll bar
         *            changes
         * @param updatedTool the tool that is updated when the scroll bar
         *            changes
         */
        public RandomChanceScrollListener(final JLabel display,
            final ToolRandomArea updatedTool) {
            textDisplay = display;
            tool = updatedTool;
        }

        /**
         * Called when the scroll bar changes. This causes the text display and
         * the tool to be updated.
         */
        @Override
        public void stateChanged(final ChangeEvent e) {
            if (e.getSource() instanceof JSlider) {
                final int value = ((JSlider) e.getSource()).getValue();
                final TextBuilder builder = TextBuilder.newInstance();
                builder.append(value);
                builder.append('%');
                textDisplay.setText(builder.toString());
                TextBuilder.recycle(builder);
                tool.setProbability(value / 100.f);
            }
        }

    }

    /**
     * The serialization UID of this ribbon band.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The default constructor for the placement band that prepares this
     * component completely.
     */
    @SuppressWarnings("nls")
    public PlacementBand() {
        super(Lang.getMsg(PlacementBand.class, "Title"), null, null);

        final JCommandToggleButton singlePlacement =
            new JCommandToggleButton(
                Lang.getMsg(PlacementBand.class, "Single"),
                Utils.getResizableIconFromResource("singleSelect.png"));
        final JCommandToggleButton areaPlacement =
            new JCommandToggleButton(Lang.getMsg(PlacementBand.class, "Area"),
                Utils.getResizableIconFromResource("areaSelect.png"));
        final JCommandToggleButton randomPlacement =
            new JCommandToggleButton(
                Lang.getMsg(PlacementBand.class, "Random"),
                Utils.getResizableIconFromResource("randomSelect.png"));

        singlePlacement.setActionRichTooltip(new RichTooltip("Knopf",
            "Toller Knopf"));

        final JSlider randomProp =
            new JSlider(Adjustable.HORIZONTAL, 0, 100, 33);
        randomProp.setMajorTickSpacing(25);
        randomProp.setMinorTickSpacing(5);
        randomProp.setPaintTicks(true);
        randomProp.setPaintLabels(true);
        randomProp.setPreferredSize(new Dimension(150, Integer.MAX_VALUE));

        final JLabel randomPropLabel = new JLabel();
        randomPropLabel.setText("33%");
        randomPropLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        randomPropLabel.setBorder(BorderFactory
            .createEmptyBorder(0, 10, 2, 10));

        final JRibbonComponent randomPropLabelRibbon =
            new JRibbonComponent(randomPropLabel);
        randomPropLabelRibbon
            .setHorizontalAlignment(HorizontalAlignment.TRAILING);

        final CommandToggleButtonGroup placementGroup =
            new CommandToggleButtonGroup();
        placementGroup.add(singlePlacement);
        placementGroup.add(areaPlacement);
        placementGroup.add(randomPlacement);
        placementGroup.setSelected(singlePlacement, true);
        placementGroup.setAllowsClearingSelection(false);

        addCommandButton(singlePlacement, RibbonElementPriority.TOP);
        addCommandButton(areaPlacement, RibbonElementPriority.TOP);
        addCommandButton(randomPlacement, RibbonElementPriority.TOP);
        startGroup(Lang.getMsg(PlacementBand.class, "RandomProp"));
        addRibbonComponent(new JRibbonComponent(randomProp));
        addRibbonComponent(randomPropLabelRibbon);

        singlePlacement.addActionListener(new ButtonPlacementListener(
            new ToolSingle()));
        areaPlacement.addActionListener(new ButtonPlacementListener(
            new ToolArea()));

        final ToolRandomArea randomTool = new ToolRandomArea();
        randomPlacement.addActionListener(new ButtonPlacementListener(
            randomTool));
        randomProp.addChangeListener(new RandomChanceScrollListener(
            randomPropLabel, randomTool));

        singlePlacement.doActionClick();

        final List<RibbonBandResizePolicy> policies =
            new ArrayList<RibbonBandResizePolicy>();
        policies.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        policies.add(new CoreRibbonResizePolicies.Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}
