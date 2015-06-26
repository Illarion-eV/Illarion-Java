/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
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
    private final Component comp;

    public ParameterPanel(@Nonnull TemplateParameter parameter) {
        JLabel description = new JLabel(parameter.getDescription() + ':');
        String type = parameter.getType();
        switch (type) {
            case "TEXT":
                comp = new TextParameter(17);
                break;
            case "POSITION":
                comp = new PositionParameter();
                break;
            case "INTEGER":
                comp = new IntegerParameter();
                break;
            case "INTEGERRELATION":
                comp = new IntegerRelationParameter();
                break;
            case "TEXTRELATION":
                comp = new TextRelationParameter();
                break;
            default:
                comp = null;
                break;
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