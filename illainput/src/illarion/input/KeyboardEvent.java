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
package illarion.input;

import javolution.util.FastList;

/**
 * This event is send to all receivers of keyboard events in case the user did
 * anything with his keyboard.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class KeyboardEvent {
    /**
     * The event code in case a button is pressed down.
     */
    public static final int EVENT_KEY_DOWN = 0;

    /**
     * The event in case a button is pressed down. This can be fired again and
     * again until the key is released again.
     */
    public static final int EVENT_KEY_PRESSED = 2;
    /**
     * The event code in case a button is released.
     */
    public static final int EVENT_KEY_UP = 1;

    /**
     * Constant for the 0 key. Equals the ASCII code for '0'.
     */
    public static final int VK_0 = 0x30;

    /**
     * Constant for the 1 key. Equals the ASCII code for '1'.
     */
    public static final int VK_1 = 0x31;

    /**
     * Constant for the 2 key. Equals the ASCII code for '2'.
     */
    public static final int VK_2 = 0x32;

    /**
     * Constant for the 3 key. Equals the ASCII code for '3'.
     */
    public static final int VK_3 = 0x33;

    /**
     * Constant for the 4 key. Equals the ASCII code for '4'.
     */
    public static final int VK_4 = 0x34;

    /**
     * Constant for the 5 key. Equals the ASCII code for '5'.
     */
    public static final int VK_5 = 0x35;

    /**
     * Constant for the 6 key. Equals the ASCII code for '6'.
     */
    public static final int VK_6 = 0x36;

    /**
     * Constant for the 7 key. Equals the ASCII code for '7'.
     */
    public static final int VK_7 = 0x37;

    /**
     * Constant for the 8 key. Equals the ASCII code for '8'.
     */
    public static final int VK_8 = 0x38;

    /**
     * Constant for the 9 key. Equals the ASCII code for '9'.
     */
    public static final int VK_9 = 0x39;

    /**
     * Constant for the key A. Equals the ASCII value for 'A'.
     */
    public static final int VK_A = 0x41;

    /**
     * Constant for the add key.
     */
    public static final int VK_ADD = 0x6B;

    /**
     * Constant for the do again key.
     */
    public static final int VK_AGAIN = 0xFFC9;

    /**
     * Constant for the ALT key.
     */
    public static final int VK_ALT = 0x12;

    /**
     * Constant for the AltGraph function key.
     */
    public static final int VK_ALT_GRAPH = 0xFF7E;

    /**
     * Constant for the ambersand key &
     */
    public static final int VK_AMPERSAND = 0x96;

    /**
     * Constant for the asterisk key.
     */
    public static final int VK_ASTERISK = 0x97;

    /**
     * Constant for the "@" key.
     */
    public static final int VK_AT = 0x0200;

    /**
     * Constant for the key B. Equals the ASCII value for 'B'.
     */
    public static final int VK_B = 0x42;

    /**
     * Constant for the back quote key.
     */
    public static final int VK_BACK_QUOTE = 0xC0;

    /**
     * Constant for the back slash key, "\"
     */
    public static final int VK_BACK_SLASH = 0x5C;

    /**
     * Constant for the backspace key.
     */
    public static final int VK_BACK_SPACE = '\b';

    /**
     * Constant for the Begin key.
     */
    public static final int VK_BEGIN = 0xFF58;

    /**
     * Constant for the left brace key.
     */
    public static final int VK_BRACELEFT = 0xa1;

    /**
     * Constant for the right brace key.
     */
    public static final int VK_BRACERIGHT = 0xa2;

    /**
     * Constant for the key C. Equals the ASCII value for 'C'.
     */
    public static final int VK_C = 0x43;

    /**
     * Constant for the cancel key.
     */
    public static final int VK_CANCEL = 0x03;

    /**
     * Constant for the caps lock key.
     */
    public static final int VK_CAPS_LOCK = 0x14;

    /**
     * Constant for the "^" key.
     */
    public static final int VK_CIRCUMFLEX = 0x0202;

    /**
     * Constant for the clear key.
     */
    public static final int VK_CLEAR = 0x0C;

    /**
     * Constant for the close bracket key, "]"
     */
    public static final int VK_CLOSE_BRACKET = 0x5D;

    /**
     * Constant for the ":" key.
     */
    public static final int VK_COLON = 0x0201;

    /**
     * Constant for the comma key, ","
     */
    public static final int VK_COMMA = 0x2C;

    /**
     * Constant for the Compose function key.
     */
    public static final int VK_COMPOSE = 0xFF20;

    /**
     * Constant for the Microsoft Windows Context Menu key.
     */
    public static final int VK_CONTEXT_MENU = 0x020D;

    /**
     * Constant for the control key.
     */
    public static final int VK_CONTROL = 0x11;

    /**
     * Constant for the copy key.
     */
    public static final int VK_COPY = 0xFFCD;

    /**
     * Constant for the cut key.
     */
    public static final int VK_CUT = 0xFFD1;

    /**
     * Constant for the key D. Equals the ASCII value for 'D'.
     */
    public static final int VK_D = 0x44;

    /**
     * Constant for the dead above dot key <b>˙</b>
     */
    public static final int VK_DEAD_ABOVEDOT = 0x86;

    /**
     * Constant for the dead above ring key <b>˚</b>
     */
    public static final int VK_DEAD_ABOVERING = 0x88;

    /**
     * Constant for the dead acute key <b>´</b>
     */
    public static final int VK_DEAD_ACUTE = 0x81;

    /**
     * Constant for the dead breve key <b>˘</b>
     */
    public static final int VK_DEAD_BREVE = 0x85;

    /**
     * Constant for the dead caron key
     */
    public static final int VK_DEAD_CARON = 0x8a;

    /**
     * Constant for the dead cedilla key
     */
    public static final int VK_DEAD_CEDILLA = 0x8b;

    /**
     * Constant for the dead circumflex key <b>^</b>
     */
    public static final int VK_DEAD_CIRCUMFLEX = 0x82;

    /**
     * Constant for the dead diaereis key <b>¨</b>
     */
    public static final int VK_DEAD_DIAERESIS = 0x87;

    /**
     * Constant for the dead above double acute key
     */
    public static final int VK_DEAD_DOUBLEACUTE = 0x89;

    /**
     * Constant for the dead grave key <b>`</b>
     */
    public static final int VK_DEAD_GRAVE = 0x80;

    /**
     * Constant for the dead iota key
     */
    public static final int VK_DEAD_IOTA = 0x8d;

    /**
     * Constant for the dead macron key <b>¯</b>
     */
    public static final int VK_DEAD_MACRON = 0x84;

    /**
     * Constant for the dead ogonek key
     */
    public static final int VK_DEAD_OGONEK = 0x8c;

    /**
     * Constant for the dead semi voiced sound key
     */
    public static final int VK_DEAD_SEMIVOICED_SOUND = 0x8f;

    /**
     * Constant for the dead tilde key <b>~</b>
     */
    public static final int VK_DEAD_TILDE = 0x83;

    /**
     * Constant for the dead voiced sound key
     */
    public static final int VK_DEAD_VOICED_SOUND = 0x8e;

    /**
     * Constant for the decimal point key.
     */
    public static final int VK_DECIMAL = 0x6E;

    /**
     * Constant for the delete key. This equals the ASCII value for delete.
     */
    public static final int VK_DELETE = 0x7F;

    /**
     * Constant for the divide key.
     */
    public static final int VK_DIVIDE = 0x6F;

    /**
     * Constant for the "$" key.
     */
    public static final int VK_DOLLAR = 0x0203;

    /**
     * Constant for the non-numpad <b>down</b> arrow key.
     */
    public static final int VK_DOWN = 0x28;

    /**
     * Constant for the key E. Equals the ASCII value for 'E'.
     */
    public static final int VK_E = 0x45;

    /**
     * Constant for the end key.
     */
    public static final int VK_END = 0x23;

    /**
     * Constant for the enter key.
     */
    public static final int VK_ENTER = '\n';

    /**
     * Constant for the equals key, "="
     */
    public static final int VK_EQUALS = 0x3D;

    /**
     * Constant for the escape (ESC) key.
     */
    public static final int VK_ESCAPE = 0x1B;

    /**
     * Constant for the Euro currency sign key.
     */
    public static final int VK_EURO_SIGN = 0x0204;

    /**
     * Constant for the "!" key.
     */
    public static final int VK_EXCLAMATION_MARK = 0x0205;

    /**
     * Constant for the key F. Equals the ASCII value for 'F'.
     */
    public static final int VK_F = 0x46;

    /**
     * Constant for the F1 function key.
     */
    public static final int VK_F1 = 0x70;

    /**
     * Constant for the F10 function key.
     */
    public static final int VK_F10 = 0x79;

    /**
     * Constant for the F11 function key.
     */
    public static final int VK_F11 = 0x7A;

    /**
     * Constant for the F12 function key.
     */
    public static final int VK_F12 = 0x7B;

    /**
     * Constant for the F2 function key.
     */
    public static final int VK_F2 = 0x71;

    /**
     * Constant for the F3 function key.
     */
    public static final int VK_F3 = 0x72;

    /**
     * Constant for the F4 function key.
     */
    public static final int VK_F4 = 0x73;

    /**
     * Constant for the F5 function key.
     */
    public static final int VK_F5 = 0x74;

    /**
     * Constant for the F6 function key.
     */
    public static final int VK_F6 = 0x75;

    /**
     * Constant for the F7 function key.
     */
    public static final int VK_F7 = 0x76;

    /**
     * Constant for the F8 function key.
     */
    public static final int VK_F8 = 0x77;

    /**
     * Constant for the F9 function key.
     */
    public static final int VK_F9 = 0x78;

    /**
     * Constant for the find key.
     */
    public static final int VK_FIND = 0xFFD0;

    /**
     * Constant for the key G. Equals the ASCII value for 'G'.
     */
    public static final int VK_G = 0x47;

    /**
     * Constant for the greater key.
     */
    public static final int VK_GREATER = 0xa0;

    /**
     * Constant for the key H. Equals the ASCII value for 'H'.
     */
    public static final int VK_H = 0x48;

    /**
     * Constant for the help key.
     */
    public static final int VK_HELP = 0x9C;

    /**
     * Constant for the home key.
     */
    public static final int VK_HOME = 0x24;

    /**
     * Constant for the key I. Equals the ASCII value for 'I'.
     */
    public static final int VK_I = 0x49;

    /**
     * Constant for the insert key.
     */
    public static final int VK_INSERT = 0x9B;

    /**
     * Constant for the inverted exclamation mark key.
     */
    public static final int VK_INVERTED_EXCLAMATION_MARK = 0x0206;

    /**
     * Constant for the key J. Equals the ASCII value for 'J'.
     */
    public static final int VK_J = 0x4A;

    /**
     * Constant for the key K. Equals the ASCII value for 'K'.
     */
    public static final int VK_K = 0x4B;

    /**
     * Constant for the numeric keypad <b>down</b> arrow key.
     */
    public static final int VK_KP_DOWN = 0xE1;

    /**
     * Constant for the numeric keypad <b>left</b> arrow key.
     */
    public static final int VK_KP_LEFT = 0xE2;

    /**
     * Constant for the numeric keypad <b>right</b> arrow key.
     */
    public static final int VK_KP_RIGHT = 0xE3;

    /**
     * Constant for the numeric keypad <b>up</b> arrow key.
     */
    public static final int VK_KP_UP = 0xE0;

    /**
     * Constant for the key L. Equals the ASCII value for 'L'.
     */
    public static final int VK_L = 0x4C;

    /**
     * Constant for the non-numpad <b>left</b> arrow key.
     */
    public static final int VK_LEFT = 0x25;

    /**
     * Constant for the "(" key.
     */
    public static final int VK_LEFT_PARENTHESIS = 0x0207;

    /**
     * Constant for the lesser key.
     */
    public static final int VK_LESS = 0x99;

    /**
     * Constant for the key M. Equals the ASCII value for 'M'.
     */
    public static final int VK_M = 0x4D;

    /**
     * Constant for the meta (windows) key.
     */
    public static final int VK_META = 0x9D;

    /**
     * Constant for the minus key, "-"
     */
    public static final int VK_MINUS = 0x2D;

    /**
     * Constant for the multiply key.
     */
    public static final int VK_MULTIPLY = 0x6A;

    /**
     * Constant for the key N. Equals the ASCII value for 'N'.
     */
    public static final int VK_N = 0x4E;

    /**
     * Constant for the numbers lock key.
     */
    public static final int VK_NUM_LOCK = 0x90;

    /**
     * Constant for the "#" key.
     */
    public static final int VK_NUMBER_SIGN = 0x0208;

    /**
     * Constant for the 0 key on the numbers pad.
     */
    public static final int VK_NUMPAD0 = 0x60;

    /**
     * Constant for the 1 key on the numbers pad.
     */
    public static final int VK_NUMPAD1 = 0x61;

    /**
     * Constant for the 2 key on the numbers pad.
     */
    public static final int VK_NUMPAD2 = 0x62;

    /**
     * Constant for the 3 key on the numbers pad.
     */
    public static final int VK_NUMPAD3 = 0x63;

    /**
     * Constant for the 4 key on the numbers pad.
     */
    public static final int VK_NUMPAD4 = 0x64;

    /**
     * Constant for the 5 key on the numbers pad.
     */
    public static final int VK_NUMPAD5 = 0x65;

    /**
     * Constant for the 6 key on the numbers pad.
     */
    public static final int VK_NUMPAD6 = 0x66;

    /**
     * Constant for the 7 key on the numbers pad.
     */
    public static final int VK_NUMPAD7 = 0x67;

    /**
     * Constant for the 8 key on the numbers pad.
     */
    public static final int VK_NUMPAD8 = 0x68;

    /**
     * Constant for the 9 key on the numbers pad.
     */
    public static final int VK_NUMPAD9 = 0x69;

    /**
     * Constant for the key O. Equals the ASCII value for 'O'.
     */
    public static final int VK_O = 0x4F;

    /**
     * Constant for the open bracket key, "["
     */
    public static final int VK_OPEN_BRACKET = 0x5B;

    /**
     * Constant for the key P. Equals the ASCII value for 'P'.
     */
    public static final int VK_P = 0x50;

    /**
     * Constant for the page up key.
     */
    public static final int VK_PAGE_DOWN = 0x22;

    /**
     * Constant for the page down key.
     */
    public static final int VK_PAGE_UP = 0x21;

    /**
     * Constant for the paste key.
     */
    public static final int VK_PASTE = 0xFFCF;

    /**
     * Constant for the pause key.
     */
    public static final int VK_PAUSE = 0x13;

    /**
     * Constant for the period key, "."
     */
    public static final int VK_PERIOD = 0x2E;

    /**
     * Constant for the "+" key.
     */
    public static final int VK_PLUS = 0x0209;

    /**
     * Constant for the print screen key.
     */
    public static final int VK_PRINTSCREEN = 0x9A;

    /**
     * Constant for the properties key.
     */
    public static final int VK_PROPS = 0xFFCA;

    /**
     * Constant for the key Q. Equals the ASCII value for 'Q'.
     */
    public static final int VK_Q = 0x51;

    /**
     * Constant for the quote key.
     */
    public static final int VK_QUOTE = 0xDE;

    /**
     * Constant for the souble quote key.
     */
    public static final int VK_QUOTEDBL = 0x98;

    /**
     * Constant for the key R. Equals the ASCII value for 'R'.
     */
    public static final int VK_R = 0x52;

    /**
     * Constant for the non-numpad <b>right</b> arrow key.
     */
    public static final int VK_RIGHT = 0x27;

    /**
     * Constant for the ")" key.
     */
    public static final int VK_RIGHT_PARENTHESIS = 0x020A;

    /**
     * Constant for the key S. Equals the ASCII value for 'S'.
     */
    public static final int VK_S = 0x53;

    /**
     * Constant for the scroll lock key.
     */
    public static final int VK_SCROLL_LOCK = 0x91;

    /**
     * Constant for the semicolon key, ";"
     */
    public static final int VK_SEMICOLON = 0x3B;

    /**
     * Constant for the Numpad Separator key.
     */
    public static final int VK_SEPARATOR = 0x6C;

    /**
     * Constant for the shift key.
     */
    public static final int VK_SHIFT = 0x10;

    /**
     * Constant for the forward slash key, "/"
     */
    public static final int VK_SLASH = 0x2F;

    /**
     * Constant for the space key.
     */
    public static final int VK_SPACE = 0x20;

    /**
     * Constant for the stop key.
     */
    public static final int VK_STOP = 0xFFC8;

    /**
     * Constant for the subtract key.
     */
    public static final int VK_SUBTRACT = 0x6D;

    /**
     * Constant for the key T. Equals the ASCII value for 'T'.
     */
    public static final int VK_T = 0x54;

    /**
     * Constant for the tab key.
     */
    public static final int VK_TAB = '\t';

    /**
     * Constant for the key U. Equals the ASCII value for 'U'.
     */
    public static final int VK_U = 0x55;

    /**
     * This value is used to indicate that the keyCode is unknown.
     * EVENT_KEY_PRESSED events do not have a keyCode value; this value is used
     * instead.
     */
    public static final int VK_UNDEFINED = 0x0;

    /**
     * Constant for the "_" key.
     */
    public static final int VK_UNDERSCORE = 0x020B;

    /**
     * Constant for the undo key.
     */
    public static final int VK_UNDO = 0xFFCB;

    /**
     * Constant for the non-numpad <b>up</b> arrow key.
     */
    public static final int VK_UP = 0x26;

    /**
     * Constant for the key V. Equals the ASCII value for 'V'.
     */
    public static final int VK_V = 0x56;

    /**
     * Constant for the key W. Equals the ASCII value for 'W'.
     */
    public static final int VK_W = 0x57;

    /**
     * Constant for the Microsoft Windows "Windows" key. It is used for both the
     * left and right version of the key.
     */
    public static final int VK_WINDOWS = 0x020C;

    /**
     * Constant for the key X. Equals the ASCII value for 'X'.
     */
    public static final int VK_X = 0x58;

    /**
     * Constant for the key Y. Equals the ASCII value for 'Y'.
     */
    public static final int VK_Y = 0x59;

    /**
     * Constant for the key Z. Equals the ASCII value for 'Z'.
     */
    public static final int VK_Z = 0x5A;

    /**
     * The buffer that stores the currently unused keyboard event instances.
     */
    private static final FastList<KeyboardEvent> BUFFER =
        new FastList<KeyboardEvent>();

    /**
     * The character of the key that triggered this event.
     */
    private char character;

    /**
     * The ID of the key event.
     * 
     * @see #EVENT_KEY_DOWN
     * @see #EVENT_KEY_PRESSED
     * @see #EVENT_KEY_UP
     */
    private int event;

    /**
     * The code of the key that was pressed.
     */
    private int key;

    /**
     * Stores if the event is repeated. This is <code>true</code> for the second
     * and all further calls of {@link #EVENT_KEY_PRESSED} until the key is
     * released again.
     */
    private boolean repeated;

    /**
     * The private constructor to avoid that any instances are created that did
     * not went past the static get function.
     */
    private KeyboardEvent() {
        // nothing to do
    }

    /**
     * Get a instances of the keyboard event. Either a unused one from the
     * buffer or a new instance.
     * 
     * @return a instance of the keyboard event that is free to use now
     */
    public static KeyboardEvent get() {
        synchronized (BUFFER) {
            if (BUFFER.isEmpty()) {
                return new KeyboardEvent();
            }
            return BUFFER.removeFirst();
        }
    }

    /**
     * Get the character of the key that was used here. The effect of the Shift
     * Key is already considered.
     * 
     * @return the character of the key that was used at this event.
     */
    public char getCharacter() {
        return character;
    }

    /**
     * Get the detailed keyboard that was triggered.
     * 
     * @return the type of keyboard event
     * @see #EVENT_KEY_DOWN
     * @see #EVENT_KEY_PRESSED
     * @see #EVENT_KEY_UP
     */
    public int getEvent() {
        return event;
    }

    /**
     * Get the key that triggered this event.
     * 
     * @return the key that triggered this event
     */
    public int getKey() {
        return key;
    }

    /**
     * Get if the event is repeated. This is only the case if the event is
     * {@link #EVENT_KEY_PRESSED} and it was triggered already more then once
     * between since the last key down.
     * 
     * @return <code>true</code> is the event is repeated
     */
    public boolean isRepeated() {
        return repeated;
    }

    /**
     * Put this instance back into a buffer after is was used.
     */
    public void recycle() {
        synchronized (BUFFER) {
            BUFFER.addLast(this);
        }
    }

    /**
     * Set the data for this keyboard event.
     * 
     * @param newKey the key that was used at this event
     * @param newEvent the event that was called in detail
     * @param newRepeated <code>true</code> if the event is repeated
     * @param newCharacter the character of the key that triggered this event
     */
    public void setEventData(final int newKey, final int newEvent,
        final boolean newRepeated, final char newCharacter) {
        key = newKey;
        event = newEvent;
        repeated = newRepeated;
        character = newCharacter;
    }
}
