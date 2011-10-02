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

import java.util.List;
import java.util.ArrayList;

public class ConditionTemplate implements Comparable<ConditionTemplate>
{
    private String name;
    private String title;
    private String condition;
    private List<TemplateParameter> parameters;
    
    public ConditionTemplate(String name)
    {
        this.name = name;
        title = null;
        condition = null;
        parameters = new ArrayList<TemplateParameter>();
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setCondition(String condition)
    {
        this.condition = condition;
    }
    
    public String getCondition()
    {
        return condition;
    }
    
    public int size()
    {
        return parameters.size();
    }
    
    public TemplateParameter getParameter(int number)
    {
        return parameters.get(number);
    }
    
    public void addParameter(TemplateParameter parameter)
    {
        parameters.add(parameter);
    }
    
    public boolean isComplete()
    {
        return (title != null) && (condition != null) && (parameters.size() > 0);
    }
    
    public String toString()
    {
        return title;
    }

	@Override
	public int compareTo(ConditionTemplate o) {
		return toString().compareTo(o.toString());
	}
}