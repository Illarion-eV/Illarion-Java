/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.data;

import javax.annotation.Nonnull;
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
    private static Credits instance;

    /**
     * Get the singleton instance of the credits class.
     * <p/>
     * The class instance is created upon the first call of this function.
     *
     * @return the credits instance
     */
    public static Credits getInstance() {
        if (instance == null) {
            synchronized (Credits.class) {
                if (instance == null) {
                    instance = new Credits();
                }
            }
        }
        return instance;
    }

    /**
     * The constructor of the credits.
     * <p/>
     * Be aware, this class will load all the text and objects required to display the credits. Only do so in case
     * you really want to display this stuff.
     */
    private Credits() {
        singlePosList = new ArrayList<>();
        multiPosList = new ArrayList<>();

        final CreditsList projectManager = new CreditsList("Projektleiter", "Project Manager");
        final CreditsList chiefContent = new CreditsList("Leitender Entwickler für Spielinhalte",
                                                         "Chief Game Content Developer");
        final CreditsList chiefClient = new CreditsList("Leitender Entwickler für den Client",
                                                        "Chief Client Developer");
        final CreditsList chiefGraphics = new CreditsList("Leitender Grafiker", "Chief Graphics Designer");
        final CreditsList chiefMusic = new CreditsList("Original-Soundtrack", "Original Soundtrack");
        final CreditsList chiefMap = new CreditsList("Leitender Gestalter für die Karte", "Chief Map Designer");
        final CreditsList chiefServer = new CreditsList("Leitender Entwickler für den Server",
                                                        "Chief Server Developer");
        final CreditsList presentedBy = new CreditsList("Präsentiert vom", "Presented by the");

        final CreditsList gameplay = new CreditsList("Spielmechanik", "Gameplay");
        final CreditsList content = new CreditsList("Spielinhalte", "Game Content");
        final CreditsList client = new CreditsList("Client");
        final CreditsList graphics = new CreditsList("Grafiken", "Graphics");
        final CreditsList maps = new CreditsList("Karten", "Maps");
        final CreditsList website = new CreditsList("Website");
        final CreditsList easyNPC = new CreditsList("easyNPC-Editor", "easyNPC Editor");
        final CreditsList easyQuest = new CreditsList("easyQuest-Editor", "easyQuest Editor");
        final CreditsList mapEditor = new CreditsList("Karten-Editor", "Map Editor");
        final CreditsList server = new CreditsList("Server");
        final CreditsList gameMaster = new CreditsList("Spielleiter", "Game Master");
        final CreditsList communityManager = new CreditsList("Community-Manager", "Community Manager");
        final CreditsList qualityAssurance = new CreditsList("Qualitätsprüfung", "Quality Assurance");
        final CreditsList specialThanks = new CreditsList("Besonderen Dank", "Special Thanks");

        CreditsPerson
                .create("Andreas", "Vilarion", "Grob", projectManager, chiefServer, gameplay, client, website, easyNPC,
                        easyQuest, server, qualityAssurance);
        CreditsPerson.create("Lennart", "Estralis", "Stutz", chiefContent, gameplay, content, qualityAssurance);
        CreditsPerson.create("Martin", "Nitram", "Karing", chiefClient, gameplay, client, website, easyNPC, easyQuest,
                             mapEditor, server, qualityAssurance);
        CreditsPerson.create("Martin", "Polak", chiefGraphics, gameplay, client, graphics, website, easyNPC, easyQuest,
                             server, qualityAssurance);
        CreditsPerson.create("Oganalp", "Canatan", chiefMusic);
        CreditsPerson.create("Marvin", "Kopp", chiefMusic);
        CreditsPerson.create("Zot", chiefMap, content, graphics, maps, gameMaster, qualityAssurance);
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
        CreditsPerson.create("Martin", "Skamato", "Großmann", content, qualityAssurance, gameMaster);
        CreditsPerson.create("Tiim", client, mapEditor);
        CreditsPerson.create("Fredrik K", mapEditor);
        CreditsPerson.create("Smjert", client);
        CreditsPerson.create("Samaras", graphics);
        CreditsPerson.create("Dandelion", graphics, communityManager);
        CreditsPerson.create("Drakon Gerwulf", graphics);
        CreditsPerson.create("Karl", "Salameh", graphics);
        CreditsPerson.create("Matt", "Raelith", "Hollier", website);
        CreditsPerson.create("Onyxx", gameMaster);
        CreditsPerson.create("Revan", gameMaster);
        CreditsPerson.create("Semtex", gameMaster);
        CreditsPerson.create("Teflon", gameMaster);
        CreditsPerson.create("Arien Edhel", gameMaster);
        CreditsPerson.create("Djironnyma", communityManager);
        CreditsPerson.create("Athian", communityManager);
        CreditsPerson.create("Slightly", communityManager);
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
        CreditsPerson.create("Silverwing", qualityAssurance, gameMaster);
        CreditsPerson.create("Victor", "Vigalf", "Becker", qualityAssurance);
        CreditsPerson.create("Cindy", "Elynah", "Ludwig", qualityAssurance);
        CreditsPerson.create("", "Mesha", "", maps);
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

    public static void main(final String[] args) {
        System.out.println("CREDITS");
        System.out.println();

        final Credits credits = new Credits();
        final Iterator<CreditsList> singleCredits = credits.getSingleLists();
        while (singleCredits.hasNext()) {
            final CreditsList list = singleCredits.next();
            System.out.println(list.getNameEnglish());
            for (final CreditsPerson person : list) {
                System.out.print("\t");
                System.out.println(person.getName());
            }
            System.out.println();
        }

        final Iterator<CreditsList> multiCredits = credits.getMultiLists();
        while (multiCredits.hasNext()) {
            final CreditsList list = multiCredits.next();
            System.out.println(list.getNameEnglish());
            for (final CreditsPerson person : list) {
                System.out.print("\t");
                System.out.println(person.getName());
            }
            System.out.println();
        }
    }
}
