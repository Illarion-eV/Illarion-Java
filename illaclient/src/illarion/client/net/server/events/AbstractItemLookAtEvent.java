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

import illarion.common.util.Money;

/**
 * This is the abstract item look-at event. Its awesome.
 * It stores all the values shared by the different look-at events.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AbstractItemLookAtEvent {
    /**
     * The name of the item.
     */
    private final String name;

    /**
     * The type of the item.
     */
    private final int itemType;

    /**
     * The name of the producer of the item. Can be {@code null} in case there is not producer.
     */
    private final String producer;

    /**
     * The worth of the item that is shown in the tooltip. Can be {@code null} in case this item can't ever be sold.
     */
    private final Money worth;

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
     * The level of the blackstone that is embedded in the item. Valid values from {@code 0} (no blackstone) to
     * {@code 10} (very strong blackstone)
     */
    private final int blackStoneLevel;

    /**
     * The level of the bluestone that is embedded in the item. Valid values from {@code 0} (no bluestone) to
     * {@code 10} (very strong bluestone)
     */
    private final int blueStoneLevel;

    /**
     * The level of the topaz that is embedded in the item. Valid values from {@code 0} (no topaz) to {@code 10}
     * (very strong topaz)
     */
    private final int topazLevel;

    /**
     * Default constructor that allows setting all the parameters of this class.
     *
     * @param name            the name of the item
     * @param itemType        the type constant of this item
     * @param producer        the name of the producer
     * @param worth           the worth of the item
     * @param qualityText     the text representing the quality
     * @param durabilityText  the text representing the durability
     * @param durability      the value of the durability
     * @param diamondLevel    the level of the diamond
     * @param emeraldLevel    the level of the emerald
     * @param rubyLevel       the level of the ruby
     * @param blackStoneLevel the level of the blackstone
     * @param blueStoneLevel  the level of the bluestone
     * @param topazLevel      the level of the topaz
     */
    protected AbstractItemLookAtEvent(final String name, final int itemType, final String producer, final Money worth,
                                      final String qualityText, final String durabilityText, final int durability,
                                      final int diamondLevel, final int emeraldLevel, final int rubyLevel,
                                      final int blackStoneLevel, final int blueStoneLevel, final int topazLevel) {
        this.name = name;
        this.itemType = itemType;
        this.producer = producer;
        this.worth = worth;
        this.qualityText = qualityText;
        this.durabilityText = durabilityText;
        this.durability = durability;
        this.diamondLevel = diamondLevel;
        this.emeraldLevel = emeraldLevel;
        this.rubyLevel = rubyLevel;
        this.blackStoneLevel = blackStoneLevel;
        this.blueStoneLevel = blueStoneLevel;
        this.topazLevel = topazLevel;
    }

    /**
     * Get the level of the blackstone in this item.
     *
     * @return the blackstone level
     */
    public int getBlackStoneLevel() {
        return blackStoneLevel;
    }

    /**
     * Get the level of the bluestone in this item.
     *
     * @return the bluestone level
     */
    public int getBlueStoneLevel() {
        return blueStoneLevel;
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
     * Get the type of the item.
     *
     * @return the type of the item
     */
    public int getItemType() {
        return itemType;
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
     * Get the level of the ruby in this item.
     *
     * @return the ruby level
     */
    public int getRubyLevel() {
        return rubyLevel;
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
     * Get the worth of the item.
     *
     * @return the item worth
     */
    public Money getWorth() {
        return worth;
    }
}
