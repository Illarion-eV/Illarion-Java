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
package illarion.client.resources.data;

import illarion.client.resources.Resource;

/**
 * This interface defines a generic template that can be used to create a instance of a specific class.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface ResourceTemplate extends Resource {
    /**
     * Get the ID that is used to store the template.
     *
     * @return the ID of the template
     */
    int getTemplateId();
}
