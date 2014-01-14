package illarion.download.maven;

import illarion.common.util.DirectoryManager;
import illarion.common.util.ProgressMonitor;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.repository.internal.*;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.impl.*;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.eclipse.aether.util.graph.selector.AndDependencySelector;
import org.eclipse.aether.util.graph.selector.OptionalDependencySelector;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;
import org.eclipse.aether.util.graph.visitor.FilteringDependencyVisitor;
import org.eclipse.aether.util.graph.visitor.TreeDependencyVisitor;
import org.eclipse.aether.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.concurrent.*;

import static org.eclipse.aether.repository.RepositoryPolicy.*;
import static org.eclipse.aether.util.artifact.JavaScopes.*;

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

    /**
     * The repository system that is used by this downloader. This stores the repositories that are queried for the
     * artifacts and the location of the local repository.
     */
    @Nonnull
    private final RepositorySystem system;

    /**
     * The maven session that stores the current temporary states of the maven resolver.
     */
    @Nonnull
    private final DefaultRepositorySystemSession session;

    /**
     * In case this is set {@code true} the downloader will accept snapshot versions of the root artifacts.
     */
    private final boolean snapshot;

    /**
     * The logger that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenDownloader.class);

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

        repositories = new ArrayList<>();
        setupRepositories();
    }

    /**
     * Download the artifacts. This will check if the files are present and download all missing files.
     *
     * @param groupId the group id of the artifact to download
     * @param artifactId the artifact id of the artifact to download
     * @param callback the callback implementation to report to
     * @return the files that have to be in the classpath to run the application
     */
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
            final NavigableSet<String> versions = new TreeSet<>(new Comparator<String>() {
                @Override
                public int compare(@Nonnull final String o1, @Nonnull final String o2) {
                    String[] versionParts1 = o1.split("[.-]");
                    String[] versionParts2 = o2.split("[.-]");

                    int count = Math.min(versionParts1.length, versionParts2.length);
                    for (int i = 0; i < count; i++) {
                        int cmpResult = compareEntry(versionParts1[i], versionParts2[i]);
                        if (cmpResult != 0) {
                            return cmpResult;
                        }
                    }

                    return Integer.compare(versionParts1.length, versionParts2.length);
                }

                public int compareEntry(@Nonnull final String e1, @Nonnull final String e2) {
                    if ("SNAPSHOT".equals(e1)) {
                        if ("SNAPSHOT".equals(e2)) {
                            return 0;
                        }
                        return -1;
                    } else if ("SNAPSHOT".equals(e2)) {
                        return 1;
                    }
                    try {
                        int version1 = Integer.parseInt(e1);
                        int version2 = Integer.parseInt(e2);
                        return Integer.compare(version1, version2);
                    } catch (NumberFormatException e) {
                        // illegal formatted number
                    }
                    return e1.compareTo(e2);
                }
            });
            for (final Version version : result.getVersions()) {
                if (snapshot || !version.toString().contains("SNAPSHOT")) {
                    versions.add(version.toString());
                }
            }

            if (!versions.isEmpty()) {
                artifact = new DefaultArtifact(groupId, artifactId, "jar", versions.pollLast());
            }
        } catch (VersionRangeResolutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (callback != null) {
            callback.reportNewState(MavenDownloaderCallback.State.ResolvingDependencies, null);
        }
        final Dependency dependency = new Dependency(artifact, RUNTIME, false);

        final List<String> usedScopes = Arrays.asList(COMPILE, RUNTIME, SYSTEM);
        final DependencySelector selector = new AndDependencySelector(new OptionalDependencySelector(),
                                                                      new ScopeDependencySelector(usedScopes, null));
        session.setDependencySelector(
                selector.deriveChildSelector(new DefaultDependencyCollectionContext(session, artifact, dependency)));

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

            final List<File> result = new ArrayList<>();
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
        } catch (@Nonnull DependencyCollectionException | InterruptedException | ExecutionException e) {
            LOGGER.error("Failed to download.");
        }
        if (callback != null) {
            callback.resolvingDone(null);
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
