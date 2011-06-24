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
package illarion.client.net.client;

import java.util.ArrayList;

import illarion.client.guiNG.references.AbstractReference;
import illarion.client.net.CommandList;
import illarion.client.net.NetCommWriter;

/**
 * Client Command: Use something, optional with something else (
 * {@link illarion.client.net.CommandList#CMD_USE}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class UseCmd extends AbstractCommand {
    /**
     * The amount of references that can be maximal used together.
     */
    private static final int MAXIMAL_USES_COUNT = 3;

    /**
     * The current state of the counter that is send to the server.
     */
    private byte counter;

    /**
     * The list of things that are involved in the use command.
     */
    private final ArrayList<AbstractReference> uses =
        new ArrayList<AbstractReference>(MAXIMAL_USES_COUNT);

    /**
     * Default constructor for the use command.
     */
    public UseCmd() {
        super(CommandList.CMD_USE);
    }

    /**
     * Ad a single use element to the list of elements that are involved in this
     * use. This function also causes that the counter value is stored in the
     * command.
     * 
     * @param execute the use element that shall be added.
     */
    @SuppressWarnings("nls")
    public void addUse(final AbstractReference execute) {
        uses.add(execute);
        if (uses.size() > MAXIMAL_USES_COUNT) {
            throw new IllegalArgumentException(
                "only two uses and menu are supported");
        }
        counter = (byte) 0;
    }

    /**
     * Create a duplicate of this use command.
     * 
     * @return new instance of this command
     */
    @Override
    public UseCmd clone() {
        return new UseCmd();
    }

    /**
     * Encode the data of this use command and put the values into the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeByte((byte) uses.size());

        for (final AbstractReference use : uses) {
            use.encodeUse(writer);
        }

        // add counter value
        writer.writeByte(counter);
    }

    /**
     * Clean up the command before put it back into the recycler for later
     * reuse.
     */
    @Override
    public void reset() {
        uses.clear();
    }

    /**
     * Set the counter value to a forced value. The text book requires this
     * possibility.
     * 
     * @param forcedCounter the state of the counter that is send
     */
    public void setCounter(final int forcedCounter) {
        counter = (byte) forcedCounter;
    }

    /**
     * Get the data of this move command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return toString("Elements: " + uses.size() + " Counter: " + counter);
    }
}
