/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.gui;

import illarion.common.util.ObjectSource;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;
import illarion.graphics.Sprite;

/**
 * This class is used to load and store the graphics that are needed for
 * displaying the GUI of the game.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class GuiImageFactory implements ObjectSource<Sprite>, TableLoaderSink {

    /* (non-Javadoc)
     * @see illarion.common.util.TableLoaderSink#processRecord(int, illarion.common.util.TableLoader)
     */
    @Override
    public boolean processRecord(int line, TableLoader loader) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean containsObject(String key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void disposeObject(String key, Sprite object) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Sprite getObject(String key) {
        // TODO Auto-generated method stub
        return null;
    }

}
