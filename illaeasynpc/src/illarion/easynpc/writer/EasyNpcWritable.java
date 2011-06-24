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
package illarion.easynpc.writer;

import java.io.IOException;
import java.io.Writer;

/**
 * This interface needs to be implemented to classes that can be written by the
 * {@link illarion.easynpc.writer.EasyNpcWriter}. They use this interface to
 * generate the text that needs to be written in to the script.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public interface EasyNpcWritable {
    /**
     * Check if the writable effects a certain easyNPC writing stage. This is
     * used to detect what parts of the easyNPC script are used, and what parts
     * are not.
     * 
     * @param stage the stage to check
     * @return <code>true</code> in case this stage is effected
     */
    boolean effectsEasyNpcStage(EasyNpcWriter.WritingStage stage);

    /**
     * Write the data of this EasyNpcWriteable to the script according to the
     * parsing stage the script writer is currently working in.
     * 
     * @param target the writer that is the data target
     * @param stage the current stage of the writing process
     * @exception IOException thrown in case the writing operation failed
     */
    void writeEasyNpc(Writer target, EasyNpcWriter.WritingStage stage)
        throws IOException;
}
