/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.client.docu;

import javax.annotation.Nullable;

/**
 * This entry is used to store a generic entry in the documentation. The
 * documentation framework uses this entries to fetch all needed informations in
 * order to display a proper GUI.
 *
 * @author Fredrik K
 */
public interface DocuEntry extends Iterable<DocuEntry> {
    /**
     * Get the child with the index handed over here.
     *
     * @param index the index of the child requested
     * @return the requested child
     */
    @Nullable
    DocuEntry getChild(int index);

    /**
     * The amount of child entries this documentation entry has.
     *
     * @return the amount of child entries.
     */
    int getChildCount();

    /**
     * Get the description of this documentation entry.
     *
     * @return the description of this documentation entry
     */
    String getDescription();

    /**
     * Get the title of this entry in the documentation.
     *
     * @return the title of this documentation entry
     */
    String getTitle();
}
