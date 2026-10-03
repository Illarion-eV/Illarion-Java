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
package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.EffectBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.spi.input.InputSystem;
import de.lessvoid.nifty.spi.render.RenderDevice;
import de.lessvoid.nifty.spi.sound.SoundDevice;
import illarion.client.IllaClient;
import illarion.client.gui.controller.game.InformHandler;
import illarion.client.gui.controller.game.UpdatableHandler;
import illarion.client.net.server.InformMsg;
import illarion.client.net.server.ServerReplyResult;
import illarion.client.util.UpdateTaskManager;
import illarion.client.world.World;
import illarion.common.config.Config;
import org.easymock.EasyMock;
import org.illarion.engine.GameContainer;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.powermock.reflect.Whitebox;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@PrepareForTest({World.class, IllaClient.class})
@PowerMockIgnore({"javax.management.*", "javax.xml.parsers.*", "com.sun.org.apache.xerces.internal.jaxp.*",
        "ch.qos.logback.*", "org.slf4j.*", "de.lessvoid.nifty.*"})
public class GameScreenControllerTest {
    private GameScreenController controller;
    private Collection<ScreenController> children;
    private Collection<UpdatableHandler> updaters;
    private InformHandler informs;
    private UpdateTaskManager updates;
    private GameContainer container;
    private Nifty nifty;
    private Screen game;
    private long time;

    @ObjectFactory
    public IObjectFactory createObjectFactory() {
        return new PowerMockObjectFactory();
    }

    @BeforeMethod
    public void setUp() {
        // Keep the real lifecycle methods, without constructing unrelated world-dependent handlers.
        controller = Whitebox.newInstance(GameScreenController.class);
        children = new ArrayList<>();
        updaters = new ArrayList<>();
        Whitebox.setInternalState(controller, "childControllers", children);
        Whitebox.setInternalState(controller, "childUpdateControllers", updaters);
        informs = new InformHandler();
        children.add(informs);
        Whitebox.setInternalState(controller, "informHandler", informs);
        updates = new UpdateTaskManager();
        container = EasyMock.createNiceMock(GameContainer.class);
        Config config = EasyMock.createNiceMock(Config.class);
        EasyMock.replay(container, config);

        PowerMock.mockStatic(World.class);
        EasyMock.expect(World.getGameGui()).andReturn(controller).anyTimes();
        EasyMock.expect(World.getUpdateTaskManager()).andAnswer(() -> updates).anyTimes();
        World.cleanEnvironment();
        EasyMock.expectLastCall().andAnswer(() -> {
            updates = new UpdateTaskManager();
            return null;
        }).anyTimes();
        PowerMock.mockStatic(IllaClient.class);
        EasyMock.expect(IllaClient.getCfg()).andReturn(config).anyTimes();
        PowerMock.replay(World.class, IllaClient.class);
    }

    @Test
    public void bindingDoesNotMakeTheScreenReady() {
        createScreens();
        controller.bind(nifty, game);
        assertFalse(controller.isReady());
    }

    @Test
    public void childrenStartBeforeTheScreenBecomesReady() {
        ScreenController child = EasyMock.createMock(ScreenController.class);
        child.onStartScreen();
        EasyMock.expectLastCall().andAnswer(() -> {
            assertFalse(controller.isReady());
            return null;
        });
        EasyMock.replay(child);
        children.add(child);
        controller.onStartScreen();
        assertTrue(controller.isReady());
        EasyMock.verify(child);
    }

    @Test
    public void leavingRevokesReadinessBeforeChildrenStop() {
        ScreenController child = EasyMock.createMock(ScreenController.class);
        child.onEndScreen();
        EasyMock.expectLastCall().andAnswer(() -> {
            assertFalse(controller.isReady());
            return null;
        });
        EasyMock.replay(child);
        createScreens();
        controller.bind(nifty, game);
        controller.onStartScreen();
        children.add(child);
        controller.onEndScreen();
        assertFalse(controller.isReady());
        EasyMock.verify(child);
    }

    @Test
    public void childUpdatesOnlyRunWhileTheScreenIsReady() {
        UpdatableHandler updater = EasyMock.createMock(UpdatableHandler.class);
        updater.update(container, 20);
        EasyMock.expectLastCall().once();
        EasyMock.replay(updater);
        updaters.add(updater);
        controller.onUpdateGame(container, 20);
        controller.onStartScreen();
        controller.onUpdateGame(container, 20);
        controller.onEndScreen();
        controller.onUpdateGame(container, 20);
        EasyMock.verify(updater);
    }

