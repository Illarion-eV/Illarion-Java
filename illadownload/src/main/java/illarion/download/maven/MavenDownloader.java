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

import illarion.common.util.AppIdent;
import illarion.common.util.DirectoryManager;
import illarion.common.util.ProgressMonitor;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.repository.internal.*;
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
    private static final AppIdent APPLICATION = new AppIdent("Illarion Launcher");
    private static final Logger log = LoggerFactory.getLogger(MavenDownloader.class);

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
     * This value is set in case the application is running in offline mode.
     */
    private final boolean offline;

    /**
     * The logger that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenDownloader.class);

    private final MavenRepositoryListener repositoryListener;

    /**
     * Create a new instance of the downloader along with the information if its supposed to download snapshot
     * versions of the main application.
     *
     * @param snapshot {@code true} in case the downloader is supposed to use snapshot versions of the main application
     * @param attemps the indicator how many times downloading was already tried and failed.
     */
    public MavenDownloader(boolean snapshot, int attemps) {
        this.snapshot = snapshot;

        boolean offlineFlag = false;
        int requestTimeOut = 180000; // 3 Minutes
        try {
            InetAddress inet = InetAddress.getByName("illarion.org");
            while (requestTimeOut > 20) {
                if (inet.isReachable(requestTimeOut)) {
                    requestTimeOut /= 2;
                } else {
                    break;
                }
            }
            for (int i = 0; i < attemps; i++) {
                requestTimeOut *= 3;
            }
        } catch (IOException e) {
            LOGGER.warn("No internet connection. Activating offline mode.");
            offlineFlag = true;
        }
        offline = offlineFlag;

        serviceLocator = new DefaultServiceLocator();
        setupServiceLocator();

        system = serviceLocator.getService(RepositorySystem.class);
        session = new DefaultRepositorySystemSession();

        repositoryListener = new MavenRepositoryListener();

        session.setTransferListener(new MavenTransferListener());
        session.setRepositoryListener(repositoryListener);
        session.setConfigProperty(ConfigurationProperties.USER_AGENT, APPLICATION.getApplicationIdentifier());
        session.setConfigProperty(ConfigurationProperties.REQUEST_TIMEOUT, requestTimeOut);

        if (requestTimeOut > 30000) {
            LOGGER.warn("Used request timeout level is very high: {}ms", requestTimeOut);
        } else {
            LOGGER.info("Used request timeout: {}ms", requestTimeOut);
        }

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
            @Nonnull String groupId, @Nonnull String artifactId, @Nullable final MavenDownloaderCallback callback)
            throws DependencyCollectionException, InterruptedException, ExecutionException {
        Artifact artifact = new DefaultArtifact(groupId, artifactId, "jar", "[1,]");
        try {
            if (callback != null) {
                callback.reportNewState(MavenDownloaderCallback.State.SearchingNewVersion, null, offline, null);
            }
            VersionRangeResult result = system
                    .resolveVersionRange(session, new VersionRangeRequest(artifact, repositories, RUNTIME));
            NavigableSet<String> versions = new TreeSet<>(new Comparator<String>() {
                @Override
                public int compare(@Nonnull String o1, @Nonnull String o2) {
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

                public int compareEntry(@Nonnull String e1, @Nonnull String e2) {
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
            for (Version version : result.getVersions()) {
                if (snapshot || !version.toString().contains("SNAPSHOT")) {
                    log.info("Found {}:{}:jar:{}", groupId, artifactId, version);
                    versions.add(version.toString());
                }
            }

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
                selector.deriveChildSelector(new DefaultDependencyCollectionContext(session, artifact, dependency)));

        DependencyFilter filter = DependencyFilterUtils.classpathFilter(RUNTIME, COMPILE);

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(dependency);
        collectRequest.setRepositories(repositories);

        try {
            CollectResult collectResult = system.collectDependencies(session, collectRequest);

            final ProgressMonitor progressMonitor = new ProgressMonitor();

            ArtifactRequestBuilder builder = new ArtifactRequestBuilder(null, system, session,
                                                                        new ArtifactRequestTracer() {
                                                                            @Override
                                                                            public void trace(
                                                                                    @Nonnull ProgressMonitor monitor,
                                                                                    @Nonnull String artifact,
                                                                                    long totalSize,
                                                                                    long transferred) {
                                                                                if (totalSize >= 0) {
                                                                                    monitor.setProgress(transferred /
                                                                                                                (float) totalSize);
                                                                                }
                                                                                if (callback != null) {
                                                                                    callback.reportNewState(
                                                                                            ResolvingArtifacts,
                                                                                            progressMonitor, offline,
                                                                                            humanReadableByteCount(
                                                                                                    transferred, true) +
                                                                                                    ' ' + artifact);
                                                                                }
                                                                            }
                                                                        });
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

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            List<Future<ArtifactResult>> results = executorService.invokeAll(requests);
            executorService.shutdown();
            while (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                LOGGER.info("Downloading is not done yet.");
            }

            List<File> result = new ArrayList<>();
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
        } catch (DependencyCollectionException | InterruptedException | ExecutionException e) {
            if (callback != null) {
                callback.resolvingFailed(e);
            }
            throw e;
        }
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (StrictMath.log(bytes) / StrictMath.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / StrictMath.pow(unit, exp), pre);
    }

    private void setupRepositories() {
        if (!offline) {
            repositories.add(setupRepository("central", "http://repo1.maven.org/maven2/", false,
                                             setupRepository("ibiblio.org", "http://mirrors.ibiblio.org/maven2/",
                                                             false), setupRepository("antelink",
                                                                                     "http://maven.antelink.com/content/repositories/central/",
                                                                                     false),
                                             setupRepository("exist", "http://repo.exist.com/maven2/", false),
                                             setupRepository("ibiblio.net",
                                                             "http://www.ibiblio.net/pub/packages/maven2/", false),
                                             setupRepository("central-uk", "http://uk.maven.org/maven2/", false)));
            repositories.add(setupRepository("illarion", "http://illarion.org/media/java/maven", snapshot));
            repositories.add(setupRepository("oss-sonatype", "http://oss.sonatype.org/content/repositories/releases/",
                                             false));
        }

        Path localDir = DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.Data);
        LocalRepository localRepo = new LocalRepository(localDir.toFile());
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
    }

    @Nonnull
    private static RemoteRepository setupRepository(
            @Nonnull String id, @Nonnull String url, boolean enableSnapshots, @Nonnull RemoteRepository... mirrors) {
        RemoteRepository.Builder repo = new RemoteRepository.Builder(id, "default", url);
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
