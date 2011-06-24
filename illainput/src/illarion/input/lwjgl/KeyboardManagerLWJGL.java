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

import javolution.util.FastList;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import illarion.common.util.Stoppable;
import illarion.common.util.StoppableStorage;

import illarion.graphics.Graphics;
import illarion.graphics.RenderTask;

import illarion.input.KeyboardEvent;
import illarion.input.KeyboardEventReceiver;
import illarion.input.KeyboardManager;

/**
 * The keyboard LWJGL implementation offers the client the possibility to be
 * controlled by the LWJGL Keyboard.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class KeyboardManagerLWJGL extends Thread implements
    KeyboardManager, Stoppable {
    /**
     * Logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(KeyboardManagerLWJGL.class);

    /**
     * The storage that contains the current state of all buttons as it was last
     * reported.
     */
    private final boolean[] buttonState = new boolean[Keyboard.KEYBOARD_SIZE];

    /**
     * The storage that contains all the keyboard data that was yet not handled.
     */
    private final FastList<KeyboardData> data = new FastList<KeyboardData>();

    /**
     * This variable stores if the manager is currently running.
     */
    private boolean managerRunning = false;

    /**
     * The storage of events that are triggered in case of changes of the key
     * states.
     */
    private KeyboardEventReceiver receiver;

    /**
     * The running flag. In case this is set to <code>false</code> the loop will
     * exit at the next run.
     */
    private volatile boolean running = true;

    /**
     * Create the class and the underlying thread with the proper settings.
     */
    @SuppressWarnings("nls")
    public KeyboardManagerLWJGL() {
        super("KeyboardManager - LWJGL");
    }

    /**
     * Translate the value of a key from the Illarion value to the LWJGL value.
     * 
     * @param key the Illarion value of the key
     * @return the LWJGL value of the key
     */
    private static int translateKeysIllarionLWJGL(final int key) {
        switch (key) {
            case KeyboardEvent.VK_0:
                return Keyboard.KEY_0;
            case KeyboardEvent.VK_1:
                return Keyboard.KEY_1;
            case KeyboardEvent.VK_2:
                return Keyboard.KEY_2;
            case KeyboardEvent.VK_3:
                return Keyboard.KEY_3;
            case KeyboardEvent.VK_4:
                return Keyboard.KEY_4;
            case KeyboardEvent.VK_5:
                return Keyboard.KEY_5;
            case KeyboardEvent.VK_6:
                return Keyboard.KEY_6;
            case KeyboardEvent.VK_7:
                return Keyboard.KEY_7;
            case KeyboardEvent.VK_8:
                return Keyboard.KEY_8;
            case KeyboardEvent.VK_9:
                return Keyboard.KEY_9;
            case KeyboardEvent.VK_A:
                return Keyboard.KEY_A;
            case KeyboardEvent.VK_ADD:
                return Keyboard.KEY_ADD;
            case KeyboardEvent.VK_AT:
                return Keyboard.KEY_AT;
            case KeyboardEvent.VK_B:
                return Keyboard.KEY_B;
            case KeyboardEvent.VK_BACK_SPACE:
                return Keyboard.KEY_BACK;
            case KeyboardEvent.VK_BACK_SLASH:
                return Keyboard.KEY_BACKSLASH;
            case KeyboardEvent.VK_C:
                return Keyboard.KEY_C;
            case KeyboardEvent.VK_CAPS_LOCK:
                return Keyboard.KEY_CAPITAL;
            case KeyboardEvent.VK_CIRCUMFLEX:
                return Keyboard.KEY_CIRCUMFLEX;
            case KeyboardEvent.VK_COLON:
                return Keyboard.KEY_COLON;
            case KeyboardEvent.VK_COMMA:
                return Keyboard.KEY_COMMA;
            case KeyboardEvent.VK_D:
                return Keyboard.KEY_D;
            case KeyboardEvent.VK_DECIMAL:
                return Keyboard.KEY_DECIMAL;
            case KeyboardEvent.VK_DELETE:
                return Keyboard.KEY_DELETE;
            case KeyboardEvent.VK_DIVIDE:
                return Keyboard.KEY_DIVIDE;
            case KeyboardEvent.VK_DOWN:
                return Keyboard.KEY_DOWN;
            case KeyboardEvent.VK_E:
                return Keyboard.KEY_E;
            case KeyboardEvent.VK_END:
                return Keyboard.KEY_END;
            case KeyboardEvent.VK_EQUALS:
                return Keyboard.KEY_EQUALS;
            case KeyboardEvent.VK_ESCAPE:
                return Keyboard.KEY_ESCAPE;
            case KeyboardEvent.VK_F:
                return Keyboard.KEY_F;
            case KeyboardEvent.VK_F1:
                return Keyboard.KEY_F1;
            case KeyboardEvent.VK_F2:
                return Keyboard.KEY_F2;
            case KeyboardEvent.VK_F3:
                return Keyboard.KEY_F3;
            case KeyboardEvent.VK_F4:
                return Keyboard.KEY_F4;
            case KeyboardEvent.VK_F5:
                return Keyboard.KEY_F5;
            case KeyboardEvent.VK_F6:
                return Keyboard.KEY_F6;
            case KeyboardEvent.VK_F7:
                return Keyboard.KEY_F7;
            case KeyboardEvent.VK_F8:
                return Keyboard.KEY_F8;
            case KeyboardEvent.VK_F9:
                return Keyboard.KEY_F9;
            case KeyboardEvent.VK_F10:
                return Keyboard.KEY_F10;
            case KeyboardEvent.VK_F11:
                return Keyboard.KEY_F11;
            case KeyboardEvent.VK_F12:
                return Keyboard.KEY_F12;
            case KeyboardEvent.VK_G:
                return Keyboard.KEY_G;
            case KeyboardEvent.VK_H:
                return Keyboard.KEY_H;
            case KeyboardEvent.VK_HOME:
                return Keyboard.KEY_HOME;
            case KeyboardEvent.VK_I:
                return Keyboard.KEY_I;
            case KeyboardEvent.VK_INSERT:
                return Keyboard.KEY_INSERT;
            case KeyboardEvent.VK_J:
                return Keyboard.KEY_J;
            case KeyboardEvent.VK_K:
                return Keyboard.KEY_K;
            case KeyboardEvent.VK_L:
                return Keyboard.KEY_L;
            case KeyboardEvent.VK_BRACELEFT:
                return Keyboard.KEY_LBRACKET;
            case KeyboardEvent.VK_CONTROL:
                return Keyboard.KEY_LCONTROL;
            case KeyboardEvent.VK_LEFT:
                return Keyboard.KEY_LEFT;
            case KeyboardEvent.VK_META:
                return Keyboard.KEY_LMETA;
            case KeyboardEvent.VK_SHIFT:
                return Keyboard.KEY_LSHIFT;
            case KeyboardEvent.VK_M:
                return Keyboard.KEY_M;
            case KeyboardEvent.VK_MINUS:
                return Keyboard.KEY_MINUS;
            case KeyboardEvent.VK_MULTIPLY:
                return Keyboard.KEY_MULTIPLY;
            case KeyboardEvent.VK_N:
                return Keyboard.KEY_N;
            case KeyboardEvent.VK_NUM_LOCK:
                return Keyboard.KEY_NUMLOCK;
            case KeyboardEvent.VK_NUMPAD0:
                return Keyboard.KEY_NUMPAD0;
            case KeyboardEvent.VK_NUMPAD1:
                return Keyboard.KEY_NUMPAD1;
            case KeyboardEvent.VK_NUMPAD2:
                return Keyboard.KEY_NUMPAD2;
            case KeyboardEvent.VK_NUMPAD3:
                return Keyboard.KEY_NUMPAD3;
            case KeyboardEvent.VK_NUMPAD4:
                return Keyboard.KEY_NUMPAD4;
            case KeyboardEvent.VK_NUMPAD5:
                return Keyboard.KEY_NUMPAD5;
            case KeyboardEvent.VK_NUMPAD6:
                return Keyboard.KEY_NUMPAD6;
            case KeyboardEvent.VK_NUMPAD7:
                return Keyboard.KEY_NUMPAD7;
            case KeyboardEvent.VK_NUMPAD8:
                return Keyboard.KEY_NUMPAD8;
            case KeyboardEvent.VK_NUMPAD9:
                return Keyboard.KEY_NUMPAD9;
            case KeyboardEvent.VK_O:
                return Keyboard.KEY_O;
            case KeyboardEvent.VK_P:
                return Keyboard.KEY_P;
            case KeyboardEvent.VK_PAUSE:
                return Keyboard.KEY_PAUSE;
            case KeyboardEvent.VK_PERIOD:
                return Keyboard.KEY_PERIOD;
            case KeyboardEvent.VK_Q:
                return Keyboard.KEY_Q;
            case KeyboardEvent.VK_R:
                return Keyboard.KEY_R;
            case KeyboardEvent.VK_ENTER:
                return Keyboard.KEY_RETURN;
            case KeyboardEvent.VK_RIGHT:
                return Keyboard.KEY_RIGHT;
            case KeyboardEvent.VK_S:
                return Keyboard.KEY_S;
            case KeyboardEvent.VK_SCROLL_LOCK:
                return Keyboard.KEY_SCROLL;
            case KeyboardEvent.VK_SEMICOLON:
                return Keyboard.KEY_SEMICOLON;
            case KeyboardEvent.VK_SLASH:
                return Keyboard.KEY_SLASH;
            case KeyboardEvent.VK_SPACE:
                return Keyboard.KEY_SPACE;
            case KeyboardEvent.VK_STOP:
                return Keyboard.KEY_STOP;
            case KeyboardEvent.VK_SUBTRACT:
                return Keyboard.KEY_SUBTRACT;
            case KeyboardEvent.VK_T:
                return Keyboard.KEY_T;
            case KeyboardEvent.VK_TAB:
                return Keyboard.KEY_TAB;
            case KeyboardEvent.VK_U:
                return Keyboard.KEY_U;
            case KeyboardEvent.VK_UNDERSCORE:
                return Keyboard.KEY_UNDERLINE;
            case KeyboardEvent.VK_UNDEFINED:
                return Keyboard.KEY_UNLABELED;
            case KeyboardEvent.VK_UP:
                return Keyboard.KEY_UP;
            case KeyboardEvent.VK_V:
                return Keyboard.KEY_V;
            case KeyboardEvent.VK_W:
                return Keyboard.KEY_W;
            case KeyboardEvent.VK_X:
                return Keyboard.KEY_X;
            case KeyboardEvent.VK_Y:
                return Keyboard.KEY_Y;
            case KeyboardEvent.VK_Z:
                return Keyboard.KEY_Z;
            default:
                return -1;
        }
    }

    /**
     * Translate the value of a key from the LWJGL value to the Illarion value.
     * 
     * @param key the LWJGL value of the key
     * @return the Illarion value of the key
     */
    private static int translateKeysLWJGLIllarion(final int key) {
        switch (key) {
            case Keyboard.KEY_0:
                return KeyboardEvent.VK_0;
            case Keyboard.KEY_1:
                return KeyboardEvent.VK_1;
            case Keyboard.KEY_2:
                return KeyboardEvent.VK_2;
            case Keyboard.KEY_3:
                return KeyboardEvent.VK_3;
            case Keyboard.KEY_4:
                return KeyboardEvent.VK_4;
            case Keyboard.KEY_5:
                return KeyboardEvent.VK_5;
            case Keyboard.KEY_6:
                return KeyboardEvent.VK_6;
            case Keyboard.KEY_7:
                return KeyboardEvent.VK_7;
            case Keyboard.KEY_8:
                return KeyboardEvent.VK_8;
            case Keyboard.KEY_9:
                return KeyboardEvent.VK_9;
            case Keyboard.KEY_A:
                return KeyboardEvent.VK_A;
            case Keyboard.KEY_ADD:
                return KeyboardEvent.VK_ADD;
            case Keyboard.KEY_AT:
                return KeyboardEvent.VK_AT;
            case Keyboard.KEY_B:
                return KeyboardEvent.VK_B;
            case Keyboard.KEY_BACK:
                return KeyboardEvent.VK_BACK_SPACE;
            case Keyboard.KEY_BACKSLASH:
                return KeyboardEvent.VK_BACK_SLASH;
            case Keyboard.KEY_C:
                return KeyboardEvent.VK_C;
            case Keyboard.KEY_CAPITAL:
                return KeyboardEvent.VK_CAPS_LOCK;
            case Keyboard.KEY_CIRCUMFLEX:
                return KeyboardEvent.VK_CIRCUMFLEX;
            case Keyboard.KEY_COLON:
                return KeyboardEvent.VK_COLON;
            case Keyboard.KEY_COMMA:
                return KeyboardEvent.VK_COMMA;
            case Keyboard.KEY_D:
                return KeyboardEvent.VK_D;
            case Keyboard.KEY_DECIMAL:
                return KeyboardEvent.VK_DECIMAL;
            case Keyboard.KEY_DELETE:
                return KeyboardEvent.VK_DELETE;
            case Keyboard.KEY_DIVIDE:
                return KeyboardEvent.VK_DIVIDE;
            case Keyboard.KEY_DOWN:
                return KeyboardEvent.VK_DOWN;
            case Keyboard.KEY_E:
                return KeyboardEvent.VK_E;
            case Keyboard.KEY_END:
                return KeyboardEvent.VK_END;
            case Keyboard.KEY_EQUALS:
                return KeyboardEvent.VK_EQUALS;
            case Keyboard.KEY_ESCAPE:
                return KeyboardEvent.VK_ESCAPE;
            case Keyboard.KEY_F:
                return KeyboardEvent.VK_F;
            case Keyboard.KEY_F1:
                return KeyboardEvent.VK_F1;
            case Keyboard.KEY_F2:
                return KeyboardEvent.VK_F2;
            case Keyboard.KEY_F3:
                return KeyboardEvent.VK_F3;
            case Keyboard.KEY_F4:
                return KeyboardEvent.VK_F4;
            case Keyboard.KEY_F5:
                return KeyboardEvent.VK_F5;
            case Keyboard.KEY_F6:
                return KeyboardEvent.VK_F6;
            case Keyboard.KEY_F7:
                return KeyboardEvent.VK_F7;
            case Keyboard.KEY_F8:
                return KeyboardEvent.VK_F8;
            case Keyboard.KEY_F9:
                return KeyboardEvent.VK_F9;
            case Keyboard.KEY_F10:
                return KeyboardEvent.VK_F10;
            case Keyboard.KEY_F11:
                return KeyboardEvent.VK_F11;
            case Keyboard.KEY_F12:
                return KeyboardEvent.VK_F12;
            case Keyboard.KEY_G:
                return KeyboardEvent.VK_G;
            case Keyboard.KEY_H:
                return KeyboardEvent.VK_H;
            case Keyboard.KEY_HOME:
                return KeyboardEvent.VK_HOME;
            case Keyboard.KEY_I:
                return KeyboardEvent.VK_I;
            case Keyboard.KEY_INSERT:
                return KeyboardEvent.VK_INSERT;
            case Keyboard.KEY_J:
                return KeyboardEvent.VK_J;
            case Keyboard.KEY_K:
                return KeyboardEvent.VK_K;
            case Keyboard.KEY_L:
                return KeyboardEvent.VK_L;
            case Keyboard.KEY_LBRACKET:
                return KeyboardEvent.VK_BRACELEFT;
            case Keyboard.KEY_LCONTROL:
                return KeyboardEvent.VK_CONTROL;
            case Keyboard.KEY_LEFT:
                return KeyboardEvent.VK_LEFT;
            case Keyboard.KEY_LMETA:
                return KeyboardEvent.VK_META;
            case Keyboard.KEY_LSHIFT:
                return KeyboardEvent.VK_SHIFT;
            case Keyboard.KEY_M:
                return KeyboardEvent.VK_M;
            case Keyboard.KEY_MINUS:
                return KeyboardEvent.VK_MINUS;
            case Keyboard.KEY_MULTIPLY:
                return KeyboardEvent.VK_MULTIPLY;
            case Keyboard.KEY_N:
                return KeyboardEvent.VK_N;
            case Keyboard.KEY_NUMLOCK:
                return KeyboardEvent.VK_NUM_LOCK;
            case Keyboard.KEY_NUMPAD0:
                return KeyboardEvent.VK_NUMPAD0;
            case Keyboard.KEY_NUMPAD1:
                return KeyboardEvent.VK_NUMPAD1;
            case Keyboard.KEY_NUMPAD2:
                return KeyboardEvent.VK_NUMPAD2;
            case Keyboard.KEY_NUMPAD3:
                return KeyboardEvent.VK_NUMPAD3;
            case Keyboard.KEY_NUMPAD4:
                return KeyboardEvent.VK_NUMPAD4;
            case Keyboard.KEY_NUMPAD5:
                return KeyboardEvent.VK_NUMPAD5;
            case Keyboard.KEY_NUMPAD6:
                return KeyboardEvent.VK_NUMPAD6;
            case Keyboard.KEY_NUMPAD7:
                return KeyboardEvent.VK_NUMPAD7;
            case Keyboard.KEY_NUMPAD8:
                return KeyboardEvent.VK_NUMPAD8;
            case Keyboard.KEY_NUMPAD9:
                return KeyboardEvent.VK_NUMPAD9;
            case Keyboard.KEY_O:
                return KeyboardEvent.VK_O;
            case Keyboard.KEY_P:
                return KeyboardEvent.VK_P;
            case Keyboard.KEY_PAUSE:
                return KeyboardEvent.VK_PAUSE;
            case Keyboard.KEY_PERIOD:
                return KeyboardEvent.VK_PERIOD;
            case Keyboard.KEY_Q:
                return KeyboardEvent.VK_Q;
            case Keyboard.KEY_R:
                return KeyboardEvent.VK_R;
            case Keyboard.KEY_RETURN:
                return KeyboardEvent.VK_ENTER;
            case Keyboard.KEY_RIGHT:
                return KeyboardEvent.VK_RIGHT;
            case Keyboard.KEY_S:
                return KeyboardEvent.VK_S;
            case Keyboard.KEY_SCROLL:
                return KeyboardEvent.VK_SCROLL_LOCK;
            case Keyboard.KEY_SEMICOLON:
                return KeyboardEvent.VK_SEMICOLON;
            case Keyboard.KEY_SLASH:
                return KeyboardEvent.VK_SLASH;
            case Keyboard.KEY_SPACE:
                return KeyboardEvent.VK_SPACE;
            case Keyboard.KEY_STOP:
                return KeyboardEvent.VK_STOP;
            case Keyboard.KEY_SUBTRACT:
                return KeyboardEvent.VK_SUBTRACT;
            case Keyboard.KEY_T:
                return KeyboardEvent.VK_T;
            case Keyboard.KEY_TAB:
                return KeyboardEvent.VK_TAB;
            case Keyboard.KEY_U:
                return KeyboardEvent.VK_U;
            case Keyboard.KEY_UNDERLINE:
                return KeyboardEvent.VK_UNDERSCORE;
            case Keyboard.KEY_UP:
                return KeyboardEvent.VK_UP;
            case Keyboard.KEY_V:
                return KeyboardEvent.VK_V;
            case Keyboard.KEY_W:
                return KeyboardEvent.VK_W;
            case Keyboard.KEY_X:
                return KeyboardEvent.VK_X;
            case Keyboard.KEY_Y:
                return KeyboardEvent.VK_Y;
            case Keyboard.KEY_Z:
                return KeyboardEvent.VK_Z;
            default:
                return KeyboardEvent.VK_UNDEFINED;
        }
    }

    /**
     * Clean the keyboard manager and remove all event handler from the manager.
     */
    @Override
    public void clear() {
        receiver = null;
    }

    /**
     * Check if a key is pressed down.
     * 
     * @param key the key that shall be checked
     * @return <code>true</code> in case the key is pressed down
     */
    @Override
    public boolean isKeyDown(final int key) {
        if (!managerRunning) {
            return false;
        }
        final int keyCode = translateKeysIllarionLWJGL(key);
        final boolean result = buttonState[keyCode];
        if (!result && (keyCode == Keyboard.KEY_LCONTROL)) {
            return buttonState[Keyboard.KEY_RCONTROL];
        } else if (!result && (keyCode == Keyboard.KEY_LMENU)) {
            return buttonState[Keyboard.KEY_RMENU];
        } else if (!result && (keyCode == Keyboard.KEY_LMETA)) {
            return buttonState[Keyboard.KEY_RMETA];
        } else if (!result && (keyCode == Keyboard.KEY_LSHIFT)) {
            return buttonState[Keyboard.KEY_RSHIFT];
        } else if (!result && (keyCode == Keyboard.KEY_LBRACKET)) {
            return buttonState[Keyboard.KEY_RBRACKET];
        }
        return result;
    }

    /**
     * Check if a key is not pressed down.
     * 
     * @param key the key that shall be checked
     * @return <code>true</code> in case the key is not pressed down
     */
    @Override
    public boolean isKeyUp(final int key) {
        return !isKeyDown(key);
    }

    /**
     * Register a event handler that is notified about a keyboard event.
     * 
     * @param event the event handler that is registered to the manager
     */
    @Override
    public void registerEventHandler(final KeyboardEventReceiver event) {
        receiver = event;
    }

    /**
     * The main loop of the thread managing the input.
     */
    @Override
    @SuppressWarnings("nls")
    public void run() {
        while (running) {
            KeyboardData keyboardEvent = null;
            synchronized (data) {
                if (data.isEmpty()) {
                    try {
                        data.wait();
                    } catch (final InterruptedException e) {
                        LOGGER.warn("Keyboard manager worken up unexpected");
                    }
                    continue;
                }

                keyboardEvent = data.removeFirst();
            }

            if (receiver == null) {
                continue;
            }

            final int orgKeyCode = keyboardEvent.getKey();
            final int keyCode = translateKeysLWJGLIllarion(orgKeyCode);

            final boolean pressed = keyboardEvent.getState();
            final boolean repeated = keyboardEvent.isRepeated();
            final char eventChar = keyboardEvent.getCharacter();

            if (pressed && !repeated) {
                final KeyboardEvent event = KeyboardEvent.get();
                event.setEventData(keyCode, KeyboardEvent.EVENT_KEY_DOWN,
                    false, eventChar);
                receiver.handleKeyboardEvent(event);
                buttonState[orgKeyCode] = true;
            }
            if (pressed) {
                final KeyboardEvent event = KeyboardEvent.get();
                event.setEventData(keyCode, KeyboardEvent.EVENT_KEY_PRESSED,
                    repeated, eventChar);
                receiver.handleKeyboardEvent(event);
            }
            if (!pressed && !repeated) {
                final KeyboardEvent event = KeyboardEvent.get();
                event.setEventData(keyCode, KeyboardEvent.EVENT_KEY_UP, false,
                    eventChar);
                receiver.handleKeyboardEvent(event);
                buttonState[orgKeyCode] = false;
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
     * Destroy the keyboard manager and clear the handler. After this is done
     * the keyboard handler is not working at all anymore.
     */
    @Override
    public void shutdown() {
        if (!managerRunning) {
            return;
        }
        clear();
        Keyboard.destroy();
        managerRunning = false;
        synchronized (data) {
            data.notify();
        }
    }

    /**
     * Start the keyboard manager so its processes the keyboard input correctly.
     * Ensure that the target window is in focus so the keyboard manager is
     * binded to this.
     */
    @Override
    @SuppressWarnings("nls")
    public void startManager() {
        if (!Graphics.getInstance().getRenderDisplay()
            .isInputListenerSupported(null)) {
            throw new InputLWJGLException(
                "Input Handler is not supported with the graphic binding.");
        }
        while (!Display.isCreated()) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException ex) {
                // nothing to do
            }
        }
        if (!Keyboard.isCreated()) {
            try {
                Keyboard.create();
                Keyboard.enableRepeatEvents(true);
            } catch (final LWJGLException e) {
                LOGGER.error("Failed creating the keyboard", e);
            }
        } else {
            Keyboard.enableRepeatEvents(true);
        }

        Keyboard.poll();
        while (Keyboard.next()) {
            // clean the buffer
        }

        // Updates need to be called by the graphic loop, else everything messes
        // up.
        Graphics.getInstance().getRenderManager().addTask(new RenderTask() {
            @Override
            @SuppressWarnings("synthetic-access")
            public boolean render(final int delta) {
                if (managerRunning) {
                    updateKeyboardData();
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
     * Update the data of the keyboard.
     */
    protected void updateKeyboardData() {
        Keyboard.poll();

        synchronized (data) {
            synchronized (KeyboardData.LOCK) {
                while (Keyboard.next()) {
                    final KeyboardData dataBlock = KeyboardData.get();
                    dataBlock.set(Keyboard.getEventKey(),
                        Keyboard.getEventKeyState(), Keyboard.isRepeatEvent(),
                        Keyboard.getEventCharacter());
                    data.addLast(dataBlock);
                }
            }
            data.notify();
        }
    }
}
