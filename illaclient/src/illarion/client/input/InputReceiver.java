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

import java.awt.*;

import org.bushe.swing.event.EventBus;
import org.newdawn.slick.util.InputAdapter;

import illarion.client.world.World;
import illarion.common.util.Timer;

/**
 * This class is used to receive and forward all user input.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class InputReceiver
        extends InputAdapter {
    /**
     * The topic that is in general used to publish input events.
     */
    public static final String EB_TOPIC = "InputEvent";

    private boolean wasDoubleClick = false;
    private Timer timer = null;

    private final KeyMapper keyMapper = new KeyMapper();

    private final NiftyInputForwarding forwardingControl;

    public InputReceiver(final NiftyInputForwarding forwardingInputSystem) {
        forwardingControl = forwardingInputSystem;
    }

    /**
     * @see org.newdawn.slick.InputListener#keyPressed(int, char)
     */
    public void keyPressed(final int key, final char c) {
        keyMapper.handleKeyInput(key);
    }

    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
        EventBus.publish(EB_TOPIC, new DragOnMapEvent(oldx, oldy, newx, newy,
                                                      forwardingControl.getInputForwardingControl()));
    }

    public void mouseReleased(int button, int x, int y) {
        World.getPlayer().getMovementHandler().stopWalkTowards();
        if (forwardingControl.isInputForwardingSupported()) {
            forwardingControl.getInputForwardingControl().releaseExclusiveMouse();
        }
    }

    /**
     * @see org.newdawn.slick.InputListener#mouseClicked(int, int, int, int)
     */
    public void mouseClicked(int button, int x, int y, int clickCount) {
        if (button == 0) {
            if (clickCount == 2) {
                if (timer != null) {
                    timer.stop();
                    timer = null;
                }
                wasDoubleClick = true;
                EventBus.publish(EB_TOPIC, new DoubleClickOnMapEvent(x, y,
                                                                     forwardingControl.getInputForwardingControl()));
            } else {
                Integer timerinterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt" +
                                                                                                         "" +
                                                                                                         "" +
                                                                                                         "" +
                                                                                                         "" +
                                                                                                         ".multiClickInterval");

                final int xc = x, yc = y;
                if (timer != null) {
                    timer.stop();
                }

                wasDoubleClick = false;
                timer = new Timer(timerinterval, new Runnable() {
                    @Override
                    public void run() {
                        if (!wasDoubleClick) {
                            EventBus.publish(EB_TOPIC, new ClickOnMapEvent(xc, yc, forwardingControl.getInputForwardingControl()));
                        }
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
}
