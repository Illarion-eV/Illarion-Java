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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;

import org.apache.log4j.Logger;

import illarion.input.InputManager;
import illarion.input.KeyboardEvent;

/**
 * The text cursor is able to receive input from the keyboard and forward it to
 * a cursor target.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class TextCursor {
    /**
     * The singleton instance of this class.
     */
    private static final TextCursor INSTANCE = new TextCursor();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(TextCursor.class);

    /**
     * The position of the cursor in the text line.
     */
    private int cursorPos = 0;

    /**
     * The text cursor target of this cursor. This target receives all input
     * from the text cursor.
     */
    private TextCursorTarget target = null;

    /**
     * Private class to avoid any instances but the singleton instance created.
     */
    private TextCursor() {
        // nothing to do
    }

    /**
     * Get the singleton instance of the text cursor.
     * 
     * @return the singleton instance of the text cursor
     */
    public static TextCursor getInstance() {
        return INSTANCE;
    }

    /**
     * Clear the target of this cursor. Better use this then directly clearing
     * the target, else the cursor is messed up.
     */
    public void clearTarget() {
        if (isActive()) {
            target.clear();
            cursorPos = 0;
        }
    }

    /**
     * Disable this text cursor and shut the current target down.
     */
    public void disable() {
        if (isActive()) {
            target.disableCursor();
            target = null;
            cursorPos = 0;
        }
    }

    /**
     * Handle a keyboard event and update the target according to this.
     * 
     * @param event the keyboard event that is to be handled
     * @return <code>true</code> in case the function was able to handle this
     *         event
     */
    public boolean handleEvent(final KeyboardEvent event) {
        if (!isActive()) {
            return false;
        }

        switch (event.getEvent()) {
            case KeyboardEvent.EVENT_KEY_DOWN:
                switch (event.getKey()) {
                    case KeyEvent.VK_C:
                        if (InputManager.getInstance().getKeyboardManager()
                            .isKeyDown(KeyEvent.VK_CONTROL) && !event.isRepeated()) {
                            copyToClipboard();
                        }
                        return true;

                    case KeyEvent.VK_V:
                        if (InputManager.getInstance().getKeyboardManager()
                            .isKeyDown(KeyEvent.VK_CONTROL) && !event.isRepeated()) {
                            copyFromClipboard();
                        }
                        return true;
                    case KeyEvent.VK_LEFT:
                        if (cursorPos > 0) {
                            --cursorPos;
                            target.setCursorPosition(cursorPos);
                        }
                        return true;

                    case KeyEvent.VK_RIGHT:
                        if (cursorPos < target.getTextLength()) {
                            ++cursorPos;
                            target.setCursorPosition(cursorPos);
                        }
                        return true;

                    case KeyEvent.VK_BACK_SPACE:
                        if (cursorPos > 0) {
                            target.remove();
                            --cursorPos;
                            target.setCursorPosition(cursorPos);
                        }
                        return true;

                    case KeyEvent.VK_DELETE:
                        if (cursorPos < target.getTextLength()) {
                            target.removeAt(cursorPos);
                        } else if ((cursorPos > 0)
                            && (cursorPos == target.getTextLength())) {
                            target.remove();
                            --cursorPos;
                            target.setCursorPosition(cursorPos);
                        }
                        return true;
                    case KeyEvent.VK_ESCAPE:
                        target.cancelInput();
                        cursorPos = 0;
                        return true;

                    default:
                } // switch (event.getKey())
                break; // KeyboardEvent.EVENT_KEY_DOWN

            case KeyboardEvent.EVENT_KEY_UP:
                if (event.isRepeated()) {
                    return false;
                }

                switch (event.getKey()) {
                    case KeyEvent.VK_ENTER:
                        target.executeEnter();
                        cursorPos = 0;
                        return true;

                    default:
                }
                break; // KeyboardEvent.EVENT_KEY_UP

            case KeyboardEvent.EVENT_KEY_PRESSED:
                    final char character = event.getCharacter();
    
                    if (target.getTextFontSource().getGlyph(character).getId() != 0) {
                        if ((target.getMaxLength() > target.getTextLength())
                            && Character.isDefined(character) && !Character.isISOControl(character) && !Character.isIdentifierIgnorable(character)) {
                            target.insertCharacter(character);
                            ++cursorPos;
                            target.setCursorPosition(cursorPos);
                        }
                        return true;
                    }
                break; // KeyboardEvent.EVENT_KEY_PRESSED
        } // switch (event.getEvent())

        return false;
    }

    /**
     * Check if the text cursor is active and has a target to send the text to.
     * 
     * @return <code>true</code> in case the cursor is working and has a target
     */
    public boolean isActive() {
        return target != null;
    }

    /**
     * True in case the text target is exactly the same object as the current
     * text cursor target.
     * 
     * @param testTarget the target to test
     * @return <code>true</code> in case the test target and the cursor target
     *         are one and the same
     */
    public boolean isTarget(final TextCursorTarget testTarget) {
        return (target == testTarget);
    }

    /**
     * Set the cursor to the end of the line of the target.
     */
    public void setCursorToEnd() {
        if (isActive()) {
            cursorPos = target.getTextLength();
            target.setCursorPosition(cursorPos);
        }
    }

    /**
     * Set the target to a new one. This does nothing in case the old and the
     * new target are the very same. In case there is a old target it gets
     * correctly disabled before the new one is activated.
     * 
     * @param newTarget the new target
     */
    public void setTarget(final TextCursorTarget newTarget) {
        if (isActive() && target.equals(newTarget)) {
            return;
        }
        disable();
        if (newTarget != null) {
            target = newTarget;
            cursorPos = target.getTextLength();
            target.setCursorPosition(cursorPos);
        }
    }

    /**
     * Copy the data from the Clip board to the target of the cursor.
     */
    @SuppressWarnings("nls")
    private void copyFromClipboard() {
        try {
            final Clipboard sysClip =
                Toolkit.getDefaultToolkit().getSystemClipboard();
            final Transferable transfer = sysClip.getContents(null);
            String data =
                (String) transfer.getTransferData(DataFlavor.stringFlavor);
            if ((target.getTextLength() + data.length()) > target
                .getMaxLength()) {
                data =
                    data.substring(0,
                        target.getMaxLength() - target.getTextLength());
            }
            target.insertCharacters(data);
            cursorPos += data.length();
            target.setCursorPosition(cursorPos);
        } catch (final IOException ex) {
            LOGGER.error("Reading the Clipboard failed");
        } catch (final UnsupportedFlavorException ex) {
            LOGGER.error("Converting the data in the clipboard failed");
        }
    }

    /**
     * Copy the text of the target to the clip board.
     */
    private void copyToClipboard() {
        final StringSelection ss =
            new StringSelection(target.getText().toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }
}
