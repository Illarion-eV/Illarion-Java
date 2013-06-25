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
package illarion.mapedit.gui.util;

import illarion.mapedit.events.ToolSelectedEvent;
import illarion.mapedit.tools.AbstractTool;
import org.bushe.swing.event.EventBus;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;

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
    public ToolMenuButton(final AbstractTool tool) {
        super(tool.getLocalizedName(), tool.getToolIcon());

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                EventBus.publish(new ToolSelectedEvent(tool));
            }
        });
    }
}
