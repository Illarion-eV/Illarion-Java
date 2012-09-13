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

import illarion.common.util.Location;
import illarion.common.util.Money;

/**
 * This event is generated in case the client receives a tooltip for a map item from the server.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MapItemLookAtEvent extends AbstractItemLookAtEvent {
    /**
     * The location on the map where the item the player is looking at is located.
     */
    private final Location location;

    /**
     * Default constructor that allows setting all the parameters of this class.
     *
     * @param location       the location on the map where the item is located
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
    public MapItemLookAtEvent(final Location location, final String name, final int rareness, final String description,
                              final String producer, final Money worth, final int weight,
                              final String qualityText, final String durabilityText, final int durability,
                              final int amethystLevel, final int diamondLevel, final int emeraldLevel,
                              final int rubyLevel, final int obsidianLevel, final int sapphireLevel,
                              final int topazLevel, final int bonus) {
        super(name, rareness, description, producer, worth, weight, qualityText, durabilityText, durability,
                amethystLevel, diamondLevel, emeraldLevel, rubyLevel, obsidianLevel, sapphireLevel, topazLevel, bonus);
        this.location = location;
    }

    /**
     * Get the location on the map where the item is located.
     *
     * @return the location of the item
     */
    public Location getLocation() {
        return location;
    }
}
