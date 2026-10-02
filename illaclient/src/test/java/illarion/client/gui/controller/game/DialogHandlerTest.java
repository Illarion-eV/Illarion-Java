/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2026 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.Window;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.spi.input.InputSystem;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import illarion.client.graphics.FontLoader;
import illarion.client.gui.DialogGui;
import illarion.client.gui.DialogType;
import illarion.client.gui.GameGui;
import illarion.client.net.NetComm;
import illarion.client.net.client.CloseDialogCraftingCmd;
import illarion.client.util.UpdateTaskManager;
import illarion.client.world.World;
import illarion.client.world.items.CraftingItem;
import org.easymock.EasyMock;
import org.illarion.engine.GameContainer;
import org.illarion.engine.graphic.Font;
import org.illarion.nifty.controls.CraftingItemEntry;
import org.illarion.nifty.controls.DialogCrafting;
import org.illarion.nifty.controls.DialogCraftingCloseEvent;
import org.illarion.nifty.controls.DialogMerchant;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.powermock.reflect.Whitebox;
import org.testng.IObjectFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.EnumSet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@PrepareForTest({World.class, NetComm.class, FontLoader.class})
@PowerMockIgnore({"javax.management.*", "javax.xml.parsers.*", "com.sun.org.apache.xerces.internal.jaxp.*",
        "ch.qos.logback.*", "org.slf4j.*", "de.lessvoid.nifty.*"})
public class DialogHandlerTest {
    private DialogHandler handler;
    private UpdateTaskManager updates;
    private GameContainer container;
    private Nifty nifty;
    private Element element;
    private Element merchantElement;
    private Screen screen;
    private DialogCrafting crafting;
    private NetComm network;
    private int dialogId;
    private int selectedIndex;
    private int amount;
    private float progress;
    private long time;

    @ObjectFactory
    public IObjectFactory createObjectFactory() {
        return new PowerMockObjectFactory();
    }

    private void setUp(boolean hideEffect) {
        createScreen(hideEffect);
        prepareCraftingControl();
        handler = new DialogHandler(null, null, null);
        Whitebox.setInternalState(handler, "nifty", nifty);
        Whitebox.setInternalState(handler, "screen", screen);
        Whitebox.setInternalState(handler, "craftingDialog", crafting);
        prepareMerchantControl();
        registerPlaceholderDialogs();
        prepareWorld();
    }

    private void createScreen(boolean hideEffect) {
        RenderDevice render = EasyMock.createNiceMock(RenderDevice.class);
        EasyMock.expect(render.getWidth()).andReturn(800).anyTimes();
        EasyMock.expect(render.getHeight()).andReturn(600).anyTimes();
        SoundDevice sound = EasyMock.createNiceMock(SoundDevice.class);
        InputSystem input = EasyMock.createNiceMock(InputSystem.class);
        container = EasyMock.createNiceMock(GameContainer.class);
        ScreenController controller = EasyMock.createNiceMock(ScreenController.class);
        EasyMock.replay(render, sound, input, container, controller);

        time = 1000;
        nifty = new Nifty(render, sound, input, () -> time);
        ScreenBuilder builder = new ScreenBuilder("game", controller);
        LayerBuilder layer = new LayerBuilder("windows");
        layer.childLayoutCenter();
        PanelBuilder panel = new PanelBuilder("craftingDialog");
        panel.childLayoutCenter();
        panel.width("100px");
        panel.height("100px");
        panel.visible(false);

        if (hideEffect) {
            EffectBuilder fade = new EffectBuilder("fade");
            fade.length(200);
            fade.effectParameter("start", "#f");
            fade.effectParameter("end", "#0");
            panel.onHideEffect(fade);
        }

        layer.panel(panel);
        builder.layer(layer);
        PanelBuilder merchantPanel = new PanelBuilder("merchantDialog");
        merchantPanel.width("100px");
        merchantPanel.height("100px");
        merchantPanel.visible(false);
        layer.panel(merchantPanel);
        screen = builder.build(nifty);
        nifty.addScreen("game", screen);
        nifty.gotoScreen("game");
        element = screen.findElementById("craftingDialog");
        assertFalse(element.isVisible());
    }

