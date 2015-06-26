/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
class MavenTransferListener implements TransferListener {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(MavenTransferListener.class);

    @Override
    public void transferInitiated(@Nonnull TransferEvent event) throws TransferCancelledException {
        log.info(event.toString());
    }

    @Override
    public void transferStarted(@Nonnull TransferEvent event) throws TransferCancelledException {
        log.info(event.toString());
        @Nullable ArtifactTraceData data = getTraceData(event);
        if (data != null) {
            reportTrace(data, event);
        }
    }

    @Override
    public void transferProgressed(@Nonnull TransferEvent event) throws TransferCancelledException {
        log.debug(event.toString());
        @Nullable ArtifactTraceData data = getTraceData(event);
        if (data != null) {
            reportTrace(data, event);
        }
    }

    @Override
    public void transferCorrupted(@Nonnull TransferEvent event) throws TransferCancelledException {
        log.error(event.toString());
    }

    @Override
    public void transferSucceeded(@Nonnull TransferEvent event) {
        log.info(event.toString());
        @Nullable ArtifactTraceData data = getTraceData(event);
        if (data != null) {
            reportTrace(data, event);
        }
    }

    @Override
    public void transferFailed(@Nonnull TransferEvent event) {
        log.info(event.toString());
    }

    @Nullable
    private static ArtifactTraceData getTraceData(@Nonnull TransferEvent event) {
        @Nullable RequestTrace trace = event.getResource().getTrace();
        while (true) {
            if (trace == null) {
                break;
            }
            if (trace.getData() instanceof ArtifactTraceData) {
                return (ArtifactTraceData) trace.getData();
            }
            trace = trace.getParent();
        }
        return null;
    }

    private static void reportTrace(@Nonnull ArtifactTraceData data, @Nonnull TransferEvent event) {
        ArtifactRequestTracer requestTracer = data.getTracer();
        ProgressMonitor monitor = data.getMonitor();
        long totalSize = event.getResource().getContentLength();
        requestTracer.trace(monitor, event.getResource().getResourceName(), totalSize, event.getTransferredBytes());
    }
}
