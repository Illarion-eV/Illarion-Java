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
package illarion.common.util;

import javax.annotation.Nonnull;

/**
 * This interface is the callback interface that can be used to receive updates of the progress monitor.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface ProgressMonitorCallback {
    /**
     * This function is called in case the progress monitor receives a new update.
     *
     * @param monitor the monitor that contains the new value
     */
    void updatedProgress(@Nonnull final ProgressMonitor monitor);
}
