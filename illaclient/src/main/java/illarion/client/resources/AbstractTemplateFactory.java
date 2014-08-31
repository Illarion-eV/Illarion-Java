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
package illarion.client.resources;

import illarion.client.resources.data.ResourceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashMap;
import java.util.Map;

/**
 * The purpose of this class is to store and retrieve the templates that were load from the resources. Those
 * templates are later on used to create the actual objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public abstract class AbstractTemplateFactory<T extends ResourceTemplate> implements ResourceFactory<T> {
    private static final Logger log = LoggerFactory.getLogger(AbstractTemplateFactory.class);

    /**
     * The map that is used to store the resources.
     */
    @Nonnull
    private final Map<Integer, T> storage;

    /**
     * The ID used in case the requested object does not exist.
     */
    private final int defaultId;

    /**
     * The default constructor.
     */
    protected AbstractTemplateFactory() {
        this(-1);
    }

    /**
     * The default constructor.
     */
    protected AbstractTemplateFactory(int defaultId) {
        storage = new HashMap<>();
        this.defaultId = defaultId;
    }

    @Override
    public void storeResource(@Nonnull T resource) {
        if (storage.containsKey(resource.getTemplateId())) {
            log.warn("Located duplicated resource template: {}", resource);
        }
        storage.put(resource.getTemplateId(), resource);
    }

    @Override
    public void loadingFinished() {
    }

    @Override
    public void init() {
    }

    public boolean hasTemplate(int templateId) {
        return storage.containsKey(templateId);
    }

    @Nonnull
    public T getTemplate(int templateId) {
        T object = storage.get(templateId);
        if ((object == null) && (defaultId > -1)) {
            T defaultObject = storage.get(defaultId);
            if (defaultObject == null) {
                throw new IllegalStateException("Requested template " + templateId + " and the default template " +
                                                        defaultId + " were not found.");
            }
            return defaultObject;
        }
        if (object == null) {
            throw new IllegalStateException("Requested template " + templateId +
                                                    " was not found and not default template was declared.");
        }
        return object;
    }
}
