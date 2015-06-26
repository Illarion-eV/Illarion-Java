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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TriggerTemplate {
    private final String name;
    @Nullable
    private String title;
    @Nullable
    private String category;
    @Nullable
    private String header;
    @Nullable
    private String body;
    @Nullable
    private String entryPoint;
    private boolean hasQuestNumber;
    private boolean hasPrior;
    private boolean hasPosterior;
    @Nullable
    private TemplateParameter id;
    private final List<TemplateParameter> parameters;

    public TriggerTemplate(String name) {
        this.name = name;
        title = null;
        category = null;
        header = null;
        body = null;
        entryPoint = null;
        hasQuestNumber = false;
        hasPrior = false;
        hasPosterior = false;
        id = null;
        parameters = new ArrayList<>();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getCategory() {
        return category;
    }

    public void setCategory(@Nonnull String category) {
        this.category = category;
        String idType, idDescription;
        if (category.equals("triggerfield")) {
            idType = "POSITION";
            idDescription = TriggerTemplate.class.getName() + ".idPosition";
        } else {
            idType = "INTEGER";
            idDescription = TriggerTemplate.class.getName() + ".idInteger";
        }
        id = new TemplateParameter("objectId", idType, idDescription);
    }

    @Nullable
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Nullable
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Nullable
    public String getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    @Nullable
    public TemplateParameter getId() {
        return id;
    }

    public int size() {
        return parameters.size();
    }

    public TemplateParameter getParameter(int number) {
        return parameters.get(number);
    }

    public void foundQuestNumber() {
        hasQuestNumber = true;
    }

    public void foundPrior() {
        hasPrior = true;
    }

    public void foundPosterior() {
        hasPosterior = true;
    }

    public void addParameter(TemplateParameter parameter) {
        parameters.add(parameter);
    }

    public boolean isComplete() {
        return (title != null) && hasQuestNumber && hasPrior && hasPosterior && (header != null) && (body != null) &&
                (entryPoint != null) && (category != null) && (id != null);
    }

    @Override
    @Nonnull
    public String toString() {
        return title;
    }
}