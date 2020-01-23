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
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class RaceTypeResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("hairs")
    private List<IdNameResponse> hairs;

    @SerializedName("beards")
    private List<IdNameResponse> beards;

    @SerializedName("hairColour")
    private List<ColourResponse> hairColours;

    @SerializedName("skinColour")
    private List<ColourResponse> skinColours;

    public int getId() {
        return id;
    }

    @Nonnull
    public List<IdNameResponse> getHairs() {
        return (hairs == null) ? Collections.emptyList() : Collections.unmodifiableList(hairs);
    }

    @Nonnull
    public List<IdNameResponse> getBeards() {
        return (beards == null) ? Collections.emptyList() : Collections.unmodifiableList(beards);
    }

    @Nonnull
    public List<ColourResponse> getHairColours() {
        return (hairColours == null) ? Collections.emptyList() : Collections.unmodifiableList(hairColours);
    }

    @Nonnull
    public List<ColourResponse> getSkinColours() {
        return (skinColours == null) ? Collections.emptyList() : Collections.unmodifiableList(skinColours);
    }
}
