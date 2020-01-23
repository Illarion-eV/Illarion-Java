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
package illarion.client.gui.util;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.checkbox.builder.CheckboxBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Stefano Bonicatti &lt;smjert@gmail.com&gt;
 */
public class CheckBoxOption implements Option<Boolean, CheckBox> {

    @Nullable
    private final CheckBox control;
    private final String key;

    public CheckBoxOption(String name, @Nonnull Element parent, @Nonnull Nifty nifty, @Nonnull Screen screen) {
        key = name;

        CheckboxBuilder checkBoxBuilder = new CheckboxBuilder(name + "GUI");
        Element checkBoxElement = checkBoxBuilder.build(nifty, screen, parent);
        control = checkBoxElement.getNiftyControl(CheckBox.class);
    }

    @Override
    public Boolean getValue() {
        if (control == null) {
            return false;
        }
        return control.isChecked();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Nullable
    @Override
    public CheckBox getControl() {
        return control;
    }
}
