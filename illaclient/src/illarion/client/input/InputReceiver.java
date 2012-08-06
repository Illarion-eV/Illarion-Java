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
import illarion.client.gui.util.AbstractMultiActionHelper;
import illarion.client.world.World;
import org.bushe.swing.event.EventBus;
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
    private static final class Button0MultiClickHelper
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
         * The input forwarding system that is published along with the click events.
         */
        private ForwardingInputSystem fwdInputSystem;

        /**
         * The constructor that sets the used timeout interval to the system default double click interval.
         */
        public Button0MultiClickHelper() {
            super(IllaClient.getCfg().getInteger("doubleClickInterval"));
        }

        /**
         * Update the data that is needed to report the state of the last click properly.
         *
         * @param posX       the x coordinate where the click happened
         * @param posY       the y coordinate where the click happened
         * @param forwarding the input forwarding system required to be published with the events
         */
        public void setInputData(final int posX, final int posY, final ForwardingInputSystem forwarding) {
            x = posX;
            y = posY;
            fwdInputSystem = forwarding;
        }

        @Override
        public void executeAction(final int count) {
            switch (count) {
                case 1:
                    EventBus.publish(new ClickOnMapEvent(x, y, fwdInputSystem));
                    break;
                case 2:
                    EventBus.publish(new DoubleClickOnMapEvent(x, y, fwdInputSystem));
                    break;
            }
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
     * This variable is used to keep track if the mouse went down on the map. This is required to detect if a
     * dragging operation started on the map.
     */
    private boolean keyDownOnMap[];

    /**
     * The instance of the button multiclick helper that is used in this instance of the input receiver.
     */
    private final Button0MultiClickHelper button0MultiClickHelper = new Button0MultiClickHelper();

    public InputReceiver(final NiftyInputForwarding forwardingInputSystem) {
        forwardingControl = forwardingInputSystem;

        keyDownOnMap = new boolean[3];
        for (int i = 0; i < keyDownOnMap.length; i++) {
            keyDownOnMap[i] = false;
        }
    }

    /**
     * @see org.newdawn.slick.InputListener#keyPressed(int, char)
     */
    @Override
    public void keyPressed(final int key, final char c) {
        keyMapper.handleKeyInput(key);
    }

    /**
     * @see org.newdawn.slick.InputListener#mouseClicked(int, int, int, int)
     */
    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        if (button == 0) {
            button0MultiClickHelper.setInputData(x, y, forwardingControl.getInputForwardingControl());
            button0MultiClickHelper.pulse();
        }
    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
        for (int i = 0; i < keyDownOnMap.length; i++) {
            if (keyDownOnMap[i]) {
                EventBus.publish(new DragOnMapEvent(oldx, oldy, newx, newy, i,
                        forwardingControl.getInputForwardingControl()));
            }
        }
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        keyDownOnMap[button] = true;
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        if (button == MOVE_KEY) {
            World.getPlayer().getMovementHandler().stopWalkTowards();
            if (forwardingControl.isInputForwardingSupported()) {
                forwardingControl.getInputForwardingControl().releaseExclusiveMouse();
            }
        }
        keyDownOnMap[button] = false;
    }
}
