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
package illarion.download.maven;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class MavenVersionComparator implements Comparator<String> {
    @Nonnull
    private static final Pattern VERSION_SEP_PATTER = Pattern.compile("[.-]");

    @Override
    public int compare(@Nonnull String o1, @Nonnull String o2) {
        String[] versionParts1 = VERSION_SEP_PATTER.split(o1);
        String[] versionParts2 = VERSION_SEP_PATTER.split(o2);

        int count = Math.min(versionParts1.length, versionParts2.length);
        for (int i = 0; i < count; i++) {
            int cmpResult = compareEntry(versionParts1[i], versionParts2[i]);
            if (cmpResult != 0) {
                return cmpResult;
            }
        }

        return Integer.compare(versionParts1.length, versionParts2.length);
    }

    public int compareEntry(@Nonnull String e1, @Nonnull String e2) {
        if ("SNAPSHOT".equals(e1)) {
            if ("SNAPSHOT".equals(e2)) {
                return 0;
            }
            return -1;
        }
        if ("SNAPSHOT".equals(e2)) {
            return 1;
        }
        try {
            int version1 = Integer.parseInt(e1);
            int version2 = Integer.parseInt(e2);
            return Integer.compare(version1, version2);
        } catch (NumberFormatException e) {
            // illegal formatted number
        }
        return e1.compareTo(e2);
    }
}
