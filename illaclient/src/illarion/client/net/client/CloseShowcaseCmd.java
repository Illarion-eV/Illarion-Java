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
 * Client Command: Close a opened container
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Immutable
public final class CloseShowcaseCmd extends AbstractCommand {
    /**
     * The ID of the container.
     */
    private final int showcaseId;

    /**
     * Default constructor for the open bag command.
     *
     * @param showcaseId the ID of the showcase to close
     */
    public CloseShowcaseCmd(final int showcaseId) {
        super(CommandList.CMD_CLOSE_SHOWCASE);

        this.showcaseId = showcaseId;
    }

    @Override
    public void encode(@NonNull final NetCommWriter writer) {
        writer.writeByte((byte) showcaseId);
    }

    @NonNull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Showcase: " + showcaseId);
    }
}
