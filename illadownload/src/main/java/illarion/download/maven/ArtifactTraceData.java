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

import javax.annotation.Nonnull;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class ArtifactTraceData {
    @Nonnull
    private final ProgressMonitor monitor;
    @Nonnull
    private final ArtifactRequestTracer tracer;

    ArtifactTraceData(@Nonnull ArtifactRequestTracer tracer, @Nonnull ProgressMonitor monitor) {
        this.tracer = tracer;
        this.monitor = monitor;
    }

    @Nonnull
    public ProgressMonitor getMonitor() {
        return monitor;
    }

    @Nonnull
    public ArtifactRequestTracer getTracer() {
        return tracer;
    }
}
