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
package illarion.download.cleanup;

import illarion.common.util.DirectoryManager;
import illarion.common.util.DirectoryManager.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * This cleanup function is the one that takes care for the artifact repository and keeps things tidy there.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class ArtifactCleaner implements Callable<Void> {
    /**
     * The logger that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(ArtifactCleaner.class);
    @Nonnull
    private final Comparator<Path> versionComparator = new VersionComparator();

    @Override
    public Void call() throws Exception {
        log.info("Starting cleanup of artifact directory.");
        Collection<Path> removalTargets = getRemovalTargets();
        Cleaner.printFileList(removalTargets);

        /* And remove the stuff. */
        for (Path file : removalTargets) {
            Files.delete(file);
        }
        log.info("Removed {} files.", removalTargets.size());
        return null;
    }

    /**
     * This function creates a list of all files to be removed.
     *
     * @return the files that should be removed
     */
    @Nonnull
    private Collection<Path> getRemovalTargets() throws IOException {
        DirectoryManager dm = DirectoryManager.getInstance();

        Path dataDir = dm.getDirectory(Directory.Data);

        Collection<Path> artifactDirectories = getArtifactDirectories(dataDir);

        Collection<Path> resultList = new LinkedList<>();
        for (Path artifactDir : artifactDirectories) {
            resultList.addAll(getArtifactRemovalTargets(artifactDir));
        }

        return Collections.unmodifiableCollection(resultList);
    }

    @Nonnull
    private static Collection<Path> getArtifactDirectories(@Nonnull Path rootDir) throws IOException {
        ArtifactDirectoryVisitor visitor = new ArtifactDirectoryVisitor();
        Files.walkFileTree(rootDir, visitor);
        return visitor.getArtifactDirectories();
    }

    private static boolean isArtifactDirectory(@Nonnull Path dir) throws IOException {
        //noinspection ConstantConditions
        return Files.list(dir).filter(Files::isRegularFile).allMatch(path ->
                path.getFileName().toString().endsWith(".jar"));
    }

    @Nonnull
    private Collection<Path> getArtifactRemovalTargets(@Nonnull Path artifactDirectory) throws IOException {
        List<Path> releaseVersions = new LinkedList<>();
        List<Path> snapshotVersions = new LinkedList<>();

        /* Group the files into two lists. One for the release versions and one for the snapshot versions. */
        Files.list(artifactDirectory).filter(Files::isDirectory).forEach(path -> {
            int nameCount = path.getNameCount();
            Path lastSegment = path.getName(nameCount - 1);
            if (lastSegment.toString().contains("SNAPSHOT")) {
                snapshotVersions.add(path);
            } else {
                releaseVersions.add(path);
            }
        });

        Collection<Path> result = new LinkedList<>();
        /*
        The next thing is to remove all but the newest version of each artifact, both for release and snapshot
        versions. The snapshot may contain multiple versions in addition. We take care for this later on.
         */
        for (List<Path> versionList : Arrays.asList(releaseVersions, snapshotVersions)) {
            /* Sort the versions. The newest version will be at the end of the list. */
            Collections.sort(versionList, versionComparator);

            while (versionList.size() > 1) {
                Path dir = versionList.remove(0);
                if ((dir != null) && isArtifactDirectory(dir)) {
                    result.addAll(Cleaner.enlistRecursively(dir, (path) -> true));
                }
            }
        }

        if (!snapshotVersions.isEmpty()) {
            //noinspection ConstantConditions
            result.addAll(getOldSnapshots(snapshotVersions.get(0)));
        }

        return result;
    }

    @Nonnull
    private static Collection<Path> getOldSnapshots(@Nonnull Path snapshotDir) throws IOException {
        List<Path> snapshotJars = Files.list(snapshotDir)
                .filter(Files::isRegularFile)
                .filter(path -> {
                    //noinspection ConstantConditions
                    String fileName = path.getFileName().toString();
                    return fileName.endsWith(".jar") && !fileName.contains("SNAPSHOT");
                })
                .collect(Collectors.toList());

        Collections.sort(snapshotJars);
        snapshotJars.remove(snapshotJars.size() - 1);

        if (snapshotJars.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<String> snapshotNames = snapshotJars.stream()
                .filter(path -> path != null)
                .map(path -> {
                    //noinspection ConstantConditions
                    return com.google.common.io.Files.getNameWithoutExtension(path.getFileName().toString());
                })
                .collect(Collectors.toList());

        return Files.list(snapshotDir).filter(Files::isRegularFile)
                .filter(path -> {
                    //noinspection ConstantConditions
                    String fileName = path.getFileName().toString();
                    return snapshotNames.stream().anyMatch(fileName::startsWith);
                }).collect(Collectors.toList());
    }
}