    private void prepareCraftingControl() {
        Window window = createWindow(element);
        crafting = EasyMock.createNiceMock(DialogCrafting.class);
        dialogId = 0;
        selectedIndex = 0;
        amount = 0;
        progress = 0;
        EasyMock.expect(crafting.getDialogId()).andAnswer(() -> dialogId).anyTimes();
        crafting.setDialogId(EasyMock.anyInt());
        EasyMock.expectLastCall().andAnswer(() -> {
            dialogId = (Integer) EasyMock.getCurrentArguments()[0];
            return null;
        }).anyTimes();
        EasyMock.expect(crafting.getElement()).andReturn(element).anyTimes();
        crafting.closeWindow();
        EasyMock.expectLastCall().andAnswer(() -> {
            window.closeWindow();
            return null;
        }).anyTimes();
        crafting.selectItemByItemIndex(EasyMock.anyInt());
        EasyMock.expectLastCall().andAnswer(() -> {
            selectedIndex = (Integer) EasyMock.getCurrentArguments()[0];
            return null;
        }).anyTimes();
        crafting.setAmount(EasyMock.anyInt());
        EasyMock.expectLastCall().andAnswer(() -> {
            amount = (Integer) EasyMock.getCurrentArguments()[0];
            return null;
        }).anyTimes();
        crafting.setProgress(EasyMock.anyFloat());
        EasyMock.expectLastCall().andAnswer(() -> {
            progress = (Float) EasyMock.getCurrentArguments()[0];
            return null;
        }).anyTimes();
    }

    private void prepareMerchantControl() {
        merchantElement = screen.findElementById("merchantDialog");
        Window merchantWindow = createWindow(merchantElement);
        DialogMerchant merchant = EasyMock.createNiceMock(DialogMerchant.class);
        EasyMock.expect(merchant.getElement()).andReturn(merchantElement).anyTimes();
        EasyMock.expect(merchant.getDialogId()).andReturn(0).anyTimes();
        merchant.closeWindow();
        EasyMock.expectLastCall().andAnswer(() -> {
            merchantWindow.closeWindow();
            return null;
        }).anyTimes();
        EasyMock.replay(merchant);
        Whitebox.setInternalState(handler, "merchantDialog", merchant);
    }

    // WindowControl is the existing production superclass; exercise its real close behaviour.
    @SuppressWarnings("deprecation")
    private Window createWindow(Element target) {
        Window window = new de.lessvoid.nifty.controls.window.WindowControl();
        Whitebox.setInternalState(window, "element", target);
        Whitebox.setInternalState(window, "nifty", nifty);
        Whitebox.setInternalState(window, "hideOnClose", true);
        return window;
    }

    private void registerPlaceholderDialogs() {
        // Minimal windows isolate ordering from resource-dependent dialog contents.
        for (String name : new String[] {"dialog-message", "dialog-input", "dialog-select"}) {
            ControlDefinitionBuilder definition = new ControlDefinitionBuilder(name);
            PanelBuilder root = new PanelBuilder();
            root.width("100px");
            root.height("100px");
            root.childLayoutCenter();
            definition.panel(root);
            definition.registerControlDefintion(nifty);
        }
    }

    private void prepareWorld() {
        Font font = EasyMock.createNiceMock(Font.class);
        EasyMock.replay(font);
        FontLoader fonts = PowerMock.createMock(FontLoader.class);
        EasyMock.expect(fonts.getFont(FontLoader.TEXT_FONT)).andReturn(font).anyTimes();
        PowerMock.mockStatic(FontLoader.class);
        EasyMock.expect(FontLoader.getInstance()).andReturn(fonts).anyTimes();
        PowerMock.replay(fonts, FontLoader.class);
        updates = new UpdateTaskManager();
        network = PowerMock.createMock(NetComm.class);
        GameGui gui = EasyMock.createMock(GameGui.class);
        EasyMock.expect(gui.getDialogGui()).andReturn(handler).anyTimes();
        EasyMock.replay(gui);
        PowerMock.mockStatic(World.class);
        EasyMock.expect(World.getUpdateTaskManager()).andReturn(updates).anyTimes();
        EasyMock.expect(World.getNet()).andReturn(network).anyTimes();
        EasyMock.expect(World.getGameGui()).andReturn(gui).anyTimes();
        PowerMock.replay(World.class);
    }

    private void start() {
        EasyMock.replay(crafting);
        PowerMock.replay(network);
        open(0);
        frame();
        assertTrue(element.isVisible());
    }

    private void open(int id) {
        handler.showCraftingDialog(id, "Gemcutting", Collections.singletonList("Gems"),
                Collections.<CraftingItem>emptyList());
    }

    private void close(int id) {
        handler.closeDialog(id, EnumSet.of(DialogType.Crafting));
    }

