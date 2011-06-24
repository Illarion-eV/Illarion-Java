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

import illarion.common.util.RecycleFactory;
import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

/**
 * Created: 02.09.2005 22:25:11
 */
public class MarkerFactory extends RecycleFactory<Marker> implements
    TableLoaderSink {
    public static final int CHAR_SELECT = 12;

    public static final int CRSR_COMBAT = 2;
    public static final int CRSR_MAGIC = 4;
    public static final int CRSR_NORMAL = 1;
    public static final int CRSR_USE = 3;
    public static final int CRSR_WALK_E = 32;
    public static final int CRSR_WALK_N = 30;
    public static final int CRSR_WALK_NE = 31;
    public static final int CRSR_WALK_NW = 37;
    public static final int CRSR_WALK_S = 34;
    public static final int CRSR_WALK_SE = 33;
    public static final int CRSR_WALK_SW = 35;
    public static final int CRSR_WALK_W = 36;

    public static final int DRAGGING_LOCK = 44;
    public static final int DRAGGING_UNLOCK = 45;
    public static final int GUI_BAR = 5;
    public static final int GUI_BAR_BUTTON = 8;

    public static final int GUI_BAR_LEFT = 6;
    public static final int GUI_BAR_RIGHT = 7;
    public static final int GUI_BOOK_ARROWL = 62;

    public static final int GUI_BOOK_ARROWL_HOVER = 63;
    public static final int GUI_BOOK_ARROWR = 64;
    public static final int GUI_BOOK_ARROWR_HOVER = 65;
    public static final int GUI_BOOK_STOP = 61;
    public static final int GUI_HORZ_BORDER = 41;
    public static final int GUI_HORZ_LIMIT = 42;
    public static final int GUI_INV_BAG = 52;

    public static final int GUI_INV_BELT = 59;

    public static final int GUI_INV_CHEST = 51;
    public static final int GUI_INV_COAT = 60;
    public static final int GUI_INV_HANDS = 66;
    public static final int GUI_INV_HELMET = 50;

    public static final int GUI_INV_LEGS = 57;
    public static final int GUI_INV_NECK = 53;
    public static final int GUI_INV_RING = 56;
    public static final int GUI_INV_SHIELD = 55;
    public static final int GUI_INV_SHOES = 58;
    public static final int GUI_INV_WEAPON = 54;

    public static final int INV_SLOT = 10;
    public static final int MAGIC_SELECT = 26;
    public static final int MAP_SELECT = 11;
    public static final int MARK_FOE = 14;
    public static final int MARK_MAGIC = 27;

    public static final int MARK_USE = 13;
    public static final int MENU_CLOSE = 18;

    public static final int MENU_LONG = 15;
    public static final int MENU_MAGIC = 19;
    public static final int MENU_SELECT = 17;
    public static final int MENU_SHORT = 16;
    public static final int MINIMAP_DE = 16;
    public static final int MINIMAP_US = 17;
    public static final int SKILL_SCALE = 25;

    public static final int SPELL_CLEAR = 28;
    public static final int SPELL_STORE = 29;

    public static final int STATUS_BACK_FOOD = 10;
    public static final int STATUS_BACK_HEALTH = 9;
    public static final int STATUS_BACK_MANA = 15;
    public static final int STATUS_BAR_FILL = 20;

    public static final int STATUS_BAR_FOOD = 22;
    public static final int STATUS_BAR_HEALTH = 21;

    public static final int STATUS_BAR_MANA = 40;
    public static final int STATUS_FILL = 46;
    public static final int STATUS_FOOD = 48;
    public static final int STATUS_HEALTH = 47;
    public static final int STATUS_MANA = 49;
    public static final int TB_FRAME = 2;
    public static final int TB_ID = 0;
    public static final int TB_MODE = 5;
    public static final int TB_NAME = 1;
    public static final int TB_OFFX = 3;
    public static final int TB_OFFY = 4;
    public static final int WINDOW_BACKGROUND_BEIGE = 39;

    public static final int WINDOW_BACKGROUND_GREEN = 38;
    public static final int WINDOW_BORDER_H = 23;
    public static final int WINDOW_BORDER_V = 24;
    public static final int WINDOW_CLOSE_BUTTON = 67;
    private static final MarkerFactory instance = new MarkerFactory();

    @SuppressWarnings("nls")
    private MarkerFactory() {
        super();

        new TableLoader("Gui", this);

        mapDefault(CRSR_MAGIC, 1);
        finish();
    }

    /**
     * Get instance of singleton
     */
    public static MarkerFactory getInstance() {
        return instance;
    }

    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final int id = loader.getInt(TB_ID);
        Marker mark;
        switch (loader.getInt(TB_MODE)) {
        // animated marker e.g. rotating
            case 1:
                mark =
                    new AnimatedMarker(id, loader.getString(TB_NAME),
                        loader.getInt(TB_FRAME), loader.getInt(TB_OFFX),
                        loader.getInt(TB_OFFY));
                break;

            // background marker animating when selected
            case 2:
                mark =
                    new SlotTile(id, loader.getString(TB_NAME),
                        loader.getInt(TB_FRAME), loader.getInt(TB_OFFX),
                        loader.getInt(TB_OFFY));
                break;

            // standard static marker
            default:
                mark =
                    new Marker(id, loader.getString(TB_NAME),
                        loader.getInt(TB_FRAME), loader.getInt(TB_OFFX),
                        loader.getInt(TB_OFFY));
        }
        register(mark);
        mark.activate(id);

        return true;
    }

}
