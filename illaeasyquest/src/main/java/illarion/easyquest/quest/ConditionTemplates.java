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

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

public class ConditionTemplates {
    private static final ConditionTemplates INSTANCE = new ConditionTemplates();
    @Nonnull
    private final Map<String, ConditionTemplate> typeMap;
    /**
     * Internal storage for the templates.
     */
    private ConditionTemplate[] templates;
    /**
     * Templates array that gets exposed to the rest of the application.
     */
    private ConditionTemplate[] publicTemplates;

    public ConditionTemplates() {
        typeMap = new HashMap<>();

        load();
    }

    @Nonnull
    public static ConditionTemplates getInstance() {
        return INSTANCE;
    }

    private static InputStream getResource(String name) {
        ClassLoader loader = ConditionTemplates.class.getClassLoader();
        return loader.getResourceAsStream(name);
    }

    @Nonnull
    private static List<String> loadFileList() {
        List<String> result = new ArrayList<>();
        BufferedReader bRead = null;
        try {
            bRead = new BufferedReader(new InputStreamReader(getResource("template/condition/filelist"), Charset.defaultCharset()));

            String line;
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
        Collection<String> templateFiles = loadFileList();
        Collection<ConditionTemplate> templateList = new ArrayList<>();

        if (templateFiles.isEmpty()) {
            System.out.println("Condition directory does not exist!");
        } else {
            boolean isGerman = Lang.getInstance().isGerman();
            for (String rawFileName : templateFiles) {
                String fileName = rawFileName.replace('\\', '/');
                String line = null;
                String uniqueName = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
                ConditionTemplate conditionTemplate = new ConditionTemplate(uniqueName);
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(getResource(fileName), "ISO-8859-1"));

                    while ((line = reader.readLine()) != null) {

                        if (line.isEmpty()) {
                        } else if (line.matches("--.*\\w+.*--.*\\w+.*")) {
                            String[] names = line.split("\\s*--\\s*");
                            if (isGerman) {
                                conditionTemplate.setTitle(names[2]);
                            } else {
                                conditionTemplate.setTitle(names[1]);
                            }
                        } else if (line.matches("local\\s+[_A-Z0-9]+\\s*=\\s*[_A-Z0-9]+\\s*--.*\\w+.*--.*\\w+.*")) {
                            String[] param = line.split("^local\\s+|\\s*=\\s*|\\s*--\\s*");
                            if (isGerman) {
                                conditionTemplate
                                        .addParameter(new TemplateParameter(param[1], param[2] + "RELATION", param[4]));
                            } else {
                                conditionTemplate
                                        .addParameter(new TemplateParameter(param[1], param[2] + "RELATION", param[3]));
                            }
                        } else {
                            String oldCondition = conditionTemplate.getCondition();
                            if (oldCondition == null) {
                                conditionTemplate.setCondition(line);
                            } else {
                                conditionTemplate.setCondition(oldCondition + '\n' + line);
                            }
                        }
                    }

                    if (conditionTemplate.isComplete()) {
                        templateList.add(conditionTemplate);
                        typeMap.put(uniqueName, conditionTemplate);
                    } else {
                        System.out.println("Syntax error in template " + fileName);
                    }
                } catch (@Nonnull IOException e1) {
                    System.out.println("Error loading template " + fileName);
                }
            }
        }

        templates = templateList.toArray(new ConditionTemplate[templateList.size()]);
        publicTemplates = templates.clone();
    }

    public int size() {
        return templates.length;
    }

    public ConditionTemplate getTemplate(int number) {
        return templates[number];
    }

    public ConditionTemplate getTemplate(String type) {
        return typeMap.get(type);
    }

    public ConditionTemplate[] getTemplates() {
        System.arraycopy(templates, 0, publicTemplates, 0, templates.length);
        return publicTemplates;
    }
}
