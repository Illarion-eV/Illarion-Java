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
package illarion.easyquest.quest;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Arrays;

@SuppressWarnings("serial")
public class Condition implements Serializable {
    @Nonnull
    private final String type;
    @Nonnull
    private Object[] parameters;

    public Condition(@Nonnull final String type, @Nonnull final Object[] parameters) {
        this.type = type;
        this.parameters = Arrays.copyOf(parameters, parameters.length);
    }

    @Nonnull
    public String getType() {
        return type;
    }

    @Nonnull
    public Object[] getParameters() {
        return parameters;
    }
}