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
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import javax.annotation.Nonnull;
import java.awt.event.ActionEvent;
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
        final JCommandButton copy = MainFrame.getCommandButton("gui.clipboardband.Copy", "editcopy", KeyEvent.VK_C, "Copy");
        paste = MainFrame.getToggleButton("gui.clipboardband.Paste","editpaste", KeyEvent.VK_V, "Paste");
        final JCommandButton cut = MainFrame.getCommandButton("gui.clipboardband.Cut","editcut", KeyEvent.VK_X, "Cut");

        final ActionListener copyListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new ClipboardCopyEvent());
            }
        };
        copy.addActionListener(copyListener);

        final ActionListener pasteListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new ClipboardPasteEvent());
            }
        };
        paste.addActionListener(pasteListener);

        final ActionListener cutListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new ClipboardCutEvent());
            }
        };
        cut.addActionListener(cutListener);

        addCommandButton(paste, RibbonElementPriority.MEDIUM);
        addCommandButton(copy, RibbonElementPriority.MEDIUM);
        addCommandButton(cut, RibbonElementPriority.MEDIUM);

        final List<RibbonBandResizePolicy> policies = new ArrayList<RibbonBandResizePolicy>();
        policies.add(new CoreRibbonResizePolicies.Mirror(getControlPanel()));
        policies.add(new CoreRibbonResizePolicies.High2Mid(getControlPanel()));
        setResizePolicies(policies);
    }

    @EventSubscriber
    public void onDidPaste(@Nonnull final DidPasteEvent e) {
        paste.getActionModel().setSelected(false);
    }
}
