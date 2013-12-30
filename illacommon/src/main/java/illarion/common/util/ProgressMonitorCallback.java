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