    private void frame() {
        // PlayingState processes the general GUI tasks before the child handlers.
        updates.onUpdateGame(container, 16);
        handler.update(container, 16);
    }

    @Test
    public void testFirstOpenShowsWindow() {
        setUp(false);
        start();
        assertEquals(amount, 1);
        PowerMock.verify(network);
    }

    @Test
    public void testReusedIdAfterServerCloseShowsWindow() {
        setUp(false);
        start();
        close(0);
        frame();
        assertFalse(element.isVisible());
        open(0);
        frame();
        assertTrue(element.isVisible());
        PowerMock.verify(network);
    }

    @Test
    public void testNewIdAfterServerCloseShowsWindow() {
        setUp(false);
        start();
        close(0);
        frame();
        open(1);
        frame();
        assertTrue(element.isVisible());
        assertEquals(dialogId, 1);
    }

    @Test
    public void testServerCloseEndsProduction() {
        setUp(false);
        start();
        handler.startProductionIndicator(0, 3, 20);
        frame();
        assertTrue(handler.isCraftingInProgress());
        close(0);
        frame();
        assertFalse(handler.isCraftingInProgress());
        PowerMock.verify(network);
    }

    @Test
    public void testCloseAllAllowsReopening() {
        setUp(false);
        start();
        handler.closeDialog(DialogGui.ALL_DIALOGS, EnumSet.allOf(DialogType.class));
        frame();
        assertFalse(element.isVisible());
        open(0);
        frame();
        assertTrue(element.isVisible());
    }

    @Test
    public void testNewDialogDoesNotInheritProduction() {
        setUp(false);
        start();
        handler.startProductionIndicator(0, 3, 20);
        frame();
        open(1);
        frame();
        assertFalse(handler.isCraftingInProgress());
        assertEquals(amount, 1);
    }

    @Test
    public void testOtherIdDoesNotCloseCrafting() {
        setUp(false);
        start();
        close(1);
        frame();
        assertTrue(element.isVisible());
    }

    @Test
    public void testOtherTypeDoesNotCloseCrafting() {
        setUp(false);
        start();
        handler.closeDialog(0, EnumSet.of(DialogType.Message));
        frame();
        assertTrue(element.isVisible());
    }

    @Test
    public void testOpenDialogUpdatePreservesSelectionAndProduction() {
        setUp(false);
        CraftingItemEntry selection = EasyMock.createMock(CraftingItemEntry.class);
        EasyMock.expect(selection.getItemIndex()).andReturn(7).anyTimes();
        EasyMock.replay(selection);
        EasyMock.expect(crafting.getSelectedCraftingItem()).andReturn(selection).anyTimes();
        start();
        handler.startProductionIndicator(0, 3, 20);
        frame();
        progress = 0.5f;
        open(0);
        frame();
        assertEquals(selectedIndex, 7);
        assertEquals(amount, 3);
        assertEquals(progress, 0.5f);
        assertTrue(handler.isCraftingInProgress());
        assertTrue(element.isVisible());
    }

    @Test
    public void testManualCloseSendsOneReplyAndAllowsReopening() {
        setUp(false);
        network.sendCommand(EasyMock.isA(CloseDialogCraftingCmd.class));
        EasyMock.expectLastCall().once();
        start();
        handler.handleCraftingCloseDialogEvent("craftingDialog", new DialogCraftingCloseEvent(0));
        handler.handleCraftingCloseDialogEvent("craftingDialog", new DialogCraftingCloseEvent(0));
        frame();
        assertFalse(element.isVisible());
        open(0);
        frame();
        assertTrue(element.isVisible());
        PowerMock.verify(network);
    }

    @Test
    public void testScreenEndAllowsSameIdInNextSession() {
        setUp(false);
        start();
        handler.onEndScreen();
        assertFalse(element.isVisible());
        open(0);
        frame();
        assertTrue(element.isVisible());
        PowerMock.verify(network);
    }

    @Test
    public void testReopenCancelsPendingHideEffect() {
        setUp(true);
        start();
        close(0);
        frame();
        assertTrue(element.isEffectActive(EffectEventId.onHide));
        open(0);
        frame();
        assertFalse(element.isEffectActive(EffectEventId.onHide));
        time += 1000;
        nifty.update();
        nifty.render(false);
        assertTrue(element.isVisible());
    }

    @DataProvider(name = "dialogTypes")
    public Object[][] dialogTypes() {
        return new Object[][] {
            {"Crafting"}, {"Merchant"}, {"Message"},
            {"Input"}, {"Selection"}
        };
    }

