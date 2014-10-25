/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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

import illarion.client.net.CommandList;
import illarion.client.net.annotations.ReplyMessage;
import illarion.client.resources.SoundFactory;
import illarion.client.util.UpdateTask;
import illarion.client.world.World;
import illarion.common.net.NetCommReader;
import illarion.common.types.Location;
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

    @Override
    public void decode(@Nonnull NetCommReader reader) throws IOException {
        loc = decodeLocation(reader);
        effectId = reader.readUShort();
    }

    @Override
    public void executeUpdate() {
        World.getUpdateTaskManager().addTask(this);
    }

    @Override
    public void onUpdateGame(@Nonnull GameContainer container, int delta) {
        Location plyLoc = World.getPlayer().getLocation();
        Sound sound = SoundFactory.getInstance()
                .getSound(effectId, container.getEngine().getAssets().getSoundsManager());
        if (sound == null) {
            return;
        }
        Sounds sounds = container.getEngine().getSounds();
        sounds.playSound(sound, sounds.getSoundVolume(), loc.getScX() - plyLoc.getScX(), loc.getScY() - plyLoc.getScY(),
                         loc.getScZ() - plyLoc.getScZ());
    }

    @Nonnull
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Sound: " + effectId);
    }
}
