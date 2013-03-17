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
package illarion.client.resources.loaders;

import illarion.client.graphics.Sprite;
import illarion.client.graphics.SpriteBuffer;
import illarion.client.resources.MiscImageFactory;
import illarion.client.resources.ResourceFactory;
import illarion.client.resources.data.MiscImageTemplate;

/**
 * This class is used to load the misc images required in the graphics.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class MiscImageLoader extends AbstractResourceLoader<MiscImageTemplate> {
    /**
     * The path inside the resources where the GUI images are stored.
     */
    private static final String GUI_PATH = "data/gui/";

    /**
     * Trigger the loading sequence for this loader.
     */
    @Override
    public ResourceFactory<MiscImageTemplate> call() {
        if (!hasTargetFactory()) {
            throw new IllegalStateException("targetFactory not set yet.");
        }

        final ResourceFactory<MiscImageTemplate> factory = getTargetFactory();

        assert factory != null;
        factory.init();

        final Sprite attackMarkerSprite = SpriteBuffer.getInstance().getSprite(GUI_PATH, "attackMarker", 1, 0, 0,
                Sprite.HAlign.center, Sprite.VAlign.middle, false);
        getTargetFactory().storeResource(new MiscImageTemplate(MiscImageFactory.ATTACK_MARKER, attackMarkerSprite, 1));

        factory.loadingFinished();

        loadingDone();

        return factory;
    }
}
