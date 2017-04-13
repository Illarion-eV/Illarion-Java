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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.graphics.FontLoader;
import illarion.client.gui.*;
import illarion.client.gui.controller.game.NumberSelectPopupHandler.Callback;
import illarion.client.gui.events.TooltipsRemovedEvent;
import illarion.client.gui.util.NiftyCraftingCategory;
import illarion.client.gui.util.NiftyCraftingItem;
import illarion.client.gui.util.NiftyMerchantItem;
import illarion.client.gui.util.NiftySelectItem;
import illarion.client.net.client.*;
import illarion.client.util.Lang;
import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.client.world.items.CraftingItem;
import illarion.client.world.items.MerchantItem;
import illarion.client.world.items.MerchantItem.MerchantItemType;
import illarion.client.world.items.MerchantList;
import illarion.client.world.items.SelectionItem;
import illarion.common.types.CharacterId;
import illarion.common.types.ItemCount;
import illarion.common.types.ItemId;
import illarion.common.types.Rectangle;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Font;
import org.illarion.engine.input.Button;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;
import org.illarion.nifty.controls.*;
import org.illarion.nifty.controls.DialogInput.DialogButton;
import org.illarion.nifty.controls.dialog.input.DialogCharacterControl;
import org.illarion.nifty.controls.dialog.input.builder.DialogCharacterBuilder;
import org.illarion.nifty.controls.dialog.input.builder.DialogInputBuilder;
import org.illarion.nifty.controls.dialog.message.builder.DialogMessageBuilder;
import org.illarion.nifty.controls.dialog.select.builder.DialogSelectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the dialog handler that takes care for receiving events to show dialogs. It opens and maintains those
 * dialogs and notifies the server in case a dialog is closed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogHandler
        implements DialogGui, DialogCraftingGui, DialogMerchantGui, DialogMessageGui, DialogInputGui,
        DialogSelectionGui, ScreenController, UpdatableHandler {

    private static class BuildWrapper {
        @Nonnull
        private final ControlBuilder builder;
        @Nonnull
        private final Element parent;
        @Nullable
        private final PostBuildTask task;

        BuildWrapper(@Nonnull ControlBuilder builder, @Nonnull Element parent, @Nullable PostBuildTask task) {
            this.builder = builder;
            this.parent = parent;
            this.task = task;
        }

        public void executeTask(@Nonnull Element createdElement) {
            if (task != null) {
                task.run(createdElement);
            }
        }

        @Nonnull
        public ControlBuilder getBuilder() {
            return builder;
        }

        @Nonnull
        public Element getParent() {
            return parent;
        }
    }

    private static class DialogCloseData {
        @Nonnull
        public final Set<DialogType> types;
        public final int dialogId;

        public DialogCloseData(int id, @Nonnull Collection<DialogType> types) {
            this.types = EnumSet.copyOf(types);
            dialogId = id;
        }
    }

    private interface PostBuildTask {
        void run(@Nonnull Element createdElement);
    }

    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(DialogHandler.class);

    @Nonnull
    private static final Pattern dialogNamePattern = Pattern.compile("([a-z]+)Dialog([0-9]+)");
    /**
     * The input control that is used in this dialog handler.
     */
    private final Input input;

    @Nullable
    private DialogMerchant merchantDialog;
    @Nullable
    private DialogCrafting craftingDialog;
    private boolean openCraftDialog;
    private boolean craftingInProgress;

    @Nonnull
    private final Queue<BuildWrapper> builders;
    @Nonnull
    private final Queue<DialogCloseData> closers;
    private Nifty nifty;
    private Screen screen;
    private final NumberSelectPopupHandler numberSelect;
    private final TooltipHandler tooltipHandler;

    private int lastCraftingTooltip = -2;
    @Nullable
    private MerchantListEntry lastMerchantTooltipItem;

    public DialogHandler(
            Input input, NumberSelectPopupHandler numberSelectPopupHandler, TooltipHandler tooltipHandler) {
        this.input = input;
        this.tooltipHandler = tooltipHandler;
        builders = new ConcurrentLinkedQueue<>();
        closers = new ConcurrentLinkedQueue<>();
        numberSelect = numberSelectPopupHandler;
    }

    private void addCraftingItemsToDialog(@Nonnull Iterable<String> groups, @Nonnull Iterable<CraftingItem> items,
                                          @Nonnull DialogCrafting dialog) {
        if (nifty == null) {
            throw new IllegalStateException("Dialog handler is not bound yet.");
        }

        List<NiftyCraftingCategory> categories = new ArrayList<>();
        for (@Nonnull String group : groups) {
            categories.add(new NiftyCraftingCategory(group));
        }

        NiftyCraftingCategory unknownCat = null;

        for (CraftingItem item : items) {
            int groupId = item.getGroup();
            if ((groupId < 0) || (groupId >= categories.size())) {
                if (unknownCat == null) {
                    unknownCat = new NiftyCraftingCategory("not assigned");
                }
                unknownCat.addChild(new NiftyCraftingItem(nifty, item));
                log.warn("Crafting item with illegal group received: {}", Integer.toString(groupId));
            } else {
                NiftyCraftingCategory category = categories.get(item.getGroup());
                assert category != null;
                category.addChild(new NiftyCraftingItem(nifty, item));
            }
        }

        dialog.addCraftingItems(categories);
        if (unknownCat != null) {
            dialog.addCraftingItems(unknownCat);
        }
    }

    @Override
    public void showSelectionDialog(int dialogId, @Nonnull String title, @Nonnull String content,
                                    @Nonnull Collection<SelectionItem> items) {
        World.getUpdateTaskManager().addTask((container, delta) ->
                showSelectionDialogImpl(dialogId, title, content, items));
    }

    private void showSelectionDialogImpl(int dialogId, @Nonnull String title, @Nonnull String content,
                                         @Nonnull Collection<SelectionItem> items) {
        if (screen == null) {
            throw new IllegalStateException("UI is not ready yet.");
        }
        Element parentArea = screen.findElementById("windows");
        if (parentArea == null) {
            throw new IllegalStateException("UI is corrupted. Windows panel not found.");
        }
        DialogSelectBuilder builder = new DialogSelectBuilder("selectDialog" + dialogId, title);
        builder.dialogId(dialogId);
        builder.message(content);

        int selectedWidth = 0;
        boolean useImages = false;
        Font textFont = FontLoader.getInstance().getFont(FontLoader.TEXT_FONT);
        for (@Nonnull SelectionItem item : items) {
            useImages = useImages || ItemId.isValidItem(item.getId());
            selectedWidth = Math.max(selectedWidth, textFont.getWidth(item.getName()));
        }
        if (useImages) {
            selectedWidth += 79; // width of the image container area
        }
        selectedWidth += 2;  // padding of entry
        selectedWidth += 26; // padding of list box and window
        if (items.size() > 6) {
            selectedWidth += 16; // space for the scroll bar
        }
        selectedWidth += 10; // padding to make it look good (some space on the right side of the text entries)
        selectedWidth += 20; // magical additional width of unknown origin (determined by testing)

        selectedWidth = Math.max(selectedWidth, 270); // width required to display the buttons properly

        builder.width(SizeValue.px(selectedWidth));
        builder.itemCount(Math.min(6, items.size()));
        builders.add(new BuildWrapper(builder, parentArea, createdElement -> {
            DialogSelect dialog = createdElement.getNiftyControl(DialogSelect.class);
            if (dialog == null) {
                log.warn("Newly created dialog was NULL");
            } else {
                if (nifty == null) {
                    throw new IllegalStateException("UI is not ready yet.");
                }
                for (@Nonnull SelectionItem item : items) {
                    dialog.addItem(new NiftySelectItem(nifty, item));
                }
            }
        }));
    }

    @EventSubscriber
    public void handleTooltipRemovedEvent(TooltipsRemovedEvent event) {
        lastCraftingTooltip = -2;
        lastMerchantTooltipItem = null;

        if (input.isAnyButtonDown(Button.Left, Button.Right)) {
            return;
        }
    }

    @NiftyEventSubscriber(id = "craftingDialog")
    public void handleCraftingCloseDialogEvent(String topic, @Nonnull DialogCraftingCloseEvent event) {
        closeCraftingDialog(event.getDialogId());
    }

    private void closeCraftingDialog(int id) {
        if (!openCraftDialog) {
            return;
        }
        World.getNet().sendCommand(new CloseDialogCraftingCmd(id));
        World.getGameGui().getDialogGui().closeDialog(id, EnumSet.of(DialogType.Crafting));
        openCraftDialog = false;
    }

    @NiftyEventSubscriber(id = "craftingDialog")
    public void handleCraftingCraftItemEvent(String topic, @Nonnull DialogCraftingCraftEvent event) {
        World.getNet()
                .sendCommand(new CraftItemCmd(event.getDialogId(), event.getItem().getItemIndex(), event.getCount()));
    }

    @NiftyEventSubscriber(id = "craftingDialog")
    public void handleCraftingIngredientLookAtEvent(
            String topic, @Nonnull DialogCraftingLookAtIngredientItemEvent event) {
        if (lastCraftingTooltip == event.getIngredientIndex()) {
            return;
        }

        if (input.isAnyButtonDown(Button.Left, Button.Right)) {
            return;
        }

        World.getNet().sendCommand(new LookAtCraftIngredientCmd(event.getDialogId(), event.getItem().getItemIndex(),
                event.getIngredientIndex()));
        lastCraftingTooltip = event.getIngredientIndex();
    }

    @NiftyEventSubscriber(id = "craftingDialog")
    public void handleCraftingItemLookAtEvent(String topic, @Nonnull DialogCraftingLookAtItemEvent event) {
        if (lastCraftingTooltip == -1) {
            return;
        }

        if (input.isAnyButtonDown(Button.Left, Button.Right)) {
            return;
        }

        World.getNet().sendCommand(new LookAtCraftItemCmd(event.getDialogId(), event.getItem().getItemIndex()));
        lastCraftingTooltip = -1;
    }

    @NiftyEventSubscriber(id = "merchantDialog")
    public void handleMerchantLookAtEvent(String topic, @Nonnull DialogMerchantLookAtEvent event) {
        if (input.isAnyButtonDown(Button.Left, Button.Right)) {
            return;
        }

        if (Objects.equals(lastMerchantTooltipItem, event.getItem())) {
            return;
        }

        byte listId = -1;
        switch (event.getItem().getEntryType()) {
            case Selling:
                listId = LookAtMerchantItemCmd.LIST_ID_SELL;
                break;
            case BuyPrimary:
                listId = LookAtMerchantItemCmd.LIST_ID_BUY_PRIMARY;
                break;
            case BuySecondary:
                listId = LookAtMerchantItemCmd.LIST_ID_BUY_SECONDARY;
                break;
        }

        int dialogId = event.getDialogId();
        int itemIndex = event.getItem().getIndex();

        lastMerchantTooltipItem = event.getItem();

        LookAtMerchantItemCmd cmd = new LookAtMerchantItemCmd(dialogId, listId, itemIndex);

        World.getNet().sendCommand(cmd);
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;

        merchantDialog = this.screen.findNiftyControl("merchantDialog", DialogMerchant.class);
        craftingDialog = this.screen.findNiftyControl("craftingDialog", DialogCrafting.class);
    }

    @Override
    public void onEndScreen() {
        AnnotationProcessor.unprocess(this);
        nifty.unsubscribeAnnotations(this);

        closeDialogImpl(ALL_DIALOGS, EnumSet.allOf(DialogType.class));
    }

    @Override
    public void onStartScreen() {
        AnnotationProcessor.process(this);
        nifty.subscribeAnnotations(this);
    }

    @Override
    public void showMerchantListTooltip(int dialogId, int list, int itemIndex, @Nonnull Tooltip tooltip) {
        if ((merchantDialog == null) || (merchantDialog.getDialogId() != dialogId)) {
            return;
        }

        MerchantList merchantList = World.getPlayer().getMerchantList();
        if (merchantList == null) {
            log.warn("Received tooltip for merchant dialog {} but there is no merchant list.", dialogId);
            return;
        }
        MerchantItemType expectedItemType;
        switch (list) {
            case 0:
                expectedItemType = MerchantItemType.SellingItem;
                break;
            case 1:
                expectedItemType = MerchantItemType.BuyingPrimaryItem;
                break;
            case 2:
                expectedItemType = MerchantItemType.BuyingSecondaryItem;
                break;
            default:
                log.warn("Received merchant item look-at for unexpected list: {}", list);
                return;
        }

        MerchantItem selectedItem = merchantList.getItem(expectedItemType, itemIndex);
        if (selectedItem == null) {
            log.warn("Failed to located item index {} in merchant list {} ({}) for dialog {}.", itemIndex,
                     expectedItemType, list, dialogId);
            return;
        }
        Rectangle renderArea = merchantDialog.getRenderAreaForEntry(new NiftyMerchantItem(nifty, selectedItem));
        if (!renderArea.isEmpty()) {
            tooltipHandler.showToolTip(renderArea, tooltip);
        }
    }

    @Override
    public void showMerchantDialog(int dialogId, @Nonnull String title,
                                   @Nonnull Collection<MerchantItem> items) {
        World.getUpdateTaskManager().addTask((container, delta) -> showMerchantDialogImpl(dialogId, title, items));
    }

    private void showMerchantDialogImpl(int dialogId, @Nonnull String title, @Nonnull Iterable<MerchantItem> items) {
        if ((merchantDialog == null) || (nifty == null)) {
            log.error("Can't show the merchant dialog. Binding is now done.");
            return;
        }

        merchantDialog.clearItems();
        merchantDialog.setDialogId(dialogId);
        merchantDialog.setTitle(title);
        Collection<MerchantListEntry> sellingList = new ArrayList<>();
        Collection<MerchantListEntry> buyingList = new ArrayList<>();
        for (MerchantItem item : items) {
            NiftyMerchantItem niftyItem = new NiftyMerchantItem(nifty, item);

            switch (niftyItem.getType()) {
                case SellingItem:
                    sellingList.add(niftyItem);
                    break;
                case BuyingPrimaryItem:
                case BuyingSecondaryItem:
                    buyingList.add(niftyItem);
                    break;
            }
        }
        merchantDialog.addAllSellingItems(sellingList);
        merchantDialog.addAllBuyingItems(buyingList);

        Element element = merchantDialog.getElement();
        if (element != null) {
            if (element.isVisible()) {
                merchantDialog.moveToFront();
            } else {
                element.show(merchantDialog::moveToFront);
            }
        }
    }

    @Override
    public void showCraftIngredientTooltip(
            int dialogId, int index, int ingredientIndex, @Nonnull Tooltip tooltip) {
        if ((craftingDialog == null) || (craftingDialog.getDialogId() != dialogId)) {
            return;
        }

        CraftingItemEntry selectedEntry = craftingDialog.getSelectedCraftingItem();
        if ((selectedEntry != null) && (selectedEntry.getItemIndex() == index)) {
            Element targetElement = craftingDialog.getIngredientItemDisplay(ingredientIndex);
            Rectangle elementRectangle = new Rectangle();
            elementRectangle.set(targetElement.getX(), targetElement.getY(), targetElement.getWidth(),
                                 targetElement.getHeight());
            tooltipHandler.showToolTip(elementRectangle, tooltip);
        }
    }

    @Override
    public void showCraftItemTooltip(int dialogId, int index, @Nonnull Tooltip tooltip) {
        if ((craftingDialog == null) || (craftingDialog.getDialogId() != dialogId)) {
            return;
        }

        CraftingItemEntry selectedEntry = craftingDialog.getSelectedCraftingItem();
        if ((selectedEntry != null) && (selectedEntry.getItemIndex() == index)) {
            Element targetElement = craftingDialog.getCraftingItemDisplay();
            Rectangle elementRectangle = new Rectangle();
            elementRectangle.set(targetElement.getX(), targetElement.getY(), targetElement.getWidth(),
                                 targetElement.getHeight());
            tooltipHandler.showToolTip(elementRectangle, tooltip);
        }
    }

    private void showCraftingDialogImpl(int dialogId, @Nonnull String title,
                                        @Nonnull Iterable<String> groups,
                                        @Nonnull Iterable<CraftingItem> items) {
        if (craftingDialog == null) {
            throw new IllegalStateException("Instance of crafting dialog not found. Binding not done?");
        }

        if ((dialogId == craftingDialog.getDialogId()) && openCraftDialog) {
            CraftingItemEntry selectedItem = craftingDialog.getSelectedCraftingItem();

            int selectedIndex = (selectedItem != null) ? selectedItem.getItemIndex() : 0;

            craftingDialog.clearItemList();
            addCraftingItemsToDialog(groups, items, craftingDialog);

            if (selectedItem != null) {
                craftingDialog.selectItemByItemIndex(selectedIndex);
            }
        } else {
            craftingDialog.setDialogId(dialogId);
            craftingDialog.setTitle(title);
            craftingDialog.clearItemList();
            addCraftingItemsToDialog(groups, items, craftingDialog);
            craftingDialog.setProgress(0.f);
            craftingDialog.selectItemByItemIndex(0);
            craftingDialog.setAmount(1);

            Element craftingDialogElement = craftingDialog.getElement();
            if (craftingDialogElement != null) {
                craftingDialogElement.show(() -> {
                    assert craftingDialog != null;
                    craftingDialog.moveToFront();
                    craftingDialog.selectItemByItemIndex(0);
                });
            }
            openCraftDialog = true;
        }
    }

    @Override
    public void showCraftingDialog(int dialogId, @Nonnull String title, @Nonnull Collection<String> groups,
                                   @Nonnull Collection<CraftingItem> items) {
        World.getUpdateTaskManager().addTask((container, delta) -> showCraftingDialogImpl(dialogId, title, groups, items));
    }

    @Override
    public void startProductionIndicator(int dialogId, int remainingItemCount,
                                         double requiredTime) {
        World.getUpdateTaskManager().addTask((container, delta) -> {
            if ((craftingDialog != null) && openCraftDialog && (craftingDialog.getDialogId() == dialogId)) {
                craftingDialog.setAmount(remainingItemCount);
                craftingDialog.startProgress(requiredTime);
                craftingInProgress = true;
            }
        });
    }

    @Override
    public void finishProduction(int dialogId) {
        World.getUpdateTaskManager().addTask((container, delta) -> {
            if ((craftingDialog != null) && openCraftDialog && (craftingDialog.getDialogId() == dialogId)) {
                craftingDialog.setAmount(craftingDialog.getAmount() - 1);
                craftingDialog.setProgress(0.f);
                craftingInProgress = false;
            }
        });
    }

    @Override
    public void abortProduction(int dialogId) {
        World.getUpdateTaskManager().addTask((container, delta) -> {
            if ((craftingDialog != null) && openCraftDialog && (craftingDialog.getDialogId() == dialogId)) {
                craftingDialog.setProgress(0.f);
                craftingInProgress = false;
            }
        });
    }

    @Override
    public boolean isCraftingInProgress() {
        return craftingInProgress;
    }

    @Override
    public void showInputDialog(
            int dialogId, @Nonnull String title, @Nonnull String message, int maxLength, boolean multiLine) {
        Element parentArea = screen.findElementById("windows");
        DialogInputBuilder builder = new DialogInputBuilder("inputDialog" + Integer.toString(dialogId), title);
        builder.description(message);
        builder.buttonLeft("OK");
        builder.buttonRight("Cancel");
        builder.dialogId(dialogId);
        builder.maxLength(maxLength);
        if (multiLine) {
            builder.style("illarion-dialog-input-multi");
        } else {
            builder.style("illarion-dialog-input-single");
        }
        builders.add(new BuildWrapper(builder, parentArea, createdElement -> {
            DialogInput control = createdElement.getNiftyControl(DialogInput.class);
            if (control != null) {
                control.setFocus();
            }
        }));
    }

    @Override
    public void showCharacterDialog(@Nonnull CharacterId charId, String lookAt) {
        Element parentArea = screen.findElementById("windows");
        Char chara = World.getPeople().getCharacter(charId);

        if (chara == null || !chara.isHuman() || World.getPlayer().isPlayer(charId)) {
            log.warn("Tried to open a character dialog on an invalid character.");
            return;
        }
        String currentCustomName = chara.getCustomName();
        String dialogName = "characterDialog" + Long.toString(charId.getValue());
        if (parentArea.findElementById(dialogName) != null) {
            return;
        }
        DialogCharacterBuilder builder = new DialogCharacterBuilder(dialogName, Lang.getMsg("gui.dialog.character.title"));
        builder.description(String.format(Lang.getMsg("gui.dialog.character.description"), chara.getName()));
        builder.buttonLeft(Lang.getMsg("gui.dialog.character.ok"));
        builder.buttonRight(Lang.getMsg("gui.dialog.character.cancel"));
        builder.dialogId(charId.getAsInteger());
        builder.lookAt(lookAt);
        builder.maxLength(255);
        builder.initalText((currentCustomName == null) ? "" : currentCustomName);

        builder.style("illarion-dialog-character");
        log.debug("Built Character dialog: " + builder.toString());
        builders.add(new BuildWrapper(builder, parentArea, createdElement -> {
            DialogCharacterControl control = createdElement.getNiftyControl(DialogCharacterControl.class);
            if (control != null) {
                control.setFocus();
            }
        }));
    }

    @Override
    public void showMessageDialog(int dialogId, @Nonnull String title, @Nonnull String message) {
        Element parentArea = screen.findElementById("windows");
        DialogMessageBuilder builder = new DialogMessageBuilder("msgDialog" + Integer.toString(dialogId), title);
        builder.text(message);
        builder.button("OK");
        builder.dialogId(dialogId);
        builders.add(new BuildWrapper(builder, parentArea, null));
    }

    @Override
    public void update(@Nonnull GameContainer container, int delta) {
        while (true) {
            BuildWrapper wrapper = builders.poll();
            if (wrapper == null) {
                break;
            }

            Element element = wrapper.getBuilder().build(nifty, screen, wrapper.getParent());

            wrapper.executeTask(element);

            element.layoutElements();
            element.setConstraintX(SizeValue.px((wrapper.getParent().getWidth() - element.getWidth()) / 2));
            element.setConstraintY(SizeValue.px((wrapper.getParent().getHeight() - element.getHeight()) / 2));
            wrapper.getParent().layoutElements();
        }

        while (true) {
            DialogCloseData closeEvent = closers.poll();
            if (closeEvent == null) {
                break;
            }
            closeDialogImpl(closeEvent.dialogId, closeEvent.types);
        }
    }

    @Nullable
    @Override
    public DialogType getDialogType(int dialogId) {
        return getDialogType(dialogId, EnumSet.allOf(DialogType.class));
    }

    @Nullable
    @Override
    public DialogType getDialogType(int dialogId, @Nonnull DialogType firstType, @Nonnull DialogType... moreTypes) {
        return getDialogType(dialogId, EnumSet.of(firstType, moreTypes));
    }

    @Override
    public void closeDialog(int dialogId, @Nonnull Collection<DialogType> dialogTypes) {
        closers.add(new DialogCloseData(dialogId, dialogTypes));
    }

    @Nullable
    public DialogType getDialogType(int dialogId, @Nonnull Collection<DialogType> types) {
        if (types.contains(DialogType.Merchant)) {
            if (merchantDialog != null) {
                if (dialogId == merchantDialog.getDialogId()) {
                    return DialogType.Merchant;
                }
            }
        }

        if (types.contains(DialogType.Crafting)) {
            if (craftingDialog != null) {
                if (dialogId == craftingDialog.getDialogId()) {
                    return DialogType.Crafting;
                }
            }
        }

        Element parentArea = screen.findElementById("windows");
        if (parentArea == null) {
            return null;
        }

        for (Element child : parentArea.getChildren()) {
            String childId = child.getId();
            if (childId == null) {
                continue;
            }
            Matcher matcher = dialogNamePattern.matcher(childId);

            if (!matcher.find()) {
                continue;
            }

            try {
                String type = matcher.group(1);
                int id = Integer.parseInt(matcher.group(2));

                if (id == dialogId) {
                    if ("msg".equals(type) && types.contains(DialogType.Message)) {
                        return DialogType.Message;
                    }
                    if ("input".equals(type) && types.contains(DialogType.Input)) {
                        return DialogType.Input;
                    }
                    if ("select".equals(type) && types.contains(DialogType.Selection)) {
                        return DialogType.Selection;
                    }
                }
            } catch (@Nonnull NumberFormatException ignored) {
                // nothing
            }
        }
        return null;
    }

    private void closeDialogImpl(int dialogId, @Nonnull Collection<DialogType> dialogTypes) {
        if (screen == null) {
            return;
        }
        Element parentArea = screen.findElementById("windows");
        if (parentArea == null) {
            return;
        }

        if (dialogTypes.contains(DialogType.Merchant)) {
            if ((merchantDialog != null) && merchantDialog.getElement().isVisible()) {
                if ((dialogId == ALL_DIALOGS) || (dialogId == merchantDialog.getDialogId())) {
                    merchantDialog.closeWindow();
                }
            }
        }
        if (dialogTypes.contains(DialogType.Crafting)) {
            if ((craftingDialog != null) && craftingDialog.getElement().isVisible()) {
                if ((dialogId == ALL_DIALOGS) || (dialogId == craftingDialog.getDialogId())) {
                    craftingDialog.closeWindow();
                }
            }
        }

        for (Element child : parentArea.getChildren()) {
            String childId = child.getId();
            if (childId == null) {
                continue;
            }
            Matcher matcher = dialogNamePattern.matcher(childId);

            if (!matcher.find()) {
                continue;
            }

            try {
                String type = matcher.group(1);
                int id = Integer.parseInt(matcher.group(2));

                if ((dialogId == ALL_DIALOGS) || (id == dialogId)) {
                    if ("msg".equals(type) && dialogTypes.contains(DialogType.Message)) {
                        child.hide(child::markForRemoval);
                    }
                    if ("input".equals(type) && dialogTypes.contains(DialogType.Input)) {
                        child.hide(child::markForRemoval);
                    }
                    if ("select".equals(type) && dialogTypes.contains(DialogType.Selection)) {
                        child.hide(child::markForRemoval);
                    }
                }
            } catch (@Nonnull NumberFormatException ignored) {
                // nothing
            }
        }
    }

    @NiftyEventSubscriber(id = "merchantDialog")
    public void handleMerchantBuyEvent(String topic, @Nonnull DialogMerchantBuyEvent event) {
        MerchantList list = World.getPlayer().getMerchantList();

        if (list == null) {
            log.error("Buying event received, but there is not merchant list.");
            return;
        }

        int index = event.getItem().getIndex();
        if (ItemCount.isGreaterOne(list.getItem(index).getBundleSize())) {
            list.buyItem(index);
        } else {
            if (input.isAnyKeyDown(Key.LeftShift, Key.RightShift)) {
                numberSelect.requestNewPopup(1, 250, new Callback() {
                    @Override
                    public void popupCanceled() {
                        // nothing
                    }

                    @Override
                    public void popupConfirmed(int value) {
                        list.buyItem(index, ItemCount.getInstance(value));
                    }
                });
            } else {
                list.buyItem(index);
            }
        }
    }

    @NiftyEventSubscriber(id = "merchantDialog")
    public void handleMerchantCloseEvent(String topic, DialogMerchantCloseEvent event) {
        MerchantList list = World.getPlayer().getMerchantList();
        if (list == null) {
            log.error("Close merchant list received, but there is not opened merchant list.");
        } else {
            list.closeDialog();
        }
        lastMerchantTooltipItem = null;
    }



    @NiftyEventSubscriber(pattern = "inputDialog[0-9]+")
    public void handleInputConfirmedEvent(String topic, @Nonnull DialogInputConfirmedEvent event) {
        if (event.getPressedButton() == DialogButton.LeftButton) {
            World.getNet().sendCommand(new CloseDialogInputCmd(event.getDialogId(), event.getText(), true));
        } else {
            World.getNet().sendCommand(new CloseDialogInputCmd(event.getDialogId(), "", false));
        }
    }

    @NiftyEventSubscriber(pattern = "characterDialog[-0-9]+")
    public void handleNamingConfirmedEvent(String topic, @Nonnull DialogInputConfirmedEvent event) {
        if (event.getPressedButton() == DialogButton.LeftButton) {
            CharacterId id = new CharacterId(event.getDialogId());
            String newName = event.getText();
            World.getNet().sendCommand(new NamePlayerCmd(id, newName));
            Char character = World.getPeople().getCharacter(id);
            if (character != null) {
                character.setCustomName(newName);
            }
        }
    }

    @NiftyEventSubscriber(pattern = "msgDialog[0-9]+")
    public void handleMessageConfirmedEvent(String topic, @Nonnull DialogMessageConfirmedEvent event) {
        World.getNet().sendCommand(new CloseDialogMessageCmd(event.getDialogId()));
    }

    @NiftyEventSubscriber(pattern = "selectDialog[0-9]+")
    public void handleSelectionCancelEvent(String topic, @Nonnull DialogSelectCancelEvent event) {
        World.getNet().sendCommand(new CloseDialogSelectionCmd(event.getDialogId(), 0, false));
        World.getGameGui().getDialogGui().closeDialog(event.getDialogId(), EnumSet.of(DialogType.Selection));
    }

    @NiftyEventSubscriber(pattern = "selectDialog[0-9]+")
    public void handleSelectionSelectEvent(String topic, @Nonnull DialogSelectSelectEvent event) {
        World.getNet().sendCommand(new CloseDialogSelectionCmd(event.getDialogId(), event.getItemIndex(), true));
        World.getGameGui().getDialogGui().closeDialog(event.getDialogId(), EnumSet.of(DialogType.Selection));
    }
}
