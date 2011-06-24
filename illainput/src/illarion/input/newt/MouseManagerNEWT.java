/*
 * This file is part of the Illarion Input Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Input Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Input Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Input Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.input.newt;

import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;

import illarion.common.util.FastMath;
import illarion.common.util.Timer;

import illarion.graphics.Graphics;

import illarion.input.MouseEventReceiver;
import illarion.input.MouseManager;

/**
 * This is the NEWT implementation of the mouse manager. Its supposed to handle
 * occurring mouse events properly.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class MouseManagerNEWT implements MouseManager, MouseListener {
    /**
     * The largest mouse key index that is handled by this class.
     */
    static final int MOUSE_KEYS = 3;

    /**
     * This timer is used to handle clicks and double clicks properly.
     */
    private final Timer doubleClickTimer;

    /**
     * A list of flags for each mouse button if there is currently a dragging
     * event in action.
     */
    private final boolean[] dragging;

    /**
     * The timer that is used to handle time based dragging events.
     */
    private final Timer draggingTimer;

    /**
     * The state of the keys. <code>True</code> means the key is pressed down.
     */
    private final boolean[] keystate;

    /**
     * The last X coordinate that was received from the mouse. <code>-1</code>
     * in case its outside of the render screen.
     */
    private int lastX = -1;

    /**
     * The last Y coordinate that was received from the mouse. <code>-1</code>
     * in case its outside of the render screen.
     */
    private int lastY = -1;

    /**
     * The timer that is used to handle the point at events.
     */
    private final Timer pointAtTimer;

    /**
     * The event receiver that is used to fetch all received mouse events.
     */
    private MouseEventReceiver receiver;

    /**
     * The click event that was recorded but yet not passed to the rest of the
     * application in favor of the double click check.
     */
    private illarion.input.MouseEvent recordedClickEvent;

    /**
     * The constructor of the mouse manager that prepares all required objects
     * for the proper operation of this handler.
     */
    public MouseManagerNEWT() {
        dragging = new boolean[MOUSE_KEYS + 1];
        keystate = new boolean[MOUSE_KEYS + 1];
        pointAtTimer = new Timer(500, new Runnable() {
            /**
             * Called by the timer in the set interval.
             */
            @Override
            public void run() {
                final illarion.input.MouseEvent event =
                    illarion.input.MouseEvent.get();

                event.setEventData(illarion.input.MouseEvent.EVENT_POINT_AT,
                    illarion.input.MouseEvent.NOBUTTON, getLastX(),
                    getLastY(), 0);

                final MouseEventReceiver recv = getReceiver();
                if (recv != null) {
                    recv.handleMouseEvent(event);
                }
            }
        });

        draggingTimer = new Timer(150, new Runnable() {
            /**
             * Called by the timer in the set interval.
             */
            @Override
            public void run() {
                for (int i = 1; i <= MOUSE_KEYS; i++) {
                    if (isKeyDown(i) && !isKeyDragging(i)) {
                        final int eventType =
                            illarion.input.MouseEvent.EVENT_DRAG_START;

                        final illarion.input.MouseEvent event =
                            illarion.input.MouseEvent.get();

                        event.setEventData(eventType, i, getLastX(),
                            getLastY(), 0);
                        setKeyDragging(i, true);
                        final MouseEventReceiver recv = getReceiver();
                        if (recv != null) {
                            recv.handleMouseEvent(event);
                        }
                    }
                }
            }
        });

        doubleClickTimer = new Timer(250, new Runnable() {
            /**
             * Called by the timer in the set interval.
             */
            @Override
            public void run() {
                final illarion.input.MouseEvent event =
                    getRecordedMouseClickEvent();
                if (event != null) {
                    clearRecordedMouseClickEvent();
                    final MouseEventReceiver recv = getReceiver();
                    if (recv != null) {
                        recv.handleMouseEvent(event);
                    }
                }
            }

        });
    }

    /**
     * Correct the y coordinate of the mouse location to meet the illarion
     * coordinate specifications.
     * 
     * @param orgY the java y coordinate
     * @return the illarion y coordinate
     */
    static int fixY(final int orgY) {
        return Graphics.getInstance().getRenderDisplay().getRenderArea()
            .getHeight()
            - orgY;
    }

    /**
     * Translate the Newt key codes of the mouse keys to the Illarion key codes.
     * 
     * @param key the Java code of the key
     * @return the Illarion code of the key
     */
    static int translateKeysNewtIllarion(final int key) {
        switch (key) {
            case MouseEvent.BUTTON1:
                return illarion.input.MouseEvent.BUTTON1;
            case MouseEvent.BUTTON2:
                return illarion.input.MouseEvent.BUTTON2;
            case MouseEvent.BUTTON3:
                return illarion.input.MouseEvent.BUTTON3;
            default:
                return illarion.input.MouseEvent.NOBUTTON;
        }
    }

    /**
     * Translate the Illarion key codes for mouse keys to the Newt keys.
     * 
     * @param key the Illarion code of the key
     * @return the Java code of the key
     */
    private static int translateKeysIllarionNewt(final int key) {
        switch (key) {
            case illarion.input.MouseEvent.BUTTON1:
                return MouseEvent.BUTTON1;
            case illarion.input.MouseEvent.BUTTON2:
                return MouseEvent.BUTTON2;
            case illarion.input.MouseEvent.BUTTON3:
                return MouseEvent.BUTTON3;
            default:
                return -1;
        }
    }

    /**
     * Clean up the receiver and remove the mouse event receiver.
     */
    @Override
    public void clear() {
        receiver = null;
    }

    /**
     * Get the last recorded X coordinate of the mouse.
     * 
     * @return the mouse coordinate or -1 in case the mouse is outside of the
     *         screen
     */
    @Override
    public int getMousePosX() {
        return lastX;
    }

    /**
     * Get the last recorded Y coordinate of the mouse.
     * 
     * @return the mouse coordinate or -1 in case the mouse is outside of the
     *         screen
     */
    @Override
    public int getMousePosY() {
        return lastY;
    }

    /**
     * Check if one key of the mouse is pressed down.
     * 
     * @param key the key of the button to check
     * @return <code>true</code> in case the button is currently pressed
     */
    @Override
    public boolean isKeyDown(final int key) {
        return keystate[translateKeysIllarionNewt(key)];
    }

    /**
     * Check if one key of the mouse is not pressed down.
     * 
     * @param key the key of the button to check
     * @return <code>true</code> in case the button is currently not pressed
     */
    @Override
    public boolean isKeyUp(final int key) {
        return !keystate[translateKeysIllarionNewt(key)];
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        lastX = e.getX();
        lastY = fixY(e.getY());

        final illarion.input.MouseEvent event =
            illarion.input.MouseEvent.get();

        final int eventID = illarion.input.MouseEvent.EVENT_KEY_CLICK;
        event.setEventData(eventID, translateKeysNewtIllarion(e.getButton()),
            e.getX(), fixY(e.getY()), 0);

        doubleClickTimer.stop();

        if (recordedClickEvent == null) {
            recordedClickEvent = event;
            doubleClickTimer.restart();
        } else {
            if ((recordedClickEvent.getKey() == event.getKey())
                && FastMath.equal(recordedClickEvent.getPosX(),
                    event.getPosX(), 2)
                && FastMath.equal(recordedClickEvent.getPosY(),
                    event.getPosY(), 2)) {
                recordedClickEvent.setEventData(
                    illarion.input.MouseEvent.EVENT_KEY_DBLCLICK,
                    event.getKey(), event.getPosX(), event.getPosY(), 0);
                if (receiver != null) {
                    receiver.handleMouseEvent(recordedClickEvent);
                }
                recordedClickEvent = null;
                event.recycle();
            } else {
                if (receiver != null) {
                    receiver.handleMouseEvent(recordedClickEvent);
                }
                recordedClickEvent = event;
                doubleClickTimer.restart();
            }
        }

        pointAtTimer.restart();
        draggingTimer.stop();
    }

    /**
     * A drag event of the mouse.
     * 
     * @param e the mouse wheel event
     */
    @Override
    public void mouseDragged(final MouseEvent e) {
        lastX = e.getX();
        lastY = fixY(e.getY());

        final int button = translateKeysNewtIllarion(e.getButton());

        int eventType = illarion.input.MouseEvent.EVENT_DRAG_START;
        if (dragging[button]) {
            eventType = illarion.input.MouseEvent.EVENT_LOCATION;
        }

        final illarion.input.MouseEvent event =
            illarion.input.MouseEvent.get();

        event.setEventData(eventType, translateKeysNewtIllarion(button),
            e.getX(), fixY(e.getY()), 0);
        dragging[button] = true;
        if (receiver != null) {
            receiver.handleMouseEvent(event);
        }

        pointAtTimer.stop();
        draggingTimer.stop();
    }

    /**
     * The mouse enters the screen.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseEntered(final MouseEvent e) {
        lastX = e.getX();
        lastY = fixY(e.getY());

        final illarion.input.MouseEvent event =
            illarion.input.MouseEvent.get();

        event.setEventData(illarion.input.MouseEvent.EVENT_LOCATION,
            illarion.input.MouseEvent.NOBUTTON, e.getX(), fixY(e.getY()), 0);
        if (receiver != null) {
            receiver.handleMouseEvent(event);
        }

        pointAtTimer.start();
        draggingTimer.stop();
    }

    /**
     * The mouse leaves the screen.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseExited(final MouseEvent e) {
        lastX = -1;
        lastY = -1;

        final illarion.input.MouseEvent event =
            illarion.input.MouseEvent.get();

        event.setEventData(illarion.input.MouseEvent.EVENT_LOCATION,
            illarion.input.MouseEvent.NOBUTTON, -1, -1, 0);
        if (receiver != null) {
            receiver.handleMouseEvent(event);
        }

        pointAtTimer.stop();
        draggingTimer.stop();
    }

    /**
     * A mouse move event.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseMoved(final MouseEvent e) {
        lastX = e.getX();
        lastY = fixY(e.getY());

        final illarion.input.MouseEvent event =
            illarion.input.MouseEvent.get();

        event.setEventData(illarion.input.MouseEvent.EVENT_LOCATION,
            illarion.input.MouseEvent.NOBUTTON, e.getX(), fixY(e.getY()), 0);
        if (receiver != null) {
            receiver.handleMouseEvent(event);
        }

        pointAtTimer.restart();
        draggingTimer.stop();
    }

    /**
     * One key of the mouse is pressed down.
     * 
     * @param e the mouse event
     */
    @Override
    public void mousePressed(final MouseEvent e) {
        lastX = e.getX();
        lastY = fixY(e.getY());

        final illarion.input.MouseEvent event =
            illarion.input.MouseEvent.get();

        final int illaButton = translateKeysNewtIllarion(e.getButton());
        event.setEventData(illarion.input.MouseEvent.EVENT_KEY_DOWN,
            illaButton, e.getX(), fixY(e.getY()), 0);
        keystate[illaButton] = true;
        if (receiver != null) {
            receiver.handleMouseEvent(event);
        }

        pointAtTimer.restart();
        draggingTimer.restart();
    }

    /**
     * One key of the mouse is released down.
     * 
     * @param e the mouse event
     */
    @Override
    public void mouseReleased(final MouseEvent e) {
        mouseReleasedImpl(e, translateKeysNewtIllarion(e.getButton()));
    }

    /**
     * The mouse wheel is moved.
     * 
     * @param e the mouse wheel event
     */
    @Override
    public void mouseWheelMoved(final MouseEvent e) {
        lastX = e.getX();
        lastY = fixY(e.getY());

        final illarion.input.MouseEvent event =
            illarion.input.MouseEvent.get();

        event.setEventData(illarion.input.MouseEvent.EVENT_WHEEL_CHANGE,
            illarion.input.MouseEvent.NOBUTTON, e.getX(), fixY(e.getY()),
            e.getWheelRotation());
        if (receiver != null) {
            receiver.handleMouseEvent(event);
        }
    }

    /**
     * Set the receiver that is from now on supposed to get all updates of the
     * mouse.
     */
    @Override
    public void registerEventHandler(final MouseEventReceiver event) {
        receiver = event;
    }

    /**
     * Shut the mouse manager down by removing the listeners that are set up.
     */
    @Override
    public void shutdown() {
        Graphics.getInstance().getRenderDisplay().removeInputListener(this);
    }

    /**
     * Start the Mouse Manager by setting up the required listeners.
     */
    @Override
    @SuppressWarnings("nls")
    public void startManager() {
        if (!Graphics.getInstance().getRenderDisplay()
            .isInputListenerSupported(this)) {
            throw new InputNEWTException(
                "Input Handler is not supported with the graphic binding.");
        }

        Graphics.getInstance().getRenderDisplay().addInputListener(this);

        pointAtTimer.setRepeats(false);
        pointAtTimer.start();

        doubleClickTimer.setRepeats(false);
        draggingTimer.setRepeats(false);
    }

    /**
     * Clear the mouse click event.
     */
    void clearRecordedMouseClickEvent() {
        recordedClickEvent = null;
    }

    /**
     * The last x coordinate the mouse was found at.
     * 
     * @return the last x coordinate
     */
    int getLastX() {
        return lastX;
    }

    /**
     * The last y coordinate the mouse was found at.
     * 
     * @return the last y coordinate
     */
    int getLastY() {
        return lastY;
    }

    /**
     * Get the receiver of the mouse events.
     * 
     * @return the receiver of the mouse events or <code>null</code> in case no
     *         receiver is set
     */
    MouseEventReceiver getReceiver() {
        return receiver;
    }

    /**
     * Get the mouse click event that was recorded but yet not fired to the rest
     * of the application.
     * 
     * @return the mouse event that was recorded
     */
    illarion.input.MouseEvent getRecordedMouseClickEvent() {
        return recordedClickEvent;
    }

    /**
     * Check if a key is currently used for a dragging event.
     * 
     * @param key the key
     * @return <code>true</code> in case this key is currently performing a
     *         dragging event
     */
    boolean isKeyDragging(final int key) {
        return dragging[key];
    }

    /**
     * Set the dragging state of one key.
     * 
     * @param key the key that shall be set
     * @param value the new dragging state
     */
    void setKeyDragging(final int key, final boolean value) {
        dragging[key] = value;
    }

    /**
     * Execute the mouse released event properly.
     * 
     * @param e the mouse event to handle
     * @param button the button that is handled
     */
    private void mouseReleasedImpl(final MouseEvent e, final int button) {
        final int mouseX = e.getX();
        final int mouseY = fixY(e.getY());
        lastX = mouseX;
        lastY = mouseY;

        final illarion.input.MouseEvent event =
            illarion.input.MouseEvent.get();

        event.setEventData(illarion.input.MouseEvent.EVENT_KEY_UP, button,
            mouseX, mouseY, 0);
        keystate[button] = false;
        if (receiver != null) {
            receiver.handleMouseEvent(event);
        }

        if (!dragging[button]) {
            pointAtTimer.restart();
            return;
        }

        final illarion.input.MouseEvent event2 =
            illarion.input.MouseEvent.get();

        event2.setEventData(illarion.input.MouseEvent.EVENT_DRAG_END, button,
            mouseX, mouseY, 0);
        dragging[button] = false;
        if (receiver != null) {
            receiver.handleMouseEvent(event2);
        }

        pointAtTimer.start();
        draggingTimer.stop();
    }
}
