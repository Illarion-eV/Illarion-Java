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
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.controls.textfield.filter.input.TextFieldInputCharFilter;
import de.lessvoid.nifty.controls.textfield.format.TextFieldDisplayFormat;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.effects.Effect;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.events.NiftyMouseMovedEvent;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.layout.align.HorizontalAlign;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.*;
import org.illarion.nifty.effects.DoubleEffect;

import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * This subscriber is used to keep track on clicks on the "craft" button in the crafting dialog.
     */
    private final class CraftButtonClickedEventSubscriber implements EventTopicSubscriber<ButtonClickedEvent> {
        @Override
        public void onEvent(final String topic, final ButtonClickedEvent data) {
            final CraftingItemEntry selectedItem = getSelectedCraftingItem();
            if (selectedItem == null) {
                return;
            }

            niftyInstance.publishEvent(getId(), new DialogCraftingCraftEvent(dialogId, selectedItem, getAmount()));
        }
    }

    /**
     * This subscriber is used to keep track on clicks on the "close" button in the crafting dialog.
     */
    private final class CloseButtonClickedEventSubscriber implements EventTopicSubscriber<ButtonClickedEvent> {
        @Override
        public void onEvent(final String topic, final ButtonClickedEvent data) {
            closeWindow();
            niftyInstance.publishEvent(getId(), new DialogCraftingCloseEvent(dialogId));
        }
    }

    private final class SelectCraftItemEventSubscriber implements
            EventTopicSubscriber<TreeItemSelectionChangedEvent<ListEntry>> {
        @Override
        public void onEvent(final String topic, final TreeItemSelectionChangedEvent<ListEntry> data) {
            final List<TreeItem<ListEntry>> selection = data.getSelection();
            if (selection.isEmpty()) {
                return;
            }

            final CraftingTreeItem selectedEntry = selection.get(0).getValue().entry;

            if (selectedEntry instanceof CraftingCategoryEntry) {
                setSelectedItem(null);
            } else {
                setSelectedItem((CraftingItemEntry) selectedEntry);
            }
        }
    }

    private final class MouseOverItemEventSubscriber implements EventTopicSubscriber<NiftyMouseMovedEvent> {
        @Override
        public void onEvent(final String topic, final NiftyMouseMovedEvent data) {
            final CraftingItemEntry selectedItem = getSelectedCraftingItem();
            if (selectedItem == null) {
                return;
            }
            niftyInstance.publishEvent(getId(), new DialogCraftingLookAtItemEvent(dialogId, selectedItem));
        }
    }

    private final class MouseOverIngredientItemEventSubscriber implements EventTopicSubscriber<NiftyMouseMovedEvent> {
        @Override
        public void onEvent(final String topic, final NiftyMouseMovedEvent data) {
            final CraftingItemEntry selectedItem = getSelectedCraftingItem();
            if (selectedItem == null) {
                return;
            }
            final Matcher matcher = INGREDIENT_INDEX_PATTERN.matcher(topic);
            if (!matcher.find()) {
                return;
            }

            final int ingredientId = Integer.parseInt(matcher.group(1));

            niftyInstance.publishEvent(getId(), new DialogCraftingLookAtIngredientItemEvent(dialogId,
                    selectedItem, ingredientId));
        }
    }

    private final class IncreaseAmountButtonEventSubscriber implements EventTopicSubscriber<ButtonClickedEvent> {
        @Override
        public void onEvent(final String topic, final ButtonClickedEvent data) {
            getAmountTextField().setText(Integer.toString(getAmount() + 1));
        }
    }

    private final class DecreaseAmountButtonEventSubscriber implements EventTopicSubscriber<ButtonClickedEvent> {
        @Override
        public void onEvent(final String topic, final ButtonClickedEvent data) {
            final int newAmount = getAmount() - 1;
            if (newAmount <= 1) {
                getAmountTextField().setText("");
            } else {
                getAmountTextField().setText(Integer.toString(newAmount));
            }
        }
    }

    private static final Pattern INGREDIENT_INDEX_PATTERN = Pattern.compile("#ingredient(\\d+)");

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
     * The root node of the tree that displays all the crafting items.
     */
    private TreeItem<ListEntry> treeRootNode;

    private final CraftButtonClickedEventSubscriber craftButtonEventHandler;
    private final CloseButtonClickedEventSubscriber closeButtonEventHandler;
    private final SelectCraftItemEventSubscriber listSelectionChangedEventHandler;
    private final MouseOverItemEventSubscriber mouseOverItemEventHandler;
    private final MouseOverIngredientItemEventSubscriber mouseOverIngredientEventHandler;
    private final IncreaseAmountButtonEventSubscriber increaseAmountButtonEventHandler;
    private final DecreaseAmountButtonEventSubscriber decreaseAmountButtonEventHandler;
    private final DecimalFormat timeFormat;

    public DialogCraftingControl() {
        craftButtonEventHandler = new CraftButtonClickedEventSubscriber();
        closeButtonEventHandler = new CloseButtonClickedEventSubscriber();
        listSelectionChangedEventHandler = new SelectCraftItemEventSubscriber();
        mouseOverItemEventHandler = new MouseOverItemEventSubscriber();
        mouseOverIngredientEventHandler = new MouseOverIngredientItemEventSubscriber();
        increaseAmountButtonEventHandler = new IncreaseAmountButtonEventSubscriber();
        decreaseAmountButtonEventHandler = new DecreaseAmountButtonEventSubscriber();

        treeRootNode = new TreeItem<ListEntry>();
        timeFormat = new DecimalFormat("#0.0");
    }

    @Override
    public void bind(final Nifty nifty, final Screen screen, final Element element, final Properties parameter,
                     final Attributes controlDefinitionAttributes) {
        super.bind(nifty, screen, element, parameter, controlDefinitionAttributes);
        niftyInstance = nifty;
        currentScreen = screen;

        dialogId = Integer.parseInt(controlDefinitionAttributes.get("dialogId"));

        alreadyClosed = false;

        getAmountTextField().enableInputFilter(new TextFieldInputCharFilter() {
            @Override
            public boolean acceptInput(final int index, final char newChar) {
                if (!Character.isDigit(newChar)) {
                    return false;
                }
                final String currentText = getAmountTextField().getRealText();
                final StringBuilder buffer = new StringBuilder(currentText);
                buffer.insert(index, newChar);

                final int value = Integer.parseInt(buffer.toString());
                return value > 0;
            }
        });

        getAmountTextField().setFormat(new TextFieldDisplayFormat() {
            @Override
            public CharSequence getDisplaySequence(final CharSequence original, final int start, final int end) {
                if (original.length() == 0) {
                    return Integer.toString(1);
                }
                return original.subSequence(start, end);
            }
        });
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

        niftyInstance.subscribe(currentScreen, getContent().findElementByName("#craftButton").getId(),
                ButtonClickedEvent.class, craftButtonEventHandler);
        niftyInstance.subscribe(currentScreen, getContent().findElementByName("#cancelButton").getId(),
                ButtonClickedEvent.class, closeButtonEventHandler);
        niftyInstance.subscribe(currentScreen, getContent().findElementByName("#buttonAmountUp").getId(),
                ButtonClickedEvent.class, increaseAmountButtonEventHandler);
        niftyInstance.subscribe(currentScreen, getContent().findElementByName("#buttonAmountDown").getId(),
                ButtonClickedEvent.class, decreaseAmountButtonEventHandler);
        niftyInstance.subscribe(currentScreen, getItemList().getElement().getId(),
                ListBoxSelectionChangedEvent.class, listSelectionChangedEventHandler);
        niftyInstance.subscribe(currentScreen, getContent().findElementByName("#selectedItemInfos").getId(),
                NiftyMouseMovedEvent.class, mouseOverItemEventHandler);

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
    public CraftingItemEntry getSelectedCraftingItem() {
        final List<TreeItem<ListEntry>> selection = getItemList().getSelection();
        if (selection.isEmpty()) {
            return null;
        }

        final CraftingTreeItem treeItem = selection.get(0).getValue().entry;
        if (treeItem instanceof CraftingItemEntry) {
            return (CraftingItemEntry) treeItem;
        }
        return null;
    }

    /**
     * Remove everything from the current item list.
     */
    @Override
    public void clearItemList() {
        treeRootNode = new TreeItem<ListEntry>();
        getItemList().setTree(treeRootNode);
    }

    /**
     * Select a item by the item index of the entry.
     */
    @Override
    public void selectItemByItemIndex(final int index) {
        TreeItem<ListEntry> selectedEntry = null;
        for (final TreeItem<ListEntry> categoryTreeItem : treeRootNode) {
            for (final TreeItem<ListEntry> itemTreeItem : categoryTreeItem) {
                final CraftingItemEntry currentItem = (CraftingItemEntry) itemTreeItem.getValue().entry;
                if (currentItem.getItemIndex() == index) {
                    selectedEntry = itemTreeItem;
                    break;
                }
            }

            if (selectedEntry != null) {
                break;
            }
        }

        if (selectedEntry == null) {
            return;
        }

        selectedEntry.getParentItem().setExpanded(true);

        final TreeBox<ListEntry> tree = getItemList();
        tree.setTree(treeRootNode);
        tree.selectItem(selectedEntry);
    }

    @Override
    public Element getCraftingItemDisplay() {
        return getElement().findElementByName("#selectedItemInfos");
    }

    @Override
    public Element getIngredientItemDisplay(final int index) {
        final Element ingredientsPanel = getElement().findElementByName("#ingredients");
        return ingredientsPanel.findElementByName("#ingredient" + Integer.toString(index));
    }

    /**
     * Get the text field that takes care for showing the amount of items that get produced at once.
     *
     * @return the amount textfield
     */
    public TextField getAmountTextField() {
        return getElement().findNiftyControl("#amountInput", TextField.class);
    }

    public int getAmount() {
        return Integer.parseInt(getAmountTextField().getDisplayedText());
    }

    private static final class ListEntry {
        private final CraftingTreeItem entry;

        private ListEntry(final CraftingTreeItem entry) {
            this.entry = entry;
        }

        public String toString() {
            return entry.getTreeLabel();
        }
    }

    private static void applyImage(final Element element, final NiftyImage image, final int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        if (width > maxSize) {
            height *= width / maxSize;
            width = maxSize;
        }
        if (height > maxSize) {
            width *= height / maxSize;
            height = maxSize;
        }

        final SizeValue widthSize = SizeValue.px(width);
        final SizeValue heightSize = SizeValue.px(height);

        element.getRenderer(ImageRenderer.class).setImage(image);
        element.setConstraintHeight(heightSize);
        element.setConstraintWidth(widthSize);
    }

    public void startProgress(final double seconds) {
        final Element progressBar = getContent().findElementByName("#progress");
        progressBar.getNiftyControl(Progress.class).setProgress(0.f);

        final List<Effect> effects = progressBar.getEffects(EffectEventId.onCustom, DoubleEffect.class);
        if (effects.isEmpty()) {
            return;
        }

        final Effect effect = effects.get(0);
        effect.getParameters().setProperty("length", Integer.toString((int) (seconds * 1000.0)));
        effect.updateParameters();

        progressBar.startEffect(EffectEventId.onCustom, null, "automaticProgress");
    }

    /**
     * Show the details of one item in the details part of the crafting window.
     *
     * @param selectedEntry the entry that is supposed to be displayed in detail
     */
    private void setSelectedItem(final CraftingItemEntry selectedEntry) {
        if (selectedEntry == null) {
            return;
        }
        final Element image = getContent().findElementByName("#selectedItemImage");
        applyImage(image, selectedEntry.getImage(), 64);

        final Label imageAmount = getContent().findNiftyControl("#selectedItemAmount", Label.class);
        if (selectedEntry.getBuildStackSize() == 1) {
            imageAmount.getElement().hide();
        } else {
            final Element imageAmountElement = imageAmount.getElement();
            final TextRenderer textRenderer = imageAmountElement.getRenderer(TextRenderer.class);
            textRenderer.setText(Integer.toString(selectedEntry.getBuildStackSize()));
            imageAmountElement.setConstraintWidth(SizeValue.px(textRenderer.getTextWidth()));
            imageAmountElement.setConstraintHorizontalAlign(HorizontalAlign.right);
            imageAmountElement.show();
        }

        final Element title = getContent().findElementByName("#selectedItemName");
        title.getRenderer(TextRenderer.class).setText(selectedEntry.getName());

        final Element productionTime = getContent().findElementByName("#productionTime");
        productionTime.getRenderer(TextRenderer.class).setText("${illarion-dialog-crafting-bundle.craftTime}: " +
                timeFormat.format(selectedEntry.getCraftTime()) + "s");

        final Element ingredientsPanel = getContent().findElementByName("#ingredients");

        final int ingredientsAmount = selectedEntry.getIngredientCount();
        Element currentPanel = null;
        for (int i = 0; i < ingredientsAmount; i++) {
            if ((i % 10) == 0) {
                currentPanel = getIngredientPanel(ingredientsPanel, i / 10);
            }

            final Element currentImage = getIngredientImage(ingredientsPanel.getId(), currentPanel, i % 10);
            applyImage(currentImage.getElements().get(0), selectedEntry.getIngredientImage(i), 32);
            showIngredientAmount(currentImage, selectedEntry.getIngredientAmount(i));
        }

        int index = ingredientsAmount;
        while (deleteIngredientImage(ingredientsPanel, index)) {
            index++;
        }
        int panelIndex = (ingredientsAmount / 10) + 1;
        while (deleteIngredientPanel(ingredientsPanel, panelIndex)) {
            panelIndex++;
        }

        ingredientsPanel.setConstraintHeight(SizeValue.px(((ingredientsAmount / 10) + 1) * 34));

        getElement().getNifty().getCurrentScreen().resetLayout();
        getElement().getNifty().getCurrentScreen().layoutLayers();
    }

    private boolean deleteIngredientPanel(final Element ingredientsPanel, final int index) {
        final List<Element> elements = ingredientsPanel.getElements();
        if ((elements.size() - 1) >= index) {
            niftyInstance.removeElement(currentScreen, elements.get(index));
            return true;
        }
        return false;
    }

    private Element getIngredientPanel(final Element ingredientsPanel, final int index) {
        final List<Element> elements = ingredientsPanel.getElements();
        if ((elements.size() - 1) >= index) {
            return elements.get(index);
        }
        if (elements.size() < index) {
            throw new InvalidParameterException("Index out of valid range");
        }
        final PanelBuilder builder = new PanelBuilder();
        builder.childLayoutHorizontal();
        builder.width("450px");
        builder.height("32px");
        builder.marginBottom("1px");
        builder.marginTop("1px");
        return builder.build(niftyInstance, currentScreen, ingredientsPanel);
    }

    private boolean deleteIngredientImage(final Element ingredientsPanel, final int index) {
        final Element image = ingredientsPanel.findElementByName("#ingredient" + Integer.toString(index));
        if (image == null) {
            return false;
        }
        niftyInstance.unsubscribe(image.getId(), mouseOverIngredientEventHandler);
        niftyInstance.removeElement(currentScreen, image);
        return true;
    }

    private Element getIngredientImage(final String parentId, final Element parentPanel, final int index) {
        final List<Element> elements = parentPanel.getElements();
        if ((elements.size() - 1) >= index) {
            return elements.get(index);
        }
        if (elements.size() < index) {
            throw new InvalidParameterException("Index out of valid range");
        }

        final PanelBuilder panelBuilder = new PanelBuilder(parentId + "#ingredient" + Integer.toString(index));
        panelBuilder.margin("1px");
        panelBuilder.childLayoutCenter();
        panelBuilder.width("32px");
        panelBuilder.height("32px");
        panelBuilder.visibleToMouse();
        final ImageBuilder builder = new ImageBuilder();
        panelBuilder.image(builder);

        final Element ingredientObject = panelBuilder.build(niftyInstance, currentScreen, parentPanel);
        niftyInstance.subscribe(currentScreen, ingredientObject.getId(), NiftyMouseMovedEvent.class,
                mouseOverIngredientEventHandler);
        return ingredientObject;
    }

    @Override
    public boolean inputEvent(final NiftyInputEvent inputEvent) {
        super.inputEvent(inputEvent);
        return true;
    }

    private void showIngredientAmount(final Element ingredientElement, final int count) {
        final List<Element> elements = ingredientElement.getElements();
        if ((elements.size() > 2) || (elements.size() < 1)) {
            throw new InvalidParameterException("Something is wrong, parent element appears to be wrong.");
        }

        if (count > 1) {
            if (elements.size() == 2) {
                final Label countLabel = elements.get(1).getNiftyControl(Label.class);
                countLabel.setText(Integer.toString(count));
            } else {
                final LabelBuilder labelBuilder = new LabelBuilder();
                labelBuilder.text(Integer.toString(count));
                labelBuilder.alignRight();
                labelBuilder.valignBottom();
                labelBuilder.color("#ff0f");
                labelBuilder.backgroundColor("#bb15");
                labelBuilder.visibleToMouse(false);

                labelBuilder.build(niftyInstance, currentScreen, ingredientElement);
            }
        } else {
            if (elements.size() == 2) {
                niftyInstance.removeElement(currentScreen, elements.get(1));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private TreeBox<ListEntry> getItemList() {
        return getContent().findNiftyControl("#craftItemList", TreeBox.class);
    }

    @Override
    public void addCraftingItems(final CraftingCategoryEntry... entries) {
        final TreeBox<DialogCraftingControl.ListEntry> list = getItemList();

        for (final CraftingCategoryEntry entry : entries) {
            final TreeItem<ListEntry> categoryItem = new TreeItem<ListEntry>(new ListEntry(entry));
            for (final CraftingItemEntry itemEntry : entry.getChildren()) {
                categoryItem.addTreeItem(new TreeItem<ListEntry>(new ListEntry(itemEntry)));
            }
            treeRootNode.addTreeItem(categoryItem);
        }

        list.setTree(treeRootNode);
    }

    @Override
    public void setProgress(final float progress) {
        final Element progressBar = getContent().findElementByName("#progress");
        progressBar.stopEffect(EffectEventId.onCustom);
        progressBar.getNiftyControl(Progress.class).setProgress(progress);
    }
}
