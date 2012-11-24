/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.util;

import illarion.client.gui.events.TooltipsRemovedEvent;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

import java.lang.ref.SoftReference;

/**
 * The sole purpose of this class is not monitor the look at events that were invoked in order to prevent that they
 * get invoked more then once.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LookAtTracker implements EventSubscriber<TooltipsRemovedEvent> {
    /**
     * The instance of this class.
     */
    private static final LookAtTracker INSTANCE = new LookAtTracker();

    /**
     * The reference to the object last looked at.
     */
    private SoftReference<Object> lookAtObject;

    /**
     * Private constructor that subscribes the events required.
     */
    private LookAtTracker() {
        EventBus.subscribe(TooltipsRemovedEvent.class, this);
    }

    /**
     * Set the item that was last looked at. After calling this function {@link #isLookAtObject(Object)} will return
     * {@code true} for this object until all tooltips are removed or this function is called again.
     *
     * @param object the object to set als current look at focus
     */
    public static void setLookAtObject(final Object object) {
        INSTANCE.lookAtObject = new SoftReference<Object>(object);
    }

    /**
     * Check if a specified object is the current look at focus.
     *
     * @param testObject the object to test
     * @return {@code true} in case this object is the same object last set with {@link #setLookAtObject(Object)}
     */
    public static boolean isLookAtObject(final Object testObject) {
        if (INSTANCE.lookAtObject == null) {
            return false;
        }

        final Object localLookAtObject = INSTANCE.lookAtObject.get();
        if (localLookAtObject == null) {
            return false;
        }

        return localLookAtObject == testObject;
    }

    @Override
    public void onEvent(final TooltipsRemovedEvent event) {
        lookAtObject = null;
    }
}
