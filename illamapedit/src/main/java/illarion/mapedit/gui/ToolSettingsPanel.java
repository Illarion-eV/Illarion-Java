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
package illarion.mapedit.gui;

import illarion.mapedit.events.ToolSelectedEvent;
import org.apache.log4j.Logger;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

/**
 * @author Tim
 */
public class ToolSettingsPanel extends JPanel {
    public static final Logger LOGGER = Logger.getLogger(ToolSettingsPanel.class);
    private static final int WIDTH = 200;
    @Nullable
    private JComponent lastChild;

    public ToolSettingsPanel() {
        super(new BorderLayout());
        AnnotationProcessor.process(this);
        setPreferredSize(new Dimension(WIDTH, 0));
    }

    @SuppressWarnings("unused")
    @EventSubscriber(eventClass = ToolSelectedEvent.class)
    public void onToolSelected(@Nonnull final ToolSelectedEvent e) {
        LOGGER.debug("Tool Selected " + e.getTool());
        removeAll();
        lastChild = e.getTool().getSettingsPanel();
        if (lastChild != null) {
            lastChild.setVisible(true);
            add(lastChild, BorderLayout.CENTER);
        }
        validate();
        repaint();
    }
}
