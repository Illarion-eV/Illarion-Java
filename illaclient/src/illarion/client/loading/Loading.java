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
package illarion.client.loading;

import org.newdawn.slick.loading.LoadingList;

import illarion.common.graphics.TextureLoader;

/**
 * This class is used to create the list of things that need to be loaded before the game is able to start.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Loading {
    /**
     * This variable is set to true in case the elements got enlisted already and this class must not do anything
     * anymore.
     */
    private static boolean loadingDone = false;

    public static void enlistMissingComponents() {
        if (!loadingDone) {
            while (!TextureLoader.getInstance().preloadAtlasTextures()) {
            }
            ;
            LoadingList.get().add(new ResourceTableLoading());
            LoadingList.get().add(new GameEnvironmentLoading());
            loadingDone = true;
        }

        LoadingList.get().add(new FinishLoading());
    }
}
