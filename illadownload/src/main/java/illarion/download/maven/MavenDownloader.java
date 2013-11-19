package illarion.download.maven;

import illarion.common.config.Config;
import illarion.common.util.DirectoryManager;
import illarion.common.util.ProgressMonitor;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.repository.internal.*;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.*;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.impl.*;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;
import org.eclipse.aether.util.graph.visitor.FilteringDependencyVisitor;
import org.eclipse.aether.util.graph.visitor.TreeDependencyVisitor;
import org.eclipse.aether.version.Version;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static org.eclipse.aether.repository.RepositoryPolicy.CHECKSUM_POLICY_FAIL;
import static org.eclipse.aether.repository.RepositoryPolicy.UPDATE_POLICY_ALWAYS;
import static org.eclipse.aether.repository.RepositoryPolicy.UPDATE_POLICY_NEVER;
import static org.eclipse.aether.util.artifact.JavaScopes.COMPILE;
import static org.eclipse.aether.util.artifact.JavaScopes.RUNTIME;
import static org.eclipse.aether.util.artifact.JavaScopes.SYSTEM;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MavenDownloader {
    /**
     * The list of repositories that are used.
     */
    @Nonnull
    private final List<RemoteRepository> repositories;

    /**
     * The service locator used to link in the required services.
     */
    @Nonnull
    private final DefaultServiceLocator serviceLocator;

    @Nonnull
    private final RepositorySystem system;

    @Nonnull
    private final DefaultRepositorySystemSession session;

    private final boolean snapshot;

    /**
     * Create a new instance of the downloader along with the information if its supposed to download snapshot
     * versions of the main application.
     *
     * @param snapshot {@code true} in case the downloader is supposed to use snapshot versions of the main application
     */
    public MavenDownloader(final boolean snapshot) {
        this.snapshot = snapshot;

        serviceLocator = new DefaultServiceLocator();
        setupServiceLocator();

        system = serviceLocator.getService(RepositorySystem.class);
        session = new DefaultRepositorySystemSession();

        session.setTransferListener(new MavenTransferListener());

        repositories = new ArrayList<RemoteRepository>();
        setupRepositories();
    }

    /**
     * Download the artifacts in a background worker thread. This function only launches a new thread that performs
     * the download operation. It will not block the execution until its done.
     *
     * @param groupId the group id of the artifact to download
     * @param artifactId the artifact id of the artifact to download
     * @param callback the callback implementation to report to
     */
    public void downloadArtifactNonBlocking(@Nonnull final String groupId, @Nonnull final String artifactId,
                                            @Nonnull final MavenDownloaderCallback callback) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                downloadArtifact(groupId, artifactId, callback);
            }
        }, "Download Artifacts Thread");
        thread.setDaemon(true);
        thread.run();
    }

    @Nullable
    public Collection<File> downloadArtifact(@Nonnull final String groupId, @Nonnull final String artifactId,
                                             @Nullable final MavenDownloaderCallback callback) {
        Artifact artifact = new DefaultArtifact(groupId, artifactId, "jar", "[1,]");

        try {
            if (callback != null) {
                callback.reportNewState(MavenDownloaderCallback.State.SearchingNewVersion, null);
            }
            final VersionRangeResult result = system.resolveVersionRange(session, new VersionRangeRequest(artifact,
                    repositories, RUNTIME));
            Version selectedVersion = null;
            for (final Version version : result.getVersions()) {
                if (snapshot || !version.toString().contains("SNAPSHOT")) {
                    selectedVersion = version;
                }
            }

            if (selectedVersion != null) {
                artifact = new DefaultArtifact(groupId, artifactId, "jar", selectedVersion.toString());
            }
        } catch (VersionRangeResolutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (callback != null) {
            callback.reportNewState(MavenDownloaderCallback.State.ResolvingDependencies, null);
        }
        final Dependency dependency = new Dependency(artifact, RUNTIME, false);

        final List<String> usedScopes = Arrays.asList(COMPILE, RUNTIME, SYSTEM);
        session.setDependencySelector(
                new ScopeDependencySelector(usedScopes, null)
                        .deriveChildSelector(new DefaultDependencyCollectionContext(session, artifact, dependency)));

        final DependencyFilter filter = DependencyFilterUtils.classpathFilter(RUNTIME, COMPILE);

        final CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(dependency);
        collectRequest.setRepositories(repositories);

        try {
            final CollectResult collectResult = system.collectDependencies(session, collectRequest);

            final ArtifactRequestBuilder builder = new ArtifactRequestBuilder(null, system, session);
            DependencyVisitor visitor = new FilteringDependencyVisitor(builder, filter);
            visitor = new TreeDependencyVisitor(visitor);
            collectResult.getRoot().accept(visitor);

            final List<FutureArtifactRequest> requests = builder.getRequests();

            if (callback != null) {
                final ProgressMonitor progressMonitor = new ProgressMonitor();
                for (@Nonnull final FutureArtifactRequest request : requests) {
                    progressMonitor.addChild(request.getProgressMonitor());
                }
                callback.reportNewState(MavenDownloaderCallback.State.ResolvingArtifacts, progressMonitor);
            }

            final ExecutorService executorService = Executors.newCachedThreadPool();
            final List<Future<ArtifactResult>> results = executorService.invokeAll(requests);
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);

            final List<File> result = new ArrayList<File>();
            for (@Nonnull final Future<ArtifactResult> artifactResult : results) {
                result.add(artifactResult.get().getArtifact().getFile());
            }

            if (result.isEmpty()) {
                if (callback != null) {
                    callback.resolvingDone(null);
                }
                return null;
            }
            if (callback != null) {
                callback.resolvingDone(result);
            }
            return result;
        } catch (DependencyCollectionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ExecutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    private void setupRepositories() {
        repositories.add(setupRepository("central", "http://repo1.maven.org/maven2/", true));
        repositories.add(
                setupRepository("nifty-gui", "http://nifty-gui.sourceforge.net/nifty-maven-repo", true));
        repositories.add(setupRepository("illarion", "http://illarion.org/media/java/maven", snapshot));
        repositories.add(setupRepository("oss-sonatype", "http://oss.sonatype.org/content/repositories/releases/",
                true));

        LocalRepository localRepo = new LocalRepository(DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.Data));
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
    }

    @Nonnull
    private RemoteRepository setupRepository(@Nonnull final String id,
                                             @Nonnull final String url,
                                             final boolean enableSnapshots) {
        RemoteRepository.Builder repo = new RemoteRepository.Builder(id, "default", url);
        if (enableSnapshots) {
            repo.setSnapshotPolicy(new RepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_FAIL));
        } else {
            repo.setSnapshotPolicy(new RepositoryPolicy(false, UPDATE_POLICY_NEVER, CHECKSUM_POLICY_FAIL));
        }
        repo.setReleasePolicy(new RepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_FAIL));
        return repo.build();
    }

    private void setupServiceLocator() {
        serviceLocator.addService(ArtifactDescriptorReader.class, DefaultArtifactDescriptorReader.class);

        serviceLocator.addService(VersionResolver.class, DefaultVersionResolver.class);
        serviceLocator.addService(VersionRangeResolver.class, DefaultVersionRangeResolver.class);

        serviceLocator.addService(MetadataGeneratorFactory.class, SnapshotMetadataGeneratorFactory.class);
        serviceLocator.addService(MetadataGeneratorFactory.class, VersionsMetadataGeneratorFactory.class);

        serviceLocator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        serviceLocator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        serviceLocator.setServices(ModelBuilder.class, new DefaultModelBuilderFactory().newInstance());
    }
}
