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
package org.illarion.nifty.controls.dialog.merchant.builder;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyIdCreator;
import de.lessvoid.nifty.controls.dynamic.attributes.ControlAttributes;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.loaderv2.types.ControlType;
import de.lessvoid.nifty.loaderv2.types.ElementType;
import de.lessvoid.nifty.screen.Screen;
import org.illarion.nifty.controls.DialogMerchant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The control creator for the merchant dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CreateDialogMerchantControl extends ControlAttributes {
    /**
     * The identifier string of the input dialog control.
     */
    static final String NAME = "dialog-merchant";

    /**
     * Create a new input dialog with a automatically generated ID.
     */
    public CreateDialogMerchantControl() {
        setAutoId(NiftyIdCreator.generate());
        setName(NAME);
    }

    /**
     * Create a new input dialog with a user defined ID.
     *
     * @param id the ID of the new control
     */
    public CreateDialogMerchantControl(@Nonnull final String id) {
        setId(id);
        setName(NAME);
    }

    /**
     * Create the input dialog.
     *
     * @param nifty the instance of the Nifty-GUI that will display the dialog
     * @param screen the screen this dialog will be a part of
     * @param parent the parent element of this dialog
     * @return the newly created input dialog
     */
    @Nullable
    public DialogMerchant create(
            @Nonnull final Nifty nifty,
            @Nonnull final Screen screen,
            @Nonnull final Element parent) {
        return nifty.addControl(screen, parent, getStandardControl()).getNiftyControl(DialogMerchant.class);
    }

    /**
     * Create the element type of this dialog.
     *
     * @return the element type of the dialog
     */
    @Nonnull
    @Override
    public ElementType createType() {
        return new ControlType(getAttributes());
    }
}