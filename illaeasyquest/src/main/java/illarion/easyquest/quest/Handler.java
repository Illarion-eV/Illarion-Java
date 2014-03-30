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

import illarion.common.util.CalledByReflection;

import javax.annotation.Nonnull;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Handler implements Serializable {

    @Nonnull
    private String type;
    @Nonnull
    private Object[] parameters;

    public Handler() {
    }

    public Handler(@Nonnull final String type, @Nonnull final Object... parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    @CalledByReflection
    public void setParameters(@Nonnull final Object[] parameters) {
        this.parameters = parameters;
    }

    @CalledByReflection
    public void setType(@Nonnull final String type) {
        this.type = type;
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