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
package illarion.easynpc.parser.talk;

import illarion.easynpc.parsed.talk.TalkCondition;

/**
 * This interface is a talking condition. It covers all possible methods
 * required to have this object acting as a proper condition.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public abstract class ConditionParser extends Parser {
    /**
     * Extract the condition from a line.
     * 
     * @return the condition filled with the data of this line
     */
    public abstract TalkCondition extract();
}