    @Test
    public void informReplyWaitsForTheSecondScreenStart() {
        createScreens();
        login();
        nifty.gotoScreen("login");
        frames(300);
        nifty.gotoScreen("gamescreen");
        frames(20);
        InformMsg reply = new InformMsg();
        Whitebox.setInternalState(reply, "informText", "Welcome");
        Whitebox.setInternalState(reply, "informType", 100);
        assertEquals(reply.execute(), ServerReplyResult.Reschedule);
        frames(300);
        assertTrue(controller.isReady());
        assertEquals(reply.execute(), ServerReplyResult.Success);
    }

    @Test
    public void welcomeQueuedDuringEachLoginIsRemoved() {
        createScreens();

        for (int session = 0; session < 2; session++) {
            nifty.gotoScreen("gamescreen");
            frames(20);
            assertEquals(nifty.getCurrentScreen().getScreenId(), "login");
            queueWelcome();
            frames(20);
            assertTrue(messagePanel().getChildren().isEmpty(), "Do not build messages during the login fade");
            frames(300);
            assertNotNull(game.findElementById("welcome"));
            frames(2000);
            assertTrue(messagePanel().getChildren().isEmpty(), "The welcome message must finish hiding");
            nifty.gotoScreen("login");
            frames(300);
        }
    }

    @Test
    public void welcomeAfterScreenStartIsRemoved() {
        createScreens();
        login();
        queueWelcome();
        frames(20);
        assertNotNull(game.findElementById("welcome"));
        frames(2000);
        assertTrue(messagePanel().getChildren().isEmpty());
    }

    private void createScreens() {
        time = 1000;
        RenderDevice render = EasyMock.createNiceMock(RenderDevice.class);
        EasyMock.expect(render.getWidth()).andReturn(800).anyTimes();
        EasyMock.expect(render.getHeight()).andReturn(600).anyTimes();
        SoundDevice sound = EasyMock.createNiceMock(SoundDevice.class);
        InputSystem input = EasyMock.createNiceMock(InputSystem.class);
        ScreenController loginController = EasyMock.createNiceMock(ScreenController.class);
        EasyMock.replay(render, sound, input, loginController);
        nifty = new Nifty(render, sound, input, () -> time);

        ScreenBuilder login = new ScreenBuilder("login", loginController);
        LayerBuilder loginLayer = new LayerBuilder("loginLayer");
        loginLayer.childLayoutCenter();
        EffectBuilder fade = new EffectBuilder("fade");
        fade.length(200);
        fade.effectParameter("start", "#ff");
        fade.effectParameter("end", "#00");
        loginLayer.onEndScreenEffect(fade);
        login.layer(loginLayer);
        nifty.addScreen("login", login.build(nifty));

        ScreenBuilder gameBuilder = new ScreenBuilder("gamescreen", controller);
        LayerBuilder gameLayer = new LayerBuilder("gameLayer");
        gameLayer.childLayoutVertical();
        PanelBuilder panel = new PanelBuilder("scriptMessagePanel");
        panel.childLayoutVertical();
        panel.width("100%");
        panel.height("100%");
        gameLayer.panel(panel);
        gameBuilder.layer(gameLayer);
        game = gameBuilder.build(nifty);
        nifty.addScreen("gamescreen", game);
        nifty.gotoScreen("login");
        frames(300);
    }

    private void login() {
        nifty.gotoScreen("gamescreen");
        frames(300);
    }

    private void frames(int milliseconds) {
        for (int elapsed = 0; elapsed < milliseconds; elapsed += 20) {
            time += 20;
            nifty.update();

            if (controller.isReady()) {
                updates.onUpdateGame(container, 20);
            }

            nifty.render(false);
        }
    }

    private Element messagePanel() {
        return game.findElementById("scriptMessagePanel");
    }

    private void queueWelcome() {
        PanelBuilder message = new PanelBuilder("welcome");
        message.childLayoutCenter();
        message.width("100px");
        message.height("20px");
        EffectBuilder hide = new EffectBuilder("hide");
        hide.startDelay(500);
        message.onHideEffect(hide);
        informs.showInform(message, messagePanel(), messagePanel());
    }
}
