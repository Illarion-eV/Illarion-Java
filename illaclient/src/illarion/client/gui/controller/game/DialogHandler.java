/*
 * This file is part of the Illarion Client.
 *
 * Copyright Â© 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
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
import illarion.client.gui.events.TooltipsRemovedEvent;
import illarion.client.gui.util.NiftyCraftingCategory;
import illarion.client.gui.util.NiftyCraftingItem;
import illarion.client.gui.util.NiftyMerchantItem;
import illarion.client.gui.util.NiftySelectItem;
import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.CloseDialogInputCmd;
import illarion.client.net.client.CloseDialogMessageCmd;
import illarion.client.net.client.CloseDialogSelectionCmd;
import illarion.client.net.client.CraftItemCmd;
import illarion.client.net.server.events.*;
import illarion.client.world.World;
import illarion.client.world.events.CloseDialogEvent;
import illarion.client.world.items.CraftingItem;
import illarion.client.world.items.MerchantList;
import illarion.common.types.ItemCount;
import illarion.common.types.Rectangle;
import org.apache.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.illarion.nifty.controls.*;
import org.illarion.nifty.controls.dialog.input.builder.DialogInputBuilder;
import org.illarion.nifty.controls.dialog.message.builder.DialogMessageBuilder;
import org.illarion.nifty.controls.dialog.select.builder.DialogSelectBuilder;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the dialog handler that takes care for receiving events to show dialogs. It opens and maintains those
 * dialogs and notifies the server in case a dialog is closed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogHandler implements ScreenController, UpdatableHandler {
    /**
     * The input control that is used in this dialog handler.
     */
    private Input input;

    private DialogMerchant merchantDialog;
    private DialogCrafting craftingDialog;
    private boolean openCraftDialog;

    @Override
    public void update(final GameContainer container, final int delta) {
        input = container.getInput();
        while (true) {
            final DialogHandler.BuildWrapper wrapper = builders.poll();
            if (wrapper == null) {
                break;
            }

            final Element element = wrapper.getBuilder().build(nifty, screen, wrapper.getParent());

            wrapper.executeTask(element);

            element.layoutElements();
            element.setConstraintX(SizeValue.px((wrapper.getParent().getWidth() - element.getWidth()) / 2));
            element.setConstraintY(SizeValue.px((wrapper.getParent().getHeight() - element.getHeight()) / 2));
            wrapper.getParent().layoutElements();
        }

        while (true) {
            final CloseDialogEvent closeEvent = closers.poll();
            if (closeEvent == null) {
                break;
            }

            closeDialog(closeEvent);
        }

        while (true) {
            final Runnable task = updater.poll();
            if (task == null) {
                break;
            }

            task.run();
        }
    }

    private class BuildWrapper {
        private final ControlBuilder builder;
        private final Element parent;
        private final DialogHandler.PostBuildTask task;

        BuildWrapper(final ControlBuilder builder, final Element parent, final DialogHandler.PostBuildTask task) {
            this.builder = builder;
            this.parent = parent;
            this.task = task;
        }

        public ControlBuilder getBuilder() {
            return builder;
        }

        public Element getParent() {
            return parent;
        }

        public void executeTask(final Element createdElement) {
            if (task != null) {
                task.run(createdElement);
            }
        }
    }

    private interface PostBuildTask {
        void run(Element createdElement);
    }

    private static final Logger LOGGER = Logger.getLogger(DialogHandler.class);

    private final Queue<DialogHandler.BuildWrapper> builders;
    private final Queue<CloseDialogEvent> closers;
    private final Queue<Runnable> updater;
    private Nifty nifty;
    private Screen screen;
    private final NumberSelectPopupHandler numberSelect;
    private final TooltipHandler tooltipHandler;

    public DialogHandler(final NumberSelectPopupHandler numberSelectPopupHandler, final TooltipHandler tooltipHandler) {
        this.tooltipHandler = tooltipHandler;
        builders = new ConcurrentLinkedQueue<DialogHandler.BuildWrapper>();
        closers = new ConcurrentLinkedQueue<CloseDialogEvent>();
        updater = new ConcurrentLinkedQueue<Runnable>();
        numberSelect = numberSelectPopupHandler;
    }

    @Override
    public void bind(final Nifty parentNifty, final Screen parentScreen) {
        nifty = parentNifty;
        screen = parentScreen;

        merchantDialog = screen.findNiftyControl("merchantDialog", DialogMerchant.class);
        craftingDialog = screen.findNiftyControl("craftingDialog", DialogCrafting.class);
    }

    @EventSubscriber
    public void handleMessageDialogEvent(final DialogMessageReceivedEvent event) {
        showDialogMessage(event.getId(), event.getTitle(), event.getMessage());
    }

    @EventSubscriber
    public void handleInputDialogEvent(final DialogInputReceivedEvent event) {
        showDialogInput(event.getId(), event.getTitle(), event.getDescription(), event.getMaxLength(),
                event.hasMultipleLines());
    }

    @EventSubscriber
    public void handleCraftingDialogEvent(final DialogCraftingReceivedEvent event) {
        showCraftingDialog(event);
    }

    @EventSubscriber
    public void handleMerchantDialogEvent(final DialogMerchantReceivedEvent event) {
        showMerchantDialog(event);
    }

    @EventSubscriber
    public void handleSelectDialogEvent(final DialogSelectionReceivedEvent event) {
        showSelectDialog(event);
    }

    @EventSubscriber
    public void handleCloseDialogEvent(final CloseDialogEvent event) {
        closers.offer(event);
    }

    private int lastCraftingTooltip = -2;

    @EventSubscriber
    public void handleTooltipRemovedEvent(final TooltipsRemovedEvent event) {
        lastCraftingTooltip = -2;

        if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) || input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
            return;
        }
    }

    @EventSubscriber
    public void handleDialogItemLookAtEvent(final DialogItemLookAtEvent event) {
        if (craftingDialog == null) {
            return;
        }

        if (craftingDialog.getSelectedCraftingItem().getItemIndex() == event.getSlot()) {
            final Element targetElement = craftingDialog.getCraftingItemDisplay();
            final Rectangle elementRectangle = new Rectangle();
            elementRectangle.set(targetElement.getX(), targetElement.getY(), targetElement.getWidth(),
                    targetElement.getHeight());
            tooltipHandler.showToolTip(elementRectangle, event);
        }
    }

    @EventSubscriber
    public void handleDialogSecondaryItemLookAtEvent(final DialogSecondaryItemLookAtEvent event) {
        if (craftingDialog == null) {
            return;
        }

        if (craftingDialog.getSelectedCraftingItem().getItemIndex() == event.getSlot()) {
            final Element targetElement = craftingDialog.getIngredientItemDisplay(event.getSecondarySlot());
            final Rectangle elementRectangle = new Rectangle();
            elementRectangle.set(targetElement.getX(), targetElement.getY(), targetElement.getWidth(),
                    targetElement.getHeight());
            tooltipHandler.showToolTip(elementRectangle, event);
        }
    }

    @EventSubscriber
    public void handleDialogCraftingUpdateStartEvent(final DialogCraftingUpdateStartReceivedEvent event) {
        if (craftingDialog == null) {
            return;
        }

        craftingDialog.startProgress((double) event.getRequiredTime() / 10.0);
    }

    @EventSubscriber
    public void handleDialogCraftingUpdateAbortedEvent(final DialogCraftingUpdateAbortedReceivedEvent event) {
        if (craftingDialog == null) {
            return;
        }

        craftingDialog.setProgress(0.f);
    }

    @EventSubscriber
    public void handleDialogCraftingUpdateCompletedEvent(final DialogCraftingUpdateCompletedReceivedEvent event) {
        if (craftingDialog == null) {
            return;
        }

        craftingDialog.setProgress(0.f);
    }

    @NiftyEventSubscriber(id = "craftingDialog")
    public void handleCraftingItemLookAtEvent(final String topic, final DialogCraftingLookAtItemEvent event) {
        if (lastCraftingTooltip == -1) {
            return;
        }

        if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) || input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
            return;
        }

        final CommandFactory factory = CommandFactory.getInstance();
        final CraftItemCmd cmd = factory.getCommand(CommandList.CMD_CRAFT_ITEM, CraftItemCmd.class);
        cmd.setLookAtItem(event.getItem().getItemIndex());
        cmd.setDialogId(event.getDialogId());
        cmd.send();

        lastCraftingTooltip = -1;
    }

    @NiftyEventSubscriber(id = "craftingDialog")
    public void handleCraftingIngredientLookAtEvent(final String topic,
                                                    final DialogCraftingLookAtIngredientItemEvent event) {
        if (lastCraftingTooltip == event.getIngredientIndex()) {
            return;
        }

        if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON) || input.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON)) {
            return;
        }

        final CommandFactory factory = CommandFactory.getInstance();
        final CraftItemCmd cmd = factory.getCommand(CommandList.CMD_CRAFT_ITEM, CraftItemCmd.class);
        cmd.setLookAtIngredient(event.getItem().getItemIndex(), event.getIngredientIndex());
        cmd.setDialogId(event.getDialogId());
        cmd.send();

        lastCraftingTooltip = event.getIngredientIndex();
    }

    @NiftyEventSubscriber(id = "craftingDialog")
    public void handleCraftingCraftItemEvent(final String topic, final DialogCraftingCraftEvent event) {
        final CommandFactory factory = CommandFactory.getInstance();
        final CraftItemCmd cmd = factory.getCommand(CommandList.CMD_CRAFT_ITEM, CraftItemCmd.class);
        cmd.setCraftItem(event.getItem().getItemIndex(), event.getCount());
        cmd.setDialogId(event.getDialogId());
        cmd.send();
    }

    @NiftyEventSubscriber(id = "craftingDialog")
    public void handleCraftingCloseDialogEvent(final String topic, final DialogCraftingCloseEvent event) {
        closeCraftingDialog(event.getDialogId());
    }

    private void closeCraftingDialog(final int id) {
        if (!openCraftDialog) {
            return;
        }
        final CommandFactory factory = CommandFactory.getInstance();
        final CraftItemCmd cmd = factory.getCommand(CommandList.CMD_CRAFT_ITEM, CraftItemCmd.class);
        cmd.setCloseDialog();
        cmd.setDialogId(id);
        cmd.send();
        EventBus.publish(new CloseDialogEvent(id, CloseDialogEvent.DialogType.Crafting));
        openCraftDialog = false;
    }

    @SuppressWarnings("MethodMayBeStatic")
    @NiftyEventSubscriber(pattern = "msgDialog[0-9]+")
    public void handleMessageConfirmedEvent(final String topic, final DialogMessageConfirmedEvent event) {
        final CommandFactory factory = CommandFactory.getInstance();
        final CloseDialogMessageCmd cmd = factory.getCommand(CommandList.CMD_CLOSE_DIALOG_MSG,
                CloseDialogMessageCmd.class);
        cmd.setDialogId(event.getDialogId());
        cmd.send();
    }

    @SuppressWarnings("MethodMayBeStatic")
    @NiftyEventSubscriber(pattern = "inputDialog[0-9]+")
    public void handleInputConfirmedEvent(final String topic, final DialogInputConfirmedEvent event) {
        final CommandFactory factory = CommandFactory.getInstance();
        final CloseDialogInputCmd cmd = factory.getCommand(CommandList.CMD_CLOSE_DIALOG_INPUT,
                CloseDialogInputCmd.class);
        cmd.setDialogId(event.getDialogId());
        if (event.getPressedButton() == DialogInput.DialogButton.left) {
            cmd.setSuccess(true);
            cmd.setText(event.getText());
        } else {
            cmd.setSuccess(false);
            cmd.setText("");
        }
        cmd.send();
    }

    @SuppressWarnings("MethodMayBeStatic")
    @NiftyEventSubscriber(id = "merchantDialog")
    public void handleMerchantBuyEvent(final String topic, final DialogMerchantBuyEvent event) {
        final MerchantList list = World.getPlayer().getMerchantList();
        final int index = event.getItem().getIndex();

        if (ItemCount.isGreaterOne(list.getItem(index).getBundleSize())) {
            list.buyItem(index);
        } else {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                numberSelect.requestNewPopup(1, 250, new NumberSelectPopupHandler.Callback() {
                    @Override
                    public void popupCanceled() {
                        // nothing
                    }

                    @Override
                    public void popupConfirmed(final int value) {
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
    public void handleMerchantCloseEvent(final String topic, final DialogMerchantCloseEvent event) {
        World.getPlayer().getMerchantList().closeDialog();
    }

    @SuppressWarnings("MethodMayBeStatic")
    @NiftyEventSubscriber(pattern = "selectDialog[0-9]+")
    public void handleSelectionSelectEvent(final String topic, final DialogSelectSelectEvent event) {
        final CloseDialogSelectionCmd cmd = CommandFactory.getInstance().getCommand(
                CommandList.CMD_CLOSE_DIALOG_SELECTION, CloseDialogSelectionCmd.class);
        cmd.setDialogId(event.getDialogId());
        cmd.setSelectedIndex(event.getItemIndex());
        cmd.setSuccess(true);
        cmd.send();

        EventBus.publish(new CloseDialogEvent(event.getDialogId(), CloseDialogEvent.DialogType.Selection));
    }

    @SuppressWarnings("MethodMayBeStatic")
    @NiftyEventSubscriber(pattern = "selectDialog[0-9]+")
    public void handleSelectionCancelEvent(final String topic, final DialogSelectCancelEvent event) {
        final CloseDialogSelectionCmd cmd = CommandFactory.getInstance().getCommand(
                CommandList.CMD_CLOSE_DIALOG_SELECTION, CloseDialogSelectionCmd.class);
        cmd.setDialogId(event.getDialogId());
        cmd.setSuccess(false);
        cmd.send();

        EventBus.publish(new CloseDialogEvent(event.getDialogId(), CloseDialogEvent.DialogType.Selection));
    }

    @Override
    public void onStartScreen() {
        AnnotationProcessor.process(this);
        nifty.subscribeAnnotations(this);
    }

    @Override
    public void onEndScreen() {
        AnnotationProcessor.unprocess(this);
        nifty.unsubscribeAnnotations(this);
    }

    private void showDialogMessage(final int id, final String title, final String message) {
        final Element parentArea = screen.findElementByName("windows");
        final DialogMessageBuilder builder = new DialogMessageBuilder("msgDialog" + Integer.toString(id), title);
        builder.text(message);
        builder.button("OK");
        builder.dialogId(id);
        builders.add(new DialogHandler.BuildWrapper(builder, parentArea, null));
    }

    private void showDialogInput(final int id, final String title, final String description, final int maxCharacters,
                                 final boolean multipleLines) {
        final Element parentArea = screen.findElementByName("windows");
        final DialogInputBuilder builder = new DialogInputBuilder("inputDialog" + Integer.toString(id), title);
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
        builders.add(new DialogHandler.BuildWrapper(builder, parentArea, null));
    }

    private void showMerchantDialog(final DialogMerchantReceivedEvent event) {
        updater.add(new Runnable() {
            @Override
            public void run() {
                merchantDialog.clearItems();
                merchantDialog.setDialogId(event.getId());
                merchantDialog.setTitle(event.getTitle());
                addMerchantItemsToDialog(event, merchantDialog);
                merchantDialog.getElement().setVisible(true);
                merchantDialog.getElement().getParent().layoutElements();
            }
        });
    }

    private void showSelectDialog(final DialogSelectionReceivedEvent event) {
        final Element parentArea = screen.findElementByName("windows");
        final DialogSelectBuilder builder = new DialogSelectBuilder(
                "selectDialog" + Integer.toString(event.getId()), event.getTitle());
        builder.dialogId(event.getId());
        builder.message(event.getMessage());
        builder.width(builder.pixels(500));
        builders.add(new DialogHandler.BuildWrapper(builder, parentArea, new DialogHandler.PostBuildTask() {
            @Override
            public void run(final Element createdElement) {
                final DialogSelect dialog = createdElement.getNiftyControl(DialogSelect.class);

                addSelectItemsToDialog(event, dialog);
            }
        }));
    }

    private void showCraftingDialog(final DialogCraftingReceivedEvent event) {
        if ((event.getRequestId() == craftingDialog.getDialogId()) && openCraftDialog) {
            updater.add(new Runnable() {
                @Override
                public void run() {
                    final int selectedIndex = craftingDialog.getSelectedCraftingItem().getItemIndex();

                    craftingDialog.clearItemList();
                    addCraftingItemsToDialog(event, craftingDialog);

                    craftingDialog.selectItemByItemIndex(selectedIndex);
                }
            });
        } else {
            closeCraftingDialog(craftingDialog.getDialogId());
            updater.add(new Runnable() {
                @Override
                public void run() {
                    craftingDialog.setDialogId(event.getRequestId());
                    craftingDialog.clearItemList();
                    addCraftingItemsToDialog(event, craftingDialog);
                    craftingDialog.getElement().setVisible(true);
                    craftingDialog.getElement().layoutElements();
                    openCraftDialog = true;
                }
            });
        }
    }

    private void addMerchantItemsToDialog(final DialogMerchantReceivedEvent event, final DialogMerchant dialog) {
        final List<MerchantListEntry> sellingList = new ArrayList<MerchantListEntry>();
        final List<MerchantListEntry> buyingList = new ArrayList<MerchantListEntry>();
        for (int i = 0; i < event.getItemCount(); i++) {
            final NiftyMerchantItem item = new NiftyMerchantItem(nifty, event.getItem(i));

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

    private void addSelectItemsToDialog(final DialogSelectionReceivedEvent event, final DialogSelect dialog) {
        for (int i = 0; i < event.getOptionCount(); i++) {
            dialog.addItem(new NiftySelectItem(nifty, event.getOption(i)));
        }
    }

    private void addCraftingItemsToDialog(final DialogCraftingReceivedEvent event, final DialogCrafting dialog) {
        final NiftyCraftingCategory[] categories = new NiftyCraftingCategory[event.getGroupCount()];
        for (int i = 0; i < event.getGroupCount(); i++) {
            categories[i] = new NiftyCraftingCategory(event.getGroupTitle(i));
        }

        boolean addedToUnknown = false;
        final NiftyCraftingCategory unknownCat = new NiftyCraftingCategory("not assigned");

        for (int i = 0; i < event.getCraftingItemCount(); i++) {
            final CraftingItem item = event.getCraftingItem(i);
            final int groupId = item.getGroup();
            if ((groupId < 0) || (groupId >= categories.length)) {
                addedToUnknown = true;
                unknownCat.addChild(new NiftyCraftingItem(nifty, i, item));
                LOGGER.warn("Crafting item with illegal group received: " + Integer.toString(groupId));
            } else {
                categories[item.getGroup()].addChild(new NiftyCraftingItem(nifty, i, item));
            }
        }

        dialog.addCraftingItems(categories);
        if (addedToUnknown) {
            dialog.addCraftingItems(unknownCat);
        }
    }

    private static final Pattern dialogNamePattern = Pattern.compile("([a-z]+)Dialog([0-9]+)");

    private void closeDialog(final CloseDialogEvent event) {
        final Element parentArea = screen.findElementByName("windows");

        if (event.getDialogType() == CloseDialogEvent.DialogType.Merchant) {
            if (event.getDialogId() == merchantDialog.getDialogId()) {
                merchantDialog.closeWindow();
            }
            return;
        }
        if (event.getDialogType() == CloseDialogEvent.DialogType.Crafting) {
            if (event.getDialogId() == craftingDialog.getDialogId()) {
                craftingDialog.closeWindow();
            }
            return;
        }

        for (final Element child : parentArea.getElements()) {
            final Matcher matcher = dialogNamePattern.matcher(child.getId());

            if (!matcher.find()) {
                continue;
            }

            try {
                final String type = matcher.group(1);
                final int id = Integer.parseInt(matcher.group(2));

                boolean wrongDialogType = false;
                switch (event.getDialogType()) {
                    case Any:
                        break;
                    case Message:
                        if (!type.equals("msg")) {
                            wrongDialogType = true;
                        }
                        break;
                    case Input:
                        if (!type.equals("input")) {
                            wrongDialogType = true;
                        }
                        break;
                    case Merchant:
                        if (!type.equals("merchant")) {
                            wrongDialogType = true;
                        }
                    case Selection:
                        if (!type.equals("select")) {
                            wrongDialogType = true;
                        }
                        break;
                    case Crafting:
                        if (!type.equals("crafting")) {
                            wrongDialogType = true;
                        }
                        break;
                }

                if (wrongDialogType) {
                    continue;
                }

                if ((event.getDialogId() == CloseDialogEvent.ALL_DIALOGS) || (event.getDialogId() == id)) {
                    child.hide(new EndNotify() {
                        @Override
                        public void perform() {
                            child.markForRemoval();
                        }
                    });
                }

            } catch (final NumberFormatException ignored) {
                // nothing
            }
        }
    }
}