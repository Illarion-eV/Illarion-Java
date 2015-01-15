/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.graphics.FontLoader;
import illarion.client.gui.*;
import illarion.client.gui.events.TooltipsRemovedEvent;
import illarion.client.gui.util.NiftyCraftingCategory;
import illarion.client.gui.util.NiftyCraftingItem;
import illarion.client.gui.util.NiftyMerchantItem;
import illarion.client.gui.util.NiftySelectItem;
import illarion.client.net.client.*;
import illarion.client.net.server.events.*;
import illarion.client.util.GlobalExecutorService;
import illarion.client.util.Lang;
import illarion.client.util.UpdateTask;
import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.client.world.events.CloseDialogEvent;
import illarion.client.world.items.CraftingItem;
import illarion.client.world.items.MerchantItem;
import illarion.client.world.items.MerchantList;
import illarion.client.world.items.SelectionItem;
import illarion.common.types.CharacterId;
import illarion.common.types.ItemCount;
import illarion.common.types.Rectangle;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Font;
import org.illarion.engine.input.Button;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.Key;
import org.illarion.nifty.controls.*;
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
        implements DialogGui, DialogCraftingGui, DialogMerchantGui, DialogMessageGui, DialogInputGui, ScreenController,
        UpdatableHandler {

    private static class BuildWrapper {
        private final ControlBuilder builder;
        private final Element parent;

        @Nullable
        private final PostBuildTask task;

        BuildWrapper(
                ControlBuilder builder, Element parent, @Nullable PostBuildTask task) {
            this.builder = builder;
            this.parent = parent;
            this.task = task;
        }

        public void executeTask(Element createdElement) {
            if (task != null) {
                task.run(createdElement);
            }
        }

        public ControlBuilder getBuilder() {
            return builder;
        }

        public Element getParent() {
            return parent;
        }
    }

    private interface PostBuildTask {
        void run(Element createdElement);
    }

    private static final Logger log = LoggerFactory.getLogger(DialogHandler.class);

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

    @Nonnull
    private final Queue<BuildWrapper> builders;
    @Nonnull
    private final Queue<CloseDialogEvent> closers;
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

    @EventSubscriber
    public void handleCloseDialogEvent(CloseDialogEvent event) {
        closers.offer(event);
    }

    @EventSubscriber
    public void handleCraftingDialogEvent(@Nonnull final DialogCraftingReceivedEvent event) {
        GlobalExecutorService.getService().submit(new Runnable() {
            @Override
            public void run() {
                showCraftingDialog(event);
            }
        });
    }

    private void showCraftingDialog(@Nonnull final DialogCraftingReceivedEvent event) {
        if ((event.getRequestId() == craftingDialog.getDialogId()) && openCraftDialog) {
            World.getUpdateTaskManager().addTask(new UpdateTask() {
                @Override
                public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                    CraftingItemEntry selectedItem = craftingDialog.getSelectedCraftingItem();

                    int selectedIndex;
                    if (selectedItem != null) {
                        selectedIndex = selectedItem.getItemIndex();
                    } else {
                        selectedIndex = 0;
                    }

                    craftingDialog.clearItemList();
                    addCraftingItemsToDialog(event, craftingDialog);

                    if (selectedItem != null) {
                        craftingDialog.selectItemByItemIndex(selectedIndex);
                    }
                }
            });
        } else {
            closeCraftingDialog(craftingDialog.getDialogId());
            World.getUpdateTaskManager().addTask(new UpdateTask() {
                @Override
                public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                    craftingDialog.setDialogId(event.getRequestId());
                    craftingDialog.clearItemList();
                    addCraftingItemsToDialog(event, craftingDialog);
                    craftingDialog.setProgress(0.f);
                    craftingDialog.selectItemByItemIndex(0);
                    craftingDialog.setAmount(1);
                    craftingDialog.getElement().show(new EndNotify() {
                        @Override
                        public void perform() {
                            craftingDialog.moveToFront();
                            craftingDialog.selectItemByItemIndex(0);
                        }
                    });
                    openCraftDialog = true;
                }
            });
        }
    }

    private void addCraftingItemsToDialog(
            @Nonnull DialogCraftingReceivedEvent event, @Nonnull DialogCrafting dialog) {
        NiftyCraftingCategory[] categories = new NiftyCraftingCategory[event.getGroupCount()];
        for (int i = 0; i < event.getGroupCount(); i++) {
            categories[i] = new NiftyCraftingCategory(event.getGroupTitle(i));
        }

        boolean addedToUnknown = false;
        NiftyCraftingCategory unknownCat = new NiftyCraftingCategory("not assigned");

        for (int i = 0; i < event.getCraftingItemCount(); i++) {
            CraftingItem item = event.getCraftingItem(i);
            int groupId = item.getGroup();
            if ((groupId < 0) || (groupId >= categories.length)) {
                addedToUnknown = true;
                unknownCat.addChild(new NiftyCraftingItem(nifty, i, item));
                log.warn("Crafting item with illegal group received: {}", Integer.toString(groupId));
            } else {
                categories[item.getGroup()].addChild(new NiftyCraftingItem(nifty, i, item));
            }
        }

        dialog.addCraftingItems(categories);
        if (addedToUnknown) {
            dialog.addCraftingItems(unknownCat);
        }
    }

    @EventSubscriber
    public void handleDialogCraftingUpdateAbortedEvent(DialogCraftingUpdateAbortedReceivedEvent event) {
        if (craftingDialog == null) {
            return;
        }

        craftingDialog.setProgress(0.f);
    }

    @EventSubscriber
    public void handleDialogCraftingUpdateCompletedEvent(DialogCraftingUpdateCompletedReceivedEvent event) {
        if (craftingDialog == null) {
            return;
        }

        craftingDialog.setProgress(0.f);
        craftingDialog.setAmount(craftingDialog.getAmount() - 1);
    }

    @EventSubscriber
    public void handleDialogCraftingUpdateStartEvent(@Nonnull DialogCraftingUpdateStartReceivedEvent event) {
        if (craftingDialog == null) {
            return;
        }

        craftingDialog.startProgress(event.getRequiredTime() / 10.0);
    }

    @EventSubscriber
    public void handleMerchantDialogEvent(@Nonnull DialogMerchantReceivedEvent event) {
        showMerchantDialog(event);
    }

    private void showMerchantDialog(@Nonnull final DialogMerchantReceivedEvent event) {
        World.getUpdateTaskManager().addTask(new UpdateTask() {
            @Override
            public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                merchantDialog.clearItems();
                merchantDialog.setDialogId(event.getId());
                merchantDialog.setTitle(event.getTitle());
                addMerchantItemsToDialog(event, merchantDialog);
                merchantDialog.getElement().show(new EndNotify() {
                    @Override
                    public void perform() {
                        craftingDialog.moveToFront();
                    }
                });
            }
        });
    }

    private void addMerchantItemsToDialog(
            @Nonnull DialogMerchantReceivedEvent event, @Nonnull DialogMerchant dialog) {
        Collection<MerchantListEntry> sellingList = new ArrayList<>();
        Collection<MerchantListEntry> buyingList = new ArrayList<>();
        for (int i = 0; i < event.getItemCount(); i++) {
            NiftyMerchantItem item = new NiftyMerchantItem(nifty, event.getItem(i));

            switch (item.getType()) {
                case SellingItem:
                    sellingList.add(item);
                    break;
                case BuyingPrimaryItem:
                case BuyingSecondaryItem:
                    buyingList.add(item);
                    break;
            }
        }
        dialog.addAllSellingItems(sellingList);
        dialog.addAllBuyingItems(buyingList);
    }

    @EventSubscriber
    public void handleSelectDialogEvent(@Nonnull final DialogSelectionReceivedEvent event) {
        GlobalExecutorService.getService().submit(new Runnable() {
            @Override
            public void run() {
                showSelectDialog(event);
            }
        });
    }

    private void showSelectDialog(@Nonnull final DialogSelectionReceivedEvent event) {
        Element parentArea = screen.findElementById("windows");
        DialogSelectBuilder builder = new DialogSelectBuilder("selectDialog" + Integer.toString(event.getId()),
                                                              event.getTitle());
        builder.dialogId(event.getId());
        builder.message(event.getMessage());

        int selectedWidth = 0;
        boolean useImages = false;
        Font textFont = FontLoader.getInstance().getFont(FontLoader.TEXT_FONT);
        for (int i = 0; i < event.getOptionCount(); i++) {
            SelectionItem item = event.getOption(i);
            useImages = useImages || (item.getId() > 0);
            selectedWidth = Math.max(selectedWidth, textFont.getWidth(item.getName()));
        }
        if (useImages) {
            selectedWidth += 79; // width of the image container area
        }
        selectedWidth += 2;  // padding of entry
        selectedWidth += 26; // padding of list box and window
        if (event.getOptionCount() > 6) {
            selectedWidth += 16; // space for the scroll bar
        }
        selectedWidth += 10; // padding to make it look good (some space on the right side of the text entries)
        selectedWidth += 20; // magical additional width of unknown origin (determined by testing)

        selectedWidth = Math.max(selectedWidth, 270); // width required to display the buttons properly

        builder.width(SizeValue.px(selectedWidth));
        builder.itemCount(Math.min(6, event.getOptionCount()));
        builders.add(new BuildWrapper(builder, parentArea, new PostBuildTask() {
            @Override
            public void run(@Nonnull Element createdElement) {
                DialogSelect dialog = createdElement.getNiftyControl(DialogSelect.class);
                if (dialog == null) {
                    log.warn("Newly created dialog was NULL");
                } else {
                    addSelectItemsToDialog(event, dialog);
                }
            }
        }));
    }

    private void addSelectItemsToDialog(
            @Nonnull DialogSelectionReceivedEvent event, @Nonnull DialogSelect dialog) {
        for (int i = 0; i < event.getOptionCount(); i++) {
            dialog.addItem(new NiftySelectItem(nifty, event.getOption(i)));
        }
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
        EventBus.publish(new CloseDialogEvent(id, DialogType.Crafting));
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
    public void bind(@Nonnull Nifty parentNifty, @Nonnull Screen parentScreen) {
        nifty = parentNifty;
        screen = parentScreen;

        merchantDialog = screen.findNiftyControl("merchantDialog", DialogMerchant.class);
        craftingDialog = screen.findNiftyControl("craftingDialog", DialogCrafting.class);
    }

    @Override
    public void onEndScreen() {
        AnnotationProcessor.unprocess(this);
        nifty.unsubscribeAnnotations(this);

        closeDialog(new CloseDialogEvent(CloseDialogEvent.ALL_DIALOGS));
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
        MerchantItem.MerchantItemType expectedItemType;
        switch (list) {
            case 0:
                expectedItemType = MerchantItem.MerchantItemType.SellingItem;
                break;
            case 1:
                expectedItemType = MerchantItem.MerchantItemType.BuyingPrimaryItem;
                break;
            case 2:
                expectedItemType = MerchantItem.MerchantItemType.BuyingSecondaryItem;
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

    @Override
    public void showInputDialog(
            int id, @Nonnull String title, @Nonnull String description, int maxCharacters, boolean multipleLines) {
        Element parentArea = screen.findElementById("windows");
        DialogInputBuilder builder = new DialogInputBuilder("inputDialog" + Integer.toString(id), title);
        builder.description(description);
        builder.buttonLeft("OK");
        builder.buttonRight("Cancel");
        builder.dialogId(id);
        builder.maxLength(maxCharacters);
        if (multipleLines) {
            builder.style("illarion-dialog-input-multi");
        } else {
            builder.style("illarion-dialog-input-single");
        }
        builders.add(new BuildWrapper(builder, parentArea, null));
    }

    @Override
    public void showNamingDialog(@Nonnull Char chara) {
        Element parentArea = screen.findElementById("windows");
        CharacterId charId = chara.getCharId();
        if ((charId == null) || !charId.isHuman() || World.getPlayer().isPlayer(charId)) {
            return;
        }
        String currentCustomName = chara.getCustomName();
        String dialogName = "namingDialog" + Long.toString(charId.getValue());
        if (parentArea.findElementById(dialogName) != null) {
            return;
        }
        DialogInputBuilder builder = new DialogInputBuilder(dialogName, Lang.getMsg("gui.dialog.naming.title"));
        builder.description(String.format(Lang.getMsg("gui.dialog.naming.description"), chara.getName()));
        builder.buttonLeft(Lang.getMsg("gui.dialog.naming.ok"));
        builder.buttonRight(Lang.getMsg("gui.dialog.naming.cancel"));
        builder.dialogId(charId.getAsInteger());
        builder.maxLength(255);
        builder.initalText((currentCustomName == null) ? "" : currentCustomName);
        builder.style("illarion-dialog-input-single");
        builders.add(new BuildWrapper(builder, parentArea, new PostBuildTask() {
            @Override
            public void run(Element createdElement) {
                DialogInput control = createdElement.getNiftyControl(DialogInput.class);
                if (control != null) {
                    control.setFocus();
                }
            }
        }));
    }

    @Override
    public void showMessageDialog(int id, String title, String message) {
        Element parentArea = screen.findElementById("windows");
        DialogMessageBuilder builder = new DialogMessageBuilder("msgDialog" + Integer.toString(id), title);
        builder.text(message);
        builder.button("OK");
        builder.dialogId(id);
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
            CloseDialogEvent closeEvent = closers.poll();
            if (closeEvent == null) {
                break;
            }

            closeDialog(closeEvent);
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

        for (final Element child : parentArea.getChildren()) {
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

    private void closeDialog(@Nonnull CloseDialogEvent event) {
        Element parentArea = screen.findElementById("windows");
        if (parentArea == null) {
            return;
        }

        if (event.isClosingDialogType(DialogType.Merchant)) {
            if (merchantDialog != null) {
                if ((event.getDialogId() == CloseDialogEvent.ALL_DIALOGS) ||
                        (event.getDialogId() == merchantDialog.getDialogId())) {
                    merchantDialog.closeWindow();
                }
            }
        }
        if (event.isClosingDialogType(DialogType.Crafting)) {
            if (craftingDialog != null) {
                if ((event.getDialogId() == CloseDialogEvent.ALL_DIALOGS) ||
                        (event.getDialogId() == craftingDialog.getDialogId())) {
                    craftingDialog.closeWindow();
                }
            }
        }

        for (final Element child : parentArea.getChildren()) {
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

                if ((event.getDialogId() == CloseDialogEvent.ALL_DIALOGS) || (id == event.getDialogId())) {
                    if ("msg".equals(type) && event.isClosingDialogType(DialogType.Message)) {
                        child.hide(new EndNotify() {
                            @Override
                            public void perform() {
                                child.markForRemoval();
                            }
                        });
                    }
                    if ("input".equals(type) && event.isClosingDialogType(DialogType.Input)) {
                        child.hide(new EndNotify() {
                            @Override
                            public void perform() {
                                child.markForRemoval();
                            }
                        });
                    }
                    if ("select".equals(type) && event.isClosingDialogType(DialogType.Selection)) {
                        child.hide(new EndNotify() {
                            @Override
                            public void perform() {
                                child.markForRemoval();
                            }
                        });
                    }
                }
            } catch (@Nonnull NumberFormatException ignored) {
                // nothing
            }
        }
    }

    @SuppressWarnings("MethodMayBeStatic")
    @NiftyEventSubscriber(id = "merchantDialog")
    public void handleMerchantBuyEvent(String topic, @Nonnull DialogMerchantBuyEvent event) {
        final MerchantList list = World.getPlayer().getMerchantList();

        if (list == null) {
            log.error("Buying event received, but there is not merchant list.");
            return;
        }

        final int index = event.getItem().getIndex();
        if (ItemCount.isGreaterOne(list.getItem(index).getBundleSize())) {
            list.buyItem(index);
        } else {
            if (input.isAnyKeyDown(Key.LeftShift, Key.RightShift)) {
                numberSelect.requestNewPopup(1, 250, new NumberSelectPopupHandler.Callback() {
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

    @SuppressWarnings("MethodMayBeStatic")
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

    @SuppressWarnings("MethodMayBeStatic")
    @NiftyEventSubscriber(pattern = "inputDialog[0-9]+")
    public void handleInputConfirmedEvent(String topic, @Nonnull DialogInputConfirmedEvent event) {
        if (event.getPressedButton() == DialogInput.DialogButton.LeftButton) {
            World.getNet().sendCommand(new CloseDialogInputCmd(event.getDialogId(), event.getText(), true));
        } else {
            World.getNet().sendCommand(new CloseDialogInputCmd(event.getDialogId(), "", false));
        }
    }

    @SuppressWarnings("MethodMayBeStatic")
    @NiftyEventSubscriber(pattern = "namingDialog[-0-9]+")
    public void handleNamingConfirmedEvent(String topic, @Nonnull DialogInputConfirmedEvent event) {
        if (event.getPressedButton() == DialogInput.DialogButton.LeftButton) {
            CharacterId id = new CharacterId(event.getDialogId());
            String newName = event.getText();
            World.getNet().sendCommand(new NamePlayerCmd(id, newName));
            Char character = World.getPeople().getCharacter(id);
            if (character != null) {
                character.setCustomName(newName);
            }
        }
    }

    @SuppressWarnings("MethodMayBeStatic")
    @NiftyEventSubscriber(pattern = "msgDialog[0-9]+")
    public void handleMessageConfirmedEvent(String topic, @Nonnull DialogMessageConfirmedEvent event) {
        World.getNet().sendCommand(new CloseDialogMessageCmd(event.getDialogId()));
    }

    @SuppressWarnings("MethodMayBeStatic")
    @NiftyEventSubscriber(pattern = "selectDialog[0-9]+")
    public void handleSelectionCancelEvent(String topic, @Nonnull DialogSelectCancelEvent event) {
        World.getNet().sendCommand(new CloseDialogSelectionCmd(event.getDialogId(), 0, false));
        EventBus.publish(new CloseDialogEvent(event.getDialogId(), DialogType.Selection));
    }

    @SuppressWarnings("MethodMayBeStatic")
    @NiftyEventSubscriber(pattern = "selectDialog[0-9]+")
    public void handleSelectionSelectEvent(String topic, @Nonnull DialogSelectSelectEvent event) {
        World.getNet().sendCommand(new CloseDialogSelectionCmd(event.getDialogId(), event.getItemIndex(), true));
        EventBus.publish(new CloseDialogEvent(event.getDialogId(), DialogType.Selection));
    }
}
