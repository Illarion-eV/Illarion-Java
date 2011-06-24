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
package illarion.client.guiNG.references;

import illarion.client.net.NetCommWriter;
import illarion.client.world.Char;
import illarion.client.world.Game;

/**
 * This is the reference to a character. Such references are used to handle the
 * dragging effects correctly.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class CharReference extends AbstractReference {
    /**
     * The id of the character this reference currently refers to.
     */
    private long charId;

    /**
     * Constructor to create a new instance of a character reference.
     */
    public CharReference() {
        super(AbstractReference.CHARACTER);
    }

    /**
     * Encode the needed data for a use on this character.
     * 
     * @param writer the interface to write data on the network
     */
    @Override
    public void encodeUse(final NetCommWriter writer) {
        final Char refChar = Game.getPeople().getCharacter(charId);
        if (refChar == null) {
            return;
        }
        writer.writeByte((byte) AbstractReference.MAP);
        writer.writeLocation(refChar.getLocation());
    }

    /**
     * Get the Id of the character this reference is pointing to.
     * 
     * @return the character id
     */
    public long getReferringCharacter() {
        return charId;
    }

    /**
     * Set the ID of the character this reference is pointing to.
     * 
     * @param newCharId the id of the character this reference is pointing to
     */
    public void setReferringCharacter(final long newCharId) {
        charId = newCharId;
    }

}
