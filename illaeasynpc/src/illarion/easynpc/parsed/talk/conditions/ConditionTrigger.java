/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyNPC Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyNPC Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc.parsed.talk.conditions;

import java.io.IOException;
import java.io.Writer;

import javolution.context.ObjectFactory;

import illarion.easynpc.parsed.talk.TalkCondition;

/**
 * This class stores a trigger that is used in a condition.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ConditionTrigger implements TalkCondition {
    /**
     * The factory class that creates and buffers ConditionTrigger objects for
     * later reuse.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ConditionTriggerFactory extends
        ObjectFactory<ConditionTrigger> {
        /**
         * Public constructor to the parent class is able to create a instance
         * properly.
         */
        public ConditionTriggerFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of this class.
         */
        @Override
        protected ConditionTrigger create() {
            return new ConditionTrigger();
        }
    }

    /**
     * The code needed for this condition in the easyNPC script.
     */
    @SuppressWarnings("nls")
    private static final String EASY_CODE = "\"%1$s\"";

    /**
     * The factory used to create and reuse objects of this class.
     */
    private static final ConditionTriggerFactory FACTORY =
        new ConditionTriggerFactory();

    /**
     * The LUA code needed for this consequence to work.
     */
    @SuppressWarnings("nls")
    private static final String LUA_CODE = "talkEntry:addTrigger(\"%1$s\");"
        + illarion.easynpc.writer.LuaWriter.NL;

    /**
     * The trigger text of this trigger condition.
     */
    private String triggerString;

    /**
     * Constructor that limits the access to this class, in order to have only
     * the factory creating instances.
     */
    ConditionTrigger() {
        // nothing to do
    }

    /**
     * Get a newly created or a old and reused instance of this class.
     * 
     * @return the instance of this class that is now ready to be used
     */
    public static ConditionTrigger getInstance() {
        return FACTORY.object();
    }

    /**
     * Get the LUA module needed for this condition.
     */
    @Override
    public String getLuaModule() {
        return null;
    }

    /**
     * Recycle the object so it can be used again later.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Reset the state of this instance to its ready to be used later.
     */
    @Override
    public void reset() {
        triggerString = null;
    }

    /**
     * Set the data for this trigger condition.
     * 
     * @param newTriggerString the trigger used in this trigger condition.
     */
    public void setTriggerString(final String newTriggerString) {
        triggerString = newTriggerString;
    }

    /**
     * Write this trigger condition into its easyNPC shape.
     */
    @Override
    public void writeEasyNpc(final Writer target) throws IOException {
        target.write(String.format(EASY_CODE, triggerString));
    }

    /**
     * Write the LUA code needed for this trigger.
     */
    @Override
    @SuppressWarnings("nls")
    public void writeLua(final Writer target) throws IOException {
        target.write(String.format(LUA_CODE,
            triggerString.replace("%NUMBER", "(%d+)")));
    }
}
