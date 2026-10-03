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
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ScrollPanel;
import de.lessvoid.nifty.controls.Scrollbar;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.spi.input.InputSystem;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.render.RenderFont;
import de.lessvoid.nifty.spi.render.RenderImage;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.Game;
import illarion.client.IllaClient;
import illarion.client.gui.GameGui;
import illarion.client.world.World;
import illarion.common.config.Config;
import org.illarion.engine.GameContainer;
import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.powermock.reflect.Whitebox;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@PrepareForTest({World.class, IllaClient.class})
@PowerMockIgnore({"javax.management.*", "javax.xml.parsers.*", "com.sun.org.apache.xerces.internal.jaxp.*",
        "ch.qos.logback.*", "org.slf4j.*", "de.lessvoid.nifty.*"})
public class GUIChatHandlerTest {
    private Game game;
    private GameContainer container;
    private boolean worldInitialised;

    @ObjectFactory
    public IObjectFactory createObjectFactory() {
        return new PowerMockObjectFactory();
    }

    private int width;
    private int height;
    private Nifty nifty;
    private GUIChatHandler handler;
    private ScrollPanel scroll;
    private Element content;

    @BeforeMethod
    public void setUp() throws Exception {
        width = 1000;
        height = 700;
        RenderFont font = EasyMock.createNiceMock(RenderFont.class);
        EasyMock.expect(font.getHeight()).andReturn(16).anyTimes();
        EasyMock.expect(font.getWidth(EasyMock.anyString())).andAnswer(this::textWidth).anyTimes();
        EasyMock.expect(font.getWidth(EasyMock.anyString(), EasyMock.anyFloat()))
                .andAnswer(this::textWidth).anyTimes();
        EasyMock.expect(font.getCharacterAdvance(EasyMock.anyChar(), EasyMock.anyChar(), EasyMock.anyFloat()))
                .andReturn(8).anyTimes();
        RenderImage image = EasyMock.createNiceMock(RenderImage.class);
        EasyMock.expect(image.getWidth()).andReturn(64).anyTimes();
        EasyMock.expect(image.getHeight()).andReturn(64).anyTimes();
        RenderDevice render = EasyMock.createNiceMock(RenderDevice.class);
        EasyMock.expect(render.getWidth()).andAnswer(() -> width).anyTimes();
        EasyMock.expect(render.getHeight()).andAnswer(() -> height).anyTimes();
        EasyMock.expect(render.createFont(EasyMock.anyString())).andReturn(font).anyTimes();
        EasyMock.expect(render.createImage(EasyMock.anyString(), EasyMock.anyBoolean())).andReturn(image).anyTimes();
        SoundDevice sound = EasyMock.createNiceMock(SoundDevice.class);
        InputSystem input = EasyMock.createNiceMock(InputSystem.class);
        ScreenController controller = EasyMock.createNiceMock(ScreenController.class);
        EasyMock.replay(render, font, image, sound, input, controller);
        nifty = new Nifty(render, sound, input, () -> 1000L);
        nifty.loadStyleFile("nifty-illarion-style.xml");
        nifty.loadControlFile("nifty-default-controls.xml");
        ScreenBuilder builder = new ScreenBuilder("game", controller);
        LayerBuilder layer = new LayerBuilder("layer");
        layer.childLayoutCenter();
        PanelBuilder outer = new PanelBuilder("outer");
        outer.childLayoutVertical();
        PanelBuilder inner = new PanelBuilder("inner");
        inner.childLayoutVertical();
        ControlBuilder panel = new ControlBuilder("chatPanel", "scrollPanel");
        panel.width("600px");
        panel.height("180px");
        panel.style("nifty-chatlog");
        panel.parameter("vertical", "true");
        panel.parameter("horizontal", "false");
        panel.parameter("autoScroll", "off");
        panel.parameter("stepSizeY", "20");
        PanelBuilder log = new PanelBuilder("chatLog");
        log.childLayoutVertical();
        log.width("574px");
        panel.panel(log);
        inner.control(panel);
        outer.panel(inner);
        layer.panel(outer);
        builder.layer(layer);
        Screen screen = builder.build(nifty);
        nifty.addScreen("game", screen);
        nifty.gotoScreen("game");
        scroll = screen.findNiftyControl("chatPanel", ScrollPanel.class);
        content = screen.findElementById("chatLog");
        // Exercise real chat/layout code without constructing the unrelated translation service.
        handler = Whitebox.newInstance(GUIChatHandler.class);
        Whitebox.setInternalState(handler, "nifty", nifty);
        Whitebox.setInternalState(handler, "screen", screen);
        Whitebox.setInternalState(handler, "chatLog", scroll);
        Whitebox.setInternalState(handler, "chatLineCounter", new AtomicLong());
        game = Whitebox.newInstance(Game.class);
        Whitebox.setInternalState(game, "nifty", nifty);
        Whitebox.setInternalState(game, "activeListener", -1);
        container = EasyMock.createNiceMock(GameContainer.class);
        GameGui gui = EasyMock.createNiceMock(GameGui.class);
        EasyMock.expect(gui.getChatGui()).andReturn(handler).anyTimes();
        Config config = EasyMock.createNiceMock(Config.class);
        EasyMock.replay(container, gui, config);
        worldInitialised = true;
        PowerMock.mockStatic(World.class);
        EasyMock.expect(World.isInitDone()).andAnswer(() -> worldInitialised).anyTimes();
        EasyMock.expect(World.getGameGui()).andReturn(gui).anyTimes();
        PowerMock.mockStatic(IllaClient.class);
        EasyMock.expect(IllaClient.getCfg()).andReturn(config).anyTimes();
        PowerMock.replay(World.class, IllaClient.class);

        appendMessages(60);
        finishLayout();
        assertTrue(scroll.getVerticalPos() > 200, "The fixture must have a scrollable history");
        scroll.setVerticalPos(200);
    }

