/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyQuest Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyQuest Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easyquest.quest;

import illarion.common.util.CalledByReflection;

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

    public final String toString() {
        return getName();
    }
}