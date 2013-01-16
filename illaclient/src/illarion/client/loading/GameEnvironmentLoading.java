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

import illarion.client.graphics.FontLoader;
import illarion.client.world.World;
import org.newdawn.slick.loading.DeferredResource;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * This loading task takes care for loading the components of the game environment that still need to be loaded.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GameEnvironmentLoading implements DeferredResource {
    /**
     * Load the game environment.
     */
    @Override
    public void load() throws IOException {
        World.initMissing();
        FontLoader.getInstance().prepareAllFonts();
    }

    /**
     * The human readable description of this loading task.
     */
    @Nullable
    @Override
    public String getDescription() {
        return null;
    }
}
