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

import java.io.File;
import java.io.FilenameFilter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;

import illarion.easyquest.Lang;

public class TriggerTemplates
{
    private static final TriggerTemplates instance = new TriggerTemplates();
    
    public static TriggerTemplates getInstance()
    {
        return instance;
    }
    
    public TriggerTemplates()
    {
        load();
    }
    
    public void load()
    {
        File dir = new File("res/template/trigger");
        FilenameFilter luaFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".lua");
            }
        };
        File[] templates = dir.listFiles(luaFilter);
        if (templates == null) {
            System.out.println("Trigger directory does not exist!");
        } else {
            boolean isGerman = Lang.getInstance().isGerman();
            for (int i=0; i<templates.length; i++) {
                String line = null;
                boolean isHeader = true;
                String name = null;
                boolean foundNumber = false;
                boolean foundPreCondition = false;
                boolean foundPostCondition = false;
                StringBuffer header = new StringBuffer();
                StringBuffer body = new StringBuffer();
                try
                {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(
                            templates[i]), "ISO-8859-1"));
                    while ((line = reader.readLine()) != null) {
                        if (isHeader && line.contains("function"))
                        {
                            isHeader = false;
                        }
                        if (isHeader)
                        {
                            if (line.isEmpty())
                            {
                                continue;
                            }
                            else if (line.matches("--.*\\w+.*--.*\\w+.*"))
                            {
                                String[] names = line.split("\\s*--\\s*");
                                if (isGerman)
                                {
                                    name = names[2];
                                }
                                else
                                {
                                    name = names[1];
                                }
                                System.out.println("Trigger Name: "+name);
                            }
                            else if (line.matches("local\\s+QUEST_NUMBER\\s*=\\s*0\\s*"))
                            {
                                foundNumber = true;
                                System.out.println("found number");
                                continue;
                            }
                            else if (line.matches("local\\s+PRECONDITION_QUESTSTATE\\s*=\\s*0\\s*"))
                            {
                                foundPreCondition = true;
                                System.out.println("found prior");
                                continue;
                            }
                            else if (line.matches("local\\s+POSTCONDITION_QUESTSTATE\\s*=\\s*0\\s*"))
                            {
                                foundPostCondition = true;
                                System.out.println("found posterior");
                                continue;
                            }                        
                            else if (line.matches("local\\s+[_A-Z0-9]+\\s*=\\s*[_A-Z0-9]+\\s*--.*\\w+.*--.*\\w+.*"))
                            {
                                String[] param = line.split("^local\\s+|\\s*=\\s*|\\s*--\\s*");
                                System.out.println("Name: "+param[1]+"\nType: "+param[2]+"\nEN: "+param[3]+"\nDE: "+param[4]+"\n");
                                continue;
                            }
                        
                            header.append(line+"\n");
                        }
                        else
                        {
                            body.append(line+"\n");
                        }
                    }
                } catch (final IOException e1) {
                    System.out.println("Error loading template " + templates[i].getName());
                }
            }
        }
    }
}