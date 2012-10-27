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
package org.illarion.nifty.controls.dialog.select;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.DialogSelect;
import org.illarion.nifty.controls.DialogSelectCancelEvent;
import org.illarion.nifty.controls.DialogSelectSelectEvent;
import org.illarion.nifty.controls.SelectListEntry;

import java.util.Properties;

/**
 * This is the control class of the select dialogs. Not meant to direct usage.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @deprecated Use {@link DialogSelect}
 */
@Deprecated
public final class DialogSelectControl extends WindowControl implements DialogSelect {
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
     * The event handler that handles the events on the close button.
     */
    private final EventTopicSubscriber<ButtonClickedEvent> closeButtonEventHandler;

    public DialogSelectControl() {
        closeButtonEventHandler = new EventTopicSubscriber<ButtonClickedEvent>() {
            @Override
            public void onEvent(final String topic, final ButtonClickedEvent data) {
                if (alreadyClosed) {
                    return;
                }
                closeWindow();
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
        element.findNiftyControl("#message", Label.class).setText(
                controlDefinitionAttributes.getWithDefault("message", ""));
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

        final Element closeButton = getElement().findElementByName("#button");
        niftyInstance.subscribe(currentScreen, closeButton.getId(), ButtonClickedEvent.class, closeButtonEventHandler);
    }

    @Override
    public int getEntryCount() {
        return getList().itemCount();
    }

    @Override
    public SelectListEntry getSelectedItem() {
        return getList().getFocusItem();
    }

    @Override
    public int getSelectedIndex() {
        return getList().getFocusItemIndex();
    }

    @Override
    public void addItem(final SelectListEntry entry) {
        getList().addItem(entry);
    }

    public void selectItem(final int index) {
        final ListBox<SelectListEntry> list = getList();
        niftyInstance.publishEvent(getId(), new DialogSelectSelectEvent(dialogId, list.getItems().get(index), index));
    }

    public void selectItem(final SelectListEntry item) {
        selectItem(item.getIndex());
    }

    @SuppressWarnings("unchecked")
    private ListBox<SelectListEntry> getList() {
        return getElement().findNiftyControl("#list", ListBox.class);
    }

    @Override
    public void closeWindow() {
        super.closeWindow();
        niftyInstance.publishEvent(getId(), new DialogSelectCancelEvent(dialogId));
    }
}
