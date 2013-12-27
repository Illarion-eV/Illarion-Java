/*
 * This file is part of the Illarion Nifty-GUI Controls.
 *
 * Copyright © 2013 - Illarion e.V.
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
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.DialogMerchant;
import org.illarion.nifty.controls.DialogMerchantBuyEvent;
import org.illarion.nifty.controls.DialogMerchantCloseEvent;
import org.illarion.nifty.controls.MerchantListEntry;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This is the control class of the merchant dialogs. Not meant to direct usage.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @deprecated Use {@link DialogMerchant}
 */
@Deprecated
public final class DialogMerchantControl extends WindowControl implements DialogMerchant {
    /**
     * The instance of the Nifty-GUI that is parent to this control.
     */
    private Nifty niftyInstance;

    /**
     * The screen that displays this control.
     */
    private Screen currentScreen;

    /**
     * The ID of this dialog.
     */
    private int dialogId;

    /**
     * The event handler that handles the events on the close button.
     */
    @Nonnull
    private final EventTopicSubscriber<ButtonClickedEvent> closeButtonEventHandler;

    public DialogMerchantControl() {
        closeButtonEventHandler = new EventTopicSubscriber<ButtonClickedEvent>() {
            @Override
            public void onEvent(final String topic, final ButtonClickedEvent data) {
                closeWindow();
            }
        };
    }

    @Override
    public void bind(@Nonnull final Nifty nifty,
                     @Nonnull final Screen screen,
                     @Nonnull final Element element,
                     @Nonnull final Parameters parameter) {
        super.bind(nifty, screen, element, parameter);
        niftyInstance = nifty;
        currentScreen = screen;

        dialogId = parameter.getAsInteger("dialogId", -1);
    }

    @Override
    public void onStartScreen() {
        super.onStartScreen();

        final Element element = getElement();
        if (element == null) {
            return;
        }
        final Element parent = element.getParent();

        final int x = (parent.getWidth() - element.getWidth()) / 2;
        final int y = (parent.getHeight() - element.getHeight()) / 2;

        element.setConstraintX(SizeValue.px(x));
        element.setConstraintY(SizeValue.px(y));

        parent.layoutElements();

        final Element closeButton = getElement().findElementById("#button");
        niftyInstance.subscribe(currentScreen, closeButton.getId(), ButtonClickedEvent.class, closeButtonEventHandler);
    }

    @Override
    public int getBuyEntryCount() {
        return getBuyList().itemCount();
    }

    @Override
    public int getSellEntryCount() {
        return getSellList().itemCount();
    }

    @Override
    public MerchantListEntry getSelectedItem() {
        return getSellList().getFocusItem();
    }

    @Override
    public int getSelectedIndex() {
        return getSellList().getFocusItemIndex();
    }

    @Override
    public void addAllSellingItems(@Nonnull final List<MerchantListEntry> entry) {
        getSellList().addAllItems(entry);
    }

    @Override
    public void addSellingItem(@Nonnull final MerchantListEntry entry) {
        getSellList().addItem(entry);
    }

    @Override
    public void addAllBuyingItems(@Nonnull final List<MerchantListEntry> entry) {
        getBuyList().addAllItems(entry);
    }

    @Override
    public void setDialogId(final int id) {
        dialogId = id;
    }

    @Override
    public int getDialogId() {
        return dialogId;
    }

    @Override
    public void addBuyingItem(@Nonnull final MerchantListEntry entry) {
        getBuyList().addItem(entry);
    }

    /**
     * Remove all items from both the buying and the selling list.
     */
    @Override
    public void clearItems() {
        getSellList().clear();
        getBuyList().clear();
    }

    @SuppressWarnings("unchecked")
    private ListBox<MerchantListEntry> getSellList() {
        return getElement().findNiftyControl("#sellList", ListBox.class);
    }

    @SuppressWarnings("unchecked")
    private ListBox<MerchantListEntry> getBuyList() {
        return getElement().findNiftyControl("#buyList", ListBox.class);
    }

    public void buyItem(final int index) {
        final ListBox<MerchantListEntry> sellList = getSellList();
        niftyInstance.publishEvent(getId(),
                new DialogMerchantBuyEvent(dialogId, sellList.getItems().get(index), index));
    }

    public void buyItem(@Nonnull final MerchantListEntry entry) {
        buyItem(entry.getIndex());
    }

    @Override
    public void closeWindow() {
        super.closeWindow();
        niftyInstance.publishEvent(getId(), new DialogMerchantCloseEvent(dialogId));
    }
}
