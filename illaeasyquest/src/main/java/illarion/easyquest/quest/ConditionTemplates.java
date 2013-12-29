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
import javolution.util.FastMap;
import javolution.util.FastTable;
import javolution.util.function.Equalities;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ConditionTemplates {
    /**
     * Internal storage for the templates.
     */
    private ConditionTemplate[] templates;

    /**
     * Templates array that gets exposed to the rest of the application.
     */
    private ConditionTemplate[] publicTemplates;

    @Nonnull
    private final Map<String, ConditionTemplate> typeMap;

    private static final ConditionTemplates INSTANCE = new ConditionTemplates();

    @Nonnull
    public static ConditionTemplates getInstance() {
        return INSTANCE;
    }

    public ConditionTemplates() {
        typeMap = new FastMap<>(Equalities.LEXICAL_FAST, Equalities.STANDARD);

        load();
    }

    private static InputStream getResource(final String name) {
        final ClassLoader loader = ConditionTemplates.class.getClassLoader();
        return loader.getResourceAsStream(name);
    }

    @Nonnull
    private static List<String> loadFileList() {
        List<String> result = new FastTable<>();
        BufferedReader bRead = null;
        try {
            bRead = new BufferedReader(new InputStreamReader((getResource("template/condition/filelist"))));

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
        Collection<String> templateFiles = loadFileList();
        Collection<ConditionTemplate> templateList = new FastTable<>();

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
                            continue;
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
                                conditionTemplate.setCondition(oldCondition + "\n" + line);
                            }
                        }
                    }

                    if (conditionTemplate.isComplete()) {
                        templateList.add(conditionTemplate);
                        typeMap.put(uniqueName, conditionTemplate);
                    } else {
                        System.out.println("Syntax error in template " + fileName);
                    }
                } catch (@Nonnull final IOException e1) {
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
