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
package illarion.client.world.movement;

import illarion.client.graphics.MapDisplayManager;
import illarion.client.graphics.MoveAnimation;
import illarion.client.net.NetComm;
import illarion.client.net.client.AbstractCommand;
import illarion.client.net.client.MoveCmd;
import illarion.client.util.UpdateTaskManager;
import illarion.client.world.Char;
import illarion.client.world.CharMovementMode;
import illarion.client.world.Player;
import illarion.client.world.World;
import illarion.common.types.CharacterId;
import illarion.common.types.Direction;
import illarion.common.types.ServerCoordinate;
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

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@PrepareForTest({World.class, Player.class, Char.class, MapDisplayManager.class, NetComm.class})
@PowerMockIgnore({"javax.management.*", "javax.xml.parsers.*", "com.sun.org.apache.xerces.internal.jaxp.*",
        "ch.qos.logback.*", "org.slf4j.*"})
public class MovementTest {
    private static final ServerCoordinate ORIGIN = new ServerCoordinate(359, 876, 0);
    private static final ServerCoordinate FIRST_STEP = new ServerCoordinate(359, 875, 0);
    private static final ServerCoordinate WARP = new ServerCoordinate(359, 878, 1);
    private static final ServerCoordinate SECOND_STEP = new ServerCoordinate(358, 877, 1);
    private Movement movement;
    private MoveAnimator animator;
    private UpdateTaskManager updates;
    private Queue<Runnable> pendingTasks;
    private GameContainer container;
    private ServerCoordinate location;
    private Direction direction;
    private int commandsSent;

    @ObjectFactory
    public IObjectFactory createObjectFactory() {
        return new PowerMockObjectFactory();
    }

    @BeforeMethod
    public void setUp() {
        location = ORIGIN;
        direction = Direction.North;
        commandsSent = 0;
        updates = new UpdateTaskManager();
        pendingTasks = new ConcurrentLinkedQueue<>();
        container = EasyMock.createNiceMock(GameContainer.class);
        Player player = PowerMock.createNiceMock(Player.class);
        Char character = PowerMock.createNiceMock(Char.class);
        MapDisplayManager display = PowerMock.createNiceMock(MapDisplayManager.class);
        NetComm network = PowerMock.createNiceMock(NetComm.class);
        MoveAnimation animation = EasyMock.createNiceMock(MoveAnimation.class);
        ExecutorService executor = EasyMock.createNiceMock(ExecutorService.class);
        EasyMock.expect(executor.submit(EasyMock.anyObject(Runnable.class))).andAnswer(() -> {
            pendingTasks.add((Runnable) EasyMock.getCurrentArguments()[0]);
            return CompletableFuture.completedFuture(null);
        }).anyTimes();
        EasyMock.expect(player.getCharacter()).andReturn(character).anyTimes();
        EasyMock.expect(player.getLocation()).andAnswer(() -> location).anyTimes();
        EasyMock.expect(character.getLocation()).andAnswer(() -> location).anyTimes();
        player.setLocation(EasyMock.anyObject(ServerCoordinate.class));
        EasyMock.expectLastCall().andAnswer(this::updateLocation).anyTimes();
        player.updateLocation(EasyMock.anyObject(ServerCoordinate.class));
        EasyMock.expectLastCall().andAnswer(this::updateLocation).anyTimes();
        character.setDirection(EasyMock.anyObject(Direction.class));
        EasyMock.expectLastCall().andAnswer(() -> {
            direction = (Direction) EasyMock.getCurrentArguments()[0];
            return null;
        }).anyTimes();
        network.sendCommand(EasyMock.anyObject(AbstractCommand.class));
        EasyMock.expectLastCall().andAnswer(() -> {
            commandsSent++;
            return null;
        }).anyTimes();
        EasyMock.replay(container, player, character, display, network, animation, executor);

        movement = Whitebox.newInstance(Movement.class);
        Whitebox.setInternalState(movement, "player", player);
        Whitebox.setInternalState(movement, "playerLocation", ORIGIN);
        Whitebox.setInternalState(movement, "executorService", executor);
        Whitebox.setInternalState(movement, "moveAnimation", animation);
        animator = new MoveAnimator(movement, animation);
        Whitebox.setInternalState(movement, "animator", animator);
        PowerMock.mockStatic(World.class);
        EasyMock.expect(World.getPlayer()).andReturn(player).anyTimes();
        EasyMock.expect(World.getUpdateTaskManager()).andReturn(updates).anyTimes();
        EasyMock.expect(World.getMapDisplay()).andReturn(display).anyTimes();
        EasyMock.expect(World.getNet()).andReturn(network).anyTimes();
        PowerMock.replay(World.class);
    }

    private Object updateLocation() {
        location = (ServerCoordinate) EasyMock.getCurrentArguments()[0];
        return null;
    }

    @Test
    public void queuedConfirmationBeforeWarpCannotRestoreTheOldLocation() {
        confirm(FIRST_STEP);
        movement.executeServerLocation(WARP);
        drainTasks();
        updates.onUpdateGame(container, 0);
        assertEquals(location, WARP);
        assertEquals(movement.getServerLocation(), WARP);
    }

    @Test
    public void queuedTurnBeforeWarpIsDiscarded() {
        movement.executeServerRespTurn(Direction.South);
        movement.executeServerLocation(WARP);
        drainTasks();
        updates.onUpdateGame(container, 0);
        assertEquals(direction, Direction.North);
    }

    @Test
    public void deferredCancellationBeforeWarpCannotResetTheLocation() {
        animator.cancelMove(ORIGIN);
        movement.executeServerLocation(WARP);
        updates.onUpdateGame(container, 0);
        assertEquals(location, WARP);
    }

