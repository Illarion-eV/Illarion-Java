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
package org.illarion.engine.backend.slick;

import org.illarion.engine.backend.shared.AbstractForwardingInput;
import org.illarion.engine.input.Button;
import org.illarion.engine.input.InputListener;
import org.illarion.engine.input.Key;
import org.newdawn.slick.Input;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This is the input implementation of the Slick2D backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickInput extends AbstractForwardingInput implements org.newdawn.slick.InputListener {
    /**
     * The instance of the slick input that is used by this input engine implementation for Slick2D.
     */
    @Nullable
    private Input input;

    /**
     * The queue of input events that are published once the polling function is called.
     */
    @Nonnull
    private final Queue<Runnable> pollingQueue;

    /**
     * The listener that receives the input data once the polling function is called.
     */
    @Nullable
    private InputListener listener;

    /**
     * Create a new instance of the slick input.
     */
    SlickInput() {
        pollingQueue = new LinkedList<>();
    }

    /**
     * Convert the slick mouse button key to the mouse identifier of the game engine.
     *
     * @param slickButton the button key of slick
     * @return the button identifier of the engine
     */
    @Nullable
    private static Button getIgeButtonId(final int slickButton) {
        switch (slickButton) {
            case Input.MOUSE_LEFT_BUTTON:
                return Button.Left;
            case Input.MOUSE_RIGHT_BUTTON:
                return Button.Right;
            case Input.MOUSE_MIDDLE_BUTTON:
                return Button.Middle;
            default:
                return null;
        }
    }

    /**
     * This method is used to convert the slick key id to the key identifier of the engine
     *
     * @param slickKey the key code of slick
     * @return the key
     */
    @SuppressWarnings("SwitchStatementWithTooManyBranches")
    @Nullable
    private static Key getIgeKeyId(final int slickKey) {
        switch (slickKey) {
            case Input.KEY_A:
                return Key.A;
            case Input.KEY_B:
                return Key.B;
            case Input.KEY_C:
                return Key.C;
            case Input.KEY_D:
                return Key.D;
            case Input.KEY_E:
                return Key.E;
            case Input.KEY_F:
                return Key.F;
            case Input.KEY_G:
                return Key.G;
            case Input.KEY_H:
                return Key.H;
            case Input.KEY_I:
                return Key.I;
            case Input.KEY_J:
                return Key.J;
            case Input.KEY_K:
                return Key.K;
            case Input.KEY_L:
                return Key.L;
            case Input.KEY_M:
                return Key.M;
            case Input.KEY_N:
                return Key.N;
            case Input.KEY_O:
                return Key.O;
            case Input.KEY_P:
                return Key.P;
            case Input.KEY_Q:
                return Key.Q;
            case Input.KEY_R:
                return Key.R;
            case Input.KEY_S:
                return Key.S;
            case Input.KEY_T:
                return Key.T;
            case Input.KEY_U:
                return Key.U;
            case Input.KEY_V:
                return Key.V;
            case Input.KEY_W:
                return Key.W;
            case Input.KEY_X:
                return Key.X;
            case Input.KEY_Y:
                return Key.Y;
            case Input.KEY_Z:
                return Key.Z;
            case Input.KEY_LSHIFT:
                return Key.LeftShift;
            case Input.KEY_RSHIFT:
                return Key.RightShift;
            case Input.KEY_LALT:
                return Key.LeftAlt;
            case Input.KEY_RALT:
                return Key.RightAlt;
            case Input.KEY_LCONTROL:
                return Key.LeftCtrl;
            case Input.KEY_RCONTROL:
                return Key.RightCtrl;
            case Input.KEY_LEFT:
                return Key.CursorLeft;
            case Input.KEY_RIGHT:
                return Key.CursorRight;
            case Input.KEY_UP:
                return Key.CursorUp;
            case Input.KEY_DOWN:
                return Key.CursorDown;
            case Input.KEY_ENTER:
                return Key.Enter;
            case Input.KEY_BACK:
                return Key.Backspace;
            case Input.KEY_NUMPAD0:
                return Key.NumPad0;
            case Input.KEY_NUMPAD1:
                return Key.NumPad1;
            case Input.KEY_NUMPAD2:
                return Key.NumPad2;
            case Input.KEY_NUMPAD3:
                return Key.NumPad3;
            case Input.KEY_NUMPAD4:
                return Key.NumPad4;
            case Input.KEY_NUMPAD5:
                return Key.NumPad5;
            case Input.KEY_NUMPAD6:
                return Key.NumPad6;
            case Input.KEY_NUMPAD7:
                return Key.NumPad7;
            case Input.KEY_NUMPAD8:
                return Key.NumPad8;
            case Input.KEY_NUMPAD9:
                return Key.NumPad9;
            case Input.KEY_NUMLOCK:
                return Key.NumLock;
            case Input.KEY_ESCAPE:
                return Key.Escape;
            case Input.KEY_F1:
                return Key.F1;
            case Input.KEY_F2:
                return Key.F2;
            case Input.KEY_F3:
                return Key.F3;
            case Input.KEY_F4:
                return Key.F4;
            case Input.KEY_F5:
                return Key.F5;
            case Input.KEY_F6:
                return Key.F6;
            case Input.KEY_F7:
                return Key.F7;
            case Input.KEY_F8:
                return Key.F8;
            case Input.KEY_F9:
                return Key.F9;
            case Input.KEY_F10:
                return Key.F10;
            case Input.KEY_F11:
                return Key.F11;
            case Input.KEY_F12:
                return Key.F12;
            case Input.KEY_INSERT:
                return Key.Insert;
            case Input.KEY_DELETE:
                return Key.Delete;
            case Input.KEY_HOME:
                return Key.Home;
            case Input.KEY_END:
                return Key.End;
            case Input.KEY_PRIOR:
                return Key.PageUp;
            case Input.KEY_NEXT:
                return Key.PageDown;
            case Input.KEY_TAB:
                return Key.Tab;
            default:
                return null;
        }
    }

    /**
     * Convert the mouse button identifier of the game engine to the values used by the Slick2D.
     *
     * @param button the button identifier
     * @return the button identifier used by slick
     */
    private static int getSlickButtonId(@Nonnull final Button button) {
        switch (button) {
            case Left:
                return Input.MOUSE_LEFT_BUTTON;
            case Right:
                return Input.MOUSE_RIGHT_BUTTON;
            case Middle:
                return Input.MOUSE_MIDDLE_BUTTON;
        }
        return -1;
    }

    /**
     * This method is used to convert the identifier of a key to its slick identifier.
     *
     * @param key the key
     * @return the key code for slick
     */
    @SuppressWarnings("SwitchStatementWithTooManyBranches")
    private static int getSlickKeyId(@Nonnull final Key key) {
        switch (key) {
            case A:
                return Input.KEY_A;
            case B:
                return Input.KEY_B;
            case C:
                return Input.KEY_C;
            case D:
                return Input.KEY_D;
            case E:
                return Input.KEY_E;
            case F:
                return Input.KEY_F;
            case G:
                return Input.KEY_G;
            case H:
                return Input.KEY_H;
            case I:
                return Input.KEY_I;
            case J:
                return Input.KEY_J;
            case K:
                return Input.KEY_K;
            case L:
                return Input.KEY_L;
            case M:
                return Input.KEY_M;
            case N:
                return Input.KEY_N;
            case O:
                return Input.KEY_O;
            case P:
                return Input.KEY_P;
            case Q:
                return Input.KEY_Q;
            case R:
                return Input.KEY_R;
            case S:
                return Input.KEY_S;
            case T:
                return Input.KEY_T;
            case U:
                return Input.KEY_U;
            case V:
                return Input.KEY_V;
            case W:
                return Input.KEY_W;
            case X:
                return Input.KEY_X;
            case Y:
                return Input.KEY_Y;
            case Z:
                return Input.KEY_Z;
            case LeftShift:
                return Input.KEY_LSHIFT;
            case RightShift:
                return Input.KEY_RSHIFT;
            case LeftAlt:
                return Input.KEY_LALT;
            case RightAlt:
                return Input.KEY_RALT;
            case LeftCtrl:
                return Input.KEY_LCONTROL;
            case RightCtrl:
                return Input.KEY_RCONTROL;
            case CursorLeft:
                return Input.KEY_LEFT;
            case CursorRight:
                return Input.KEY_RIGHT;
            case CursorUp:
                return Input.KEY_UP;
            case CursorDown:
                return Input.KEY_DOWN;
            case Enter:
                return Input.KEY_ENTER;
            case Backspace:
                return Input.KEY_BACK;
            case NumPad0:
                return Input.KEY_NUMPAD0;
            case NumPad1:
                return Input.KEY_NUMPAD1;
            case NumPad2:
                return Input.KEY_NUMPAD2;
            case NumPad3:
                return Input.KEY_NUMPAD3;
            case NumPad4:
                return Input.KEY_NUMPAD4;
            case NumPad5:
                return Input.KEY_NUMPAD5;
            case NumPad6:
                return Input.KEY_NUMPAD6;
            case NumPad7:
                return Input.KEY_NUMPAD7;
            case NumPad8:
                return Input.KEY_NUMPAD8;
            case NumPad9:
                return Input.KEY_NUMPAD9;
            case NumLock:
                return Input.KEY_NUMLOCK;
            case Escape:
                return Input.KEY_ESCAPE;
            case F1:
                return Input.KEY_F1;
            case F2:
                return Input.KEY_F2;
            case F3:
                return Input.KEY_F3;
            case F4:
                return Input.KEY_F4;
            case F5:
                return Input.KEY_F5;
            case F6:
                return Input.KEY_F6;
            case F7:
                return Input.KEY_F7;
            case F8:
                return Input.KEY_F8;
            case F9:
                return Input.KEY_F9;
            case F10:
                return Input.KEY_F10;
            case F11:
                return Input.KEY_F11;
            case F12:
                return Input.KEY_F12;
            case Insert:
                return Input.KEY_INSERT;
            case Delete:
                return Input.KEY_DELETE;
            case Home:
                return Input.KEY_HOME;
            case End:
                return Input.KEY_END;
            case PageUp:
                return Input.KEY_PRIOR;
            case PageDown:
                return Input.KEY_NEXT;
            case Tab:
                return Input.KEY_TAB;
        }
        return -1;
    }

    @Override
    public void controllerButtonPressed(final int i, final int i2) {
        // controller input is ignored
    }

    @Override
    public void controllerButtonReleased(final int i, final int i2) {
        // controller input is ignored
    }

    @Override
    public void controllerDownPressed(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerDownReleased(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerLeftPressed(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerLeftReleased(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerRightPressed(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerRightReleased(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerUpPressed(final int i) {
        // controller input is ignored
    }

    @Override
    public void controllerUpReleased(final int i) {
        // controller input is ignored
    }

    @Override
    public int getMouseX() {
        if (input == null) {
            return 0;
        }
        return input.getMouseX();
    }

    @Override
    public int getMouseY() {
        if (input == null) {
            return 0;
        }
        return input.getMouseY();
    }

    @Override
    public void setMouseLocation(final int x, final int y) {
        // not supported
    }

    @Override
    public void inputEnded() {
        // nothing to do
    }

    @Override
    public void inputStarted() {
        // nothing to do
    }

    @Override
    public boolean isAcceptingInput() {
        return true;
    }

    @Override
    public boolean isButtonDown(@Nonnull final Button button) {
        if (input == null) {
            return false;
        }
        final int buttonId = getSlickButtonId(button);
        if (buttonId > -1) {
            return input.isMouseButtonDown(buttonId);
        }
        return false;
    }

    @Override
    public boolean isKeyDown(@Nonnull final Key key) {
        if (input == null) {
            return false;
        }
        final int slickKey = getSlickKeyId(key);
        if (slickKey > -1) {
            return input.isKeyDown(slickKey);
        }
        return false;
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
    public void keyPressed(final int i, final char c) {
        final Key key = getIgeKeyId(i);
        if (key != null) {
            pollingQueue.offer(new Runnable() {
                @Override
                public void run() {
                    assert listener != null;
                    listener.keyDown(key);
                }
            });
        }
        if (Character.isDefined(c)) {
            pollingQueue.offer(new Runnable() {
                @Override
                public void run() {
                    assert listener != null;
                    listener.keyTyped(c);
                }
            });
        }
    }

    @Override
    public void keyReleased(final int i, final char c) {
        final Key key = getIgeKeyId(i);
        if (key != null) {
            pollingQueue.offer(new Runnable() {
                @Override
                public void run() {
                    assert listener != null;
                    listener.keyUp(key);
                }
            });
        }
    }

    @Override
    public void mouseClicked(final int button, final int x, final int y, final int clickCount) {
        final Button igeButton = getIgeButtonId(button);
        if (igeButton != null) {
            pollingQueue.offer(new Runnable() {
                @Override
                public void run() {
                    assert listener != null;
                    listener.buttonClicked(x, y, igeButton, clickCount);
                }
            });
        }
    }

    @Override
    public void mouseDragged(final int oldX, final int oldY, final int newX, final int newY) {
        pollingQueue.offer(new Runnable() {
            @Override
            public void run() {
                assert listener != null;
                for (@Nonnull final Button button : Button.values()) {
                    if (isButtonDown(button)) {
                        listener.mouseDragged(button, oldX, oldY, newX, newY);
                    }
                }
            }
        });
    }

    @Override
    public void mouseMoved(final int oldX, final int oldY, final int newX, final int newY) {
        pollingQueue.offer(new Runnable() {
            @Override
            public void run() {
                assert listener != null;
                listener.mouseMoved(newX, newY);
            }
        });
    }

    @Override
    public void mousePressed(final int button, final int x, final int y) {
        final Button igeButton = getIgeButtonId(button);
        if (igeButton != null) {
            pollingQueue.offer(new Runnable() {
                @Override
                public void run() {
                    assert listener != null;
                    listener.buttonDown(x, y, igeButton);
                }
            });
        }
    }

    @Override
    public void mouseReleased(final int button, final int x, final int y) {
        final Button igeButton = getIgeButtonId(button);
        if (igeButton != null) {
            pollingQueue.offer(new Runnable() {
                @Override
                public void run() {
                    assert listener != null;
                    listener.buttonUp(x, y, igeButton);
                }
            });
        }
    }

    @Override
    public void mouseWheelMoved(final int delta) {
        pollingQueue.offer(new Runnable() {
            @Override
            public void run() {
                assert listener != null;
                listener.mouseWheelMoved(getMouseX(), getMouseY(), delta);
            }
        });
    }

    @Override
    public void poll() {
        if (listener == null) {
            pollingQueue.clear();
        } else {
            while (true) {
                final Runnable task = pollingQueue.poll();
                if (task == null) {
                    break;
                }
                task.run();
            }
        }
    }

    @Override
    public void setInput(@Nullable final Input input) {
        if (this.input != input) {
            if (this.input != null) {
                this.input.removeListener(this);
            }
            this.input = null;
            if (input != null) {
                input.enableKeyRepeat();
                this.input = input;
                input.addListener(this);
            }
        }
    }

    @Override
    public void setListener(@SuppressWarnings("NullableProblems") @Nonnull final InputListener listener) {
        this.listener = listener;
    }
}
