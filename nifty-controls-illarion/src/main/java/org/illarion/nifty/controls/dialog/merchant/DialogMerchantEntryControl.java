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
package org.illarion.nifty.controls.dialog.merchant;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import org.illarion.nifty.controls.MerchantListEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This control is used to monitor the different entries of the merchant dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Deprecated
public final class DialogMerchantEntryControl extends AbstractController {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(DialogMerchantEntryControl.class);
    @Nullable
    private MerchantListEntry listEntry;
    @Nullable
    private DialogMerchantControl merchantControl;
    private boolean selectable;

    @SuppressWarnings("unchecked")
    @Override
    public void bind(
            @Nonnull Nifty nifty, @Nonnull Screen screen, @Nonnull Element element, @Nonnull Parameters parameter) {
        bind(element);
        selectable = Boolean.parseBoolean(parameter.get("selectable"));
        merchantControl = getParent(element, 12).getNiftyControl(DialogMerchantControl.class);
    }

    private static Element getParent(Element root, int grade) {
        Element result = root;
        for (int i = 0; i < grade; i++) {
            result = result.getParent();
        }
        return result;
    }

    @Override
    public void onStartScreen() {
    }

    @Override
    public boolean inputEvent(@Nonnull NiftyInputEvent inputEvent) {
        return false;
    }

    /**
     * This function is called in case someone clicks on the control entry.
     *
     * @param x the x coordinate of the mouse
     * @param y the y coordinate of the mouse
     */
    public void onMultiClick(int x, int y, int clickCount) {
        if (selectable && (clickCount == 2) && (merchantControl != null) && (listEntry != null)) {
            log.debug("Received double click on item. Sending out a buy request for: {}", listEntry);
            merchantControl.buyItem(listEntry);
        }
    }

    /**
     * This function is called in case someone performs a mouse over, over the merchant entry.
     * <p />
     * Called by reflection. The reference is in the XML file.
     * <p />
     * This function will initiate the request for a look at.
     */
    public void onMouseOver() {
        if ((merchantControl != null) && (listEntry != null)) {
            merchantControl.lookAtItem(listEntry);
        }
    }

    public void setListEntry(@Nullable MerchantListEntry listEntry) {
        this.listEntry = listEntry;
    }

    @Nullable
    public MerchantListEntry getListEntry() {
        return listEntry;
    }
}
