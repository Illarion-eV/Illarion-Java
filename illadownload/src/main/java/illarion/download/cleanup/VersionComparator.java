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
package illarion.download.cleanup;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class VersionComparator implements Comparator<Path>, Serializable {
    private static final Pattern VERSION_SPLIT_PATTERN = Pattern.compile("\\.");

    @Override
    public int compare(@Nonnull Path file1, @Nonnull Path file2) {
        String version1;
        String version2;
        if (Files.isDirectory(file1)) {
            if (Files.isDirectory(file2)) {
                version1 = file1.toString();
                version2 = file2.toString();
            } else {
                return 1;
            }
        } else {
            if (Files.isDirectory(file2)) {
                return -1;
            } else {
                version1 = file1.toString();
                version2 = file2.toString();
                version1 = version1.substring(version1.indexOf('-'));
                version2 = version2.substring(version2.indexOf('-'));
            }
        }

        boolean snapshot1;
        boolean snapshot2;
        if (version1.endsWith("-SNAPSHOT")) {
            snapshot1 = true;
            version1 = version1.replace("-SNAPSHOT", "");
        } else {
            snapshot1 = false;
        }
        if (version2.endsWith("-SNAPSHOT")) {
            snapshot2 = true;
            version2 = version2.replace("-SNAPSHOT", "");
        } else {
            snapshot2 = false;
        }

        String[] version1Parts = VERSION_SPLIT_PATTERN.split(version1);
        String[] version2Parts = VERSION_SPLIT_PATTERN.split(version2);

        int count = Math.min(version1Parts.length, version2Parts.length);
        for (int i = 0; i < count; i++) {
            try {
                int value1 = Integer.parseInt(version1Parts[i]);
                int value2 = Integer.parseInt(version2Parts[i]);
                int compareResult = Integer.compare(value1, value2);
                if (compareResult != 0) {
                    return compareResult;
                }
            } catch (NumberFormatException ignored) {
            }
            int compareResult = version1Parts[i].compareTo(version2Parts[i]);
            if (compareResult != 0) {
                return compareResult;
            }
        }

        if (snapshot1 && !snapshot2) {
            return 1;
        }
        if (!snapshot1 && snapshot2) {
            return -1;
        }
        return 0;
    }
}
