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
package illarion.client.guiNG.event;

import illarion.client.guiNG.elements.Widget;

/**
 * This event takes care to set one widget to visible and another to invisible
 * at the same time. This can be used easily to get click and hover effects
 * working.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class ToggleWidgetsEvent implements WidgetEvent {
    /**
     * The serialization UID this event script.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The widget that is made invisible when this event is triggered.
     */
    private Widget disableWidget;

    /**
     * The widget that is made visible as soon when this event is triggered.
     */
    private Widget enableWidget;

    /**
     * The private constructor to avoid instances created uncontrolled.
     */
    private ToggleWidgetsEvent() {
        // private constructor to avoid instances created uncontrolled.
    }

    /**
     * Get a new instance of this event script. This either creates a new
     * instance of this class or returns always the same, depending on what is
     * needed for this script.
     * 
     * @return the instance of this event script that is to be used from now on
     */
    public static ToggleWidgetsEvent getInstance() {
        return new ToggleWidgetsEvent();
    }

    /**
     * This function is called when the event is triggered. In this case one
     * linked widget is set visible and another is set invisible.
     * 
     * @param source the widget this event was triggered from
     */
    @Override
    @SuppressWarnings("nls")
    public void handleEvent(final Widget source) {
        assert (enableWidget != null) && (disableWidget != null) : "Useless widget event.";
        if (enableWidget != null) {
            enableWidget.setVisible(true);
        }
        if (disableWidget != null) {
            disableWidget.setVisible(false);
        }
    }

    /**
     * Set the widgets that are effected by this event. In case one of the
     * widgets is set to <code>null</code> widget is effected this way.
     * 
     * @param visibleWidget the widget that becomes visible when the event is
     *            triggered
     * @param invisibleWidget the widget that becomes invisible when the event
     *            is triggered
     */
    public void setEffectedWidgets(final Widget visibleWidget,
        final Widget invisibleWidget) {
        enableWidget = visibleWidget;
        disableWidget = invisibleWidget;
    }

}
