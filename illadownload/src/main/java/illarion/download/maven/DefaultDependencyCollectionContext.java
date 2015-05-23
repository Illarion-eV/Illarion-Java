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

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.graph.Dependency;

import java.util.Collections;
import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class DefaultDependencyCollectionContext implements DependencyCollectionContext {
    private final RepositorySystemSession session;
    private final Artifact artifact;
    private final Dependency dependency;

    public DefaultDependencyCollectionContext(
            RepositorySystemSession session, Artifact artifact, Dependency dependency) {
        this.session = session;
        this.artifact = artifact;
        this.dependency = dependency;
    }

    @Override
    public RepositorySystemSession getSession() {
        return session;
    }

    @Override
    public Artifact getArtifact() {
        return artifact;
    }

    @Override
    public Dependency getDependency() {
        return dependency;
    }

    @Override
    public List<Dependency> getManagedDependencies() {
        return Collections.emptyList();
    }
}
