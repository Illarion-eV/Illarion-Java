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
package illarion.download.tasks.unpack;

/**
 * This interface is used to declare the callback classes for the unpacker.
 * Classes that implement this interface are able to register to the unpack
 * manager and get notified then about the progress of unpacking operations.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.00
 */
public interface UnpackCallback {
    /**
     * This function is called once unpacking a resource file is done.
     * 
     * @param result
     * @param unpack
     */
    void reportUnpackFinished(Unpack unpack, UnpackResult result);

    /**
     * This function is called on a regular base while a resource is unpacked.
     * This is done to notify the user of the application about the progress.
     * 
     * @param bytesTotal
     * @param bytesDone
     * @param unpack
     */
    void reportUnpackProgress(Unpack unpack, long bytesDone, long bytesTotal);
}
