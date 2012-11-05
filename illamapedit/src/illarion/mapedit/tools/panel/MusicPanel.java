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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * @author Tim
 */
public class MusicPanel extends JPanel {
    private final JSpinner spinner;

    public MusicPanel(final SettingsChangedListener listener) {
        setLayout(new GridBagLayout());
        GridBagConstraints gb = new GridBagConstraints();

        spinner = new JSpinner(new SpinnerNumberModel(0, 0, 9000, 1));
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                listener.settingsChanged();
            }
        });
        gb.gridx = 0;
        gb.gridy = 0;

        add(new JLabel(Lang.getMsg("tools.MusicTool.MusicID")), gb);
        gb.gridx++;
        add(spinner, gb);
    }

    public int getMusicID() {
        return (Integer) spinner.getValue();
    }
}
