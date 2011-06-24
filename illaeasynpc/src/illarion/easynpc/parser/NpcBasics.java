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
package illarion.easynpc.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Segment;

import jsyntaxpane.Token;
import jsyntaxpane.TokenType;

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.data.BooleanFlagValues;
import illarion.easynpc.data.CharacterDirection;
import illarion.easynpc.data.CharacterLanguage;
import illarion.easynpc.data.CharacterRace;
import illarion.easynpc.data.CharacterSex;
import illarion.easynpc.data.Towns;
import illarion.easynpc.docu.DocuEntry;

import illarion.common.util.Location;

/**
 * This NpcType is able to parse the basic informations out of a easyNPC script.
 * Such informations are the name and the location, race and so on of the NPC.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public final class NpcBasics implements NpcType {
    /**
     * This internal class is a helper class for the documentation. Each
     * instance of this class contains the documentation data for one command
     * the NPC Basics parser manages.
     * 
     * @author Martin Karing
     * @since 1.01
     */
    private static final class ChildDocuClass implements DocuEntry {
        /**
         * The key for the description of this command.
         */
        private final String docuDesc;

        /**
         * The key for the examples of this command.
         */
        private final String docuEx;

        /**
         * The key for the syntax of this command.
         */
        private final String docuSyntax;

        /**
         * The key for the title of this command.
         */
        private final String docuTitle;

        /**
         * The default constructor that prepares the key values.
         * 
         * @param name the name that is part of the key value to identify the
         *            header entry
         */
        @SuppressWarnings("nls")
        public ChildDocuClass(final String name) {
            docuTitle = "Docu." + name + ".title";
            docuDesc = "Docu." + name + ".description";
            docuEx = "Docu." + name + ".example";
            docuSyntax = "Docu." + name + ".syntax";
        }

        @SuppressWarnings("nls")
        @Override
        public DocuEntry getChild(final int index) {
            throw new IllegalArgumentException(
                "There are no childs to request.");
        }

        @Override
        public int getChildCount() {
            return 0;
        }

        @Override
        public String getDescription() {
            return Lang.getMsg(NpcBasics.class, docuDesc);
        }

        @Override
        public String getExample() {
            return Lang.getMsg(NpcBasics.class, docuEx);
        }

        @Override
        public String getSyntax() {
            return Lang.getMsg(NpcBasics.class, docuSyntax);
        }

        @Override
        public String getTitle() {
            return Lang.getMsg(NpcBasics.class, docuTitle);
        }

    }

    /**
     * The pattern to find a affiliation of this NPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_AFFILIATION = Pattern.compile(
        "^\\s*(affiliation)\\s*=\\s*\"*([a-z]+)\"*[\\s;]*",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    /**
     * The pattern to find a author name in this NPC script.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_AUTHOR = Pattern.compile(
        "^\\s*(author)\\s*=\\s*\"*([^\"]+)\"*[\\s;]*", Pattern.MULTILINE);

    /**
     * The header that stores the flag of the NPC is supposed to auto introduce
     * himself or not.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_AUTO_INTRO = Pattern.compile(
        "^\\s*(autointroduce)\\s*=\\s*([a-z]+)\\s*$", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE);

    /**
     * The header that stores the German version of the message displayed if the
     * character talks in a language the NPC does not understand.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_CONFUSED_DE = Pattern.compile(
        "^\\s*(wrongLangDE)\\s*=\\s*\"(.*)\"\\s*$", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE);

    /**
     * The header that stores the English version of the message displayed if
     * the character talks in a language the NPC does not understand.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_CONFUSED_US = Pattern.compile(
        "^\\s*(wrongLangUS)\\s*=\\s*\"(.*)\"\\s*$", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE);

    /**
     * The pattern to find the default language of this NPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_DEFAULT_LANG = Pattern.compile(
        "^\\s*(defaultLanguage)\\s*=\\s*\"*([a-z]+)\"*[\\s;]*",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    /**
     * The pattern to find the looking direction of the NPC in the easyNPC
     * script.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_DIRECTION = Pattern.compile(
        "^\\s*(dire?c?t?i?o?n?)\\s*=\\s*\"*([a-z]+)\"*[\\s;]*",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    /**
     * The pattern to find a job of this NPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_JOB = Pattern.compile(
        "^\\s*(job)\\s*=\\s*\"*([^\"]+)\"*[\\s;]*", Pattern.MULTILINE);

    /**
     * The pattern to find a language of this NPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_LANGUAGE = Pattern.compile(
        "^\\s*(language)\\s*=\\s*\"*([a-z]+)\"*[\\s;]*",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    /**
     * The header that stores the German version of the message displayed on a
     * look at.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_LOOKAT_DE = Pattern.compile(
        "^\\s*(lookatDE)\\s*=\\s*\"(.*)\"\\s*$", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE);

    /**
     * The header that stores the English version of the message displayed on a
     * look at.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_LOOKAT_US = Pattern.compile(
        "^\\s*(lookatUS)\\s*=\\s*\"(.*)\"\\s*$", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE);

    /**
     * The pattern to find the name of the NPC in this easyNPC script.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_NAME = Pattern.compile(
        "^\\s*(name)\\s*=\\s*\"*([^\"]+)\"*[\\s;]*", Pattern.MULTILINE);

    /**
     * The pattern to find the position of the NPC in the easyNPC script.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_POSITION =
        Pattern
            .compile(
                "^\\s*(position)\\s*=\\s*(-*[0-9]+)[, ]+(-*[0-9]+)[, ]+(-*[0-9]+)[\\s;]*",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    /**
     * The pattern to find the race of the NPC in the easyNPC script.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_RACE = Pattern.compile(
        "^\\s*(race)\\s*=\\s*\"*([a-z ]+)\"*[\\s;]*", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE);

    /**
     * The pattern to find the sex of the NPC in the easyNPC script.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_SEX = Pattern.compile(
        "^\\s*(sex)\\s*=\\s*\"*([a-z]+)\"*[\\s;]*", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE);

    /**
     * The header that stores the German version of the message displayed on a
     * useNPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_USE_DE = Pattern.compile(
        "^\\s*(useMsgDE)\\s*=\\s*\"(.*)\"\\s*$", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE);

    /**
     * The header that stores the English version of the message displayed on a
     * useNPC.
     */
    @SuppressWarnings("nls")
    private static final Pattern HEADER_USE_US = Pattern.compile(
        "^\\s*(useMsgUS)\\s*=\\s*\"(.*)\"\\s*$", Pattern.CASE_INSENSITIVE
            | Pattern.MULTILINE);

    /**
     * The list of documentation entries that are insert as children of the NPC
     * Basics parser.
     */
    private final DocuEntry[] childEntries;

    /**
     * The Constructor that prepares the lists needed for this class to work
     * properly.
     */
    @SuppressWarnings("nls")
    public NpcBasics() {
        final List<DocuEntry> list = new ArrayList<DocuEntry>();

        list.add(new ChildDocuClass("Name"));
        list.add(new ChildDocuClass("Sex"));
        list.add(new ChildDocuClass("Race"));
        list.add(new ChildDocuClass("Position"));
        list.add(new ChildDocuClass("Direction"));
        list.add(new ChildDocuClass("Author"));
        list.add(new ChildDocuClass("Job"));
        list.add(new ChildDocuClass("Affiliation"));
        list.add(new ChildDocuClass("Language"));
        list.add(new ChildDocuClass("DefaultLang"));
        list.add(new ChildDocuClass("Lookat"));
        list.add(new ChildDocuClass("Use"));
        list.add(new ChildDocuClass("Confused"));
        list.add(new ChildDocuClass("AutoIntro"));

        childEntries = list.toArray(new DocuEntry[list.size()]);
    }

    /**
     * Check if this line can be parsed by this parser.
     */
    @Override
    public boolean canParseLine(final EasyNpcScript.Line lineStruct) {
        final String line = lineStruct.getLine();
        if (HEADER_NAME.matcher(line).find()) {
            return true;
        }
        if (HEADER_SEX.matcher(line).find()) {
            return true;
        }
        if (HEADER_DIRECTION.matcher(line).find()) {
            return true;
        }
        if (HEADER_RACE.matcher(line).find()) {
            return true;
        }
        if (HEADER_POSITION.matcher(line).find()) {
            return true;
        }
        if (HEADER_AUTHOR.matcher(line).find()) {
            return true;
        }
        if (HEADER_JOB.matcher(line).find()) {
            return true;
        }
        if (HEADER_AFFILIATION.matcher(line).find()) {
            return true;
        }
        if (HEADER_LANGUAGE.matcher(line).find()) {
            return true;
        }
        if (HEADER_DEFAULT_LANG.matcher(line).find()) {
            return true;
        }
        if (HEADER_LOOKAT_DE.matcher(line).find()) {
            return true;
        }
        if (HEADER_LOOKAT_US.matcher(line).find()) {
            return true;
        }
        if (HEADER_USE_DE.matcher(line).find()) {
            return true;
        }
        if (HEADER_USE_US.matcher(line).find()) {
            return true;
        }
        if (HEADER_CONFUSED_DE.matcher(line).find()) {
            return true;
        }
        if (HEADER_CONFUSED_US.matcher(line).find()) {
            return true;
        }
        if (HEADER_AUTO_INTRO.matcher(line).find()) {
            return true;
        }

        return false;
    }

    @SuppressWarnings("nls")
    @Override
    public DocuEntry getChild(final int index) {
        if ((index < 0) || (index > (childEntries.length - 1))) {
            throw new IllegalArgumentException("Index out of range.");
        }
        return childEntries[index];
    }

    @Override
    public int getChildCount() {
        return childEntries.length;
    }

    @Override
    public String getDescription() {
        return Lang.getMsg(getClass(), "Docu.description"); //$NON-NLS-1$
    }

    @Override
    public String getExample() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(getClass(), "Docu.title"); //$NON-NLS-1$
    }

    /**
     * Parse the line and add the found informations to the ParsedNpc object
     * that is the target of this operation.
     */
    @Override
    @SuppressWarnings("nls")
    public void parseLine(final EasyNpcScript.Line lineStruct,
        final ParsedNpc npc) {
        Matcher matcher;

        final String line = lineStruct.getLine();

        matcher = HEADER_NAME.matcher(line);
        if (matcher.find()) {
            final String name = matcher.group(2);
            npc.setNpcName(name);
            return;
        }

        matcher = HEADER_SEX.matcher(line);
        if (matcher.find()) {
            final String sex = matcher.group(2);

            for (final CharacterSex sexConstant : CharacterSex.values()) {
                if (sex.equalsIgnoreCase(sexConstant.name())) {
                    npc.setNpcSex(sexConstant);
                    return;
                }
            }

            final StringBuilder errorBuilder = new StringBuilder();
            errorBuilder.append(Lang.getMsg(getClass(), "sex"));
            errorBuilder.append(' ');
            for (final CharacterSex sexConstant : CharacterSex.values()) {
                errorBuilder.append(sexConstant.name());
                errorBuilder.append(", ");
            }
            errorBuilder.setLength(errorBuilder.length() - 2);

            npc.addError(lineStruct, errorBuilder.toString());
            return;
        }

        matcher = HEADER_DIRECTION.matcher(line);
        if (matcher.find()) {
            final String dir = matcher.group(2);

            for (final CharacterDirection dirConstant : CharacterDirection
                .values()) {
                if (dir.equalsIgnoreCase(dirConstant.name())) {
                    npc.setNpcDir(dirConstant);
                    return;
                }
            }

            final StringBuilder errorBuilder = new StringBuilder();
            errorBuilder.append(Lang.getMsg(getClass(), "direction"));
            errorBuilder.append(' ');
            for (final CharacterDirection dirConstant : CharacterDirection
                .values()) {
                errorBuilder.append(dirConstant.name());
                errorBuilder.append(", ");
            }

            errorBuilder.setLength(errorBuilder.length() - 2);
            npc.addError(lineStruct, errorBuilder.toString());
            return;
        }

        matcher = HEADER_RACE.matcher(line);
        if (matcher.find()) {
            final String race = matcher.group(2).trim();

            for (final CharacterRace raceConstant : CharacterRace.values()) {
                if (race.equalsIgnoreCase(raceConstant.name())) {
                    npc.setNpcRace(raceConstant);
                    return;
                }
            }

            final StringBuilder errorBuilder = new StringBuilder();
            errorBuilder.append(Lang.getMsg(getClass(), "race"));
            errorBuilder.append(' ');
            for (final CharacterRace raceConstant : CharacterRace.values()) {
                errorBuilder.append(raceConstant.name());
                errorBuilder.append(", ");
            }

            errorBuilder.setLength(errorBuilder.length() - 2);
            npc.addError(lineStruct, errorBuilder.toString());
            return;
        }

        matcher = HEADER_POSITION.matcher(line);
        if (matcher.find()) {
            final int x = Integer.parseInt(matcher.group(2));
            final int y = Integer.parseInt(matcher.group(3));
            final int z = Integer.parseInt(matcher.group(4));

            final Location loc = Location.getInstance();
            loc.setSC(x, y, z);
            npc.setNpcPos(loc);
            return;
        }

        matcher = HEADER_AUTHOR.matcher(line);
        if (matcher.find()) {
            final String name = matcher.group(2).trim();
            npc.addAuthor(name);
            return;
        }

        matcher = HEADER_JOB.matcher(line);
        if (matcher.find()) {
            final String job = matcher.group(2).trim();
            npc.setJob(job);
            return;
        }

        matcher = HEADER_AFFILIATION.matcher(line);
        if (matcher.find()) {
            final String aff = matcher.group(2).trim();

            for (final Towns testTown : Towns.values()) {
                if (aff.equalsIgnoreCase(testTown.name())) {
                    npc.setAffiliation(testTown);
                    return;
                }
            }

            final StringBuilder errorBuilder = new StringBuilder();
            errorBuilder.append(Lang.getMsg(getClass(), "affiliation"));
            errorBuilder.append(' ');
            for (final Towns testTown : Towns.values()) {
                errorBuilder.append(testTown.name());
                errorBuilder.append(", ");
            }

            errorBuilder.setLength(errorBuilder.length() - 2);
            npc.addError(lineStruct, errorBuilder.toString());
            return;
        }

        matcher = HEADER_LANGUAGE.matcher(line);
        if (matcher.find()) {
            final String lang = matcher.group(2).trim();

            for (final CharacterLanguage testLang : CharacterLanguage.values()) {
                if (lang.equalsIgnoreCase(testLang.name())) {
                    npc.addLanguage(testLang);
                    return;
                }
            }

            final StringBuilder errorBuilder = new StringBuilder();
            errorBuilder.append(Lang.getMsg(getClass(), "language"));
            errorBuilder.append(' ');
            for (final CharacterLanguage testLang : CharacterLanguage.values()) {
                errorBuilder.append(testLang.name());
                errorBuilder.append(", ");
            }

            errorBuilder.setLength(errorBuilder.length() - 2);
            npc.addError(lineStruct, errorBuilder.toString());
            return;
        }

        matcher = HEADER_DEFAULT_LANG.matcher(line);
        if (matcher.find()) {
            final String lang = matcher.group(2).trim();

            for (final CharacterLanguage testLang : CharacterLanguage.values()) {
                if (lang.equalsIgnoreCase(testLang.name())) {
                    npc.setDefaultLanguage(testLang);
                    return;
                }
            }

            final StringBuilder errorBuilder = new StringBuilder();
            errorBuilder.append(Lang.getMsg(getClass(), "defaultLang"));
            errorBuilder.append(' ');
            for (final CharacterLanguage testLang : CharacterLanguage.values()) {
                errorBuilder.append(testLang.name());
                errorBuilder.append(", ");
            }

            errorBuilder.setLength(errorBuilder.length() - 2);
            npc.addError(lineStruct, errorBuilder.toString());
            return;
        }

        matcher = HEADER_AUTO_INTRO.matcher(line);
        if (matcher.find()) {
            final String value = matcher.group(2).trim();

            for (final BooleanFlagValues testValue : BooleanFlagValues
                .values()) {
                if (testValue.getPattern().matcher(value).matches()) {
                    npc.setAutoIntroduce(testValue);
                    return;
                }
            }

            final StringBuilder errorBuilder = new StringBuilder();
            errorBuilder.append(Lang.getMsg(getClass(), "autoIntro"));
            errorBuilder.append(' ');
            for (final BooleanFlagValues testValue : BooleanFlagValues
                .values()) {
                errorBuilder.append(testValue.getEasyNpc());
                errorBuilder.append(", ");
            }

            errorBuilder.setLength(errorBuilder.length() - 2);
            npc.addError(lineStruct, errorBuilder.toString());
            return;
        }

        matcher = HEADER_LOOKAT_DE.matcher(line);
        if (matcher.find()) {
            final String germanLookAt = matcher.group(2).trim();
            if (germanLookAt.replace("\\\"", "''").contains("\"")) {
                npc.addError(lineStruct, Lang.getMsg(getClass(), "lookat"));
            }
            npc.setGermanLookat(germanLookAt);
            return;
        }

        matcher = HEADER_LOOKAT_US.matcher(line);
        if (matcher.find()) {
            final String englishLookAt = matcher.group(2).trim();
            if (englishLookAt.replace("\\\"", "''").contains("\"")) {
                npc.addError(lineStruct, Lang.getMsg(getClass(), "lookat"));
            }
            npc.setEnglishLookat(englishLookAt);
            return;
        }

        matcher = HEADER_USE_DE.matcher(line);
        if (matcher.find()) {
            final String germanUse = matcher.group(2).trim();
            if (germanUse.replace("\\\"", "''").contains("\"")) {
                npc.addError(lineStruct, Lang.getMsg(getClass(), "use"));
            }
            npc.setGermanUse(germanUse);
            return;
        }

        matcher = HEADER_USE_US.matcher(line);
        if (matcher.find()) {
            final String englishUse = matcher.group(2).trim();
            if (englishUse.replace("\\\"", "''").contains("\"")) {
                npc.addError(lineStruct, Lang.getMsg(getClass(), "use"));
            }
            npc.setEnglishUse(englishUse);
            return;
        }

        matcher = HEADER_CONFUSED_DE.matcher(line);
        if (matcher.find()) {
            final String germanConfused = matcher.group(2).trim();
            if (germanConfused.replace("\\\"", "''").contains("\"")) {
                npc.addError(lineStruct, Lang.getMsg(getClass(), "confused"));
            }
            npc.setGermanWrongLang(germanConfused);
            return;
        }

        matcher = HEADER_CONFUSED_US.matcher(line);
        if (matcher.find()) {
            final String englishConfused = matcher.group(2).trim();
            if (englishConfused.replace("\\\"", "''").contains("\"")) {
                npc.addError(lineStruct, Lang.getMsg(getClass(), "confused"));
            }
            npc.setEnglishWrongLang(englishConfused);
            return;
        }

        npc.addError(lineStruct, Lang.getMsg(getClass(), "generalFailure"));
    }

    @Override
    public void parseSegment(final Segment segment, final int offset,
        final List<Token> tokens) {
        parseSegmentImpl(segment, offset, tokens, HEADER_NAME);
        parseSegmentImpl(segment, offset, tokens, HEADER_SEX);
        parseSegmentImpl(segment, offset, tokens, HEADER_DIRECTION);
        parseSegmentImpl(segment, offset, tokens, HEADER_RACE);
        parseSegmentImpl(segment, offset, tokens, HEADER_POSITION);
        parseSegmentImpl(segment, offset, tokens, HEADER_AUTHOR);
        parseSegmentImpl(segment, offset, tokens, HEADER_JOB);
        parseSegmentImpl(segment, offset, tokens, HEADER_AFFILIATION);
        parseSegmentImpl(segment, offset, tokens, HEADER_LANGUAGE);
        parseSegmentImpl(segment, offset, tokens, HEADER_DEFAULT_LANG);
        parseSegmentImpl(segment, offset, tokens, HEADER_LOOKAT_DE);
        parseSegmentImpl(segment, offset, tokens, HEADER_LOOKAT_US);
        parseSegmentImpl(segment, offset, tokens, HEADER_USE_DE);
        parseSegmentImpl(segment, offset, tokens, HEADER_USE_US);
        parseSegmentImpl(segment, offset, tokens, HEADER_CONFUSED_DE);
        parseSegmentImpl(segment, offset, tokens, HEADER_CONFUSED_US);
        parseSegmentImpl(segment, offset, tokens, HEADER_AUTO_INTRO);
    }

    /**
     * Additional implementation to parse a segment. This reads all lines in the
     * segment in and matches it against the pattern handed over.
     * 
     * @param segment the segment
     * @param offset the offset to the start of the segment
     * @param tokens the list of old tokens
     * @param pattern the pattern used to check
     */
    private void parseSegmentImpl(final Segment segment, final int offset,
        final List<Token> tokens, final Pattern pattern) {
        final Matcher matcher = pattern.matcher(segment);
        while (matcher.find()) {
            tokens.add(new Token(TokenType.KEYWORD, matcher.start(1) + offset,
                matcher.end(1) - matcher.start(1)));
        }
    }
}
