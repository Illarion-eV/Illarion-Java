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
package illarion.client.graphics;

import javolution.util.FastComparator;
import javolution.util.FastMap;
import illarion.common.util.ObjectSource;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;
import illarion.graphics.Sprite;
import illarion.graphics.common.SpriteBuffer;

/**
 * This class is used to load and store the graphics that are needed for
 * displaying the GUI of the game.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class GuiImageFactory implements ObjectSource<Sprite>, ResourceFactory,
    TableLoaderSink {
    private static final int TB_FRAME = 2;
    private static final int TB_NAME = 1;
    private static final int TB_OFFX = 3;
    private static final int TB_OFFY = 4;
    private static final String PATH = "data/gui/";

    final FastMap<String, Sprite> sprites;

    public GuiImageFactory() {
        sprites = new FastMap<String, Sprite>();
        sprites.setKeyComparator(FastComparator.STRING);

    }
    
    @Override
    public void init() {
        new TableLoader("Gui", this);
    }

    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final String name = loader.getString(TB_NAME);
        
        sprites.put(
            name,
            SpriteBuffer.getInstance().getSprite(PATH, name,
                loader.getInt(TB_FRAME), loader.getInt(TB_OFFX),
                loader.getInt(TB_OFFY), Sprite.HAlign.left, Sprite.VAlign.top,
                true, false));
        
        return true;
    }

    @Override
    public boolean containsObject(final String key) {
        return sprites.containsKey(key);
    }

    @Override
    public void disposeObject(final String key, final Sprite object) {
        if (!containsObject(key)) {
            return;
        }
        sprites.remove(key);
    }

    @Override
    public Sprite getObject(final String key) {
        return sprites.get(key);
    }
}
