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
package illarion.client.net.server;

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.resources.SoundFactory;
import illarion.client.util.UpdateTask;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.illarion.engine.GameContainer;
import org.illarion.engine.sound.Sound;
import org.illarion.engine.sound.Sounds;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * Servermessage: Sound effect ( {@link CommandList#MSG_SOUND_FX}.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
@ReplyMessage(replyId = CommandList.MSG_SOUND_FX)
public final class SoundEffectMsg extends AbstractReply implements UpdateTask {
    /**
     * ID of the effect that shall be shown.
     */
    private int effectId;

    /**
     * The location the effect occurs on.
     */
    private transient Location loc;

    /**
     * Decode the effect data the receiver got and prepare it for the execution.
     *
     * @param reader the receiver that got the data from the server that needs to be decoded
     * @throws IOException thrown in case there was not enough data received to decode the full message
     */
    @Override
    public void decode(@Nonnull final NetCommReader reader)
            throws IOException {
        loc = decodeLocation(reader);
        effectId = reader.readUShort();
    }

    /**
     * The logging instance of this class..
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SoundEffectMsg.class);

    /**
     * Execute the effect message and send the decoded data to the rest of the client.
     *
     * @return true if the execution is done, false if it shall be called again
     */
    @Override
    public boolean executeUpdate() {
        World.getUpdateTaskManager().addTask(this);
        return true;
    }

    @Override
    public void onUpdateGame(@Nonnull final GameContainer container, final int delta) {
        final Location plyLoc = World.getPlayer().getLocation();
        final Sound sound = SoundFactory.getInstance().getSound(effectId, container.getEngine().getAssets().getSoundsManager());
        if (sound == null) {
            return;
        }
        final Sounds sounds = container.getEngine().getSounds();
        sounds.playSound(sound, sounds.getSoundVolume(), loc.getScX() - plyLoc.getScX(),
                loc.getScY() - plyLoc.getScY(), loc.getScZ() - plyLoc.getScZ());
    }

    /**
     * Get the data of this effect message as string.
     *
     * @return the string that contains the values that were decoded for this message
     */
    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Sound: " + effectId);
    }
}
