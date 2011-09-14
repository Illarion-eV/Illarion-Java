/*
 * This file is part of the Illarion Nifty-GUI binding.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Nifty-GUI binding is free software: you can redistribute i
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * The Illarion Nifty-GUI binding is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Nifty-GUI binding. If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.nifty.renderer.input;

import illarion.graphics.Graphics;
import illarion.input.InputManager;
import illarion.input.KeyboardEvent;
import illarion.input.KeyboardManager;
import illarion.input.MouseEvent;
import illarion.input.MouseManager;
import illarion.input.receiver.KeyboardEventReceiverComplex;
import illarion.input.receiver.KeyboardEventReceiverPrimitive;
import illarion.input.receiver.MouseEventReceiverComplex;
import illarion.input.receiver.MouseEventReceiverPrimitive;
import de.lessvoid.nifty.NiftyInputConsumer;
import de.lessvoid.nifty.input.keyboard.KeyboardInputEvent;
import de.lessvoid.nifty.spi.input.InputSystem;

public final class IllarionInputSystem implements InputSystem,
    MouseEventReceiverPrimitive, KeyboardEventReceiverPrimitive {

    /**
     * Correct the y coordinate of the mouse location to meet the nifty
     * coordinate specifications.
     * 
     * @param orgY the illarion y coordinate
     * @return the nifty y coordinate
     */
    static int fixY(final int orgY) {
        return Graphics.getInstance().getRenderDisplay().getRenderArea()
            .getHeight()
            - orgY;
    }

    /**
     * Translate the keyboard key ID of Illarion to the fitting ID of the nifty
     * GUI.
     * 
     * @param illarionKey the key in the Illarion event system
     * @return the key in the nifty event system
     */
    private static int translateKeyboardIllarionNifty(final int illarionKey) {
        switch (illarionKey) {
            case KeyboardEvent.VK_0:
                return KeyboardInputEvent.KEY_0;
            case KeyboardEvent.VK_1:
                return KeyboardInputEvent.KEY_1;
            case KeyboardEvent.VK_2:
                return KeyboardInputEvent.KEY_2;
            case KeyboardEvent.VK_3:
                return KeyboardInputEvent.KEY_3;
            case KeyboardEvent.VK_4:
                return KeyboardInputEvent.KEY_4;
            case KeyboardEvent.VK_5:
                return KeyboardInputEvent.KEY_5;
            case KeyboardEvent.VK_6:
                return KeyboardInputEvent.KEY_6;
            case KeyboardEvent.VK_7:
                return KeyboardInputEvent.KEY_7;
            case KeyboardEvent.VK_8:
                return KeyboardInputEvent.KEY_8;
            case KeyboardEvent.VK_9:
                return KeyboardInputEvent.KEY_9;
            case KeyboardEvent.VK_A:
                return KeyboardInputEvent.KEY_A;
            case KeyboardEvent.VK_ADD:
                return KeyboardInputEvent.KEY_ADD;
            case KeyboardEvent.VK_AT:
                return KeyboardInputEvent.KEY_AT;
            case KeyboardEvent.VK_B:
                return KeyboardInputEvent.KEY_B;
            case KeyboardEvent.VK_BACK_SPACE:
                return KeyboardInputEvent.KEY_BACK;
            case KeyboardEvent.VK_BACK_SLASH:
                return KeyboardInputEvent.KEY_BACKSLASH;
            case KeyboardEvent.VK_C:
                return KeyboardInputEvent.KEY_C;
            case KeyboardEvent.VK_CAPS_LOCK:
                return KeyboardInputEvent.KEY_CAPITAL;
            case KeyboardEvent.VK_CIRCUMFLEX:
                return KeyboardInputEvent.KEY_CIRCUMFLEX;
            case KeyboardEvent.VK_COLON:
                return KeyboardInputEvent.KEY_COLON;
            case KeyboardEvent.VK_COMMA:
                return KeyboardInputEvent.KEY_COMMA;
            case KeyboardEvent.VK_D:
                return KeyboardInputEvent.KEY_D;
            case KeyboardEvent.VK_DECIMAL:
                return KeyboardInputEvent.KEY_DECIMAL;
            case KeyboardEvent.VK_DELETE:
                return KeyboardInputEvent.KEY_DELETE;
            case KeyboardEvent.VK_DIVIDE:
                return KeyboardInputEvent.KEY_DIVIDE;
            case KeyboardEvent.VK_DOWN:
                return KeyboardInputEvent.KEY_DOWN;
            case KeyboardEvent.VK_E:
                return KeyboardInputEvent.KEY_E;
            case KeyboardEvent.VK_END:
                return KeyboardInputEvent.KEY_END;
            case KeyboardEvent.VK_EQUALS:
                return KeyboardInputEvent.KEY_EQUALS;
            case KeyboardEvent.VK_ESCAPE:
                return KeyboardInputEvent.KEY_ESCAPE;
            case KeyboardEvent.VK_F:
                return KeyboardInputEvent.KEY_F;
            case KeyboardEvent.VK_F1:
                return KeyboardInputEvent.KEY_F1;
            case KeyboardEvent.VK_F2:
                return KeyboardInputEvent.KEY_F2;
            case KeyboardEvent.VK_F3:
                return KeyboardInputEvent.KEY_F3;
            case KeyboardEvent.VK_F4:
                return KeyboardInputEvent.KEY_F4;
            case KeyboardEvent.VK_F5:
                return KeyboardInputEvent.KEY_F5;
            case KeyboardEvent.VK_F6:
                return KeyboardInputEvent.KEY_F6;
            case KeyboardEvent.VK_F7:
                return KeyboardInputEvent.KEY_F7;
            case KeyboardEvent.VK_F8:
                return KeyboardInputEvent.KEY_F8;
            case KeyboardEvent.VK_F9:
                return KeyboardInputEvent.KEY_F9;
            case KeyboardEvent.VK_F10:
                return KeyboardInputEvent.KEY_F10;
            case KeyboardEvent.VK_F11:
                return KeyboardInputEvent.KEY_F11;
            case KeyboardEvent.VK_F12:
                return KeyboardInputEvent.KEY_F12;
            case KeyboardEvent.VK_G:
                return KeyboardInputEvent.KEY_G;
            case KeyboardEvent.VK_H:
                return KeyboardInputEvent.KEY_H;
            case KeyboardEvent.VK_HOME:
                return KeyboardInputEvent.KEY_HOME;
            case KeyboardEvent.VK_I:
                return KeyboardInputEvent.KEY_I;
            case KeyboardEvent.VK_INSERT:
                return KeyboardInputEvent.KEY_INSERT;
            case KeyboardEvent.VK_J:
                return KeyboardInputEvent.KEY_J;
            case KeyboardEvent.VK_K:
                return KeyboardInputEvent.KEY_K;
            case KeyboardEvent.VK_L:
                return KeyboardInputEvent.KEY_L;
            case KeyboardEvent.VK_BRACELEFT:
                return KeyboardInputEvent.KEY_LBRACKET;
            case KeyboardEvent.VK_CONTROL:
                return KeyboardInputEvent.KEY_LCONTROL;
            case KeyboardEvent.VK_LEFT:
                return KeyboardInputEvent.KEY_LEFT;
            case KeyboardEvent.VK_META:
                return KeyboardInputEvent.KEY_LMETA;
            case KeyboardEvent.VK_SHIFT:
                return KeyboardInputEvent.KEY_LSHIFT;
            case KeyboardEvent.VK_M:
                return KeyboardInputEvent.KEY_M;
            case KeyboardEvent.VK_MINUS:
                return KeyboardInputEvent.KEY_MINUS;
            case KeyboardEvent.VK_MULTIPLY:
                return KeyboardInputEvent.KEY_MULTIPLY;
            case KeyboardEvent.VK_N:
                return KeyboardInputEvent.KEY_N;
            case KeyboardEvent.VK_NUM_LOCK:
                return KeyboardInputEvent.KEY_NUMLOCK;
            case KeyboardEvent.VK_NUMPAD0:
                return KeyboardInputEvent.KEY_NUMPAD0;
            case KeyboardEvent.VK_NUMPAD1:
                return KeyboardInputEvent.KEY_NUMPAD1;
            case KeyboardEvent.VK_NUMPAD2:
                return KeyboardInputEvent.KEY_NUMPAD2;
            case KeyboardEvent.VK_NUMPAD3:
                return KeyboardInputEvent.KEY_NUMPAD3;
            case KeyboardEvent.VK_NUMPAD4:
                return KeyboardInputEvent.KEY_NUMPAD4;
            case KeyboardEvent.VK_NUMPAD5:
                return KeyboardInputEvent.KEY_NUMPAD5;
            case KeyboardEvent.VK_NUMPAD6:
                return KeyboardInputEvent.KEY_NUMPAD6;
            case KeyboardEvent.VK_NUMPAD7:
                return KeyboardInputEvent.KEY_NUMPAD7;
            case KeyboardEvent.VK_NUMPAD8:
                return KeyboardInputEvent.KEY_NUMPAD8;
            case KeyboardEvent.VK_NUMPAD9:
                return KeyboardInputEvent.KEY_NUMPAD9;
            case KeyboardEvent.VK_O:
                return KeyboardInputEvent.KEY_O;
            case KeyboardEvent.VK_P:
                return KeyboardInputEvent.KEY_P;
            case KeyboardEvent.VK_PAUSE:
                return KeyboardInputEvent.KEY_PAUSE;
            case KeyboardEvent.VK_PERIOD:
                return KeyboardInputEvent.KEY_PERIOD;
            case KeyboardEvent.VK_Q:
                return KeyboardInputEvent.KEY_Q;
            case KeyboardEvent.VK_R:
                return KeyboardInputEvent.KEY_R;
            case KeyboardEvent.VK_ENTER:
                return KeyboardInputEvent.KEY_RETURN;
            case KeyboardEvent.VK_RIGHT:
                return KeyboardInputEvent.KEY_RIGHT;
            case KeyboardEvent.VK_S:
                return KeyboardInputEvent.KEY_S;
            case KeyboardEvent.VK_SCROLL_LOCK:
                return KeyboardInputEvent.KEY_SCROLL;
            case KeyboardEvent.VK_SEMICOLON:
                return KeyboardInputEvent.KEY_SEMICOLON;
            case KeyboardEvent.VK_SLASH:
                return KeyboardInputEvent.KEY_SLASH;
            case KeyboardEvent.VK_SPACE:
                return KeyboardInputEvent.KEY_SPACE;
            case KeyboardEvent.VK_STOP:
                return KeyboardInputEvent.KEY_STOP;
            case KeyboardEvent.VK_SUBTRACT:
                return KeyboardInputEvent.KEY_SUBTRACT;
            case KeyboardEvent.VK_T:
                return KeyboardInputEvent.KEY_T;
            case KeyboardEvent.VK_TAB:
                return KeyboardInputEvent.KEY_TAB;
            case KeyboardEvent.VK_U:
                return KeyboardInputEvent.KEY_U;
            case KeyboardEvent.VK_UNDERSCORE:
                return KeyboardInputEvent.KEY_UNDERLINE;
            case KeyboardEvent.VK_UNDEFINED:
                return KeyboardInputEvent.KEY_UNLABELED;
            case KeyboardEvent.VK_UP:
                return KeyboardInputEvent.KEY_UP;
            case KeyboardEvent.VK_V:
                return KeyboardInputEvent.KEY_V;
            case KeyboardEvent.VK_W:
                return KeyboardInputEvent.KEY_W;
            case KeyboardEvent.VK_X:
                return KeyboardInputEvent.KEY_X;
            case KeyboardEvent.VK_Y:
                return KeyboardInputEvent.KEY_Y;
            case KeyboardEvent.VK_Z:
                return KeyboardInputEvent.KEY_Z;
            default:
                return -1;
        }
    }

    private static int translateMouseButtonIllarionNifty(
        final int illarionButton) {
        return illarionButton - 1;
    }

    private NiftyInputConsumer currentConsumer;

    private final KeyboardManager keyboardManager;

    private final MouseManager mouseManager;

    public IllarionInputSystem() {
        mouseManager = InputManager.getInstance().getMouseManager();
        keyboardManager = InputManager.getInstance().getKeyboardManager();

        mouseManager.registerEventHandler(this);
        keyboardManager.registerEventHandler(this);
    }

    @Override
    public void forwardEvents(final NiftyInputConsumer inputEventConsumer) {
        currentConsumer = inputEventConsumer;
        mouseManager.poll();
        keyboardManager.poll();
    }

    @Override
    public boolean handleKeyboardEvent(int key, char character, boolean down) {
        boolean isShiftDown = false;
        boolean isControlDown = false;
        if (down) {
            isShiftDown = (key == KeyboardEvent.VK_SHIFT);
            isControlDown = (key == KeyboardEvent.VK_CONTROL);
        }
        
        final KeyboardInputEvent niftyEvent =
            new KeyboardInputEvent(
                translateKeyboardIllarionNifty(key),
                character, down, isShiftDown, isControlDown);

        return currentConsumer.processKeyboardEvent(niftyEvent);
    }

    @Override
    public boolean handleMouseEvent(int mouseX, int mouseY, int wheelDelta,
        int button, boolean buttonDown) {
        
        return currentConsumer.processMouseEvent(mouseX,
            fixY(mouseY), wheelDelta,
            translateMouseButtonIllarionNifty(button),
            buttonDown);
    }

    @Override
    public void setMousePosition(final int x, final int y) {
        // the mouse is where the mouse is, that function should not work
    }

}
