/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.client;

import javolution.text.TextBuilder;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommWriter;

/**
 * Client Command: Cast a spell (
 * {@link illarion.client.net.CommandList#CMD_CAST}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class CastCmd extends AbstractCommand {
    /**
     * The ID of the spell, made of the runes selected to cast the spell.
     */
    private long spell;

    /**
     * The target of the spell. Could be a char or a tile or something like
     * this.
     */
    // private transient Reference target;

    /**
     * Default constructor of this casting command.
     */
    public CastCmd() {
        super(CommandList.CMD_CAST);
    }

    /**
     * Create a duplicate of this casting character command.
     * 
     * @return new instance of this command
     */
    @Override
    public CastCmd clone() {
        return new CastCmd();
    }

    /**
     * Encode the data of this casting command and put the values into the
     * buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeUInt(spell);

        // add use info
        // encode target
        // if (target != null) {
        // target.encode(writer);
        // } else {
        // writer.writeByte(TARGET_NULL_CONST);
        // }
    }

    /**
     * Clean up the stored references in this cast spell command before put it
     * back into the recycler.
     */
    @Override
    public void reset() {
        // target = null;
    }

    /**
     * Set the ID of the spell that is casted. Every byte set in this ID stands
     * for a selected rune.
     * 
     * @param castSpell the ID of the spell that is casted that shall be send by
     *            this command
     */
    public void setSpell(final long castSpell) {
        spell = castSpell;
    }

    /**
     * Set the target of the spell that is casted.
     * 
     * @param execute the target of the spell that is casted
     */
    // public void setTarget(final Reference execute) {
    // target = execute;
    // }

    /**
     * Get the data of this casting command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final TextBuilder builder = TextBuilder.newInstance();
        builder.append("Spell: ");
        builder.append(spell);
        // builder.append(" - Target: ");
        // if (target == null) {
        // builder.append("NULL");
        // } else {
        // builder.append(target.toString());
        // }

        final String result = toString(builder.toString());
        TextBuilder.recycle(builder);
        return result;
    }
}
