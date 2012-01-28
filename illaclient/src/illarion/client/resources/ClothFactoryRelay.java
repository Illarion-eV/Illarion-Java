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
package illarion.client.resources;

import illarion.client.graphics.Avatar;
import illarion.client.graphics.AvatarCloth;
import illarion.client.graphics.AvatarClothManager;
import javolution.util.FastTable;

import java.util.List;

/**
 * This class is not real resource factory. Its a relay that forwards the cloth
 * objects to the different factories that are assigned to the avatars.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ClothFactoryRelay implements ResourceFactory<AvatarCloth> {
    /**
     * This list stores the avatars that received clothes. This is needed to
     * trigger the cleanup properly.
     */
    private List<Avatar> usedAvatars;

    /**
     * Initialize the factory for loading the resources.
     */
    @Override
    public void init() {
        usedAvatars = FastTable.newInstance();
    }

    /**
     * Finish the loading and optimize the factory for normal operation.
     */
    @Override
    public void loadingFinished() {
        for (final Avatar ava : usedAvatars) {
            ava.getClothes().finish();
        }
        usedAvatars.clear();
        FastTable.recycle((FastTable<Avatar>) usedAvatars);
        usedAvatars = null;
    }

    /**
     * Store a resource in the factory. In this case the resource is forwarded
     * to the factory that is actually in charge of maintaining this resource.
     */
    @Override
    public void storeResource(final AvatarCloth resource) {
        final Avatar parentAva =
            CharacterFactory.getInstance()
                .getPrototype(resource.getAvatarId());
        final AvatarClothManager manager = parentAva.getClothes();
        manager.addCloth(resource.getLocationId(), resource);

        if (!usedAvatars.contains(parentAva)) {
            usedAvatars.add(parentAva);
        }
    }

}
