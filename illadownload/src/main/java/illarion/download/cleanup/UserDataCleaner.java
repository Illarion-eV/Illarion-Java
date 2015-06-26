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
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * This class takes care for cleaning the user directory.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class UserDataCleaner implements Callable<Void> {
    /**
     * The logger that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(UserDataCleaner.class);

    @Override
    public Void call() throws Exception {
        log.info("Starting cleanup of user directory.");
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
    private static Collection<Path> getRemovalTargets() throws IOException {
        DirectoryManager dm = DirectoryManager.getInstance();

        Filter<Path> userDirFilter = new UserDirectoryFilenameFilter();

        Path userDir = dm.getDirectory(Directory.User);
        return Cleaner.enlistRecursively(userDir, userDirFilter);
    }
}
