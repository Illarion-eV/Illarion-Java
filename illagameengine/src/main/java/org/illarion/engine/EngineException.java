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
package org.illarion.engine;

import javax.annotation.Nonnull;

/**
 * This is the exception thrown in case something goes wrong inside the engine.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class EngineException extends Exception {
    public EngineException() {
    }

    public EngineException(@Nonnull String message) {
        super(message);
    }

    public EngineException(@Nonnull String message, @Nonnull Throwable cause) {
        super(message, cause);
    }

    public EngineException(@Nonnull Throwable cause) {
        super(cause);
    }
}
