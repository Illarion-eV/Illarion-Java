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
import java.util.HashMap;

import java.io.File;
import java.io.FilenameFilter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;

import illarion.easyquest.Lang;

public class HandlerTemplates
{
    private List<HandlerTemplate> templates;
    private HashMap<String, HandlerTemplate> typeMap;
    
    private static final HandlerTemplates instance = new HandlerTemplates();
    
    public static HandlerTemplates getInstance()
    {
        return instance;
    }
    
    public HandlerTemplates()
    {
        templates = new ArrayList<HandlerTemplate>();
        typeMap = new HashMap<String, HandlerTemplate>();
        load();
    }
    
    private void load()
    {
        File dir = new File("res/template/handler");
        FilenameFilter luaFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".lua");
            }
        };
        File[] templateFiles = dir.listFiles(luaFilter);
        if (templateFiles == null) {
            System.out.println("Handler directory does not exist!");
        } else {
            boolean isGerman = Lang.getInstance().isGerman();
            for (int i=0; i<templateFiles.length; i++) {
                String line = null;
                String fileName = templateFiles[i].getName();
                String uniqueName = fileName.substring(0, fileName.lastIndexOf('.'));
                int parameterCount = 0;
                HandlerTemplate handlerTemplate = new HandlerTemplate(uniqueName);
                try
                {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(
                            templateFiles[i]), "ISO-8859-1"));
                    
                    while ((line = reader.readLine()) != null) {

                        if (line.isEmpty())
                        {
                            continue;
                        }
                        else if (line.matches("--.*\\w+.*--.*\\w+.*"))
                        {
                            String[] names = line.split("\\s*--\\s*");
                            if (isGerman)
                            {
                                handlerTemplate.setTitle(names[2]);
                            }
                            else
                            {
                                handlerTemplate.setTitle(names[1]);
                            }
                        }                      
                        else if (line.matches("local\\s+[_A-Z0-9]+\\s*=\\s*[_A-Z0-9]+\\s*--.*\\w+.*--.*\\w+.*"))
                        {
                            String[] param = line.split("^local\\s+|\\s*=\\s*|\\s*--\\s*");
                            if (isGerman)
                            {
                                handlerTemplate.addParameter(new TemplateParameter(param[1], param[2], param[4]));
                            }
                            else
                            {
                                handlerTemplate.addParameter(new TemplateParameter(param[1], param[2], param[3]));
                            }
                            parameterCount = parameterCount + 1;
                        }
                        else if(line.matches("--\\s*PLAYER"))
                        {
                            handlerTemplate.addPlayerParameterAt(parameterCount);
                        }
                    }
                    
                    if (handlerTemplate.isComplete())
                    {
                        templates.add(handlerTemplate);
                        typeMap.put(uniqueName, handlerTemplate);
                    }
                    else
                    {
                        System.out.println("Syntax error in template " + templateFiles[i].getName());
                    }
                } catch (final IOException e1) {
                    System.out.println("Error loading template " + templateFiles[i].getName());
                }
            }
        }
    }
    
    public int size()
    {
        return templates.size();
    }
    
    public HandlerTemplate getTemplate(int number)
    {
        return templates.get(number);
    }
    
    public HandlerTemplate getTemplate(String type)
    {
        return typeMap.get(type);
    }
    
    public HandlerTemplate[] getTemplates()
    {
        return templates.toArray(new HandlerTemplate[0]);
    }
}