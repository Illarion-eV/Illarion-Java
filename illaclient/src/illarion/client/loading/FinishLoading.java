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

import illarion.common.graphics.SpriteBuffer;
import org.newdawn.slick.loading.DeferredResource;

import java.io.IOException;

/**
 * The finishing task for the loading sequence. This one should be called as
 * the last one during the loading sequence.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class FinishLoading implements DeferredResource {
    /**
     * Perform the finishing tasks of the texture loading.
     */
    @Override
    public void load() throws IOException {
        SpriteBuffer.getInstance().cleanup();
    }

    /**
     * The human readable description of this task.
     */
    @Override
    public String getDescription() {
        return null;
    }

}
