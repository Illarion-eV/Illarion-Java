/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
