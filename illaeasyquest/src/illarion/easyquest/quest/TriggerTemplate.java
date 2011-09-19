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
    private String category;
    private String header;
    private String bodyBeforeHandler;
    private String bodyAfterHandler;
    private String entryPoint;
    private boolean hasQuestNumber;
    private boolean hasPrior;
    private boolean hasPosterior;
    private List<TemplateParameter> parameters;
    
    public TriggerTemplate(String name)
    {
        this.name = name;
        title = null;
        category = null;
        header = null;
        bodyBeforeHandler = null;
        bodyAfterHandler = null;
        entryPoint = null;
        hasQuestNumber = false;
        hasPrior = false;
        hasPosterior = false;
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
    
    public String getCategory()
    {
        return category;
    }
    
    public void setCategory(String category)
    {
        this.category = category;
    }
    
    public String getHeader()
    {
        return header;
    }
    
    public void setHeader(String header)
    {
        this.header = header;
    }
    
    public String getBodyBeforeHandler()
    {
        return bodyBeforeHandler;
    }
    
    public void setBodyBeforeHandler(String body)
    {
        this.bodyBeforeHandler = body;
    }
    
    public String getBodyAfterHandler()
    {
        return bodyAfterHandler;
    }
    
    public void setBodyAfterHandler(String body)
    {
        this.bodyAfterHandler = body;
    }
    
    public String getEntryPoint()
    {
        return entryPoint;
    }
    
    public void setEntryPoint(String entryPoint)
    {
        this.entryPoint = entryPoint;
    }
    
    public int size()
    {
        return parameters.size();
    }
    
    public TemplateParameter getParameter(int number)
    {
        return parameters.get(number);
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
    
    public void addParameter(TemplateParameter parameter)
    {
        parameters.add(parameter);
    }
    
    public boolean isComplete()
    {
        return (title != null) && hasQuestNumber && hasPrior && hasPosterior
            && (header != null) && (bodyBeforeHandler != null)
            && (bodyAfterHandler != null) && (entryPoint != null)
            && (category != null) && (parameters.size() > 0);
    }
    
    public String toString()
    {
        return title;
    }
}