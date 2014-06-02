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
package illarion.download.maven;

import illarion.common.util.ProgressMonitor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Slf4j
public class MavenTransferListener implements TransferListener {
    @Override
    public void transferInitiated(TransferEvent event) throws TransferCancelledException {
        log.info(event.toString());
    }

    @Override
    public void transferStarted(TransferEvent event) throws TransferCancelledException {
        log.info(event.toString());
    }

    @Override
    public void transferProgressed(@Nonnull TransferEvent event) throws TransferCancelledException {
        log.info(event.toString());
        @Nullable RequestTrace trace = event.getResource().getTrace();
        while (true) {
            if (trace == null) {
                break;
            }
            if (trace.getData() instanceof ProgressMonitor) {
                ProgressMonitor monitor = (ProgressMonitor) trace.getData();
                long totalSize = event.getResource().getContentLength();
                if (totalSize <= 0) {
                    return;
                }
                monitor.setProgress((float) event.getTransferredBytes() / totalSize);
            }
            trace = trace.getParent();
        }
    }

    @Override
    public void transferCorrupted(TransferEvent event) throws TransferCancelledException {
        log.info(event.toString());
    }

    @Override
    public void transferSucceeded(TransferEvent event) {
        log.info(event.toString());
    }

    @Override
    public void transferFailed(TransferEvent event) {
        log.info(event.toString());
    }
}
