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
package org.illarion.engine.input;

import javax.annotation.Nonnull;

/**
 * This listener is used to monitor changes of the forwarding input system.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface ForwardingListener {
    /**
     * This function in case the forwarding for a specific target got enabled.
     *
     * @param target the forwarding target that got enabled
     */
    void forwardingEnabledFor(@Nonnull ForwardingTarget target);

    /**
     * This function in case the forwarding for a specific target got disabled.
     *
     * @param target the forwarding target that got disabled
     */
    void forwardingDisabledFor(@Nonnull ForwardingTarget target);
}
