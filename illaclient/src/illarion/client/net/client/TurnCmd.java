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
package illarion.client.net.client;

import illarion.client.net.CommandList;
import illarion.common.annotation.NonNull;
import illarion.common.net.NetCommWriter;
import illarion.common.types.Location;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

/**
 * This command is used to inform the server that the character turns towards a specified direction.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@ThreadSafe
public final class TurnCmd extends AbstractCommand {
    /**
     * Default constructor for the turn message.
     *
     * @param direction the direction to turn to
     */
    public TurnCmd(final int direction) {
        super(CommandList.CMD_TURN_N + direction);

        if ((direction < 0) || (direction >= Location.DIR_MOVE8)) {
            throw new IllegalArgumentException("Direction out of range: " + direction);
        }
    }

    @Override
    public void encode(@NonNull final NetCommWriter writer) {
    }

    @NonNull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Direction: " + (getId() - CommandList.CMD_TURN_N));
    }
}
