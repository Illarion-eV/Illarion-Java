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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import org.illarion.engine.backend.shared.AbstractForwardingInput;
import org.illarion.engine.input.Button;
import org.illarion.engine.input.InputListener;
import org.illarion.engine.input.Key;
import org.lwjgl.input.Keyboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This is the input system of the libGDX backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxInput extends AbstractForwardingInput implements InputProcessor {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(GdxInput.class);

    /**
     * This is the ID of the pointer that is the only one used.
     */
    private static final int USED_MOUSE_POINTER = 0;

    /**
     * This variable stores the size of the area the touch down and the touch up has to be,
     * in order to accept these events as clicks.
     */
    private static final int CLICK_TOLERANCE = 5;

    /**
     * The time in milliseconds between two clicks to recognise them as a double click.
     */
    private final long doubleClickDelay;
    /**
     * The events received since the last polling.
     */
    @Nonnull
    private final Queue<Runnable> events;
    /**
     * The libGDX input system that provides the updates.
     */
    @Nonnull
    private final Input gdxInput;
    /**
     * The input listener that receives the input data when the polling function is called.
     */
    @Nullable
    private InputListener inputListener;
    /**
     * This variable stores the location where the mouse button was pressed down the last time.
     */
    private int lastDragRelevantX;

    /**
     * This variable stores the location where the mouse button was pressed down the last time.
     */
    private int lastDragRelevantY;

    /**
     * This variable stores the location where the mouse button was pressed down the last time.
     */
    private int touchDownX;

    /**
     * This variable stores the location where the mouse button was pressed down the last time.
     */
    private int touchDownY;

    /**
     * This variable stores the mouse pointer that was pressed last time.
     */
    private int touchDownPointer;

    /**
     * This variable stores the button that was pressed down last time.
     */
    @Nullable
    private Button touchDownButton;

    /**
     * The button that was clicked at the first click.
     */
    @Nullable
    private Button clickButton;

    /**
     * The timestamp until the timeout for the next double click runs.
     */
    private long clickTimeout;

    /**
     * The storage for the numbers that were typed in while a alt key was pressed.
     */
    private char altKeyCode;

    /**
     * Create a new instance of the libGDX input system.
     *
     * @param gdxInput the input provider of libGDX that is supposed to be used
     */
    GdxInput(@Nonnull Input gdxInput) {
        @Nonnull Toolkit awtDefaultToolkit = Toolkit.getDefaultToolkit();
        @Nullable Object doubleClick = awtDefaultToolkit.getDesktopProperty("awt.multiClickInterval");
        if (doubleClick instanceof Number) {
            doubleClickDelay = ((Number) doubleClick).longValue();
        } else {
            doubleClickDelay = 500L;
        }

        this.gdxInput = gdxInput;
        gdxInput.setInputProcessor(this);
        events = new LinkedList<>();
        Keyboard.enableRepeatEvents(true);
    }

    /**
     * Convert a libGDX button code to the engine button.
     *
     * @param button the libGDX button code
     * @return the engine button or {@code null} in case the mapping failed
     */
    @Nullable
    private static Button getEngineButton(int button) {
        switch (button) {
            case Buttons.LEFT:
                return Button.Left;
            case Buttons.RIGHT:
                return Button.Right;
            case Buttons.MIDDLE:
                return Button.Middle;
            default:
                return null;
        }
    }

    /**
     * Get the libGDX button code from a engine button.
     *
     * @param button the button
     * @return the libGDX button code or {@code -1} in case the mapping failed
     */
    private static int getGdxButton(@Nonnull Button button) {
        switch (button) {
            case Left:
                return Buttons.LEFT;
            case Right:
                return Buttons.RIGHT;
            case Middle:
                return Buttons.MIDDLE;
        }
        return -1;
    }

    /**
     * Convert a libGDX key code to a game engine key.
     *
     * @param gdxKeyCode the libGDX key code
     * @return the game engine key or {@code null} in case the mapping was not possible
     */
    @SuppressWarnings("SwitchStatementWithTooManyBranches")
    @Nullable
    private static Key getEngineKey(int gdxKeyCode) {
        switch (gdxKeyCode) {
            case Keys.A:
                return Key.A;
            case Keys.B:
                return Key.B;
            case Keys.C:
                return Key.C;
            case Keys.D:
                return Key.D;
            case Keys.E:
                return Key.E;
            case Keys.F:
                return Key.F;
            case Keys.G:
                return Key.G;
            case Keys.H:
                return Key.H;
            case Keys.I:
                return Key.I;
            case Keys.J:
                return Key.J;
            case Keys.K:
                return Key.K;
            case Keys.L:
                return Key.L;
            case Keys.M:
                return Key.M;
            case Keys.N:
                return Key.N;
            case Keys.O:
                return Key.O;
            case Keys.P:
                return Key.P;
            case Keys.Q:
                return Key.Q;
            case Keys.R:
                return Key.R;
            case Keys.S:
                return Key.S;
            case Keys.T:
                return Key.T;
            case Keys.U:
                return Key.U;
            case Keys.V:
                return Key.V;
            case Keys.W:
                return Key.W;
            case Keys.X:
                return Key.X;
            case Keys.Y:
                return Key.Y;
            case Keys.Z:
                return Key.Z;
            case Keys.SHIFT_LEFT:
                return Key.LeftShift;
            case Keys.SHIFT_RIGHT:
                return Key.RightShift;
            case Keys.ALT_LEFT:
                return Key.LeftAlt;
            case Keys.ALT_RIGHT:
                return Key.RightAlt;
            case Keys.CONTROL_LEFT:
                return Key.LeftCtrl;
            case Keys.CONTROL_RIGHT:
                return Key.RightCtrl;
            case Keys.LEFT:
                return Key.CursorLeft;
            case Keys.RIGHT:
                return Key.CursorRight;
            case Keys.UP:
                return Key.CursorUp;
            case Keys.DOWN:
                return Key.CursorDown;
            case Keys.ENTER:
                return Key.Enter;
            case Keys.BACKSPACE:
                return Key.Backspace;
            case Keys.SPACE:
                return Key.Space;
            case Keys.NUMPAD_0:
                return Key.NumPad0;
            case Keys.NUMPAD_1:
                return Key.NumPad1;
            case Keys.NUMPAD_2:
                return Key.NumPad2;
            case Keys.NUMPAD_3:
                return Key.NumPad3;
            case Keys.NUMPAD_4:
                return Key.NumPad4;
            case Keys.NUMPAD_5:
                return Key.NumPad5;
            case Keys.NUMPAD_6:
                return Key.NumPad6;
            case Keys.NUMPAD_7:
                return Key.NumPad7;
            case Keys.NUMPAD_8:
                return Key.NumPad8;
            case Keys.NUMPAD_9:
                return Key.NumPad9;
            case Keys.NUM:
                return Key.NumLock;
            case Keys.ESCAPE:
                return Key.Escape;
            case Keys.F1:
                return Key.F1;
            case Keys.F2:
                return Key.F2;
            case Keys.F3:
                return Key.F3;
            case Keys.F4:
                return Key.F4;
            case Keys.F5:
                return Key.F5;
            case Keys.F6:
                return Key.F6;
            case Keys.F7:
                return Key.F7;
            case Keys.F8:
                return Key.F8;
            case Keys.F9:
                return Key.F9;
            case Keys.F10:
                return Key.F10;
            case Keys.F11:
                return Key.F11;
            case Keys.F12:
                return Key.F12;
            case Keys.INSERT:
                return Key.Insert;
            case Keys.FORWARD_DEL:
                return Key.Delete;
            case Keys.HOME:
                return Key.Home;
            case Keys.END:
                return Key.End;
            case Keys.PAGE_UP:
                return Key.PageUp;
            case Keys.PAGE_DOWN:
                return Key.PageDown;
            case Keys.TAB:
                return Key.Tab;
            default:
                return null;
        }
    }

    /**
     * Get the libGDX key code of a engine button.
     *
     * @param key the engine key
     * @return the libGDX key code or {@link Keys#UNKNOWN} in case the mapping fails
     */
    @SuppressWarnings("SwitchStatementWithTooManyBranches")
    private static int getGdxKey(@Nonnull Key key) {
        switch (key) {
            case A:
                return Keys.A;
            case B:
                return Keys.B;
            case C:
                return Keys.C;
            case D:
                return Keys.D;
            case E:
                return Keys.E;
            case F:
                return Keys.F;
            case G:
                return Keys.G;
            case H:
                return Keys.H;
            case I:
                return Keys.I;
            case J:
                return Keys.J;
            case K:
                return Keys.K;
            case L:
                return Keys.L;
            case M:
                return Keys.M;
            case N:
                return Keys.N;
            case O:
                return Keys.O;
            case P:
                return Keys.P;
            case Q:
                return Keys.Q;
            case R:
                return Keys.R;
            case S:
                return Keys.S;
            case T:
                return Keys.T;
            case U:
                return Keys.U;
            case V:
                return Keys.V;
            case W:
                return Keys.W;
            case X:
                return Keys.X;
            case Y:
                return Keys.Y;
            case Z:
                return Keys.Z;
            case LeftShift:
                return Keys.SHIFT_LEFT;
            case RightShift:
                return Keys.SHIFT_RIGHT;
            case LeftAlt:
                return Keys.ALT_LEFT;
            case RightAlt:
                return Keys.ALT_RIGHT;
            case LeftCtrl:
                return Keys.CONTROL_LEFT;
            case RightCtrl:
                return Keys.CONTROL_RIGHT;
            case CursorLeft:
                return Keys.LEFT;
            case CursorRight:
                return Keys.RIGHT;
            case CursorUp:
                return Keys.UP;
            case CursorDown:
                return Keys.DOWN;
            case Enter:
                return Keys.ENTER;
            case Backspace:
                return Keys.BACKSPACE;
            case Space:
                return Keys.SPACE;
            case NumPad0:
                return Keys.NUMPAD_0;
            case NumPad1:
                return Keys.NUMPAD_1;
            case NumPad2:
                return Keys.NUMPAD_2;
            case NumPad3:
                return Keys.NUMPAD_3;
            case NumPad4:
                return Keys.NUMPAD_4;
            case NumPad5:
                return Keys.NUMPAD_5;
            case NumPad6:
                return Keys.NUMPAD_6;
            case NumPad7:
                return Keys.NUMPAD_7;
            case NumPad8:
                return Keys.NUMPAD_8;
            case NumPad9:
                return Keys.NUMPAD_9;
            case NumLock:
                return Keys.NUM;
            case Escape:
                return Keys.ESCAPE;
            case F1:
                return Keys.F1;
            case F2:
                return Keys.F2;
            case F3:
                return Keys.F3;
            case F4:
                return Keys.F4;
            case F5:
                return Keys.F5;
            case F6:
                return Keys.F6;
            case F7:
                return Keys.F7;
            case F8:
                return Keys.F8;
            case F9:
                return Keys.F9;
            case F10:
                return Keys.F10;
            case F11:
                return Keys.F11;
            case F12:
                return Keys.F12;
            case Insert:
                return Keys.INSERT;
            case Delete:
                return Keys.FORWARD_DEL;
            case Home:
                return Keys.HOME;
            case End:
                return Keys.END;
            case PageUp:
                return Keys.PAGE_UP;
            case PageDown:
                return Keys.PAGE_DOWN;
            case Tab:
                return Keys.TAB;
        }
        return Keys.UNKNOWN;
    }

    @Override
    public boolean keyDown(int keyCode) {
        Key pressedKey = getEngineKey(keyCode);
        if (pressedKey == null) {
            log.debug("Received key down with code: {} that failed to translate to a key.", keyCode);
            return true;
        }
        if (isAnyKeyDown(Key.LeftAlt, Key.RightAlt) && isNumPadNumber(pressedKey)) {
            addKeyToAltKeyCode(pressedKey);
        }
        log.debug("Received key down with code: {} that translated to key: {}", keyCode, pressedKey);
        events.offer(() -> {
            assert inputListener != null;
            inputListener.keyDown(pressedKey);
        });
        return true;
    }

    @Override
    public boolean keyUp(int keyCode) {
        Key releasedKey = getEngineKey(keyCode);
        if (releasedKey == null) {
            log.debug("Received key up with code: {} that failed to translate to a key.", keyCode);
            return true;
        }
        log.debug("Received key up with code: {} that translated to key: {}", keyCode, releasedKey);
        events.offer(() -> {
            assert inputListener != null;
            inputListener.keyUp(releasedKey);
        });
        if ((releasedKey == Key.LeftAlt) || (releasedKey == Key.RightAlt)) {
            keyTyped(altKeyCode);
            altKeyCode = 0;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        if (Character.isDefined(character) && (character != 0)) {
            log.debug("Received key typed with character: {}", character);
            events.offer(() -> {
                assert inputListener != null;
                inputListener.keyTyped(character);
            });
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        if (pointer != USED_MOUSE_POINTER) {
            return false;
        }
        Button pressedButton = getEngineButton(button);
        if (pressedButton == null) {
            return true;
        }
        events.offer(() -> {
            assert inputListener != null;
            inputListener.buttonDown(x, y, pressedButton);
        });

        touchDownX = x;
        touchDownY = y;
        lastDragRelevantX = x;
        lastDragRelevantY = y;
        touchDownPointer = pointer;
        touchDownButton = pressedButton;

        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if (pointer != USED_MOUSE_POINTER) {
            return false;
        }
        Button releasedButton = getEngineButton(button);
        if (releasedButton == null) {
            return true;
        }
        if ((touchDownButton == releasedButton) && (touchDownPointer == pointer) &&
                (Math.abs(touchDownX - x) < CLICK_TOLERANCE) && (Math.abs(touchDownY - y) < CLICK_TOLERANCE)) {
            publishClick(x, y, releasedButton);
        }
        events.offer(() -> {
            assert inputListener != null;
            inputListener.buttonUp(x, y, releasedButton);
        });
        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        if (pointer != USED_MOUSE_POINTER) {
            return false;
        }

        int startX = lastDragRelevantX;
        int startY = lastDragRelevantY;

        lastDragRelevantX = x;
        lastDragRelevantY = y;

        for (@Nonnull Button button : Button.values()) {
            if (isButtonDown(button)) {
                events.offer(() -> {
                    assert inputListener != null;
                    inputListener.mouseDragged(button, startX, startY, x, y);
                });
            }
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        events.offer(() -> {
            assert inputListener != null;
            inputListener.mouseMoved(x, y);
        });
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        events.offer(() -> {
            assert inputListener != null;
            inputListener.mouseWheelMoved(getMouseX(), getMouseY(), -amount);
        });
        return true;
    }

    private void addKeyToAltKeyCode(@Nonnull Key key) {
        int newNumber;
        switch (key) {
            case NumPad0:
                newNumber = 0;
                break;
            case NumPad1:
                newNumber = 1;
                break;
            case NumPad2:
                newNumber = 2;
                break;
            case NumPad3:
                newNumber = 3;
                break;
            case NumPad4:
                newNumber = 4;
                break;
            case NumPad5:
                newNumber = 5;
                break;
            case NumPad6:
                newNumber = 6;
                break;
            case NumPad7:
                newNumber = 7;
                break;
            case NumPad8:
                newNumber = 8;
                break;
            case NumPad9:
                newNumber = 9;
                break;
            default:
                throw new IllegalArgumentException("Key is not a valid Numpad key: " + key);
        }

        altKeyCode *= 10;
        altKeyCode += newNumber;
    }

    private boolean isNumPadNumber(@Nonnull Key key) {
        return (key == Key.NumPad0) || (key == Key.NumPad1) || (key == Key.NumPad2) || (key == Key.NumPad3) ||
                (key == Key.NumPad4) || (key == Key.NumPad5) || (key == Key.NumPad6) || (key == Key.NumPad7) ||
                (key == Key.NumPad8) || (key == Key.NumPad9);
    }

    /**
     * Publish the event as mouse click event. This function also handles double clicks.
     *
     * @param x the x coordinate where the click happened
     * @param y the y coordinate where the click happened
     * @param button the button that was clicked
     */
    private void publishClick(int x, int y, @Nonnull Button button) {
        if ((clickTimeout == 0) || (clickButton != button) || (System.currentTimeMillis() > clickTimeout)) {
            clickButton = button;
            clickTimeout = System.currentTimeMillis() + doubleClickDelay;
            events.offer(() -> {
                assert inputListener != null;
                inputListener.buttonClicked(x, y, button, 1);
            });
        } else {
            clickTimeout = 0;
            events.offer(() -> {
                assert inputListener != null;
                inputListener.buttonClicked(x, y, button, 2);
            });
        }
    }

    @Override
    public void poll() {
        @Nullable Runnable task = events.poll();
        while (task != null) {
            task.run();
            task = events.poll();
        }
    }

    @Override
    public void setListener(@Nonnull InputListener listener) {
        inputListener = listener;
    }

    @Override
    public boolean isButtonDown(@Nonnull Button button) {
        int buttonCode = getGdxButton(button);
        if (buttonCode == -1) {
            return false;
        }
        return gdxInput.isButtonPressed(buttonCode);
    }

    @Override
    public boolean isKeyDown(@Nonnull Key key) {
        return gdxInput.isKeyPressed(getGdxKey(key));
    }

    @Override
    public boolean isAnyButtonDown() {
        return isAnyButtonDown(Button.values());
    }

    @Override
    public boolean isAnyButtonDown(@Nonnull Button... buttons) {
        for (@Nonnull Button button : buttons) {
            if (isButtonDown(button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAnyKeyDown() {
        return isAnyKeyDown(Key.values());
    }

    @Override
    public boolean isAnyKeyDown(@Nonnull Key... keys) {
        for (@Nonnull Key key : keys) {
            if (isKeyDown(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getMouseX() {
        return gdxInput.getX(USED_MOUSE_POINTER);
    }

    @Override
    public int getMouseY() {
        return gdxInput.getY(USED_MOUSE_POINTER);
    }

    @Override
    public void setMouseLocation(int x, int y) {
        gdxInput.setCursorPosition(x, y);
    }
}
