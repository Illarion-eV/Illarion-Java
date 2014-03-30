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
package org.illarion.nifty.controls;

import de.lessvoid.nifty.controls.NiftyControl;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * This interface defines the possibilities to control a progress bar.
 */
@NotThreadSafe
public interface Progress extends NiftyControl {
    /**
     * Set the value of the progress. All values will be clamped to {@code 0.f} and {@code 1.f}.
     *
     * @param value the progress value
     */
    void setProgress(double value);
}
