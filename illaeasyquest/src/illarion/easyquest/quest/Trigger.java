/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright 2011 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyQuest Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyQuest Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easyquest.quest;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Trigger implements Serializable {

    private String name;
    private long objectId;
    private String type;
    private Object[] parameters;

    public Trigger() {
        name = "";
        objectId = 0;
        type = null;
        parameters = null;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setObjectId(long id) {
        objectId = id;
    }
    
    public long getObjectId() {
        return objectId;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
    
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
    
    public Object[] getParameters() {
        return parameters;
    }
    
    public final String toString() {
        return getName();
    }
    
}