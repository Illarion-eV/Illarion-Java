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

import illarion.client.graphics.AnimationManager;
import illarion.client.graphics.MapDisplayManager;
import illarion.client.graphics.MoveAnimation;
import illarion.client.util.UpdateTaskManager;
import illarion.client.world.Char;
import illarion.client.world.CharMovementMode;
import illarion.client.world.GameMap;
import illarion.client.world.Player;
import illarion.client.world.World;
import illarion.common.types.ServerCoordinate;
import org.easymock.EasyMock;
import org.illarion.engine.GameContainer;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@PrepareForTest({World.class, Player.class, Char.class, GameMap.class, MapDisplayManager.class})
@PowerMockIgnore({"javax.management.*", "javax.xml.parsers.*", "com.sun.org.apache.xerces.internal.jaxp.*",
        "ch.qos.logback.*", "org.slf4j.*"})
public class MoveAnimatorTest {
    private static final ServerCoordinate FIRST_STEP = new ServerCoordinate(359, 875, 0);
    private static final ServerCoordinate SECOND_STEP = new ServerCoordinate(358, 877, 1);
    private MoveAnimator animator;
    private MoveAnimation animation;
    private AnimationManager animations;
    private UpdateTaskManager updates;
    private GameContainer container;
    private ServerCoordinate location;
    private int readyCount;

    @ObjectFactory
    public IObjectFactory createObjectFactory() {
        return new PowerMockObjectFactory();
    }

    @BeforeMethod
    public void setUp() {
        location = new ServerCoordinate(359, 876, 0);
        readyCount = 0;
        updates = new UpdateTaskManager();
        animations = new AnimationManager();
        container = EasyMock.createNiceMock(GameContainer.class);
        Player player = PowerMock.createNiceMock(Player.class);
        Char character = PowerMock.createNiceMock(Char.class);
        GameMap map = PowerMock.createNiceMock(GameMap.class);
        MapDisplayManager display = PowerMock.createNiceMock(MapDisplayManager.class);
        Movement movement = EasyMock.createNiceMock(Movement.class);
        EasyMock.expect(movement.getPlayer()).andReturn(player).anyTimes();
        movement.reportReadyForNextStep();
        EasyMock.expectLastCall().andAnswer(() -> {
            readyCount++;
            return null;
        }).anyTimes();
        EasyMock.expect(player.getCharacter()).andReturn(character).anyTimes();
        EasyMock.expect(player.getLocation()).andAnswer(() -> location).anyTimes();
        EasyMock.expect(character.getLocation()).andAnswer(() -> location).anyTimes();
        player.updateLocation(EasyMock.anyObject(ServerCoordinate.class));
        EasyMock.expectLastCall().andAnswer(() -> {
            location = (ServerCoordinate) EasyMock.getCurrentArguments()[0];
            return null;
        }).anyTimes();
        EasyMock.replay(container, movement, player, character, map, display);
        PowerMock.mockStatic(World.class);
        EasyMock.expect(World.getUpdateTaskManager()).andReturn(updates).anyTimes();
        EasyMock.expect(World.getAnimationManager()).andReturn(animations).anyTimes();
        EasyMock.expect(World.getMap()).andReturn(map).anyTimes();
        EasyMock.expect(World.getMapDisplay()).andReturn(display).anyTimes();
        PowerMock.replay(World.class);
        animation = new MoveAnimation(null);
        animator = new MoveAnimator(movement, animation);
        animation.addTarget(animator, false);
    }

    @Test
    public void confirmationWithoutPredictionCompletesMovement() {
        confirm(SECOND_STEP);
        finishMove();
        assertEquals(location, SECOND_STEP);
        assertEquals(readyCount, 1);
    }

    @Test
    public void warpBeforePredictionStartsDoesNotLoseTheNextConfirmation() {
        predict(FIRST_STEP);
        confirm(FIRST_STEP);
        animator.cancelAll();
        confirm(SECOND_STEP);
        finishMove();
        assertEquals(location, SECOND_STEP);
        assertEquals(readyCount, 1);
    }

    @Test
    public void warpDuringUnconfirmedAnimationAllowsTheNextConfirmedMove() {
        predict(FIRST_STEP);
        updates.onUpdateGame(container, 0);
        assertTrue(animation.isRunning());
        animator.cancelAll();
        assertFalse(animation.isRunning());
        confirm(SECOND_STEP);
        finishMove();
        assertEquals(location, SECOND_STEP);
        assertEquals(readyCount, 1);
    }

    @Test
    public void stoppingAtTheEndDoesNotRequestAnotherStepDuringCancellation() {
        confirm(FIRST_STEP);
        updates.onUpdateGame(container, 0);
        animation.setDuration(0);
        animator.cancelAll();
        assertEquals(readyCount, 0, "The synchronous stop callback must not request another movement");
    }

    @Test
    public void repeatedCancellationDoesNotLeaveQueuedMovement() {
        predict(FIRST_STEP);
        confirm(FIRST_STEP);
        animator.cancelAll();
        animator.cancelAll();
        updates.onUpdateGame(container, 0);
        assertEquals(location, new ServerCoordinate(359, 876, 0));
        assertFalse(animation.isRunning());
        assertEquals(readyCount, 0);
    }

    @Test
    public void confirmationAfterCancelledPredictionIsQueuedAgain() {
        predict(FIRST_STEP);
        animator.cancelMove(location);
        readyCount = 0;
        confirm(SECOND_STEP);
        finishMove();
        assertEquals(location, SECOND_STEP);
        assertEquals(readyCount, 1);
    }

    @Test
    public void completedPredictionWaitsForConfirmation() {
        predict(FIRST_STEP);
        finishMove();
        assertEquals(readyCount, 0);
        confirm(FIRST_STEP);
        assertEquals(readyCount, 1);
    }

    @Test
    public void confirmationCanReplaceTheQueuedPrediction() {
        predict(FIRST_STEP);
        confirm(SECOND_STEP);
        finishMove();
        assertEquals(location, SECOND_STEP);
        assertEquals(readyCount, 1);
    }

    private void predict(ServerCoordinate target) {
        animator.scheduleEarlyMove(CharMovementMode.Walk, target, 400);
    }

    private void confirm(ServerCoordinate target) {
        animator.confirmMove(CharMovementMode.Walk, target, 400);
    }

    private void finishMove() {
        updates.onUpdateGame(container, 0);
        animations.animate(0);
        animations.animate(401);
    }
}