    @Test
    public void postWarpConfirmationIsStillExecuted() {
        confirm(FIRST_STEP);
        movement.executeServerLocation(WARP);
        confirm(SECOND_STEP);
        drainTasks();
        updates.onUpdateGame(container, 0);
        drainTasks();
        assertEquals(location, SECOND_STEP);
        assertEquals(movement.getServerLocation(), SECOND_STEP);
        assertFalse(Whitebox.<Boolean>getInternalState(movement, "stepInProgress"));
    }

    @Test
    public void warpUpdatesLogicalLocationBeforeReturning() {
        movement.executeServerLocation(WARP);
        assertEquals(location, WARP);
        assertEquals(movement.getServerLocation(), WARP);
    }

    @Test
    public void repeatedWarpsDiscardOnlyEarlierWork() {
        movement.executeServerLocation(WARP);
        confirm(SECOND_STEP);
        movement.executeServerLocation(ORIGIN);
        confirm(FIRST_STEP);
        drainTasks();
        updates.onUpdateGame(container, 0);
        assertEquals(location, FIRST_STEP);
        assertEquals(movement.getServerLocation(), FIRST_STEP);
    }

    @Test
    public void queuedRetryDoesNotResendThePreWarpCommand() {
        setPreviousCommand();
        movement.executeServerRespMoveTooEarly();
        movement.executeServerLocation(WARP);
        drainTasks();
        assertEquals(commandsSent, 0);
    }

    @Test
    public void retryAfterWarpHasNoPreWarpCommandToResend() {
        setPreviousCommand();
        movement.executeServerLocation(WARP);
        movement.executeServerRespMoveTooEarly();
        drainTasks();
        assertEquals(commandsSent, 0);
    }

    @Test
    public void warpDoesNotDisengageANewlyActivatedHandler() {
        MovementHandler previous = createHandler();
        MovementHandler next = createHandler();
        movement.activate(previous);
        movement.executeServerLocation(WARP);
        movement.activate(next);
        updates.onUpdateGame(container, 0);
        drainTasks();
        assertTrue(movement.isActive(next));
    }

    @Test
    public void shutdownInvalidatesPendingMovementTasks() {
        confirm(FIRST_STEP);
        movement.shutdown();
        drainTasks();
        updates.onUpdateGame(container, 0);
        assertEquals(location, ORIGIN);
    }

    @Test
    public void warpDoesNotWaitForPathFindingOrApplyItsObsoleteResult() throws Exception {
        CountDownLatch calculating = new CountDownLatch(1);
        CountDownLatch finishCalculation = new CountDownLatch(1);
        AtomicInteger actions = new AtomicInteger();
        MovementHandler handler = new AbstractMovementHandler(movement) {
            @Override
            public StepData getNextStep(ServerCoordinate currentLocation) {
                assertEquals(currentLocation, ORIGIN);
                calculating.countDown();

                try {
                    assertTrue(finishCalculation.await(5, TimeUnit.SECONDS), "The calculation must be released");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(exception);
                }

                return new DefaultStepData(CharMovementMode.None, null, actions::incrementAndGet);
            }
        };
        movement.activate(handler);
        FutureTask<Void> calculation = new FutureTask<>(pendingTasks.remove(), null);
        Thread calculationThread = new Thread(calculation, "test-path-calculation");
        calculationThread.setDaemon(true);
        calculationThread.start();
        FutureTask<Void> warp = new FutureTask<>(() -> movement.executeServerLocation(WARP), null);
        Thread warpThread = new Thread(warp, "test-warp");
        warpThread.setDaemon(true);

        try {
            assertTrue(calculating.await(5, TimeUnit.SECONDS));
            warpThread.start();
            warp.get(2, TimeUnit.SECONDS);
        } finally {
            finishCalculation.countDown();
            calculation.get(5, TimeUnit.SECONDS);
            warp.get(5, TimeUnit.SECONDS);
        }

        drainTasks();
        assertEquals(actions.get(), 0, "Do not apply a path result calculated before the warp");
        assertEquals(location, WARP);
    }

    @Test
    public void predictionCorrectionDoesNotDiscardLaterServerConfirmations() {
        MovingTask prediction = new MovingTask(animator, CharMovementMode.Walk, ORIGIN, 400);
        Whitebox.setInternalState(prediction, "executed", true);
        Whitebox.setInternalState(animator, "uncomfirmedMoveTask", prediction);
        MoveAnimation animation = Whitebox.getInternalState(movement, "moveAnimation");
        Whitebox.setInternalState(animation, "running", true);
        confirm(FIRST_STEP);
        confirm(SECOND_STEP);
        drainTasks();
        updates.onUpdateGame(container, 0);
        assertEquals(location, SECOND_STEP);
        assertEquals(movement.getServerLocation(), SECOND_STEP);
    }

    private MovementHandler createHandler() {
        return new AbstractMovementHandler(movement) {
            @Override
            public StepData getNextStep(ServerCoordinate currentLocation) {
                return null;
            }
        };
    }

    private void setPreviousCommand() {
        MoveCmd command = new MoveCmd(new CharacterId(1), CharMovementMode.Walk, Direction.North);
        Whitebox.setInternalState(movement, "lastSendMoveCommand", command);
    }

    private void confirm(ServerCoordinate target) {
        movement.executeServerRespMove(CharMovementMode.None, target, 0);
    }

    private void drainTasks() {
        while (!pendingTasks.isEmpty()) {
            pendingTasks.remove().run();
        }
    }
}
