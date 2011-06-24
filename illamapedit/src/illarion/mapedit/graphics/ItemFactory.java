/*
 * This file is part of the Illarion Mapeditor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Mapeditor is free software: you can redistribute i and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Mapeditor is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Mapeditor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.mapedit.graphics;

import org.apache.log4j.Logger;

import illarion.mapedit.MapEditor;

import illarion.common.graphics.ItemInfo;
import illarion.common.util.RecycleFactory;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

import illarion.graphics.Graphics;
import illarion.graphics.SpriteColor;

/**
 * The Item factory loads creates and stores all instances of the item class
 * that are around in the client.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.95
 */
public final class ItemFactory extends RecycleFactory<Item> implements
    TableLoaderSink {
    /**
     * The singleton instance of this factory.
     */
    private static final ItemFactory INSTANCE = new ItemFactory();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ItemFactory.class);

    /**
     * The table index that stores the alpha modifier that shall be applied to
     * the original color of this avatar graphic.
     */
    private static final int TB_COLORMOD_ALPHA = 24;

    /**
     * The table index that stores the blue color modifier that shall be applied
     * to the original color of this avatar graphic.
     */
    private static final int TB_COLORMOD_BLUE = 23;

    /**
     * The table index that stores the green color modifier that shall be
     * applied to the original color of this avatar graphic.
     */
    private static final int TB_COLORMOD_GREEN = 22;

    /**
     * The table index that stores the red color modifier that shall be applied
     * to the original color of this avatar graphic.
     */
    private static final int TB_COLORMOD_RED = 21;

    /**
     * The table index of the item face that is used to determine the direction
     * the item accepts light from and the directions the item blocks the light
     * from.
     */
    private static final int TB_FACE = 8;

    /**
     * The table index of the frame count of this item in the definition table.
     */
    private static final int TB_FRAME = 2;

    /**
     * The table index of the item group this item belongs to.
     */
    private static final int TB_GROUP = 19;

    /**
     * The table index of the item id in the definition table.
     */
    private static final int TB_ID = 0;

    /**
     * The table index of the surface level of the item.
     */
    private static final int TB_LEVEL = 18;

    /**
     * The table index of the encoded value of the light that is emitted by this
     * item.
     */
    private static final int TB_LIGHT = 20;

    /**
     * The table index of the mode of the item that is used to determine if the
     * item is a animated one or one with variances.
     */
    private static final int TB_MODE = 3;

    /**
     * The table index of the flag if the item is move able so the client has to
     * allow to drag it around or not.
     */
    private static final int TB_MOVABLE = 9;

    /**
     * The table index of the item resource name, so the base name of the images
     * for this item, in the definition table.
     */
    private static final int TB_NAME = 1;

    /**
     * The table index of the flag if the item is obstacle or not.
     */
    private static final int TB_OBSTACLE = 15;

    /**
     * The table index of the x offset of the item graphic in the definition
     * table.
     */
    private static final int TB_OFFX = 4;

    /**
     * The table index of the y offset of the item graphic in the definition
     * table.
     */
    private static final int TB_OFFY = 5;

    /**
     * The table index of the opacity of the item, so the value in percent the
     * item blocks the line of sight.
     */
    private static final int TB_OPACITY = 14;

    /**
     * The table index of the special item flag that encodes if the item is a
     * container, a book or a Jesus-Item(c).
     */
    private static final int TB_SPECIAL = 12;

    /**
     * The table index of the frame animation speed of this item in the
     * definition table.
     */
    private static final int TB_SPEED = 6;

    /**
     * The table index of the variance value, so the value in percent the item
     * graphic can be scaled up or down.
     */
    private static final int TB_VARIANCE = 13;

    /**
     * Construct the item factory, that also triggers the loading process of the
     * items table that automatically fills the factory. The only instance of
     * this class is the singleton instance that is created with this
     * constructor.
     */
    private ItemFactory() {
        super();
    }

    /**
     * Get the singleton instance of this factory.
     * 
     * @return the singleton instance of this factory
     */
    public static ItemFactory getInstance() {
        return INSTANCE;
    }

    /**
     * The initialization function prepares all prototyped that are needed to
     * work with this function.
     */
    @SuppressWarnings("nls")
    public void init() {
        for (int i = 0, n = ItemInfo.getGroupCount(); i < n; i++) {
//            MapEditor.getMainFrame().getRightToolbar().getPartSelector()
//                .getItemsList().addGroup(ItemInfo.getGroupName(i));
        }

        new TableLoader("Items", this);
        mapDefault(0, 1);

        ItemInfo.cleanup();
        finish();
    }

    /**
     * Process one line of the item definition table and create a item
     * definition from it.
     * 
     * @param line the number of the line that is currently processed
     * @param loader the table loader that handles this line and supplies the
     *            data
     * @return <code>true</code> to go on reading the table, false to cancel the
     *         reading process
     */
    @SuppressWarnings("nls")
    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final String name = loader.getString(TB_NAME);

        final int colorRed = loader.getInt(TB_COLORMOD_RED);
        final int colorGreen = loader.getInt(TB_COLORMOD_GREEN);
        final int colorBlue = loader.getInt(TB_COLORMOD_BLUE);
        final int colorAlpha = loader.getInt(TB_COLORMOD_ALPHA);

        SpriteColor baseColor = null;
        if ((colorRed >= 0) && (colorGreen >= 0) && (colorBlue >= 0)
            && (colorAlpha >= 0)) {
            baseColor = Graphics.getInstance().getSpriteColor();
            baseColor.set(colorRed, colorGreen, colorBlue);
            baseColor.setAlpha(colorAlpha);
        }

        final int mode = loader.getInt(TB_MODE);
        final int itemID = loader.getInt(TB_ID);
        final int face = loader.getInt(TB_FACE);
        final boolean moveable = loader.getBoolean(TB_MOVABLE);
        final int specialFlag = loader.getInt(TB_SPECIAL);
        final boolean obstacle = loader.getBoolean(TB_OBSTACLE);
        final int variance = loader.getInt(TB_VARIANCE);
        final int opacity = loader.getInt(TB_OPACITY);
        final int surfaceLevel = loader.getInt(TB_LEVEL);
        final int itemLight = loader.getInt(TB_LIGHT);
        final int offsetX = loader.getInt(TB_OFFX);
        final int offsetY = loader.getInt(TB_OFFY);

        final ItemInfo info =
            ItemInfo.create(face, moveable, specialFlag, obstacle, variance,
                opacity, surfaceLevel, itemLight);

        int frames;
        int speed;

        if (mode == 1) {
            frames = loader.getInt(TB_FRAME);
            speed = loader.getInt(TB_SPEED);
        } else if (mode == 2) {
            frames = loader.getInt(TB_FRAME);
            speed = 0;
        } else {
            frames = 1;
            speed = 0;
        }

        final Item item =
            new Item(itemID, name, offsetX, offsetY, frames, speed, info);

        try {
            // register item with factory
            register(item);

            item.activate(itemID);

//            MapEditor.getMainFrame().getRightToolbar().getPartSelector()
//                .getItemsList().addPart(loader.getInt(TB_GROUP) - 1, item);
        } catch (final IllegalStateException ex) {
            LOGGER.error("Failed adding item to internal factory. ID: "
                + Integer.toString(itemID) + " - Filename: " + name);
        }

        return true;
    }
}
