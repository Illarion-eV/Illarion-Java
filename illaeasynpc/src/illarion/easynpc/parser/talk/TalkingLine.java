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
package illarion.easynpc.parser.talk;

import java.util.ArrayList;
import java.util.List;

import illarion.easynpc.EasyNpcScript;
import illarion.easynpc.Lang;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.parsed.ParsedTalk;
import illarion.easynpc.parsed.talk.TalkCondition;
import illarion.easynpc.parsed.talk.TalkConsequence;

/**
 * This class is used to store talking informations of a NPC. It stores the data
 * about one line of talking with all triggers, answers, consequences and
 * conditions.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public final class TalkingLine {
    /**
     * The string used to split the conditions and the consequences of the
     * talking line.
     */
    @SuppressWarnings("nls")
    private static final String SPLIT_STRING = "->";

    /**
     * The documentation entry for the conditions.
     */
    private final DocuEntry conditionDocu;

    /**
     * The list of condition parsers.
     */
    private final ArrayList<ConditionParser> condPar;

    /**
     * The documentation entry for the consequences.
     */
    private final DocuEntry consequenceDocu;

    /**
     * The list of consequence parsers.
     */
    private final ArrayList<ConsequenceParser> consPar;

    /**
     * The constructor that sets up all known parsers this class requires to
     * work properly.
     */
    public TalkingLine() {
        condPar = new ArrayList<ConditionParser>();
        consPar = new ArrayList<ConsequenceParser>();

        condPar.add(new illarion.easynpc.parser.talk.conditions.Trigger());
        condPar.add(new illarion.easynpc.parser.talk.conditions.State());
        condPar.add(new illarion.easynpc.parser.talk.conditions.Skill());
        condPar.add(new illarion.easynpc.parser.talk.conditions.Attribute());
        condPar.add(new illarion.easynpc.parser.talk.conditions.Money());
        condPar.add(new illarion.easynpc.parser.talk.conditions.Race());
        condPar.add(new illarion.easynpc.parser.talk.conditions.Queststatus());
        condPar.add(new illarion.easynpc.parser.talk.conditions.Item());
        condPar.add(new illarion.easynpc.parser.talk.conditions.Language());
        condPar.add(new illarion.easynpc.parser.talk.conditions.Chance());
        condPar.add(new illarion.easynpc.parser.talk.conditions.Town());
        condPar.add(new illarion.easynpc.parser.talk.conditions.Number());
        condPar.add(new illarion.easynpc.parser.talk.conditions.Talkstate());
        condPar.add(new illarion.easynpc.parser.talk.conditions.Sex());
        condPar.add(new illarion.easynpc.parser.talk.conditions.Admin());

        consPar.add(new illarion.easynpc.parser.talk.consequences.Inform());
        consPar.add(new illarion.easynpc.parser.talk.consequences.Answer());
        consPar.add(new illarion.easynpc.parser.talk.consequences.State());
        consPar.add(new illarion.easynpc.parser.talk.consequences.Skill());
        consPar.add(new illarion.easynpc.parser.talk.consequences.Attribute());
        consPar.add(new illarion.easynpc.parser.talk.consequences.Rune());
        consPar.add(new illarion.easynpc.parser.talk.consequences.Money());
        consPar
            .add(new illarion.easynpc.parser.talk.consequences.DeleteItem());
        consPar.add(new illarion.easynpc.parser.talk.consequences.Item());
        consPar
            .add(new illarion.easynpc.parser.talk.consequences.Queststatus());
        consPar
            .add(new illarion.easynpc.parser.talk.consequences.Rankpoints());
        consPar.add(new illarion.easynpc.parser.talk.consequences.Talkstate());
        consPar.add(new illarion.easynpc.parser.talk.consequences.Treasure());
        consPar.add(new illarion.easynpc.parser.talk.consequences.Introduce());

        final List<ConditionParser> conditionsList = condPar;
        final List<ConsequenceParser> consequenceList = consPar;

        conditionDocu = new DocuEntry() {
            @SuppressWarnings("nls")
            @Override
            public DocuEntry getChild(final int index) {
                if ((index < 0) || (index >= conditionsList.size())) {
                    throw new IllegalArgumentException("Index out of range");
                }
                return conditionsList.get(index);
            }

            @Override
            public int getChildCount() {
                return conditionsList.size();
            }

            @SuppressWarnings("nls")
            @Override
            public String getDescription() {
                return Lang.getMsg(TalkingLine.class,
                    "Conditions.Docu.description");
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
            @SuppressWarnings("nls")
            public String getTitle() {
                return Lang.getMsg(TalkingLine.class, "Conditions.Docu.title");
            }
        };

        consequenceDocu = new DocuEntry() {
            @SuppressWarnings("nls")
            @Override
            public DocuEntry getChild(final int index) {
                if ((index < 0) || (index >= consequenceList.size())) {
                    throw new IllegalArgumentException("Index out of range");
                }
                return consequenceList.get(index);
            }

            @Override
            public int getChildCount() {
                return consequenceList.size();
            }

            @SuppressWarnings("nls")
            @Override
            public String getDescription() {
                return Lang.getMsg(TalkingLine.class,
                    "Consequence.Docu.description");
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
            @SuppressWarnings("nls")
            public String getTitle() {
                return Lang
                    .getMsg(TalkingLine.class, "Consequence.Docu.title");
            }
        };
    }

    /**
     * Get the documentation entry for the conditions.
     * 
     * @return the conditions documentation entry
     */
    public DocuEntry getConditionDocuEntry() {
        return conditionDocu;
    }

    /**
     * Get the documentation entry for the consequences.
     * 
     * @return the consequences documentation entry
     */
    public DocuEntry getConsequenceDocuEntry() {
        return consequenceDocu;
    }

    /**
     * Parse a talking line into a properly parsed line.
     * 
     * @param line the line to parse
     * @param npc the npc that receives the data parsed here
     */
    @SuppressWarnings("nls")
    public void parseLine(final EasyNpcScript.Line line, final ParsedNpc npc) {
        final String[] workingLines = line.getLine().split(SPLIT_STRING);

        if (workingLines.length != 2) {
            npc.addError(line, "Invalid line fetched!");
            return;
        }

        String conditions = workingLines[0] + ",";
        String consequences = workingLines[1] + ",";

        final ParsedTalk parsedLine = ParsedTalk.getInstance();

        for (final ConditionParser parser : condPar) {
            parser.setLine(conditions);
            parser.setErrorParent(npc, line);

            TalkCondition con = null;
            while (true) {
                con = parser.extract();
                if (con == null) {
                    break;
                }

                parsedLine.addCondition(con);
            }

            conditions = parser.getNewLine();
            parser.cleanup();
        }

        for (final ConsequenceParser parser : consPar) {
            parser.setLine(consequences);
            parser.setErrorParent(npc, line);

            TalkConsequence con = null;
            while (true) {
                con = parser.extract();
                if (con == null) {
                    break;
                }

                parsedLine.addConsequence(con);
            }

            consequences = parser.getNewLine();
            parser.cleanup();
        }

        if (conditions.length() > 0) {
            npc.addError(line, Lang.getMsg(getClass(), "remainConditions")
                + " " + conditions);
        }
        if (consequences.length() > 0) {
            npc.addError(line, Lang.getMsg(getClass(), "remainConsequences")
                + " " + consequences);
        }

        npc.addNpcData(parsedLine);
    }
}
