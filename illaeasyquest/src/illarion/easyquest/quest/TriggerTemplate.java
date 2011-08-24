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

public class TriggerTemplate
{
    private String name;
    private String title;
    private boolean hasQuestNumber;
    private boolean hasPrior;
    private boolean hasPosterior;
    private List<TriggerParameter> parameters;
    
    public TriggerTemplate(String name)
    {
        this.name = name;
        title = null;
        hasQuestNumber = false;
        hasPrior = false;
        hasPosterior = false;
        parameters = new ArrayList<TriggerParameter>();
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void foundQuestNumber()
    {
        hasQuestNumber = true;
    }
    
    public void foundPrior()
    {
        hasPrior = true;
    }
    
    public void foundPosterior()
    {
        hasPosterior = true;
    }
    
    public void addParameter(TriggerParameter parameter)
    {
        parameters.add(parameter);
    }
    
    public boolean isComplete()
    {
        return (title != null) && hasQuestNumber && hasPrior && hasPosterior
            && (parameters.size() > 0);
    }
    
    public String toString()
    {
        return name;
    }
}