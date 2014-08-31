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
package illarion.download.launcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This iterable implementation supplies the most common locations to store the Java executable on all systems.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractJavaExecutableIterable implements Iterable<Path> {
    /**
     * The iterator implementation for the that iterates over the data supplies by the java executable iterable.
     */
    protected abstract static class AbstractJavaExecutableIterator implements Iterator<Path> {
        /**
         * The original data source.
         */
        @Nonnull
        private final AbstractJavaExecutableIterable source;

        /**
         * The current index.
         */
        private int currentIndex = -1;

        /**
         * Create a new instance of this iterator.
         *
         * @param source the data source of this iterator
         */
        public AbstractJavaExecutableIterator(@Nonnull AbstractJavaExecutableIterable source) {
            this.source = source;
        }

        @Override
        public boolean hasNext() {
            return currentIndex < ((source.getUsedJreRootDir() == null) ? 1 : 2);
        }

        @Override
        @Nonnull
        public Path next() {
            Path result = source.getPath(++currentIndex);
            if (result == null) {
                throw new NoSuchElementException();
            }
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Nullable
    private final String usedJreRootDir;

    protected AbstractJavaExecutableIterable() {
        usedJreRootDir = System.getProperty("org.illarion.jre");
    }

    /**
     * The the path assigned to the index.
     *
     * @param index the index
     * @return the path
     */
    @Nullable
    private Path getPath(int index) {
        int usedIndex = index;
        if (usedJreRootDir != null) {
            if (usedIndex == 0) {
                return Paths.get(usedJreRootDir, "bin", "java");
            } else {
                usedIndex -= 1;
            }
        }
        switch (usedIndex) {
            case 0:
                return Paths.get("java");
            case 1:
                return Paths.get(System.getProperty("java.home"), "bin", "java");
            default:
                return null;
        }
    }

    @Nullable
    String getUsedJreRootDir() {
        return usedJreRootDir;
    }
}
