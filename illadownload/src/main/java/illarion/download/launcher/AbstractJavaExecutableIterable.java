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
    protected abstract class AbstractJavaExecutableIterator implements Iterator<Path> {
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
            return currentIndex < 2;
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

    /**
     * The the path assigned to the index.
     *
     * @param index the index
     * @return the path
     */
    @Nullable
    private Path getPath(final int index) {
        switch (index) {
            case 0:
                return Paths.get("java");
            case 1:
                return Paths.get(System.getProperty("java.home"), "bin", "java");
        }
        return null;
    }
}
