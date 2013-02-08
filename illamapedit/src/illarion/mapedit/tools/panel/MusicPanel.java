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
import illarion.mapedit.tools.ToolManager;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Tim
 */
public class MusicPanel extends JPanel {

    @Nonnull
    private final JSpinner spinner;
    @Nonnull
    private final JCheckBox delCheckBox;
    @Nonnull
    private final JSpinner radiusSpinner;

    public MusicPanel() {
        setLayout(new BorderLayout());

        final JPanel northPanel = new JPanel(new GridLayout(0, 2));

        spinner = new JSpinner(new SpinnerNumberModel(0, 0, 9000, 1));
        radiusSpinner = new JSpinner(new SpinnerNumberModel(1, 1, ToolManager.TOOL_RADIUS, 1));
        delCheckBox = new JCheckBox();

        delCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                spinner.setEnabled(!delCheckBox.isSelected());
            }
        });

        northPanel.add(new JLabel(Lang.getMsg("tools.MusicTool.MusicID")));
        northPanel.add(spinner);
        northPanel.add(new JLabel(Lang.getMsg("tools.MusicTool.Radius")));
        northPanel.add(radiusSpinner);
        northPanel.add(new JLabel(Lang.getMsg("tools.MusicTool.Delete")));
        northPanel.add(delCheckBox);
        add(northPanel, BorderLayout.NORTH);
    }

    /**
     * Get selected musicID
     *
     * @return musicID or 0 if eraser is selected
     */
    public int getMusicID() {
        Integer musicID = 0;

        if (!delCheckBox.isSelected()) {
            musicID = (Integer) spinner.getValue();
        }
        return musicID;
    }

    public int getRadius() {
        return (Integer) radiusSpinner.getValue();
    }
}
