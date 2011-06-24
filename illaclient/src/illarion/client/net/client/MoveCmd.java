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

import illarion.client.net.CommandList;
import illarion.client.net.NetCommWriter;

/**
 * Client Command: Request a move or a push (
 * {@link illarion.client.net.CommandList#CMD_MOVE}).
 * 
 * @author Nop
 * @author Martin Karing
 * @since 0.92
 * @version 1.22
 */
public final class MoveCmd extends AbstractCommand {
    /**
     * Byte flag for a simple move.
     */
    private static final byte MODE_MOVE = 0x0B;

    /**
     * Byte flag for a pushing.
     */
    private static final byte MODE_PUSH = 0x0C;

    /**
     * Byte flag for a running move.
     */
    private static final byte MODE_RUN = 0x0D;

    /**
     * The character ID of the char that shall move.
     */
    private long charId;

    /**
     * The direction the character moves to.
     */
    private byte dir;

    /**
     * Set the movement type. Possible values are {@link #MODE_MOVE} and
     * {@link #MODE_PUSH}.
     */
    private byte mode;

    /**
     * Default constructor for the move command.
     */
    public MoveCmd() {
        super(CommandList.CMD_MOVE);
    }

    /**
     * Create a duplicate of this move command.
     * 
     * @return new instance of this command
     */
    @Override
    public MoveCmd clone() {
        return new MoveCmd();
    }

    /**
     * Encode the data of this move command and put the values into the buffer.
     * 
     * @param writer the interface that allows writing data to the network
     *            communication system
     */
    @Override
    public void encode(final NetCommWriter writer) {
        writer.writeUInt(charId);
        writer.writeByte(dir);
        writer.writeByte(mode);
    }

    /**
     * Set the ID of the character that shall move. The client is able to use
     * {@link #setDirection(long, int)}. This function is only needed for the
     * load test client.
     * 
     * @param moveCharId the ID of the character that shall move
     */
    public void setCharID(final long moveCharId) {
        charId = moveCharId;
    }

    /**
     * Set the direction of the move and the ID of the character that shall
     * move.
     * 
     * @param moveCharId the ID of the character that shall move
     * @param moveDir the direction the character shall move to
     */
    public void setDirection(final long moveCharId, final int moveDir) {
        dir = (byte) moveDir;
        charId = moveCharId;
    }

    /**
     * Set the type of this move to moving.
     */
    public void setMoving() {
        mode = MODE_MOVE;
    }

    /**
     * Set the type of this move to pushing.
     */
    public void setPushing() {
        mode = MODE_PUSH;
    }

    /**
     * Set the type of this move to running.
     */
    public void setRunning() {
        mode = MODE_RUN;
    }

    /**
     * Get the data of this move command as string.
     * 
     * @return the data of this command as string
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ID: ");
        builder.append(charId);
        builder.append(" dir=");
        builder.append(dir);
        builder.append(" mode=");
        builder.append(mode);
        return toString(builder.toString());
    }
}
