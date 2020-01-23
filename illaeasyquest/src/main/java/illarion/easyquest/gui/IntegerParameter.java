/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;

@SuppressWarnings("serial")
public class IntegerParameter extends JFormattedTextField implements Parameter {

    public IntegerParameter() {
        NumberFormat format = NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(format);
        DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
        setFormatterFactory(factory);
        setHorizontalAlignment(JFormattedTextField.RIGHT);
        setParameter(0L);
    }

    @Override
    public void setParameter(@Nullable Object parameter) {
        if (parameter != null) {
            if (parameter instanceof Long) {
                setValue(parameter);
            } else {
                setValue(Long.valueOf((String) parameter));
            }
        } else {
            setValue(0L);
        }
    }

    @Override
    @Nonnull
    public Object getParameter() {
        return getValue();
    }
}
