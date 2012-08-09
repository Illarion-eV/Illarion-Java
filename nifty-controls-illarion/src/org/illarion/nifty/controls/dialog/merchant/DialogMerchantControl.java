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
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.DialogMerchant;
import org.illarion.nifty.controls.DialogMerchantBuyEvent;
import org.illarion.nifty.controls.DialogMerchantCloseEvent;
import org.illarion.nifty.controls.MerchantListEntry;

import java.util.Properties;

/**
 * This is the control class of the merchant dialogs. Not means to direct usage.
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
     * Helper variable to prevent double firing close events.
     */
    private boolean alreadyClosed;

    /**
     * The event handler that handles the events on the buy button.
     */
    private final EventTopicSubscriber<ButtonClickedEvent> buyButtonEventHandler;

    /**
     * This event handler is used to monitor the slider that sets the amount of items to buy.
     */
    private final EventTopicSubscriber<SliderChangedEvent> sliderChangedEventHandler;

    public DialogMerchantControl() {

        buyButtonEventHandler = new EventTopicSubscriber<ButtonClickedEvent>() {
            @Override
            public void onEvent(final String topic, final ButtonClickedEvent data) {
                if (alreadyClosed) {
                    return;
                }

                niftyInstance.publishEvent(getId(),
                        new DialogMerchantBuyEvent(dialogId, getSelectedItem(), getSelectedIndex(),
                                getSelectedAmount()));
            }
        };

        sliderChangedEventHandler = new EventTopicSubscriber<SliderChangedEvent>() {
            @Override
            public void onEvent(final String topic, final SliderChangedEvent data) {
                getElement().findNiftyControl("#buyCountAmountDisplay", Label.class).setText(
                        Integer.toString(getSelectedAmount()));
            }
        };
    }

    @Override
    public void bind(final Nifty nifty, final Screen screen, final Element element, final Properties parameter,
                     final Attributes controlDefinitionAttributes) {
        super.bind(nifty, screen, element, parameter, controlDefinitionAttributes);
        niftyInstance = nifty;
        currentScreen = screen;

        dialogId = Integer.parseInt(controlDefinitionAttributes.get("dialogId"));

        alreadyClosed = false;
    }

    @Override
    public void onStartScreen() {
        super.onStartScreen();

        final Element element = getElement();
        final Element parent = element.getParent();

        final int x = (parent.getWidth() - element.getWidth()) / 2;
        final int y = (parent.getHeight() - element.getHeight()) / 2;

        element.setConstraintX(new SizeValue(Integer.toString(x) + "px"));
        element.setConstraintY(new SizeValue(Integer.toString(y) + "px"));

        parent.layoutElements();

        final Element leftButton = getElement().findElementByName("#button");
        niftyInstance.subscribe(currentScreen, leftButton.getId(), ButtonClickedEvent.class, buyButtonEventHandler);
        niftyInstance.subscribe(currentScreen, getElement().findElementByName("#buyCountSlider").getId(),
                SliderChangedEvent.class, sliderChangedEventHandler);
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
    public int getSelectedAmount() {
        return Math.round(getElement().findNiftyControl("#buyCountSlider", Slider.class).getValue());
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
    public void addSellingItem(final MerchantListEntry entry) {
        getSellList().addItem(entry);
    }

    @Override
    public void addBuyingItem(final MerchantListEntry entry) {
        getBuyList().addItem(entry);
    }

    @SuppressWarnings("unchecked")
    private ListBox<MerchantListEntry> getSellList() {
        return getElement().findNiftyControl("#sellList", ListBox.class);
    }

    @SuppressWarnings("unchecked")
    private ListBox<MerchantListEntry> getBuyList() {
        return getElement().findNiftyControl("#buyList", ListBox.class);
    }

    @Override
    public void closeWindow() {
        super.closeWindow();
        niftyInstance.publishEvent(getId(), new DialogMerchantCloseEvent(dialogId));
    }
}
