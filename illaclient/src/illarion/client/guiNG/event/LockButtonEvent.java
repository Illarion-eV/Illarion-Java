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

import illarion.client.guiNG.elements.DragLayer;
import illarion.client.guiNG.elements.Widget;

/**
 * This event script is called to handle the lock and unlock actions of a
 * locking button.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class LockButtonEvent implements WidgetEvent {
    /**
     * The serialization UID this event script.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The widget that is set visible in case the target widget is set to lock.
     */
    private Widget lockedWidget;

    /**
     * The target drag layer that is toggled.
     */
    private DragLayer target;

    /**
     * The widget that is set to visible in case the target widget is set to
     * unlock.
     */
    private Widget unlockedWidget;

    /**
     * The private constructor to avoid instances created uncontrolled.
     */
    private LockButtonEvent() {
        // private constructor to avoid instances created uncontrolled.
    }

    /**
     * Get a new instance of this event script. This either creates a new
     * instance of this class or returns always the same, depending on what is
     * needed for this script.
     * 
     * @return the instance of this event script that is to be used from now on
     */
    public static LockButtonEvent getInstance() {
        return new LockButtonEvent();
    }

    /**
     * Handle this event. Toggle the state of the target widget and set the
     * locked and unlocked widgets according this.
     * 
     * @param source the widget this event was called from
     */
    @Override
    public void handleEvent(final Widget source) {
        if (target.draggingEnabled()) {
            target.disableDragging();
            lockedWidget.setVisible(true);
            unlockedWidget.setVisible(false);
        } else {
            target.enableDragging();
            lockedWidget.setVisible(false);
            unlockedWidget.setVisible(true);
        }
    }

    /**
     * Set the effect widgets that are exchanged when the event is triggered.
     * 
     * @param locked the widget that is set visible in case the target widget is
     *            set to locked
     * @param unlocked the widget that is set visible in case the target widget
     *            is set to unlocked
     */
    public void setEffectWidgets(final Widget locked, final Widget unlocked) {
        lockedWidget = locked;
        unlockedWidget = unlocked;
    }

    /**
     * Set the target of this widget event. The enable value of the dragging
     * layer is toggled when this event is triggered.
     * 
     * @param targetWidget the dragging layer that is effected
     */
    public void setTargetWidget(final DragLayer targetWidget) {
        target = targetWidget;
    }

}
