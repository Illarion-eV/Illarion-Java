/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.net.client;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * This command is used to inform the server that a trading dialog was closed.
 *
 * @author Martin Karing &gt;nitram@illarion.org&lt;
 */
@Immutable
public final class CloseDialogTradingCmd extends AbstractTradeItemCmd {
    /**
     * The sub command ID for this command.
     */
    private static final int SUB_CMD_ID = 0;

    /**
     * Default constructor for the close crafting dialog command.
     *
     * @param dialogId the ID of the dialog to close
     */
    public CloseDialogTradingCmd(int dialogId) {
        super(dialogId, SUB_CMD_ID);
    }

    @Nonnull
    @Override
    public String toString() {
        return toString(super.toString());
    }
}
