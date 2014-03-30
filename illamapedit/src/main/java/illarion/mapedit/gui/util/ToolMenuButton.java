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
package illarion.mapedit.gui.util;

import illarion.mapedit.events.ToolSelectedEvent;
import illarion.mapedit.tools.AbstractTool;
import org.bushe.swing.event.EventBus;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;

import javax.annotation.Nonnull;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Tim
 */
public class ToolMenuButton extends JCommandToggleButton {
    /**
     * Creates a new command menu button.
     *
     * @param tool Command menu button for tool.
     */
    public ToolMenuButton(@Nonnull final AbstractTool tool) {
        super(tool.getLocalizedName(), tool.getToolIcon());

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new ToolSelectedEvent(tool));
            }
        });
    }
}
