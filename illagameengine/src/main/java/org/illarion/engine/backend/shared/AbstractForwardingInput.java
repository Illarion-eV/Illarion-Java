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
package org.illarion.engine.backend.shared;

import org.illarion.engine.input.ForwardingListener;
import org.illarion.engine.input.ForwardingTarget;
import org.illarion.engine.input.Input;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This implementation of the input only implements the components required for the forwarding flag.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractForwardingInput implements Input {
    /**
     * The internal structure used to store the forwarding flags.
     */
    @Nonnull
    private final Set<ForwardingTarget> forwardingFlags;

    /**
     * The list of forwarding listeners that receive updates in case the forwarding state changed.
     */
    @Nonnull
    private final List<ForwardingListener> forwardingListeners;

    /**
     * Create a new instance and setup the required internal structures.
     */
    protected AbstractForwardingInput() {
        forwardingFlags = EnumSet.noneOf(ForwardingTarget.class);
        forwardingListeners = new LinkedList<>();
    }

    @Override
    public boolean isForwardingEnabled(@Nonnull ForwardingTarget target) {
        return forwardingFlags.contains(ForwardingTarget.All) || forwardingFlags.contains(target);
    }

    @Override
    public void enableForwarding(@Nonnull ForwardingTarget target) {
        boolean changedSomething;
        if (target == ForwardingTarget.All) {
            changedSomething = forwardingFlags.addAll(EnumSet.allOf(ForwardingTarget.class));
        } else {
            changedSomething = forwardingFlags.add(target);
        }
        if (changedSomething) {
            for (@Nonnull ForwardingListener listener : forwardingListeners) {
                listener.forwardingEnabledFor(target);
            }
        }
    }

    @Override
    public void disableForwarding(@Nonnull ForwardingTarget target) {
        boolean changedSomething = false;
        if (target == ForwardingTarget.All) {
            changedSomething = !forwardingFlags.isEmpty();
            forwardingFlags.clear();
        } else {
            if (forwardingFlags.remove(ForwardingTarget.All)) {
                changedSomething = true;
            }
            if (forwardingFlags.remove(target)) {
                changedSomething = true;
            }
        }
        if (changedSomething) {
            for (@Nonnull ForwardingListener listener : forwardingListeners) {
                listener.forwardingDisabledFor(target);
            }
        }
    }

    @Override
    public void addForwardingListener(@Nonnull ForwardingListener listener) {
        forwardingListeners.add(listener);
    }
}
