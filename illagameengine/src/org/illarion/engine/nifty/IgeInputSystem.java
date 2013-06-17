/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.nifty;

import de.lessvoid.nifty.NiftyInputConsumer;
import de.lessvoid.nifty.input.keyboard.KeyboardInputEvent;
import de.lessvoid.nifty.spi.input.InputSystem;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;
import org.illarion.engine.input.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the input system that is used to forward the input data provided by the game engine to the Nifty-GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class IgeInputSystem implements InputSystem, InputListener {
    private Key stalledKeyDownKey;

    @Override
    public void keyDown(@Nonnull final Key key) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if (input.isForwardingEnabled(ForwardingTarget.Keyboard)) {
            listener.keyDown(key);
        } else {
            final int keyCode = getNiftyKeyId(key);
            final boolean shiftDown = input.isAnyKeyDown(Key.LeftShift, Key.RightShift);
            final boolean controlDown = input.isAnyKeyDown(Key.LeftCtrl, Key.RightCtrl);
            final KeyboardInputEvent event = new KeyboardInputEvent(keyCode, Character.MIN_VALUE, true, shiftDown,
                    controlDown);

            if (!currentConsumer.processKeyboardEvent(event)) {
                stalledKeyDownKey = key;
            } else {
                stalledKeyDownKey = null;
            }
        }
    }

    @Override
    public void keyUp(@Nonnull final Key key) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if (input.isForwardingEnabled(ForwardingTarget.Keyboard)) {
            listener.keyUp(key);
        } else {
            final int keyCode = getNiftyKeyId(key);
            final boolean shiftDown = input.isAnyKeyDown(Key.LeftShift, Key.RightShift);
            final boolean controlDown = input.isAnyKeyDown(Key.LeftCtrl, Key.RightCtrl);
            final KeyboardInputEvent event = new KeyboardInputEvent(keyCode, Character.MIN_VALUE, false, shiftDown,
                    controlDown);

            if (!currentConsumer.processKeyboardEvent(event)) {
                listener.keyUp(key);
            }
        }
    }

    @Override
    public void keyTyped(final char character) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }

        if (input.isForwardingEnabled(ForwardingTarget.Keyboard)) {
            listener.keyTyped(character);
        } else {

            if (Character.isDefined(character)) {
                final boolean shiftDown = input.isAnyKeyDown(Key.LeftShift, Key.RightShift);
                final boolean controlDown = input.isAnyKeyDown(Key.LeftCtrl, Key.RightCtrl);
                final KeyboardInputEvent event = new KeyboardInputEvent(-1, character, true, shiftDown, controlDown);
                if (!currentConsumer.processKeyboardEvent(event) && (stalledKeyDownKey != null)) {
                    listener.keyDown(stalledKeyDownKey);
                    listener.keyTyped(character);
                }
            } else {
                if (stalledKeyDownKey != null) {
                    listener.keyDown(stalledKeyDownKey);
                }
            }

            stalledKeyDownKey = null;
        }
    }

    /**
     * The amount of mouse click events that should be consumed by the Nifty-GUI.
     */
    private int consumeClicks;

    private boolean receivedClick;

    @Override
    public void buttonDown(final int mouseX, final int mouseY, @Nonnull final Button button) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if (input.isForwardingEnabled(ForwardingTarget.Mouse)) {
            listener.buttonDown(mouseX, mouseY, button);
        } else {
            final int buttonKey = getNiftyButtonKey(button);
            if (currentConsumer.processMouseEvent(mouseX, mouseY, 0, buttonKey, true)) {
                consumeClicks++;
                receivedClick = false;
            } else {
                listener.buttonDown(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void buttonUp(final int mouseX, final int mouseY, @Nonnull final Button button) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if (input.isForwardingEnabled(ForwardingTarget.Mouse)) {
            listener.buttonUp(mouseX, mouseY, button);
        } else {
            final int buttonKey = getNiftyButtonKey(button);
            if (!receivedClick) {
                consumeClicks--;
            }
            if (!currentConsumer.processMouseEvent(mouseX, mouseY, 0, buttonKey, false)) {
                listener.buttonUp(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void buttonClicked(final int mouseX, final int mouseY, @Nonnull final Button button, final int count) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if ((consumeClicks == 0) || input.isForwardingEnabled(ForwardingTarget.Mouse)) {
            listener.buttonClicked(mouseX, mouseY, button, count);
        } else {
            consumeClicks--;
            receivedClick = true;
            if (count > 1) {
                buttonClicked(mouseX, mouseY, button, count - 1);
            }
        }
    }

    @Override
    public void mouseMoved(final int mouseX, final int mouseY) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if (input.isForwardingEnabled(ForwardingTarget.Mouse) ||
                !currentConsumer.processMouseEvent(mouseX, mouseY, 0, -1, false)) {
            listener.mouseMoved(mouseX, mouseY);
        }
        consumeClicks = 0;
    }

    @Override
    public void mouseDragged(@Nonnull final Button button, final int fromX, final int fromY, final int toX, final int toY) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if (input.isForwardingEnabled(ForwardingTarget.Mouse)) {
            listener.mouseDragged(button, fromX, fromY, toX, toY);
        } else {
            final int buttonKey = getNiftyButtonKey(button);
            final boolean startUsed = currentConsumer.processMouseEvent(fromX, fromY, 0, buttonKey, true);
            final boolean endUsed = currentConsumer.processMouseEvent(toX, toY, 0, buttonKey, true);
            if (!(startUsed || endUsed)) {
                listener.mouseDragged(button, fromX, fromY, toX, toY);
            }
            consumeClicks = 0;
        }
    }

    @Override
    public void mouseWheelMoved(final int mouseX, final int mouseY, final int delta) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if (input.isForwardingEnabled(ForwardingTarget.Mouse) ||
                !currentConsumer.processMouseEvent(mouseX, mouseY, delta, -1, false)) {
            listener.mouseWheelMoved(mouseX, mouseY, delta);
        }
        consumeClicks = 0;
    }

    /**
     * Get the Nifty-GUI button key for a mouse button.
     *
     * @param button the button
     * @return the button key for the Nifty-GUI
     */
    private static int getNiftyButtonKey(@Nonnull final Button button) {
        switch (button) {
            case Left:
                return 0;
            case Right:
                return 1;
            case Middle:
                return 2;
        }
        return -1;
    }

    /**
     * Get the Nifty-GUI key code for a keyboard key.
     *
     * @param key the key
     * @return the Nifty-GUI key code
     */
    @SuppressWarnings("SwitchStatementWithTooManyBranches")
    private static int getNiftyKeyId(@Nonnull final Key key) {
        switch (key) {
            case A:
                return KeyboardInputEvent.KEY_A;
            case B:
                return KeyboardInputEvent.KEY_B;
            case C:
                return KeyboardInputEvent.KEY_C;
            case D:
                return KeyboardInputEvent.KEY_D;
            case E:
                return KeyboardInputEvent.KEY_E;
            case F:
                return KeyboardInputEvent.KEY_F;
            case G:
                return KeyboardInputEvent.KEY_G;
            case H:
                return KeyboardInputEvent.KEY_H;
            case I:
                return KeyboardInputEvent.KEY_I;
            case J:
                return KeyboardInputEvent.KEY_J;
            case K:
                return KeyboardInputEvent.KEY_K;
            case L:
                return KeyboardInputEvent.KEY_L;
            case M:
                return KeyboardInputEvent.KEY_M;
            case N:
                return KeyboardInputEvent.KEY_N;
            case O:
                return KeyboardInputEvent.KEY_O;
            case P:
                return KeyboardInputEvent.KEY_P;
            case Q:
                return KeyboardInputEvent.KEY_Q;
            case R:
                return KeyboardInputEvent.KEY_R;
            case S:
                return KeyboardInputEvent.KEY_S;
            case T:
                return KeyboardInputEvent.KEY_T;
            case U:
                return KeyboardInputEvent.KEY_U;
            case V:
                return KeyboardInputEvent.KEY_V;
            case W:
                return KeyboardInputEvent.KEY_W;
            case X:
                return KeyboardInputEvent.KEY_X;
            case Y:
                return KeyboardInputEvent.KEY_Y;
            case Z:
                return KeyboardInputEvent.KEY_Z;
            case LeftShift:
                return KeyboardInputEvent.KEY_LSHIFT;
            case RightShift:
                return KeyboardInputEvent.KEY_RSHIFT;
            case LeftAlt:
                return -1;
            case RightAlt:
                return -1;
            case LeftCtrl:
                return KeyboardInputEvent.KEY_LCONTROL;
            case RightCtrl:
                return KeyboardInputEvent.KEY_RCONTROL;
            case CursorLeft:
                return KeyboardInputEvent.KEY_LEFT;
            case CursorRight:
                return KeyboardInputEvent.KEY_RIGHT;
            case CursorUp:
                return KeyboardInputEvent.KEY_UP;
            case CursorDown:
                return KeyboardInputEvent.KEY_DOWN;
            case Enter:
                return KeyboardInputEvent.KEY_RETURN;
            case Backspace:
                return KeyboardInputEvent.KEY_BACK;
            case NumPad0:
                return KeyboardInputEvent.KEY_NUMPAD0;
            case NumPad1:
                return KeyboardInputEvent.KEY_NUMPAD1;
            case NumPad2:
                return KeyboardInputEvent.KEY_NUMPAD2;
            case NumPad3:
                return KeyboardInputEvent.KEY_NUMPAD3;
            case NumPad4:
                return KeyboardInputEvent.KEY_NUMPAD4;
            case NumPad5:
                return KeyboardInputEvent.KEY_NUMPAD5;
            case NumPad6:
                return KeyboardInputEvent.KEY_NUMPAD6;
            case NumPad7:
                return KeyboardInputEvent.KEY_NUMPAD7;
            case NumPad8:
                return KeyboardInputEvent.KEY_NUMPAD8;
            case NumPad9:
                return KeyboardInputEvent.KEY_NUMPAD9;
            case NumLock:
                return KeyboardInputEvent.KEY_NUMLOCK;
            case Escape:
                return KeyboardInputEvent.KEY_ESCAPE;
            case F1:
                return KeyboardInputEvent.KEY_F1;
            case F2:
                return KeyboardInputEvent.KEY_F2;
            case F3:
                return KeyboardInputEvent.KEY_F3;
            case F4:
                return KeyboardInputEvent.KEY_F4;
            case F5:
                return KeyboardInputEvent.KEY_F5;
            case F6:
                return KeyboardInputEvent.KEY_F6;
            case F7:
                return KeyboardInputEvent.KEY_F7;
            case F8:
                return KeyboardInputEvent.KEY_F8;
            case F9:
                return KeyboardInputEvent.KEY_F9;
            case F10:
                return KeyboardInputEvent.KEY_F10;
            case F11:
                return KeyboardInputEvent.KEY_F11;
            case F12:
                return KeyboardInputEvent.KEY_F12;
            case Insert:
                return KeyboardInputEvent.KEY_INSERT;
            case Delete:
                return KeyboardInputEvent.KEY_DELETE;
            case Home:
                return KeyboardInputEvent.KEY_HOME;
            case End:
                return KeyboardInputEvent.KEY_END;
            case PageUp:
                return KeyboardInputEvent.KEY_PRIOR;
            case PageDown:
                return KeyboardInputEvent.KEY_NEXT;
            case Tab:
                return KeyboardInputEvent.KEY_TAB;
        }
        return -1;
    }

    @Override
    public void setResourceLoader(final NiftyResourceLoader niftyResourceLoader) {
        // not needed
    }

    /**
     * The input implementation of the engine.
     */
    @Nonnull
    private final Input input;

    /**
     * Create a new input device and set the input implementation that provides the input data.
     *
     * @param input    the input implementation
     * @param listener the listener that receives input data was was not processed by the Nifty-GUI
     */
    public IgeInputSystem(@Nonnull final Input input, @Nonnull final InputListener listener) {
        input.setListener(this);
        this.input = input;
        this.listener = listener;
    }

    /**
     * This is the consumer that receives the input data. This is {@code null} while Nifty is not expecting input data.
     */
    @Nullable
    private NiftyInputConsumer currentConsumer;

    /**
     * This is the listener that is supposed to receive the input data that was not processed by the Nifty-GUI.
     */
    @Nonnull
    private final InputListener listener;

    @Override
    public void forwardEvents(@Nonnull final NiftyInputConsumer inputEventConsumer) {
        currentConsumer = inputEventConsumer;
        input.poll();
        currentConsumer = null;
    }

    @Override
    public void setMousePosition(final int x, final int y) {
        input.setMouseLocation(x, y);
    }
}
