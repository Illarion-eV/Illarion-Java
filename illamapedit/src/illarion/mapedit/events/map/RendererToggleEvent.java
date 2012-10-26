/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Mapeditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Mapeditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.events.map;

import illarion.mapedit.render.AbstractMapRenderer;
import illarion.mapedit.render.RendererManager;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Tim
 */
public class RendererToggleEvent {
    /**
     * The logger instance for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(RendererToggleEvent.class);
    private final Class<? extends AbstractMapRenderer> renderer;

    public RendererToggleEvent(final Class<? extends AbstractMapRenderer> renderer) {
        this.renderer = renderer;
    }

    public Class<? extends AbstractMapRenderer> getRendererClass() {
        return renderer;
    }

    public AbstractMapRenderer getRenderer(final RendererManager manager) {
        try {
            return renderer.getConstructor(manager.getClass()).newInstance(manager);
        } catch (InvocationTargetException e) {
            LOGGER.error("Can't instantiate " + renderer, e);
        } catch (NoSuchMethodException e) {
            LOGGER.error("Can't instantiate " + renderer, e);
        } catch (InstantiationException e) {
            LOGGER.error("Can't instantiate " + renderer, e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Can't instantiate " + renderer, e);
        }
        return null;
    }
}
