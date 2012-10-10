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
 * This event is generated in case a look at event for a dialog is received.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogSecondaryItemLookAtEvent extends AbstractItemLookAtEvent {
    /**
     * The dialog ID this event refers to.
     */
    private final int dialogId;

    /**
     * The primary slot in the dialog.
     */
    private final int slot;

    /**
     * The second slot in the dialog that is marked.
     */
    private final int secondarySlot;

    /**
     * Default constructor that allows setting all the parameters of this class.
     *
     * @param dialogId       the id of the dialog
     * @param slot           the slot of the item to look at
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
    public DialogSecondaryItemLookAtEvent(final int dialogId, final int slot, final int secondarySlot,
                                          final String name, final int rareness, final String description,
                                          final String producer, final Money worth, final int weight,
                                          final String qualityText, final String durabilityText, final int durability,
                                          final int amethystLevel, final int diamondLevel, final int emeraldLevel,
                                          final int rubyLevel, final int obsidianLevel, final int sapphireLevel,
                                          final int topazLevel, final int bonus) {
        super(name, rareness, description, producer, worth, weight, qualityText, durabilityText, durability,
                amethystLevel, diamondLevel, emeraldLevel, rubyLevel, obsidianLevel, sapphireLevel, topazLevel, bonus);
        this.dialogId = dialogId;
        this.slot = slot;
        this.secondarySlot = secondarySlot;
    }

    /**
     * Get the dialog id.
     *
     * @return the dialog id
     */
    public int getDialogId() {
        return dialogId;
    }

    /**
     * Get the dialog slot.
     *
     * @return the dialog slot
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Get the secondary dialog slot.
     *
     * @return the secondary dialog slot
     */
    public int getSecondarySlot() {
        return secondarySlot;
    }
}
