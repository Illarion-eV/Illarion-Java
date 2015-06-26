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
package org.illarion.nifty.controls.dialog.select;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.controls.listbox.ListBoxItemController;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import org.illarion.nifty.controls.SelectListEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This control is used to monitor the different entries of the merchant dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Deprecated
public final class DialogSelectEntryControl extends ListBoxItemController<SelectListEntry> {
    private int index;
    @Nullable
    private DialogSelectControl selectDialogControl;
    private boolean selectable;

    @Override
    public void bind(
            @Nonnull Nifty nifty, @Nonnull Screen screen, @Nonnull Element element, @Nonnull Parameters parameter) {
        super.bind(nifty, screen, element, parameter);
        selectable = Boolean.parseBoolean(parameter.get("selectable"));

        if (selectable) {
            selectDialogControl = getParent(element, 8).getNiftyControl(DialogSelectControl.class);
        }
    }

    private static Element getParent(Element root, int grade) {
        Element result = root;
        for (int i = 0; i < grade; i++) {
            result = result.getParent();
        }
        return result;
    }

    public void setIndex(int value) {
        index = value;
    }

    public void listBoxItemMultiClicked(int x, int y, int count) {
        if (selectable && (count == 2) && (selectDialogControl != null)) {
            selectDialogControl.selectItem(index);
        }
    }
}
