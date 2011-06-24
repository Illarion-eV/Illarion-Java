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
package illarion.client.guiNG.elements;

import illarion.client.guiNG.GUI;
import illarion.client.guiNG.event.WidgetEvent;

import illarion.input.MouseEvent;

/**
 * A button is a widget that is in general not visible. Its just a layer that
 * fetches all clicks on it.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public class Button extends Widget {
    /**
     * The serialization UID of this button widget.
     */
    private static final long serialVersionUID = 1L;

    /**
     * If set to true, a double click will have the same result as a normal
     * click. If set to false, double clicks will be simply ignored.
     */
    private boolean allowDoubleClick;

    /**
     * This widget event is called in case the user clicks the button.
     */
    private WidgetEvent clickHandler;

    /**
     * This variable stores if the user is currently moving the mouse above the
     * widget.
     */
    private transient boolean currHover = false;

    /**
     * This variable stores if the user is currently pointing at the widget.
     */
    private transient boolean currPointing = false;

    /**
     * This widget event is called in case the user is moving the mouse above
     * the button widget.
     */
    private WidgetEvent startHoverHandler;

    /**
     * This widget event is called in case the user points a while as the
     * button.
     */
    private WidgetEvent startPointAtHandler;

    /**
     * This widget event is called in case the user does not point anymore on
     * the button widget.
     */
    private WidgetEvent stopHoverHandler;

    /**
     * This widget event is called in case the user does anything with the mouse
     * after a pointing event was started.
     */
    private WidgetEvent stopPointAtHandler;

    /**
     * Handle the mouse event that that is fired on this widget.
     * 
     * @param event the mouse event that was received by this widget
     */
    @Override
    public void handleMouseEvent(final MouseEvent event) {
        if (!isVisible()) {
            return;
        }

        if (((event.getEvent() == MouseEvent.EVENT_KEY_CLICK) || ((event
            .getEvent() == MouseEvent.EVENT_KEY_DBLCLICK) && allowDoubleClick))
            && (event.getKey() == java.awt.event.MouseEvent.BUTTON1)) {
            if (clickHandler != null) {
                clickHandler.handleEvent(this);
            }
            if (currPointing && (stopPointAtHandler != null)) {
                stopPointAtHandler.handleEvent(this);
            }
            currPointing = false;
        } else if (event.getEvent() == MouseEvent.EVENT_LOCATION) {
            if (isInside(event.getPosX(), event.getPosY())) {
                if (startHoverHandler != null) {
                    startHoverHandler.handleEvent(this);
                }
                if (currPointing && (stopPointAtHandler != null)) {
                    stopPointAtHandler.handleEvent(this);
                }
                currHover = true;
                currPointing = false;
                GUI.getInstance().requestExclusiveMouse(this);
            } else {
                if (currHover && (stopHoverHandler != null)) {
                    stopHoverHandler.handleEvent(this);
                }
                if (currPointing && (stopPointAtHandler != null)) {
                    stopPointAtHandler.handleEvent(this);
                }
                currHover = false;
                currPointing = false;
                GUI.getInstance().requestExclusiveMouse(null);
                GUI.getInstance().processMouseEvent(event);
            }
        } else if (event.getEvent() == MouseEvent.EVENT_POINT_AT) {
            if (startPointAtHandler != null) {
                startPointAtHandler.handleEvent(this);
            }
            currPointing = true;
            GUI.getInstance().requestExclusiveMouse(this);
        }
    }

    /**
     * If set to true, a double click will have the same result as a normal
     * click. If set to false, double clicks will be simply ignored.
     * 
     * @return true if double clicks are handled
     */
    public boolean isAllowDoubleClick() {
        return allowDoubleClick;
    }

    /**
     * If set to true, a double click will have the same result as a normal
     * click. If set to false, double clicks will be simply ignored.
     * 
     * @param newAllowDoubleClick the new value for allowDoubleClick
     */
    public void setAllowDoubleClick(final boolean newAllowDoubleClick) {
        allowDoubleClick = newAllowDoubleClick;
    }

    /**
     * Set the widget event that shall be called in case someone clicks the
     * button.
     * 
     * @param handler the event handler that is called when someone clicks the
     *            button
     */
    public void setClickHandler(final WidgetEvent handler) {
        clickHandler = handler;
    }

    /**
     * Set the widget events that are called in case the user hovers with the
     * mouse over the button.
     * 
     * @param startHandler the event that is called when the user starts to
     *            hover over the button
     * @param stopHandler the event that is called when the user stops to hover
     *            over the button
     */
    public void setHoverHandler(final WidgetEvent startHandler,
        final WidgetEvent stopHandler) {
        startHoverHandler = startHandler;
        stopHoverHandler = stopHandler;
    }

    /**
     * Set the widget events that are called in case the user points with the
     * mouse at the button.
     * 
     * @param startHandler the event that is called when the user starts to
     *            point at the button
     * @param stopHandler the event that is called when the user stops to point
     *            at the button
     */
    public void setPointAtHandler(final WidgetEvent startHandler,
        final WidgetEvent stopHandler) {
        startPointAtHandler = startHandler;
        stopPointAtHandler = stopHandler;
    }

    /**
     * Override of setVisible() to prevent staying in exclusive mouse mode when
     * setting the visibility to false.
     */
    @Override
    public void setVisible(final boolean newVisible) {
        super.setVisible(newVisible);
        if (!newVisible) {
            GUI.getInstance().requestExclusiveMouse(null);
        }
    }
}
