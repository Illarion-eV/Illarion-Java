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
package illarion.easynpc;

import java.util.Collections;

import javolution.context.ObjectFactory;
import javolution.util.FastTable;

import illarion.easynpc.data.BooleanFlagValues;
import illarion.easynpc.data.CharacterDirection;
import illarion.easynpc.data.CharacterLanguage;
import illarion.easynpc.data.CharacterRace;
import illarion.easynpc.data.CharacterSex;
import illarion.easynpc.data.Towns;
import illarion.easynpc.parsed.ParsedData;
import illarion.easynpc.writer.EasyNpcWritable;
import illarion.easynpc.writer.LuaWritable;

import illarion.common.util.Location;
import illarion.common.util.Reusable;

/**
 * This class contains a parsed NPC structure. A detailed and analyzed version
 * of a easyNPC script that is easily maintainable by this application and easy
 * to convert into a LUA or a easyNPC script.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.02
 */
public final class ParsedNpc implements Reusable {
    /**
     * This support class is used to store all informations regarding a error
     * that was found in the script. It stores the position where it was found
     * as well as the description of the error.
     * 
     * @author Martin Karing
     * @since 1.00
     * @version 1.01
     */
    public static final class Error implements Comparable<Error> {
        /**
         * The line the error occurred at.
         */
        private final EasyNpcScript.Line line;

        /**
         * The error message.
         */
        private final String message;

        /**
         * Create this error message.
         * 
         * @param problemLine the line that caused this problem
         * @param errorMsg the message of this error
         */
        protected Error(final EasyNpcScript.Line problemLine,
            final String errorMsg) {
            line = problemLine;
            message = errorMsg;
        }

        /**
         * Compare method used to sort the errors.
         */
        @Override
        public int compareTo(final Error o) {
            return line.getLineNumber() - o.line.getLineNumber();
        }

        /**
         * Get the line the error occurred on.
         * 
         * @return the line with the error
         */
        public EasyNpcScript.Line getLine() {
            return line;
        }

        /**
         * Get the message describing the error.
         * 
         * @return the error message
         */
        public String getMessage() {
            return message;
        }
    }

    /**
     * The factory for the parsed NPC. This stores all formerly created and
     * currently unused instances of the ParsedNpc class.
     * 
     * @author Martin Karing
     * @since 1.02
     * @version 1.02
     */
    private static final class ParsedNpcFactory extends
        ObjectFactory<ParsedNpc> {
        /**
         * Public constructor to allow the parent class to create a proper
         * instance.
         */
        public ParsedNpcFactory() {
            // nothing to do
        }

        /**
         * Create a new instance of the recycled object.
         */
        @Override
        protected ParsedNpc create() {
            return new ParsedNpc();
        }
    }

    /**
     * The instance of the factory used to create the objects for this class.
     */
    private static final ParsedNpcFactory FACTORY = new ParsedNpcFactory();

    /**
     * The town affiliation of this NPC.
     */
    private Towns affiliation;

    /**
     * The list of authors who wrote this script.
     */
    private FastTable<String> authors;

    /**
     * The auto introduce flag of the NPC. If this is set to false, the NPC will
     * not introduce automatically.
     */
    private BooleanFlagValues autoIntroduce = null;

    /**
     * The language the NPC is talking by default.
     */
    private CharacterLanguage defaultLanguage;

    /**
     * A list of errors occurred while parsing this NPC.
     */
    private FastTable<Error> errors;

    /**
     * Flag that stores if the error messages are sorted correctly or not.
     */
    private boolean errorsSorted;

    /**
     * The job of the NPC.
     */
    private String job;

    /**
     * The list of languages the NPC is able to speak.
     */
    private FastTable<CharacterLanguage> languages;

    /**
     * The German version of the message displayed in case a character looks at
     * the NPC.
     */
    private String lookatDe = null;

    /**
     * The English version of the message displayed in case a character looks at
     * the NPC.
     */
    private String lookatUs = null;

    /**
     * The additional data the NPC contains.
     */
    private FastTable<ParsedData> npcData;

    /**
     * The direction the NPC is looking at.
     */
    private CharacterDirection npcDir;

    /**
     * The name of this NPC.
     */
    private String npcName;

    /**
     * The position of this NPC.
     */
    private Location npcPos;

    /**
     * The race of the NPC.
     */
    private CharacterRace npcRace;

    /**
     * The sex of this NPC.
     */
    private CharacterSex npcSex;

    /**
     * The German version of the message displayed in case the player uses the
     * NPC.
     */
    private String useMsgDe = null;

    /**
     * The English version of the message displayed in case the player uses the
     * NPC.
     */
    private String useMsgUs = null;

