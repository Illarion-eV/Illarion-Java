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
import illarion.common.types.ItemCount;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.*;
import org.illarion.nifty.effects.DoubleEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
 * @deprecated Use {@link DialogCrafting} to access the dialog
 */
@Deprecated
public class DialogCraftingControl extends WindowControl implements DialogCrafting,
        EventTopicSubscriber<ButtonClickedEvent> {

    /**
     * The size of the slot to show the ingredient in pixels.
     */
    private static final int INGREDIENT_IMAGE_SIZE = 32;

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
        public void onEvent(final String topic, @Nonnull final TreeItemSelectionChangedEvent<ListEntry> data) {
            final List<TreeItem<ListEntry>> selection = data.getSelection();
            if (selection.isEmpty()) {
                return;
            }

            final TreeItem<ListEntry> selectedTreeEntry = selection.get(0);
            final CraftingTreeItem selectedEntry = selectedTreeEntry.getValue().entry;

            if (selectedEntry instanceof CraftingCategoryEntry) {
                if (!selectedTreeEntry.isExpanded()) {
                    selectedTreeEntry.setExpanded(true);
                    updateTree(selectedTreeEntry);
                }
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
        super();
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
                     @Nonnull final Attributes controlDefinitionAttributes) {
        super.bind(nifty, screen, element, parameter, controlDefinitionAttributes);
        niftyInstance = nifty;
        currentScreen = screen;

        dialogId = Integer.parseInt(controlDefinitionAttributes.getWithDefault("dialogId", "-1"));

        nifty.subscribeAnnotations(this);

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
            public CharSequence getDisplaySequence(@Nonnull final CharSequence original, final int start, final int end) {
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

        niftyInstance.subscribe(currentScreen, getContent().findElementById("#craftButton").getId(),
                ButtonClickedEvent.class, craftButtonEventHandler);
        niftyInstance.subscribe(currentScreen, getContent().findElementById("#cancelButton").getId(),
                ButtonClickedEvent.class, closeButtonEventHandler);
        niftyInstance.subscribe(currentScreen, getContent().findElementById("#buttonAmountUp").getId(),
                ButtonClickedEvent.class, increaseAmountButtonEventHandler);
        niftyInstance.subscribe(currentScreen, getContent().findElementById("#buttonAmountDown").getId(),
                ButtonClickedEvent.class, decreaseAmountButtonEventHandler);
        niftyInstance.subscribe(currentScreen, getItemList().getElement().getId(),
                ListBoxSelectionChangedEvent.class, listSelectionChangedEventHandler);
        niftyInstance.subscribe(currentScreen, getContent().findElementById("#selectedItemInfos").getId(),
                NiftyMouseMovedEvent.class, mouseOverItemEventHandler);

    }

    @Override
    public void onEvent(final String topic, final ButtonClickedEvent data) {
        niftyInstance.publishEvent(getId(), new DialogMessageConfirmedEvent(dialogId));
        closeWindow();
    }

    @Nullable
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
            setSelectedItem(null);
            return;
        }

        if (selectedEntry.getParentItem() != null) {
            selectedEntry.getParentItem().setExpanded(true);
        }

        updateTree(selectedEntry);
        setSelectedItem((CraftingItemEntry) selectedEntry.getValue().entry);
    }

    private void updateTree(@Nullable final TreeItem<ListEntry> selectedItem) {
        final TreeBox<ListEntry> tree = getItemList();
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
    public Element getIngredientItemDisplay(final int index) {
        final Element ingredientsPanel = getElement().findElementById("#ingredients");
        return ingredientsPanel.findElementById("#ingredient" + Integer.toString(index));
    }

    /**
     * Get the text field that takes care for showing the amount of items that get produced at once.
     *
     * @return the amount textfield
     */
    public TextField getAmountTextField() {
        return getElement().findNiftyControl("#amountInput", TextField.class);
    }

    @Override
    public int getAmount() {
        return Integer.parseInt(getAmountTextField().getDisplayedText());
    }

    @Override
    public void setAmount(final int amount) {
        if (amount <= 1) {
            getAmountTextField().setText("");
        } else {
            getAmountTextField().setText(Integer.toString(amount));
        }
    }

    private static final class ListEntry {
        private final CraftingTreeItem entry;

        ListEntry(final CraftingTreeItem entry) {
            this.entry = entry;
        }

        @Override
        @Nonnull
        public String toString() {
            return entry.getTreeLabel();
        }
    }

    private static void applyImage(@Nonnull final Element element, @Nonnull final NiftyImage image, final int maxSize) {
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

    @Override
    public void startProgress(final double seconds) {
        final Element progressBar = getContent().findElementById("#progress");
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

    @Override
    public void setDialogId(final int id) {
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
    private void setSelectedItem(@Nullable final CraftingItemEntry selectedEntry) {
        if (selectedEntry == null) {
            final Element image = getContent().findElementById("#selectedItemImage");
            image.getRenderer(ImageRenderer.class).setImage(null);

            final Label imageAmount = getContent().findNiftyControl("#selectedItemAmount", Label.class);
            imageAmount.getElement().hide();

            final Element ingredientsPanel = getContent().findElementById("#ingredients");

            int index = 0;
            while (deleteIngredientImage(ingredientsPanel, index)) {
                index++;
            }
            return;
        }
        final Element image = getContent().findElementById("#selectedItemImage");
        applyImage(image, selectedEntry.getImage(), 56);

        final Label imageAmount = getContent().findNiftyControl("#selectedItemAmount", Label.class);
        if (ItemCount.isGreaterOne(selectedEntry.getBuildStackSize())) {
            imageAmount.getElement().hide();
        } else {
            final Element imageAmountElement = imageAmount.getElement();
            final TextRenderer textRenderer = imageAmountElement.getRenderer(TextRenderer.class);
            textRenderer.setText(Integer.toString(selectedEntry.getBuildStackSize().getValue()));
            imageAmountElement.setConstraintWidth(SizeValue.px(textRenderer.getTextWidth()));
            imageAmountElement.setConstraintHorizontalAlign(HorizontalAlign.right);
            imageAmountElement.show();
        }

        final Element title = getContent().findElementById("#selectedItemName");
        title.getRenderer(TextRenderer.class).setText(selectedEntry.getName());

        final Element productionTime = getContent().findElementById("#productionTime");
        productionTime.getRenderer(TextRenderer.class).setText("${illarion-dialog-crafting-bundle.craftTime}: "
                + timeFormat.format(selectedEntry.getCraftTime()) + "s");

        final Element ingredientsPanel = getContent().findElementById("#ingredients");

        final int ingredientsAmount = selectedEntry.getIngredientCount();
        Element currentPanel = null;
        for (int i = 0; i < ingredientsAmount; i++) {
            if ((i % 10) == 0) {
                currentPanel = getIngredientPanel(ingredientsPanel, i / 10);
            }

            assert currentPanel != null;
            final Element currentImage = getIngredientImage(ingredientsPanel.getId(), currentPanel, i % 10);
            applyImage(currentImage.getElements().get(0), selectedEntry.getIngredientImage(i), INGREDIENT_IMAGE_SIZE);
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

    private boolean deleteIngredientPanel(@Nonnull final Element ingredientsPanel, final int index) {
        final List<Element> elements = ingredientsPanel.getElements();
        if ((elements.size() - 1) >= index) {
            niftyInstance.removeElement(currentScreen, elements.get(index));
            return true;
        }
        return false;
    }

    @Nonnull
    private Element getIngredientPanel(@Nonnull final Element ingredientsPanel, final int index) {
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
        builder.height(SizeValue.px(INGREDIENT_IMAGE_SIZE + 10).toString());
        return builder.build(niftyInstance, currentScreen, ingredientsPanel);
    }

    private boolean deleteIngredientImage(@Nonnull final Element ingredientsPanel, final int index) {
        final Element image = ingredientsPanel.findElementById("#ingredient" + Integer.toString(index));
        if (image == null) {
            return false;
        }
        niftyInstance.unsubscribe(image.getId(), mouseOverIngredientEventHandler);
        niftyInstance.removeElement(currentScreen, image);
        return true;
    }

    @Nonnull
    private Element getIngredientImage(final String parentId, @Nonnull final Element parentPanel, final int index) {
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
        panelBuilder.width(SizeValue.px(INGREDIENT_IMAGE_SIZE + 8).toString());
        panelBuilder.height(SizeValue.px(INGREDIENT_IMAGE_SIZE + 8).toString());
        panelBuilder.style("nifty-panel-item");
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

    private void showIngredientAmount(@Nonnull final Element ingredientElement, @Nonnull final ItemCount count) {
        final List<Element> elements = ingredientElement.getElements();
        if ((elements.size() > 2) || (elements.size() < 1)) {
            throw new InvalidParameterException("Something is wrong, parent element appears to be wrong.");
        }

        if (ItemCount.isGreaterOne(count)) {
            if (elements.size() == 2) {
                final Label countLabel = elements.get(1).getNiftyControl(Label.class);
                countLabel.setText(Integer.toString(count.getValue()));
            } else {
                final LabelBuilder labelBuilder = new LabelBuilder();
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
    public void addCraftingItems(@Nonnull final CraftingCategoryEntry... entries) {
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
        final Element progressBar = getContent().findElementById("#progress");
        progressBar.stopEffect(EffectEventId.onCustom);
        progressBar.getNiftyControl(Progress.class).setProgress(progress);
    }
}
