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
