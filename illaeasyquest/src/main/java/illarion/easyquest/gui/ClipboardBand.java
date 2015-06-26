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
package illarion.easyquest.gui;

import illarion.easyquest.Lang;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mid2Low;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mirror;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

final class ClipboardBand extends JRibbonBand {
    /**
     * The serialization UID of this ribbon band.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The constructor for this clipboard band that prepares the buttons and all
     * settings needed for the proper display.
     */
    public ClipboardBand() {
        super(Lang.getMsg(ClipboardBand.class, "title"), null);

        JCommandButton pasteButton = new JCommandButton(Lang.getMsg(getClass(), "pasteButton"),
                                                              Utils.getResizableIconFromResource("editpaste.png"));
        JCommandButton copyButton = new JCommandButton(Lang.getMsg(getClass(), "copyButton"),
                                                             Utils.getResizableIconFromResource("editcopy.png"));
        JCommandButton cutButton = new JCommandButton(Lang.getMsg(getClass(), "cutButton"),
                                                            Utils.getResizableIconFromResource("editcut.png"));

        pasteButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "pasteButtonTooltipTitle"),
                                                         Lang.getMsg(getClass(), "pasteButtonTooltip")));
        copyButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "copyButtonTooltipTitle"),
                                                        Lang.getMsg(getClass(), "copyButtonTooltip")));
        cutButton.setActionRichTooltip(new RichTooltip(Lang.getMsg(getClass(), "cutButtonTooltipTitle"),
                                                       Lang.getMsg(getClass(), "cutButtonTooltip")));

        ActionListener pasteAction = e -> TransferHandler.getPasteAction().actionPerformed(
                new ActionEvent(MainFrame.getInstance().getCurrentQuestEditor(), e.getID(),
                        e.getActionCommand())
        );

        ActionListener copyAction = e -> TransferHandler.getCopyAction().actionPerformed(
                new ActionEvent(MainFrame.getInstance().getCurrentQuestEditor(), e.getID(),
                        e.getActionCommand())
        );

        ActionListener cutAction = e -> TransferHandler.getCutAction().actionPerformed(
                new ActionEvent(MainFrame.getInstance().getCurrentQuestEditor(), e.getID(),
                        e.getActionCommand())
        );

        pasteButton.addActionListener(pasteAction);
        copyButton.addActionListener(copyAction);
        cutButton.addActionListener(cutAction);

        addCommandButton(pasteButton, RibbonElementPriority.TOP);
        addCommandButton(copyButton, RibbonElementPriority.TOP);
        addCommandButton(cutButton, RibbonElementPriority.TOP);

        List<RibbonBandResizePolicy> policies = new ArrayList<>();
        policies.add(new Mirror(getControlPanel()));
        policies.add(new Mid2Low(getControlPanel()));
        setResizePolicies(policies);
    }
}
