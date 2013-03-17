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
package illarion.client.loading;

import illarion.client.graphics.SpriteBuffer;
import illarion.common.util.ProgressMonitor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * The finishing task for the loading sequence. This one should be called as
 * the last one during the loading sequence.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public final class FinishLoading implements LoadingTask {
    /**
     * The progress monitor of this class.
     */
    @Nonnull
    private final ProgressMonitor monitor = new ProgressMonitor();

    /**
     * This is set to {@code true} once the loading is done
     */
    private boolean loadingDone;

    @Override
    public void load() {
        SpriteBuffer.getInstance().cleanup();
        monitor.setProgress(1.f);
        loadingDone = true;
    }

    @Override
    public boolean isLoadingDone() {
        return loadingDone;
    }

    @Nonnull
    @Override
    public ProgressMonitor getProgressMonitor() {
        return monitor;
    }
}
