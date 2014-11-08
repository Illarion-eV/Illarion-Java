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
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MavenTransferListener implements TransferListener {
    private static final Logger log = LoggerFactory.getLogger(MavenTransferListener.class);

    @Override
    public void transferInitiated(@Nonnull TransferEvent event) throws TransferCancelledException {
        log.info(event.toString());
    }

    @Override
    public void transferStarted(@Nonnull TransferEvent event) throws TransferCancelledException {
        log.info(event.toString());
        @Nullable Object[] dataArray = getTraceObjectArray(event);
        if (dataArray != null) {
            reportTrace(dataArray, event);
        }
    }

    @Override
    public void transferProgressed(@Nonnull TransferEvent event) throws TransferCancelledException {
        log.debug(event.toString());
        @Nullable Object[] dataArray = getTraceObjectArray(event);
        if (dataArray != null) {
            reportTrace(dataArray, event);
        }
    }

    private void reportTrace(@Nonnull Object[] dataArray, TransferEvent event) {
        ArtifactRequestTracer requestTracer = (ArtifactRequestTracer) dataArray[0];
        ProgressMonitor monitor = (ProgressMonitor) dataArray[1];
        long totalSize = event.getResource().getContentLength();
        requestTracer.trace(monitor, event.getResource().getResourceName(), totalSize, event.getTransferredBytes());
    }

    @Nullable
    private Object[] getTraceObjectArray(@Nonnull TransferEvent event) {
        @Nullable RequestTrace trace = event.getResource().getTrace();
        while (true) {
            if (trace == null) {
                break;
            }
            if (trace.getData() instanceof Object[]) {
                return (Object[]) trace.getData();
            }
            trace = trace.getParent();
        }
        return null;
    }

    @Override
    public void transferCorrupted(@Nonnull TransferEvent event) throws TransferCancelledException {
        log.error(event.toString());
    }

    @Override
    public void transferSucceeded(TransferEvent event) {
        log.info(event.toString());
        @Nullable Object[] dataArray = getTraceObjectArray(event);
        if (dataArray != null) {
            reportTrace(dataArray, event);
        }
    }

    @Override
    public void transferFailed(TransferEvent event) {
        log.info(event.toString());
    }
}
