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
package illarion.client.net;

import illarion.client.net.annotations.ReplyMessage;
import illarion.client.net.server.*;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Factory for commands the server sends to the client. This factory creates the required message objects on
 * demand.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("OverlyCoupledClass")
public final class ReplyFactory {
    /**
     * The singleton instance of this factory.
     */
    @Nonnull
    private static final ReplyFactory INSTANCE = new ReplyFactory();

    /**
     * The logger that takes care for the logging output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(ReplyFactory.class);

    /**
     * This map stores the message classes along with the IDs of the command encoded in them.
     */
    @Nonnull
    private final Map<Integer, Class<? extends ServerReply>> replyMap;

    /**
     * The default constructor of the factory. This registers all commands.
     */
    @SuppressWarnings({"OverlyLongMethod", "OverlyCoupledMethod"})
    private ReplyFactory() {
        replyMap = new HashMap<>();

        register(AppearanceMsg.class);
        register(AttackMsg.class);
        register(AttributeMsg.class);
        register(BookMsg.class);
        register(CarryLoadMsg.class);
        register(ChangeItemMsg.class);
        register(CharacterAnimationMsg.class);
        register(CloseShowcaseMsg.class);
        register(CloseDialogMsg.class);
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
        register(KeepAliveMsg.class);
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
        register(QuestMsg.class);
        register(QuestDeleteMsg.class);
        register(QuestAvailabilityMsg.class);
        register(RemoveCharMsg.class);
        register(RemoveItemMsg.class);
        register(SayMsg.class);
        register(ShoutMsg.class);
        register(WhisperMsg.class);
        register(ShowcaseMsg.class);
        register(ShowcaseSingleMsg.class);
        register(SkillMsg.class);
        register(SoundEffectMsg.class);
        register(TargetLostMsg.class);
        register(TurnCharMsg.class);
        register(WeatherMsg.class);
    }

    /**
     * Register a class as replay message class. Those classes need to implement the {@link ServerReply} interface
     * and they require the contain the {@link ReplyMessage} annotation.
     *
     * @param clazz the class to register as reply.
     */
    private void register(@Nonnull Class<? extends ServerReply> clazz) {
        ReplyMessage messageData = clazz.getAnnotation(ReplyMessage.class);

        if (messageData == null) {
            log.error("Illegal class supplied to register! No annotation: {}", clazz.getName());
            return;
        }

        if (replyMap.containsKey(messageData.replyId())) {
            log.error("Class with duplicated key: {}", clazz.getName());
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
    @Nullable
    @Contract(pure = true)
    public ServerReply getReply(int id) {
        Class<? extends ServerReply> replyClass = replyMap.get(id);

        if (replyClass == null) {
            log.error("Illegal reply requested. ID: 0x{}", Integer.toHexString(id));
            return null;
        }

        try {
            return replyClass.getConstructor().newInstance();
        } catch (InstantiationException e) {
            log.error("Failed to create instance of reply class!", e);
        } catch (IllegalAccessException e) {
            log.error("Access to reply class constructor was denied.", e);
        } catch (NoSuchMethodException e) {
            log.error("Failed to locate required constructor.", e);
        } catch (InvocationTargetException e) {
            log.error("Problem while executing the constructor.", e);
        }
        return null;
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    @Contract(pure = true)
    public static ReplyFactory getInstance() {
        return INSTANCE;
    }
}
