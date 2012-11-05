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

import javax.swing.*;

/**
 * @author Tim
 */
public class WarpPanel extends JPanel {

    public final JSpinner xSpinner;
    public final JSpinner ySpinner;
    public final JSpinner zSpinner;

    public WarpPanel() {
        xSpinner = new JSpinner(new SpinnerNumberModel(0, -1000000, 1000000, 1));
        ySpinner = new JSpinner(new SpinnerNumberModel(0, -1000000, 1000000, 1));
        zSpinner = new JSpinner(new SpinnerNumberModel(0, -1000000, 1000000, 1));
    }

    public int getX() {
        return (Integer) xSpinner.getValue();
    }

    public int getY() {
        return (Integer) ySpinner.getValue();
    }

    public int getZ() {
        return (Integer) zSpinner.getValue();
    }
}
