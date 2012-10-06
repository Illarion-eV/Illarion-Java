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
package org.illarion.nifty.controls.dialog.crafting;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.ListBoxSelectionChangedEvent;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.CraftingListEntry;
import org.illarion.nifty.controls.DialogCrafting;
import org.illarion.nifty.controls.DialogMessageConfirmedEvent;

import java.util.List;
import java.util.Properties;

/**
 * This is the main control class for message dialogs.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Deprecated
public class DialogCraftingControl
        extends WindowControl
        implements DialogCrafting, EventTopicSubscriber<ButtonClickedEvent> {
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
    }

    @Override
    public void onEvent(final String topic, final ButtonClickedEvent data) {
        if (alreadyClosed) {
            return;
        }
        niftyInstance.publishEvent(getId(), new DialogMessageConfirmedEvent(dialogId));
        closeWindow();
    }

    @Override
    public void closeWindow() {
        getElement().hide(new EndNotify() {
            @Override
            public void perform() {
                getElement().markForRemoval();
            }
        });
        alreadyClosed = true;
    }

    @Override
    public int getCraftingItemCount() {
        return getItemList().itemCount();
    }

    @Override
    public int getSelectedCraftingItem() {
        final List<Integer> selectedIndices = getItemList().getSelectedIndices();
        if (selectedIndices.isEmpty()) {
            return -1;
        }
        return selectedIndices.get(0);
    }

    private static final class ListEntry {
        private final CraftingListEntry entry;

        private ListEntry(final CraftingListEntry entry) {
            this.entry = entry;
        }

        public String toString() {
            return entry.getName();
        }
    }

    @NiftyEventSubscriber(id = "#craftItemList")
    public void onListFocusChanged(final String topic,
                                   final ListBoxSelectionChangedEvent<DialogCraftingControl.ListEntry> event) {
        final List<DialogCraftingControl.ListEntry> selection = event.getSelection();
        if (selection.isEmpty()) {
            return;
        }

        final CraftingListEntry selectedEntry = selection.get(0).entry;
        setSelectedItem(selectedEntry);
    }

    private void setSelectedItem(final CraftingListEntry selectedEntry) {
        final Element image = getElement().findElementByName("#selectedItemImage");
        image.getRenderer(ImageRenderer.class).setImage(selectedEntry.getImage());

        final Element title = getElement().findElementByName("#selectedItemName");
        title.getRenderer(TextRenderer.class).setText(selectedEntry.getName());

        final Element productionTime = getElement().findElementByName("#productionTime");
        productionTime.getRenderer(TextRenderer.class).setText("Production time: " +
                Double.toString(selectedEntry.getCraftTime()) + "s");

        final Element ingredientsPanel = getElement().findElementByName("#ingredients");
        final List<Element> elements = ingredientsPanel.getElements();

        for (final Element element : elements) {
            niftyInstance.removeElement(currentScreen, element);
        }

        final int ingredientsAmount = selectedEntry.getIngredientCount();
        Element currentPanel = null;
        for (int i = 0; i < ingredientsAmount; i++) {
            if ((i % 5) == 0) {
                final PanelBuilder builder = new PanelBuilder();
                builder.childLayoutHorizontal();
                currentPanel = builder.build(niftyInstance, currentScreen, ingredientsPanel);
            }

            final ImageBuilder builder = new ImageBuilder();
            builder.width("48px");
            builder.height("48px");
            final Element currentImage = builder.build(niftyInstance, currentScreen, currentPanel);
            currentImage.getRenderer(ImageRenderer.class).setImage(selectedEntry.getIngredientImage(i));
        }
    }

    @SuppressWarnings("unchecked")
    private ListBox<DialogCraftingControl.ListEntry> getItemList() {
        return getContent().findNiftyControl("#craftItemList", ListBox.class);
    }

    @Override
    public void addCraftingItems(final CraftingListEntry... entries) {
        final ListBox<DialogCraftingControl.ListEntry> list = getItemList();
        for (final CraftingListEntry entry : entries) {
            list.addItem(new DialogCraftingControl.ListEntry(entry));
        }
    }
}
