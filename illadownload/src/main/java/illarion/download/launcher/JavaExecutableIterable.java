package illarion.download.launcher;

import java.nio.file.Path;
import java.util.Iterator;

/**
 * This is the default implementation for the java executable iterator. This one does not add any additional paths
 * to the default ones. It will suffice for the most system.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class JavaExecutableIterable extends AbstractJavaExecutableIterable {
    @Override
    public Iterator<Path> iterator() {
        return new AbstractJavaExecutableIterator(this) {
        };
    }
}
