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

import illarion.easyquest.Lang;
import javolution.util.FastComparator;
import javolution.util.FastMap;
import javolution.util.FastTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class TriggerTemplates {
    private TriggerTemplate[] templates;
    private final Map<String, TriggerTemplate> typeMap;

    private static final TriggerTemplates instance = new TriggerTemplates();

    public static TriggerTemplates getInstance() {
        return instance;
    }

    public TriggerTemplates() {
        final FastMap<String, TriggerTemplate> localTypeMap =
                new FastMap<String, TriggerTemplate>();
        localTypeMap.setKeyComparator(FastComparator.STRING);
        typeMap = localTypeMap;

        load();
    }

    private static InputStream getResource(final String name) {
        final ClassLoader loader = TriggerTemplates.class.getClassLoader();
        return loader.getResourceAsStream(name);
    }

    private static List<String> loadFileList() {
        List<String> result = new FastTable<String>();
        BufferedReader bRead = null;
        try {
            bRead =
                    new BufferedReader(new InputStreamReader(
                            getResource("template/trigger/filelist")));

            String line = null;
            while ((line = bRead.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {
            // reading failure
        } catch (NullPointerException e) {
            // file list does not exist
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
        FastTable<TriggerTemplate> templateList = FastTable.newInstance();

        if (templateFiles.isEmpty()) {
            System.out.println("Trigger directory does not exist!");
        } else {
            boolean isGerman = Lang.getInstance().isGerman();
            for (String rawFileName : templateFiles) {
                String fileName = rawFileName.replace('\\', '/');
                String line = null;
                boolean isHeader = true;
                StringBuffer header = new StringBuffer();
                StringBuffer body = new StringBuffer();
                String uniqueName =
                        fileName.substring(fileName.lastIndexOf('/') + 1,
                                fileName.lastIndexOf('.'));
                TriggerTemplate triggerTemplate =
                        new TriggerTemplate(uniqueName);
                try {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(
                                    getResource(fileName), "ISO-8859-1"));

                    while ((line = reader.readLine()) != null) {
                        if (isHeader && line.matches("function .*")) {
                            isHeader = false;
                            String[] temp = line.split("function |\\(");
                            String entryPoint = temp[1].trim();
                            triggerTemplate.setEntryPoint(entryPoint);
                        }
                        if (isHeader) {
                            if (line.isEmpty()) {
                                continue;
                            } else if (line.matches("module.*")) {
                                continue;
                            } else if (line.matches("--\\s*category:.*")) {
                                String[] temp = line.split("--\\s*category:");
                                String category = temp[1].trim();
                                triggerTemplate.setCategory(category);
                                continue;
                            } else if (line.matches("--.*\\w+.*--.*\\w+.*")) {
                                String[] names = line.split("\\s*--\\s*");
                                if (isGerman) {
                                    triggerTemplate.setTitle(names[2]);
                                } else {
                                    triggerTemplate.setTitle(names[1]);
                                }
                                continue;
                            } else if (line
                                    .matches("local\\s+QUEST_NUMBER\\s*=\\s*0\\s*")) {
                                triggerTemplate.foundQuestNumber();
                                continue;
                            } else if (line
                                    .matches("local\\s+PRECONDITION_QUESTSTATE\\s*=\\s*0\\s*")) {
                                triggerTemplate.foundPrior();
                                continue;
                            } else if (line
                                    .matches("local\\s+POSTCONDITION_QUESTSTATE\\s*=\\s*0\\s*")) {
                                triggerTemplate.foundPosterior();
                                continue;
                            } else if (line
                                    .matches("local\\s+[_A-Z0-9]+\\s*=\\s*[_A-Z0-9]+\\s*--.*\\w+.*--.*\\w+.*")) {
                                String[] param =
                                        line.split("^local\\s+|\\s*=\\s*|\\s*--\\s*");

                                String description;
                                if (isGerman) {
                                    description = param[4];
                                } else {
                                    description = param[3];
                                }

                                TemplateParameter parameter = new TemplateParameter(
                                        param[1], param[2], description);

                                triggerTemplate.addParameter(parameter);

                                continue;
                            }

                            header.append(line + "\n");
                        } else {
                            body.append(line + "\n");
                        }
                    }

                    triggerTemplate.setHeader(header.toString());
                    triggerTemplate.setBody(body.toString());

                    if (triggerTemplate.isComplete()) {
                        templateList.add(triggerTemplate);
                        typeMap.put(uniqueName, triggerTemplate);
                    } else {
                        System.out.println("Syntax error in template "
                                + fileName);
                    }
                } catch (final Exception e1) {
                    System.out.println("Error loading template " + fileName);
                }
            }
        }

        templates = templateList.toArray(new TriggerTemplate[templateList.size()]);
        FastTable.recycle(templateList);
    }

    public int size() {
        return templates.length;
    }

    public TriggerTemplate getTemplate(int number) {
        return templates[number];
    }

    public TriggerTemplate getTemplate(String type) {
        return typeMap.get(type);
    }
}
