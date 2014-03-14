/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.docu;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Fredrik K
 */
public class DocuRoot implements Iterable<DocuEntry> {
    @Nonnull
    private static final DocuRoot INSTANCE = new DocuRoot();
    @Nonnull
    private final List<DocuEntry> types;

    private DocuRoot() {
        types = new ArrayList<>();

        DocuNode docu = new DocuNode("mouse");
        docu.addChild("clickLeft");
        docu.addChild("holdLeft");
        docu.addChild("doubleLeft");
        docu.addChild("clickRight");
        docu.addChild("moveItem");
        docu.addChild("unstackItem");
        types.add(docu);

        docu = new DocuNode("keys");
        docu.addChild("return");
        docu.addChild("emptyReturn");
        docu.addChild("ctrlC");
        docu.addChild("ctrlV");
        docu.addChild("altGr");
        types.add(docu);

        docu = new DocuNode("noChat");
        docu.addChild("wasd");
        docu.addChild("arrow");
        docu.addChild("numpad");
        docu.addChild("altNumpad");
        docu.addChild("ctrlNumpad");
        docu.addChild("cKey");
        docu.addChild("iKey");
        docu.addChild("pKey");
        docu.addChild("qKey");
        docu.addChild("bKey");
        docu.addChild("esc");
        types.add(docu);

        docu = new DocuNode("inChat");
        docu.addChild("iCommand");
        docu.addChild("oCommand");
        docu.addChild("wCommand");
        docu.addChild("sCommand");
        docu.addChild("meCommand");
        types.add(docu);
    }

    /**
     * Get the singleton instance of this parser.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static DocuRoot getInstance() {
        return INSTANCE;
    }

    @Override
    public Iterator<DocuEntry> iterator() {
        return new Iterator<DocuEntry>() {
            public int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < types.size();
            }

            @Override
            public DocuEntry next() {
                return types.get(currentIndex++);
            }

            @Override
            public void remove() {

            }
        };
    }
}