    private void openDialog(DialogType type) {
        switch (type) {
            case Crafting:
                open(0);
                break;
            case Merchant:
                handler.showMerchantDialog(0, "Merchant", Collections.emptyList());
                break;
            case Message:
                handler.showMessageDialog(0, "Message", "Text");
                break;
            case Input:
                handler.showInputDialog(0, "Input", "Text", 20, false);
                break;
            case Selection:
                handler.showSelectionDialog(0, "Selection", "Text", Collections.emptyList());
                break;
            default:
                throw new AssertionError(type);
        }
    }

    private boolean isDialogVisible(DialogType type) {
        switch (type) {
            case Crafting:
                return element.isVisible();
            case Merchant:
                return merchantElement.isVisible();
            case Message:
                return isWindowVisible("msgDialog0");
            case Input:
                return isWindowVisible("inputDialog0");
            case Selection:
                return isWindowVisible("selectDialog0");
            default:
                throw new AssertionError(type);
        }
    }

    private boolean isWindowVisible(String id) {
        return screen.findElementById("windows").getChildren().stream()
                .anyMatch(child -> id.equals(child.getId()) && child.isVisible());
    }

    @Test(dataProvider = "dialogTypes")
    public void testEarlierCloseDoesNotOvertakeOpen(String typeName) {
        DialogType type = DialogType.valueOf(typeName);
        setUp(false);
        EasyMock.replay(crafting);
        PowerMock.replay(network);
        handler.closeDialog(0, EnumSet.of(type));
        openDialog(type);
        frame();
        assertTrue(isDialogVisible(type));
        PowerMock.verify(network);
    }

    @Test(dataProvider = "dialogTypes")
    public void testOpenThenCloseInOneFrameEndsClosed(String typeName) {
        DialogType type = DialogType.valueOf(typeName);
        setUp(false);
        EasyMock.replay(crafting);
        PowerMock.replay(network);
        openDialog(type);
        handler.closeDialog(0, EnumSet.of(type));
        frame();
        assertFalse(isDialogVisible(type));
        PowerMock.verify(network);
    }

    @Test(dataProvider = "dialogTypes")
    public void testCloseThenReopenInOneFrameEndsOpen(String typeName) {
        DialogType type = DialogType.valueOf(typeName);
        setUp(false);
        EasyMock.replay(crafting);
        PowerMock.replay(network);
        openDialog(type);
        frame();
        assertTrue(isDialogVisible(type));
        handler.closeDialog(0, EnumSet.of(type));
        openDialog(type);
        frame();
        nifty.update();
        assertTrue(isDialogVisible(type));
        PowerMock.verify(network);
    }

    @Test
    public void testCloseAllDoesNotCloseLaterCraftingRequest() {
        setUp(false);
        start();
        handler.closeDialog(DialogGui.ALL_DIALOGS, EnumSet.allOf(DialogType.class));
        open(0);
        frame();
        assertTrue(element.isVisible());
    }

    @Test
    public void testClosePreventsLaterProductionUpdate() {
        setUp(false);
        start();
        close(0);
        handler.startProductionIndicator(0, 8, 20);
        frame();
        assertEquals(amount, 1);
        assertFalse(handler.isCraftingInProgress());
    }

    @Test
    public void testScreenEndDiscardsPendingDialogRequests() {
        setUp(false);
        start();
        open(0);
        handler.onEndScreen();
        frame();
        assertFalse(element.isVisible());
    }

    @Test
    public void testQueuedOpenStartAndAbortRunInOrder() {
        setUp(false);
        EasyMock.replay(crafting);
        PowerMock.replay(network);
        open(0);
        handler.startProductionIndicator(0, 8, 20);
        handler.abortProduction(0);
        frame();
        assertTrue(element.isVisible());
        assertEquals(amount, 8);
        assertEquals(progress, 0.f);
        assertFalse(handler.isCraftingInProgress());
    }

    @Test
    public void testCompletionAfterCloseIsIgnored() {
        setUp(false);
        start();
        close(0);
        handler.finishProduction(0);
        frame();
        assertFalse(element.isVisible());
        assertEquals(amount, 1);
        PowerMock.verify(network);
    }

    @Test
    public void testCloseRetainsRequestedTypes() {
        setUp(false);
        start();
        EnumSet<DialogType> types = EnumSet.of(DialogType.Crafting);
        handler.closeDialog(0, types);
        types.clear();
        types.add(DialogType.Message);
        frame();
        assertFalse(element.isVisible());
    }
}
