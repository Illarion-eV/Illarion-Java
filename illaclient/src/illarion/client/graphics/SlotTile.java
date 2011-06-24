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

/**
 * Marker for a container slot that animates through frames when selected
 * Created: 02.09.2005 22:29:57
 */
public class SlotTile extends Marker {
    private final int slotFrames;

    public SlotTile(final int id, final String name, final int frames,
        final int offX, final int offY) {
        super(id, name, frames, offX, offY);
        slotFrames = frames;
    }

    public SlotTile(final SlotTile org) {
        super(org);
        slotFrames = org.slotFrames;
    }

    @Override
    public SlotTile clone() {
        return new SlotTile(this);
    }

    @Override
    public boolean draw() {

        return super.draw();
    }

}
