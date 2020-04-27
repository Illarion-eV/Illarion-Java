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
package illarion.mapedit.resource;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class manages all resources, represented by the interface {@link Resource}
 *
 * @author Tim
 */
public final class ResourceManager {
    /**
     * The singleton instance of this class.
     */
    private static final ResourceManager INSTANCE = new ResourceManager();
    /**
     * The list of resources.
     */
    @Nonnull
    private final List<Resource> res;
    /**
     * The actual loading index.
     */
    private int index;

    /**
     * A private constructor to prevent multiple instances.
     */
    private ResourceManager() {
        res = new ArrayList<>();
        index = 0;
    }

    /**
     * Adds the given resources to the resource list.
     *
     * @param r
     */
    public void addResources(Resource... r) {
        res.addAll(Arrays.asList(r));
    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return
     */
    @Nonnull
    public static ResourceManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns false if all resources are loaded, true otherwise.
     *
     * @return
     */
    public boolean hasNextToLoad() {
        return index < res.size();
    }

    /**
     * Loads the next resource.
     *
     * @throws IOException
     */
    public void loadNext() throws IOException {
        res.get(index++).load();
    }

    /**
     * Loads all resources a once.
     *
     * @throws IOException
     */
    public void loadAll() throws IOException {
        while (hasNextToLoad()) {
            loadNext();
        }
    }

    /**
     * Returns the description of the next resources.
     *
     * @return
     */
    @Nonnull
    public String getNextDescription() {
        return res.get(index).getDescription();
    }

    /**
     * Returns the description of the previous loaded resource.
     *
     * @return
     */
    @Nonnull
    public String getPrevDescription() {
        if (index <= 0) {
            return "";
        }
        return res.get(index - 1).getDescription();
    }
}
