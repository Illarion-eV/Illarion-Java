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
package illarion.mapedit.tools.parts;

import java.awt.event.MouseEvent;

import javolution.util.FastList;

/**
 * The mouse parts manager recycles and binds the mouse parts to the keys of the
 * mouse and allows triggering this parts on locations on the map.
 * 
 * @author Martin Karing
 * @since 0.99
 */
public final class MousePartsManager {
    /**
     * The singleton instance of this class.
     */
    private static final MousePartsManager INSTANCE = new MousePartsManager();

    /**
     * The parts there are currently activated.
     */
    private final AbstractMousePart[] activeParts = new AbstractMousePart[3];

    /**
     * The buffer of already created objects for items bound to a mouse key.
     */
    private final FastList<AbstractMousePart> itemBuffer;

    /**
     * The buffer of already created objects for tiles bound to a mouse key.
     */
    private final FastList<AbstractMousePart> tileBuffer;

    /**
     * Private default constructor to ensure that only the singleton instance is
     * created.
     */
    private MousePartsManager() {
        itemBuffer = new FastList<AbstractMousePart>();
        tileBuffer = new FastList<AbstractMousePart>();
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance of this class
     */
    public static MousePartsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Get the currently activated part.
     * 
     * @param key the key of the activated part that is required
     */
    @SuppressWarnings("nls")
    public AbstractMousePart getActivePart(final int key) {
        if ((key != MouseEvent.BUTTON1) && (key != MouseEvent.BUTTON2)
            && (key != MouseEvent.BUTTON3)) {
            throw new IllegalArgumentException("Illegal key: "
                + Integer.toString(key));
        }
        return activeParts[key - 1];
    }

    /**
     * Set a active part on one of the key slots.
     * 
     * @param key the key the part shall be bound on
     * @param type the
     * @param id
     */
    @SuppressWarnings("nls")
    public void setActivePart(final int key, final int type, final int id,
        final DisableListener listener) {
        if ((type != AbstractMousePart.TYPE_ITEM)
            && (type != AbstractMousePart.TYPE_TILE)) {
            throw new IllegalArgumentException("Illegal type: "
                + Integer.toString(type));
        }

        int index;
        switch (key) {
            case MouseEvent.BUTTON1:
                index = 0;
                break;
            case MouseEvent.BUTTON2:
                index = 1;
                break;
            case MouseEvent.BUTTON3:
                index = 2;
                break;
            default:
                throw new IllegalArgumentException("Illegal key: "
                    + Integer.toString(key));
        }
        if ((activeParts[index] == null)
            || (activeParts[index].getType() != type)) {
            if (activeParts[index] != null) {
                activeParts[index].reportDisabled();
                recyclePart(activeParts[index]);
            }
            if (type == AbstractMousePart.TYPE_ITEM) {
                activeParts[index] = getItemPart();
            } else if (type == AbstractMousePart.TYPE_TILE) {
                activeParts[index] = getTilePart();
            }
        } else if (activeParts[index].getId() == id) {
            return;
        } else {
            activeParts[index].reportDisabled();
        }
        activeParts[index].setId(id);
        activeParts[index].setDisableListener(listener);

        for (int i = 0; i < activeParts.length; i++) {
            if ((index != i) && (activeParts[i] != null)
                && (activeParts[i].getType() == type)
                && (activeParts[i].getId() == id)) {
                activeParts[i].reportDisabled();
                recyclePart(activeParts[i]);
                activeParts[i] = null;
            }
        }
    }

    /**
     * Get a instance of a item part that is valid to be used.
     * 
     * @return the item part to be used
     */
    private AbstractMousePart getItemPart() {
        if (itemBuffer.isEmpty()) {
            return new MousePartItem();
        }
        return itemBuffer.removeLast();
    }

    /**
     * Get a instance of a tile part that is valid to be used.
     * 
     * @return the tile part to be used
     */
    private AbstractMousePart getTilePart() {
        if (tileBuffer.isEmpty()) {
            return new MousePartTile();
        }
        return tileBuffer.removeLast();
    }

    /**
     * Put a part back onto the recycler. So it can be reused later.
     * 
     * @param part the part to be placed back in the recycler
     */
    private void recyclePart(final AbstractMousePart part) {
        if (part.getType() == AbstractMousePart.TYPE_ITEM) {
            itemBuffer.addLast(part);
        } else if (part.getType() == AbstractMousePart.TYPE_TILE) {
            tileBuffer.addLast(part);
        }
    }
}
