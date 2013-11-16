package illarion.download.maven;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
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

    public FutureArtifactRequest(@Nonnull final RepositorySystem system,
                                 @Nonnull final RepositorySystemSession session,
                                 @Nonnull final ArtifactRequest request) {
        this.request = request;
        this.system = system;
        this.session = session;
    }

    @Nonnull
    public ArtifactRequest getRequest() {
        return request;
    }

    @Override
    public ArtifactResult call() throws Exception {
        return system.resolveArtifact(session, request);
    }
}
