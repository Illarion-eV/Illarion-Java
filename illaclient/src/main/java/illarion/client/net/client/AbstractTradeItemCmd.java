/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
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
import illarion.common.net.NetCommWriter;

import javax.annotation.Nonnull;

/**
 * This abstract command is shared by all commands that refer to a trading dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractTradeItemCmd extends AbstractCommand {
    /**
     * The ID of the referenced trading dialog
     */
    private final int dialogId;

    /**
     * The ID of the sub-command.
     */
    private final byte subCommandId;

    /**
     * Default constructor for the trade item command.
     *
     * @param dialogId     the ID of the dialog to buy the item from
     * @param subCommandId the ID of the sub command
     */
    public AbstractTradeItemCmd(final int dialogId, final int subCommandId) {
        super(CommandList.CMD_TRADE_ITEM);

        this.dialogId = dialogId;
        this.subCommandId = (byte) subCommandId;
    }

    @Override
    public void encode(@Nonnull final NetCommWriter writer) {
        writer.writeInt(dialogId);
        writer.writeByte(subCommandId);
    }

    @Nonnull
    @Override
    public String toString() {
        return "dialog ID: " + dialogId;
    }
}
