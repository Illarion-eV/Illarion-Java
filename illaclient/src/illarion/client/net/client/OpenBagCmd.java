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
import net.jcip.annotations.Immutable;

/**
 * Client Command: Open the bag the character carries in the bag slot ({@link CommandList#CMD_OPEN_BAG}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class OpenBagCmd extends AbstractCommand {
    /**
     * Default constructor for the open bag command.
     */
    public OpenBagCmd() {
        super(CommandList.CMD_OPEN_BAG);
    }

    /**
     * Encode the data of this open bag command and put the values into the buffer.
     *
     * @param writer the interface that allows writing data to the network communication system
     */
    @Override
    public void encode(@NonNull final NetCommWriter writer) {
        // nothing
    }

    /**
     * Get the data of this open bag command as string.
     *
     * @return the data of this command as string
     */
    @NonNull
    @Override
    public String toString() {
        return toString(null);
    }
}
