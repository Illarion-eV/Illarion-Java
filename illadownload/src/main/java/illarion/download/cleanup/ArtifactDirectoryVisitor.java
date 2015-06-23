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
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * This is a file visitor designed to locate and record the artifact directories inside a repository.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class ArtifactDirectoryVisitor implements FileVisitor<Path> {
    @Nonnull
    private final Collection<Path> artifactDirectories;

    /**
     * This counter tracks how many directory levels up can be skipped to find the next artifact directory.
     * Each level reduces the counter by {@code 1}. {@code 0} means nothing is done. {@code 1} means this level is
     * the important one and needs to be recorded. The value that is active when the post visit function is called is
     * relevant.
     */
    private int dropOutCounter;

    ArtifactDirectoryVisitor() {
        artifactDirectories = new ArrayList<>();
    }

    @Nonnull
    Collection<Path> getArtifactDirectories() {
        return Collections.unmodifiableCollection(artifactDirectories);
    }

    @Override
    public FileVisitResult preVisitDirectory(@Nonnull Path dir, @Nonnull BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(@Nonnull Path file, @Nonnull BasicFileAttributes attrs) throws IOException {
        //noinspection ConstantConditions
        if (Files.isRegularFile(file) && file.getFileName().toString().endsWith(".jar")) {
            /* This is a jar file. Means two directories up is a artifact directory. */
            dropOutCounter = 2;
            return FileVisitResult.SKIP_SIBLINGS;
        }
        /* No jar file. Just check out the rest of the tree. */
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(@Nonnull Path file, @Nonnull IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(@Nonnull Path dir, @Nullable IOException exc) throws IOException {
        if (dropOutCounter > 1) {
            dropOutCounter -= 1;
            return FileVisitResult.SKIP_SIBLINGS;
        }
        if (dropOutCounter == 1) {
            /* This is the artifact directory. Store it! */
            artifactDirectories.add(dir);
            dropOutCounter = 0;
        }
        return FileVisitResult.CONTINUE;
    }
}
