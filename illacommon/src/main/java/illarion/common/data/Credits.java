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
package illarion.common.data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class contains the list of people who helped to create Illarion.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Credits {
    /**
     * The list of entries that are assigned to a single person always.
     */
    @Nonnull
    private final List<CreditsList> singlePosList;

    /**
     * The list of entries that are assigned to multiple persons.
     */
    @Nonnull
    private final List<CreditsList> multiPosList;

    /**
     * The singleton instance of this credits class.
     */
    @Nullable
    private static Reference<Credits> instance = null;

    /**
     * Get the singleton instance of the credits class.
     * <p/>
     * The instance is created once this function is called. It is also only stored weakly, so once there is on use for
     * it anymore the instance is being disposed of.
     *
     * @return the credits instance
     */
    @Nonnull
    public static Credits getInstance() {
        Reference<Credits> usedInstance = instance;
        Credits usedCredits = null;
        if (usedInstance != null) {
            usedCredits = usedInstance.get();
        }
        if (usedCredits == null) {
            usedCredits = new Credits();
            instance = new SoftReference<>(usedCredits);
        }
        return usedCredits;
    }

    /**
     * The constructor of the credits.
     * <p/>
     * Be aware, this class will load all the text and objects required to display the credits. Only do so in case
     * you really want to display this stuff.
     */
    @SuppressWarnings("OverlyLongMethod")
    private Credits() {
        singlePosList = new ArrayList<>();
        multiPosList = new ArrayList<>();

        CreditsList projectManager = new CreditsList("Projektleiter", "Project Manager");
        CreditsList chiefContent = new CreditsList("Leitender Entwickler für Spielinhalte",
                                                         "Chief Game Content Developer");
        CreditsList chiefClient = new CreditsList("Leitender Entwickler für den Client",
                                                        "Chief Client Developer");
        CreditsList chiefGraphics = new CreditsList("Leitender Grafiker", "Chief Graphics Designer");
        CreditsList chiefMusic = new CreditsList("Original-Soundtrack", "Original Soundtrack");
        CreditsList chiefMap = new CreditsList("Leitender Gestalter für die Karte", "Chief Map Designer");
        CreditsList chiefServer = new CreditsList("Leitender Entwickler für den Server",
                                                        "Chief Server Developer");
        CreditsList presentedBy = new CreditsList("Präsentiert vom", "Presented by the");

        CreditsList gameplay = new CreditsList("Spielmechanik", "Gameplay");
        CreditsList content = new CreditsList("Spielinhalte", "Game Content");
        CreditsList client = new CreditsList("Client");
        CreditsList graphics = new CreditsList("Grafiken", "Graphics");
        CreditsList maps = new CreditsList("Karten", "Maps");
        CreditsList website = new CreditsList("Website");
        CreditsList easyNPC = new CreditsList("easyNPC-Editor", "easyNPC Editor");
        CreditsList easyQuest = new CreditsList("easyQuest-Editor", "easyQuest Editor");
        CreditsList mapEditor = new CreditsList("Karten-Editor", "Map Editor");
        CreditsList server = new CreditsList("Server");
        CreditsList gameMaster = new CreditsList("Spielleiter", "Game Master");
        CreditsList communityManager = new CreditsList("Community-Manager", "Community Manager");
        CreditsList qualityAssurance = new CreditsList("Qualitätsprüfung", "Quality Assurance");
        CreditsList specialThanks = new CreditsList("Besonderen Dank", "Special Thanks");

        CreditsPerson.create("Andreas", "Vilarion", "Grob",
                projectManager, chiefServer, gameplay, client, website, easyNPC, easyQuest, server, qualityAssurance);
        CreditsPerson.create("Lennart", "Estralis", "Stutz", chiefContent, gameplay, content, website,
                qualityAssurance);
        CreditsPerson.create("Martin", "Nitram", "Karing",
                chiefClient, gameplay, client, website, easyNPC, easyQuest, mapEditor, server, qualityAssurance);
        CreditsPerson.create("Martin", "Polak",
                chiefGraphics, gameplay, client, graphics, website, easyNPC, easyQuest, server, qualityAssurance);
        CreditsPerson.create("Oganalp", "Canatan", chiefMusic);
        CreditsPerson.create("Marvin", "Kopp", chiefMusic);
        CreditsPerson.create("Zot", content, graphics, maps, qualityAssurance);

        CreditsPerson.create("Teflon", gameMaster);
        CreditsPerson.create("Silverwing", qualityAssurance, gameMaster);
        CreditsPerson.create("Slightly", gameMaster);
        CreditsPerson.create("Ruben", "Zephyrius", "Garza", gameMaster);
        CreditsPerson.create("Kristen", "Obsydien", "Stewart", gameMaster);

        CreditsPerson.create("Djironnyma", communityManager);
        CreditsPerson.create("Achae Eanstray", communityManager, graphics);

        CreditsPerson.create("Andreas", "Caldarion", "Gahr", gameplay, content, qualityAssurance);
        CreditsPerson.create("Merung", gameplay, content, qualityAssurance);
        CreditsPerson.create("Ardian", gameplay);
        CreditsPerson.create("Esther", "Kadiya", "Sense", gameplay, graphics, website);
        CreditsPerson.create("Jan", "Mattner", gameplay, qualityAssurance);
        CreditsPerson.create("Wolfgang", "Müller", content);
        CreditsPerson.create("Thomas", "Messerschmidt", content);
        CreditsPerson.create("Faladron", content);
        CreditsPerson.create("Henry", "Mill", content, maps);
        CreditsPerson.create("Lisa", "Maletzki", gameplay, content, qualityAssurance);
        CreditsPerson.create("Marion", "Miriam", "Herstell", content, client, qualityAssurance);
        CreditsPerson.create("Vitoria", content);
        CreditsPerson.create("Nikolaus", "Nalcaryos", "Tauß", content);
        CreditsPerson.create("Grokk", content, qualityAssurance);
        CreditsPerson.create("Kawan", "Regallo", "Baxter", content);
        CreditsPerson.create("Alex", "Flux", "Rose", content);
        CreditsPerson.create("Llama", content);
        CreditsPerson.create("Dominic", "Dyluck", "W.", content);
        CreditsPerson.create("Martin", "Skamato", "Großmann", content, qualityAssurance);
        CreditsPerson.create("Tiim", client, mapEditor);
        CreditsPerson.create("Fredrik K", client, mapEditor);
        CreditsPerson.create("Smjert", client);
        CreditsPerson.create("Samaras", graphics);
        CreditsPerson.create("Drakon Gerwulf", graphics);
        CreditsPerson.create("Karl", "Salameh", graphics);
        CreditsPerson.create("Matt", "Raelith", "Hollier", website);
        CreditsPerson.create("Jaime", "Quinasa", "Hughes", qualityAssurance);
        CreditsPerson.create("H.-Robert", "Damien", "Matthes", qualityAssurance);
        CreditsPerson.create("Rakaya", qualityAssurance);
        CreditsPerson.create("Larissa", "Soraja", "Falkenbach", qualityAssurance);
        CreditsPerson.create("Alrik", qualityAssurance);
        CreditsPerson.create("Katharina", qualityAssurance);
        CreditsPerson.create("Mike", "Salathe", "Hudak", qualityAssurance);
        CreditsPerson.create("Slightly", qualityAssurance);
        CreditsPerson.create("PurpleMonkeys", qualityAssurance);
        CreditsPerson.create("GolfLima", qualityAssurance);
        CreditsPerson.create("Victor", "Vigalf", "Becker", qualityAssurance);
        CreditsPerson.create("Cindy", "Elynah", "Ludwig", qualityAssurance);
        CreditsPerson.create("Mesha", maps);
        CreditsPerson.create("Oliver", "Herzog", maps);
        CreditsPerson.create("Evie", chiefMap, content, maps);
        CreditsPerson.create("Quirkily", content, maps);
        CreditsPerson.create("Arien Edhel", specialThanks);
        CreditsPerson.create("Jan", "Alatar", "Falke", specialThanks);
        CreditsPerson.create("Aragon ben Galwan", specialThanks);
        CreditsPerson.create("void256", specialThanks);
        CreditsPerson.create("Illarion", "e.V.", presentedBy);

        singlePosList.add(projectManager);
        singlePosList.add(chiefContent);
        singlePosList.add(chiefClient);
        singlePosList.add(chiefGraphics);
        singlePosList.add(chiefMusic);
        singlePosList.add(chiefMap);
        singlePosList.add(chiefServer);
        singlePosList.add(presentedBy);

        multiPosList.add(gameplay);
        multiPosList.add(content);
        multiPosList.add(client);
        multiPosList.add(maps);
        multiPosList.add(website);
        multiPosList.add(easyNPC);
        multiPosList.add(easyQuest);
        multiPosList.add(mapEditor);
        multiPosList.add(server);
        multiPosList.add(gameMaster);
        multiPosList.add(communityManager);
        multiPosList.add(qualityAssurance);
        multiPosList.add(specialThanks);
    }

    /**
     * Get the iterator over the list of entries that are assigned to a single person.
     *
     * @return the single person entries
     */
    @Nonnull
    public Iterator<CreditsList> getSingleLists() {
        return singlePosList.iterator();
    }

    /**
     * Get the iterator over the list of entries that are assigned to multiple persons.
     *
     * @return the multi person entries
     */
    @Nonnull
    public Iterator<CreditsList> getMultiLists() {
        return multiPosList.iterator();
    }
}
