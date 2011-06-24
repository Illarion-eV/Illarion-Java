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

import illarion.common.graphics.ItemInfo;
import illarion.common.graphics.MapConstants;
import illarion.common.graphics.MapVariance;
import illarion.common.util.Location;

import illarion.graphics.Sprite;
import illarion.graphics.SpriteColor;

/**
 * A item is a object that is on the game map or in the inventory or in any
 * container showcase of the client.
 * 
 * @author Martin Karing
 * @author Nop
 * @since 0.95
 * @version 1.22
 */
public final class Item extends AbstractEntity {
    /**
     * The minimal height of the item image in pixels that is needed so the item
     * graphic fades out in case the player avatar is (partly) hidden by the
     * item.
     */
    private static final int FADING_LIMIT = 70;

    /**
     * The resource path to the item graphics. All graphics need to be located
     * at this path within the JAR-resource files.
     */
    @SuppressWarnings("nls")
    private static final String ITEM_PATH = "data/items/";

    /**
     * The frame animation object that is used in case the item contains a
     * animation that needs to be played.
     */
    private transient final FrameAnimation ani;

    /**
     * The amount of items that are represented by this item instance. So in
     * case the number is larger then 1 this item represents a stack of items of
     * the same kind.
     */
    private int count;

    private boolean displayNumber = false;

    /**
     * True in case the item has to fade out, false if not.
     */
    private final boolean fading;

    /**
     * General informations about the item that do not vary from item instance
     * to instance and are stores in only one object for all instances of the
     * same item this way.
     */
    private transient final ItemInfo info;

    /**
     * The text tag is the renderable text that shows the count of the item next
     * to it.
     */
    private TextTag number;

    /**
     * The color that is used as base color in case paperdolling in done with
     * this item.
     */
    private transient SpriteColor paperdollingColor = null;

    /**
     * The reference ID of this item to the paperdolling object.
     */
    private final int paperdollingID;

