/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyQuest Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyQuest Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easyquest.gui;

import javax.annotation.Nullable;
import javax.swing.*;

@SuppressWarnings("serial")
public class TextRelationParameter extends JTextField implements Parameter {
    public TextRelationParameter() {
        super();
    }

    public TextRelationParameter(int width) {
        super(width);
    }

    public void setParameter(@Nullable Object parameter) {
        if (parameter != null) {
            setText((String) parameter);
        } else {
            setText("");
        }
    }

    public Object getParameter() {
        return getText();
    }
}