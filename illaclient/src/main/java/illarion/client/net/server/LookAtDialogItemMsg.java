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
package illarion.client.net.server;

import illarion.client.gui.DialogType;
import illarion.client.gui.GameGui;
import illarion.client.gui.Tooltip;
import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Server message: Look at description of a tile
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@ReplyMessage(replyId = CommandList.MSG_LOOKAT_DIALOG_ITEM)
public final class LookAtDialogItemMsg implements ServerReply {
    /**
     * The type constant that means that the look at points to the primary slot of the dialog.
     */
    private static final int TYPE_PRIMARY_SLOT = 0;

    /**
     * The type constant that means that the look at points to the secondary slot of the dialog.
     */
    private static final int TYPE_SECONDARY_SLOT = 1;
    /**
     * The ID of the dialog.
     */
    private int dialogId;

    /**
     * The look at type.
     */
    private int type;

    /**
     * The ID of the slot.
     */
    private int slotId;

    /**
     * The ID of the secondary slot.
     */
    private int secondarySlotId;

    /**
     * The tool tip.
     */
    @Nullable
    private Tooltip tooltip;

    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(LookAtDialogItemMsg.class);

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        dialogId = reader.readInt();
        type = reader.readUByte();
        switch (type) {
            case TYPE_PRIMARY_SLOT:
                slotId = reader.readUByte();
                break;
            case TYPE_SECONDARY_SLOT:
                slotId = reader.readUByte();
                secondarySlotId = reader.readUByte();
                break;
            default:
                log.error("Illegal type ID: {}", type);
                return;
        }

        tooltip = new Tooltip(reader);
    }

    @Nonnull
    @Override
    public ServerReplyResult execute() {
        if (tooltip == null) {
            throw new NotDecodedException();
        }

        if (!World.getGameGui().isReady()) {
            return ServerReplyResult.Reschedule;
        }

        GameGui gui = World.getGameGui();
        switch (type) {
            case TYPE_PRIMARY_SLOT:
                gui.getDialogCraftingGui().showCraftItemTooltip(dialogId, slotId, tooltip);
                break;
            case TYPE_SECONDARY_SLOT:
                DialogType dialogType = gui.getDialogGui()
                        .getDialogType(dialogId, DialogType.Crafting, DialogType.Merchant);
                if (dialogType == null) {
                    log.warn("Failed to assign dialog item look at to a fitting dialog.");
                } else if (dialogType == DialogType.Crafting) {
                    gui.getDialogCraftingGui().showCraftIngredientTooltip(dialogId, slotId, secondarySlotId, tooltip);
                } else if (dialogType == DialogType.Merchant) {
                    gui.getDialogMerchantGui().showMerchantListTooltip(dialogId, slotId, secondarySlotId, tooltip);
                }
                break;
            default:
                log.error("Illegal type ID {}", type);
        }
        return ServerReplyResult.Success;
    }

    @Nonnull
    @Override
    @Contract(pure = true)
    public String toString() {
        return Utilities.toString(LookAtDialogItemMsg.class, "DialogID: " + dialogId, tooltip);
    }
}
