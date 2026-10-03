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

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("ALL")
public class VersionComparatorTest {
    private VersionComparator comparator;
    private Path temporaryRoot;

    @BeforeClass
    public void setUp() throws IOException {
        temporaryRoot = Files.createTempDirectory("illarion-version-test");
        comparator = new VersionComparator();
    }

    @Test
    public void compareDirectoriesReleases() {
        assert comparator != null;

        Path firstDir = testPath("bin", "org", "illarion", "download", "2.1.1.0");
        Path secondDir = testPath("bin", "org", "illarion", "download", "2.1.1.1");

        preparePath(firstDir, true);
        preparePath(secondDir, true);

        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result < 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 > 0);
    }

    @Test
    public void compareDirectoriesReleasesMatch() {
        assert comparator != null;

        Path firstDir = testPath("bin", "org", "illarion", "download", "2.1.1.1");
        Path secondDir = testPath("bin", "org", "illarion", "download", "2.1.1.1");

        preparePath(firstDir, true);
        preparePath(secondDir, true);

        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result == 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 == 0);
    }

    @Test
    public void compareDirectoriesSnapshots() {
        assert comparator != null;

        Path firstDir = testPath("bin", "org", "illarion", "download", "2.1.1.0-SNAPSHOT");
        Path secondDir = testPath("bin", "org", "illarion", "download", "2.1.1.1-SNAPSHOT");

        preparePath(firstDir, true);
        preparePath(secondDir, true);

        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result < 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 > 0);
    }

    @Test
    public void compareDirectoriesSnapshotsMatch() {
        assert comparator != null;

        Path firstDir = testPath("bin", "org", "illarion", "download", "2.1.1.1-SNAPSHOT");
        Path secondDir = testPath("bin", "org", "illarion", "download", "2.1.1.1-SNAPSHOT");

        preparePath(firstDir, true);
        preparePath(secondDir, true);

        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result == 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 == 0);
    }

    @Test
    public void compareDirectoriesReleaseSnapshot1() {
        assert comparator != null;

        Path firstDir = testPath("bin", "org", "illarion", "download", "2.1.1.0-SNAPSHOT");
        Path secondDir = testPath("bin", "org", "illarion", "download", "2.1.1.1");

        preparePath(firstDir, true);
        preparePath(secondDir, true);

        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result < 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 > 0);
    }

    @Test
    public void compareDirectoriesReleaseSnapshot2() {
        assert comparator != null;

        Path firstDir = testPath("bin", "org", "illarion", "download", "2.1.1.0");
        Path secondDir = testPath("bin", "org", "illarion", "download", "2.1.1.1-SNAPSHOT");

        preparePath(firstDir, true);
        preparePath(secondDir, true);

        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result < 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 > 0);
    }

    @Test
    public void compareDirectoriesReleaseSnapshot3() {
        assert comparator != null;

        Path firstDir = testPath("bin", "org", "illarion", "download", "2.1.1.1");
        Path secondDir = testPath("bin", "org", "illarion", "download", "2.1.1.1-SNAPSHOT");

        preparePath(firstDir, true);
        preparePath(secondDir, true);

        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result < 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 > 0);
    }

    @Test
    public void compareDirectoryFile() {
        assert comparator != null;

        Path firstFile = testPath("bin", "org", "illarion", "download", "2.1.1.1", "download-2.1.1.1.jar");
        Path secondDir = testPath("bin", "org", "illarion", "download", "2.1.1.1");

        preparePath(firstFile, false);
        preparePath(secondDir, true);

        int result = comparator.compare(firstFile, secondDir);
        Assert.assertTrue(result < 0);

        int result2 = comparator.compare(secondDir, firstFile);
        Assert.assertTrue(result2 > 0);
    }

    @Test
    public void compareFilesReleases() {
        assert comparator != null;

        Path firstFile = testPath("bin", "org", "illarion", "download", "2.1.1.1", "download-2.1.1.1.jar");
        Path secondFile = testPath("bin", "org", "illarion", "download", "2.1.1.0", "download-2.1.1.0.jar");

        preparePath(firstFile, false);
        preparePath(secondFile, false);

        int result = comparator.compare(firstFile, secondFile);
        Assert.assertTrue(result > 0);

        int result2 = comparator.compare(secondFile, firstFile);
        Assert.assertTrue(result2 < 0);
    }

    @Test
    public void compareFilesReleasesMatch() {
        assert comparator != null;

        Path firstFile = testPath("bin", "org", "illarion", "download", "2.1.1.1", "download-2.1.1.1.jar");
        Path secondFile = testPath("bin", "org", "illarion", "download", "2.1.1.1", "download-2.1.1.1.jar");

        preparePath(firstFile, false);
        preparePath(secondFile, false);

        int result = comparator.compare(firstFile, secondFile);
        Assert.assertTrue(result == 0);

        int result2 = comparator.compare(secondFile, firstFile);
        Assert.assertTrue(result2 == 0);
    }

    @Test
    public void compareFilesSnapshots1() {
        assert comparator != null;

        Path firstFile = testPath("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-SNAPSHOT.jar");
        Path secondFile = testPath("bin", "org", "illarion", "download",
                "2.1.1.0-SNAPSHOT", "download-2.1.1.0-SNAPSHOT.jar");

        preparePath(firstFile, false);
        preparePath(secondFile, false);

        int result = comparator.compare(firstFile, secondFile);
        Assert.assertTrue(result > 0);

        int result2 = comparator.compare(secondFile, firstFile);
        Assert.assertTrue(result2 < 0);
    }

    @Test
    public void compareFilesSnapshots2() {
        assert comparator != null;

        Path firstFile = testPath("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-SNAPSHOT.jar");
        Path secondFile = testPath("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-20150607.174327-14.jar");

        preparePath(firstFile, false);
        preparePath(secondFile, false);

        int result = comparator.compare(firstFile, secondFile);
        Assert.assertTrue(result > 0);

        int result2 = comparator.compare(secondFile, firstFile);
        Assert.assertTrue(result2 < 0);
    }

    @Test
    public void compareFilesSnapshots3() {
        assert comparator != null;

        Path firstFile = testPath("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-20150610-205023-15.jar");
        Path secondFile = testPath("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-20150607.174327-14.jar");

        preparePath(firstFile, false);
        preparePath(secondFile, false);

        int result = comparator.compare(firstFile, secondFile);
        Assert.assertTrue(result > 0);

        int result2 = comparator.compare(secondFile, firstFile);
        Assert.assertTrue(result2 < 0);
    }

    @Test
    public void compareFilesSnapshotsMatch() {
        assert comparator != null;

        Path firstFile = testPath("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-20150607.174327-14.jar");
        Path secondFile = testPath("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-20150607.174327-14.jar");

        preparePath(firstFile, false);
        preparePath(secondFile, false);

        int result = comparator.compare(firstFile, secondFile);
        Assert.assertTrue(result == 0);

        int result2 = comparator.compare(secondFile, firstFile);
        Assert.assertTrue(result2 == 0);
    }

    private Path testPath(String first, String... more) {
        return temporaryRoot.resolve(Paths.get(first, more));
    }

    private void preparePath(Path path, boolean directory) {
        try {
            if (directory) {
                Files.createDirectories(path);
            } else {
                Files.createDirectories(path.getParent());

                if (!Files.exists(path)) {
                    Files.createFile(path);
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @AfterClass
    public void cleanUp() throws IOException {
        try (Stream<Path> paths = Files.walk(temporaryRoot)) {
            for (Path path : (Iterable<Path>) paths.sorted(Comparator.reverseOrder())::iterator) {
                Files.delete(path);
            }
        }
    }
}
