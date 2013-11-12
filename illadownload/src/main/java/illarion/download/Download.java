package illarion.download;

import illarion.common.config.ConfigSystem;
import illarion.common.util.DirectoryManager;
import illarion.download.launcher.JavaLauncher;
import illarion.download.maven.MavenDownloader;

import java.io.File;
import java.util.Collection;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Download {
    public static void main(final String[] args) {
        final ConfigSystem cfg = new ConfigSystem(new File(DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.User),
                "download.xcfgz"));
        cfg.setDefault("snapshots", true);

        final MavenDownloader downloader = new MavenDownloader(cfg);

        final Collection<File> classpath = downloader.downloadArtifact("org.illarion", "client");

        if (classpath != null) {
            final JavaLauncher launcher = new JavaLauncher(cfg);
            launcher.launch(classpath, "illarion.client.IllaClient");
        }
    }
}