    /**
     * The German version of the message the NPC speaks in case a character
     * talks to him in a invalid language.
     */
    private String wrongLanguageDe = null;

    /**
     * The English version of the message the NPC speaks in case a character
     * talks to him in a invalid language.
     */
    private String wrongLanguageUs = null;

    /**
     * Constructor for the class that creates the required object to store all
     * data for this NPC.
     */
    ParsedNpc() {
        // nothing to do
    }

    /**
     * Get a newly created or a old reused instance of this class that is now
     * free to be used.
     * 
     * @return the instance to use
     */
    public static ParsedNpc getInstance() {
        final ParsedNpc result = FACTORY.object();
        result.prepareInstance();
        return result;
    }

    /**
     * Add one name to the list of authors.
     * 
     * @param author the name of the author to add
     */
    public void addAuthor(final String author) {
        if (authors == null) {
            authors = FastTable.newInstance();
        }
        authors.add(author);
    }

    /**
     * Add a error to the list of errors that occurred while this NPC was
     * parsed.
     * 
     * @param line the line the error occurred at
     * @param message the message describing the error
     */
    public void addError(final EasyNpcScript.Line line, final String message) {
        if (errors == null) {
            errors = FastTable.newInstance();
        }
        errors.add(new Error(line, message));
        errorsSorted = false;
    }

    /**
     * Add a language to the list of languages the NPC is able to speak.
     * 
     * @param lang the language to add
     */
    public void addLanguage(final CharacterLanguage lang) {
        if (languages == null) {
            languages = FastTable.newInstance();
        }
        if (!languages.contains(lang)) {
            languages.add(lang);
        }
    }

    /**
     * Add some complex parsed data to this NPC.
     * 
     * @param data the data to add
     */
    public void addNpcData(final ParsedData data) {
        if (npcData == null) {
            npcData = FastTable.newInstance();
        }
        npcData.add(data);
    }

    /**
     * Get the affiliation of this NPC.
     * 
     * @return the affiliation of this NPC
     */
    public Towns getAffiliation() {
        if (affiliation == null) {
            return Towns.None;
        }
        return affiliation;
    }

    /**
     * Get the list of the names of the authors of this script.
     * 
     * @return a array of the author names
     */
    public String[] getAuthors() {
        if (authors == null) {
            return new String[0];
        }
        return authors.toArray(new String[authors.size()]);
    }

    /**
     * Get the current value of the auto introduce flag.
     * 
     * @return the auto introduce flag
     */
    public BooleanFlagValues getAutoIntroduce() {
        if (autoIntroduce == null) {
            return BooleanFlagValues.on;
        }
        return autoIntroduce;
    }

    /**
     * Get the amount of data entries that are written in this NPC.
     * 
     * @return the amount of data entries
     */
    public int getDataCount() {
        if (npcData == null) {
            return 0;
        }
        return npcData.size();
    }

    /**
     * Get the language this character is speaking by default.
     * 
     * @return the language this character is speaking by default
     */
    public CharacterLanguage getDefaultLanguage() {
        if (defaultLanguage == null) {
            return CharacterLanguage.common;
        }
        return defaultLanguage;
    }

    /**
     * Get the easyNPC data sources for the writer.
     * 
     * @param index the index of the data
     * @return the data at the selected index
     */
    @SuppressWarnings("nls")
    public EasyNpcWritable getEasyNpcData(final int index) {
        if (npcData == null) {
            throw new IndexOutOfBoundsException("No values stored.");
        }
        return npcData.get(index);
    }

    /**
     * Get the English version of the text that is displayed in case the player
     * looks at the NPC.
     * 
     * @return the message
     */
    @SuppressWarnings("nls")
    public String getEnglishLookat() {
        if (lookatUs == null) {
            return "This is a NPC who's developer was too lazy to type in a description.";
        }
        return lookatUs;
    }

    /**
     * Get the English version of the text displayed in case the player uses the
     * NPC.
     * 
     * @return the message
     */
    @SuppressWarnings("nls")
    public String getEnglishUse() {
        if (useMsgUs == null) {
            return "Do not touch me!";
        }
        return useMsgUs;
    }

    /**
     * Get the English version of the message that is displayed in case the
     * player talks to the NPC in the wrong language.
     * 
     * @return the message
     */
    @SuppressWarnings("nls")
    public String getEnglishWrongLang() {
        if (wrongLanguageDe == null) {
            return "#me looks at you confused.";
        }
        return wrongLanguageUs;
    }

    /**
     * Get the error stored at a index.
     * 
     * @param index the index of the error that is requested
     * @return the error stored with this index
     */
    public Error getError(final int index) {
        if (errors == null) {
            throw new IndexOutOfBoundsException("No errors stored.");
        }
        if (!errorsSorted) {
            Collections.sort(errors);
            errorsSorted = true;
        }
        return errors.get(index);
    }

