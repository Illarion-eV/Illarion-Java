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

import illarion.mapedit.MapEditor;

import illarion.common.graphics.ItemInfo;
import illarion.common.graphics.MapVariance;
import illarion.common.util.Location;

import illarion.graphics.Sprite;

/**
 * The default representation for one item. It stores all required informations
 * about a single item on the map.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public class Item extends AbstractEntity {
    /**
     * The resource path to the item graphics. All graphics need to be located
     * at this path within the JAR-resource files.
     */
    @SuppressWarnings("nls")
    private static final String ITEM_PATH = "data/items/";

    /**
     * General informations about the item that do not vary from item instance
     * to instance and are stores in only one object for all instances of the
     * same item this way.
     */
    private transient final ItemInfo info;

    private final String itemName;

    /**
     * True in case the object contains variances instead of a frame animation
     * for the different frames of the image. So if this is set to
     * <code>true</code> all the frames of this image are not handles as a
     * animation, they are used as variances, selected by the location of the
     * item on the map.
     */
    private final boolean variants;

    /**
     * Create a new item based on the parameters. The item allows recoloring by
     * a base color.
     * 
     * @param itemID the ID of the item
     * @param name the base name of the item resource files that are loaded to
     *            get the image(s) for this item
     * @param offX the x offset of the item graphic
     * @param offY the y offset of the item graphic
     * @param frames the amount of frames of this item, this frames can be used
     *            as animation or as variances
     * @param speed the speed of the animation, speed 0 means the item uses the
     *            frames as variances
     * @param itemInfo the item informations about the items that are shared by
     *            all instances of the item and do never change
     */
    protected Item(final int itemID, final String name, final int offX,
        final int offY, final int frames, final int speed,
        final ItemInfo itemInfo) {
        super(itemID, ITEM_PATH, name, frames, 0, offX, offY,
            Sprite.HAlign.center, Sprite.VAlign.bottom);

        info = itemInfo;
        itemName = name;

        if ((speed == 0) && (frames > 1)) {
            variants = true;
        } else {
            variants = false;
        }
    }

    /**
     * Copy constructor that creates a dublicate of the current item. This needs
     * to be done if the same item is drawn more then once on the game screen.
     * <p>
     * The animation object is just copied as well, so the same instance is used
     * for all items. That results in the point that all items of the same kind
     * use the same instance of the animation and show the same frame at the
     * same time.
     * </p>
     * 
     * @param org the item instance that shall be copied
     */
    protected Item(final Item org) {
        super(org);
        info = org.info;
        variants = org.variants;
        itemName = org.itemName;
    }

    /**
     * Create a new item instance for a ID and a specified location. The
     * location is used in case the item has variances. The item is not set on
     * the map tile of the location by default.
     * 
     * @param itemID the ID of the item that shall be created
     * @param locColumn the column on the map where the item shall be created
     * @param locRow the row on the map where the item shall be created
     * @return the item object that shall be used, either a newly created one or
     *         a unused from the recycler
     */
    public static Item create(final int itemID, final int locColumn,
        final int locRow) {
        final Item item = ItemFactory.getInstance().getCommand(itemID);
        item.setVariance(locColumn, locRow);
        return item;
    }

    /**
     * Create a new item instance for a ID and a specified location. The
     * location is used in case the item has variances. The item is not set on
     * the map tile of the location by default.
     * 
     * @param itemID the ID of the item that shall be created
     * @param loc the location where the item shall be shown
     * @return the item object that shall be used, either a newly created one or
     *         a unused from the recycler
     */
    public static Item create(final int itemID, final Location loc) {
        return create(itemID, loc.getCol(), loc.getRow());
    }

    /**
     * Duplicate this item instance. This is done in case there are more then
     * one item object of the same kind on the screen and more instances of this
     * object are needed. The returned copy is usable right away and will draw
     * exactly the same as this instance.
     * 
     * @return the newly created item instance
     */
    @Override
    public Item clone() {
        return new Item(this);
    }

    /**
     * Overwritten drawing function to hide the items out in case the display is
     * set this way.
     */
    @Override
    public boolean draw() {
        if (!MapEditor.getDisplay().isInsideViewport(getBorderRectangle())) {
            return false;
        }
        if (MapEditor.getDisplay().getSettingsItem() == MapDisplay.ItemDisplay.show) {
            super.draw();
        }
        return true;
    }

    public ItemInfo getInfos() {
        return info;
    }

    @Override
    public String getName() {
        return itemName;
    }

    @Override
    public void hide() {
        updateGraphic();
    }

    /**
     * Recycle this item. This causes that the item is hidden from the screen,
     * taken away from the animation in case there is any and placed back in its
     * factory.
     */
    @Override
    public void recycle() {
        ItemFactory.getInstance().recycle(this);
    }

    /**
     * Clean up the data of this item before placing it back to the recycle
     * factory.
     */
    @Override
    public void reset() {
        super.reset();
    }

    /**
     * Set the variance possibilities of this item.
     * 
     * @param locX the x coordinate of the item
     * @param locY the y coordinate of the item
     */
    public void setVariance(final int locX, final int locY) {
        if (variants) {
            setFrame(MapVariance.getItemFrameVariance(locX, locY, getFrames()));
        }
        if (info.hasVariance()) {
            setScale(MapVariance.getItemScaleVariance(locX, locY,
                info.getVariance()));
        }
    }

    @Override
    public void show() {
        updateGraphic();
    }
}
