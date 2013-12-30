/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.resources;

import illarion.client.resources.data.ResourceTemplate;

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
    protected AbstractTemplateFactory(final int defaultId) {
        storage = new HashMap<>();
        this.defaultId = defaultId;
    }

    @Override
    public void storeResource(@Nonnull final T resource) {
        storage.put(resource.getTemplateId(), resource);
    }

    @Override
    public void loadingFinished() {
    }

    @Override
    public void init() {
    }

    public boolean hasTemplate(final int templateId) {
        return storage.get(templateId) != null;
    }

    @Nonnull
    public T getTemplate(final int templateId) {
        final T object = storage.get(templateId);
        if ((object == null) && (defaultId > -1)) {
            final T defaultObject = storage.get(defaultId);
            if (defaultObject == null) {
                throw new IllegalStateException("Requested object and the default object were not found.");
            }
            return defaultObject;
        }
        if (object == null) {
            throw new IllegalStateException("Requested object was not found and not default object was declared.");
        }
        return object;
    }
}
