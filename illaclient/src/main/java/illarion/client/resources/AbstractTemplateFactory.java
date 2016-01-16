/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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

import com.google.common.collect.ImmutableMap;
import illarion.client.resources.data.ResourceTemplate;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.HashSet;
import java.util.Set;

/**
 * The purpose of this class is to store and retrieve the templates that were load from the resources. Those
 * templates are later on used to create the actual objects.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@NotThreadSafe
public abstract class AbstractTemplateFactory<T extends ResourceTemplate> implements ResourceFactory<T> {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(AbstractTemplateFactory.class);
    /**
     * The ID used in case the requested object does not exist.
     */
    private final int defaultId;
    /**
     * This is the builder that is used to create the resource storage. This variable is only used during the
     * initialization phase of this factory. Once loading is done it is not required anymore.
     */
    @Nullable
    private ImmutableMap.Builder<Integer, T> storageBuilder;
    /**
     * This variable is used during populating the resources to ensure that all keys are unique.
     */
    @Nullable
    private Set<Integer> storageBuilderKeys;
    /**
     * The map that is used to store the resources.
     */
    @Nullable
    private ImmutableMap<Integer, T> storage;

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
        this.defaultId = defaultId;
    }

    @Override
    public void init() {
        storageBuilder = new ImmutableMap.Builder<>();
        storageBuilderKeys = new HashSet<>();
    }

    @Override
    public void loadingFinished() {
        if (storageBuilder == null) {
            throw new IllegalStateException("Factory was not initialized yet.");
        }

        storage = storageBuilder.build();
        storageBuilder = null;
        storageBuilderKeys = null;
    }

    @Override
    public void storeResource(@Nonnull T resource) {
        if ((storageBuilder == null) || (storageBuilderKeys == null)) {
            throw new IllegalStateException("Factory was not initialized yet.");
        }

        if (!storageBuilderKeys.add(resource.getTemplateId())) {
            log.warn("Located duplicated resource template: {}", resource);
        }

        storageBuilder.put(resource.getTemplateId(), resource);
    }

    @Contract(pure = true)
    public boolean hasTemplate(int templateId) {
        if (storage == null) {
            throw new IllegalStateException("Factory was not initialized yet.");
        }

        return storage.containsKey(templateId);
    }

    @Nonnull
    @Contract(pure = true)
    public T getTemplate(int templateId) {
        if (storage == null) {
            throw new IllegalStateException("Factory was not initialized yet.");
        }

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
