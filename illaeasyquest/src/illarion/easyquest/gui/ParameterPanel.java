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

import illarion.easyquest.quest.TemplateParameter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class ParameterPanel extends JPanel {
    @Nullable
    private Component comp;

    public ParameterPanel(@Nonnull TemplateParameter parameter) {
        JLabel description = new JLabel(parameter.getDescription() + ":");
        String type = parameter.getType();
        if (type.equals("TEXT")) {
            comp = new TextParameter(17);
        } else if (type.equals("POSITION")) {
            comp = new PositionParameter();
        } else if (type.equals("INTEGER")) {
            comp = new IntegerParameter();
        } else if (type.equals("INTEGERRELATION")) {
            comp = new IntegerRelationParameter();
        } else if (type.equals("TEXTRELATION")) {
            comp = new TextRelationParameter();
        } else {
            comp = null;
        }

        setLayout(new GridLayout(1, 2));

        add(description);
        if (comp != null) {
            add(comp);
        } else {
            add(new JLabel("TYPE \"" + type + "\" NOT IMPLEMENTED"));
        }
    }

    public Object getParameter() {
        if (comp != null) {
            return ((Parameter) comp).getParameter();
        } else {
            return "TYPE NOT IMPLEMENTED";
        }
    }

    public void setParameter(Object parameter) {
        if (comp != null) {
            ((Parameter) comp).setParameter(parameter);
        }
    }
}