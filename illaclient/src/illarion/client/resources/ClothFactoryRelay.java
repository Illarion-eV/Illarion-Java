/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2013 - Illarion e.V.
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
package illarion.client.resources;

import illarion.client.graphics.AvatarClothManager;
import illarion.client.resources.data.AvatarClothTemplate;
import illarion.client.resources.data.AvatarTemplate;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is not real resource factory. Its a relay that forwards the cloth objects to the different factories
 * that are assigned to the avatars.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ClothFactoryRelay implements ResourceFactory<AvatarClothTemplate> {
    /**
     * This list stores the avatars that received clothes. This is needed to trigger the cleanup properly.
     */
    @Nonnull
    private List<AvatarTemplate> usedAvatars;

    /**
     * Initialize the factory for loading the resources.
     */
    @Override
    public void init() {
        usedAvatars = new ArrayList<AvatarTemplate>();
    }

    /**
     * Finish the loading and optimize the factory for normal operation.
     */
    @Override
    public void loadingFinished() {
        for (final AvatarTemplate ava : usedAvatars) {
            ava.getClothes().finish();
        }
        usedAvatars.clear();
    }

    /**
     * Store a resource in the factory. In this case the resource is forwarded to the factory that is actually in
     * charge of maintaining this resource.
     */
    @Override
    public void storeResource(@Nonnull final AvatarClothTemplate resource) {
        final AvatarTemplate avatarTemplate = CharacterFactory.getInstance().getTemplate(resource.getAvatarId());
        final AvatarClothManager manager = avatarTemplate.getClothes();
        manager.addCloth(resource.getClothSlot(), resource);

        if (!usedAvatars.contains(avatarTemplate)) {
            usedAvatars.add(avatarTemplate);
        }
    }

}
