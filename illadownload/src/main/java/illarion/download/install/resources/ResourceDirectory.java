/*
 * This file is part of the Illarion Download Manager.
 * 
 * Copyright © 2011 - Illarion e.V.
 * 
 * The Illarion Download Manager is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Download Manager is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Download Manager. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.download.install.resources;

/**
 * This interface defines the resource directory handlers that are used to
 * expose the directory of the resource groups.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public interface ResourceDirectory {
    /**
     * Get the directory for the current resource group
     * 
     * @return the directory
     */
    String getDirectory();
}
