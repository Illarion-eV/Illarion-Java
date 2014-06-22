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
package illarion.client.input;

import illarion.client.IllaClient;
import illarion.client.world.World;
import illarion.common.gui.AbstractMultiActionHelper;
import org.bushe.swing.event.EventBus;
import org.illarion.engine.input.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Set;

/**
 * This class is used to receive and forward all user input.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InputReceiver implements InputListener {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(InputReceiver.class);
    private static final int MOVE_KEY = 1;

    /**
     * This is the multi click helper that is used to detect single and multiple clicks on the game map.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class ButtonMultiClickHelper extends AbstractMultiActionHelper {
        /**
         * The x coordinate of the last reported click.
         */
        private int x;

        /**
         * The Y coordinate of the last reported click.
         */
        private int y;

        /**
         * The key that is used.
         */
        private Button key;

        /**
         * The constructor that sets the used timeout interval to the system default double click interval.
         */
        private ButtonMultiClickHelper() {
            super(IllaClient.getCfg().getInteger("doubleClickInterval"), 2);
        }

        /**
         * Update the data that is needed to report the state of the last click properly.
         *
         * @param mouseKey the key that was clicked
         * @param posX the x coordinate where the click happened
         * @param posY the y coordinate where the click happened
         */
        public void setInputData(@Nonnull Button mouseKey, int posX, int posY) {
            x = posX;
            y = posY;
            key = mouseKey;
        }

        @Override
        public void executeAction(int count) {
            switch (count) {
                case 1:
                    if (log.isDebugEnabled()) {
                        log.debug("Raising single click event for {} button at {} {}", key.name(), x, y);
                    }
                    EventBus.publish(new ClickOnMapEvent(key, x, y));
                    break;
                case 2:
                    if (log.isDebugEnabled()) {
                        log.debug("Raising double click event for {} button at {} {}", key.name(), x, y);
                    }
                    World.getMapDisplay().getGameScene().publishEvent(new DoubleClickOnMapEvent(key, x, y));
                    break;
            }
        }
    }

    /**
     * This class is used as helper for the point at events.
     */
    private static final class PointAtHelper extends AbstractMultiActionHelper {
        /**
         * The x coordinate of the last reported click.
         */
        private int x;

        /**
         * The Y coordinate of the last reported click.
         */
        private int y;

        /**
         * Default constructor.
         */
        private PointAtHelper() {
            super(50);
        }

        /**
         * Update the data that is needed to report the state of the last move properly.
         *
         * @param posX the x coordinate where the click happened
         * @param posY the y coordinate where the click happened
         */
        public void setInputData(int posX, int posY) {
            x = posX;
            y = posY;
        }

        @Override
        public void executeAction(int count) {
            EventBus.publish(new PointOnMapEvent(x, y));
        }
    }

    /**
     * The topic that is in general used to publish input events.
     */
    @Nonnull
    public static final String EB_TOPIC = "InputEvent";

    /**
     * The key mapper stores the keep-action assignments of the client.
     */
    @Nonnull
    private final KeyMapper keyMapper;

    /**
     * The instance of the button multi-click helper that is used in this instance of the input receiver.
     */
    @Nonnull
    private final ButtonMultiClickHelper buttonMultiClickHelper = new ButtonMultiClickHelper();

    /**
     * The instance of the point at helper used by this instance of the input receiver.
     */
    @Nonnull
    private final PointAtHelper pointAtHelper = new PointAtHelper();

    /**
     * The input engine.
     */
    @Nonnull
    private final Input input;

    /**
     * In case this value is set {@code true} this receiver is active and working. If not will discard all events
     * received.
     */
    private boolean enabled;

    @Nonnull
    private final Set<Button> buttonDownReceived;

    @Nonnull
    private final Set<Button> buttonDownDragged;

    /**
     * Create a new instance of the input receiver.
     *
     * @param input the input system this class is receiving its data from
     */
    public InputReceiver(@Nonnull Input input) {
        this.input = input;
        buttonDownReceived = EnumSet.noneOf(Button.class);
        buttonDownDragged = EnumSet.noneOf(Button.class);
        enabled = false;
        keyMapper = new KeyMapper(input);
    }

    /**
     * Set the enabled flag of this input receiver.
     *
     * @param value the new enabled flag
     */
    public void setEnabled(boolean value) {
        enabled = value;
    }

    @Override
    public void keyDown(@Nonnull Key key) {
        if (enabled) {
            keyMapper.handleKeyPressedInput(key);
        }
    }

    @Override
    public void keyUp(@Nonnull Key key) {
        if (enabled) {
            keyMapper.handleKeyReleasedInput(key);
        }
    }

    @Override
    public void keyTyped(char character) {
        // nothing
    }

    @Override
    public void buttonDown(int mouseX, int mouseY, @Nonnull Button button) {
        if (enabled) {
            buttonDownDragged.remove(button);
            buttonDownReceived.add(button);
            log.debug("Received {} mouse button down at {}, {}", button, mouseX, mouseY);
        }
    }

    @Override
    public void buttonUp(int mouseX, int mouseY, @Nonnull Button button) {
        if (enabled) {
            if (buttonDownReceived.remove(button)) {
                if (!buttonDownDragged.contains(button)) {
                    buttonMultiClickHelper.setInputData(button, mouseX, mouseY);
                    //buttonMultiClickHelper.pulse();
                }
                log.debug("Received {} mouse button up at {}, {}", button, mouseX, mouseY);
                World.getPlayer().getMovementHandler().getTargetMouseMovementHandler().disengage(true);
                World.getPlayer().getMovementHandler().getFollowMouseHandler().disengage(true);
                input.disableForwarding(ForwardingTarget.Mouse);
            } else {
                log.debug("Received {} mouse button up at {}, {} but skipped it.", button, mouseX, mouseY);
            }
        }
    }

    @Override
    public void buttonClicked(int mouseX, int mouseY, @Nonnull Button button, int count) {
        if (enabled) {
            if (!buttonDownReceived.contains(button) || buttonDownDragged.contains(button)) {
                log.debug("Received {} mouse clicked {} times at {}, {} but skipped it.", button, count, mouseX,
                          mouseY);
            } else {
                log.debug("Received {} mouse clicked {} times at {}, {}", button, count, mouseX, mouseY);
                switch (count) {
                    case 1:
                        World.getMapDisplay().getGameScene().publishEvent(new ClickOnMapEvent(button, mouseX, mouseY));
                        break;
                    case 2:
                        World.getMapDisplay().getGameScene()
                                .publishEvent(new DoubleClickOnMapEvent(button, mouseX, mouseY));
                        break;
                    default:
                        log.warn("Too many {} mouse clicks received: {}", button, count);
                }
            }
        }
    }

    @Override
    public void mouseMoved(int mouseX, int mouseY) {
        if (enabled) {
            pointAtHelper.setInputData(mouseX, mouseY);
            pointAtHelper.pulse();
            EventBus.publish(new MoveOnMapEvent(mouseX, mouseY));
        }
    }

    @Override
    public void mouseDragged(@Nonnull Button button, int fromX, int fromY, int toX, int toY) {
        if (enabled) {
            buttonMultiClickHelper.reset();
            if (buttonDownReceived.contains(button)) {
                buttonDownDragged.add(button);
                log.debug("Received {} mouse button dragged from {}, {} to {}, {}", button, fromX, fromY, toX, toY);
                EventBus.publish(new DragOnMapEvent(fromX, fromY, toX, toY, button, this));
            } else {
                log.debug("Received {} mouse button dragged from {}, {} to {}, {} but skipped it.", button, fromX,
                          fromY, toX, toY);
            }
        }
    }

    /**
     * Inform the input handler that the GUI took control over the last actions and the input receiver needs to reset.
     */
    public void guiTookControl() {
        log.debug("GUI is taking over all input. Receiver reset.");
        buttonDownReceived.clear();
        buttonDownDragged.clear();
    }

    @Override
    public void mouseWheelMoved(int mouseX, int mouseY, int delta) {
        if (enabled) {
            log.debug("Received mouse wheel turned by {} at {}, {}", delta, mouseX, mouseY);
        }
    }
}
