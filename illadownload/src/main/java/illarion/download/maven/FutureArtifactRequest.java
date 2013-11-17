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

    public FutureArtifactRequest(@Nonnull final RepositorySystem system,
                                 @Nonnull final RepositorySystemSession session,
                                 @Nonnull final ArtifactRequest request) {
        this.request = request;
        this.system = system;
        this.session = session;
        progressMonitor = new ProgressMonitor();

        request.setTrace(new RequestTrace(progressMonitor));
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
        final ArtifactResult result = system.resolveArtifact(session, request);
        progressMonitor.setProgress(1.f);
        return result;
    }
}
