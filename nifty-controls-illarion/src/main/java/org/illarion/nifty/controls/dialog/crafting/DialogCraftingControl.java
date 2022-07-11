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
package org.illarion.nifty.controls.dialog.crafting;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
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
import illarion.common.types.ItemCount;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.*;
import org.illarion.nifty.effects.DoubleEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the main control class for message dialogs.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @deprecated Use {@link DialogCrafting} to access the dialog
 */
@Deprecated
public class DialogCraftingControl extends WindowControl
        implements DialogCrafting, EventTopicSubscriber<ButtonClickedEvent> {

    /**
     * The size of the slot to show the ingredient in pixels.
     */
    private static final int INGREDIENT_IMAGE_SIZE = 32;

    /**
     * This subscriber is used to keep track on clicks on the "craft" button in the crafting dialog.
     */
    private final class CraftButtonClickedEventSubscriber implements EventTopicSubscriber<ButtonClickedEvent> {
        @Override
        public void onEvent(String topic, ButtonClickedEvent data) {
            CraftingItemEntry selectedItem = getSelectedCraftingItem();
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
        public void onEvent(String topic, ButtonClickedEvent data) {
            closeWindow();
            niftyInstance.publishEvent(getId(), new DialogCraftingCloseEvent(dialogId));
        }
    }

    private final class SelectCraftItemEventSubscriber
            implements EventTopicSubscriber<TreeItemSelectionChangedEvent<ListEntry>> {
        @Override
        public void onEvent(String topic, @Nonnull TreeItemSelectionChangedEvent<ListEntry> data) {
            List<TreeItem<ListEntry>> selection = data.getSelection();
            if (selection.isEmpty()) {
                return;
            }

            TreeItem<ListEntry> selectedTreeEntry = selection.get(0);
            CraftingTreeItem selectedEntry = selectedTreeEntry.getValue().entry;

            if (selectedEntry instanceof CraftingItemEntry) {
                setSelectedItem((CraftingItemEntry) selectedEntry);
            } else {
                setSelectedItem(null);
            }
        }
    }

    private final class MouseOverItemEventSubscriber implements EventTopicSubscriber<NiftyMouseMovedEvent> {
        @Override
        public void onEvent(String topic, NiftyMouseMovedEvent data) {
            CraftingItemEntry selectedItem = getSelectedCraftingItem();
            if (selectedItem == null) {
                return;
            }
            niftyInstance.publishEvent(getId(), new DialogCraftingLookAtItemEvent(dialogId, selectedItem));
        }
    }

    private final class MouseOverIngredientItemEventSubscriber implements EventTopicSubscriber<NiftyMouseMovedEvent> {
        @Override
        public void onEvent(@Nonnull String topic, NiftyMouseMovedEvent data) {
            CraftingItemEntry selectedItem = getSelectedCraftingItem();
            if (selectedItem == null) {
                return;
            }
            Matcher matcher = INGREDIENT_INDEX_PATTERN.matcher(topic);
            if (!matcher.find()) {
                return;
            }

            int ingredientId = Integer.parseInt(matcher.group(1));

            niftyInstance.publishEvent(getId(), new DialogCraftingLookAtIngredientItemEvent(dialogId, selectedItem,
                                                                                            ingredientId));
        }
    }

    private final class IncreaseAmountButtonEventSubscriber implements EventTopicSubscriber<ButtonClickedEvent> {
        @Override
        public void onEvent(String topic, ButtonClickedEvent data) {
            getAmountTextField().setText(Integer.toString(getAmount() + 1));
        }
    }

    private final class DecreaseAmountButtonEventSubscriber implements EventTopicSubscriber<ButtonClickedEvent> {
        @Override
        public void onEvent(String topic, ButtonClickedEvent data) {
            int newAmount = getAmount() - 1;
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
     * The root node of the tree that displays all the crafting items.
     */
    private TreeItem<ListEntry> treeRootNode;

    @Nonnull
    private final CraftButtonClickedEventSubscriber craftButtonEventHandler;
    @Nonnull
    private final CloseButtonClickedEventSubscriber closeButtonEventHandler;
    @Nonnull
    private final SelectCraftItemEventSubscriber listSelectionChangedEventHandler;
    @Nonnull
    private final MouseOverItemEventSubscriber mouseOverItemEventHandler;
    @Nonnull
    private final MouseOverIngredientItemEventSubscriber mouseOverIngredientEventHandler;
    @Nonnull
    private final IncreaseAmountButtonEventSubscriber increaseAmountButtonEventHandler;
    @Nonnull
    private final DecreaseAmountButtonEventSubscriber decreaseAmountButtonEventHandler;
    @Nonnull
    private final DecimalFormat timeFormat;

    public DialogCraftingControl() {
        craftButtonEventHandler = new CraftButtonClickedEventSubscriber();
        closeButtonEventHandler = new CloseButtonClickedEventSubscriber();
        listSelectionChangedEventHandler = new SelectCraftItemEventSubscriber();
        mouseOverItemEventHandler = new MouseOverItemEventSubscriber();
        mouseOverIngredientEventHandler = new MouseOverIngredientItemEventSubscriber();
        increaseAmountButtonEventHandler = new IncreaseAmountButtonEventSubscriber();
        decreaseAmountButtonEventHandler = new DecreaseAmountButtonEventSubscriber();

        treeRootNode = new TreeItem<>();
        timeFormat = new DecimalFormat("#0.0");
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

        dialogId = Integer.parseInt(parameter.getWithDefault("dialogId", "-1"));

        nifty.subscribeAnnotations(this);

        getAmountTextField().enableInputFilter((int index, char newChar) -> {
            if (!Character.isDigit(newChar)) {
                return false;
            }
            String currentText = getAmountTextField().getRealText();
            if (currentText.length() >= 5) {
                return false;
            }

            StringBuilder buffer = new StringBuilder(currentText);
            buffer.insert(index, newChar);

            try {
                int value = Integer.parseInt(buffer.toString());
                return value > 0;
            } catch (NumberFormatException ex) {
                return false;
            }
        });

        getAmountTextField().setFormat(new TextFieldDisplayFormat() {
            @Nonnull
            @Override
            public CharSequence getDisplaySequence(
                    @Nonnull CharSequence original,
                    int start,
                    int end) {
                CharSequence usedText = original;
                if (original.length() == 0) {
                    return Integer.toString(1);
                }
                if (original.length() >= 5) {
                    return Integer.toString(10000);
                }
                return usedText.subSequence(start, end);
            }
        });
    }

    @Override
    public void onStartScreen() {
        super.onStartScreen();

        Element element = getElement();
        Element parent = element.getParent();

        int x = (parent.getWidth() - element.getWidth()) / 2;
        int y = (parent.getHeight() - element.getHeight()) / 2;

        element.setConstraintX(new SizeValue(Integer.toString(x) + "px"));
        element.setConstraintY(new SizeValue(Integer.toString(y) + "px"));

        parent.layoutElements();

        niftyInstance.subscribe(currentScreen, getContent().findElementById("#craftButton").getId(),
                                ButtonClickedEvent.class, craftButtonEventHandler);
        niftyInstance.subscribe(currentScreen, getContent().findElementById("#cancelButton").getId(),
                                ButtonClickedEvent.class, closeButtonEventHandler);
        niftyInstance.subscribe(currentScreen, getContent().findElementById("#buttonAmountUp").getId(),
                                ButtonClickedEvent.class, increaseAmountButtonEventHandler);
        niftyInstance.subscribe(currentScreen, getContent().findElementById("#buttonAmountDown").getId(),
                                ButtonClickedEvent.class, decreaseAmountButtonEventHandler);
        niftyInstance.subscribe(currentScreen, getItemList().getElement().getId(), ListBoxSelectionChangedEvent.class,
                                listSelectionChangedEventHandler);
        niftyInstance.subscribe(currentScreen, getContent().findElementById("#selectedItemInfos").getId(),
                                NiftyMouseMovedEvent.class, mouseOverItemEventHandler);
    }

    @Override
    public void onEvent(String topic, ButtonClickedEvent data) {
        niftyInstance.publishEvent(getId(), new DialogMessageConfirmedEvent(dialogId));
        closeWindow();
    }

    @Nullable
    @Override
    public CraftingItemEntry getSelectedCraftingItem() {
        List<TreeItem<ListEntry>> selection = getItemList().getSelection();
        if (selection.isEmpty()) {
            return null;
        }

        CraftingTreeItem treeItem = selection.get(0).getValue().entry;
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
        treeRootNode = new TreeItem<>();
        getItemList().setTree(treeRootNode);
    }

    /**
     * Select a item by the item index of the entry.
     */
    @Override
    public void selectItemByItemIndex(int index) {
        TreeItem<ListEntry> selectedEntry = null;
        for (TreeItem<ListEntry> categoryTreeItem : treeRootNode) {
            for (TreeItem<ListEntry> itemTreeItem : categoryTreeItem) {
                CraftingItemEntry currentItem = (CraftingItemEntry) itemTreeItem.getValue().entry;
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
            setSelectedItem(null);
            return;
        }

        if (selectedEntry.getParentItem() != null) {
            selectedEntry.getParentItem().setExpanded(true);
        }

        updateTree(selectedEntry);
        setSelectedItem((CraftingItemEntry) selectedEntry.getValue().entry);
    }

    private void updateTree(@Nullable TreeItem<ListEntry> selectedItem) {
        TreeBox<ListEntry> tree = getItemList();
        tree.setTree(treeRootNode);
        if (selectedItem != null) {
            tree.selectItem(selectedItem);
        }
    }

    @Nonnull
    @Override
    public Element getCraftingItemDisplay() {
        return getElement().findElementById("#selectedItemInfos");
    }

    @Nonnull
    @Override
    public Element getIngredientItemDisplay(int index) {
        Element ingredientsPanel = getElement().findElementById("#ingredients");
        return ingredientsPanel.findElementById("#ingredient" + Integer.toString(index));
    }

    /**
     * Get the text field that takes care for showing the amount of items that get produced at once.
     *
     * @return the amount textfield
     */
    @Nullable
    public TextField getAmountTextField() {
        return getElement().findNiftyControl("#amountInput", TextField.class);
    }

    @Override
    public int getAmount() {
        int amount = Integer.parseInt(getAmountTextField().getDisplayedText());
        if (amount > 250) {
            amount = 250;
        }
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        if (amount <= 1) {
            getAmountTextField().setText("");
        } else {
            getAmountTextField().setText(Integer.toString(amount));
        }
    }

    private static final class ListEntry {
        private final CraftingTreeItem entry;

        ListEntry(CraftingTreeItem entry) {
            this.entry = entry;
        }

        @Override
        @Nonnull
        public String toString() {
            return entry.getTreeLabel();
        }
    }

    private static void applyImage(@Nonnull Element element, @Nonnull NiftyImage image, int maxSize) {
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

        SizeValue widthSize = SizeValue.px(width);
        SizeValue heightSize = SizeValue.px(height);

        element.getRenderer(ImageRenderer.class).setImage(image);
        element.setConstraintHeight(heightSize);
        element.setConstraintWidth(widthSize);
    }

    @Override
    public void startProgress(double seconds) {
        Element progressBar = getContent().findElementById("#progress");
        progressBar.getNiftyControl(Progress.class).setProgress(0.f);

        List<Effect> effects = progressBar.getEffects(EffectEventId.onCustom, DoubleEffect.class);
        if (effects.isEmpty()) {
            return;
        }

        Effect effect = effects.get(0);
        effect.getParameters().setProperty("length", Integer.toString((int) (seconds * 1000.0)));
        effect.updateParameters();

        progressBar.startEffect(EffectEventId.onCustom, null, "automaticProgress");
    }

    @Override
    public void setDialogId(int id) {
        dialogId = id;
    }

    @Override
    public int getDialogId() {
        return dialogId;
    }

    /**
     * Show the details of one item in the details part of the crafting window.
     *
     * @param selectedEntry the entry that is supposed to be displayed in detail
     */
    private void setSelectedItem(@Nullable CraftingItemEntry selectedEntry) {
        if (selectedEntry == null) {
            Element image = getContent().findElementById("#selectedItemImage");
            image.getRenderer(ImageRenderer.class).setImage(null);

            Label imageAmount = getContent().findNiftyControl("#selectedItemAmount", Label.class);
            imageAmount.getElement().hide();

            Element ingredientsPanel = getContent().findElementById("#ingredients");

            int index = 0;
            while (deleteIngredientImage(ingredientsPanel, index)) {
                index++;
            }
            return;
        }
        Element image = getContent().findElementById("#selectedItemImage");
        applyImage(image, selectedEntry.getImage(), 56);

        Label imageAmount = getContent().findNiftyControl("#selectedItemAmount", Label.class);
        if (ItemCount.isGreaterOne(selectedEntry.getBuildStackSize())) {
            Element imageAmountElement = imageAmount.getElement();
            TextRenderer textRenderer = imageAmountElement.getRenderer(TextRenderer.class);
            textRenderer.setText(Integer.toString(selectedEntry.getBuildStackSize().getValue()));
            imageAmountElement.setConstraintWidth(SizeValue.px(textRenderer.getTextWidth()));
            imageAmountElement.setConstraintHorizontalAlign(HorizontalAlign.right);
            imageAmountElement.show();
        } else {
            imageAmount.getElement().hide();
        }

        Element title = getContent().findElementById("#selectedItemName");
        title.getRenderer(TextRenderer.class).setText(selectedEntry.getName());

        Element productionTime = getContent().findElementById("#productionTime");
        productionTime.getRenderer(TextRenderer.class).setText(
                "${illarion-dialog-crafting-bundle.craftTime}: " + timeFormat.format(selectedEntry.getCraftTime()) +
                        "s");

        Element ingredientsPanel = getContent().findElementById("#ingredients");

        int ingredientsAmount = selectedEntry.getIngredientCount();
        Element currentPanel = null;
        for (int i = 0; i < ingredientsAmount; i++) {
            if ((i % 10) == 0) {
                currentPanel = getIngredientPanel(ingredientsPanel, i / 10);
            }

            assert currentPanel != null;
            Element currentImage = getIngredientImage(ingredientsPanel.getId(), currentPanel, i % 10);
            applyImage(currentImage.getChildren().get(0), selectedEntry.getIngredientImage(i), INGREDIENT_IMAGE_SIZE);
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

        getElement().getParent().layoutElements();
    }

    private boolean deleteIngredientPanel(@Nonnull Element ingredientsPanel, int index) {
        List<Element> elements = ingredientsPanel.getChildren();
        if ((elements.size() - 1) >= index) {
            niftyInstance.removeElement(currentScreen, elements.get(index));
            return true;
        }
        return false;
    }

    @Nonnull
    private Element getIngredientPanel(@Nonnull Element ingredientsPanel, int index) {
        List<Element> elements = ingredientsPanel.getChildren();
        if ((elements.size() - 1) >= index) {
            return elements.get(index);
        }
        if (elements.size() < index) {
            throw new InvalidParameterException("Index out of valid range");
        }
        PanelBuilder builder = new PanelBuilder();
        builder.childLayoutHorizontal();
        builder.width("450px");
        builder.height(SizeValue.px(INGREDIENT_IMAGE_SIZE + 10).toString());
        return builder.build(niftyInstance, currentScreen, ingredientsPanel);
    }

    private boolean deleteIngredientImage(@Nonnull Element ingredientsPanel, int index) {
        Element image = ingredientsPanel.findElementById("#ingredient" + Integer.toString(index));
        if (image == null) {
            return false;
        }
        niftyInstance.unsubscribe(image.getId(), mouseOverIngredientEventHandler);
        niftyInstance.removeElement(currentScreen, image);
        return true;
    }

    @Nonnull
    private Element getIngredientImage(String parentId, @Nonnull Element parentPanel, int index) {
        List<Element> elements = parentPanel.getChildren();
        if ((elements.size() - 1) >= index) {
            return elements.get(index);
        }
        if (elements.size() < index) {
            throw new InvalidParameterException("Index out of valid range");
        }

        PanelBuilder panelBuilder = new PanelBuilder(parentId + "#ingredient" + Integer.toString(index));
        panelBuilder.margin("1px");
        panelBuilder.childLayoutCenter();
        panelBuilder.width(SizeValue.px(INGREDIENT_IMAGE_SIZE + 8).toString());
        panelBuilder.height(SizeValue.px(INGREDIENT_IMAGE_SIZE + 8).toString());
        panelBuilder.style("nifty-panel-item");
        panelBuilder.visibleToMouse();
        ImageBuilder builder = new ImageBuilder();
        panelBuilder.image(builder);

        Element ingredientObject = panelBuilder.build(niftyInstance, currentScreen, parentPanel);
        niftyInstance.subscribe(currentScreen, ingredientObject.getId(), NiftyMouseMovedEvent.class,
                                mouseOverIngredientEventHandler);
        return ingredientObject;
    }

    @Override
    public boolean inputEvent(@Nonnull NiftyInputEvent inputEvent) {
        super.inputEvent(inputEvent);
        return true;
    }

    private void showIngredientAmount(@Nonnull Element ingredientElement, @Nonnull ItemCount count) {
        List<Element> elements = ingredientElement.getChildren();
        if ((elements.size() > 2) || (elements.size() < 1)) {
            throw new InvalidParameterException("Something is wrong, parent element appears to be wrong.");
        }

        if (ItemCount.isGreaterOne(count)) {
            if (elements.size() == 2) {
                Label countLabel = elements.get(1).getNiftyControl(Label.class);
                countLabel.setText(Integer.toString(count.getValue()));
            } else {
                LabelBuilder labelBuilder = new LabelBuilder();
                labelBuilder.text(Integer.toString(count.getValue()));
                labelBuilder.alignRight();
                labelBuilder.valignBottom();
                labelBuilder.marginBottom("4px");
                labelBuilder.marginRight("4px");
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

    @Nonnull
    @SuppressWarnings("unchecked")
    private TreeBox<ListEntry> getItemList() {
        return getContent().findNiftyControl("#craftItemList", TreeBox.class);
    }

    @Override
    public void addCraftingItems(@Nonnull CraftingCategoryEntry... entries) {
        addCraftingItems(Arrays.asList(entries));
    }

    @Override
    public <T extends CraftingCategoryEntry> void addCraftingItems(@Nonnull Collection<T> entries) {
        TreeBox<ListEntry> list = getItemList();

        for (T entry : entries) {
            TreeItem<ListEntry> categoryItem = new TreeItem<>(new ListEntry(entry));
            for (CraftingItemEntry itemEntry : entry.getChildren()) {
                categoryItem.addTreeItem(new TreeItem<>(new ListEntry(itemEntry)));
            }
            treeRootNode.addTreeItem(categoryItem);
        }

        list.setTree(treeRootNode);
    }

    @Override
    public void setProgress(float progress) {
        Element progressBar = getContent().findElementById("#progress");
        progressBar.stopEffect(EffectEventId.onCustom);
        progressBar.getNiftyControl(Progress.class).setProgress(progress);
    }
}
