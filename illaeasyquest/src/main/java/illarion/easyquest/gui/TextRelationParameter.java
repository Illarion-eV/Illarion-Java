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

import javax.annotation.Nullable;
import javax.swing.*;

@SuppressWarnings("serial")
public class TextRelationParameter extends JTextField implements Parameter {
    public TextRelationParameter() {
    }

    public TextRelationParameter(int width) {
        super(width);
    }

    @Override
    public void setParameter(@Nullable Object parameter) {
        if (parameter != null) {
            setText((String) parameter);
        } else {
            setText("");
        }
    }

    @Override
    public Object getParameter() {
        return getText();
    }
}