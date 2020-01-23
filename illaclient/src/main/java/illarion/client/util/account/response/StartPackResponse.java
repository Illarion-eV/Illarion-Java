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
package illarion.client.util.account.response;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class StartPackResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("skills")
    private List<IdNameResponse> skills;

    @SerializedName("items")
    private List<StartPackItemsResponse> items;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Nonnull
    public List<IdNameResponse> getSkills() {
        return (skills == null) ? Collections.emptyList() : Collections.unmodifiableList(skills);
    }

    @Nonnull
    public List<StartPackItemsResponse> getItems() {
        return (items == null) ? Collections.emptyList() : Collections.unmodifiableList(items);
    }
}
