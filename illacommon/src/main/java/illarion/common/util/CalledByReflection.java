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

import java.lang.annotation.*;

/**
 * This annotation is used to mark functions that are called by reflections. This is mainly used to tell proguard that
 * those functions may not be removed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(value = {ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface CalledByReflection {
}
