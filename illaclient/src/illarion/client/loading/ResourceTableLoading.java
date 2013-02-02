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

import illarion.client.graphics.shader.ShaderManager;
import illarion.client.resources.*;
import illarion.client.resources.loaders.*;
import illarion.client.util.GlobalExecutorService;
import org.newdawn.slick.loading.DeferredResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to allow the loading sequence of the client to load
 * the resource tables.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ResourceTableLoading implements DeferredResource {
    /**
     * Perform the loading.
     */
    @Override
    public void load() throws IOException {
        final List<AbstractResourceLoader<? extends Resource>> taskList = new ArrayList<AbstractResourceLoader<? extends Resource>>();

        taskList.add(new TileLoader().setTarget(TileFactory.getInstance()));
        taskList.add(new OverlayLoader().setTarget(OverlayFactory.getInstance()));
        taskList.add(new ItemLoader().setTarget(ItemFactory.getInstance()));
        taskList.add(new CharacterLoader().setTarget(CharacterFactory.getInstance()));
        taskList.add(new ClothLoader().setTarget(new ClothFactoryRelay()));
        taskList.add(new EffectLoader().setTarget(EffectFactory.getInstance()));
        taskList.add(new MiscImageLoader().setTarget(MiscImageFactory.getInstance()));
        taskList.add(new BookLoader().setTarget(BookFactory.getInstance()));

        try {
            GlobalExecutorService.getService().invokeAll(taskList);
        } catch (@Nonnull final InterruptedException e) {
            throw new IOException(e);
        }

        ShaderManager.getInstance().load();
    }

    /**
     * Get a human readable description for this task.
     */
    @Nullable
    @Override
    public String getDescription() {
        return null;
    }

}
