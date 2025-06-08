/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package org.illarion.engine.nifty;

import de.lessvoid.nifty.NiftyInputConsumer;
import de.lessvoid.nifty.input.keyboard.KeyboardInputEvent;
import de.lessvoid.nifty.spi.input.InputSystem;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;
import org.illarion.engine.input.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the input system that is used to forward the input data provided by the game engine to the Nifty-GUI.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class IgeInputSystem implements InputSystem, InputListener {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(IgeInputSystem.class);
    /**
     * The input implementation of the engine.
     */
    @Nonnull
    private final Input input;
    /**
     * This is the listener that is supposed to receive the input data that was not processed by the Nifty-GUI.
     */
    @Nonnull
    private final InputListener listener;
    /**
     * This value contains a key that was stalled as key down to properly detect typing events.
     */
    @Nullable
    private Key stalledKeyDownKey;
    /**
     * The amount of mouse click events that should be consumed by the Nifty-GUI.
     */
    private int consumeClicks;
    /**
     * This is the consumer that receives the input data. This is {@code null} while Nifty is not expecting input data.
     */
    @Nullable
    private NiftyInputConsumer currentConsumer;
    /**
     * The current non-printable key that is down
     */
    private int holdKeyDownKey;
    /**
     * The number of polls the hold down key event was paused.
     */
    private int holdKeyDownPollCounter;
    /**
     * The number of polls between triggering the hold down key event.
     */
    private final int holdKeyDownPollInterval = 3;
    private final int holdKeyDownInitialDelay = 30;
    private boolean hasHoldKeyDownPassedInitialDelay;

    /**
     * Create a new input device and set the input implementation that provides the input data.
     *
     * @param input the input implementation
     * @param listener the listener that receives input data was was not processed by the Nifty-GUI
     */
    public IgeInputSystem(@Nonnull Input input, @Nonnull InputListener listener) {
        input.setListener(this);
        this.input = input;
        this.listener = listener;
    }

    /**
     * Get the Nifty-GUI button key for a mouse button.
     *
     * @param button the button
     * @return the button key for the Nifty-GUI
     */
    private static int getNiftyButtonKey(@Nonnull Button button) {
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
    private static int getNiftyKeyId(@Nonnull Key key) {
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
                return KeyboardInputEvent.KEY_NONE;
            case RightAlt:
                return KeyboardInputEvent.KEY_NONE;
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
        return KeyboardInputEvent.KEY_NONE;
    }

    @Override
    public void keyDown(@Nonnull Key key) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if (stalledKeyDownKey != null) {
            log.debug("Received key down why there is still a stalled key: {}", stalledKeyDownKey);
            listener.keyDown(stalledKeyDownKey);
            stalledKeyDownKey = null;
        }
        if (input.isForwardingEnabled(ForwardingTarget.Keyboard) || input.isAnyKeyDown(Key.LeftAlt)) {
            log.debug("Directly sending key {} down to the listener. Alt is pressed or forwarding is enabled.", key);
            listener.keyDown(key);
        } else {
            int keyCode = getNiftyKeyId(key);

            if (keyCode == KeyboardInputEvent.KEY_BACK || keyCode == KeyboardInputEvent.KEY_DELETE ||
                    keyCode == KeyboardInputEvent.KEY_LEFT || keyCode == KeyboardInputEvent.KEY_RIGHT) {
                log.debug("Saving key for {} because its hold event needs to be emulated.", key);
                holdKeyDownKey = keyCode;
            }

            boolean shiftDown = input.isAnyKeyDown(Key.LeftShift, Key.RightShift);
            boolean controlDown = input.isAnyKeyDown(Key.LeftCtrl, Key.RightCtrl);
            KeyboardInputEvent event = new KeyboardInputEvent(keyCode, Character.MIN_VALUE, true, shiftDown,
                                                              controlDown);

            if (!currentConsumer.processKeyboardEvent(event)) {
                log.debug("Key down event was not consumed. Stalling the event for now: {}", key);
                stalledKeyDownKey = key;
            } else {
                log.debug("Key down was handled by Nifty: {}", key);
                stalledKeyDownKey = null;
            }
        }
    }

    private void pollHoldDownKey() {
        int key = holdKeyDownKey;
        NiftyInputConsumer consumer = currentConsumer;

        if (key == 0 || consumer == null) {
            return;
        }

        holdKeyDownPollCounter++;

        if (!hasHoldKeyDownPassedInitialDelay) {
            if (holdKeyDownPollCounter < holdKeyDownInitialDelay) {
                return;
            } else {
                hasHoldKeyDownPassedInitialDelay = true;
            }
        }

        if (holdKeyDownPollCounter < holdKeyDownPollInterval) {
            return;
        }

        boolean shiftDown = input.isAnyKeyDown(Key.LeftShift, Key.RightShift);
        boolean controlDown = input.isAnyKeyDown(Key.LeftCtrl, Key.RightCtrl);
        KeyboardInputEvent event = new KeyboardInputEvent(key, Character.MIN_VALUE,
                true, shiftDown, controlDown);
        if (!currentConsumer.processKeyboardEvent(event)) {
            log.debug("Nifty failed to consume hold down key event: {}", key);
        }

        holdKeyDownPollCounter = 0;
    }

    @Override
    public void keyUp(@Nonnull Key key) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if (input.isForwardingEnabled(ForwardingTarget.Keyboard)) {
            log.debug("Input forwarding is enabled Sending key up directly: {}", key);
            listener.keyUp(key);
        } else {
            if (stalledKeyDownKey != null) {
                log.debug("Stalled key down is present and is now send to the listener: {}", stalledKeyDownKey);
                listener.keyDown(stalledKeyDownKey);
                stalledKeyDownKey = null;
            }
            int keyCode = getNiftyKeyId(key);

            if (keyCode == holdKeyDownKey) {
                holdKeyDownKey = 0;
                holdKeyDownPollCounter = 0;
                hasHoldKeyDownPassedInitialDelay = false;
            }

            boolean shiftDown = input.isAnyKeyDown(Key.LeftShift, Key.RightShift);
            boolean controlDown = input.isAnyKeyDown(Key.LeftCtrl, Key.RightCtrl);
            KeyboardInputEvent event = new KeyboardInputEvent(
                    keyCode, Character.MIN_VALUE, false, shiftDown, controlDown);

            if (!currentConsumer.processKeyboardEvent(event)) {
                log.debug("Key up was not handled by Nifty. Sending it directly: {}", key);
                listener.keyUp(key);
            }
        }
    }

    @Override
    public void keyTyped(char character) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }

        if (input.isForwardingEnabled(ForwardingTarget.Keyboard)) {
            log.debug("Key typed event directly send to the listener because forwarding is active: {}", character);
            listener.keyTyped(character);
        } else {

            if (Character.isDefined(character)) {
                boolean shiftDown = input.isAnyKeyDown(Key.LeftShift, Key.RightShift);
                boolean controlDown = input.isAnyKeyDown(Key.LeftCtrl, Key.RightCtrl);
                KeyboardInputEvent event = new KeyboardInputEvent(KeyboardInputEvent.KEY_NONE, character, true,
                                                                  shiftDown, controlDown);
                if (!currentConsumer.processKeyboardEvent(event)) {
                    if (stalledKeyDownKey != null) {
                        log.debug("Stalled key down is present and is now send to the listener: {}", stalledKeyDownKey);
                        listener.keyDown(stalledKeyDownKey);
                    }
                    log.debug("Key typed send to listener because Nifty did not use it: {}", character);
                    listener.keyTyped(character);
                }
            } else {
                if (stalledKeyDownKey != null) {
                    listener.keyDown(stalledKeyDownKey);
                    log.debug("Stalled key down is present and is now send to the listener: {}", stalledKeyDownKey);
                }
            }

            stalledKeyDownKey = null;
        }
    }

    @Override
    public void buttonDown(int mouseX, int mouseY, @Nonnull Button button) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if (input.isForwardingEnabled(ForwardingTarget.Mouse)) {
            listener.buttonDown(mouseX, mouseY, button);
        } else {
            int buttonKey = getNiftyButtonKey(button);
            if (currentConsumer.processMouseEvent(mouseX, mouseY, 0, buttonKey, true)) {
                log.debug("Nifty processed event. Consuming next click.");
                consumeClicks++;
            } else {
                listener.buttonDown(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void buttonUp(int mouseX, int mouseY, @Nonnull Button button) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if (input.isForwardingEnabled(ForwardingTarget.Mouse)) {
            listener.buttonUp(mouseX, mouseY, button);
        } else {
            int buttonKey = getNiftyButtonKey(button);
            if (consumeClicks > 0) {
                consumeClicks--;
            }
            if (!currentConsumer.processMouseEvent(mouseX, mouseY, 0, buttonKey, false)) {
                listener.buttonUp(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void buttonClicked(int mouseX, int mouseY, @Nonnull Button button, int count) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if ((consumeClicks == 0) || input.isForwardingEnabled(ForwardingTarget.Mouse)) {
            listener.buttonClicked(mouseX, mouseY, button, count);
        } else {
            consumeClicks--;
            if (count > 1) {
                buttonClicked(mouseX, mouseY, button, count - 1);
            } else {
                log.debug("Consumed one click.");
            }
        }
    }

    @Override
    public void mouseMoved(int mouseX, int mouseY) {
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
    public void mouseDragged(
            @Nonnull Button button, int fromX, int fromY, int toX, int toY) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if (input.isForwardingEnabled(ForwardingTarget.Mouse)) {
            listener.mouseDragged(button, fromX, fromY, toX, toY);
        } else {
            int buttonKey = getNiftyButtonKey(button);
            boolean startUsed = currentConsumer.processMouseEvent(fromX, fromY, 0, buttonKey, true);
            boolean endUsed = currentConsumer.processMouseEvent(toX, toY, 0, buttonKey, true);
            if (!startUsed && !endUsed) {
                listener.mouseDragged(button, fromX, fromY, toX, toY);
            }
            consumeClicks = 0;
        }
    }

    @Override
    public void mouseWheelMoved(int mouseX, int mouseY, int delta) {
        if (currentConsumer == null) {
            throw new IllegalStateException("Receiving input data while none was requested");
        }
        if (input.isForwardingEnabled(ForwardingTarget.Mouse) ||
                !currentConsumer.processMouseEvent(mouseX, mouseY, delta, -1, false)) {
            listener.mouseWheelMoved(mouseX, mouseY, delta);
        }
        consumeClicks = 0;
    }

    @Override
    public void setResourceLoader(@Nonnull NiftyResourceLoader niftyResourceLoader) {
        // not needed
    }

    @Override
    public void forwardEvents(@Nonnull NiftyInputConsumer inputEventConsumer) {
        currentConsumer = inputEventConsumer;
        pollHoldDownKey();
        input.poll();
        currentConsumer = null;
    }

    @Override
    public void setMousePosition(int x, int y) {
        input.setMouseLocation(x, y);
    }
}
