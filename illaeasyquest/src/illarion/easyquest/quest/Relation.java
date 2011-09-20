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
public class Relation implements Serializable {
    
    public enum Type {
    	EQUAL, NOTEQUAL, LESSER, GREATER, LESSEROREQUAL, GREATEROREQUAL
    }
    
    private Type type;
    
    public Relation()
    {
        type = Type.EQUAL;
    }
    
    public Relation(Type type)
    {
        this.type = type;
    }
    
    public Type getType()
    {
        return type;
    }
    
    public void setType(Type type)
    {
        this.type = type;
    }
    
    public String toString() {
    	switch (type) {
    	case EQUAL: return "=";
    	case NOTEQUAL: return "≠";
    	case LESSER: return "<";
    	case GREATER: return ">";
    	case LESSEROREQUAL: return "≤";
    	case GREATEROREQUAL: return "≥";
    	default: return "";
    	}
    }
    
    public String toLua() {
    	switch (type) {
    	case EQUAL: return "==";
    	case NOTEQUAL: return "~=";
    	case LESSER: return "<";
    	case GREATER: return ">";
    	case LESSEROREQUAL: return "<=";
    	case GREATEROREQUAL: return ">=";
    	default: return "";
    	}
    }
    
}
