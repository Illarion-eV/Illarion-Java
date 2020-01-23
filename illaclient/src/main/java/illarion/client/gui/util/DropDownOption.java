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
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.dropdown.builder.DropDownBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Stefano Bonicatti &lt;smjert@gmail.com&gt;
 */
public class DropDownOption<T> implements Option<T, DropDown<DropDownItem<T>>> {

    @Nullable
    private final DropDown<DropDownItem<T>> control;
    private final String key;

    public DropDownOption(String name, @Nonnull Element parent, @Nonnull Nifty nifty, @Nonnull Screen screen) {
        key = name;

        DropDownBuilder dropDownBuilder = new DropDownBuilder(name + "GUI");
        Element dropDownElement = dropDownBuilder.build(nifty, screen, parent);

        control = dropDownElement.getNiftyControl(DropDown.class);

        control.setViewConverter(new DropDownItemViewConverter<>());
    }

    @Nullable
    @Override
    public T getValue() {
        if (control == null) {
            return null;
        }

        return control.getSelection().getKey();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Nullable
    @Override
    public DropDown<DropDownItem<T>> getControl() {
        return control;
    }
}
