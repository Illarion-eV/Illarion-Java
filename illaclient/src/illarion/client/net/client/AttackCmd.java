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
import illarion.common.types.CharacterId;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.NotThreadSafe;

/**
 * Client Command: Attacking a character ({@link CommandList#CMD_ATTACK}).
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
@NotThreadSafe
public final class AttackCmd extends AbstractCommand {
    /**
     * The ID of the character that shall be attacked.
     */
    @NonNull
    private final CharacterId charId;

    /**
     * The constructor of this command.
     *
     * @param targetCharId the ID of the character that is attacked
     */
    public AttackCmd(@NonNull final CharacterId targetCharId) {
        super(CommandList.CMD_ATTACK);

        charId = targetCharId;
    }

    @Override
    public void encode(@NonNull final NetCommWriter writer) {
        charId.encode(writer);
    }

    @NonNull
    @Override
    public String toString() {
        return toString(charId.toString());
    }
}
