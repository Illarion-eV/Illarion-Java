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
public class Status implements Serializable {

    private String name;
    private boolean start;
    private int number;
    @Nullable
    private Handler[] handlers;

    public Status() {
        name = "";
        start = false;
        number = -1;
        handlers = null;
    }

    @CalledByReflection
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @CalledByReflection
    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isStart() {
        return start;
    }

    @CalledByReflection
    public void setNumber(int number) {
        this.number = number;
    }

    @CalledByReflection
    public int getNumber() {
        return number;
    }

    @CalledByReflection
    public void setHandlers(@Nullable Handler[] handlers) {
        this.handlers = handlers;
    }

    @CalledByReflection
    @Nullable
    public Handler[] getHandlers() {
        return handlers;
    }

    @Override
    @Nonnull
    public final String toString() {
        return getName();
    }
}