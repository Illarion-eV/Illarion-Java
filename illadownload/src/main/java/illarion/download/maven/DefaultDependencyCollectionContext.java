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

    public DefaultDependencyCollectionContext(final RepositorySystemSession session, final Artifact artifact,
                                              final Dependency dependency) {
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
        //noinspection unchecked
        return Collections.EMPTY_LIST;
    }
}