    /**
     * Get the amount of errors found in this script.
     * 
     * @return the amount of errors in this script.
     */
    public int getErrorCount() {
        if (errors == null) {
            return 0;
        }
        return errors.size();
    }

    /**
     * Get the German version of the text that is displayed in case the player
     * looks at the NPC.
     * 
     * @return the message
     */
    @SuppressWarnings("nls")
    public String getGermanLookat() {
        if (lookatDe == null) {
            return "Das ist ein NPC dessen Entwickler zu faul war eine Beschreibung einzutragen.";
        }
        return lookatDe;
    }

    /**
     * Get the German version of the text displayed in case the player uses the
     * NPC.
     * 
     * @return the message
     */
    @SuppressWarnings("nls")
    public String getGermanUse() {
        if (useMsgDe == null) {
            return "Fass mich nicht an!";
        }
        return useMsgDe;
    }

    /**
     * Get the German version of the message that is displayed in case the
     * player talks to the NPC in the wrong language.
     * 
     * @return the message
     */
    @SuppressWarnings("nls")
    public String getGermanWrongLang() {
        if (wrongLanguageDe == null) {
            return "#me schaut dich verwirrt an.";
        }
        return wrongLanguageDe;
    }

    /**
     * Get the Job of this NPC.
     * 
     * @return the job of this NPC
     */
    public String getJob() {
        if (job == null) {
            return "unspecified"; //$NON-NLS-1$
        }
        return job;
    }

    /**
     * Get the languages this NPC is able to speak.
     * 
     * @return the array of languages this NPC is able to speak
     */
    public CharacterLanguage[] getLanguages() {
        if (languages == null) {
            languages = FastTable.newInstance();
        }
        if (languages.isEmpty()) {
            languages.add(CharacterLanguage.common);
            switch (getNpcRace()) {
                case human:
                    languages.add(CharacterLanguage.human);
                    break;
                case dwarf:
                    languages.add(CharacterLanguage.dwarf);
                    break;
                case elf:
                    languages.add(CharacterLanguage.elf);
                    break;
                case orc:
                    languages.add(CharacterLanguage.orc);
                    break;
                case halfling:
                    languages.add(CharacterLanguage.halfling);
                    break;
                case lizardman:
                    languages.add(CharacterLanguage.lizard);
                    break;
            }

            final CharacterLanguage[] result =
                languages.toArray(new CharacterLanguage[languages.size()]);
            languages.clear();
            return result;
        }

        return languages.toArray(new CharacterLanguage[languages.size()]);
    }

    /**
     * Get the LUA data sources for the writer.
     * 
     * @param index the index of the data
     * @return the data at the selected index
     */
    public LuaWritable getLuaData(final int index) {
        return npcData.get(index);
    }

    /**
     * Get the correct name for the LUA script file according to the data saved
     * in this script.
     * 
     * @return the correct lua script file
     */
    @SuppressWarnings("nls")
    public String getLuaFilename() {
        return getNpcName().replace(' ', '_').toLowerCase() + ".lua";
    }

    /**
     * Get the looking direction of this NPC.
     * 
     * @return the looking direction of this NPC
     */
    public CharacterDirection getNpcDir() {
        if (npcDir == null) {
            return CharacterDirection.north;
        }
        return npcDir;
    }

    /**
     * Get the name of this NPC.
     * 
     * @return the name of this NPC
     */
    public String getNpcName() {
        if (npcName == null) {
            if (getNpcSex() == CharacterSex.male) {
                return "John Doe"; //$NON-NLS-1$
            }
            return "Jane Doe"; //$NON-NLS-1$
        }
        return npcName;
    }

    /**
     * Get the location of this NPC.
     * 
     * @return the current location of this NPC.
     */
    public Location getNpcPos() {
        if (npcPos == null) {
            npcPos = Location.getInstance();
            npcPos.setDC(0, 0, 0);
        }
        return npcPos;
    }

    /**
     * Get the race of this NPC.
     * 
     * @return The current set race of this NPC
     */
    public CharacterRace getNpcRace() {
        if (npcRace == null) {
            return CharacterRace.human;
        }
        return npcRace;
    }

    /**
     * Get the sex value of this NPC.
     * 
     * @return the sex value of this NPC
     */
    public CharacterSex getNpcSex() {
        if (npcSex == null) {
            return CharacterSex.male;
        }
        return npcSex;
    }

    /**
     * Check if the script contains any errors.
     * 
     * @return <code>true</code> if there are any errors stored in this parseNPC
     */
    public boolean hasErrors() {
        if (errors == null) {
            return false;
        }
        return !errors.isEmpty();
    }

