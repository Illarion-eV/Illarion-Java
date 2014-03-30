/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.easynpc.gui;

import illarion.easynpc.Lang;
import org.bushe.swing.event.EventBusAction;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * This band holds all buttons regarding the compiling functions of the editor.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class CompileBand extends JRibbonBand {
    /**
     * The serialization UID of this ribbon band.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor that prepares the buttons displayed on this band.
     */
    @SuppressWarnings("nls")
    CompileBand() {
        super(Lang.getMsg(CompileBand.class, "title"), null);

        final JCommandButton checkScriptButton = new JCommandButton(Lang.getMsg(getClass(), "checkScript"),
                                                                    Utils.getResizableIconFromResource("agt_reload.png")
        );
        final JCommandButton parseScriptButton = new JCommandButton(Lang.getMsg(getClass(), "rebuildScript"),
                                                                    Utils.getResizableIconFromResource("rebuild.png"));
        final JCommandToggleButton autoCheckScriptButton = new JCommandToggleButton(
                Lang.getMsg(getClass(), "autoCheck"), Utils.getResizableIconFromResource("build.png"));

        checkScriptButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "checkScriptButtonTooltipTitle"),
                                                               Lang.getMsg(getClass(), "checkScriptButtonTooltip")));
        parseScriptButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "parseScriptButtonTooltipTitle"),
                                                               Lang.getMsg(getClass(), "parseScriptButtonTooltip")));
        autoCheckScriptButton.setActionRichTooltip(
                new RichTooltip(Lang.getMsg(getClass(), "autoCheckButtonTooltipTitle"),
                                Lang.getMsg(getClass(), "autoCheckButtonTooltip"))
        );
        if (Config.getInstance().getAutoBuild()) {
            autoCheckScriptButton.doActionClick();
        }

        final ActionListener autoCheckScriptAction = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Config.getInstance().setAutoBuild(!Config.getInstance().getAutoBuild());
            }
        };

        checkScriptButton.getActionModel().setActionCommand("checkScript");
        parseScriptButton.getActionModel().setActionCommand("parseScript");
        autoCheckScriptButton.getActionModel().setActionCommand("autoCheckScript");

        checkScriptButton.addActionListener(new EventBusAction());
        parseScriptButton.addActionListener(new EventBusAction());
        autoCheckScriptButton.addActionListener(new EventBusAction());

        autoCheckScriptButton.addActionListener(autoCheckScriptAction);

        addCommandButton(checkScriptButton, RibbonElementPriority.TOP);
        addCommandButton(parseScriptButton, RibbonElementPriority.MEDIUM);
        addCommandButton(autoCheckScriptButton, RibbonElementPriority.MEDIUM);

        final List<RibbonBandResizePolicy> policies = new ArrayList<>();
        policies.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        policies.add(new CoreRibbonResizePolicies.Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}
