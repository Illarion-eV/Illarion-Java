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
    private final SettingsChangedListener listener;


    public TileBrushPanel(final SettingsChangedListener listener) {
        setLayout(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();

        this.listener = listener;

        final JLabel radiusLabel = new JLabel(Lang.getMsg("tools.TileBrushTool.Radius"));
        radiusSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));

        radiusSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                listener.settingsChanged();
            }
        });
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(new TileList());
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        add(radiusLabel, gbc);
        gbc.gridx = 1;
        add(radiusSpinner, gbc);
    }

    public int getRadius() {
        return (Integer) radiusSpinner.getValue();
    }

}
