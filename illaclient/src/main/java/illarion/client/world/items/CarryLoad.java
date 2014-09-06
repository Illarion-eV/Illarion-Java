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
package illarion.client.world.items;

import de.lessvoid.nifty.tools.Color;
import illarion.client.util.Lang;
import illarion.client.util.UpdateTask;
import illarion.client.world.World;
import org.illarion.engine.GameContainer;

import javax.annotation.Nonnull;

/**
 * This class stores and maintains the current carry load and provides some methods to easily handle the load values.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class CarryLoad {
    private int maximumLoad;
    private int currentLoad;

    /**
     * Update the current and the maximum load values. This function is supposed to be triggered by the server in
     * case the load values change due to item movement or attribute change.
     *
     * @param current the new current load value
     * @param maximum the maximum load value
     */
    public void updateLoad(int current, int maximum) {
        boolean oldRunningPossible = isRunningPossible();
        boolean oldWalkingPossible = isWalkingPossible();
        boolean isFirst = (maximumLoad == 0);

        maximumLoad = maximum;
        currentLoad = current;

        String messageToSend = null;
        //noinspection ConstantConditions
        if (isFirst && !isRunningPossible()) {
            messageToSend = Lang.getMsg("illarion.client.world.items.CarryLoad.RunningImpossible");
        }
        if (oldRunningPossible != isRunningPossible()) {
            if (oldRunningPossible) {
                messageToSend = Lang.getMsg("illarion.client.world.items.CarryLoad.RunningImpossible");
            }
            if (!isFirst && !oldRunningPossible) {
                messageToSend = Lang.getMsg("illarion.client.world.items.CarryLoad.RunningPossible");
            }
        }
        if (isFirst && !isWalkingPossible()) {
            messageToSend = Lang.getMsg("illarion.client.world.items.CarryLoad.WalkingImpossible");
        }
        if (oldWalkingPossible != isWalkingPossible()) {
            if (oldWalkingPossible) {
                messageToSend = Lang.getMsg("illarion.client.world.items.CarryLoad.WalkingImpossible");
            }
            if (!isFirst && !oldWalkingPossible) {
                messageToSend = Lang.getMsg("illarion.client.world.items.CarryLoad.WalkingPossible");
            }
        }
        if (messageToSend != null) {
            final String finalMessageToSend = messageToSend;
            World.getUpdateTaskManager().addTask(new UpdateTask() {
                @Override
                public void onUpdateGame(@Nonnull GameContainer container, int delta) {
                    World.getGameGui().getInformGui().showScriptInform(1, finalMessageToSend);
                    World.getGameGui().getChatGui().addChatMessage(finalMessageToSend, Color.WHITE);
                }
            });
        }
    }

    public double getLoadFactor() {
        if (maximumLoad == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return (double) currentLoad / maximumLoad;
    }

    public boolean isRunningPossible() {
        return getLoadFactor() <= 0.75;
    }

    public boolean isWalkingPossible() {
        return currentLoad <= maximumLoad;
    }
}
