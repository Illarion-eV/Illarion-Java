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

import org.apache.log4j.Logger;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.procedure.TIntProcedure;

import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

/**
 * This class takes care for loading the avatar clothes, sorts them to the
 * avatars and prepares to render them.
 * 
 * @author Martin Karing
 * @since 1.22
 */
public final class AvatarClothLoader implements TableLoaderSink {
    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger
        .getLogger(AvatarClothLoader.class);

    /**
     * The table index that stores the number of frames of the animation.
     */
    private static final int TB_FRAME = 1;

    /**
     * The table index that stores the body location of the graphic.
     */
    private static final int TB_LOCATION = 5;

    /**
     * The table index that stores if the graphic shall be mirrored.
     */
    private static final int TB_MIRROR = 11;

    /**
     * The table index of the file name of the cloth that shall be displayed.
     */
    private static final int TB_NAME = 0;

    /**
     * The table index that stores the x offset of the graphic.
     */
    private static final int TB_OFFSET_X = 3;

    /**
     * The table index that stores the y offset of the graphic.
     */
    private static final int TB_OFFSET_Y = 4;

    /**
     * The table index that stores the avatar ID this cloth is assigned to.
     */
    private static final int TB_REF_CHAR_ID = 10;

    /**
     * The table index that stores the item ID this cloth is assigned to.
     */
    private static final int TB_REF_ITEM_ID = 6;

    /**
     * The table index that stores the number of the first and last frame of the
     * animation.
     */
    private static final int TB_STILL = 2;

    /**
     * This list stores the IDs of the avatars that got clothes.
     */
    private TIntArrayList usedAvas;

    /**
     * The initialisation function prepares all prototyped that are needed to
     * work with this function.
     */
    @SuppressWarnings({ "nls", "unused" })
    public void init() {
        usedAvas = new TIntArrayList();
        new TableLoader("Cloth", this);

        usedAvas.forEach(new TIntProcedure() {
            @Override
            public boolean execute(final int value) {
                final Avatar parentAva =
                    AvatarFactory.getInstance().getPrototype(value);
                parentAva.getClothes().finish();
                return true;
            }
        });
        usedAvas = null;
    }

    /**
     * Handle one record from the table that is loaded by this function. This
     * function is called by the table loader.
     * 
     * @param line the line in the list that is currently processed
     * @param loader the table loader class that handles the table that is
     *            currently loading
     * @return true in case the loader shall go on reading the table, false if
     *         it should stop
     */
    @SuppressWarnings("nls")
    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final int avatarID = loader.getInt(TB_REF_CHAR_ID);
        final Avatar parentAva =
            AvatarFactory.getInstance().getPrototype(avatarID);
        final AvatarClothManager manager = parentAva.getClothes();

        final int itemID = loader.getInt(TB_REF_ITEM_ID);
        final int location = loader.getInt(TB_LOCATION);

        final AvatarCloth cloth =
            new AvatarCloth(itemID, loader.getString(TB_NAME),
                loader.getInt(TB_FRAME), loader.getInt(TB_STILL),
                loader.getInt(TB_OFFSET_X) + parentAva.getOffsetX(),
                loader.getInt(TB_OFFSET_Y) + parentAva.getOffsetY(),
                loader.getBoolean(TB_MIRROR), null);

        try {
            manager.addCloth(location, cloth);
        } catch (final IllegalStateException e) {
            LOGGER.error("Error adding paperdolling item to avatar: "
                + Integer.toString(avatarID) + " in group: "
                + Integer.toString(location) + " to item: "
                + Integer.toString(itemID));
        }

        if (!usedAvas.contains(avatarID)) {
            usedAvas.add(avatarID);
        }
        return true;
    }
}
