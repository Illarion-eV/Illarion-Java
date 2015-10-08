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
package illarion.client.resources;

import illarion.client.graphics.AvatarClothManager;
import illarion.client.resources.data.AvatarClothTemplate;
import illarion.client.resources.data.AvatarTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nullable
    private List<AvatarTemplate> usedAvatars;

    /**
     * Initialize the factory for loading the resources.
     */
    @Override
    public void init() {
        usedAvatars = new ArrayList<>();
    }

    /**
     * Finish the loading and optimize the factory for normal operation.
     */
    @Override
    public void loadingFinished() {
        if (usedAvatars == null) {
            throw new IllegalStateException("Loading of this factory was already finished.");
        }

        for (AvatarTemplate ava : usedAvatars) {
            ava.getClothes().finish();
        }
        usedAvatars.clear();
        usedAvatars = null;
    }

    /**
     * Store a resource in the factory. In this case the resource is forwarded to the factory that is actually in
     * charge of maintaining this resource.
     */
    @Override
    public void storeResource(@Nonnull AvatarClothTemplate resource) {
        if (usedAvatars == null) {
            throw new IllegalStateException("Loading of this factory was already finished.");
        }

        AvatarTemplate avatarTemplate = CharacterFactory.getInstance().getTemplate(resource.getAvatarId());
        AvatarClothManager manager = avatarTemplate.getClothes();
        manager.addCloth(resource.getClothGroup(), resource);

        if (!usedAvatars.contains(avatarTemplate)) {
            usedAvatars.add(avatarTemplate);
        }
    }
}
