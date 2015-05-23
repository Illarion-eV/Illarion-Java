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
package illarion.easyquest.quest;

import illarion.common.util.CalledByReflection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Trigger implements Serializable {
    @Nonnull
    private String name;
    @Nullable
    private String type;
    @Nullable
    private Object objectId;
    @Nullable
    private Object[] parameters;
    @Nullable
    private Condition[] conditions;

    @CalledByReflection
    public Trigger() {
        name = "";
        type = null;
        parameters = null;
        conditions = null;
    }

    @CalledByReflection
    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @CalledByReflection
    @Nonnull
    public String getName() {
        return name;
    }

    @CalledByReflection
    public void setObjectId(Object id) {
        objectId = id;
    }

    @Nullable
    @CalledByReflection
    public Object getObjectId() {
        return objectId;
    }

    @CalledByReflection
    public void setType(@Nullable String type) {
        this.type = type;
    }

    @CalledByReflection
    @Nullable
    public String getType() {
        return type;
    }

    @CalledByReflection
    public void setParameters(@Nullable Object[] parameters) {
        this.parameters = parameters;
    }

    @CalledByReflection
    @Nullable
    public Object[] getParameters() {
        return parameters;
    }

    @CalledByReflection
    public void setConditions(@Nullable Condition[] conditions) {
        this.conditions = conditions;
    }

    @CalledByReflection
    @Nullable
    public Condition[] getConditions() {
        return conditions;
    }

    @Override
    @Nonnull
    public final String toString() {
        return getName();
    }
}