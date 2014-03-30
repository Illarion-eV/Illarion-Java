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
package illarion.common.util;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * This class is able to detect the kind of environment the application is running it.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class EnvironmentDetect {
    /**
     * This value is set {@code true} in case java is executed using javaw.
     */
    private static final boolean javaw;

    /**
     * This value is set {@code true} in case java is executed using java
     */
    private static final boolean webstart;

    static {
        boolean localJavaw = true;
        try {
            System.in.available();
            localJavaw = false;
        } catch (@Nonnull final IOException ignored) {
        }
        javaw = localJavaw;

        webstart = System.getProperty("javawebstart.version") != null;
    }

    /**
     * Check if the application is launched using the {@code java} executable.
     * <p/>
     * It means that a console is likely to be available.
     *
     * @return {@code true} in case the application was launched using the {@code java} executable
     */
    public static boolean isJavaExecutable() {
        return !javaw && !webstart;
    }

    /**
     * Check if the application is launched using the {@code javaw} executable.
     * <p/>
     * It means that a console is likely to be <b>not</b> available.
     *
     * @return {@code true} in case the application was launched using the {@code javaw} executable
     */
    public static boolean isJavawExecutable() {
        return javaw && !webstart;
    }

    /**
     * Check if the application was launched from webstart.
     *
     * @return {@code true} if the application was launched from webstart
     */
    public static boolean isWebstart() {
        return webstart;
    }
}
