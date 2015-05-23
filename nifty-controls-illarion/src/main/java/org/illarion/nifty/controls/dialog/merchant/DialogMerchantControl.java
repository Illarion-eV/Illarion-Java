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
package org.illarion.nifty.controls.dialog.merchant;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.NiftyControl;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.common.types.Rectangle;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.*;
import org.illarion.nifty.controls.MerchantListEntry.EntryType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
        closeButtonEventHandler = (topic, data) -> closeWindow();
    }

    @Override
    public void bind(
            @Nonnull Nifty nifty,
            @Nonnull Screen screen,
            @Nonnull Element element,
            @Nonnull Parameters parameter) {
        super.bind(nifty, screen, element, parameter);
        niftyInstance = nifty;
        currentScreen = screen;

        dialogId = parameter.getAsInteger("dialogId", -1);
    }

    @Override
    public void onStartScreen() {
        super.onStartScreen();

        Element element = getElement();
        if (element == null) {
            return;
        }
        Element parent = element.getParent();

        int x = (parent.getWidth() - element.getWidth()) / 2;
        int y = (parent.getHeight() - element.getHeight()) / 2;

        element.setConstraintX(SizeValue.px(x));
        element.setConstraintY(SizeValue.px(y));

        parent.layoutElements();

        Element closeButton = getElement().findElementById("#button");
        if (closeButton == null) {
            throw new IllegalStateException("Failed to fetch close button of merchant dialog.");
        }
        String closeButtonId = closeButton.getId();
        assert closeButtonId != null;
        niftyInstance.subscribe(currentScreen, closeButtonId, ButtonClickedEvent.class, closeButtonEventHandler);
    }

    @Override
    public int getBuyEntryCount() {
        ListBox<MerchantListEntry> buyList = getBuyList();
        if (buyList == null) {
            return 0;
        }
        return buyList.itemCount();
    }

    @Override
    public int getSellEntryCount() {
        ListBox<MerchantListEntry> sellList = getSellList();
        if (sellList == null) {
            return 0;
        }
        return sellList.itemCount();
    }

    @Override
    public MerchantListEntry getSelectedItem() {
        ListBox<MerchantListEntry> sellList = getSellList();
        if (sellList == null) {
            throw new IllegalStateException("Fetching the selected item happen before properly binding the list.");
        }
        return sellList.getFocusItem();
    }

    @Override
    public int getSelectedIndex() {
        ListBox<MerchantListEntry> sellList = getSellList();
        if (sellList == null) {
            throw new IllegalStateException("Fetching the selected item happen before properly binding the list.");
        }
        return sellList.getFocusItemIndex();
    }

    @Override
    public void addAllSellingItems(@Nonnull Collection<MerchantListEntry> entry) {
        ListBox<MerchantListEntry> sellList = getSellList();
        if (sellList == null) {
            throw new IllegalStateException("Adding a sell item can't happen before properly binding the list.");
        }
        sellList.addAllItems(entry);
    }

    @Override
    public void addSellingItem(@Nonnull MerchantListEntry entry) {
        if (entry.getEntryType() != EntryType.Selling) {
            throw new IllegalArgumentException("Entry for selling list requires to by of type selling.");
        }
        ListBox<MerchantListEntry> sellList = getSellList();
        if (sellList == null) {
            throw new IllegalStateException("Adding a sell item can't happen before properly binding the list.");
        }
        sellList.addItem(entry);
    }

    @Override
    public void addAllBuyingItems(@Nonnull Collection<MerchantListEntry> entry) {
        ListBox<MerchantListEntry> buyList = getBuyList();
        if (buyList == null) {
            throw new IllegalStateException("Adding a buy item can't happen before properly binding the list.");
        }
        buyList.addAllItems(entry);
    }

    @Override
    public void setDialogId(int id) {
        dialogId = id;
    }

    @Override
    public int getDialogId() {
        return dialogId;
    }

    @Override
    public void addBuyingItem(@Nonnull MerchantListEntry entry) {
        if ((entry.getEntryType() != EntryType.BuyPrimary) &&
                (entry.getEntryType() != EntryType.BuySecondary)) {
            throw new IllegalArgumentException("Entry for buying list requires to by of type buying.");
        }
        ListBox<MerchantListEntry> buyList = getBuyList();
        if (buyList == null) {
            throw new IllegalStateException("Adding a buy item can't happen before properly binding the list.");
        }
        buyList.addItem(entry);
    }

    /**
     * Remove all items from both the buying and the selling list.
     */
    @Override
    public void clearItems() {
        clearList(getSellList());
        clearList(getBuyList());
    }

    @Nonnull
    @Override
    public Rectangle getRenderAreaForEntry(@Nonnull MerchantListEntry entry) {
        ListBox<MerchantListEntry> listBox = null;
        switch (entry.getEntryType()) {
            case BuyPrimary:
            case BuySecondary:
                listBox = getBuyList();
                break;
            case Selling:
                listBox = getSellList();
                break;
        }

        if (listBox == null) {
            return new Rectangle();
        }
        Element listBoxElement = listBox.getElement();
        if ((listBoxElement == null) || !listBoxElement.isVisible()) {
            return new Rectangle();
        }

        Collection<DialogMerchantEntryControl> entryControls;
        entryControls = getAllNiftyControls(listBoxElement, DialogMerchantEntryControl.class);

        for (DialogMerchantEntryControl control : entryControls) {
            MerchantListEntry controlEntry = control.getListEntry();
            if ((controlEntry != null) && controlEntry.equals(entry)) {
                Element controlElement = control.getElement();
                if (controlElement != null) {
                    return new Rectangle(controlElement.getX(), controlElement.getY(), controlElement.getWidth(),
                                         controlElement.getHeight());
                }
            }
        }
        return new Rectangle();
    }

    /**
     * Retrieve all controls of one type that are on the same layer inside the search root.
     */
    @Nonnull
    private static <T extends NiftyControl> Collection<T> getAllNiftyControls(@Nonnull Element searchRoot,
            @Nonnull Class<T> controlClass) {
        T firstChild = searchRoot.getNiftyControl(controlClass);
        if (firstChild == null) {
            return Collections.emptyList();
        }
        Element firstChildElement = firstChild.getElement();
        if (firstChildElement == null) {
            return Collections.singletonList(firstChild);
        }
        Element parent = firstChild.getElement().getParent();
        Collection<Element> children = parent.getChildren();
        Collection<T> result = new ArrayList<>();
        for (Element child : children) {
            T control = child.getNiftyControl(controlClass);
            if (control != null) {
                result.add(control);
            }
        }
        return result;
    }

    private static <T> void clearList(@Nullable ListBox<T> list) {
        if (list != null) {
            list.clear();
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private ListBox<MerchantListEntry> getSellList() {
        Element root = getContent();
        return (root == null) ? null : root.findNiftyControl("#sellList", ListBox.class);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private ListBox<MerchantListEntry> getBuyList() {
        Element root = getContent();
        return (root == null) ? null : root.findNiftyControl("#buyList", ListBox.class);
    }

    public void buyItem(@Nonnull MerchantListEntry entry) {
        String id = getId();
        if (id == null) {
            throw new IllegalStateException("The ID of a merchant dialog can't be null");
        }
        niftyInstance.publishEvent(id, new DialogMerchantBuyEvent(dialogId, entry));
    }

    public void lookAtItem(@Nonnull MerchantListEntry entry) {
        String id = getId();
        if (id == null) {
            throw new IllegalStateException("The ID of a merchant dialog can't be null");
        }
        niftyInstance.publishEvent(id, new DialogMerchantLookAtEvent(dialogId, entry));
    }

    @Override
    public void closeWindow() {
        super.closeWindow();
        String id = getId();
        if (id != null) {
            niftyInstance.publishEvent(id, new DialogMerchantCloseEvent(dialogId));
        }
    }
}
