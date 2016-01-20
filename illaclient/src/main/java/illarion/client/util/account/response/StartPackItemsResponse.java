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

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class StartPackItemsResponse {
    @SerializedName("itemId")
    private int itemId;

    @SerializedName("position")
    private int position;

    @SerializedName("number")
    private int number;

    @SerializedName("quality")
    private int quality;

    @SerializedName("name")
    private String name;

    @SerializedName("unitWorth")
    private int unitWorth;

    @SerializedName("unitWeight")
    private int unitWeight;

    public int getItemId() {
        return itemId;
    }

    public int getPosition() {
        return position;
    }

    public int getNumber() {
        return number;
    }

    public int getQuality() {
        return quality;
    }

    public String getName() {
        return name;
    }

    public int getUnitWorth() {
        return unitWorth;
    }

    public int getUnitWeight() {
        return unitWeight;
    }
}
