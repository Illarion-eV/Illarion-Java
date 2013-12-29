/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Nifty-GUI Controls is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Nifty-GUI Controls is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Nifty-GUI Controls.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.controls.dialog.merchant;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import illarion.common.gui.AbstractMultiActionHelper;
import org.illarion.nifty.controls.MerchantListEntry;

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
    private AbstractMultiActionHelper doubleClickHelper = new AbstractMultiActionHelper(
            (Integer) java.awt.Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval"), 2) {
        @Override
        public void executeAction(final int count) {
            if (count == 2) {
                merchantControl.buyItem(index);
            }
        }
    };

    private int index;
    @Nullable
    private ListBox<MerchantListEntry> listBox;
    @Nullable
    private DialogMerchantControl merchantControl;
    private boolean selectable;

    @SuppressWarnings("unchecked")
    @Override
    public void bind(
            @Nonnull final Nifty nifty,
            @Nonnull final Screen screen,
            @Nonnull final Element element,
            @Nonnull final Parameters parameter) {
        selectable = Boolean.parseBoolean(parameter.get("selectable"));

        if (selectable) {
            listBox = (ListBox<MerchantListEntry>) getParent(element, 4).getNiftyControl(ListBox.class);
            merchantControl = getParent(element, 12).getNiftyControl(DialogMerchantControl.class);
        }
    }

    private static Element getParent(final Element root, final int grade) {
        Element result = root;
        for (int i = 0; i < grade; i++) {
            result = result.getParent();
        }
        return result;
    }

    @Override
    public void onStartScreen() {
        doubleClickHelper.reset();
    }

    @Override
    public boolean inputEvent(@Nonnull final NiftyInputEvent inputEvent) {
        return false;
    }

    public void setIndex(final int value) {
        index = value;
    }

    /**
     * This function is called in case someone clicks on the control entry.
     *
     * @param x the x coordinate of the mouse
     * @param y the y coordinate of the mouse
     */
    public void onClick(final int x, final int y) {
        if (selectable) {
            doubleClickHelper.pulse();
        }
    }
}
