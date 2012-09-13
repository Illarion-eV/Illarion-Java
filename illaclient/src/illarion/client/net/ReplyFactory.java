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

import illarion.client.net.server.*;
import illarion.common.util.RecycleFactory;
import javolution.context.PoolContext;

/**
 * The Factory for commands the server sends to the client. This factory prepares and recycles all server messages and
 * set the needed mapping.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class ReplyFactory
        extends RecycleFactory<AbstractReply> {
    /**
     * The singleton instance of this factory.
     */
    private static final ReplyFactory INSTANCE = new ReplyFactory();

    /**
     * The default constructor of the factory. This registers all commands and sets up all needed mappings.
     */
    private ReplyFactory() {
        super();

        register(new DisconnectMsg());

        register(new PlayerIdMsg());
        register(new LocationMsg());
        register(new SkillMsg());
        register(new AttributeMsg());
        register(new MagicFlagMsg());
        register(new AppearanceMsg());

        register(new InventoryMsg());
        register(new ShowcaseMsg());
        register(new CloseShowcaseMsg());

        register(new MoveMsg());
        register(new MapStripeMsg());
        register(new RemoveItemMsg());
        register(new PutItemMsg());
        register(new ChangeItemMsg());
        register(new ItemUpdateMsg());

        register(new SimpleMsg(CommandList.MSG_ATTACK));
        map(CommandList.MSG_TARGET_LOST, CommandList.MSG_ATTACK);
        map(CommandList.MSG_MAP_COMPLETE, CommandList.MSG_ATTACK);

        register(new TurnCharMsg());
        register(new RemoveCharMsg());

        register(new SayMsg());
        map(CommandList.MSG_WHISPER, CommandList.MSG_SAY);
        map(CommandList.MSG_SHOUT, CommandList.MSG_SAY);
        register(new InformMsg());
        register(new IntroduceMsg());

        register(new MusicMsg());
        register(new EffectMsg());
        map(CommandList.MSG_GRAPHIC_FX, CommandList.MSG_SOUND_FX);

        register(new LookAtInvMsg());
        register(new LookAtTileMsg());
        register(new LookAtMapItemMsg());
        register(new LookAtShowcaseMsg());
        register(new LookatCharMsg());

        register(new DateTimeMsg());
        register(new WeatherMsg());

        register(new CharacterAnimationMsg());
        register(new BookMsg());

        register(new DialogInputMsg());
        register(new DialogMessageMsg());
        register(new DialogMerchantMsg());
        register(new DialogSelectionMsg());

        finish();
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public static ReplyFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Get the AbstractReply requested from this factory. This method is implemented in addition in order to ensure the
     * execution in the PoolContext.
     */
    @Override
    public AbstractReply getCommand(final int requestId) {
        PoolContext.enter();
        try {
            return super.getCommand(requestId);
        } finally {
            PoolContext.exit();
        }
    }
}
