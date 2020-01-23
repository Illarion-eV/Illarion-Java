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
package illarion.client.util.account.form;

import com.google.gson.annotations.SerializedName;

/**
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CharacterCreateForm {
    @SerializedName("name")
    private String name;
    
    @SerializedName("race")
    private int race;

    @SerializedName("sex")
    private int sex;

    @SerializedName("agility")
    private int agility;

    @SerializedName("constitution")
    private int constitution;

    @SerializedName("dexterity")
    private int dexterity;

    @SerializedName("essence")
    private int essence;

    @SerializedName("intelligence")
    private int intelligence;

    @SerializedName("perception")
    private int perception;

    @SerializedName("strength")
    private int strength;

    @SerializedName("willpower")
    private int willpower;

    @SerializedName("startPack")
    private int startPack;

    @SerializedName("hairId")
    private int hairId;

    @SerializedName("beardId")
    private int beardId;

    @SerializedName("email")
    private int skinColorRed;

    @SerializedName("skinColorGreen")
    private int skinColorGreen;

    @SerializedName("skinColorBlue")
    private int skinColorBlue;

    @SerializedName("skinColorAlpha")
    private int skinColorAlpha;

    @SerializedName("hairColorRed")
    private int hairColorRed;

    @SerializedName("hairColorGreen")
    private int hairColorGreen;

    @SerializedName("hairColorBlue")
    private int hairColorBlue;

    @SerializedName("hairColorAlpha")
    private int hairColorAlpha;
}
