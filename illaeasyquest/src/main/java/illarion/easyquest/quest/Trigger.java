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

import javax.annotation.Nullable;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Trigger implements Serializable {

    private String name;
    @Nullable
    private String type;
    private Object objectId;
    @Nullable
    private Object[] parameters;
    @Nullable
    private Condition[] conditions;

    public Trigger() {
        name = "";
        type = null;
        parameters = null;
        conditions = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setObjectId(Object id) {
        objectId = id;
    }

    public Object getObjectId() {
        return objectId;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Nullable
    public String getType() {
        return type;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    @Nullable
    public Object[] getParameters() {
        return parameters;
    }

    public void setConditions(Condition[] conditions) {
        this.conditions = conditions;
    }

    @Nullable
    public Condition[] getConditions() {
        return conditions;
    }

    public final String toString() {
        return getName();
    }

}