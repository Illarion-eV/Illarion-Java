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
import illarion.mapedit.events.ClipboardCopyEvent;
import illarion.mapedit.events.ClipboardCutEvent;
import illarion.mapedit.events.ClipboardPasteEvent;
import illarion.mapedit.events.DidPasteEvent;
import illarion.mapedit.gui.MainFrame;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.High2Mid;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mirror;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import javax.annotation.Nonnull;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the clipboard section in the ribbon.
 *
 * @author Tim
 */
public class ClipboardBand extends JRibbonBand {

    @Nonnull
    final JCommandToggleButton paste;

    public ClipboardBand() {
        super(Lang.getMsg("gui.clipboardband.Name"), null);
        AnnotationProcessor.process(this);
        JCommandButton copy = MainFrame
                .getCommandButton("gui.clipboardband.Copy", "editcopy", KeyEvent.VK_C, "Copy");
        paste = MainFrame.getToggleButton("gui.clipboardband.Paste", "editpaste", KeyEvent.VK_V, "Paste");
        JCommandButton cut = MainFrame.getCommandButton("gui.clipboardband.Cut", "editcut", KeyEvent.VK_X, "Cut");

        ActionListener copyListener = e -> EventBus.publish(new ClipboardCopyEvent());
        copy.addActionListener(copyListener);

        ActionListener pasteListener = e -> EventBus.publish(new ClipboardPasteEvent());
        paste.addActionListener(pasteListener);

        ActionListener cutListener = e -> EventBus.publish(new ClipboardCutEvent());
        cut.addActionListener(cutListener);

        addCommandButton(paste, RibbonElementPriority.MEDIUM);
        addCommandButton(copy, RibbonElementPriority.MEDIUM);
        addCommandButton(cut, RibbonElementPriority.MEDIUM);

        List<RibbonBandResizePolicy> policies = new ArrayList<>();
        policies.add(new Mirror(getControlPanel()));
        policies.add(new High2Mid(getControlPanel()));
        setResizePolicies(policies);
    }

    @EventSubscriber
    public void onDidPaste(@Nonnull DidPasteEvent e) {
        paste.getActionModel().setSelected(false);
    }
}
