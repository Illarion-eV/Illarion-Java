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
package illarion.input.lwjgl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import illarion.common.util.Stoppable;
import illarion.common.util.StoppableStorage;

import illarion.graphics.Graphics;
import illarion.graphics.RenderTask;

import illarion.input.MouseEvent;
import illarion.input.MouseEventReceiver;
import illarion.input.MouseManager;

/**
 * The LWJGL implementation of the mouse manager offers the client to get the
 * events and the state of the mouse by the LWJGL handler.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class MouseManagerLWJGL extends Thread implements MouseManager,
    Stoppable {
    /**
     * Logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(MouseManagerLWJGL.class);

    /**
     * Helper array for the detection of clicks.
     */
    private final boolean[] click = new boolean[] { false, false, false };

    /**
     * The maximal time between two clicks in milliseconds that are considered
     * as click or double click event. Currently: 100ms
     */
    private final long clickSpeed = 200L;

    /**
     * Stores if there was already a pointing at event fired since the user
     * performed any action with the mouse last time. This is used to avoid
     * massive spam of pointing at events.
     */
    private boolean currentlyPointing = false;

    /**
     * The storage that contains all the mouse data that was yet not handled.
     */
    private final FastList<MouseData> data = new FastList<MouseData>();

    /**
     * Helper array for the detection of double clicks.
     */
    private final boolean[] dblClick = new boolean[] { false, false, false };

    /**
     * Helper array for the detection of dragging event.
     */
    private final boolean[] dragClick = new boolean[] { false, false, false };

    /**
     * Stores if there is currently a dragging event running.
     */
    private boolean dragging = false;

    /**
     * Check if the mouse is currently graped to the window or not.
     */
    private boolean grap = false;

    /**
     * The location of the mouse when the state of the mouse button changed last
     * time.
     */
    private final int[] lastClickX = new int[] { 0, 0, 0 };

    /**
     * The location of the mouse when the state of the mouse change last time.
     */
    private final int[] lastClickY = new int[] { 0, 0, 0 };

    /**
     * This stores the last states of the buttons that were read. The value is
     * <code>true</code> in case the button is pressed down.
     */
    private final boolean[] lastReadButtons = new boolean[3];

    /**
     * Last read X coordinate of the mouse.
     */
    private int lastReadX;

    /**
     * Last read Y coordinate of the mouse.
     */
    private int lastReadY;

    /**
     * Last reported X coordinate of the mouse.
     */
    private int lastX;

    /**
     * Last reported Y coordinate of the mouse.
     */
    private int lastY;

    /**
     * Flag that stores if the mouse input manager is currently running or not.
     */
    private boolean managerRunning = false;

    /**
     * A list of clicks performed with the mouse keys. This is used for the
     * click and double click detection.
     */
    private final int[] possibleClick = new int[] { 0, 0, 0 };

    /**
     * The list of the event handlers that have to be notified about the events
     * the mouse handler fires.
     */
    private MouseEventReceiver receiver;

    /**
     * The running flag. In case this is set to <code>false</code> the loop will
     * exit at the next run.
     */
    private volatile boolean running = true;

    /**
     * The time when the state of the mouse was changed last time.
     */
    private final long[] timeOfLastChange = new long[] { 0, 0, 0 };

    /**
     * This variable stores the time when the last event occurred.
     */
    private long timeOfLastEvent = System.currentTimeMillis();

    /**
     * Create the class and the underlying thread with the proper settings.
     */
    @SuppressWarnings("nls")
    public MouseManagerLWJGL() {
        super("MouseManager - LWJGL");
    }

    /**
     * Translate the Illarion key codes for mouse keys to the LWJGL keys.
     * 
     * @param key the Illarion code of the key
     * @return the LWJGL code of the key
     */
    private static int translateKeysIllarionLWJGL(final int key) {
        switch (key) {
            case MouseEvent.BUTTON1:
                return 0;
            case MouseEvent.BUTTON2:
                return 1;
            case MouseEvent.BUTTON3:
                return 2;
            default:
                return -1;
        }
    }

    /**
     * Translate the LWJGL key codes of the mouse keys to the Illarion key
     * codes.
     * 
     * @param key the LWJGL code of the key
     * @return the Illarion code of the key
     */
    private static int translateKeysLWJGLIllarion(final int key) {
        switch (key) {
            case 0:
                return MouseEvent.BUTTON1;
            case 1:
                return MouseEvent.BUTTON2;
            case 2:
                return MouseEvent.BUTTON3;
            default:
                return MouseEvent.NOBUTTON;
        }
    }

    /**
     * Clear all event handlers from the list. After this was done the mouse
     * manager stays active and working. How ever all event handlers get
     * inactive.
     */
    @Override
    public void clear() {
        receiver = null;
    }

    /**
     * Get the X coordinate of the mouse on the screen.
     * 
     * @return the x coordinate of the mouse on the screen
     */
    @Override
    public int getMousePosX() {
        if (!managerRunning) {
            return -1;
        }
        return lastReadX;
    }

    /**
     * Get the Y coordinate of the mouse on the screen.
     * 
     * @return the y coordinate of the mouse on the screen
     */
    @Override
    public int getMousePosY() {
        if (!managerRunning) {
            return -1;
        }
        return lastReadY;
    }

    /**
     * Check if a button of the mouse is pressed down.
     * 
     * @param key the key of the button that shall be checked
     * @return <code>true</code> in case the button is pressed down
     */
    @Override
    public boolean isKeyDown(final int key) {
        return lastReadButtons[translateKeysIllarionLWJGL(key)];
    }

    /**
     * Check if a button of the mouse is <b>not</b> pressed down.
     * 
     * @param key the key of the button that shall be checked
     * @return <code>true</code> in case the button is <b>not</b> pressed down
     */
    @Override
    public boolean isKeyUp(final int key) {
        return !isKeyDown(key);
    }

    /**
     * Add a event to this manager. All event handlers are notified about the
     * clicks done with the mouse.
     * 
     * @param event the event handler that shall be registered to the manager
     */
    @Override
    public void registerEventHandler(final MouseEventReceiver event) {
        receiver = event;
    }

    /**
     * The main loop of the thread managing the input.
     */
    @Override
    @SuppressWarnings("nls")
    public void run() {
        while (running) {
            boolean newEvent = false;
            final long currTime = System.currentTimeMillis();
            MouseData currEvent = null;
            synchronized (data) {
                if (!data.isEmpty()) {
                    currEvent = data.removeFirst();
                }
            }
            if (currEvent != null) {
                if (receiver == null) {
                    continue;
                }

                analyseMouseEvent(currTime, currEvent);
                newEvent = true;
            }

            analyseClickAndPoint(currTime);

            if (!newEvent) {
                try {
                    synchronized (data) {
                        data.wait();
                    }
                } catch (final InterruptedException e) {
                    LOGGER.warn("Mouse manager worken up unexpected");
                }
            }
        }

        managerRunning = false;
    }

    /**
     * Stop the thread at the next loop.
     */
    @Override
    public void saveShutdown() {
        running = false;
        synchronized (data) {
            data.notify();
        }
    }

    /**
     * Bind the mouse to the window when its created. This slows the input the
     * reaction speed down up fetches the native OS hot keys.
     * 
     * @param bind <code>true</code> to bind the mouse key, <code>false</code>
     *            is activated on default
     */
    @SuppressWarnings("nls")
    public void setBindToWindow(final boolean bind) {
        grap = bind;
        if (Mouse.isCreated()) {
            if (!grap) {
                final int minSize = Math.max(Cursor.getMinCursorSize(), 2);
                final int quadMinSize = minSize * minSize;
                final IntBuffer cursorData =
                    ByteBuffer.allocateDirect(quadMinSize * 4)
                        .order(ByteOrder.nativeOrder()).asIntBuffer();
                for (int i = 0; i < quadMinSize; ++i) {
                    cursorData.put(0x00000000);
                }
                cursorData.flip();

                try {
                    final Cursor cursor =
                        new Cursor(minSize, minSize, 1, 1, 1, cursorData, null);
                    Mouse.setNativeCursor(cursor);
                    Mouse.setGrabbed(false);
                } catch (final LWJGLException e) {
                    LOGGER.error("Setting up native mousecursor failed.", e);

                    Mouse.setGrabbed(true);
                    grap = true;
                }
            } else {
                try {
                    Mouse.setNativeCursor(null);
                } catch (final LWJGLException e) {
                    LOGGER.warn("Failed to clean native cursor");
                }
                Mouse.setGrabbed(true);
            }
        }
    }

    /**
     * Shut the mouse down. This disables the mouse manager and removes all
     * event handlers. After this was done, the mouse manager is not working
     * anymore.
     */
    @Override
    public void shutdown() {
        if (!Mouse.isCreated()) {
            return;
        }
        clear();
        managerRunning = false;
        running = false;

        Mouse.destroy();
        synchronized (data) {
            data.notify();
        }
    }

    /**
     * Create the mouse. Its needed that the main window is currently in focus
     * when this is done so the mouse is created on the correct reference.
     */
    @Override
    @SuppressWarnings("nls")
    public void startManager() {
        if (!Graphics.getInstance().getRenderDisplay()
            .isInputListenerSupported(null)) {
            throw new InputLWJGLException(
                "Input Handler is not supported with the graphic binding.");
        }
        if (!Mouse.isCreated()) {
            try {
                Mouse.create();
            } catch (final LWJGLException e) {
                LOGGER.error("Failed to create mouse.", e);
            }
        }

        setBindToWindow(grap);

        Mouse.poll();
        while (Mouse.next()) {
            // clean the buffer
        }

        Graphics.getInstance().getRenderManager().addTask(new RenderTask() {
            @Override
            @SuppressWarnings("synthetic-access")
            public boolean render(final int delta) {
                if (managerRunning) {
                    updateMouseData();
                    return true;
                }
                return false;
            }
        });

        StoppableStorage.getInstance().add(this);
        start();
        managerRunning = true;
    }

    /**
     * Update the data of the mouse.
     */
    protected void updateMouseData() {
        Mouse.poll();

        synchronized (data) {
            synchronized (MouseData.LOCK) {
                while (Mouse.next()) {
                    final MouseData dataBlock = MouseData.get();
                    dataBlock.setData(Mouse.getEventX(), Mouse.getEventY(),
                        Mouse.getEventDWheel(), Mouse.getEventButton(),
                        Mouse.getEventButtonState());
                    data.addLast(dataBlock);
                }
            }
            data.notify();
        }
    }

    /**
     * Measure the time to find if a click was performed or a mouse pointing at.
     * 
     * @param currTime the current system time
     */
    private void analyseClickAndPoint(final long currTime) {
        final int currX = lastReadX;
        final int currY = lastReadY;

        if (receiver == null) {
            return;
        }
        if ((currX != lastX) || (currY != lastY)) {
            final MouseEvent event = MouseEvent.get();
            event.setEventData(MouseEvent.EVENT_LOCATION,
                java.awt.event.MouseEvent.NOBUTTON, currX, currY, 0);
            receiver.handleMouseEvent(event);
            lastX = currX;
            lastY = currY;

            timeOfLastEvent = currTime;
            currentlyPointing = false;
        }

        if (!dragging && !currentlyPointing) {
            if ((currTime - timeOfLastEvent) > 1000) {
                final MouseEvent event = MouseEvent.get();
                event.setEventData(MouseEvent.EVENT_POINT_AT,
                    java.awt.event.MouseEvent.NOBUTTON, currX, currY, 0);
                receiver.handleMouseEvent(event);
                timeOfLastEvent = currTime;
                currentlyPointing = true;
            }
        }

        if ((possibleClick[0] == 0) && (possibleClick[1] == 0)
            && (possibleClick[2] == 0)) {
            return;
        }

        boolean anythingToReport = false;
        for (int key = 0; key < 3; ++key) {
            if (((currTime - timeOfLastChange[key]) > clickSpeed)
                || ((Math.abs(lastClickX[key] - lastX) > 3) || (Math
                    .abs(lastClickY[key] - lastY) > 3))) {
                if ((possibleClick[key] == 2) || (possibleClick[key] == 3)) {
                    click[key] = true;
                    dblClick[key] = false;
                    dragClick[key] = false;
                    anythingToReport = true;
                } else if (possibleClick[key] == 4) {
                    click[key] = false;
                    dblClick[key] = true;
                    dragClick[key] = false;
                    anythingToReport = true;
                } else if ((possibleClick[key] == 1) && (key == 0)) {
                    click[key] = false;
                    dblClick[key] = false;
                    dragClick[key] = true;
                    dragging = true;
                    anythingToReport = true;
                } else {
                    click[key] = false;
                    dblClick[key] = false;
                    dragClick[key] = false;
                }
                possibleClick[key] = 0;
            } else {
                click[key] = false;
                dblClick[key] = false;
                dragClick[key] = false;
            }
        }

        if (anythingToReport) {
            for (int key = 0; key < 3; ++key) {
                if (click[key]) {
                    final MouseEvent event = MouseEvent.get();
                    event.setEventData(MouseEvent.EVENT_KEY_CLICK,
                        translateKeysLWJGLIllarion(key), lastClickX[key],
                        lastClickY[key], 0);
                    receiver.handleMouseEvent(event);
                } else if (dblClick[key]) {
                    final MouseEvent event = MouseEvent.get();
                    event.setEventData(MouseEvent.EVENT_KEY_DBLCLICK,
                        translateKeysLWJGLIllarion(key), lastClickX[key],
                        lastClickY[key], 0);
                    receiver.handleMouseEvent(event);
                } else if (dragClick[key]) {
                    final MouseEvent event = MouseEvent.get();
                    event.setEventData(MouseEvent.EVENT_DRAG_START,
                        translateKeysLWJGLIllarion(key), lastClickX[key],
                        lastClickY[key], 0);
                    receiver.handleMouseEvent(event);
                }
            }
        }
    }

    /**
     * Analyze the effects of a mouse event.
     * 
     * @param currTime the current time
     * @param mouseEvent the mouse event to check
     */
    private void analyseMouseEvent(final long currTime,
        final MouseData mouseEvent) {
        final int locX = mouseEvent.getX();
        final int locY = mouseEvent.getY();
        final int eventButton = mouseEvent.getButton();
        final int translatedButton = translateKeysLWJGLIllarion(eventButton);
        final boolean pressed = mouseEvent.isPressed();
        final int wheelDelta = mouseEvent.getDelta();

        if ((lastReadX != locX) || (lastReadY != locY)) {
            final MouseEvent event = MouseEvent.get();
            event.setEventData(MouseEvent.EVENT_LOCATION,
                java.awt.event.MouseEvent.NOBUTTON, locX, locY, 0);
            receiver.handleMouseEvent(event);
        }

        lastReadX = locX;
        lastReadY = locY;

        timeOfLastEvent = currTime;
        currentlyPointing = false;
        if (eventButton > -1) {
            lastReadButtons[eventButton] = pressed;
            lastClickX[eventButton] = locX;
            lastClickY[eventButton] = locY;
            if ((possibleClick[eventButton] == 0)
                || ((currTime - timeOfLastChange[eventButton]) < clickSpeed)) {
                if (pressed
                    && ((possibleClick[eventButton] == 0) || (possibleClick[eventButton] == 2))) {
                    ++possibleClick[eventButton];
                    timeOfLastChange[eventButton] = currTime;
                } else if (!pressed
                    && ((possibleClick[eventButton] == 1) || (possibleClick[eventButton] == 3))) {
                    ++possibleClick[eventButton];
                    timeOfLastChange[eventButton] = currTime;
                }
            } else {
                possibleClick[eventButton] = 0;
            }

            if (pressed) {
                final MouseEvent event = MouseEvent.get();
                event.setEventData(MouseEvent.EVENT_KEY_DOWN,
                    translatedButton, locX, locY, 0);
                receiver.handleMouseEvent(event);
            } else {
                final MouseEvent event = MouseEvent.get();
                event.setEventData(MouseEvent.EVENT_KEY_UP, translatedButton,
                    locX, locY, 0);
                receiver.handleMouseEvent(event);

                if (dragging) {
                    dragging = false;
                    final MouseEvent event2 = MouseEvent.get();
                    event2.setEventData(MouseEvent.EVENT_DRAG_END,
                        translatedButton, locX, locY, 0);
                    receiver.handleMouseEvent(event2);
                }
            }
        }

        if (wheelDelta != 0) {
            final MouseEvent event = MouseEvent.get();
            event.setEventData(MouseEvent.EVENT_WHEEL_CHANGE,
                java.awt.event.MouseEvent.NOBUTTON, locX, locY, wheelDelta);
            receiver.handleMouseEvent(event);
        }
    }
}
