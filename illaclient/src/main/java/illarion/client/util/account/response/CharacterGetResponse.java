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
import illarion.common.types.CharacterId;

import java.util.Collections;
import java.util.List;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CharacterGetResponse {
    @SerializedName("id")
    private CharacterId id;

    @SerializedName("name")
    private String name;

    @SerializedName("race")
    private int race;

    @SerializedName("raceType")
    private int raceType;

    @SerializedName("attributes")
    private CharacterAttributesResponse attributes;

    @SerializedName("dateOfBirth")
    private int dateOfBirth;

    @SerializedName("bodyHeight")
    private int bodyHeight;

    @SerializedName("bodyWeight")
    private int bodyWeight;

    @SerializedName("paperDoll")
    private CharacterPaperDollResponse paperDoll;

    @SerializedName("items")
    private List<CharacterItemResponse> items;

    @SerializedName("error")
    private ErrorResponse error;

    public CharacterId getId() {
        assert id != null;

        return id;
    }

    public String getName() {
        assert name != null;

        return name;
    }

    public int getRace() {
        return race;
    }

    public int getRaceType() {
        return raceType;
    }

    public CharacterAttributesResponse getAttributes() {
        assert attributes != null;

        return attributes;
    }

    public int getDateOfBirth() {
        return dateOfBirth;
    }

    public int getBodyHeight() {
        return bodyHeight;
    }

    public int getBodyWeight() {
        return bodyWeight;
    }

    public CharacterPaperDollResponse getPaperDoll() {
        assert paperDoll != null;
        return paperDoll;
    }

    public List<CharacterItemResponse> getItems() {
        assert items != null;
        return Collections.unmodifiableList(items);
    }

    public ErrorResponse getError() {
        assert error != null;
        return error;
    }
}
