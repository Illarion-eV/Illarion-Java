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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Tim
 */
public class WarpPanel extends JPanel {

    public final JSpinner xSpinner;
    public final JSpinner ySpinner;
    public final JSpinner zSpinner;
    public final JCheckBox delCheckBox;

    public WarpPanel() {
        super(new BorderLayout());
        final JPanel panel = new JPanel(new GridLayout(0, 2));
        xSpinner = new JSpinner(new SpinnerNumberModel(0, -1000000, 1000000, 1));
        ySpinner = new JSpinner(new SpinnerNumberModel(0, -1000000, 1000000, 1));
        zSpinner = new JSpinner(new SpinnerNumberModel(0, -1000000, 1000000, 1));

        delCheckBox = new JCheckBox();

        delCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                xSpinner.setEnabled(!delCheckBox.isSelected());
                ySpinner.setEnabled(!delCheckBox.isSelected());
                zSpinner.setEnabled(!delCheckBox.isSelected());
            }
        });

        panel.add(new JLabel(Lang.getMsg("tools.WarpTool.TargetX")));
        panel.add(xSpinner);
        panel.add(new JLabel(Lang.getMsg("tools.WarpTool.TargetY")));
        panel.add(ySpinner);
        panel.add(new JLabel(Lang.getMsg("tools.WarpTool.TargetZ")));
        panel.add(zSpinner);
        panel.add(new JLabel(Lang.getMsg("tools.WarpTool.Delete")));
        panel.add(delCheckBox);
        add(panel, BorderLayout.NORTH);
    }

    public int getTargetX() {
        return (Integer) xSpinner.getValue();
    }

    public int getTargetY() {
        return (Integer) ySpinner.getValue();
    }

    public int getTargetZ() {
        return (Integer) zSpinner.getValue();
    }

    public boolean isDelete() {
        return delCheckBox.isSelected();
    }
}