    /**
     * This indicates of the number of the item shall be shown. This number
     * shows how many items are on this stack. Its only useful to show this in
     * case the item actually is a stack, so {@link #count} is greater then 1
     * and the item is the only one or the one at the top position on one
     * location.
     */
    private boolean showNumber;

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
     * @param offS the shadow offset of the item graphic, so the area of the
     *            item graphic that is not taken into consideration at the check
     *            if the item needs to fade out
     * @param frames the amount of frames of this item, this frames can be used
     *            as animation or as variances
     * @param speed the speed of the animation, speed 0 means the item uses the
     *            frames as variances
     * @param itemInfo the item informations about the items that are shared by
     *            all instances of the item and do never change
     * @param baseColor the color this item is colored with or null to keep the
     *            original color
     * @param referenceID the reference ID to refer to the paperdolling graphic
     */
    protected Item(final int itemID, final String name, final int offX,
        final int offY, final int offS, final int frames, final int speed,
        final ItemInfo itemInfo, final SpriteColor baseColor,
        final int referenceID) {
        super(itemID, ITEM_PATH, name, frames, 0, offX, offY, offS,
            Sprite.HAlign.center, Sprite.VAlign.bottom,
            itemInfo.hasVariance(), false, baseColor);

        info = itemInfo;

        // an animated item
        if ((speed > 0) && (frames > 1)) {
            // start animation right away. All items of this type will share it
            ani = new FrameAnimation(null);
            ani.setup(frames, 0, speed, FrameAnimation.LOOPED);
            variants = false;
        } else if (frames > 1) {
            // a tile with variants
            variants = true;
            ani = null;
        } else {
            ani = null;
            variants = false;
        }

        fading = (getHeight() >= FADING_LIMIT);

        paperdollingID = referenceID;
        reset();
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
    private Item(final Item org) {
        super(org);
        info = org.info;
        variants = org.variants;
        ani = org.ani;
        fading = org.fading;
        paperdollingID = org.paperdollingID;
        paperdollingColor = org.paperdollingColor;
        reset();
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
        // Set variant and scaling, this functions check on their own if this is
        // allowed
        item.setVariant(locColumn, locRow);
        item.setScale(locColumn, locRow);
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
     * Activate the item instance after it got out of the recycle factory.
     * 
     * @param newID the id that was requested when this instance of the item
     *            object came out of the factory
     */
    @Override
    public void activate(final int newID) {
        // block the super function from overwriting the ID
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
     * Draw the item on the screen on a specified location. In case the item has
     * to fade out in order to free the view on the player character this is
     * done here also. And it case the item needs to draw a number, this is done
     * as well.
     * 
     * @return <code>true</code> in case the render operation is done
     *         successfully
     */
    @Override
    public boolean draw() {
        super.draw();

        if (displayNumber) {
            number.draw(getDisplayX(), getDisplayY());
        }

        return true;
    }

    /**
     * Enable the display of numbers for stacked items.
     * 
     * @param newShowNumber <code>true</code> to show the number at this item
     */
    public void enableNumbers(final boolean newShowNumber) {
        showNumber = newShowNumber && info.isMovable();
    }

    /**
     * Get the count of the item. A count greater then 1 means that this item
     * represents a item stack with the returned amount of items of the same
     * kind on it.
     * 
     * @return the number of items on the stack or 1 in case there is just one
     *         item
     */
    public int getCount() {
        return count;
    }

    /**
     * Get the value how much this item blocks the line of sight.
     * 
     * @return the value in percent how much the line of sight is blocked by
     *         this item
     * @see illarion.common.graphics.ItemInfo#getOpacity()
     */
    public int getCoverage() {
        return info.getOpacity();
    }

    /**
     * Get the facing of the item, so the directions this item accepts light
     * from.
     * 
     * @return the direction the item accepts light from
     * @see illarion.common.graphics.ItemInfo#getFace()
     */
    public int getFace() {
        return info.getFace();
    }

    /**
     * Get the encoded value of the light that is emitted by this item.
     * 
     * @return the encoded value of the light
     * @see illarion.common.graphics.ItemInfo#getLight()
     */
    public int getItemLight() {
        return info.getLight();
    }

    /**
     * Get the surface level of the item. Other items that get placed on this
     * tile need to move up by the returned value in order to appear to lie on
     * this item.
     * 
     * @return the offset of the surface of this item, relative to the origin of
     *         this item
     * @see illarion.common.graphics.ItemInfo#getLevel()
     */
    public int getLevel() {
        return info.getLevel();
    }

    /**
     * Get the color that is used for paperdolling in case there is one set.
     * 
     * @return the color that is used as base color for paperdolling or
     *         <code>null</code>
     */
    public SpriteColor getPaperdollingColor() {
        return paperdollingColor;
    }

    /**
     * Get the Reference ID that is used to the get the correct paperdolling
     * graphic.
     * 
     * @return the paperdolling reference id
     */
    public int getPaperdollingId() {
        return paperdollingID;
    }

    /**
     * Check if this item is a book or not.
     * 
     * @return <code>true</code> if this item is a book and is handle able by
     *         the book reader
     * @see illarion.common.graphics.ItemInfo#isBook()
     * @deprecated This book system is crappy and needs a update
     */
    @Deprecated
    public boolean isBook() {
        return info.isBook();
    }

    /**
     * Check if this item is a container.
     * 
     * @return <code>true</code> in case the item is a container
     * @see illarion.common.graphics.ItemInfo#isContainer()
     */
    public boolean isContainer() {
        return info.isContainer();
    }

    /**
     * Check if the item is a Jesus item and allows walking over blocked tiles
     * such as water.
     * 
     * @return <code>true</code> if the item allows walking over blocked tiles
     * @see illarion.common.graphics.ItemInfo#isJesus()
     */
    public boolean isJesus() {
        return info.isJesus();
    }

    /**
     * Check if this item emits any light.
     * 
     * @return <code>true</code> in case this item is a source of light
     * @see illarion.common.graphics.ItemInfo#isLight()
     */
    public boolean isLight() {
        return info.isLight();
    }

    /**
     * Check if the item can be moved around.
     * 
     * @return <code>true</code> if the item can be moved around by the player
     * @see illarion.common.graphics.ItemInfo#isMovable()
     */
    public boolean isMovable() {
        return info.isMovable();
    }

    /**
     * Check if the item is obstacle, so the pathfinder needs to walk around it.
     * 
     * @return <code>true</code> if the item is obstacle
     * @see illarion.common.graphics.ItemInfo#isObstacle()
     */
    public boolean isObstacle() {
        return info.isObstacle();
    }

    /**
     * Recycle this item. This causes that the item is hidden from the screen,
     * taken away from the animation in case there is any and placed back in its
     * factory.
     */
    @Override
    public void recycle() {
        hide();
        if (ani != null) {
            ani.removeTarget(this);
        }
        ItemFactory.getInstance().recycle(this);
    }

    /**
     * Clean the item instance before putting it back into the recycler. This
     * method is called automatically by the recycle factory.
     */
    @Override
    public void reset() {
        super.reset();
        showNumber = false;
        if (number != null) {
            number.recycle();
            number = null;
        }
    }

    /**
     * Scale the size of the item to a new one.
     * 
     * @param size the new size in pixel of the item object, this value is
     *            applied to the larger side, so either height or width of the
     *            item image
     * @param enlarge <code>true</code> to allow to enlarge the item image, else
     *            only scaling ratios below 1 are applied
     */
    public void scaleTo(final int size, final boolean enlarge) {
        final int value = Math.max(getHeight(), getWidth());

        float scale = 1;
        if ((value > size) || enlarge) {
            scale = (float) size / (float) value;
        }
        setScale(scale);
    }

    /**
     * Set number of stacked items.
     * 
     * @param newCount the number of items on this stack, in case its more then
     *            one a text is displayed next to the item that shown how many
     *            items are on the stack
     */
    public void setCount(final int newCount) {
        count = newCount;

        // write number to text for display
        if (count > 1) {
            if (number == null) {
                number = TextTag.create();
            }

            number.setColor(Colors.yellow);
            number.setText(Integer.toString(count));
            number.setOffset((MapConstants.TILE_W / 2) - number.getHeight()
                - number.getWidth(), -number.getHeight() / 2);
        } else if (number != null) {
            number.recycle();
            number = null;
        }
    }

    /**
     * Set the color that is used in case this item is displayed as a
     * paperdolling item.
     * 
     * @param color the instance of sprite color that is used as base color in
     *            case paperdolling is done with this item
     */
    public void setPaperdollingColor(final SpriteColor color) {
        paperdollingColor = color;
    }

    /**
     * Display the item by adding it to the display list. This also starts the
     * animation of the item in case there is any.
     */
    @Override
    public void show() {
        // add to display list
        super.show();

        startAmination();
    }

    /**
     * Start the animation of the item by adding it the the animation handler of
     * this item type in case there is any. If this item does not contain any
     * animation, calling this function has no result.
     */
    public void startAmination() {
        // connect to animation
        if (ani != null) {
            ani.addTarget(this, true);
        }
    }

    /**
     * Update the displayed item. This takes care for fading effects in case
     * needed and for handling the display of the number at the item.
     * 
     * @param delta the time in milliseconds since the last update
     */
    @Override
    public void update(final int delta) {
        if (fading) {
            super.update(delta);
        }

        if (showNumber && (count > 1) && (number != null)) {
            if (!displayNumber) {
                number.addToCamera(getDisplayX(), getDisplayY());
                displayNumber = true;
            }
        } else {
            if (displayNumber) {
                number.addToCamera(getDisplayX(), getDisplayY());
                displayNumber = false;
            }
        }
    }

    /**
     * Determine the graphical variant from a coordinate and set the needed
     * frame on this.
     * 
     * @param locX the first part of the coordinate
     * @param locY the second part of the coordinate
     */
    protected void setVariant(final int locX, final int locY) {
        if (!variants) {
            return;
        }
        setFrame(MapVariance.getItemFrameVariance(locX, locY, getFrames()));
    }

    /**
     * Set an individual scale dependent on a location. The new scale value is
     * directly applied to the item.
     * 
     * @param locX the first part of the coordinate
     * @param locY the second part of the coordinate
     */
    private void setScale(final int locX, final int locY) {
        if (info.hasVariance()) {
            setScale(MapVariance.getItemScaleVariance(locX, locY,
                info.getVariance()));
        }
    }
}
