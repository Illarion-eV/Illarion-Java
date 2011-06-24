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

import javolution.text.TextBuilder;
import javolution.util.FastList;

/**
 * This event defines a action with the mouse that was fetched and now is
 * reported to the client.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public final class MouseEvent {
    /**
     * Indicates mouse button #1; used by {@link #getKey()}. This is the primary
     * mouse button. So the left one for right handed mouses and the right for
     * left handed mouses.
     */
    public static final int BUTTON1 = 1;

    /**
     * Indicates mouse button #2; used by {@link #getKey()}.
     */
    public static final int BUTTON2 = 2;

    /**
     * Indicates mouse button #3; used by {@link #getKey()}.
     */
    public static final int BUTTON3 = 3;

    /**
     * The event code that is triggered when the mouse key is release after a
     * drag event was started.
     */
    public static final int EVENT_DRAG_END = 5;

    /**
     * The event code of a event that is triggered when the key of the mouse is
     * pressed down and held down.
     */
    public static final int EVENT_DRAG_START = 4;

    /**
     * The event code of a event that is triggered when a key pressed and
     * released right after.
     */
    public static final int EVENT_KEY_CLICK = 2;

    /**
     * The event code of a event that is triggered when a key pressed and
     * released right after 2 times.
     */
    public static final int EVENT_KEY_DBLCLICK = 3;

    /**
     * The event code of a event that is triggered when a key is pressed down.
     */
    public static final int EVENT_KEY_DOWN = 0;

    /**
     * The event code of a event that is triggered when a key is released.
     */
    public static final int EVENT_KEY_UP = 1;

    /**
     * The event that is triggered when the mouse is moved on the screen without
     * any key pressed and during a dragging event.
     */
    public static final int EVENT_LOCATION = 6;

    /**
     * The event code if the mouse cursor points at one location a short time.
     */
    public static final int EVENT_POINT_AT = 7;

    /**
     * The event code if the mouse wheel gets changed.
     */
    public static final int EVENT_WHEEL_CHANGE = 8;

    /**
     * Indicates no mouse buttons; used by {@link #getKey()}.
     */
    public static final int NOBUTTON = 0;

    /**
     * The buffer that stores the mouse events that are currently not in use.
     */
    private static final FastList<MouseEvent> BUFFER =
        new FastList<MouseEvent>();

    /**
     * The delta of the mouse wheel in case it was used in this event.
     */
    private int delta;

    /**
     * The event code of this event.
     */
    private int event;

    /**
     * The code of the mouse key that was used at this event.
     */
    private int key;

    /**
     * The x coordinate of the mouse cursor where this event was triggered.
     */
    private int posX;

    /**
     * The y coordinate of the mouse cursor where this event was triggered.
     */
    private int posY;

    /**
     * Private constructor to avoid any instances created that did not pass the
     * get function.
     */
    private MouseEvent() {
        // nothing to do
    }

    /**
     * Get a instances of the mouse event. Either a unused one from the buffer
     * or a new instance.
     * 
     * @return a instance of the mouse event that is free to use now
     */
    public static MouseEvent get() {
        synchronized (BUFFER) {
            if (BUFFER.isEmpty()) {
                return new MouseEvent();
            }
            return BUFFER.removeFirst();
        }
    }

    /**
     * Get the delta value of the mouse wheel in case it was turned during this
     * event.
     * 
     * @return the delta value of the mouse wheel
     */
    public int getDelta() {
        return delta;
    }

    /**
     * Get the event code of this event.
     * 
     * @return the event code of this event
     * @see #EVENT_DRAG_END
     * @see #EVENT_DRAG_START
     * @see #EVENT_KEY_CLICK
     * @see #EVENT_KEY_DBLCLICK
     * @see #EVENT_KEY_DOWN
     * @see #EVENT_KEY_UP
     * @see #EVENT_LOCATION
     * @see #EVENT_POINT_AT
     * @see #EVENT_WHEEL_CHANGE
     */
    public int getEvent() {
        return event;
    }

    /**
     * Get the key this event is assigned to.
     * 
     * @return The code of the key
     */
    public int getKey() {
        return key;
    }

    /**
     * Get the x coordinate of the position of the mouse when this event was
     * triggered.
     * 
     * @return The x coordinate of the mouse location
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Get the y coordinate of the position of the mouse when this event was
     * triggered.
     * 
     * @return The y coordinate of the mouse location
     */
    public int getPosY() {
        return posY;
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
     * Set all informations for the event.
     * 
     * @param newEvent the ID of the event in detail
     * @param newKey the key that was involved in the event
     * @param newPosX the x coordinate of the mouse on the screen
     * @param newPosY the y coordinate of the mouse on the screen
     * @param newDelta the delta value that is only used in case the mouse wheel
     *            was turned in this event
     */
    public void setEventData(final int newEvent, final int newKey,
        final int newPosX, final int newPosY, final int newDelta) {
        event = newEvent;
        key = newKey;
        posX = newPosX;
        posY = newPosY;
        delta = newDelta;
    }

    /**
     * Get a human readable representation of this mouse event. This entry will
     * contain informations in the event type and its values.
     * 
     * @return the generated string
     */
    @Override
    @SuppressWarnings("nls")
    public String toString() {
        final TextBuilder builder = TextBuilder.newInstance();
        builder.append("MouseEvent(");
        switch (event) {
            case EVENT_DRAG_END:
                builder.append("drag end");
                break;
            case EVENT_DRAG_START:
                builder.append("drag start");
                break;
            case EVENT_KEY_CLICK:
                builder.append("click");
                break;
            case EVENT_KEY_DBLCLICK:
                builder.append("double click");
                break;
            case EVENT_KEY_DOWN:
                builder.append("down");
                break;
            case EVENT_KEY_UP:
                builder.append("up");
                break;
            case EVENT_LOCATION:
                builder.append("location");
                break;
            case EVENT_POINT_AT:
                builder.append("point at");
                break;
            case EVENT_WHEEL_CHANGE:
                builder.append("wheel");
                break;
            default:
                builder.append("unknown");
                break;
        }
        builder.append(", posX=");
        builder.append(posX);
        builder.append(", posY=");
        builder.append(posY);
        builder.append(", delta=");
        builder.append(delta);
        builder.append(", ");
        switch (key) {
            case BUTTON1:
                builder.append("Button 1");
                break;
            case BUTTON2:
                builder.append("Button 2");
                break;
            case BUTTON3:
                builder.append("Button 3");
                break;
            default:
            case NOBUTTON:
                builder.append("no Button");
                break;
        }
        builder.append(")");
        final String result = builder.toString();
        TextBuilder.recycle(builder);
        return result;
    }
}