    private int textWidth() {
        return ((String) EasyMock.getCurrentArguments()[0]).length() * 8;
    }

    @Test
    public void noNewMessageKeepsReadingPosition() throws Exception {
        finishLayout();
        assertEquals(scroll.getVerticalPos(), 200f);
    }

    @Test
    public void newMessageKeepsReadingPosition() throws Exception {
        append("Another message");
        finishLayout();
        assertEquals(scroll.getVerticalPos(), 200f);
    }

    @Test
    public void newMessagesFollowTheBottom() throws Exception {
        scrollToBottom();
        float previous = scroll.getVerticalPos();
        appendMessages(3);
        finishLayout();
        assertTrue(scroll.getVerticalPos() > previous);
        assertAtBottom();
    }

    @Test
    public void scrollingBackToTheBottomResumesFollowing() throws Exception {
        append("Do not follow yet");
        finishLayout();
        assertEquals(scroll.getVerticalPos(), 200f);
        scrollToBottom();
        append("Follow this message");
        finishLayout();
        assertAtBottom();
    }

    @Test
    public void batchOfNewMessagesKeepsReadingPosition() throws Exception {
        appendMessages(20);
        finishLayout();
        assertEquals(scroll.getVerticalPos(), 200f);
    }

    @Test
    public void pruningOldEntriesKeepsTheSameMessageVisible() throws Exception {
        Element anchor = content.getChildren().get(60);
        scroll.setVerticalPos(anchor.getY() - content.getY() + 5);
        float previous = relativePosition(anchor);
        appendMessages(150);
        finishLayout();
        assertEquals(content.getChildrenCount(), 400);
        assertEquals(relativePosition(anchor), previous);
    }

    @Test
    public void expiredReadingAnchorFallsBackToOldestRemainingMessage() throws Exception {
        scroll.setVerticalPos(0);
        appendMessages(150);
        finishLayout();
        assertEquals(content.getChildrenCount(), 400);
        assertEquals(scroll.getVerticalPos(), 0f);
    }

    @Test
    public void pruningWhileAtTheBottomKeepsFollowing() throws Exception {
        scrollToBottom();
        appendMessages(150);
        finishLayout();
        assertEquals(content.getChildrenCount(), 400);
        assertAtBottom();
    }

