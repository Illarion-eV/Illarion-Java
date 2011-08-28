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

public class TriggerTemplates
{
    private List<TriggerTemplate> templates;
    private HashMap<String, TriggerTemplate> typeMap;
    
    private static final TriggerTemplates instance = new TriggerTemplates();
    
    public static TriggerTemplates getInstance()
    {
        return instance;
    }
    
    public TriggerTemplates()
    {
        templates = new ArrayList<TriggerTemplate>();
        typeMap = new HashMap<String, TriggerTemplate>();
        load();
    }
    
    private void load()
    {
        File dir = new File("res/template/trigger");
        FilenameFilter luaFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".lua");
            }
        };
        File[] templateFiles = dir.listFiles(luaFilter);
        if (templateFiles == null) {
            System.out.println("Trigger directory does not exist!");
        } else {
            boolean isGerman = Lang.getInstance().isGerman();
            for (int i=0; i<templateFiles.length; i++) {
                String line = null;
                boolean isHeader = true;
                StringBuffer header = new StringBuffer();
                StringBuffer body = new StringBuffer();
                String fileName = templateFiles[i].getName();
                String uniqueName = fileName.substring(0, fileName.lastIndexOf('.'));
                TriggerTemplate triggerTemplate = new TriggerTemplate(uniqueName);
                try
                {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(
                            templateFiles[i]), "ISO-8859-1"));
                    
                    while ((line = reader.readLine()) != null) {
                        if (isHeader && line.matches("function .*"))
                        {
                            isHeader = false;
                            String[] temp = line.split("function |\\(");
                            String entryPoint = temp[1].trim();
                            triggerTemplate.setEntryPoint(entryPoint);
                        }
                        if (isHeader)
                        {
                            if (line.isEmpty())
                            {
                                continue;
                            }
                            else if (line.matches("module.*"))
                            {
                                continue;
                            }
                            else if (line.matches("--\\s*category:.*"))
                            {
                                String[] temp = line.split("--\\s*category:");
                                String category = temp[1].trim();
                                triggerTemplate.setCategory(category);
                                continue;
                            }
                            else if (line.matches("--.*\\w+.*--.*\\w+.*"))
                            {
                                String[] names = line.split("\\s*--\\s*");
                                if (isGerman)
                                {
                                    triggerTemplate.setTitle(names[2]);
                                }
                                else
                                {
                                    triggerTemplate.setTitle(names[1]);
                                }
                                continue;
                            }
                            else if (line.matches("local\\s+QUEST_NUMBER\\s*=\\s*0\\s*"))
                            {
                                triggerTemplate.foundQuestNumber();
                                continue;
                            }
                            else if (line.matches("local\\s+PRECONDITION_QUESTSTATE\\s*=\\s*0\\s*"))
                            {
                                triggerTemplate.foundPrior();
                                continue;
                            }
                            else if (line.matches("local\\s+POSTCONDITION_QUESTSTATE\\s*=\\s*0\\s*"))
                            {
                                triggerTemplate.foundPosterior();
                                continue;
                            }                        
                            else if (line.matches("local\\s+[_A-Z0-9]+\\s*=\\s*[_A-Z0-9]+\\s*--.*\\w+.*--.*\\w+.*"))
                            {
                                String[] param = line.split("^local\\s+|\\s*=\\s*|\\s*--\\s*");
                                if (isGerman)
                                {
                                    triggerTemplate.addParameter(new TriggerTemplateParameter(param[1], param[2], param[4]));
                                }
                                else
                                {
                                    triggerTemplate.addParameter(new TriggerTemplateParameter(param[1], param[2], param[3]));
                                }
                                continue;
                            }
                        
                            header.append(line+"\n");
                        }
                        else
                        {
                            body.append(line+"\n");
                        }
                    }
                    
                    triggerTemplate.setHeader(header.toString());
                    triggerTemplate.setBody(body.toString());
                    
                    if (triggerTemplate.isComplete())
                    {
                        templates.add(triggerTemplate);
                        typeMap.put(uniqueName, triggerTemplate);
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
    
    public TriggerTemplate getTemplate(int number)
    {
        return templates.get(number);
    }
    
    public TriggerTemplate getTemplate(String type)
    {
        return typeMap.get(type);
    }
}