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
package illarion.mapedit.tools.panel;

import illarion.mapedit.Lang;
import illarion.mapedit.tools.panel.components.TileList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * @author Tim
 */
public class TileBrushPanel extends JPanel {
    private final JSpinner radiusSpinner;


    public TileBrushPanel(final SettingsChangedListener listener) {
        super(new BorderLayout());

        add(new TileList(), BorderLayout.CENTER);

        final JPanel brushSizePanel = new JPanel(new BorderLayout(5, 0));
        final JLabel radiusLabel = new JLabel(Lang.getMsg("tools.TileBrushTool.Radius"));
        radiusSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        brushSizePanel.add(radiusLabel, BorderLayout.WEST);
        brushSizePanel.add(radiusSpinner, BorderLayout.CENTER);

        add(brushSizePanel, BorderLayout.SOUTH);

        radiusSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                listener.settingsChanged();
            }
        });
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listener.settingsChanged();
            }
        });
    }

    public int getRadius() {
        return (Integer) radiusSpinner.getValue();
    }

}
