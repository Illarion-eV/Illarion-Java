/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.util;

import illarion.client.gui.events.TooltipsRemovedEvent;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Objects;

/**
 * The sole purpose of this class is not monitor the look at events that were invoked in order to prevent that they
 * get invoked more then once.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LookAtTracker implements EventSubscriber<TooltipsRemovedEvent> {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(LookAtTracker.class);

    /**
     * The instance of this class.
     */
    @Nonnull
    private static final LookAtTracker INSTANCE = new LookAtTracker();

    /**
     * The reference to the object last looked at.
     */
    @Nullable
    private Reference<Object> lookAtObject;

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
    public static void setLookAtObject(@Nullable Object object) {
        log.debug("Setting look at tracker to: {}", object);
        INSTANCE.lookAtObject = object == null ? null : new SoftReference<>(object);
    }

    /**
     * Check if a specified object is the current look at focus.
     *
     * @param testObject the object to test
     * @return {@code true} in case this object is the same object last set with {@link #setLookAtObject(Object)}
     */
    public static boolean isLookAtObject(@Nonnull Object testObject) {
        Reference<Object> reference = INSTANCE.lookAtObject;
        if ((reference != null) && Objects.equals(reference.get(), testObject)) {
            log.debug("Test for look at object {} successful.", testObject);
            return true;
        } else {
            log.debug("Test for look at object {} failed.", testObject);
            return false;
        }
    }

    @Override
    public void onEvent(@Nonnull TooltipsRemovedEvent event) {
        setLookAtObject(null);
    }
}
