package illarion.download.maven;

import illarion.common.config.Config;
import illarion.common.util.DirectoryManager;
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
     * The configuration that is required to setup the downloader properly.
     */
    @Nonnull
    private final Config cfg;

    /**
     * The service locator used to link in the required services.
     */
    @Nonnull
    private final DefaultServiceLocator serviceLocator;

    @Nonnull
    private final RepositorySystem system;

    @Nonnull
    private final DefaultRepositorySystemSession session;

    /**
     * Create a new instance of the downloader along with the reference to the configuration utility that is used to
     * apply the settings to the downloader.
     *
     * @param cfg
     */
    public MavenDownloader(@Nonnull final Config cfg) {
        this.cfg = cfg;

        serviceLocator = new DefaultServiceLocator();
        setupServiceLocator();

        system = serviceLocator.getService(RepositorySystem.class);
        session = new DefaultRepositorySystemSession();

        repositories = new ArrayList<RemoteRepository>();
        setupRepositories();
    }

    @Nullable
    public Collection<File> downloadArtifact(@Nonnull final String groupId, @Nonnull final String artifactId) {
        final Artifact artifact = new DefaultArtifact(groupId + ':' + artifactId + ":jar:[1,]");
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
            long time = System.currentTimeMillis();
            final CollectResult collectResult = system.collectDependencies(session, collectRequest);

            System.out.println("Collecting dependencies took " + (System.currentTimeMillis() - time) + "ms");
            time = System.currentTimeMillis();

            final ArtifactRequestBuilder builder = new ArtifactRequestBuilder(null, system, session);
            DependencyVisitor visitor = new FilteringDependencyVisitor(builder, filter);
            visitor = new TreeDependencyVisitor(visitor);
            collectResult.getRoot().accept(visitor);

            System.out.println("Cleaning dependencies took " + (System.currentTimeMillis() - time) + "ms");
            time = System.currentTimeMillis();

            final List<FutureArtifactRequest> requests = builder.getRequests();

            final ExecutorService executorService = Executors.newFixedThreadPool(4);
            final List<Future<ArtifactResult>> results = executorService.invokeAll(requests);
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);

            System.out.println("Resolving artifacts took " + (System.currentTimeMillis() - time) + "ms");

            final List<File> result = new ArrayList<File>();
            for (@Nonnull final Future<ArtifactResult> artifactResult : results) {
                result.add(artifactResult.get().getArtifact().getFile());
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

    private void resolveArtifact(@Nonnull final DependencyNode node) throws ArtifactResolutionException {
        if (node.getArtifact().getFile() == null) {
            final ArtifactRequest artifactRequest = new ArtifactRequest(node);
            final ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);

            System.out.println( artifactResult.getArtifact() + " resolved to " + artifactResult.getArtifact().getFile() );
        }
    }

    private void setupRepositories() {
        repositories.add(setupRepository("central", "http://repo1.maven.org/maven2/", true));
        repositories.add(
                setupRepository("nifty-gui", "http://nifty-gui.sourceforge.net/nifty-maven-repo", true));
        repositories.add(setupRepository("illarion", "http://illarion.org/media/java/maven",
                cfg.getBoolean("snapshots")));
        repositories.add(setupRepository("libgdx", "http://libgdx.badlogicgames.com/nightlies/maven", true));

        LocalRepository localRepo = new LocalRepository(DirectoryManager.getInstance().getDataDirectory());
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
