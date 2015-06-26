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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class DefaultDependencyCollectionContext implements DependencyCollectionContext {
    @Nonnull
    private final RepositorySystemSession session;
    @Nullable
    private final Dependency dependency;

    public DefaultDependencyCollectionContext(
            @Nonnull RepositorySystemSession session, @Nullable Dependency dependency) {
        this.session = session;
        this.dependency = dependency;
    }

    @Nonnull
    @Override
    public RepositorySystemSession getSession() {
        return session;
    }

    @Override
    @Nullable
    public Artifact getArtifact() {
        return (dependency != null) ? dependency.getArtifact() : null;
    }

    @Override
    @Nullable
    public Dependency getDependency() {
        return dependency;
    }

    @Override
    @Nonnull
    public List<Dependency> getManagedDependencies() {
        return Collections.emptyList();
    }

    @Override
    @Nonnull
    public String toString() {
        return String.valueOf(getDependency());
    }
}
