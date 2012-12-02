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

/**
 * This enumerator contains the list of shader available.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public enum Shader {
    /**
     * The fog shader.
     */
    Fog(FogShader.class),

    /**
     * The mini map shader.
     */
    MiniMap(MiniMapShader.class);

    /**
     * The class of the shader.
     */
    Class<? extends AbstractShader> shaderClass;

    /**
     * Default constructor that allows setting the class that is assigned to the shader.
     *
     * @param shaderClass the class of the shader
     */
    Shader(final Class<? extends AbstractShader> shaderClass) {
        this.shaderClass = shaderClass;
    }

    /**
     * Get the class of the shader.
     *
     * @return the class of the shader
     */
    public Class<? extends AbstractShader> getShaderClass() {
        return shaderClass;
    }
}
