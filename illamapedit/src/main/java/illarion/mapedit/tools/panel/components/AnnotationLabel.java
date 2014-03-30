/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.mapedit.tools.panel.components;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author Fredrik K
 */
public class AnnotationLabel extends JLabel {
    public AnnotationLabel() {
        Border paddingBorder = BorderFactory.createEmptyBorder(1, 10, 1, 10);
        Border border = BorderFactory.createLineBorder(Color.RED);
        setBorder(BorderFactory.createCompoundBorder(border, paddingBorder));
        setVisible(false);
    }

    public void setAnnotation(@Nullable final String text) {
        if ((text != null) && !text.isEmpty()) {
            setText(text);
            setVisible(true);
        } else {
            setText("");
            setVisible(false);
        }
    }

    public String getAnnotation() {
        if (getText() == null) {
            return "";
        }
        return getText();
    }
}
