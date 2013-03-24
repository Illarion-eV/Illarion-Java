/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
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
package illarion.client.input;

import illarion.client.IllaClient;
import illarion.client.world.World;
import illarion.common.gui.AbstractMultiActionHelper;
import org.bushe.swing.event.EventBus;
import org.illarion.engine.input.*;

import javax.annotation.Nonnull;

/**
 * This class is used to receive and forward all user input.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InputReceiver implements InputListener {
    private static final int MOVE_KEY = 1;

    /**
     * This is the multi click helper that is used to detect single and multiple clicks on the game map.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class ButtonMultiClickHelper
            extends AbstractMultiActionHelper {
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
            super(IllaClient.getCfg().getInteger("doubleClickInterval"));
        }

        /**
         * Update the data that is needed to report the state of the last click properly.
         *
         * @param mouseKey the key that was clicked
         * @param posX     the x coordinate where the click happened
         * @param posY     the y coordinate where the click happened
         */
        public void setInputData(@Nonnull final Button mouseKey, final int posX, final int posY) {
            x = posX;
            y = posY;
            key = mouseKey;
        }

        @Override
        public void executeAction(final int count) {
            switch (count) {
                case 1:
                    EventBus.publish(new ClickOnMapEvent(key, x, y));
                    break;
                case 2:
                    EventBus.publish(new DoubleClickOnMapEvent(key, x, y));
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
        public void setInputData(final int posX, final int posY) {
            x = posX;
            y = posY;
        }

        @Override
        public void executeAction(final int count) {
            EventBus.publish(new PointOnMapEvent(x, y));
        }
    }

    /**
     * The topic that is in general used to publish input events.
     */
    public static final String EB_TOPIC = "InputEvent";

    /**
     * The key mapper stores the keep-action assignments of the client.
     */
    private final KeyMapper keyMapper = new KeyMapper();

    /**
     * The instance of the button multi-click helper that is used in this instance of the input receiver.
     */
    private final ButtonMultiClickHelper buttonMultiClickHelper = new ButtonMultiClickHelper();

    /**
     * The instance of the point at helper used by this instance of the input receiver.
     */
    private final PointAtHelper pointAtHelper = new PointAtHelper();

    private final Input input;

    /**
     * Create a new instance of the input receiver.
     *
     * @param input the input system this class is receiving its data from
     */
    public InputReceiver(final Input input) {
        this.input = input;
    }

    @Override
    public void keyDown(@Nonnull final Key key) {
        keyMapper.handleKeyInput(key);
    }

    @Override
    public void keyUp(@Nonnull final Key key) {
        // nothing
    }

    @Override
    public void keyTyped(final char character) {
        // nothing
    }

    @Override
    public void buttonDown(final int mouseX, final int mouseY, @Nonnull final Button button) {
        // nothing
    }

    @Override
    public void buttonUp(final int mouseX, final int mouseY, @Nonnull final Button button) {
        World.getPlayer().getMovementHandler().stopWalkTowards();
        input.disableForwarding(ForwardingTarget.Mouse);
    }

    @Override
    public void buttonClicked(final int mouseX, final int mouseY, @Nonnull final Button button, final int count) {
        buttonMultiClickHelper.setInputData(button, mouseX, mouseY);
        buttonMultiClickHelper.pulse();
    }

    @Override
    public void mouseMoved(final int mouseX, final int mouseY) {
        pointAtHelper.setInputData(mouseX, mouseY);
        pointAtHelper.pulse();
        EventBus.publish(new MoveOnMapEvent(mouseX, mouseY));
    }

    @Override
    public void mouseDragged(@Nonnull final Button button, final int fromX, final int fromY, final int toX, final int toY) {
        EventBus.publish(new DragOnMapEvent(fromX, fromY, toX, toY, button));
    }

    @Override
    public void mouseWheelMoved(final int mouseX, final int mouseY, final int delta) {
        // nothing
    }
}
