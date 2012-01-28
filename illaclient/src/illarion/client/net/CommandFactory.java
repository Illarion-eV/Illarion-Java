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
package illarion.client.net;

import illarion.client.net.client.*;
import illarion.common.util.RecycleFactory;
import javolution.context.PoolContext;

/**
 * The Factory for commands the client sends to the server. This factory
 * prepares and recycles all client commands and set the needed mapping.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @author Nop
 */
public final class CommandFactory extends RecycleFactory<AbstractCommand> {
    /**
     * Singleton instance of this command factory.
     */
    private static final CommandFactory INSTANCE = new CommandFactory();

    /**
     * Constructor of the command factory. His registers all commands to the
     * factory and sets all the needed mappings.
     */
    private CommandFactory() {
        super();

        // keep alive
        register(new SimpleCmd(CommandList.CMD_KEEPALIVE));

        // login and logout
        register(new LoginCmd());
        map(CommandList.CMD_LOGOFF, CommandList.CMD_KEEPALIVE);

        // map dimension
        register(new MapDimensionCmd());

        // turning the character
        map(CommandList.CMD_TURN_N, CommandList.CMD_KEEPALIVE);
        map(CommandList.CMD_TURN_NE, CommandList.CMD_KEEPALIVE);
        map(CommandList.CMD_TURN_E, CommandList.CMD_KEEPALIVE);
        map(CommandList.CMD_TURN_SE, CommandList.CMD_KEEPALIVE);
        map(CommandList.CMD_TURN_S, CommandList.CMD_KEEPALIVE);
        map(CommandList.CMD_TURN_SW, CommandList.CMD_KEEPALIVE);
        map(CommandList.CMD_TURN_W, CommandList.CMD_KEEPALIVE);
        map(CommandList.CMD_TURN_NW, CommandList.CMD_KEEPALIVE);

        // moving the character
        register(new MoveCmd());

        // talking
        register(new SayCmd());
        map(CommandList.CMD_WHISPER, CommandList.CMD_SAY);
        map(CommandList.CMD_SHOUT, CommandList.CMD_SAY);
        map(CommandList.CMD_INTRODUCE, CommandList.CMD_KEEPALIVE);

        // lookat something
        register(new LookatInvCmd());
        register(new LookatTileCmd());
        register(new LookatShowcaseCmd());
        register(new LookatMenuCmd());
        register(new LookatCharCmd());

        // get the appearance of a character
        register(new RequestAppearanceCmd());

        // use something
        register(new UseCmd());

        // cast a spell
        register(new CastCmd());

        // attack or stop attacking
        register(new AttackCmd());
        map(CommandList.CMD_STAND_DOWN, CommandList.CMD_KEEPALIVE);

        // open or close a container
        register(new OpenBagCmd());
        register(new OpenMapCmd());
        register(new OpenShowcaseCmd());
        map(CommandList.CMD_CLOSE_SHOWCASE, CommandList.CMD_OPEN_BAG);

        // drag items around
        register(new DragInvScCmd());
        register(new DragScInvCmd());
        register(new DragScScCmd());
        register(new DragScMapCmd());
        register(new DragInvInvCmd());
        register(new DragInvMapCmd());
        register(new DragMapInvCmd());
        register(new DragMapScCmd());
        register(new DragMapMapCmd());
        map(CommandList.CMD_DRAG_MAP_MAP_NE, CommandList.CMD_DRAG_MAP_MAP_N);
        map(CommandList.CMD_DRAG_MAP_MAP_E, CommandList.CMD_DRAG_MAP_MAP_N);
        map(CommandList.CMD_DRAG_MAP_MAP_SE, CommandList.CMD_DRAG_MAP_MAP_N);
        map(CommandList.CMD_DRAG_MAP_MAP_S, CommandList.CMD_DRAG_MAP_MAP_N);
        map(CommandList.CMD_DRAG_MAP_MAP_SW, CommandList.CMD_DRAG_MAP_MAP_N);
        map(CommandList.CMD_DRAG_MAP_MAP_W, CommandList.CMD_DRAG_MAP_MAP_N);
        map(CommandList.CMD_DRAG_MAP_MAP_NW, CommandList.CMD_DRAG_MAP_MAP_N);
        map(CommandList.CMD_DRAG_MAP_MAP_ZERO, CommandList.CMD_DRAG_MAP_MAP_N);
        
        register(new TextResponseCmd());

        finish();
    }

    /**
     * Get the singleton instance of this command factory.
     * 
     * @return the singleton instance of the factory
     */
    public static CommandFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Get the AbstractCommand requested from this factory. This method is
     * implemented in addition in order to ensure the execution in the
     * PoolContext.
     */
    @Override
    public AbstractCommand getCommand(final int requestId) {
        PoolContext.enter();
        try {
            return super.getCommand(requestId);
        } finally {
            PoolContext.exit();
        }
    }

    /**
     * Get the AbstractCommand requested from this factory. This method is
     * implemented in addition in order to ensure the execution in the
     * PoolContext. Function returns the proper type.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractCommand> T getCommand(final int requestId, final Class<T> clazz) {
        final AbstractCommand cmd = getCommand(requestId);
        return (T) cmd;
    }
}