    @Test
    public void translationLayoutAboveTheReaderKeepsTheSameMessageVisible() throws Exception {
        Element anchor = content.getChildren().get(60);
        scroll.setVerticalPos(anchor.getY() - content.getY() + 5);
        float previous = relativePosition(anchor);
        Whitebox.invokeMethod(handler, "rememberChatPosition");
        Element translation = content.getChildren().get(1);
        translation.setMarginTop(SizeValue.def());
        translation.setVisible(true);
        translation.getNiftyControl(Label.class).setText("A translation inserted above the current reading position.");
        Whitebox.setInternalState(handler, "dirty", true);
        finishLayout();
        assertEquals(relativePosition(anchor), previous);
    }

    @Test
    public void windowResizeKeepsReadingPosition() {
        resize(1200, 800);
        assertEquals(scroll.getVerticalPos(), 200f);
        resize(800, 600);
        assertEquals(scroll.getVerticalPos(), 200f);
    }

    @Test
    public void windowResizeAtBottomKeepsFollowing() {
        scrollToBottom();
        resize(1200, 800);
        assertAtBottom();
    }

    @Test
    public void multipleResizeNotificationsBeforeRenderingKeepReadingPosition() {
        game.resize(container, 1100, 750);
        game.resize(container, 1200, 800);
        width = 1200;
        height = 800;
        game.render(container);
        assertEquals(scroll.getVerticalPos(), 200f);
        game.render(container);
        assertEquals(scroll.getVerticalPos(), 200f);
    }

    @Test
    public void rewrappingKeepsTheSameMessageVisible() {
        Element anchor = content.getChildren().get(60);
        scroll.setVerticalPos(anchor.getY() - content.getY() + 5);
        float previous = relativePosition(anchor);

        for (Element entry : content.getChildren()) {
            entry.setConstraintWidth(SizeValue.px(300));
        }

        resize(800, 600);
        assertEquals(relativePosition(anchor), previous);
    }

    @Test
    public void expandingAndCollapsingTheChatKeepsReadingPosition() throws Exception {
        Whitebox.invokeMethod(handler, "setHeightOfChatLog", SizeValue.px(500));
        assertEquals(scroll.getVerticalPos(), 200f);
        Whitebox.invokeMethod(handler, "setHeightOfChatLog", SizeValue.px(170));
        assertEquals(scroll.getVerticalPos(), 200f);
    }

    @Test
    public void expandingTheChatAtBottomKeepsFollowing() throws Exception {
        scrollToBottom();
        Whitebox.invokeMethod(handler, "setHeightOfChatLog", SizeValue.px(500));
        assertAtBottom();
    }

    @Test
    public void resizeWithNewMessagesKeepsReadingPosition() throws Exception {
        game.resize(container, 1200, 800);
        appendMessages(3);
        Whitebox.invokeMethod(handler, "cleanupChatLog");
        width = 1200;
        height = 800;
        game.render(container);
        assertEquals(scroll.getVerticalPos(), 200f);
    }

    @Test
    public void resizingBeforeWorldInitialisationDoesNotAccessChat() {
        worldInitialised = false;
        PowerMock.reset(World.class);
        EasyMock.expect(World.isInitDone()).andReturn(false).anyTimes();
        PowerMock.replay(World.class);
        resize(1200, 800);
        PowerMock.verify(World.class);
    }

    private void resize(int newWidth, int newHeight) {
        width = newWidth;
        height = newHeight;
        game.resize(container, width, height);
        game.render(container);
    }

    private float relativePosition(Element entry) {
        return entry.getY() - content.getY() - scroll.getVerticalPos();
    }

    private void appendMessages(int count) throws Exception {
        for (int i = 0; i < count; i++) {
            append("Message " + i + ": this is a chat entry long enough to have a real text layout.");
        }
    }

    private void append(String text) throws Exception {
        Whitebox.invokeMethod(handler, "addChatLogText", text, Color.WHITE);
    }

    private void finishLayout() throws Exception {
        Whitebox.invokeMethod(handler, "cleanupChatLog");
        nifty.update();
        nifty.render(false);
    }

    private void scrollToBottom() {
        scroll.setAutoScroll(ScrollPanel.AutoScroll.BOTTOM);
        scroll.setAutoScroll(ScrollPanel.AutoScroll.OFF);
    }

    private void assertAtBottom() {
        Scrollbar bar = scroll.getElement().findNiftyControl("#nifty-internal-vertical-scrollbar", Scrollbar.class);
        assertEquals(scroll.getVerticalPos(), bar.getWorldMax() - bar.getWorldPageSize());
    }
}
