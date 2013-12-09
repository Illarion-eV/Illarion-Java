package illarion.download.cleanup;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Comparator;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class VersionComparator implements Comparator<File> {
    @Override
    public int compare(@Nonnull final File file1, @Nonnull final File file2) {
        String version1;
        String version2;
        if (file1.isDirectory()) {
            if (file2.isDirectory()) {
                version1 = file1.getName();
                version2 = file2.getName();
            } else {
                return 1;
            }
        } else {
            if (file2.isDirectory()) {
                return -1;
            } else {
                version1 = file1.getName();
                version2 = file2.getName();
                version1 = version1.substring(version1.indexOf('-'));
                version2 = version2.substring(version2.indexOf('-'));
            }
        }

        final boolean snapshot1;
        final boolean snapshot2;
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

        final String[] version1Parts = version1.split("\\.");
        final String[] version2Parts = version2.split("\\.");

        final int count = Math.min(version1Parts.length, version2Parts.length);
        for (int i = 0; i < count; i++) {
            try {
                final int value1 = Integer.valueOf(version1Parts[i]);
                final int value2 = Integer.valueOf(version2Parts[i]);
                final int compareResult = Integer.compare(value1, value2);
                if (compareResult != 0) {
                    return compareResult;
                }
            } catch (NumberFormatException ignored) {}
            final int compareResult = version1Parts[i].compareTo(version2Parts[i]);
            if (compareResult != 0) {
                return compareResult;
            }
        }

        if (snapshot1 && !snapshot2) {
            return 1;
        } else if (!snapshot1 && snapshot2) {
            return -1;
        }
        return 0;
    }
}
