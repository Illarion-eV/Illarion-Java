/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.easyquest.quest;

import illarion.easyquest.Lang;
import javolution.util.FastMap;
import javolution.util.FastTable;
import javolution.util.function.Equalities;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TriggerTemplates {
    private TriggerTemplate[] templates;
    @Nonnull
    private final Map<String, TriggerTemplate> typeMap;

    private static final TriggerTemplates instance = new TriggerTemplates();

    @Nonnull
    public static TriggerTemplates getInstance() {
        return instance;
    }

    public TriggerTemplates() {
        typeMap = new FastMap<>(Equalities.LEXICAL_FAST, Equalities.STANDARD);

        load();
    }

    private static InputStream getResource(String name) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader.getResourceAsStream(name);
    }

    @Nonnull
    private static List<String> loadFileList() {
        List<String> result = new FastTable<>();
        BufferedReader bRead = null;
        try {
            bRead = new BufferedReader(new InputStreamReader(getResource("template/trigger/filelist"), Charset.defaultCharset()));

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
        Collection<TriggerTemplate> templateList = new FastTable<>();

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
                String uniqueName = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
                TriggerTemplate triggerTemplate = new TriggerTemplate(uniqueName);
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(getResource(fileName), "ISO-8859-1"));

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
                            }
                            if (line.matches("module.*")) {
                                continue;
                            }
                            if (line.matches("--\\s*category:.*")) {
                                String[] temp = line.split("--\\s*category:");
                                String category = temp[1].trim();
                                triggerTemplate.setCategory(category);
                                continue;
                            }
                            if (line.matches("--.*\\w+.*--.*\\w+.*")) {
                                String[] names = line.split("\\s*--\\s*");
                                if (isGerman) {
                                    triggerTemplate.setTitle(names[2]);
                                } else {
                                    triggerTemplate.setTitle(names[1]);
                                }
                                continue;
                            }
                            if (line.matches("local\\s+QUEST_NUMBER\\s*=\\s*0\\s*")) {
                                triggerTemplate.foundQuestNumber();
                                continue;
                            }
                            if (line.matches("local\\s+PRECONDITION_QUESTSTATE\\s*=\\s*0\\s*")) {
                                triggerTemplate.foundPrior();
                                continue;
                            }
                            if (line.matches("local\\s+POSTCONDITION_QUESTSTATE\\s*=\\s*0\\s*")) {
                                triggerTemplate.foundPosterior();
                                continue;
                            }
                            if (line.matches("local\\s+[_A-Z0-9]+\\s*=\\s*[_A-Z0-9]+\\s*--.*\\w+.*--.*\\w+.*")) {
                                String[] param = line.split("^local\\s+|\\s*=\\s*|\\s*--\\s*");

                                String description;
                                if (isGerman) {
                                    description = param[4];
                                } else {
                                    description = param[3];
                                }

                                TemplateParameter parameter = new TemplateParameter(param[1], param[2], description);

                                triggerTemplate.addParameter(parameter);

                                continue;
                            }

                            header.append(line).append('\n');
                        } else {
                            body.append(line).append('\n');
                        }
                    }

                    triggerTemplate.setHeader(header.toString());
                    triggerTemplate.setBody(body.toString());

                    if (triggerTemplate.isComplete()) {
                        templateList.add(triggerTemplate);
                        typeMap.put(uniqueName, triggerTemplate);
                    } else {
                        System.out.println("Syntax error in template " + fileName);
                    }
                } catch (@Nonnull IOException e1) {
                    System.out.println("Error loading template " + fileName);
                }
            }
        }

        templates = templateList.toArray(new TriggerTemplate[templateList.size()]);
    }

    public int size() {
        return templates.length;
    }

    public TriggerTemplate getTemplate(int number) {
        return templates[number];
    }

    public TriggerTemplate getTemplate(String type) {
        if (typeMap.containsKey(type)) {
            return typeMap.get(type);
        }
        throw new IllegalArgumentException("Illegal template type requested: " + type);
    }
}
