/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyNPC Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyNPC Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.easynpc;

import illarion.common.types.Location;
import illarion.easynpc.data.*;
import illarion.easynpc.parsed.ParsedData;
import illarion.easynpc.writer.EasyNpcWritable;
import illarion.easynpc.writer.LuaWritable;
import javolution.util.FastTable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.Normalizer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class contains a parsed NPC structure. A detailed and analyzed version of a easyNPC script that is easily
 * maintainable by this application and easy to convert into a LUA or a easyNPC script.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ParsedNpc {
    /**
     * This support class is used to store all information regarding a error that was found in the script. It stores
     * the position where it was found as well as the description of the error.
     */
    public static final class Error implements Comparable<ParsedNpc.Error> {
        /**
         * The line the error occurred at.
         */
        private final int lineNumber;

        /**
         * The character the error occurred at.
         */
        private final int characterNumber;

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
        Error(final int problemLine, final int problemChar, final String errorMsg) {
            lineNumber = problemLine;
            characterNumber = problemChar;
            message = errorMsg;
        }

        /**
         * Compare method used to sort the errors.
         */
        @Override
        public int compareTo(@Nonnull final ParsedNpc.Error o) {
            if (lineNumber == o.lineNumber) {
                return characterNumber - characterNumber;
            }
            return lineNumber - o.lineNumber;
        }

        @Override
        public int hashCode() {
            return lineNumber + (characterNumber << 13);
        }

        @Override
        public boolean equals(final Object o) {
            if (super.equals(o)) {
                return true;
            }

            if (o instanceof ParsedNpc.Error) {
                Error errObj = (Error) o;
                return errObj.lineNumber == lineNumber && errObj.characterNumber == characterNumber &&
                        errObj.message.equals(message);
            }
            return false;
        }

        /**
         * Get the line the error occurred on.
         *
         * @return the line with the error
         */
        public int getLine() {
            return lineNumber;
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
     * The town affiliation of this NPC.
     */
    private Towns affiliation;

    /**
     * The list of authors who wrote this script.
     */
    private FastTable<String> authors;

    /**
     * The auto introduce flag of the NPC. If this is set to false, the NPC will not introduce automatically.
     */
    private boolean autoIntroduce = true;

    /**
     * The language the NPC is talking by default.
     */
    @Nullable
    private CharacterLanguage defaultLanguage;

    /**
     * A list of errors occurred while parsing this NPC.
     */
    private List<Error> errors;

    /**
     * Flag that stores if the error messages are sorted correctly or not.
     */
    private boolean errorOrderDirty;

    /**
     * The job of the NPC.
     */
    private String job;

    /**
     * The list of languages the NPC is able to speak.
     */
    private Collection<CharacterLanguage> languages;

    /**
     * The German version of the message displayed in case a character looks at
     * the NPC.
     */
    private String lookAtDe;

    /**
     * The English version of the message displayed in case a character looks at the NPC.
     */
    private String lookAtUs;

    /**
     * The additional data the NPC contains.
     */
    private List<ParsedData> npcData;

    /**
     * The direction the NPC is looking at.
     */
    private CharacterDirection npcDir;

    /**
     * The name of this NPC.
     */
    private String npcName;

    /**
     * The name of the module of the NPC and in the same consequence the name of the file the NPC needs to be stored
     * in.
     */
    private String moduleName;

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
     * The German version of the message displayed in case the player uses the NPC.
     */
    private String useMsgDe;

    /**
     * The English version of the message displayed in case the player uses the NPC.
     */
    private String useMsgUs;

    /**
     * The German version of the message the NPC speaks in case a character talks to him in a invalid language.
     */
    private String wrongLanguageDe;

    /**
     * The English version of the message the NPC speaks in case a character talks to him in a invalid language.
     */
    private String wrongLanguageUs;

    /**
     * Constructor for the class that creates the required object to store all
     * data for this NPC.
     */
    public ParsedNpc() {
        affiliation = Towns.None;
        job = "none"; //$NON-NLS-1$
        errorOrderDirty = false;
        defaultLanguage = null;
    }

    /**
     * Add one name to the list of authors.
     *
     * @param author the name of the author to add
     */
    public void addAuthor(final String author) {
        if (authors == null) {
            authors = new FastTable<>();
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
    @Deprecated
    public void addError(final EasyNpcScript.Line line, final String message) {
        addError(line.getLineNumber(), message);
    }

    /**
     * Add a error to the list of errors that occurred while this NPC was
     * parsed.
     *
     * @param line the line the error occurred at
     * @param message the message describing the error
     */
    public void addError(final int line, final String message) {
        addError(line, 0, message);
    }

    /**
     * Add a error to the list of errors that occurred while this NPC was
     * parsed.
     *
     * @param line the line the error occurred at
     * @param message the message describing the error
     */
    public void addError(final int line, final int charNr, final String message) {
        if (errors == null) {
            errors = new FastTable<>();
        }
        errors.add(new ParsedNpc.Error(line, charNr, message));
        errorOrderDirty = true;
    }

    /**
     * Add a language to the list of languages the NPC is able to speak.
     *
     * @param lang the language to add
     */
    public void addLanguage(final CharacterLanguage lang) {
        if (languages == null) {
            languages = new FastTable<>();
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
            npcData = new FastTable<>();
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
    @Nonnull
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
    public boolean getAutoIntroduce() {
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
    @Nullable
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
        if (lookAtUs == null) {
            return "This is a NPC who's developer was too lazy to type in a description.";
        }
        return lookAtUs;
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
    public ParsedNpc.Error getError(final int index) {
        if (errors == null) {
            throw new IndexOutOfBoundsException("No errors stored.");
        }
        if (errorOrderDirty) {
            Collections.sort(errors);
            errorOrderDirty = false;
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
        if (lookAtDe == null) {
            return "Das ist ein NPC dessen Entwickler zu faul war eine Beschreibung einzutragen.";
        }
        return lookAtDe;
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
    @Nonnull
    public CharacterLanguage[] getLanguages() {
        if (languages == null) {
            languages = new FastTable<>();
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

            final CharacterLanguage[] result = languages.toArray(new CharacterLanguage[languages.size()]);
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
    @Nonnull
    @SuppressWarnings("nls")
    public String getLuaFilename() {
        return getModuleName() + ".lua";
    }

    public String getModuleName() {
        if (moduleName == null) {
            return convertToModuleName(getNpcName());
        }
        return moduleName;
    }

    public static String convertToModuleName(@Nonnull final String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFC).replaceAll("[^\\p{ASCII}]", "").replace(' ', '_')
                .toLowerCase();
    }

    public void setModuleName(@Nullable final String moduleName) {
        this.moduleName = moduleName;
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
            npcPos = new Location();
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
        return (errors != null) && !errors.isEmpty();
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
    public void setAutoIntroduce(final boolean newValue) {
        autoIntroduce = newValue;
    }

    /**
     * Set the language this character is speaking by default.
     *
     * @param lang the language this character is speaking by default
     */
    public void setDefaultLanguage(@Nullable final CharacterLanguage lang) {
        defaultLanguage = lang;
    }

    /**
     * Set the English version of the text that is displayed in case the player
     * looks at the NPC.
     *
     * @param msg the message
     */
    public void setEnglishLookAt(final String msg) {
        lookAtUs = msg;
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
    public void setGermanLookAt(final String msg) {
        lookAtDe = msg;
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
}
