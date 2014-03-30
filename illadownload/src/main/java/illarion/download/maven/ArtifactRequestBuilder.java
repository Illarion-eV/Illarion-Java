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
import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class ArtifactRequestBuilder implements DependencyVisitor {
    @Nullable
    private final RequestTrace trace;

    @Nonnull
    private final VersionScheme versionScheme;

    private List<FutureArtifactRequest> requests;
    @Nonnull
    private final RepositorySystem system;
    @Nonnull
    private final RepositorySystemSession session;

    public ArtifactRequestBuilder(
            @Nullable final RequestTrace trace,
            @Nonnull final RepositorySystem system,
            @Nonnull final RepositorySystemSession session) {
        this.trace = trace;
        this.requests = new ArrayList<>();
        versionScheme = new GenericVersionScheme();
        this.session = session;
        this.system = system;
    }

    public List<FutureArtifactRequest> getRequests() {
        return requests;
    }

    public boolean visitEnter(@Nonnull final DependencyNode node) {
        if (node.getDependency() != null) {
            final Artifact nodeArtifact = node.getDependency().getArtifact();
            final String classifier = nodeArtifact.getClassifier();
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
                final ArtifactRequest testRequest = requests.get(i).getRequest();
                final Artifact testArtifact = testRequest.getArtifact();
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
                    final Version testVersion = versionScheme.parseVersion(testArtifact.getVersion());
                    final Version nodeVersion = versionScheme.parseVersion(nodeArtifact.getVersion());

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

                requests.add(new FutureArtifactRequest(system, session, request));
            }
        }

        return true;
    }

    public boolean visitLeave(final DependencyNode node) {
        return true;
    }
}