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
package illarion.client.world.items;

import de.lessvoid.nifty.tools.Color;
import illarion.client.util.Lang;
import illarion.client.world.World;
import org.jetbrains.annotations.Contract;

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
    @SuppressWarnings("ConstantConditions")
    public void updateLoad(int current, int maximum) {
        boolean oldRunningPossible = isRunningPossible();
        boolean oldWalkingPossible = isWalkingPossible();
        boolean isFirst = maximumLoad == 0;

        maximumLoad = maximum;
        currentLoad = current;

        if (World.getGameGui().isReady() && !World.getGameGui().getDialogCraftingGui().isCraftingInProgress()) {
            String messageToSend = null;
            if (isFirst && !isRunningPossible()) {
                messageToSend = Lang.getMsg("illarion.client.world.items.CarryLoad.RunningImpossible");
            } else if (oldRunningPossible != isRunningPossible()) {
                if (oldRunningPossible) {
                    messageToSend = Lang.getMsg("illarion.client.world.items.CarryLoad.RunningImpossible");
                }
                if (!isFirst && !oldRunningPossible) {
                    messageToSend = Lang.getMsg("illarion.client.world.items.CarryLoad.RunningPossible");
                }
            }
            if (isFirst && !isWalkingPossible()) {
                messageToSend = Lang.getMsg("illarion.client.world.items.CarryLoad.WalkingImpossible");
            } else if (oldWalkingPossible != isWalkingPossible()) {
                if (oldWalkingPossible) {
                    messageToSend = Lang.getMsg("illarion.client.world.items.CarryLoad.WalkingImpossible");
                }
                if (!isFirst && !oldWalkingPossible) {
                    messageToSend = Lang.getMsg("illarion.client.world.items.CarryLoad.WalkingPossible");
                }
            }
            if (messageToSend != null) {
                String finalMessageToSend = messageToSend;
                World.getUpdateTaskManager().addTask((container, delta) -> {
                    World.getGameGui().getInformGui().showScriptInform(1, finalMessageToSend);
                    World.getGameGui().getChatGui().addChatMessage(finalMessageToSend, Color.WHITE);
                });
            }
        }
        if (World.getGameGui().isReady()) {
            World.getGameGui().getInventoryGui().updateCarryLoad();
        }
    }

    @Contract(pure = true)
    public double getLoadFactor() {
        if (maximumLoad == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return (double) currentLoad / maximumLoad;
    }

    @Contract(pure = true)
    public boolean isRunningPossible() {
        return getLoadFactor() <= 0.75;
    }

    @Contract(pure = true)
    public boolean isWalkingPossible() {
        return currentLoad <= maximumLoad;
    }
}
