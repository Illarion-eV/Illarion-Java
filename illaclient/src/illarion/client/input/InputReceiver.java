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

import de.lessvoid.nifty.slick2d.NiftyInputForwarding;
import de.lessvoid.nifty.slick2d.input.ForwardingInputSystem;
import illarion.client.IllaClient;
import illarion.client.world.World;
import illarion.common.gui.AbstractMultiActionHelper;
import org.bushe.swing.event.EventBus;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.Input;
import org.newdawn.slick.util.InputAdapter;

/**
 * This class is used to receive and forward all user input.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InputReceiver
        extends InputAdapter {
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
        private int key;

        /**
         * The input forwarding system that is published along with the click events.
         */
        private ForwardingInputSystem fwdInputSystem;

        /**
         * The constructor that sets the used timeout interval to the system default double click interval.
         */
        private ButtonMultiClickHelper() {
            super(IllaClient.getCfg().getInteger("doubleClickInterval"));
        }

        /**
         * Update the data that is needed to report the state of the last click properly.
         *
         * @param mouseKey   the key that was clicked
         * @param posX       the x coordinate where the click happened
         * @param posY       the y coordinate where the click happened
         * @param forwarding the input forwarding system required to be published with the events
         */
        public void setInputData(final int mouseKey, final int posX, final int posY,
                                 final ForwardingInputSystem forwarding) {
            x = posX;
            y = posY;
            key = mouseKey;
            fwdInputSystem = forwarding;
        }

        @Override
        public void executeAction(final int count) {
            switch (count) {
                case 1:
                    EventBus.publish(new ClickOnMapEvent(key, x, y, fwdInputSystem));
                    break;
                case 2:
                    EventBus.publish(new DoubleClickOnMapEvent(key, x, y, fwdInputSystem));
                    break;
            }
        }
    }

    private static final class PointAtHelper extends AbstractMultiActionHelper {
        /**
         * The x coordinate of the last reported click.
         */
        private int x;

        /**
         * The Y coordinate of the last reported click.
         */
        private int y;

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
     * This forwarding control input system is used to control the input forwarding of the Nifty-GUI.
     */
    private final NiftyInputForwarding forwardingControl;

    /**
     * Slicks input system that is monitored here.
     */
    private Input input;

    /**
     * The instance of the button multiclick helper that is used in this instance of the input receiver.
     */
    private final ButtonMultiClickHelper buttonMultiClickHelper = new ButtonMultiClickHelper();

    private final PointAtHelper pointAtHelper = new PointAtHelper();

    public InputReceiver(final NiftyInputForwarding forwardingInputSystem) {
        forwardingControl = forwardingInputSystem;
    }

    /**
     * @see org.newdawn.slick.InputListener#keyPressed(int, char)
     */
    @Override
    public void keyPressed(final int key, final char c) {
        keyMapper.handleKeyInput(key);
    }

    @Override
    public void mouseMoved(final int oldX, final int oldY, final int newX, final int newY) {
        pointAtHelper.setInputData(newX, newY);
        pointAtHelper.pulse();
        EventBus.publish(new MoveOnMapEvent(newX, newY));
    }

    /**
     * @see org.newdawn.slick.InputListener#mouseClicked(int, int, int, int)
     */
    @Override
    public void mouseClicked(final int button, final int x, final int y, final int clickCount) {
        buttonMultiClickHelper.setInputData(button, x, y, forwardingControl.getInputForwardingControl());
        buttonMultiClickHelper.pulse();
    }

    @Override
    public void mouseDragged(final int oldx, final int oldy, final int newx, final int newy) {
        buttonMultiClickHelper.reset();
        for (int i = 0; i < Mouse.getButtonCount(); i++) {
            if (Mouse.isButtonDown(i)) {
                EventBus.publish(new DragOnMapEvent(oldx, oldy, newx, newy, i,
                        forwardingControl.getInputForwardingControl()));
                return;
            }
        }
    }

    @Override
    public void setInput(final Input input) {
        this.input = input;
    }

    @Override
    public void mouseReleased(final int button, final int x, final int y) {
        World.getPlayer().getMovementHandler().stopWalkTowards();
        if (forwardingControl.isInputForwardingSupported()) {
            forwardingControl.getInputForwardingControl().releaseExclusiveMouse();
        }
    }
}
