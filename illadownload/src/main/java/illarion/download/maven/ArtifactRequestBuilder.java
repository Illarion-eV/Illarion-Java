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

import illarion.download.launcher.OSDetection;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionScheme;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class ArtifactRequestBuilder implements DependencyVisitor {
    @Nullable
    private final RequestTrace trace;

    @Nonnull
    private final VersionScheme versionScheme;

    @Nonnull
    private final List<FutureArtifactRequest> requests;
    @Nonnull
    private final RepositorySystem system;
    @Nonnull
    private final RepositorySystemSession session;
    @Nonnull
    private final ArtifactRequestTracer requestTracer;

    public ArtifactRequestBuilder(
            @Nullable RequestTrace trace,
            @Nonnull RepositorySystem system,
            @Nonnull RepositorySystemSession session,
            @Nonnull ArtifactRequestTracer requestTracer) {
        this.trace = trace;
        this.requests = new ArrayList<>();
        versionScheme = new GenericVersionScheme();
        this.session = session;
        this.system = system;
        this.requestTracer = requestTracer;
    }

    @Nonnull
    public List<FutureArtifactRequest> getRequests() {
        return Collections.unmodifiableList(requests);
    }

    @Override
    public boolean visitEnter(@Nonnull DependencyNode node) {
        if (node.getDependency() != null) {
            Artifact nodeArtifact = node.getDependency().getArtifact();
            if (nodeArtifact == null) {
                return false;
            }
            String classifier = nodeArtifact.getClassifier();
            if (classifier.contains("native")) {
                if (classifier.contains("win") && !OSDetection.isWindows()) {
                    return true;
                }
                if (classifier.contains("linux") && !OSDetection.isLinux()) {
                    return true;
                }
                if ((classifier.contains("mac") || classifier.contains("osx")) && !OSDetection.isMacOSX()) {
                    return true;
                }
            }
            boolean noMatch = true;
            for (int i = 0; i < requests.size(); i++) {
                ArtifactRequest testRequest = requests.get(i).getRequest();
                Artifact testArtifact = testRequest.getArtifact();
                if (testArtifact == null) {
                    continue;
                }
                if (!testArtifact.getGroupId().equals(nodeArtifact.getGroupId())) {
                    continue;
                }
                if (!testArtifact.getArtifactId().equals(nodeArtifact.getArtifactId())) {
                    continue;
                }
                if (!testArtifact.getExtension().equals(nodeArtifact.getExtension())) {
                    continue;
                }
                if (!testArtifact.getClassifier().equals(nodeArtifact.getClassifier())) {
                    continue;
                }

                try {
                    Version testVersion = versionScheme.parseVersion(testArtifact.getVersion());
                    Version nodeVersion = versionScheme.parseVersion(nodeArtifact.getVersion());

                    if (nodeVersion.compareTo(testVersion) > 0) {
                        requests.remove(i);
                    } else {
                        noMatch = false;
                    }
                    break;
                } catch (InvalidVersionSpecificationException ignored) {
                }
            }

            if (noMatch) {
                ArtifactRequest request = new ArtifactRequest(node);
                request.setTrace(trace);

                requests.add(new FutureArtifactRequest(system, session, request, requestTracer));
            }
        }

        return true;
    }

    @Override
    public boolean visitLeave(DependencyNode node) {
        return true;
    }
}