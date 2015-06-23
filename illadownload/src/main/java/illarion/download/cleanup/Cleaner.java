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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the cleaner implementation of the downloader. While the downloader in general only downloads stuff and
 * stores it on the drive, this class is tasked with the cleanup process. Its able to delete old artifacts or just
 * delete everything in its way.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Cleaner {
    /**
     * The logger that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(Cleaner.class);

    public void clean() {
        ExecutorService executorService = Executors.newFixedThreadPool(2,
                new ThreadFactoryBuilder()
                        .setDaemon(false)
                        .setNameFormat("Cleanup Thread-%d")
                        .build()
        );

        executorService.submit(new ArtifactCleaner());
        executorService.submit(new UserDataCleaner());

        executorService.shutdown();
    }

    @Nonnull
    static Collection<Path> enlistRecursively(@Nonnull Path rootDir,
                                              @Nullable Filter<Path> filter) throws IOException {
        if (Files.isDirectory(rootDir)) {
            RemoveDataFileVisitor visitor = new RemoveDataFileVisitor(filter);
            Files.walkFileTree(rootDir, visitor);
            return visitor.getResultList();
        }
        return Collections.emptyList();
    }

    static void printFileList(@Nonnull Collection<Path> files) throws IOException {
        long size = 0L;
        for (@Nonnull Path file : files) {
            size += Files.size(file);
            log.debug(file.toAbsolutePath().toString());
        }

        log.info("Files to delete: {} ({} Bytes)", files.size(), size);
    }
}