    /**
     * Put the instance back into the factory for later usage.
     */
    @Override
    public void recycle() {
        reset();
        FACTORY.recycle(this);
    }

    /**
     * Reset the data stored in this instance.
     */
    @Override
    public void reset() {
        if (npcData != null) {
            final int count = npcData.size();
            for (int i = 0; i < count; i++) {
                npcData.get(i).recycle();
            }
            npcData.clear();
            FastTable.recycle(npcData);
            npcData = null;
        }

        if (authors != null) {
            authors.clear();
            FastTable.recycle(authors);
            authors = null;
        }

        if (languages != null) {
            languages.clear();
            FastTable.recycle(languages);
            languages = null;
        }

        if (errors != null) {
            errors.clear();
            FastTable.recycle(errors);
            errors = null;
            errorsSorted = true;
        }

        affiliation = null;
        autoIntroduce = null;
        defaultLanguage = null;
        job = null;
        lookatDe = null;
        lookatUs = null;
        npcDir = null;

        if (npcPos != null) {
            npcPos.recycle();
            npcPos = null;
        }

        npcRace = null;
        useMsgDe = null;
        useMsgUs = null;
        wrongLanguageDe = null;
        wrongLanguageUs = null;
    }

    /**
     * Set the affiliation of this NPC.
     * 
     * @param aff the affiliation of this NPC
     */
    public void setAffiliation(final Towns aff) {
        affiliation = aff;
    }

    /**
     * Set the value for the auto introduce flag.
     * 
     * @param newValue the new value for the auto introduce flag
     */
    public void setAutoIntroduce(final BooleanFlagValues newValue) {
        autoIntroduce = newValue;
    }

    /**
     * Set the language this character is speaking by default.
     * 
     * @param lang the language this character is speaking by default
     */
    public void setDefaultLanguage(final CharacterLanguage lang) {
        defaultLanguage = lang;
    }

    /**
     * Set the English version of the text that is displayed in case the player
     * looks at the NPC.
     * 
     * @param msg the message
     */
    public void setEnglishLookat(final String msg) {
        lookatUs = msg;
    }

    /**
     * Set the English version of the text displayed in case the player uses the
     * NPC.
     * 
     * @param msg the message
     */
    public void setEnglishUse(final String msg) {
        useMsgUs = msg;
    }

    /**
     * Set the English version of the message that is displayed in case the
     * player talks to the NPC in the wrong language.
     * 
     * @param msg the message
     */

    public void setEnglishWrongLang(final String msg) {
        wrongLanguageUs = msg;
    }

    /**
     * Set the German version of the text that is displayed in case the player
     * looks at the NPC.
     * 
     * @param msg the message
     */
    public void setGermanLookat(final String msg) {
        lookatDe = msg;
    }

    /**
     * Set the German version of the text displayed in case the player uses the
     * NPC.
     * 
     * @param msg the message
     */
    public void setGermanUse(final String msg) {
        useMsgDe = msg;
    }

    /**
     * Set the German version of the message that is displayed in case the
     * player talks to the NPC in the wrong language.
     * 
     * @param msg the message
     */
    public void setGermanWrongLang(final String msg) {
        wrongLanguageDe = msg;
    }

    /**
     * Set the job of this NPC.
     * 
     * @param newJob the job of this NPC
     */
    public void setJob(final String newJob) {
        job = newJob;
    }

    /**
     * Set the new direction of this NPC.
     * 
     * @param newNpcDir the new looking direction of this NPC
     */
    public void setNpcDir(final CharacterDirection newNpcDir) {
        npcDir = newNpcDir;
    }

    /**
     * Set the name of this NPC.
     * 
     * @param newNpcName the new name of this NPC.
     */
    public void setNpcName(final String newNpcName) {
        npcName = newNpcName;
    }

    /**
     * Set the position of this NPC.
     * 
     * @param newNpcPos the new position of this NPC
     */
    public void setNpcPos(final Location newNpcPos) {
        if (npcPos != null) {
            npcPos.recycle();
        }
        npcPos = newNpcPos;
    }

    /**
     * Set the race of this NPC to a new value.
     * 
     * @param newNpcRace the race of this NPC
     */
    public void setNpcRace(final CharacterRace newNpcRace) {
        npcRace = newNpcRace;
    }

    /**
     * Set the sex of this NPC to a new value.
     * 
     * @param newNpcSex the new sex value of the NPC
     */
    public void setNpcSex(final CharacterSex newNpcSex) {
        npcSex = newNpcSex;
    }

    /**
     * Prepare this instance for usage.
     */
    private void prepareInstance() {
        affiliation = Towns.None;
        job = "none"; //$NON-NLS-1$
        errorsSorted = true;
        defaultLanguage = null;
    }
}
