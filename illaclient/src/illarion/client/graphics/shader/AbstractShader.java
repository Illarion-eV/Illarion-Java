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

import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.shader.ShaderProgram;

/**
 * This is the base class for the different shader. It takes care for loading them and provides basic access.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class AbstractShader {
    /**
     * The root directory where all shader are located.
     */
    private static final String SHADER_ROOT = "illarion/client/graphics/shader/";

    /**
     * The load shader program.
     */
    private final ShaderProgram shader;

    /**
     * Default constructor that creates the shader.
     *
     * @param vertex   the file name of the vertex shader
     * @param fragment the file name of the fragment shader
     * @throws SlickException thrown in case loading the shader fails
     */
    protected AbstractShader(final String vertex, final String fragment) throws SlickException {
        shader = ShaderProgram.loadProgram(SHADER_ROOT + vertex, SHADER_ROOT + fragment);
    }

    /**
     * Get the load shader.
     *
     * @return the shader
     */
    protected ShaderProgram getShader() {
        return shader;
    }

    /**
     * Bind the shader.
     */
    public final void bind() {
        shader.bind();
    }

    /**
     * Unbind the shader.
     */
    public final void unbind() {
        shader.unbind();
    }
}
