/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import illarion.easynpc.Lang;

/**
 * This band holds all buttons regarding the compiling functions of the editor.
 * 
 * @author Martin Karing
 * @since 1.01
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
    public CompileBand() {
        super(Lang.getMsg(CompileBand.class, "title"), null);

        final JCommandButton checkScriptButton =
            new JCommandButton(Lang.getMsg(getClass(), "checkScript"),
                Utils.getResizableIconFromResource("agt_reload.png"));
        final JCommandButton parseScriptButton =
            new JCommandButton(Lang.getMsg(getClass(), "rebuildScript"),
                Utils.getResizableIconFromResource("rebuild.png"));
        final JCommandToggleButton autoCheckScriptButton =
            new JCommandToggleButton(Lang.getMsg(getClass(), "autoCheck"),
                Utils.getResizableIconFromResource("build.png"));

        checkScriptButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
            getClass(), "checkScriptButtonTooltipTitle"), Lang.getMsg(
            getClass(), "checkScriptButtonTooltip")));
        parseScriptButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(
            getClass(), "parseScriptButtonTooltipTitle"), Lang.getMsg(
            getClass(), "parseScriptButtonTooltip")));
        autoCheckScriptButton.setActionRichTooltip(new RichTooltip(Lang
            .getMsg(getClass(), "autoCheckButtonTooltipTitle"), Lang.getMsg(
            getClass(), "autoCheckButtonTooltip")));
        if (Config.getInstance().getAutoBuild()) {
            autoCheckScriptButton.doActionClick();
        }

        final ActionListener checkScriptAction = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Utils.reparseSilent(MainFrame.getInstance()
                    .getCurrentScriptEditor());
            }
        };

        final ActionListener parseScriptAction = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Utils.reparseScript(MainFrame.getInstance()
                    .getCurrentScriptEditor());
            }
        };

        final ActionListener autoCheckScriptAction = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Config.getInstance().setAutoBuild(
                    !Config.getInstance().getAutoBuild());
            }
        };

        checkScriptButton.addActionListener(checkScriptAction);
        parseScriptButton.addActionListener(parseScriptAction);
        autoCheckScriptButton.addActionListener(autoCheckScriptAction);

        addCommandButton(checkScriptButton, RibbonElementPriority.TOP);
        addCommandButton(parseScriptButton, RibbonElementPriority.MEDIUM);
        addCommandButton(autoCheckScriptButton, RibbonElementPriority.MEDIUM);

        final List<RibbonBandResizePolicy> policies =
            new ArrayList<RibbonBandResizePolicy>();
        policies.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        policies.add(new CoreRibbonResizePolicies.Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}
