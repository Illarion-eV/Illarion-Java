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

import illarion.graphics.Sprite;

/**
 * Created: 20.08.2005 17:39:11
 */
public class Rune extends AbstractEntity {
    private static final String CHAR_PATH = "data/gui/"; //$NON-NLS-1$

    /**
     * Create a rune
     * 
     * @param id
     * @param name
     */
    public Rune(final int id, final String name) {
        super(id, CHAR_PATH, name, 1, 0, 0, 0, 0, Sprite.HAlign.center,
            Sprite.VAlign.middle, false);
        reset();
    }

    public Rune(final Rune org) {
        super(org);
        reset();
    }

    public static Rune create(final int type, final int runeID) {
        return RuneFactory.getInstance().getCommand(50 + (type * 50) + runeID);
    }

    @Override
    public Rune clone() {
        return new Rune(this);
    }

    @Override
    public void recycle() {
        hide();
        RuneFactory.getInstance().recycle(this);
    }
}
