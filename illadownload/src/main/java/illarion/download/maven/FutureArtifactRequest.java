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
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class FutureArtifactRequest implements Callable<ArtifactResult> {
    @Nonnull
    private final ArtifactRequest request;
    @Nonnull
    private final RepositorySystem system;
    @Nonnull
    private final RepositorySystemSession session;
    @Nonnull
    private final ProgressMonitor progressMonitor;

    public FutureArtifactRequest(
            @Nonnull RepositorySystem system,
            @Nonnull RepositorySystemSession session,
            @Nonnull ArtifactRequest request,
            @Nonnull ArtifactRequestTracer requestTracer) {
        this.request = request;
        this.system = system;
        this.session = session;
        progressMonitor = new ProgressMonitor();

        request.setTrace(new RequestTrace(new Object[]{requestTracer, progressMonitor}));
    }

    @Nonnull
    public ArtifactRequest getRequest() {
        return request;
    }

    @Nonnull
    public ProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }

    @Override
    public ArtifactResult call() throws Exception {
        progressMonitor.setProgress(0.f);
        ArtifactResult result = system.resolveArtifact(session, request);
        progressMonitor.setProgress(1.f);
        return result;
    }
}
