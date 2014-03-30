/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HandlerTemplates {
    /**
     * Internal storage for the templates.
     */
    private HandlerTemplate[] templates;

    /**
     * Templates array that gets exposed to the rest of the application.
     */
    private HandlerTemplate[] publicTemplates;

    @Nonnull
    private final Map<String, HandlerTemplate> typeMap;

    private static final HandlerTemplates INSTANCE = new HandlerTemplates();

    @Nonnull
    public static HandlerTemplates getInstance() {
        return INSTANCE;
    }

    public HandlerTemplates() {
        typeMap = new FastMap<>(Equalities.LEXICAL_FAST, Equalities.STANDARD);

        load();
    }

    private static InputStream getResource(final String name) {
        final ClassLoader loader = HandlerTemplates.class.getClassLoader();
        return loader.getResourceAsStream(name);
    }

    @Nonnull
    private static List<String> loadFileList() {
        List<String> result = new FastTable<>();
        BufferedReader bRead = null;
        try {
            bRead = new BufferedReader(new InputStreamReader((getResource("template/handler/filelist"))));

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
        Collection<HandlerTemplate> templateList = new FastTable<>();

        if (templateFiles.isEmpty()) {
            System.out.println("Handler directory does not exist!");
        } else {
            boolean isGerman = Lang.getInstance().isGerman();
            for (String rawFileName : templateFiles) {
                String fileName = rawFileName.replace('\\', '/');
                String line = null;
                String uniqueName = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
                int parameterCount = 0;
                HandlerTemplate handlerTemplate = new HandlerTemplate(uniqueName);
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(getResource(fileName), "ISO-8859-1"));

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
                        } else if (line.matches("local\\s+[_A-Z0-9]+\\s*=\\s*[_A-Z0-9]+\\s*--.*\\w+.*--.*\\w+.*")) {
                            String[] param = line.split("^local\\s+|\\s*=\\s*|\\s*--\\s*");
                            if (isGerman) {
                                handlerTemplate.addParameter(new TemplateParameter(param[1], param[2], param[4]));
                            } else {
                                handlerTemplate.addParameter(new TemplateParameter(param[1], param[2], param[3]));
                            }
                            parameterCount = parameterCount + 1;
                        } else if (line.matches("--\\s*PLAYER")) {
                            handlerTemplate.addPlayerParameterAt(parameterCount);
                        }
                    }

                    if (handlerTemplate.isComplete()) {
                        templateList.add(handlerTemplate);
                        typeMap.put(uniqueName, handlerTemplate);
                    } else {
                        System.out.println("Syntax error in template " + fileName);
                    }
                } catch (@Nonnull final IOException e1) {
                    System.out.println("Error loading template " + fileName);
                }
            }
        }

        templates = templateList.toArray(new HandlerTemplate[templateList.size()]);
        publicTemplates = templates.clone();
    }

    public int size() {
        return templates.length;
    }

    public HandlerTemplate getTemplate(int number) {
        return templates[number];
    }

    public HandlerTemplate getTemplate(String type) {
        return typeMap.get(type);
    }

    public HandlerTemplate[] getTemplates() {
        System.arraycopy(templates, 0, publicTemplates, 0, templates.length);
        return publicTemplates;
    }
}
