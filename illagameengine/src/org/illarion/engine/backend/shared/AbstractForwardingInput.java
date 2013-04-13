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

import org.illarion.engine.input.ForwardingTarget;
import org.illarion.engine.input.Input;

import javax.annotation.Nonnull;
import java.util.EnumMap;
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
     * Create a new instance and setup the required interla structures.
     */
    protected AbstractForwardingInput() {
        forwardingFlags = new EnumMap<ForwardingTarget, Boolean>(ForwardingTarget.class);
        disableForwarding(ForwardingTarget.All);
    }

    @Override
    public boolean isForwardingEnabled(@Nonnull final ForwardingTarget target) {
        return forwardingFlags.get(ForwardingTarget.All) || forwardingFlags.get(target);
    }

    @Override
    public void enableForwarding(@Nonnull final ForwardingTarget target) {
        if (target == ForwardingTarget.All) {
            for (@Nonnull final ForwardingTarget currentTarget : ForwardingTarget.values()) {
                forwardingFlags.put(currentTarget, Boolean.TRUE);
            }
        } else {
            forwardingFlags.put(target, Boolean.TRUE);
        }
    }

    @Override
    public void disableForwarding(@Nonnull final ForwardingTarget target) {
        if (target == ForwardingTarget.All) {
            for (@Nonnull final ForwardingTarget currentTarget : ForwardingTarget.values()) {
                forwardingFlags.put(currentTarget, Boolean.FALSE);
            }
        } else {
            forwardingFlags.put(ForwardingTarget.All, Boolean.FALSE);
            forwardingFlags.put(target, Boolean.FALSE);
        }
    }
}
