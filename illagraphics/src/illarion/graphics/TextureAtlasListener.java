/*
 * This file is part of the Illarion Graphics Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Graphics Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Graphics Engine is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Graphics Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.graphics;

/**
 * The texture atlas listener is a interface that is used by the texture atlas
 * to communicate with other classes. The death of a texture, so the removal of
 * all its instances is reported to the rest of the application using this
 * interface.
 * 
 * @author Martin Karing
 * @version 2.00
 * @since 2.00
 */
public interface TextureAtlasListener {
    /**
     * This function is called in case all instances of a texture object got
     * removed so the application can remove the remaining references to the
     * texture as well in case its wanted this way.
     * 
     * @param atlas the texture atlas that has no loaded textures anymore
     */
    void reportDeath(TextureAtlas atlas);
}
