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
package illarion.client.graphics.shader;

import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

/**
 * This class manages the instances of the shader and provides access to them.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ShaderManager {
    /**
     * The instance of the logger that handles the logging output for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ShaderManager.class);

    /**
     * The singleton instance of the manager.
     */
    private static final ShaderManager INSTANCE = new ShaderManager();

    /**
     * The map that contains the load shader.
     */
    @Nonnull
    private final Map<Shader, AbstractShader> shaderMap;

    /**
     * Default constructor.
     */
    private ShaderManager() {
        shaderMap = new EnumMap<Shader, AbstractShader>(Shader.class);
    }

    /**
     * Get the singleton instance of this manager.
     *
     * @return the singleton instance
     */
    @Nonnull
    public static ShaderManager getInstance() {
        return INSTANCE;
    }

    /**
     * Get a shader that is already load.
     *
     * @param shader      the shader to load
     * @param shaderClass the class of the shader
     * @param <T>         the type of the shader
     * @return the shader or {@code null} in case the shader is not load yet
     */
    public static <T extends AbstractShader> T getShader(final Shader shader, @Nonnull final Class<T> shaderClass) {
        return shaderClass.cast(INSTANCE.shaderMap.get(shader));
    }

    /**
     * Load all the known shader.
     */
    public void load() {
        if (!shaderMap.isEmpty()) {
            return;
        }

        for (final Shader shader : Shader.values()) {
            try {
                shaderMap.put(shader, shader.getShaderClass().newInstance());
                System.out.println("Prepared shader: " + shader.name());
            } catch (@Nonnull final InstantiationException e) {
                LOGGER.error("Failed to create shader instance!", e);
            } catch (@Nonnull final IllegalAccessException e) {
                LOGGER.error("Failed to create shader instance!", e);
            } catch (@Nonnull final Exception e) {
                LOGGER.error("Failed to create shader instance!", e);
            }
        }
    }
}
