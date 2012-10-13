/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
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
package illarion.client.net.server.events;

import illarion.common.types.Money;

/**
 * This is the abstract item look-at event. Its awesome.
 * It stores all the values shared by the different look-at events.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AbstractItemLookAtEvent {
    public static final int RARENESS_COMMON = 1;
    public static final int RARENESS_UNCOMMON = 2;
    public static final int RARENESS_RARE = 3;
    public static final int RARENESS_EPIC = 4;

    /**
     * The name of the item.
     */
    private final String name;

    /**
     * The type of the item.
     */
    private final int rareness;

    /**
     * The description of the item.
     */
    private final String description;

    /**
     * The name of the producer of the item. Can be {@code null} in case there is not producer.
     */
    private final String producer;

    /**
     * The worth of the item that is shown in the tooltip. Can be {@code null} in case this item can't ever be sold.
     */
    private final Money worth;

    /**
     * The weight of the item.
     */
    private final int weight;

    /**
     * The text that represents the quality. Can be {@code null} in case this item doesn't have a quality.
     */
    private final String qualityText;

    /**
     * The text that represents the durability. Can be {@code null} in case this item doesn't have a durability.
     */
    private final String durabilityText;

    /**
     * The level of the durability to show. Should be {@code 0} in case this item doesn't have a durability.
     */
    private final int durability;

    /**
     * The level of the amethyst that is embedded in the item. Valid values from {@code 0} (no amethyst) to {@code 10}
     * (very strong amethyst)
     */
    private final int amethystLevel;

    /**
     * The level of the diamond that is embedded in the item. Valid values from {@code 0} (no diamond) to {@code 10}
     * (very strong diamond)
     */
    private final int diamondLevel;

    /**
     * The level of the emerald that is embedded in the item. Valid values from {@code 0} (no emerald) to {@code 10}
     * (very strong emerald)
     */
    private final int emeraldLevel;

    /**
     * The level of the ruby that is embedded in the item. Valid values from {@code 0} (no ruby) to {@code 10} (very
     * strong ruby)
     */
    private final int rubyLevel;

    /**
     * The level of the obsidian that is embedded in the item. Valid values from {@code 0} (no obsidian) to
     * {@code 10} (very strong obsidian)
     */
    private final int obsidianLevel;

    /**
     * The level of the sapphire that is embedded in the item. Valid values from {@code 0} (no sapphire) to
     * {@code 10} (very strong sapphire)
     */
    private final int sapphireLevel;

    /**
     * The level of the topaz that is embedded in the item. Valid values from {@code 0} (no topaz) to {@code 10}
     * (very strong topaz)
     */
    private final int topazLevel;

    /**
     * The bonus that is granted by the gems.
     */
    private final int bonus;

    /**
     * Default constructor that allows setting all the parameters of this class.
     *
     * @param name           the name of the item
     * @param rareness       the type constant of this item
     * @param description    the description of this item
     * @param producer       the name of the producer
     * @param worth          the worth of the item
     * @param weight         the weight of this item
     * @param qualityText    the text representing the quality
     * @param durabilityText the text representing the durability
     * @param durability     the value of the durability
     * @param amethystLevel  the level of the amethyst
     * @param diamondLevel   the level of the diamond
     * @param emeraldLevel   the level of the emerald
     * @param rubyLevel      the level of the ruby
     * @param obsidianLevel  the level of the obsidian
     * @param sapphireLevel  the level of the sapphire
     * @param topazLevel     the level of the topaz
     * @param bonus          the bonus that is granted by the gems in this item
     */
    protected AbstractItemLookAtEvent(final String name, final int rareness, final String description,
                                      final String producer, final Money worth, final int weight,
                                      final String qualityText, final String durabilityText, final int durability,
                                      final int amethystLevel, final int diamondLevel, final int emeraldLevel,
                                      final int rubyLevel, final int obsidianLevel, final int sapphireLevel,
                                      final int topazLevel, final int bonus) {
        this.name = name;
        this.rareness = rareness;
        this.description = description;
        this.producer = producer;
        this.worth = worth;
        this.weight = weight;
        this.qualityText = qualityText;
        this.durabilityText = durabilityText;
        this.durability = durability;
        this.amethystLevel = amethystLevel;
        this.diamondLevel = diamondLevel;
        this.emeraldLevel = emeraldLevel;
        this.rubyLevel = rubyLevel;
        this.obsidianLevel = obsidianLevel;
        this.sapphireLevel = sapphireLevel;
        this.topazLevel = topazLevel;
        this.bonus = bonus;
    }

    /**
     * Get the level of the amethyst in this item.
     *
     * @return the amethyst level
     */
    public int getAmethystLevel() {
        return amethystLevel;
    }

    /**
     * Get the bonus that is granted by the gems in this item.
     *
     * @return the gem bonus
     */
    public int getBonus() {
        return bonus;
    }

    /**
     * Get the item description.
     *
     * @return the item description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the level of the diamond in this item.
     *
     * @return the diamond level
     */
    public int getDiamondLevel() {
        return diamondLevel;
    }

    /**
     * Get the durability of the item.
     *
     * @return the durability
     */
    public int getDurability() {
        return durability;
    }

    /**
     * Get the text representation of the durability of the item.
     *
     * @return the durability text
     */
    public String getDurabilityText() {
        return durabilityText;
    }

    /**
     * Get the level of the emerald in this item.
     *
     * @return the emerald level
     */
    public int getEmeraldLevel() {
        return emeraldLevel;
    }

    /**
     * Get the name of the item.
     *
     * @return the name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * Get the level of the blackstone in this item.
     *
     * @return the blackstone level
     */
    public int getObsidianLevel() {
        return obsidianLevel;
    }

    /**
     * Get the producer of the item.
     *
     * @return the producer of the item
     */
    public String getProducer() {
        return producer;
    }

    /**
     * Get the quality text for this item.
     *
     * @return the quality text
     */
    public String getQualityText() {
        return qualityText;
    }

    /**
     * Get the type of the item.
     *
     * @return the type of the item
     */
    public int getRareness() {
        return rareness;
    }

    /**
     * Get the level of the ruby in this item.
     *
     * @return the ruby level
     */
    public int getRubyLevel() {
        return rubyLevel;
    }

    /**
     * Get the level of the bluestone in this item.
     *
     * @return the bluestone level
     */
    public int getSapphireLevel() {
        return sapphireLevel;
    }

    /**
     * Get the level of the topaz in this item.
     *
     * @return the topaz level
     */
    public int getTopazLevel() {
        return topazLevel;
    }

    /**
     * Get the weight of the item.
     *
     * @return the weight of the item
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Get the worth of the item.
     *
     * @return the item worth
     */
    public Money getWorth() {
        return worth;
    }
}
