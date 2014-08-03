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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This small utility class is used to detect the operating system and the architecture of the current system and
 * offer that information to the rest of the application.
 *
 * @author Martin Karing
 */
public final class OSDetection {
    /**
     * The internal constant for 32bit machines.
     */
    private static final int BIT32 = 0;

    /**
     * The internal constant for 64bit machines.
     */
    private static final int BIT64 = 1;

    /**
     * The singleton instance of this class.
     */
    private static final OSDetection INSTANCE = new OSDetection();

    /**
     * The internal constant for the operating system "Linux".
     */
    private static final int LINUX = 1;

    /**
     * The internal constant for the operating system "MacOS X".
     */
    private static final int MACOSX = 2;

    /**
     * The internal constant for the operating system "Sun OS".
     */
    private static final int SOLARIS = 3;

    /**
     * The internal constant for a unknown value.
     */
    private static final int UNKNOWN = -1;

    /**
     * The internal constant for the operating system "Windows".
     */
    private static final int WINDOWS = 0;

    /**
     * This value stores the architecture.
     */
    private final int arch;

    /**
     * This value stores the operating system.
     */
    private final int os;

    /**
     * The logger instance that takes care for the logger output of this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OSDetection.class);

    /**
     * The private constructor that generates the required values.
     */
    private OSDetection() {
        String osName = System.getProperty("os.name"); //$NON-NLS-1$
        if (osName.contains("Windows")) { //$NON-NLS-1$
            os = WINDOWS;
        } else if (osName.contains("Linux")) { //$NON-NLS-1$
            os = LINUX;
        } else if (osName.contains("SunOS")) { //$NON-NLS-1$
            os = SOLARIS;
        } else if (osName.contains("Mac OS X")) { //$NON-NLS-1$
            os = MACOSX;
        } else {
            LOGGER.error("OS-Detection failed for: {}", osName);
            os = UNKNOWN;
        }

        String archName = System.getProperty("os.arch"); //$NON-NLS-1$
        if (archName.contains("amd64") || archName.contains("x86_64")) { //$NON-NLS-1$
            arch = BIT64;
        } else if (archName.contains("i386") || archName.contains("i586") || archName.contains("i686") ||
                archName.contains("x86")) { //$NON-NLS-1$
            arch = BIT32;
        } else {
            LOGGER.error("Architecture-Detection failed for: {}", archName);
            arch = UNKNOWN;
        }
    }

    /**
     * Get the string identifier of the architecture of the host system.
     *
     * @return the string identifier of the architecture
     */
    @Nonnull
    public static String getArchValue() {
        switch (INSTANCE.arch) {
            case BIT32:
                return "i586"; //$NON-NLS-1$
            case BIT64:
                return "amd64"; //$NON-NLS-1$
            default:
                return "generic"; //$NON-NLS-1$
        }
    }

    /**
     * Get a string identifier of the operating system.
     *
     * @return the OS identifier of the operating system
     */
    @Nonnull
    public static String getOsValue() {
        switch (INSTANCE.os) {
            case WINDOWS:
                return "windows"; //$NON-NLS-1$
            case LINUX:
                return "linux"; //$NON-NLS-1$
            case MACOSX:
                return "macosx"; //$NON-NLS-1$
            case SOLARIS:
                return "solaris"; //$NON-NLS-1$
            default:
                return "generic"; //$NON-NLS-1$
        }
    }

    /**
     * Check if this operating system is a 32bit system.
     *
     * @return {@code true} in case the operating system is a 32bit system
     */
    public static boolean is32Bit() {
        return INSTANCE.arch == BIT32;
    }

    /**
     * Check if this operating system is a 64bit system.
     *
     * @return {@code true} in case the operating system is a 64bit system
     */
    public static boolean is64Bit() {
        return INSTANCE.arch == BIT64;
    }

    /**
     * Check if the architecture is unknown.
     *
     * @return {@code true} in case the architecture is unknown
     */
    public static boolean isArchUnknown() {
        return INSTANCE.arch == UNKNOWN;
    }

    /**
     * Check if this operating system is "Linux".
     *
     * @return {@code true} in case the operating system is "Linux"
     */
    public static boolean isLinux() {
        return INSTANCE.os == LINUX;
    }

    /**
     * Check if this operating system is "MacOS X".
     *
     * @return {@code true} in case the operating system is "MacOS X"
     */
    public static boolean isMacOSX() {
        return INSTANCE.os == MACOSX;
    }

    /**
     * Check if the operating system is unknown.
     *
     * @return {@code true} in case the operating system is unknown
     */
    public static boolean isOsUnknown() {
        return INSTANCE.os == UNKNOWN;
    }

    /**
     * Check if this operating system is "Sun OS".
     *
     * @return {@code true} in case the operating system is "Sun OS"
     */
    public static boolean isSolaris() {
        return INSTANCE.os == SOLARIS;
    }

    /**
     * Check if this operating system is "Windows".
     *
     * @return {@code true} in case the operating system is "Windows"
     */
    public static boolean isWindows() {
        return INSTANCE.os == WINDOWS;
    }
}
