/*
 * This file is part of the Illarion easyQuest Editor.
 *
 * Copyright 2011 - Illarion e.V.
 *
 * The Illarion easyQuest Editor is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion easyQuest Editor is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion easyQuest Editor. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easyquest.quest;

public class Position {
    
    private short x;
    private short y;
    private short z;
    
    public Position ()
    {
        x = 0;
        y = 0;
        z = 0;
    }
    
    public Position (short x, short y, short z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public short getX()
    {
        return x;
    }
    
    public void setX(short x)
    {
        this.x = x;
    }
    
    public short getY()
    {
        return y;
    }
    
    public void setY(short y)
    {
        this.y = y;
    }
    
    public short getZ()
    {
        return z;
    }
    
    public void setZ(short z)
    {
        this.z = z;
    }
}
