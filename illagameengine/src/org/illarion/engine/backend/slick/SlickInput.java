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

import org.illarion.engine.input.Button;
import org.illarion.engine.input.Input;
import org.illarion.engine.input.InputListener;
import org.illarion.engine.input.Key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This is the input implementation of the Slick2D backend.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class SlickInput implements Input, org.newdawn.slick.InputListener {
    /**
     * The instance of the slick input that is used by this input engine implementation for Slick2D.
     */
    @Nonnull
    private final org.newdawn.slick.Input input;

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
     *
     * @param slickInput the slick input provider
     */
    SlickInput(@Nonnull final org.newdawn.slick.Input slickInput) {
        input = slickInput;
        pollingQueue = new LinkedList<Runnable>();
        input.addListener(this);
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
            case org.newdawn.slick.Input.MOUSE_LEFT_BUTTON:
                return Button.Left;
            case org.newdawn.slick.Input.MOUSE_RIGHT_BUTTON:
                return Button.Right;
            case org.newdawn.slick.Input.MOUSE_MIDDLE_BUTTON:
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
            case org.newdawn.slick.Input.KEY_A:
                return Key.A;
            case org.newdawn.slick.Input.KEY_B:
                return Key.B;
            case org.newdawn.slick.Input.KEY_C:
                return Key.C;
            case org.newdawn.slick.Input.KEY_D:
                return Key.D;
            case org.newdawn.slick.Input.KEY_E:
                return Key.E;
            case org.newdawn.slick.Input.KEY_F:
                return Key.F;
            case org.newdawn.slick.Input.KEY_G:
                return Key.G;
            case org.newdawn.slick.Input.KEY_H:
                return Key.H;
            case org.newdawn.slick.Input.KEY_I:
                return Key.I;
            case org.newdawn.slick.Input.KEY_J:
                return Key.J;
            case org.newdawn.slick.Input.KEY_K:
                return Key.K;
            case org.newdawn.slick.Input.KEY_L:
                return Key.L;
            case org.newdawn.slick.Input.KEY_M:
                return Key.M;
            case org.newdawn.slick.Input.KEY_N:
                return Key.N;
            case org.newdawn.slick.Input.KEY_O:
                return Key.O;
            case org.newdawn.slick.Input.KEY_P:
                return Key.P;
            case org.newdawn.slick.Input.KEY_Q:
                return Key.Q;
            case org.newdawn.slick.Input.KEY_R:
                return Key.R;
            case org.newdawn.slick.Input.KEY_S:
                return Key.S;
            case org.newdawn.slick.Input.KEY_T:
                return Key.T;
            case org.newdawn.slick.Input.KEY_U:
                return Key.U;
            case org.newdawn.slick.Input.KEY_V:
                return Key.V;
            case org.newdawn.slick.Input.KEY_W:
                return Key.W;
            case org.newdawn.slick.Input.KEY_X:
                return Key.X;
            case org.newdawn.slick.Input.KEY_Y:
                return Key.Y;
            case org.newdawn.slick.Input.KEY_Z:
                return Key.Z;
            case org.newdawn.slick.Input.KEY_LSHIFT:
                return Key.LeftShift;
            case org.newdawn.slick.Input.KEY_RSHIFT:
                return Key.RightShift;
            case org.newdawn.slick.Input.KEY_LALT:
                return Key.LeftAlt;
            case org.newdawn.slick.Input.KEY_RALT:
                return Key.RightAlt;
            case org.newdawn.slick.Input.KEY_LCONTROL:
                return Key.LeftCtrl;
            case org.newdawn.slick.Input.KEY_RCONTROL:
                return Key.RightCtrl;
            case org.newdawn.slick.Input.KEY_LEFT:
                return Key.CursorLeft;
            case org.newdawn.slick.Input.KEY_RIGHT:
                return Key.CursorRight;
            case org.newdawn.slick.Input.KEY_UP:
                return Key.CursorUp;
            case org.newdawn.slick.Input.KEY_DOWN:
                return Key.CursorDown;
            case org.newdawn.slick.Input.KEY_ENTER:
                return Key.Enter;
            case org.newdawn.slick.Input.KEY_BACK:
                return Key.Backspace;
            case org.newdawn.slick.Input.KEY_NUMPAD0:
                return Key.NumPad0;
            case org.newdawn.slick.Input.KEY_NUMPAD1:
                return Key.NumPad1;
            case org.newdawn.slick.Input.KEY_NUMPAD2:
                return Key.NumPad2;
            case org.newdawn.slick.Input.KEY_NUMPAD3:
                return Key.NumPad3;
            case org.newdawn.slick.Input.KEY_NUMPAD4:
                return Key.NumPad4;
            case org.newdawn.slick.Input.KEY_NUMPAD5:
                return Key.NumPad5;
            case org.newdawn.slick.Input.KEY_NUMPAD6:
                return Key.NumPad6;
            case org.newdawn.slick.Input.KEY_NUMPAD7:
                return Key.NumPad7;
            case org.newdawn.slick.Input.KEY_NUMPAD8:
                return Key.NumPad8;
            case org.newdawn.slick.Input.KEY_NUMPAD9:
                return Key.NumPad9;
            case org.newdawn.slick.Input.KEY_NUMLOCK:
                return Key.NumLock;
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
                return org.newdawn.slick.Input.MOUSE_LEFT_BUTTON;
            case Right:
                return org.newdawn.slick.Input.MOUSE_RIGHT_BUTTON;
            case Middle:
                return org.newdawn.slick.Input.MOUSE_MIDDLE_BUTTON;
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
                return org.newdawn.slick.Input.KEY_A;
            case B:
                return org.newdawn.slick.Input.KEY_B;
            case C:
                return org.newdawn.slick.Input.KEY_C;
            case D:
                return org.newdawn.slick.Input.KEY_D;
            case E:
                return org.newdawn.slick.Input.KEY_E;
            case F:
                return org.newdawn.slick.Input.KEY_F;
            case G:
                return org.newdawn.slick.Input.KEY_G;
            case H:
                return org.newdawn.slick.Input.KEY_H;
            case I:
                return org.newdawn.slick.Input.KEY_I;
            case J:
                return org.newdawn.slick.Input.KEY_J;
            case K:
                return org.newdawn.slick.Input.KEY_K;
            case L:
                return org.newdawn.slick.Input.KEY_L;
            case M:
                return org.newdawn.slick.Input.KEY_M;
            case N:
                return org.newdawn.slick.Input.KEY_N;
            case O:
                return org.newdawn.slick.Input.KEY_O;
            case P:
                return org.newdawn.slick.Input.KEY_P;
            case Q:
                return org.newdawn.slick.Input.KEY_Q;
            case R:
                return org.newdawn.slick.Input.KEY_R;
            case S:
                return org.newdawn.slick.Input.KEY_S;
            case T:
                return org.newdawn.slick.Input.KEY_T;
            case U:
                return org.newdawn.slick.Input.KEY_U;
            case V:
                return org.newdawn.slick.Input.KEY_V;
            case W:
                return org.newdawn.slick.Input.KEY_W;
            case X:
                return org.newdawn.slick.Input.KEY_X;
            case Y:
                return org.newdawn.slick.Input.KEY_Y;
            case Z:
                return org.newdawn.slick.Input.KEY_Z;
            case LeftShift:
                return org.newdawn.slick.Input.KEY_LSHIFT;
            case RightShift:
                return org.newdawn.slick.Input.KEY_RSHIFT;
            case LeftAlt:
                return org.newdawn.slick.Input.KEY_LALT;
            case RightAlt:
                return org.newdawn.slick.Input.KEY_RALT;
            case LeftCtrl:
                return org.newdawn.slick.Input.KEY_LCONTROL;
            case RightCtrl:
                return org.newdawn.slick.Input.KEY_RCONTROL;
            case CursorLeft:
                return org.newdawn.slick.Input.KEY_LEFT;
            case CursorRight:
                return org.newdawn.slick.Input.KEY_RIGHT;
            case CursorUp:
                return org.newdawn.slick.Input.KEY_UP;
            case CursorDown:
                return org.newdawn.slick.Input.KEY_DOWN;
            case Enter:
                return org.newdawn.slick.Input.KEY_ENTER;
            case Backspace:
                return org.newdawn.slick.Input.KEY_BACK;
            case NumPad0:
                return org.newdawn.slick.Input.KEY_NUMPAD0;
            case NumPad1:
                return org.newdawn.slick.Input.KEY_NUMPAD1;
            case NumPad2:
                return org.newdawn.slick.Input.KEY_NUMPAD2;
            case NumPad3:
                return org.newdawn.slick.Input.KEY_NUMPAD3;
            case NumPad4:
                return org.newdawn.slick.Input.KEY_NUMPAD4;
            case NumPad5:
                return org.newdawn.slick.Input.KEY_NUMPAD5;
            case NumPad6:
                return org.newdawn.slick.Input.KEY_NUMPAD6;
            case NumPad7:
                return org.newdawn.slick.Input.KEY_NUMPAD7;
            case NumPad8:
                return org.newdawn.slick.Input.KEY_NUMPAD8;
            case NumPad9:
                return org.newdawn.slick.Input.KEY_NUMPAD9;
            case NumLock:
                return org.newdawn.slick.Input.KEY_NUMLOCK;
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
        return input.getMouseX();
    }

    @Override
    public int getMouseY() {
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
        final int buttonId = getSlickButtonId(button);
        if (buttonId > 0) {
            return input.isMouseButtonDown(buttonId);
        }
        return false;
    }

    @Override
    public boolean isKeyDown(@Nonnull final Key key) {
        final int slickKey = getSlickKeyId(key);
        if (slickKey > -1) {
            return input.isKeyDown(slickKey);
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
    public void setInput(@Nonnull final org.newdawn.slick.Input input) {
        // not needed
    }

    @Override
    public void setListener(@SuppressWarnings("NullableProblems") @Nonnull final InputListener listener) {
        this.listener = listener;
    }
}
