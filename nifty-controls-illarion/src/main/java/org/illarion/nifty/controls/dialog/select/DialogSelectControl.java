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

import javax.annotation.Nonnull;
import java.util.List;
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
    @Nonnull
    private final EventTopicSubscriber<ButtonClickedEvent> closeButtonEventHandler;

    /**
     * The event handler that handles clicks on the select button.
     */
    @Nonnull
    private final EventTopicSubscriber<ButtonClickedEvent> selectButtonEventHandler;

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

        selectButtonEventHandler = new EventTopicSubscriber<ButtonClickedEvent>() {
            @Override
            public void onEvent(final String topic, final ButtonClickedEvent data) {
                if (alreadyClosed) {
                    return;
                }
                selectItem(getSelectedIndex());
            }
        };
    }

    @Override
    public void bind(final Nifty nifty, final Screen screen, @Nonnull final Element element, final Properties parameter,
                     @Nonnull final Attributes controlDefinitionAttributes) {
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

        final Element closeButton = getElement().findElementById("#cancelButton");
        niftyInstance.subscribe(currentScreen, closeButton.getId(), ButtonClickedEvent.class, closeButtonEventHandler);

        final Element selectButton = getElement().findElementById("#selectButton");
        niftyInstance.subscribe(currentScreen, selectButton.getId(), ButtonClickedEvent.class, selectButtonEventHandler);
    }

    @Override
    public int getEntryCount() {
        return getList().itemCount();
    }

    @Override
    public SelectListEntry getSelectedItem() {
        final List<SelectListEntry> selectedItems = getList().getSelection();
        if (selectedItems.isEmpty()) {
            return null;
        }
        return selectedItems.get(0);
    }

    @Override
    public int getSelectedIndex() {
        final List<Integer> selectedIndices = getList().getSelectedIndices();
        if (selectedIndices.isEmpty()) {
            return -1;
        }
        return selectedIndices.get(0);
    }

    @Override
    public void addItem(@Nonnull final SelectListEntry entry) {
        getList().addItem(entry);
    }

    public void selectItem(final int index) {
        if (index == -1) {
            return;
        }
        final ListBox<SelectListEntry> list = getList();
        niftyInstance.publishEvent(getId(), new DialogSelectSelectEvent(dialogId, list.getItems().get(index), index));
    }

    @SuppressWarnings("unchecked")
    private ListBox<SelectListEntry> getList() {
        return getElement().findNiftyControl("#list", ListBox.class);
    }

    @Override
    public void closeWindow() {
        alreadyClosed = true;
        super.closeWindow();
        niftyInstance.publishEvent(getId(), new DialogSelectCancelEvent(dialogId));
    }
}
