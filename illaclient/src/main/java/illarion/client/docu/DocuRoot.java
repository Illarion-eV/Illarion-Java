/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.client.docu;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Fredrik K
 */
public final class DocuRoot implements Iterable<DocuEntry> {
    @Nonnull
    private static final DocuRoot INSTANCE = new DocuRoot();
    @Nonnull
    private final List<DocuEntry> types;

    private DocuRoot() {
        types = new ArrayList<>();

        types.add(createMouseControlDocu());
        types.add(createKeyboardControlDocu());
        types.add(createNoChatDocu());
        types.add(createInChatDocu());
    }

    @Nonnull
    private static DocuEntry createMouseControlDocu() {
        DocuNode docu = new DocuNode("mouse");
        docu.addChild("clickLeft");
        docu.addChild("holdLeft");
        docu.addChild("altClickLeft");
        docu.addChild("doubleLeft");
        docu.addChild("clickRight");
        docu.addChild("moveItem");
        docu.addChild("unstackItem");
        return docu;
    }

    @Nonnull
    private static DocuEntry createKeyboardControlDocu() {
        DocuNode docu = new DocuNode("keys");
        docu.addChild("return");
        docu.addChild("emptyReturn");
        docu.addChild("ctrlC");
        docu.addChild("ctrlV");
        docu.addChild("altGr");
        docu.addChild("f12");
        return docu;
    }

    @Nonnull
    private static DocuEntry createNoChatDocu() {
        DocuNode docu = new DocuNode("noChat");
        docu.addChild("space");
        docu.addChild("wasd");
        docu.addChild("arrow");
        docu.addChild("numpad");
        docu.addChild("altNumpad");
        docu.addChild("ctrlNumpad");
        docu.addChild("fKey");
        docu.addChild("ShiftF");
        docu.addChild("cKey");
        docu.addChild("iKey");
        docu.addChild("mKey");
        docu.addChild("pKey");
        docu.addChild("qKey");
        docu.addChild("bKey");
        docu.addChild("esc");
        return docu;
    }

    @Nonnull
    private static DocuEntry createInChatDocu() {
        DocuNode docu = new DocuNode("inChat");
        docu.addChild("iCommand");
        docu.addChild("oCommand");
        docu.addChild("wCommand");
        docu.addChild("sCommand");
        docu.addChild("meCommand");
        return docu;
    }


    /**
     * Get the singleton instance of this parser.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    @Contract(pure = true)
    public static DocuRoot getInstance() {
        return INSTANCE;
    }

    @Nonnull
    @Override
    public Iterator<DocuEntry> iterator() {
        return types.iterator();
    }
}
