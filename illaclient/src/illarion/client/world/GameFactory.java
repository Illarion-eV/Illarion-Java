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
package illarion.client.world;

import illarion.client.graphics.TextTag;

import illarion.common.util.RecycleFactory;
import illarion.common.util.RecycleObject;

/**
 * Factory class for game objects of all kinds. Created: 20.08.2005 22:41:23
 */
public final class GameFactory extends RecycleFactory<RecycleObject> {

    /**
     * ID for the get function to get the maptext object.
     */
    public static final int OBJ_MAPTEXT = 6;

    /**
     * ID for the get function to get the message object.
     */
    public static final int OBJ_MESSAGE = 2;

    /**
     * ID for the get function to get the tag object.
     */
    public static final int OBJ_TAG = 5;

    /**
     * ID for the get function to get the tool tip object.
     */
    public static final int OBJ_TOOLTIP = 3;

    /**
     * ID for the get function to get the usage object.
     */
    public static final int OBJ_USAGE = 4;

    /**
     * ID for the get function to get the character object.
     */
    protected static final int OBJ_CHARACTER = 0;

    /**
     * ID for the get function to get the maptile object.
     */
    protected static final int OBJ_MAPTILE = 1;

    /**
     * Singleton instance of the Game Factory.
     */
    private static final GameFactory INSTANCE = new GameFactory();

    /**
     * Constructor for the Game Factory.
     */
    private GameFactory() {
        super();
    }

    /**
     * Get the singleton instance of the Game Factory.
     * 
     * @return the instance of the Game Factory
     */
    public static GameFactory getInstance() {
        return INSTANCE;
    }

    /**
     * The init function preapares all prototyped that are needed to work with
     * this function.
     */
    public void init() {
        register(new Char());
        register(new MapTile());
        // register(new TextEntry());
        // register(new Tooltip());
        // register(new Usage());
        register(new TextTag());
        // register(new MapText());
        finish();
    }
}
