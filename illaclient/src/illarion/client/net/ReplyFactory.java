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
package illarion.client.net;

import illarion.client.net.annotations.ReplyMessage;
import illarion.client.net.server.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * The Factory for commands the server sends to the client. This factory creates the required message objects on
 * demand.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ReplyFactory {
    /**
     * The singleton instance of this factory.
     */
    private static final ReplyFactory INSTANCE = new ReplyFactory();

    /**
     * The logger that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ReplyFactory.class);

    /**
     * This map stores the message classes along with the IDs of the command encoded in them.
     */
    private final Map<Integer, Class<? extends AbstractReply>> replyMap;

    /**
     * The default constructor of the factory. This registers all commands.
     */
    private ReplyFactory() {
        replyMap = new HashMap<Integer, Class<? extends AbstractReply>>();

        register(AppearanceMsg.class);
        register(AttackMsg.class);
        register(AttributeMsg.class);
        register(BookMsg.class);
        register(ChangeItemMsg.class);
        register(CharacterAnimationMsg.class);
        register(CloseShowcaseMsg.class);
        register(DateTimeMsg.class);
        register(DialogCraftingMsg.class);
        register(DialogCraftingUpdateMsg.class);
        register(DialogInputMsg.class);
        register(DialogMerchantMsg.class);
        register(DialogMessageMsg.class);
        register(DialogSelectionMsg.class);
        register(DisconnectMsg.class);
        register(GraphicEffectMsg.class);
        register(InformMsg.class);
        register(IntroduceMsg.class);
        register(InventoryMsg.class);
        register(ItemUpdateMsg.class);
        register(LocationMsg.class);
        register(LookAtCharMsg.class);
        register(LookAtDialogItemMsg.class);
        register(LookAtInvMsg.class);
        register(LookAtMapItemMsg.class);
        register(LookAtShowcaseMsg.class);
        register(LookAtTileMsg.class);
        register(MagicFlagMsg.class);
        register(MapCompleteMsg.class);
        register(MapStripeMsg.class);
        register(MoveMsg.class);
        register(MusicMsg.class);
        register(PlayerIdMsg.class);
        register(PutItemMsg.class);
        register(RemoveCharMsg.class);
        register(RemoveItemMsg.class);
        register(SayMsg.class);
        register(ShowcaseMsg.class);
        register(SkillMsg.class);
        register(SoundEffectMsg.class);
        register(TargetLostMsg.class);
        register(TurnCharMsg.class);
        register(WeatherMsg.class);
    }

    /**
     * Register a class as replay message class. Those classes need to implement the {@link AbstractReply} interface
     * and they require the contain the {@link ReplyMessage} annotation.
     *
     * @param clazz the class to register as reply.
     */
    private void register(final Class<? extends AbstractReply> clazz) {
        final ReplyMessage messageData = clazz.getAnnotation(ReplyMessage.class);

        if (messageData == null) {
            LOGGER.error("Illegal class supplied to register! No annotation: " + clazz.getName());
            return;
        }

        if (replyMap.containsKey(messageData.replyId())) {
            LOGGER.error("Class with duplicated key: " + clazz.getName());
            return;
        }

        replyMap.put(messageData.replyId(), clazz);
    }

    /**
     * Get a replay instance. This class will check if there is any reply fitting the ID registered and create a new
     * instance of it.
     *
     * @param id the ID of the reply
     * @return the newly created reply instance
     */
    public AbstractReply getReply(final int id) {
        final Class<? extends AbstractReply> replyClass = replyMap.get(id);

        if (replyClass == null) {
            LOGGER.error("Illegal reply requested. ID: " + Integer.toString(id));
            return null;
        }

        try {
            return replyClass.newInstance();
        } catch (InstantiationException e) {
            LOGGER.error("Failed to create instance of reply class!", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Access to reply class constructor was denied.", e);
        }
        return null;
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public static ReplyFactory getInstance() {
        return INSTANCE;
    }
}
