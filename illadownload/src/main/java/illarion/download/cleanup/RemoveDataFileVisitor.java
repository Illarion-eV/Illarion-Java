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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class RemoveDataFileVisitor implements FileVisitor<Path> {
    @Nonnull
    private final Deque<Boolean> removeDirectory;
    @Nullable
    private final Filter<Path> filter;
    @Nonnull
    private final List<Path> resultList;

    public RemoveDataFileVisitor(@Nullable Filter<Path> filter) {
        this.filter = filter;
        removeDirectory = new LinkedList<>();
        resultList = new LinkedList<>();
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        removeDirectory.push(Boolean.TRUE);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if ((filter == null) || filter.accept(file)) {
            resultList.add(file);
        } else {
            removeDirectory.pop();
            removeDirectory.push(Boolean.FALSE);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Boolean deleteDir = removeDirectory.pop();
        if (Boolean.TRUE.equals(deleteDir)) {
            resultList.add(dir);
        } else if (Boolean.FALSE.equals(deleteDir) && !removeDirectory.isEmpty()) {
            /* Skip also the next directory up. */
            removeDirectory.pop();
            removeDirectory.push(Boolean.FALSE);
        }
        return FileVisitResult.CONTINUE;
    }

    @Nonnull
    public List<Path> getResultList() {
        return Collections.unmodifiableList(resultList);
    }
}
