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
package illarion.client.gui;

import illarion.common.net.NetCommReader;
import illarion.common.types.Money;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;

/**
 * This class is used to store the information about a tool tip.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class Tooltip {
    public static final int RARENESS_COMMON = 1;
    public static final int RARENESS_UNCOMMON = 2;
    public static final int RARENESS_RARE = 3;
    public static final int RARENESS_EPIC = 4;

    @Nonnull
    private final String name;
    private final int rareness;
    @Nonnull
    private final String description;
    @Nonnull
    private final String craftedBy;
    @Nonnull
    private final String type;
    private final int level;
    private final boolean usable;
    private final int weight;
    @Nonnull
    private final Money worth;
    @Nonnull
    private final String qualityText;
    @Nonnull
    private final String durabilityText;
    private final int durabilityValue;
    private final int diamondLevel;
    private final int emeraldLevel;
    private final int rubyLevel;
    private final int sapphireLevel;
    private final int amethystLevel;
    private final int obsidianLevel;
    private final int topazLevel;
    private final double bonus;

    /**
     * Decode look at data from server receive buffer. And store the data for later execution.
     *
     * @param reader the receiver that stores the data that shall be decoded in this function
     * @throws IOException In case the function reads over the buffer of the receiver this exception is thrown
     */
    public Tooltip(@Nonnull NetCommReader reader) throws IOException {
        name = reader.readString();
        rareness = reader.readUByte();
        description = reader.readString();
        craftedBy = reader.readString();
        type = reader.readString();
        level = reader.readUByte();
        usable = reader.readUByte() == 1;
        weight = (int) reader.readUInt();
        worth = new Money(reader.readUInt());
        qualityText = reader.readString();
        durabilityText = reader.readString();
        durabilityValue = reader.readUByte();
        diamondLevel = reader.readUByte();
        emeraldLevel = reader.readUByte();
        rubyLevel = reader.readUByte();
        sapphireLevel = reader.readUByte();
        amethystLevel = reader.readUByte();
        obsidianLevel = reader.readUByte();
        topazLevel = reader.readUByte();
        bonus = reader.readUByte();
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public int getRareness() {
        return rareness;
    }

    @Nonnull
    public String getDescription() {
        return description;
    }

    @Nonnull
    public String getCraftedBy() {
        return craftedBy;
    }

    @Nonnull
    public String getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public boolean isUsable() {
        return usable;
    }

    public int getWeight() {
        return weight;
    }

    @Nonnull
    public Money getWorth() {
        return worth;
    }

    @Nonnull
    public String getQualityText() {
        return qualityText;
    }

    @Nonnull
    public String getDurabilityText() {
        return durabilityText;
    }

    public int getDurabilityValue() {
        return durabilityValue;
    }

    public int getDiamondLevel() {
        return diamondLevel;
    }

    public int getEmeraldLevel() {
        return emeraldLevel;
    }

    public int getRubyLevel() {
        return rubyLevel;
    }

    public int getSapphireLevel() {
        return sapphireLevel;
    }

    public int getAmethystLevel() {
        return amethystLevel;
    }

    public int getObsidianLevel() {
        return obsidianLevel;
    }

    public int getTopazLevel() {
        return topazLevel;
    }

    public double getBonus() {
        return bonus/2; //divided by 2 because it could only be stored as a integer and fighting gem bonus is calculated in 0.5 per gem, while the server protocol does not allow for floating points
    }

    @Override
    @Nonnull
    public String toString() {
        return "Item Tooltip: " + (isValid() ? name : "(invalid)");
    }

    public boolean isValid() {
        return !name.isEmpty();
    }
}
