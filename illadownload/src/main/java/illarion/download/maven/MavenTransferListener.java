package illarion.download.maven;

import illarion.common.util.ProgressMonitor;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;

import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MavenTransferListener implements TransferListener {
    @Override
    public void transferInitiated(TransferEvent event) throws TransferCancelledException {
    }

    @Override
    public void transferStarted(TransferEvent event) throws TransferCancelledException {
    }

    @Override
    public void transferProgressed(TransferEvent event) throws TransferCancelledException {
        @Nullable RequestTrace trace = event.getResource().getTrace();
        while (true) {
            if (trace == null) {
                break;
            }
            if (trace.getData() instanceof ProgressMonitor) {
                final ProgressMonitor monitor = (ProgressMonitor) trace.getData();
                final long totalSize = event.getResource().getContentLength();
                if (totalSize <= 0) {
                    return;
                }
                monitor.setProgress((float) event.getTransferredBytes() / (float) totalSize);
            }
            trace = trace.getParent();
        }
    }

    @Override
    public void transferCorrupted(TransferEvent event) throws TransferCancelledException {
    }

    @Override
    public void transferSucceeded(TransferEvent event) {
    }

    @Override
    public void transferFailed(TransferEvent event) {
    }
}
