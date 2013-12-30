/*
 * This file is part of the Illarion Game Engine.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Game Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Game Engine.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.illarion.engine.backend.shared;

import org.illarion.engine.input.ForwardingListener;
import org.illarion.engine.input.ForwardingTarget;
import org.illarion.engine.input.Input;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    private final Map<ForwardingTarget, Boolean> forwardingFlags;

    /**
     * The list of forwarding listeners that receive updates in case the forwarding state changed.
     */
    @Nonnull
    private final List<ForwardingListener> forwardingListeners;

    /**
     * Create a new instance and setup the required internal structures.
     */
    protected AbstractForwardingInput() {
        forwardingFlags = new EnumMap<>(ForwardingTarget.class);
        forwardingListeners = new LinkedList<>();
        disableForwarding(ForwardingTarget.All);
    }

    @Override
    public boolean isForwardingEnabled(@Nonnull final ForwardingTarget target) {
        return forwardingFlags.get(ForwardingTarget.All) || forwardingFlags.get(target);
    }

    @Override
    public void enableForwarding(@Nonnull final ForwardingTarget target) {
        boolean changedSomething = false;
        if (target == ForwardingTarget.All) {
            for (@Nonnull final ForwardingTarget currentTarget : ForwardingTarget.values()) {
                if (Boolean.FALSE.equals(forwardingFlags.put(currentTarget, Boolean.TRUE))) {
                    changedSomething = true;
                }
            }
        } else {
            changedSomething = Boolean.FALSE.equals(forwardingFlags.put(target, Boolean.TRUE));
        }
        if (changedSomething) {
            for (@Nonnull final ForwardingListener listener : forwardingListeners) {
                listener.forwardingEnabledFor(target);
            }
        }
    }

    @Override
    public void disableForwarding(@Nonnull final ForwardingTarget target) {
        boolean changedSomething = false;
        if (target == ForwardingTarget.All) {
            for (@Nonnull final ForwardingTarget currentTarget : ForwardingTarget.values()) {
                if (Boolean.TRUE.equals(forwardingFlags.put(currentTarget, Boolean.FALSE))) {
                    changedSomething = true;
                }
            }
        } else {
            if (Boolean.TRUE.equals(forwardingFlags.put(ForwardingTarget.All, Boolean.FALSE))) {
                changedSomething = true;
            }
            if (Boolean.TRUE.equals(forwardingFlags.put(target, Boolean.FALSE))) {
                changedSomething = true;
            }
        }
        if (changedSomething) {
            for (@Nonnull final ForwardingListener listener : forwardingListeners) {
                listener.forwardingDisabledFor(target);
            }
        }
    }

    @Override
    public void addForwardingListener(@Nonnull final ForwardingListener listener) {
        forwardingListeners.add(listener);
    }
}
