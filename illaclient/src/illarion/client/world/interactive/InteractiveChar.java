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
package illarion.client.world.interactive;

import illarion.client.net.CommandFactory;
import illarion.client.net.CommandList;
import illarion.client.net.client.MoveCmd;
import illarion.client.world.Char;
import illarion.client.world.World;
import illarion.common.util.Location;
import illarion.common.util.Reusable;
import javolution.context.ObjectFactory;

/**
 * This class represents the interactive variant of a character.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class InteractiveChar extends AbstractDraggable implements DropTarget, Reusable {
    /**
     * The factory to create and recycle instances of the interactive
     * characters.
     * 
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private static final class InteractiveCharFactory extends
        ObjectFactory<InteractiveChar> {
        /**
         * Public constructor so the parent class is able to create instances of
         * this class.
         */
        public InteractiveCharFactory() {
            // nothing
        }

        /**
         * Create a new interactive character instance.
         */
        @Override
        protected InteractiveChar create() {
            return new InteractiveChar();
        }
    }

    /**
     * The factory instance used to create and recycle instances of interactive
     * characters.
     */
    private static final InteractiveCharFactory FACTORY =
        new InteractiveCharFactory();

    /**
     * Get a new instances of the interactive character that points at a
     * existing character on the map.
     * 
     * @param chara the character the new instance is supposed to point to
     * @return the newly created or reused instances of interactive character
     */
    public static InteractiveChar create(final Char chara) {
        final InteractiveChar newChar = FACTORY.object();
        newChar.parentChar = chara;
        return newChar;
    }

    /**
     * The character this interactive reference points to.
     */
    private Char parentChar;

    /**
     * Private constructor to ensure new instances to be only created by the
     * factory.
     */
    private InteractiveChar() {
        // nothing
    }

    /**
     * Drag one character to another character. Does nothing currently.
     */
    @Override
    public void dragTo(final InteractiveChar targetChar) {
        // nothing
    }

    /**
     * Dragging the character into the inventory does nothing at all.
     */
    @Override
    public void dragTo(final InteractiveInventorySlot targetSlot) {
        // nothing
    }

    /**
     * Drag the character to another spot on the map. This causes pushing the
     * character.
     */
    @Override
    public void dragTo(final InteractiveMapTile targetTile) {
        if (!isInInteractionRange()) {
            return;
        }

        final int pushingDir =
            getLocation().getDirection(targetTile.getLocation());

        if (pushingDir == Location.DIR_ZERO) {
            return;
        }

        final MoveCmd cmd =
            CommandFactory.getInstance().getCommand(CommandList.CMD_MOVE,
                MoveCmd.class);
        cmd.setDirection(parentChar.getCharId(), pushingDir);
        cmd.setPushing();
        cmd.send();
    }

    /**
     * Get the location of the character on the map.
     * 
     * @return the location of the character on the map
     */
    public Location getLocation() {
        return parentChar.getLocation();
    }

    /**
     * Check if this character is inside the range, where the player is able to
     * interact with this character.
     * 
     * @return <code>true</code> if this character is within reach of the player
     *         character
     */
    public boolean isInInteractionRange() {
        return (World.getPlayer().getLocation().getDistance(getLocation()) < 2);
    }

    /**
     * Release this instance and place it in the recycler so it can be reused
     * later on.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Reset this instance. This shouldn't be called as it renders this instance
     * unusable.
     */
    @Override
    public void reset() {
        parentChar = null;
    }
}
