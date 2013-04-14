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
package org.illarion.engine.backend.gdx;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import org.illarion.engine.backend.shared.AbstractForwardingInput;
import org.illarion.engine.input.Button;
import org.illarion.engine.input.InputListener;
import org.illarion.engine.input.Key;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This is the input system of the libGDX backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class GdxInput extends AbstractForwardingInput implements InputProcessor {
    /**
     * The input listener that receives the input data when the polling function is called.
     */
    @Nullable
    private InputListener inputListener;

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
     * This is the ID of the pointer that is the only one used.
     */
    private static final int USED_MOUSE_POINTER = 0;

    /**
     * Create a new instance of the libGDX input system.
     *
     * @param gdxInput the input provider of libGDX that is supposed to be used
     */
    GdxInput(@Nonnull final Input gdxInput) {
        this.gdxInput = gdxInput;
        gdxInput.setInputProcessor(this);
        events = new LinkedList<Runnable>();
        Keyboard.enableRepeatEvents(true);
    }

    /**
     * Convert a libGDX button code to the engine button.
     *
     * @param button the libGDX button code
     * @return the engine button or {@code null} in case the mapping failed
     */
    @Nullable
    private static Button getEngineButton(final int button) {
        switch (button) {
            case Input.Buttons.LEFT:
                return Button.Left;
            case Input.Buttons.RIGHT:
                return Button.Right;
            case Input.Buttons.MIDDLE:
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
    private static int getGdxButton(@Nonnull final Button button) {
        switch (button) {
            case Left:
                return Input.Buttons.LEFT;
            case Right:
                return Input.Buttons.RIGHT;
            case Middle:
                return Input.Buttons.MIDDLE;
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
    private static Key getEngineKey(final int gdxKeyCode) {
        switch (gdxKeyCode) {
            case Input.Keys.A:
                return Key.A;
            case Input.Keys.B:
                return Key.B;
            case Input.Keys.C:
                return Key.C;
            case Input.Keys.D:
                return Key.D;
            case Input.Keys.E:
                return Key.E;
            case Input.Keys.F:
                return Key.F;
            case Input.Keys.G:
                return Key.G;
            case Input.Keys.H:
                return Key.H;
            case Input.Keys.I:
                return Key.I;
            case Input.Keys.J:
                return Key.J;
            case Input.Keys.K:
                return Key.K;
            case Input.Keys.L:
                return Key.L;
            case Input.Keys.M:
                return Key.M;
            case Input.Keys.N:
                return Key.N;
            case Input.Keys.O:
                return Key.O;
            case Input.Keys.P:
                return Key.P;
            case Input.Keys.Q:
                return Key.Q;
            case Input.Keys.R:
                return Key.R;
            case Input.Keys.S:
                return Key.S;
            case Input.Keys.T:
                return Key.T;
            case Input.Keys.U:
                return Key.U;
            case Input.Keys.V:
                return Key.V;
            case Input.Keys.W:
                return Key.W;
            case Input.Keys.X:
                return Key.X;
            case Input.Keys.Y:
                return Key.Y;
            case Input.Keys.Z:
                return Key.Z;
            case Input.Keys.SHIFT_LEFT:
                return Key.LeftShift;
            case Input.Keys.SHIFT_RIGHT:
                return Key.RightShift;
            case Input.Keys.ALT_LEFT:
                return Key.LeftAlt;
            case Input.Keys.ALT_RIGHT:
                return Key.RightAlt;
            case Input.Keys.CONTROL_LEFT:
                return Key.LeftCtrl;
            case Input.Keys.CONTROL_RIGHT:
                return Key.RightCtrl;
            case Input.Keys.LEFT:
                return Key.CursorLeft;
            case Input.Keys.RIGHT:
                return Key.CursorRight;
            case Input.Keys.UP:
                return Key.CursorUp;
            case Input.Keys.DOWN:
                return Key.CursorDown;
            case Input.Keys.ENTER:
                return Key.Enter;
            case Input.Keys.BACKSPACE:
                return Key.Backspace;
            case Input.Keys.NUM_0:
                return Key.NumPad0;
            case Input.Keys.NUM_1:
                return Key.NumPad1;
            case Input.Keys.NUM_2:
                return Key.NumPad2;
            case Input.Keys.NUM_3:
                return Key.NumPad3;
            case Input.Keys.NUM_4:
                return Key.NumPad4;
            case Input.Keys.NUM_5:
                return Key.NumPad5;
            case Input.Keys.NUM_6:
                return Key.NumPad6;
            case Input.Keys.NUM_7:
                return Key.NumPad7;
            case Input.Keys.NUM_8:
                return Key.NumPad8;
            case Input.Keys.NUM_9:
                return Key.NumPad9;
            case Input.Keys.NUM:
                return Key.NumLock;
            case Input.Keys.ESCAPE:
                return Key.Escape;
            case Input.Keys.F1:
                return Key.F1;
            case Input.Keys.F2:
                return Key.F2;
            case Input.Keys.F3:
                return Key.F3;
            case Input.Keys.F4:
                return Key.F4;
            case Input.Keys.F5:
                return Key.F5;
            case Input.Keys.F6:
                return Key.F6;
            case Input.Keys.F7:
                return Key.F7;
            case Input.Keys.F8:
                return Key.F8;
            case Input.Keys.F9:
                return Key.F9;
            case Input.Keys.F10:
                return Key.F10;
            case Input.Keys.F11:
                return Key.F11;
            case Input.Keys.F12:
                return Key.F12;
            case Input.Keys.INSERT:
                return Key.Insert;
            case Input.Keys.FORWARD_DEL:
                return Key.Delete;
            case Input.Keys.HOME:
                return Key.Home;
            case Input.Keys.END:
                return Key.End;
            case Input.Keys.PAGE_UP:
                return Key.PageUp;
            case Input.Keys.PAGE_DOWN:
                return Key.PageDown;
            default:
                return null;
        }
    }

    /**
     * Get the libGDX key code of a engine button.
     *
     * @param key the engine key
     * @return the libGDX key code or {@link Input.Keys#UNKNOWN} in case the mapping fails
     */
    @SuppressWarnings("SwitchStatementWithTooManyBranches")
    private static int getGdxKey(@Nonnull final Key key) {
        switch (key) {
            case A:
                return Input.Keys.A;
            case B:
                return Input.Keys.B;
            case C:
                return Input.Keys.C;
            case D:
                return Input.Keys.D;
            case E:
                return Input.Keys.E;
            case F:
                return Input.Keys.F;
            case G:
                return Input.Keys.G;
            case H:
                return Input.Keys.H;
            case I:
                return Input.Keys.I;
            case J:
                return Input.Keys.J;
            case K:
                return Input.Keys.K;
            case L:
                return Input.Keys.L;
            case M:
                return Input.Keys.M;
            case N:
                return Input.Keys.N;
            case O:
                return Input.Keys.O;
            case P:
                return Input.Keys.P;
            case Q:
                return Input.Keys.Q;
            case R:
                return Input.Keys.R;
            case S:
                return Input.Keys.S;
            case T:
                return Input.Keys.T;
            case U:
                return Input.Keys.U;
            case V:
                return Input.Keys.V;
            case W:
                return Input.Keys.W;
            case X:
                return Input.Keys.X;
            case Y:
                return Input.Keys.Y;
            case Z:
                return Input.Keys.Z;
            case LeftShift:
                return Input.Keys.SHIFT_LEFT;
            case RightShift:
                return Input.Keys.SHIFT_RIGHT;
            case LeftAlt:
                return Input.Keys.ALT_LEFT;
            case RightAlt:
                return Input.Keys.ALT_RIGHT;
            case LeftCtrl:
                return Input.Keys.CONTROL_LEFT;
            case RightCtrl:
                return Input.Keys.CONTROL_RIGHT;
            case CursorLeft:
                return Input.Keys.LEFT;
            case CursorRight:
                return Input.Keys.RIGHT;
            case CursorUp:
                return Input.Keys.UP;
            case CursorDown:
                return Input.Keys.DOWN;
            case Enter:
                return Input.Keys.ENTER;
            case Backspace:
                return Input.Keys.BACKSPACE;
            case NumPad0:
                return Input.Keys.NUM_0;
            case NumPad1:
                return Input.Keys.NUM_1;
            case NumPad2:
                return Input.Keys.NUM_2;
            case NumPad3:
                return Input.Keys.NUM_3;
            case NumPad4:
                return Input.Keys.NUM_4;
            case NumPad5:
                return Input.Keys.NUM_5;
            case NumPad6:
                return Input.Keys.NUM_6;
            case NumPad7:
                return Input.Keys.NUM_7;
            case NumPad8:
                return Input.Keys.NUM_8;
            case NumPad9:
                return Input.Keys.NUM_9;
            case NumLock:
                return Input.Keys.NUM;
            case Escape:
                return Input.Keys.ESCAPE;
            case F1:
                return Input.Keys.F1;
            case F2:
                return Input.Keys.F2;
            case F3:
                return Input.Keys.F3;
            case F4:
                return Input.Keys.F4;
            case F5:
                return Input.Keys.F5;
            case F6:
                return Input.Keys.F6;
            case F7:
                return Input.Keys.F7;
            case F8:
                return Input.Keys.F8;
            case F9:
                return Input.Keys.F9;
            case F10:
                return Input.Keys.F10;
            case F11:
                return Input.Keys.F11;
            case F12:
                return Input.Keys.F12;
            case Insert:
                return Input.Keys.INSERT;
            case Delete:
                return Input.Keys.FORWARD_DEL;
            case Home:
                return Input.Keys.HOME;
            case End:
                return Input.Keys.END;
            case PageUp:
                return Input.Keys.PAGE_UP;
            case PageDown:
                return Input.Keys.PAGE_DOWN;
        }
        return Input.Keys.UNKNOWN;
    }

    @Override
    public boolean keyDown(final int keycode) {
        final Key pressedKey = getEngineKey(keycode);
        if (pressedKey == null) {
            return true;
        }
        events.offer(new Runnable() {
            @Override
            public void run() {
                assert inputListener != null;
                inputListener.keyDown(pressedKey);
            }
        });
        return true;
    }

    @Override
    public boolean keyUp(final int keycode) {
        final Key releasedKey = getEngineKey(keycode);
        if (releasedKey == null) {
            return true;
        }
        events.offer(new Runnable() {
            @Override
            public void run() {
                assert inputListener != null;
                inputListener.keyUp(releasedKey);
            }
        });
        return true;
    }

    @Override
    public boolean keyTyped(final char character) {
        events.offer(new Runnable() {
            @Override
            public void run() {
                assert inputListener != null;
                inputListener.keyTyped(character);
            }
        });
        return true;
    }

    @Override
    public boolean touchDown(final int x, final int y, final int pointer, final int button) {
        if (pointer != USED_MOUSE_POINTER) {
            return false;
        }
        final Button pressedButton = getEngineButton(button);
        if (pressedButton == null) {
            return true;
        }
        events.offer(new Runnable() {
            @Override
            public void run() {
                assert inputListener != null;
                inputListener.buttonDown(x, y, pressedButton);
            }
        });
        return true;
    }

    @Override
    public boolean touchUp(final int x, final int y, final int pointer, final int button) {
        if (pointer != USED_MOUSE_POINTER) {
            return false;
        }
        final Button releasedButton = getEngineButton(button);
        if (releasedButton == null) {
            return true;
        }
        events.offer(new Runnable() {
            @Override
            public void run() {
                assert inputListener != null;
                inputListener.buttonUp(x, y, releasedButton);
            }
        });
        return true;
    }

    @Override
    public boolean touchDragged(final int x, final int y, final int pointer) {
        if (pointer != USED_MOUSE_POINTER) {
            return false;
        }
        for (@Nonnull final Button button : Button.values()) {
            if (isButtonDown(button)) {
                events.offer(new Runnable() {
                    @Override
                    public void run() {
                        assert inputListener != null;
                        inputListener.mouseDragged(button, x, y, x, y);
                    }
                });
            }
        }
        return true;
    }

    @Override
    public boolean mouseMoved(final int x, final int y) {
        events.offer(new Runnable() {
            @Override
            public void run() {
                assert inputListener != null;
                inputListener.mouseMoved(x, y);
            }
        });
        return true;
    }

    @Override
    public boolean scrolled(final int amount) {
        events.offer(new Runnable() {
            @Override
            public void run() {
                assert inputListener != null;
                inputListener.mouseWheelMoved(getMouseX(), getMouseY(), amount);
            }
        });
        return true;
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
    public void setListener(@Nonnull final InputListener listener) {
        inputListener = listener;
    }

    @Override
    public boolean isButtonDown(@Nonnull final Button button) {
        final int buttonCode = getGdxButton(button);
        if (buttonCode == -1) {
            return false;
        }
        return gdxInput.isButtonPressed(buttonCode);
    }

    @Override
    public boolean isKeyDown(@Nonnull final Key key) {
        return gdxInput.isKeyPressed(getGdxKey(key));
    }

    @Override
    public boolean isAnyButtonDown() {
        return isAnyButtonDown(Button.values());
    }

    @Override
    public boolean isAnyButtonDown(@Nonnull final Button... buttons) {
        for (@Nonnull final Button button : buttons) {
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
    public boolean isAnyKeyDown(@Nonnull final Key... keys) {
        for (@Nonnull final Key key : keys) {
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
    public void setMouseLocation(final int x, final int y) {
        gdxInput.setCursorPosition(x, y);
    }
}
