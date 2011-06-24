/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.guiNG;

import java.awt.event.KeyEvent;

import javolution.util.FastList;

import org.apache.log4j.Logger;

import illarion.client.world.CombatHandler;

import illarion.common.util.Stoppable;

import illarion.input.InputManager;
import illarion.input.KeyboardEvent;
import illarion.input.KeyboardEventReceiver;
import illarion.input.MouseEvent;
import illarion.input.MouseEventReceiver;

/**
 * The input handler takes the input events from the input implementation and
 * forwards them to the GUI.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class InputHandler extends Thread implements
    KeyboardEventReceiver, MouseEventReceiver, Stoppable {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(InputHandler.class);

    /**
     * The list of events of the keyboard that still need to be handled.
     */
    private final FastList<KeyboardEvent> keyboardEvents =
        new FastList<KeyboardEvent>();

    /**
     * The list of events of the mouse that still need to be handled.
     */
    private final FastList<MouseEvent> mouseEvents =
        new FastList<MouseEvent>();

    /**
     * The GUI that created this input handler.
     */
    private final GUI parentGUI;

    /**
     * The running flag, in case this is <code>true</code> the thread will keep
     * running.
     */
    private volatile boolean running = true;

    /**
     * Create a new instance of the input handler. That should only be done by
     * the GUI.
     * 
     * @param parent the parent GUI of this input handler
     */
    @SuppressWarnings("nls")
    public InputHandler(final GUI parent) {
        super("GUI Input Thread");

        InputManager.getInstance().getKeyboardManager()
            .registerEventHandler(this);
        InputManager.getInstance().getMouseManager()
            .registerEventHandler(this);
        parentGUI = parent;
    }

    /**
     * Add a keyboard event to the handler list so its handled at the next run
     * of the thread.
     */
    @Override
    public void handleKeyboardEvent(final KeyboardEvent event) {
        synchronized (keyboardEvents) {
            keyboardEvents.addLast(event);
        }
        synchronized (this) {
            notify();
        }
    }

    /**
     * Add a mouse event to the handler list so its handled at the next run of
     * the thread.
     */
    @Override
    public void handleMouseEvent(final MouseEvent event) {
        synchronized (mouseEvents) {
            mouseEvents.addLast(event);
        }
        synchronized (this) {
            notify();
        }
    }

    /**
     * Run the input handler thread and process the data that is send to the
     * input handler.
     */
    @SuppressWarnings("nls")
    @Override
    public void run() {
        while (running) {
            KeyboardEvent keyEvent = null;
            synchronized (keyboardEvents) {
                if (!keyboardEvents.isEmpty()) {
                    keyEvent = keyboardEvents.removeFirst();
                }
            }
            if (keyEvent != null) {
                if (TextCursor.getInstance().isActive()) {
                    TextCursor.getInstance().handleEvent(keyEvent);

                    keyEvent.recycle();
                    keyEvent = null;
                    continue;
                }
                if (keyEvent.getKey() == KeyEvent.VK_ENTER) {
                    if ((keyEvent.getEvent() != KeyboardEvent.EVENT_KEY_UP)
                        || keyEvent.isRepeated()) {
                        keyEvent.recycle();
                        keyEvent = null;
                        continue;
                    }

                    final ChatEditor editor = parentGUI.getChatEditor();
                    if (!editor.isVisible()) {
                        editor.setVisible(true);
                        TextCursor.getInstance().setTarget(editor);
                    }
                    keyEvent.recycle();
                    keyEvent = null;
                    continue;
                }

                if (keyEvent.getKey() == KeyEvent.VK_SPACE) {
                    if ((keyEvent.getEvent() != KeyboardEvent.EVENT_KEY_UP)
                        || keyEvent.isRepeated()) {
                        keyEvent.recycle();
                        keyEvent = null;
                        continue;
                    }

                    CombatHandler.getInstance().toggleCombatMode();
                    continue;
                }

                parentGUI.processKeyboardEvent(keyEvent);

                keyEvent.recycle();
                keyEvent = null;
                continue;
            }
            MouseEvent mouseEvent = null;
            synchronized (mouseEvents) {
                if (!mouseEvents.isEmpty()) {
                    mouseEvent = mouseEvents.removeFirst();
                }
            }
            if (mouseEvent != null) {
                parentGUI.processMouseEvent(mouseEvent);
                mouseEvent.recycle();
                mouseEvent = null;
                continue;
            }
            try {
                synchronized (this) {
                    if (mouseEvents.isEmpty() && keyboardEvents.isEmpty()) {
                        this.wait();
                    }
                }
            } catch (final InterruptedException e) {
                LOGGER.debug("InputHandler wake up unexpected", e);
            }
        }

        InputManager.getInstance().getKeyboardManager().clear();
        InputManager.getInstance().getMouseManager().clear();
    }

    /**
     * Set the thread to perform a clean shutdown as soon as possible. This
     * thread becomes unusable after this function was called.
     */
    @Override
    public void saveShutdown() {
        running = false;
        synchronized (this) {
            notify();
        }
    }
}
