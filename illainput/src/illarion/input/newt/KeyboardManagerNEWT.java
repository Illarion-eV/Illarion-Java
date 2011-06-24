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

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

import gnu.trove.list.array.TIntArrayList;

import illarion.graphics.Graphics;

import illarion.input.KeyboardEvent;
import illarion.input.KeyboardEventReceiver;
import illarion.input.KeyboardManager;

/**
 * The keyboard manager that uses the NEWT implementation to handle the keyboard
 * input to the client.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class KeyboardManagerNEWT implements KeyboardManager, KeyListener {
    /**
     * The last character that was detected. This is used to trigger repeated
     * events.
     */
    private char lastChar = (char) -1;

    /**
     * List of keys that are pressed down.
     */
    private final TIntArrayList pressedKeys = new TIntArrayList();

    /**
     * The receiver of all keyboard events.
     */
    private KeyboardEventReceiver receiver;

    /**
     * Translate the value of a key from the Illarion value to the Java value.
     * 
     * @param key the Illarion value of the key
     * @return the Java value of the key
     */
    private static int translateKeysIllarionJava(final int key) {
        switch (key) {
            case KeyboardEvent.VK_0:
                return KeyEvent.VK_0;
            case KeyboardEvent.VK_1:
                return KeyEvent.VK_1;
            case KeyboardEvent.VK_2:
                return KeyEvent.VK_2;
            case KeyboardEvent.VK_3:
                return KeyEvent.VK_3;
            case KeyboardEvent.VK_4:
                return KeyEvent.VK_4;
            case KeyboardEvent.VK_5:
                return KeyEvent.VK_5;
            case KeyboardEvent.VK_6:
                return KeyEvent.VK_6;
            case KeyboardEvent.VK_7:
                return KeyEvent.VK_7;
            case KeyboardEvent.VK_8:
                return KeyEvent.VK_8;
            case KeyboardEvent.VK_9:
                return KeyEvent.VK_9;
            case KeyboardEvent.VK_A:
                return KeyEvent.VK_A;
            case KeyboardEvent.VK_ADD:
                return KeyEvent.VK_ADD;
            case KeyboardEvent.VK_AT:
                return KeyEvent.VK_AT;
            case KeyboardEvent.VK_B:
                return KeyEvent.VK_B;
            case KeyboardEvent.VK_BACK_SPACE:
                return KeyEvent.VK_BACK_SPACE;
            case KeyboardEvent.VK_BACK_SLASH:
                return KeyEvent.VK_BACK_SLASH;
            case KeyboardEvent.VK_C:
                return KeyEvent.VK_C;
            case KeyboardEvent.VK_CAPS_LOCK:
                return KeyEvent.VK_CAPS_LOCK;
            case KeyboardEvent.VK_CIRCUMFLEX:
                return KeyEvent.VK_CIRCUMFLEX;
            case KeyboardEvent.VK_COLON:
                return KeyEvent.VK_COLON;
            case KeyboardEvent.VK_COMMA:
                return KeyEvent.VK_COMMA;
            case KeyboardEvent.VK_D:
                return KeyEvent.VK_D;
            case KeyboardEvent.VK_DECIMAL:
                return KeyEvent.VK_DECIMAL;
            case KeyboardEvent.VK_DELETE:
                return KeyEvent.VK_DELETE;
            case KeyboardEvent.VK_DIVIDE:
                return KeyEvent.VK_DIVIDE;
            case KeyboardEvent.VK_DOWN:
                return KeyEvent.VK_DOWN;
            case KeyboardEvent.VK_E:
                return KeyEvent.VK_E;
            case KeyboardEvent.VK_END:
                return KeyEvent.VK_END;
            case KeyboardEvent.VK_EQUALS:
                return KeyEvent.VK_EQUALS;
            case KeyboardEvent.VK_ESCAPE:
                return KeyEvent.VK_ESCAPE;
            case KeyboardEvent.VK_F:
                return KeyEvent.VK_F;
            case KeyboardEvent.VK_F1:
                return KeyEvent.VK_F1;
            case KeyboardEvent.VK_F2:
                return KeyEvent.VK_F2;
            case KeyboardEvent.VK_F3:
                return KeyEvent.VK_F3;
            case KeyboardEvent.VK_F4:
                return KeyEvent.VK_F4;
            case KeyboardEvent.VK_F5:
                return KeyEvent.VK_F5;
            case KeyboardEvent.VK_F6:
                return KeyEvent.VK_F6;
            case KeyboardEvent.VK_F7:
                return KeyEvent.VK_F7;
            case KeyboardEvent.VK_F8:
                return KeyEvent.VK_F8;
            case KeyboardEvent.VK_F9:
                return KeyEvent.VK_F9;
            case KeyboardEvent.VK_F10:
                return KeyEvent.VK_F10;
            case KeyboardEvent.VK_F11:
                return KeyEvent.VK_F11;
            case KeyboardEvent.VK_F12:
                return KeyEvent.VK_F12;
            case KeyboardEvent.VK_G:
                return KeyEvent.VK_G;
            case KeyboardEvent.VK_H:
                return KeyEvent.VK_H;
            case KeyboardEvent.VK_HOME:
                return KeyEvent.VK_HOME;
            case KeyboardEvent.VK_I:
                return KeyEvent.VK_I;
            case KeyboardEvent.VK_INSERT:
                return KeyEvent.VK_INSERT;
            case KeyboardEvent.VK_J:
                return KeyEvent.VK_J;
            case KeyboardEvent.VK_K:
                return KeyEvent.VK_K;
            case KeyboardEvent.VK_L:
                return KeyEvent.VK_L;
            case KeyboardEvent.VK_BRACELEFT:
                return KeyEvent.VK_BRACELEFT;
            case KeyboardEvent.VK_CONTROL:
                return KeyEvent.VK_CONTROL;
            case KeyboardEvent.VK_LEFT:
                return KeyEvent.VK_LEFT;
            case KeyboardEvent.VK_META:
                return KeyEvent.VK_META;
            case KeyboardEvent.VK_SHIFT:
                return KeyEvent.VK_SHIFT;
            case KeyboardEvent.VK_M:
                return KeyEvent.VK_M;
            case KeyboardEvent.VK_MINUS:
                return KeyEvent.VK_MINUS;
            case KeyboardEvent.VK_MULTIPLY:
                return KeyEvent.VK_MULTIPLY;
            case KeyboardEvent.VK_N:
                return KeyEvent.VK_N;
            case KeyboardEvent.VK_NUM_LOCK:
                return KeyEvent.VK_NUM_LOCK;
            case KeyboardEvent.VK_NUMPAD0:
                return KeyEvent.VK_NUMPAD0;
            case KeyboardEvent.VK_NUMPAD1:
                return KeyEvent.VK_NUMPAD1;
            case KeyboardEvent.VK_NUMPAD2:
                return KeyEvent.VK_NUMPAD2;
            case KeyboardEvent.VK_NUMPAD3:
                return KeyEvent.VK_NUMPAD3;
            case KeyboardEvent.VK_NUMPAD4:
                return KeyEvent.VK_NUMPAD4;
            case KeyboardEvent.VK_NUMPAD5:
                return KeyEvent.VK_NUMPAD5;
            case KeyboardEvent.VK_NUMPAD6:
                return KeyEvent.VK_NUMPAD6;
            case KeyboardEvent.VK_NUMPAD7:
                return KeyEvent.VK_NUMPAD7;
            case KeyboardEvent.VK_NUMPAD8:
                return KeyEvent.VK_NUMPAD8;
            case KeyboardEvent.VK_NUMPAD9:
                return KeyEvent.VK_NUMPAD9;
            case KeyboardEvent.VK_O:
                return KeyEvent.VK_O;
            case KeyboardEvent.VK_P:
                return KeyEvent.VK_P;
            case KeyboardEvent.VK_PAUSE:
                return KeyEvent.VK_PAUSE;
            case KeyboardEvent.VK_PERIOD:
                return KeyEvent.VK_PERIOD;
            case KeyboardEvent.VK_Q:
                return KeyEvent.VK_Q;
            case KeyboardEvent.VK_R:
                return KeyEvent.VK_R;
            case KeyboardEvent.VK_ENTER:
                return KeyEvent.VK_ENTER;
            case KeyboardEvent.VK_RIGHT:
                return KeyEvent.VK_RIGHT;
            case KeyboardEvent.VK_S:
                return KeyEvent.VK_S;
            case KeyboardEvent.VK_SCROLL_LOCK:
                return KeyEvent.VK_SCROLL_LOCK;
            case KeyboardEvent.VK_SEMICOLON:
                return KeyEvent.VK_SEMICOLON;
            case KeyboardEvent.VK_SLASH:
                return KeyEvent.VK_SLASH;
            case KeyboardEvent.VK_SPACE:
                return KeyEvent.VK_SPACE;
            case KeyboardEvent.VK_STOP:
                return KeyEvent.VK_STOP;
            case KeyboardEvent.VK_SUBTRACT:
                return KeyEvent.VK_SUBTRACT;
            case KeyboardEvent.VK_T:
                return KeyEvent.VK_T;
            case KeyboardEvent.VK_TAB:
                return KeyEvent.VK_TAB;
            case KeyboardEvent.VK_U:
                return KeyEvent.VK_U;
            case KeyboardEvent.VK_UNDERSCORE:
                return KeyEvent.VK_UNDERSCORE;
            case KeyboardEvent.VK_UNDEFINED:
                return KeyEvent.VK_UNDEFINED;
            case KeyboardEvent.VK_UP:
                return KeyEvent.VK_UP;
            case KeyboardEvent.VK_V:
                return KeyEvent.VK_V;
            case KeyboardEvent.VK_W:
                return KeyEvent.VK_W;
            case KeyboardEvent.VK_X:
                return KeyEvent.VK_X;
            case KeyboardEvent.VK_Y:
                return KeyEvent.VK_Y;
            case KeyboardEvent.VK_Z:
                return KeyEvent.VK_Z;
            default:
                return -1;
        }
    }

    /**
     * Translate the value of a key from the Java value to the Illarion value.
     * 
     * @param key the Java value of the key
     * @return the Illarion value of the key
     */
    private static int translateKeysJavaIllarion(final int key) {
        switch (key) {
            case KeyEvent.VK_0:
                return KeyboardEvent.VK_0;
            case KeyEvent.VK_1:
                return KeyboardEvent.VK_1;
            case KeyEvent.VK_2:
                return KeyboardEvent.VK_2;
            case KeyEvent.VK_3:
                return KeyboardEvent.VK_3;
            case KeyEvent.VK_4:
                return KeyboardEvent.VK_4;
            case KeyEvent.VK_5:
                return KeyboardEvent.VK_5;
            case KeyEvent.VK_6:
                return KeyboardEvent.VK_6;
            case KeyEvent.VK_7:
                return KeyboardEvent.VK_7;
            case KeyEvent.VK_8:
                return KeyboardEvent.VK_8;
            case KeyEvent.VK_9:
                return KeyboardEvent.VK_9;
            case KeyEvent.VK_A:
                return KeyboardEvent.VK_A;
            case KeyEvent.VK_ADD:
                return KeyboardEvent.VK_ADD;
            case KeyEvent.VK_AT:
                return KeyboardEvent.VK_AT;
            case KeyEvent.VK_B:
                return KeyboardEvent.VK_B;
            case KeyEvent.VK_BACK_SPACE:
                return KeyboardEvent.VK_BACK_SPACE;
            case KeyEvent.VK_BACK_SLASH:
                return KeyboardEvent.VK_BACK_SLASH;
            case KeyEvent.VK_C:
                return KeyboardEvent.VK_C;
            case KeyEvent.VK_CAPS_LOCK:
                return KeyboardEvent.VK_CAPS_LOCK;
            case KeyEvent.VK_CIRCUMFLEX:
                return KeyboardEvent.VK_CIRCUMFLEX;
            case KeyEvent.VK_COLON:
                return KeyboardEvent.VK_COLON;
            case KeyEvent.VK_COMMA:
                return KeyboardEvent.VK_COMMA;
            case KeyEvent.VK_D:
                return KeyboardEvent.VK_D;
            case KeyEvent.VK_DECIMAL:
                return KeyboardEvent.VK_DECIMAL;
            case KeyEvent.VK_DELETE:
                return KeyboardEvent.VK_DELETE;
            case KeyEvent.VK_DIVIDE:
                return KeyboardEvent.VK_DIVIDE;
            case KeyEvent.VK_DOWN:
                return KeyboardEvent.VK_DOWN;
            case KeyEvent.VK_E:
                return KeyboardEvent.VK_E;
            case KeyEvent.VK_END:
                return KeyboardEvent.VK_END;
            case KeyEvent.VK_EQUALS:
                return KeyboardEvent.VK_EQUALS;
            case KeyEvent.VK_ESCAPE:
                return KeyboardEvent.VK_ESCAPE;
            case KeyEvent.VK_F:
                return KeyboardEvent.VK_F;
            case KeyEvent.VK_F1:
                return KeyboardEvent.VK_F1;
            case KeyEvent.VK_F2:
                return KeyboardEvent.VK_F2;
            case KeyEvent.VK_F3:
                return KeyboardEvent.VK_F3;
            case KeyEvent.VK_F4:
                return KeyboardEvent.VK_F4;
            case KeyEvent.VK_F5:
                return KeyboardEvent.VK_F5;
            case KeyEvent.VK_F6:
                return KeyboardEvent.VK_F6;
            case KeyEvent.VK_F7:
                return KeyboardEvent.VK_F7;
            case KeyEvent.VK_F8:
                return KeyboardEvent.VK_F8;
            case KeyEvent.VK_F9:
                return KeyboardEvent.VK_F9;
            case KeyEvent.VK_F10:
                return KeyboardEvent.VK_F10;
            case KeyEvent.VK_F11:
                return KeyboardEvent.VK_F11;
            case KeyEvent.VK_F12:
                return KeyboardEvent.VK_F12;
            case KeyEvent.VK_G:
                return KeyboardEvent.VK_G;
            case KeyEvent.VK_H:
                return KeyboardEvent.VK_H;
            case KeyEvent.VK_HOME:
                return KeyboardEvent.VK_HOME;
            case KeyEvent.VK_I:
                return KeyboardEvent.VK_I;
            case KeyEvent.VK_INSERT:
                return KeyboardEvent.VK_INSERT;
            case KeyEvent.VK_J:
                return KeyboardEvent.VK_J;
            case KeyEvent.VK_K:
                return KeyboardEvent.VK_K;
            case KeyEvent.VK_L:
                return KeyboardEvent.VK_L;
            case KeyEvent.VK_BRACELEFT:
                return KeyboardEvent.VK_BRACELEFT;
            case KeyEvent.VK_CONTROL:
                return KeyboardEvent.VK_CONTROL;
            case KeyEvent.VK_LEFT:
                return KeyboardEvent.VK_LEFT;
            case KeyEvent.VK_META:
                return KeyboardEvent.VK_META;
            case KeyEvent.VK_SHIFT:
                return KeyboardEvent.VK_SHIFT;
            case KeyEvent.VK_M:
                return KeyboardEvent.VK_M;
            case KeyEvent.VK_MINUS:
                return KeyboardEvent.VK_MINUS;
            case KeyEvent.VK_MULTIPLY:
                return KeyboardEvent.VK_MULTIPLY;
            case KeyEvent.VK_N:
                return KeyboardEvent.VK_N;
            case KeyEvent.VK_NUM_LOCK:
                return KeyboardEvent.VK_NUM_LOCK;
            case KeyEvent.VK_NUMPAD0:
                return KeyboardEvent.VK_NUMPAD0;
            case KeyEvent.VK_NUMPAD1:
                return KeyboardEvent.VK_NUMPAD1;
            case KeyEvent.VK_NUMPAD2:
                return KeyboardEvent.VK_NUMPAD2;
            case KeyEvent.VK_NUMPAD3:
                return KeyboardEvent.VK_NUMPAD3;
            case KeyEvent.VK_NUMPAD4:
                return KeyboardEvent.VK_NUMPAD4;
            case KeyEvent.VK_NUMPAD5:
                return KeyboardEvent.VK_NUMPAD5;
            case KeyEvent.VK_NUMPAD6:
                return KeyboardEvent.VK_NUMPAD6;
            case KeyEvent.VK_NUMPAD7:
                return KeyboardEvent.VK_NUMPAD7;
            case KeyEvent.VK_NUMPAD8:
                return KeyboardEvent.VK_NUMPAD8;
            case KeyEvent.VK_NUMPAD9:
                return KeyboardEvent.VK_NUMPAD9;
            case KeyEvent.VK_O:
                return KeyboardEvent.VK_O;
            case KeyEvent.VK_P:
                return KeyboardEvent.VK_P;
            case KeyEvent.VK_PAUSE:
                return KeyboardEvent.VK_PAUSE;
            case KeyEvent.VK_PERIOD:
                return KeyboardEvent.VK_PERIOD;
            case KeyEvent.VK_Q:
                return KeyboardEvent.VK_Q;
            case KeyEvent.VK_R:
                return KeyboardEvent.VK_R;
            case KeyEvent.VK_ENTER:
                return KeyboardEvent.VK_ENTER;
            case KeyEvent.VK_RIGHT:
                return KeyboardEvent.VK_RIGHT;
            case KeyEvent.VK_S:
                return KeyboardEvent.VK_S;
            case KeyEvent.VK_SCROLL_LOCK:
                return KeyboardEvent.VK_SCROLL_LOCK;
            case KeyEvent.VK_SEMICOLON:
                return KeyboardEvent.VK_SEMICOLON;
            case KeyEvent.VK_SLASH:
                return KeyboardEvent.VK_SLASH;
            case KeyEvent.VK_SPACE:
                return KeyboardEvent.VK_SPACE;
            case KeyEvent.VK_STOP:
                return KeyboardEvent.VK_STOP;
            case KeyEvent.VK_SUBTRACT:
                return KeyboardEvent.VK_SUBTRACT;
            case KeyEvent.VK_T:
                return KeyboardEvent.VK_T;
            case KeyEvent.VK_TAB:
                return KeyboardEvent.VK_TAB;
            case KeyEvent.VK_U:
                return KeyboardEvent.VK_U;
            case KeyEvent.VK_UNDERSCORE:
                return KeyboardEvent.VK_UNDERSCORE;
            case KeyEvent.VK_UP:
                return KeyboardEvent.VK_UP;
            case KeyEvent.VK_V:
                return KeyboardEvent.VK_V;
            case KeyEvent.VK_W:
                return KeyboardEvent.VK_W;
            case KeyEvent.VK_X:
                return KeyboardEvent.VK_X;
            case KeyEvent.VK_Y:
                return KeyboardEvent.VK_Y;
            case KeyEvent.VK_Z:
                return KeyboardEvent.VK_Z;
            default:
                return KeyboardEvent.VK_UNDEFINED;
        }
    }

    /**
     * Clear the keyboard manager and remove included receiver.
     */
    @Override
    public void clear() {
        receiver = null;
    }

    /**
     * Check if a key with a specified ID is currently pressed down.
     */
    @Override
    public boolean isKeyDown(final int key) {
        return pressedKeys.contains(translateKeysIllarionJava(key));
    }

    /**
     * Check if a key with a specified ID is currently not pressed down.
     */
    @Override
    public boolean isKeyUp(final int key) {
        return !pressedKeys.contains(translateKeysIllarionJava(key));
    }

    /**
     * Triggered in case a key is pressed.
     * 
     * @param e the KeyEvent that is supposed to be handled by this function
     */
    @Override
    public void keyPressed(final KeyEvent e) {
        final KeyboardEvent event = KeyboardEvent.get();
        event.setEventData(translateKeysJavaIllarion(e.getKeyCode()),
            KeyboardEvent.EVENT_KEY_DOWN, false, (char) 0);
        pressedKeys.add(e.getKeyCode());
        if (receiver != null) {
            receiver.handleKeyboardEvent(event);
        }
    }

    /**
     * Triggered in case a key is released.
     * 
     * @param e the KeyEvent that is supposed to be handled by this function
     */
    @Override
    public void keyReleased(final KeyEvent e) {
        final KeyboardEvent event = KeyboardEvent.get();
        event.setEventData(translateKeysJavaIllarion(e.getKeyCode()),
            KeyboardEvent.EVENT_KEY_UP, false, (char) 0);

        final int index = pressedKeys.indexOf(e.getKeyCode());
        if (index >= 0) {
            pressedKeys.removeAt(index);
        }
        if (receiver != null) {
            receiver.handleKeyboardEvent(event);
        }
        lastChar = (char) -1;
    }

    /**
     * Triggered in case a UNICODE character is typed.
     * 
     * @param e the KeyEvent that is supposed to be handled by this function
     */
    @Override
    public void keyTyped(final KeyEvent e) {
        final boolean repeated = (lastChar == e.getKeyChar());
        final KeyboardEvent event = KeyboardEvent.get();
        event.setEventData(KeyboardEvent.VK_UNDEFINED,
            KeyboardEvent.EVENT_KEY_PRESSED, repeated, e.getKeyChar());
        lastChar = e.getKeyChar();
        if (receiver != null) {
            receiver.handleKeyboardEvent(event);
        }
    }

    /**
     * Set the event handler that receivers all input this keyboard manager
     * fetched.
     * 
     * @param event the event receiver that shall be used from now on
     */
    @Override
    public void registerEventHandler(final KeyboardEventReceiver event) {
        receiver = event;
    }

    /**
     * Shut the Keyboard Manager down by removing the needed key listeners.
     */
    @Override
    public void shutdown() {
        Graphics.getInstance().getRenderDisplay().removeInputListener(this);
    }

    /**
     * Start the manager by setting up the required listeners.
     */
    @SuppressWarnings("nls")
    @Override
    public void startManager() {
        if (!Graphics.getInstance().getRenderDisplay()
            .isInputListenerSupported(this)) {
            throw new InputNEWTException(
                "Input Handler is not supported with the graphic binding.");
        }
        Graphics.getInstance().getRenderDisplay().addInputListener(this);
    }
}
