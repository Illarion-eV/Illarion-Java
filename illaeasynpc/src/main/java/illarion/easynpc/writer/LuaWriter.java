/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package illarion.easynpc.writer;

import illarion.common.util.CopyrightHeader;
import illarion.easynpc.ParsedNpc;
import illarion.easynpc.Parser;
import illarion.easynpc.data.CharacterLanguage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This is the LUA writer. It writes the data supplied by a parsed NPC to a lua script.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class LuaWriter {
    /**
     * The stages of the writing of the script.
     */
    public enum WritingStage {
        /**
         * Clothes writing stage. Everything needed to setup the initialization of the equipment of the NPC should be
         * written here.
         */
        Clothes,

        /**
         * The writing stage for the guard part of NPCs.
         */
        Guarding,

        /**
         * Header writing stage. This lines appears in the head of the LUA script.
         */
        Header,

        /**
         * Talking writing stage. All things related to the initialization of the speech of the NPC should be written
         * here.
         */
        Talking,

        /**
         * Trading writing stage. All things related to the implementation of the trader functions should be written
         * here.
         */
        Trading
    }

    /**
     * The new line string that is used by default for this scripts.
     */
    public static final String NL = "\n"; //$NON-NLS-1$

    /**
     * The code used to add a new language the NPC is able to speak.
     */
    @SuppressWarnings("nls")
    private static final String addLanguageCode = "mainNPC:addLanguage(%1$s);";

    /**
     * The code used to set the auto introduction mode.
     */
    @SuppressWarnings("nls")
    private static final String addSetAutoIntro = "mainNPC:setAutoIntroduceMode(%1$s);";

    /**
     * The comment separator line used in the comments.
     */
    @SuppressWarnings("nls")
    private static final String commentSepLine = "--------------------------------------------------------------------------------";

    /**
     * The singleton instance of this class.
     */
    private static final LuaWriter INSTANCE = new LuaWriter();

    /**
     * The LUA code used for a require module entry.
     */
    @SuppressWarnings("nls")
    private static final String requireCode = "require(\"%1$s\")";

    /**
     * The LUA code needed to set the message displayed in case a character is
     * talking in the wrong language.
     */
    @SuppressWarnings("nls")
    private static final String setConfusedMessageCode = "mainNPC:setConfusedMessage(\"%1$s\", \"%2$s\");";

    /**
     * The LUA code needed to set the default language of an NPC.
     */
    @SuppressWarnings("nls")
    private static final String setDefaultLanguageCode = "mainNPC:setDefaultLanguage(%1$s);";

    /**
     * The LUA code needed to set the message displayed when the player performs
     * a look at on the NPC.
     */
    @SuppressWarnings("nls")
    private static final String setLookatMessageCode = "mainNPC:setLookat(\"%1$s\", \"%2$s\");";

    /**
     * The LUA code needed to set the affiliation of a NPC.
     */
    @SuppressWarnings("nls")
    private static final String setAffiliationCode = "mainNPC:setAffiliation(%1$s);";

    /**
     * The LUA code needed to set the message displayed in case the player uses
     * the NPC.
     */
    @SuppressWarnings("nls")
    private static final String setUseMessageCode = "mainNPC:setUseMessage(\"%1$s\", \"%2$s\");";

    /**
     * The copyright header of the LUA writer.
     */
    @Nonnull
    private static final CopyrightHeader COPYRIGHT_HEADER = new CopyrightHeader(80, "--[[", "]]", null, null);

    /**
     * Private default constructor to ensure the only instance to be the
     * singleton instance.
     */
    private LuaWriter() {
        // nothing to do here
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static LuaWriter getInstance() {
        return INSTANCE;
    }

    /**
     * Main writing method. This method causes that the NPC is written to a LUA
     * script.
     *
     * @param source the parsed NPC that acts as data source for the writer
     * @param target the writer that takes the written data
     * @throws IOException thrown in case a writing operation failed
     */
    public void write(
            @Nonnull final ParsedNpc source, @Nonnull final Writer target, final boolean generated) throws IOException {

        // first write the header with some basic information in the comment
        if (!generated) {
            writeIntro(source, target, LuaWriter.WritingStage.Header);
        }

        // now following the modules
        writeModules(source, target);
        writeModuleHeader(source, target);

        // now the big bad initialization
        introInitPart(target);
        writeNpcAffiliation(source, target);

        final boolean talkingExists = checkStageExists(source, LuaWriter.WritingStage.Talking);
        final boolean tradingExists = checkStageExists(source, LuaWriter.WritingStage.Trading);
        final boolean guardingExists = checkStageExists(source, LuaWriter.WritingStage.Guarding);

        if (talkingExists) {
            writeIntro(source, target, LuaWriter.WritingStage.Talking);
        }
        if (tradingExists) {
            writeIntro(source, target, LuaWriter.WritingStage.Trading);
        }
        if (guardingExists) {
            writeIntro(source, target, LuaWriter.WritingStage.Guarding);
        }

        if (talkingExists) {
            writeStage(source, target, LuaWriter.WritingStage.Talking);
        }
        if (tradingExists) {
            writeStage(source, target, LuaWriter.WritingStage.Trading);
        }
        if (guardingExists) {
            writeStage(source, target, LuaWriter.WritingStage.Guarding);
        }

        writeNpcLanguages(source, target);
        writeNpcSpecialMessages(source, target);

        if (checkStageExists(source, LuaWriter.WritingStage.Clothes)) {
            writeIntro(source, target, LuaWriter.WritingStage.Clothes);
            writeStage(source, target, LuaWriter.WritingStage.Clothes);
        }

        autoIntroPart(source, target);

        outroInitPart(target);
        // init part is done

        // write the actual NPC script
        writeMainScript(target);

        // finish the script. Nothing is to be written after this line
        writeLastLines(target);
    }

    /**
     * Write the LUA code needed to set the auto introduction mode of the NPC.
     *
     * @param source the NPC that is the data source for this function
     * @param target the writer that is the target of the function
     * @throws IOException thrown in case the writing operation fails
     */
    private static void autoIntroPart(@Nonnull final ParsedNpc source, @Nonnull final Writer target)
            throws IOException {
        target.write(String.format(addSetAutoIntro, source.getAutoIntroduce() ? "true" : "false"));
        writeNewLine(target);
    }

    /**
     * Fill the SQL query builder will all required data.
     *
     * @param source the NPC that is the data source to build the query
     * @return the prepared SQL query
     */
    private static String buildSQL(@Nonnull final ParsedNpc source) {
        final int count = source.getDataCount();

        final SQLBuilder builder = new SQLBuilder();

        LuaWritable writeable = null;
        for (int i = 0; i < count; ++i) {
            writeable = source.getLuaData(i);
            writeable.buildSQL(builder);
        }

        builder.setNpcName(source.getNpcName());

        builder.setNpcScript("npc." + source.getModuleName()); //$NON-NLS-1$

        builder.setNpcFaceTo(source.getNpcDir().getId());
        builder.setNpcPosX(source.getNpcPos().getScX());
        builder.setNpcPosY(source.getNpcPos().getScY());
        builder.setNpcPosZ(source.getNpcPos().getScZ());
        builder.setNpcType(source.getNpcRace().getId());
        builder.setNpcSex(source.getNpcSex().getId());

        return builder.getSQL();
    }

    /**
     * Check the modules already added to the list and add another one in case this one is not already a part of the
     * list.
     *
     * @param target the list the modules are stored in
     * @param module the module to add to the list
     */
    private static void checkModuleAndAdd(@Nonnull final Collection<String> target, @Nullable final String module) {
        if (module == null) {
            return;
        }

        for (final String aTarget : target) {
            if (aTarget.equals(module)) {
                return;
            }
        }

        target.add(module);
    }

    /**
     * Check if there are any entries in this stage.
     *
     * @param source the NPC that is the data source
     * @param stage the stage to check
     * @return <code>true</code> in case the NPC contains entries in this stage.
     */
    private static boolean checkStageExists(@Nonnull final ParsedNpc source, final LuaWriter.WritingStage stage) {
        final int count = source.getDataCount();

        for (int i = 0; i < count; ++i) {
            final LuaWritable writable = source.getLuaData(i);
            if (writable.effectsLuaWritingStage(stage)) {
                return true;
            }
        }

        return false;
    }

    private static void writeNewLine(@Nonnull final Writer target) throws IOException {
        target.write(NL);
    }

    /**
     * Write the lines needed before the initialization part of the NPC.
     *
     * @param target the writer that receives the text written in this function
     * @throws IOException the exception thrown in case the writing functions
     * fail
     */
    private static void introInitPart(@Nonnull final Writer target) throws IOException {
        target.write("function initNpc()"); //$NON-NLS-1$
        writeNewLine(target);
        target.write("mainNPC = npc.base.basic.baseNPC();"); //$NON-NLS-1$
        writeNewLine(target);
    }

    /**
     * Write the end of the initialization part.
     *
     * @param target the writer that receives the text written in this function
     * @throws IOException thrown in case the writing functions fail
     */
    @SuppressWarnings("nls")
    private static void outroInitPart(@Nonnull final Writer target) throws IOException {
        writeNewLine(target);
        target.write("mainNPC:initDone();");
        writeNewLine(target);
        target.write("end;");
        writeNewLine(target);
        writeNewLine(target);
    }

    /**
     * Write the introduction texts for the easyNPC script.
     *
     * @param source the parsed NPC that is the data source
     * @param target the writer that is the target
     * @param stage the current stage that is supposed to be processed
     * @throws IOException thrown in case the writing operations fail
     */
    @SuppressWarnings("nls")
    private void writeIntro(
            @Nonnull final ParsedNpc source, @Nonnull final Writer target, @Nonnull final LuaWriter.WritingStage stage)
            throws IOException {

        switch (stage) {
            case Header:
                COPYRIGHT_HEADER.writeTo(target);
                target.write(commentSepLine);
                writeNewLine(target);

                target.write(String.format("-- %1$-10s%2$-49s%3$15s --%n", "NPC Name:", source.getNpcName(),
                                           source.getAffiliation().name()));
                target.write(String.format("-- %1$-10s%2$-64s --%n", "NPC Job:", source.getJob()));

                final String freeLine = String.format("-- %1$74s --%n", "");
                target.write(freeLine);

                final String positionString = Integer.toString(source.getNpcPos().getScX()) + ", " +
                        Integer.toString(source.getNpcPos().getScY()) + ", " +
                        Integer.toString(source.getNpcPos().getScZ());
                target.write(String.format("-- %1$-37s%2$-37s --", "NPC Race: " + source.getNpcRace().name(),
                                           "NPC Position:  " + positionString));
                writeNewLine(target);

                target.write(String.format("-- %1$-37s%2$-37s --", "NPC Sex:  " + source.getNpcSex().name(),
                                           "NPC Direction: " + source.getNpcDir().name()));
                writeNewLine(target);

                target.write(freeLine);

                final String[] authors = source.getAuthors();
                final String authorFormat = "-- %1$-10s%2$-64s --%n";
                if (authors.length == 0) {
                    target.write(String.format(authorFormat, "Author:", "not set"));
                } else if (authors.length == 1) {
                    target.write(String.format(authorFormat, "Author:", authors[0]));
                } else {
                    target.write(String.format(authorFormat, "Authors:", authors[0]));

                    for (int i = 1; i < authors.length; ++i) {
                        target.write(String.format(authorFormat, "", authors[i]));
                    }
                }

                target.write(String.format("-- %1$-47s%2$27s --%n", "", Parser.APPLICATION.getApplicationIdentifier()));

                target.write(commentSepLine);
                writeNewLine(target);
                writeNewLine(target);

                target.write("--[[SQL");
                writeNewLine(target);
                target.write(buildSQL(source));
                writeNewLine(target);
                target.write("---]]");
                writeNewLine(target);
                writeNewLine(target);

                break;
            case Talking:
                target.write("local talkingNPC = npc.base.talk.talkNPC(mainNPC);");
                writeNewLine(target);
                break;
            case Trading:
                target.write("local tradingNPC = npc.base.trade.tradeNPC(mainNPC);");
                writeNewLine(target);
                break;
            case Guarding:
                target.write("local guardNPC = npc.base.guard.guardNPC(mainNPC);");
                writeNewLine(target);
                break;
            case Clothes:
                break;
        }
    }

    /**
     * Write the very last lines of the script. Nothing should follow after this
     * lines. It triggers the initialization and clean the script up.
     *
     * @param target the writer that receives the text written by this function
     * @throws IOException thrown in case the writing operations fail
     */
    private static void writeLastLines(@Nonnull final Writer target) throws IOException {
        target.write("initNpc();"); //$NON-NLS-1$
        writeNewLine(target);
        target.write("initNpc = nil;"); //$NON-NLS-1$
        writeNewLine(target);
        target.write("-- END"); //$NON-NLS-1$
    }

    /**
     * Write the actual NPC Script that does the work.
     *
     * @param target the writer that receives the text output
     * @throws IOException thrown in case the writing operation fails
     */
    @SuppressWarnings("nls")
    private static void writeMainScript(@Nonnull final Writer target) throws IOException {
        target.write("function receiveText(npcChar, texttype, message, speaker) ");
        target.write("mainNPC:receiveText(npcChar, texttype, speaker, message); ");
        target.write("end;");
        writeNewLine(target);
        target.write("function nextCycle(npcChar) ");
        target.write("mainNPC:nextCycle(npcChar); ");
        target.write("end;");
        writeNewLine(target);
        target.write("function lookAtNpc(npcChar, char, mode) ");
        target.write("mainNPC:lookAt(npcChar, char, mode); ");
        target.write("end;");
        writeNewLine(target);
        target.write("function useNPC(npcChar, char, counter, param) ");
        target.write("mainNPC:use(npcChar, char); ");
        target.write("end;");
        writeNewLine(target);
    }

    /**
     * Generate and write the module definition of this script.
     *
     * @param source the parsed NPC that supplies the needed data
     * @param target the writer that receives the written text
     * @throws IOException thrown in case there is a problem with writing the
     * text
     */
    private static void writeModuleHeader(@Nonnull final ParsedNpc source, @Nonnull final Writer target)
            throws IOException {

        target.write("module(\"npc."); //$NON-NLS-1$
        target.write(source.getModuleName());
        target.write("\", package.seeall)"); //$NON-NLS-1$
        writeNewLine(target);
        writeNewLine(target);
    }

    /**
     * Collect the required modules from all elements that are written to the
     * script, sort those modules and write them after into the needed form for
     * the LUA script.
     *
     * @param source the parsed NPC that supplies the data
     * @param target the target writer that receives the written text
     * @throws IOException thrown in case the writing operations fail
     */
    private static void writeModules(@Nonnull final ParsedNpc source, @Nonnull final Writer target) throws IOException {
        final List<String> modules = new ArrayList<>();

        LuaWritable writeable = null;
        String[] partModules;
        final int count = source.getDataCount();
        for (int i = 0; i < count; ++i) {
            writeable = source.getLuaData(i);
            partModules = writeable.getRequiredModules();

            if (partModules != null) {
                for (final String module : partModules) {
                    checkModuleAndAdd(modules, module);
                }
            }
        }

        checkModuleAndAdd(modules, "npc.base.basic"); //$NON-NLS-1$

        Collections.sort(modules);

        for (final String module : modules) {
            target.write(String.format(requireCode, module));
            writeNewLine(target);
        }
    }

    /**
     * Write the code needed to set the possible languages and the default
     * language of the NPC.
     *
     * @param source the parsed NPC that supplies the required data
     * @param target the writer that receives the text from this function
     * @throws IOException thrown in case the writing operations fail
     */
    private static void writeNpcLanguages(@Nonnull final ParsedNpc source, @Nonnull final Writer target)
            throws IOException {
        final CharacterLanguage[] languages = source.getLanguages();
        for (final CharacterLanguage lang : languages) {
            target.write(String.format(addLanguageCode, Integer.toString(lang.getLangId())));
            writeNewLine(target);
        }

        target.write(String.format(setDefaultLanguageCode, Integer.toString(source.getDefaultLanguage().getLangId())));
        writeNewLine(target);
    }

    /**
     * Write the code needed to set the special messages each NPC is able to
     * handle.
     *
     * @param source the parsed NPC that is the data source
     * @param target the writer that receives the written text
     * @throws IOException thrown in case the writing operations fail
     */
    private static void writeNpcSpecialMessages(
            @Nonnull final ParsedNpc source, @Nonnull final Writer target) throws IOException {
        target.write(String.format(setLookatMessageCode, source.getGermanLookat(), source.getEnglishLookat()));
        writeNewLine(target);

        target.write(String.format(setUseMessageCode, source.getGermanUse(), source.getEnglishUse()));
        writeNewLine(target);

        target.write(String.format(setConfusedMessageCode, source.getGermanWrongLang(), source.getEnglishWrongLang()));
        writeNewLine(target);
    }

    /**
     * Write the code needed to set the special messages each NPC is able to
     * handle.
     *
     * @param source the parsed NPC that is the data source
     * @param target the writer that receives the written text
     * @throws IOException thrown in case the writing operations fail
     */
    private static void writeNpcAffiliation(
            @Nonnull final ParsedNpc source, @Nonnull final Writer target) throws IOException {
        target.write(String.format(setAffiliationCode, source.getAffiliation().getFactionId()));
        writeNewLine(target);
    }

    /**
     * Write a given stage of the full NPC data.
     *
     * @param source the parsed NPC that is the data source
     * @param target the writer that is the target
     * @param stage the current stage that is supposed to be processed
     * @throws IOException thrown in case the writing operations fail
     */
    private static void writeStage(
            @Nonnull final ParsedNpc source, final Writer target, final LuaWriter.WritingStage stage)
            throws IOException {
        final int count = source.getDataCount();

        LuaWritable writeable = null;
        for (int i = 0; i < count; ++i) {
            writeable = source.getLuaData(i);
            if (writeable.effectsLuaWritingStage(stage)) {
                writeable.writeLua(target, stage);
            }
        }
    }
}
