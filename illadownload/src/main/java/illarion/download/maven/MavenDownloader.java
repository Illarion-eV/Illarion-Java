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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import illarion.common.util.AppIdent;
import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import illarion.common.util.ProgressMonitor;
import illarion.download.maven.MavenDownloaderCallback.State;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.ConfigurationProperties;
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
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RemoteRepository.Builder;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.eclipse.aether.util.graph.selector.AndDependencySelector;
import org.eclipse.aether.util.graph.selector.OptionalDependencySelector;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;
import org.eclipse.aether.util.graph.visitor.FilteringDependencyVisitor;
import org.eclipse.aether.util.graph.visitor.TreeDependencyVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

import static illarion.download.maven.MavenDownloaderCallback.State.ResolvingArtifacts;
import static illarion.download.maven.MavenDownloaderCallback.State.ResolvingDependencies;
import static org.eclipse.aether.repository.RepositoryPolicy.*;
import static org.eclipse.aether.util.artifact.JavaScopes.*;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class MavenDownloader {
    @Nonnull
    private static final AppIdent APPLICATION = new AppIdent("Illarion Launcher");
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(MavenDownloader.class);

    /**
     * The list of repositories that are used.
     */
    @Nonnull
    private final List<RemoteRepository> repositories;

    @Nullable
    private RemoteRepository illarionRepository;

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
     * This value is set in case the application is running in offline mode.
     */
    private final boolean offline;

    @Nonnull
    private final MavenRepositoryListener repositoryListener;

    /**
     * Create a new instance of the downloader along with the information if its supposed to download snapshot
     * versions of the main application.
     *
     * @param snapshot {@code true} in case the downloader is supposed to use snapshot versions of the main application
     * @param attemps the indicator how many times downloading was already tried and failed.
     */
    public MavenDownloader(boolean snapshot, int attemps) {
        log.trace("Creating Maven Downloader. Attempt number: {}", attemps);
        this.snapshot = snapshot;

        boolean offlineFlag = false;
        int requestTimeOut = 60000; // 1 minute
        try {
            log.trace("Starting connection test.");
            //noinspection ResultOfMethodCallIgnored
            InetAddress.getByName("illarion.org");
        } catch (IOException e) {
            log.warn("No internet connection. Activating offline mode.");
            offlineFlag = true;
        }
        offline = offlineFlag;
        log.debug("Setting offline flag: {}", offlineFlag);

        ServiceLocator serviceLocator = setupServiceLocator();

        RepositorySystem system = serviceLocator.getService(RepositorySystem.class);
        if (system == null) {
            throw new IllegalStateException("Failed to init repository system.");
        }
        this.system = system;
        session = MavenRepositorySystemUtils.newSession();

        repositoryListener = new MavenRepositoryListener();

        session.setTransferListener(new MavenTransferListener());
        session.setRepositoryListener(repositoryListener);
        session.setConfigProperty(ConfigurationProperties.USER_AGENT, APPLICATION.getApplicationIdentifier());
        session.setConfigProperty(ConfigurationProperties.REQUEST_TIMEOUT, requestTimeOut);
        session.setUpdatePolicy(UPDATE_POLICY_ALWAYS);
        session.setChecksumPolicy(CHECKSUM_POLICY_FAIL);

        log.info("Used request timeout: {}ms", requestTimeOut);

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
    public Collection<File> downloadArtifact(
            @Nonnull String groupId, @Nonnull String artifactId, @Nullable MavenDownloaderCallback callback)
            throws DependencyCollectionException, InterruptedException, ExecutionException {
        Artifact artifact = new DefaultArtifact(groupId, artifactId, "jar", "[1,]");
        try {
            if (callback != null) {
                callback.reportNewState(State.SearchingNewVersion, null, offline, null);
            }
            VersionRangeRequest request = new VersionRangeRequest();
            request.setArtifact(artifact);
            request.setRepositories(Collections.singletonList(illarionRepository));
            request.setRequestContext(RUNTIME);
            VersionRangeResult result = system.resolveVersionRange(session, request);
            NavigableSet<String> versions = new TreeSet<>(new MavenVersionComparator());
            result.getVersions().stream().filter(version -> snapshot || !version.toString().contains("SNAPSHOT")).forEach(version -> {
                log.info("Found {}:{}:jar:{}", groupId, artifactId, version);
                versions.add(version.toString());
            });

            if (!versions.isEmpty()) {
                artifact = new DefaultArtifact(groupId, artifactId, "jar", versions.pollLast());
            }
        } catch (VersionRangeResolutionException ignored) {
        }

        if (callback != null) {
            callback.reportNewState(ResolvingDependencies, null, offline, null);
            repositoryListener.setCallback(callback);
            repositoryListener.setOffline(offline);
        }
        Dependency dependency = new Dependency(artifact, RUNTIME, false);

        List<String> usedScopes = Arrays.asList(COMPILE, RUNTIME, SYSTEM);
        DependencySelector selector = new AndDependencySelector(new OptionalDependencySelector(),
                                                                new ScopeDependencySelector(usedScopes, null));
        session.setDependencySelector(
                selector.deriveChildSelector(new DefaultDependencyCollectionContext(session, dependency)));

        DependencyFilter filter = DependencyFilterUtils.classpathFilter(RUNTIME, COMPILE);

        try {
            CollectRequest collectRequest = new CollectRequest();
            collectRequest.setRoot(dependency);
            collectRequest.setRepositories(repositories);
            CollectResult collectResult = system.collectDependencies(session, collectRequest);

            ProgressMonitor progressMonitor = new ProgressMonitor();

            ArtifactRequestTracer tracer = (monitor, artifact1, totalSize, transferred) -> {
                if (totalSize >= 0) {
                    monitor.setProgress(transferred / (float) totalSize);
                }
                if (callback != null) {
                    callback.reportNewState(ResolvingArtifacts, progressMonitor, offline,
                            humanReadableByteCount(transferred, true) + ' ' + artifact1);
                }
            };

            ArtifactRequestBuilder builder = new ArtifactRequestBuilder(system, session, tracer);
            DependencyVisitor visitor = new FilteringDependencyVisitor(builder, filter);
            visitor = new TreeDependencyVisitor(visitor);
            collectResult.getRoot().accept(visitor);

            List<FutureArtifactRequest> requests = builder.getRequests();

            if (callback != null) {
                for (@Nonnull FutureArtifactRequest request : requests) {
                    progressMonitor.addChild(request.getProgressMonitor());
                }
            }
            if (callback != null) {
                callback.reportNewState(ResolvingArtifacts, progressMonitor, offline, null);
            }

            ExecutorService executorService = Executors.newSingleThreadExecutor(
                    new ThreadFactoryBuilder()
                            .setDaemon(false)
                            .setNameFormat("Download Thread")
                            .build()
            );
            List<Future<ArtifactResult>> results = executorService.invokeAll(requests);
            executorService.shutdown();
            while (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                log.info("Downloading is not done yet.");
            }

            Collection<File> result = new ArrayList<>();
            for (@Nonnull Future<ArtifactResult> artifactResult : results) {
                result.add(artifactResult.get().getArtifact().getFile());
            }

            if (result.isEmpty()) {
                if (callback != null) {
                    callback.resolvingDone(Collections.<File>emptyList());
                }
                return null;
            }
            if (callback != null) {
                callback.resolvingDone(result);
            }
            return result;
        } catch (@Nonnull DependencyCollectionException | InterruptedException | ExecutionException e) {
            if (callback != null) {
                callback.resolvingFailed(e);
            }
            throw e;
        }
    }

    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (StrictMath.log(bytes) / StrictMath.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / StrictMath.pow(unit, exp), pre);
    }

    private void setupRepositories() {
        repositories.add(setupRepository("central", "http://repo1.maven.org/maven2/", false,
                setupRepository("ibiblio.org", "http://mirrors.ibiblio.org/maven2/", false),
                setupRepository("antelink", ".com/content/repositories/central/", false),
                setupRepository("exist", "http://repo.exist.com/maven2/", false),
                setupRepository("ibiblio.net", "http://www.ibiblio.net/pub/packages/maven2/", false),
                setupRepository("central-uk", "http://uk.maven.org/maven2/", false)));

        illarionRepository = setupRepository("illarion", "http://illarion.org/media/java/maven", snapshot);
        repositories.add(illarionRepository);
        repositories.add(setupRepository("oss-sonatype", "http://oss.sonatype.org/content/repositories/releases/",
                false));

        session.setOffline(offline);

        Path localDir = DirectoryManager.getInstance().getDirectory(Directory.Data);
        LocalRepository localRepo = new LocalRepository(localDir.toFile());
        LocalRepositoryManager manager = system.newLocalRepositoryManager(session, localRepo);
        session.setLocalRepositoryManager(manager);
    }

    @Nonnull
    private static RemoteRepository setupRepository(
            @Nonnull String id, @Nonnull String url, boolean enableSnapshots, @Nonnull RemoteRepository... mirrors) {
        Builder repo = new Builder(id, "default", url);
        if (enableSnapshots) {
            repo.setSnapshotPolicy(new RepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_FAIL));
        } else {
            repo.setSnapshotPolicy(new RepositoryPolicy(false, UPDATE_POLICY_NEVER, CHECKSUM_POLICY_FAIL));
        }
        repo.setReleasePolicy(new RepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_FAIL));

        for (RemoteRepository mirror : mirrors) {
            repo.addMirroredRepository(mirror);
        }

        return repo.build();
    }

    @Nonnull
    private static ServiceLocator setupServiceLocator() {
        DefaultServiceLocator serviceLocator = MavenRepositorySystemUtils.newServiceLocator();

        serviceLocator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        serviceLocator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        serviceLocator.setServices(ModelBuilder.class, new DefaultModelBuilderFactory().newInstance());
        return serviceLocator;
    }

}
