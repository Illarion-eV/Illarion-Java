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

import org.easymock.TestSubject;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("ALL")
@PrepareForTest({Files.class, VersionComparator.class})
public class VersionComparatorTest {
    @TestSubject
    private VersionComparator comparator;

    @BeforeClass
    public void setUp() {
        comparator = new VersionComparator();
    }

    @Test
    public void compareDirectoriesReleases() {
        assert comparator != null;

        Path firstDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.0");
        Path secondDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.1");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstDir)).andReturn(true).anyTimes();
        expect(Files.isDirectory(secondDir)).andReturn(true).anyTimes();

        replayAll();
        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result < 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 > 0);
        verifyAll();
    }

    @Test
    public void compareDirectoriesReleasesMatch() {
        assert comparator != null;

        Path firstDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.1");
        Path secondDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.1");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstDir)).andReturn(true).anyTimes();
        expect(Files.isDirectory(secondDir)).andReturn(true).anyTimes();

        replayAll();
        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result == 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 == 0);
        verifyAll();
    }

    @Test
    public void compareDirectoriesSnapshots() {
        assert comparator != null;

        Path firstDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.0-SNAPSHOT");
        Path secondDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.1-SNAPSHOT");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstDir)).andReturn(true).anyTimes();
        expect(Files.isDirectory(secondDir)).andReturn(true).anyTimes();

        replayAll();
        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result < 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 > 0);
        verifyAll();
    }

    @Test
    public void compareDirectoriesSnapshotsMatch() {
        assert comparator != null;

        Path firstDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.1-SNAPSHOT");
        Path secondDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.1-SNAPSHOT");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstDir)).andReturn(true).anyTimes();
        expect(Files.isDirectory(secondDir)).andReturn(true).anyTimes();

        replayAll();
        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result == 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 == 0);
        verifyAll();
    }

    @Test
    public void compareDirectoriesReleaseSnapshot1() {
        assert comparator != null;

        Path firstDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.0-SNAPSHOT");
        Path secondDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.1");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstDir)).andReturn(true).anyTimes();
        expect(Files.isDirectory(secondDir)).andReturn(true).anyTimes();

        replayAll();
        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result < 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 > 0);
        verifyAll();
    }

    @Test
    public void compareDirectoriesReleaseSnapshot2() {
        assert comparator != null;

        Path firstDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.0");
        Path secondDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.1-SNAPSHOT");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstDir)).andReturn(true).anyTimes();
        expect(Files.isDirectory(secondDir)).andReturn(true).anyTimes();

        replayAll();
        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result < 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 > 0);
        verifyAll();
    }

    @Test
    public void compareDirectoriesReleaseSnapshot3() {
        assert comparator != null;

        Path firstDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.1");
        Path secondDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.1-SNAPSHOT");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstDir)).andReturn(true).anyTimes();
        expect(Files.isDirectory(secondDir)).andReturn(true).anyTimes();

        replayAll();
        int result = comparator.compare(firstDir, secondDir);
        Assert.assertTrue(result < 0);

        int result2 = comparator.compare(secondDir, firstDir);
        Assert.assertTrue(result2 > 0);
        verifyAll();
    }

    @Test
    public void compareDirectoryFile() {
        assert comparator != null;

        Path firstFile = Paths.get("bin", "org", "illarion", "download", "2.1.1.1", "download-2.1.1.1.jar");
        Path secondDir = Paths.get("bin", "org", "illarion", "download", "2.1.1.1");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstFile)).andReturn(false).anyTimes();
        expect(Files.isDirectory(secondDir)).andReturn(true).anyTimes();

        replayAll();
        int result = comparator.compare(firstFile, secondDir);
        Assert.assertTrue(result < 0);

        int result2 = comparator.compare(secondDir, firstFile);
        Assert.assertTrue(result2 > 0);
        verifyAll();
    }

    @Test
    public void compareFilesReleases() {
        assert comparator != null;

        Path firstFile = Paths.get("bin", "org", "illarion", "download", "2.1.1.1", "download-2.1.1.1.jar");
        Path secondFile = Paths.get("bin", "org", "illarion", "download", "2.1.1.0", "download-2.1.1.0.jar");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstFile)).andReturn(false).anyTimes();
        expect(Files.isDirectory(secondFile)).andReturn(false).anyTimes();

        replayAll();
        int result = comparator.compare(firstFile, secondFile);
        Assert.assertTrue(result > 0);

        int result2 = comparator.compare(secondFile, firstFile);
        Assert.assertTrue(result2 < 0);
        verifyAll();
    }

    @Test
    public void compareFilesReleasesMatch() {
        assert comparator != null;

        Path firstFile = Paths.get("bin", "org", "illarion", "download", "2.1.1.1", "download-2.1.1.1.jar");
        Path secondFile = Paths.get("bin", "org", "illarion", "download", "2.1.1.1", "download-2.1.1.1.jar");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstFile)).andReturn(false).anyTimes();
        expect(Files.isDirectory(secondFile)).andReturn(false).anyTimes();

        replayAll();
        int result = comparator.compare(firstFile, secondFile);
        Assert.assertTrue(result == 0);

        int result2 = comparator.compare(secondFile, firstFile);
        Assert.assertTrue(result2 == 0);
        verifyAll();
    }

    @Test
    public void compareFilesSnapshots1() {
        assert comparator != null;

        Path firstFile = Paths.get("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-SNAPSHOT.jar");
        Path secondFile = Paths.get("bin", "org", "illarion", "download",
                "2.1.1.0-SNAPSHOT", "download-2.1.1.0-SNAPSHOT.jar");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstFile)).andReturn(false).anyTimes();
        expect(Files.isDirectory(secondFile)).andReturn(false).anyTimes();

        replayAll();
        int result = comparator.compare(firstFile, secondFile);
        Assert.assertTrue(result > 0);

        int result2 = comparator.compare(secondFile, firstFile);
        Assert.assertTrue(result2 < 0);
        verifyAll();
    }

    @Test
    public void compareFilesSnapshots2() {
        assert comparator != null;

        Path firstFile = Paths.get("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-SNAPSHOT.jar");
        Path secondFile = Paths.get("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-20150607.174327-14.jar");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstFile)).andReturn(false).anyTimes();
        expect(Files.isDirectory(secondFile)).andReturn(false).anyTimes();

        replayAll();
        int result = comparator.compare(firstFile, secondFile);
        Assert.assertTrue(result > 0);

        int result2 = comparator.compare(secondFile, firstFile);
        Assert.assertTrue(result2 < 0);
        verifyAll();
    }

    @Test
    public void compareFilesSnapshots3() {
        assert comparator != null;

        Path firstFile = Paths.get("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-20150610-205023-15.jar");
        Path secondFile = Paths.get("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-20150607.174327-14.jar");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstFile)).andReturn(false).anyTimes();
        expect(Files.isDirectory(secondFile)).andReturn(false).anyTimes();

        replayAll();
        int result = comparator.compare(firstFile, secondFile);
        Assert.assertTrue(result > 0);

        int result2 = comparator.compare(secondFile, firstFile);
        Assert.assertTrue(result2 < 0);
        verifyAll();
    }

    @Test
    public void compareFilesSnapshotsMatch() {
        assert comparator != null;

        Path firstFile = Paths.get("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-20150607.174327-14.jar");
        Path secondFile = Paths.get("bin", "org", "illarion", "download",
                "2.1.1.1-SNAPSHOT", "download-2.1.1.1-20150607.174327-14.jar");

        mockStaticPartial(Files.class, "isDirectory", Path.class, LinkOption[].class);
        expect(Files.isDirectory(firstFile)).andReturn(false).anyTimes();
        expect(Files.isDirectory(secondFile)).andReturn(false).anyTimes();

        replayAll();
        int result = comparator.compare(firstFile, secondFile);
        Assert.assertTrue(result == 0);

        int result2 = comparator.compare(secondFile, firstFile);
        Assert.assertTrue(result2 == 0);
        verifyAll();
    }

    @BeforeMethod
    public void prepareTests() {
        resetAll();
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
