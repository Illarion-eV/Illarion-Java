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
import java.util.Map;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import javolution.util.FastComparator;
import javolution.util.FastList;
import javolution.util.FastMap;

import illarion.easyquest.Lang;

public class HandlerTemplates {
    private List<HandlerTemplate> templates;
    private Map<String, HandlerTemplate> typeMap;

    private static final HandlerTemplates instance = new HandlerTemplates();

    public static HandlerTemplates getInstance() {
        return instance;
    }

    public HandlerTemplates() {
        templates = new FastList<HandlerTemplate>();
        
        final FastMap<String, HandlerTemplate> localTypeMap =
            new FastMap<String, HandlerTemplate>();
        localTypeMap.setKeyComparator(FastComparator.STRING);
        typeMap = localTypeMap;
        
        load();
    }

    private static InputStream getResource(final String name) {
        final ClassLoader loader = TriggerTemplates.class.getClassLoader();
        return loader.getResourceAsStream(name);
    }

    private List<String> loadFileList() {
        List<String> result = new ArrayList<String>();
        BufferedReader bRead = null;
        try {
            bRead =
                new BufferedReader(new InputStreamReader(
                    (getResource("template/handler/filelist"))));

            String line = null;
            while ((line = bRead.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {
            // reading failure
        } finally {
            if (bRead != null) {
                try {
                    bRead.close();
                } catch (IOException e) {
                    // does not matter
                }
            }
        }

        return result;
    }

    private void load() {
        List<String> templateFiles = loadFileList();
        if (templateFiles.isEmpty()) {
            System.out.println("Handler directory does not exist!");
        } else {
            boolean isGerman = Lang.getInstance().isGerman();
            for (String rawFileName : templateFiles) {
                String fileName = rawFileName.replace('\\', '/');
                String line = null;
                String uniqueName =
                    fileName.substring(fileName.lastIndexOf('/') + 1,
                        fileName.lastIndexOf('.'));
                int parameterCount = 0;
                HandlerTemplate handlerTemplate =
                    new HandlerTemplate(uniqueName);
                try {
                    BufferedReader reader =
                        new BufferedReader(new InputStreamReader(
                            getResource(fileName), "ISO-8859-1"));

                    while ((line = reader.readLine()) != null) {

                        if (line.isEmpty()) {
                            continue;
                        } else if (line.matches("--.*\\w+.*--.*\\w+.*")) {
                            String[] names = line.split("\\s*--\\s*");
                            if (isGerman) {
                                handlerTemplate.setTitle(names[2]);
                            } else {
                                handlerTemplate.setTitle(names[1]);
                            }
                        } else if (line
                            .matches("local\\s+[_A-Z0-9]+\\s*=\\s*[_A-Z0-9]+\\s*--.*\\w+.*--.*\\w+.*")) {
                            String[] param =
                                line.split("^local\\s+|\\s*=\\s*|\\s*--\\s*");
                            if (isGerman) {
                                handlerTemplate
                                    .addParameter(new TemplateParameter(
                                        param[1], param[2], param[4]));
                            } else {
                                handlerTemplate
                                    .addParameter(new TemplateParameter(
                                        param[1], param[2], param[3]));
                            }
                            parameterCount = parameterCount + 1;
                        } else if (line.matches("--\\s*PLAYER")) {
                            handlerTemplate
                                .addPlayerParameterAt(parameterCount);
                        }
                    }

                    if (handlerTemplate.isComplete()) {
                        templates.add(handlerTemplate);
                        typeMap.put(uniqueName, handlerTemplate);
                    } else {
                        System.out.println("Syntax error in template "
                            + fileName);
                    }
                } catch (final IOException e1) {
                    System.out.println("Error loading template " + fileName);
                }
            }
        }
    }

    public int size() {
        return templates.size();
    }

    public HandlerTemplate getTemplate(int number) {
        return templates.get(number);
    }

    public HandlerTemplate getTemplate(String type) {
        return typeMap.get(type);
    }

    public HandlerTemplate[] getTemplates() {
        return templates.toArray(new HandlerTemplate[0]);
    }
}
