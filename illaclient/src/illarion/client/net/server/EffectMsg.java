/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.net.server;

import java.io.IOException;

import illarion.client.net.CommandList;
import illarion.client.net.NetCommReader;
import illarion.client.sound.SoundFactory;
import illarion.client.world.Game;
import illarion.client.world.MapTile;

import illarion.common.util.Location;

import illarion.sound.SoundManager;
import illarion.sound.SoundSource;

/**
 * Servermessage: Sound or graphic effect (
 * {@link illarion.client.net.CommandList#MSG_SOUND_FX},
 * {@link illarion.client.net.CommandList#MSG_GRAPHIC_FX}).
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.92
 * @version 1.22
 */
public final class EffectMsg extends AbstractReply {
    /**
     * ID of the effect that shall be shown.
     */
    private int effectId;

    /**
     * The location the effect occurs on.
     */
    private transient Location loc;

    /**
     * Default constructor for the effect message.
     */
    public EffectMsg() {
        super(CommandList.MSG_SOUND_FX);
    }

    /**
     * Create a new instance of the effect message as recycle object.
     * 
     * @return a new instance of this message object
     */
    @Override
    public EffectMsg clone() {
        return new EffectMsg();
    }

    /**
     * Decode the effect data the receiver got and prepare it for the execution.
     * 
     * @param reader the receiver that got the data from the server that needs
     *            to be decoded
     * @throws IOException thrown in case there was not enough data received to
     *             decode the full message
     */
    @Override
    public void decode(final NetCommReader reader) throws IOException {
        loc = decodeLocation(reader);
        effectId = reader.readUShort();
    }

    /**
     * Execute the effect message and send the decoded data to the rest of the
     * client.
     * 
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        if (getId() == CommandList.MSG_SOUND_FX) {
            final SoundSource effect =
                SoundManager.getInstance().getSoundSource();
            effect.setSoundClip(SoundFactory.getInstance().getSound(effectId));
            effect.setPosition(loc);
            effect.setEndOperation(SoundSource.OP_RECYCLE);
            effect.setType(SoundSource.TYPE_EFFECT);
            effect.start();
        } else {
            final MapTile tile = Game.getMap().getMapAt(loc);
            if (tile != null) {
                tile.showEffect(effectId);
            }

        }
        return true;
    }

    /**
     * Clean up all references that are not needed anymore.
     */
    @Override
    public void reset() {
        if (loc != null) {
            loc.recycle();
            loc = null;
        }
    }

    /**
     * Get the data of this effect message as string.
     * 
     * @return the string that contains the values that were decoded for this
     *         message
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        if (getId() == CommandList.MSG_SOUND_FX) {
            return toString("Sound: " + effectId);
        }
        return toString("Effect: " + effectId);
    }
}
