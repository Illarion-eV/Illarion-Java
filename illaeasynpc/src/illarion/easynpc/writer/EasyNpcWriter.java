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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import illarion.easynpc.ParsedNpc;
import illarion.easynpc.data.CharacterLanguage;

/**
 * This is the easyNPC writer. It writes from the data supplied by a parsed NPC
 * a easyNPC script with the proper encoding and the latest state of the syntax.
 * 
 * @author Martin Karing
 * @since 1.00
 */
public final class EasyNpcWriter {
    /**
     * This enumerator contains the constants to identify the different writing
     * stages this writer uses during the multi-pass writing.
     * 
     * @author Martin Karing
     * @since 1.00
     */
    public enum WritingStage {
        /**
         * The clothes stage is used to write the clothes the NPC wears.
         */
        clothes,

        /**
         * The color stage is used to write the color values into the NPC
         * script.
         */
        color,

        /**
         * The stage where the cycle texts are supposed to be written.
         */
        cycleTexts,

        /**
         * The hair stage is used to write the hair IDs to the NPC script.
         */
        hair,

        /**
         * The header is supposed to contains the messages at the very top of
         * the script. Basic informations about the NPC should be written in
         * this part.
         */
        header,

        /**
         * During this stage the normal talking texts should be displayed.
         */
        talking;
    }

    /**
     * The header of auto comment.
     */
    @SuppressWarnings("nls")
    public static final String AC_HEADER =
        "------------------------------------------------------------------------------AC\n";

    /**
     * The new line string that is used by default for this scripts.
     */
    public static final String NL = "\n".intern(); //$NON-NLS-1$

    /**
     * The singleton instance of this class.
     */
    private static final EasyNpcWriter INSTANCE = new EasyNpcWriter();

    /**
     * The private default constructor to avoid any instances but the singleton
     * instance.
     */
    private EasyNpcWriter() {
        // nothing to do
    }

    /**
     * Get the singleton instance of this class.
     * 
     * @return the singleton instance
     */
    public static EasyNpcWriter getInstance() {
        return INSTANCE;
    }

    /**
     * Main writing method. This method causes that the NPC is written to a
     * easyNPC script.
     * 
     * @param source the parsed NPC that acts as data source for the writer
     * @param target the writer that takes the written data
     * @throws IOException thrown in case a writing operation failed
     */
    public void write(final ParsedNpc source, final Writer target)
        throws IOException {
        writeIntro(source, target, WritingStage.header);
        // first the default header
        writeHeader(source, target);

        // now the custom header
        if (checkStageExists(source, WritingStage.header)) {
            writeStage(source, target, WritingStage.header);
        }

        // now the hair header
        if (checkStageExists(source, WritingStage.hair)) {
            writeStage(source, target, WritingStage.hair);
        }

        // now the color header
        if (checkStageExists(source, WritingStage.color)) {
            writeStage(source, target, WritingStage.color);
        }

        // now the clothes header
        if (checkStageExists(source, WritingStage.clothes)) {
            writeStage(source, target, WritingStage.clothes);
        }

        // now the talking part
        if (checkStageExists(source, WritingStage.talking)) {
            writeIntro(source, target, WritingStage.talking);
            writeStage(source, target, WritingStage.talking);
        }

        // now the cycle text part
        if (checkStageExists(source, WritingStage.cycleTexts)) {
            writeIntro(source, target, WritingStage.cycleTexts);
            writeStage(source, target, WritingStage.cycleTexts);
        }
    }

    /**
     * Check if there are any entries in this stage.
     * 
     * @param source the NPC that is the data source
     * @param stage the stage to check
     * @return <code>true</code> in case the NPC contains entries in this stage.
     */
    private boolean checkStageExists(final ParsedNpc source,
        final WritingStage stage) {
        final int count = source.getDataCount();

        EasyNpcWritable writeable = null;
        for (int i = 0; i < count; ++i) {
            writeable = source.getEasyNpcData(i);
            if (writeable.effectsEasyNpcStage(stage)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Write the header of the easyNPC script to the target. This contains the
     * name, the gender, the race, the position and the direction NPC. Every
     * single NPC requires to have this values.
     * 
     * @param source the easyNPC that is the data source for this function
     * @param target the target writer that takes the data extracted from the
     *            source
     * @throws IOException thrown in case a writing operation failed
     */
    @SuppressWarnings("nls")
    private void writeHeader(final ParsedNpc source, final Writer target)
        throws IOException {
        target.write("name = \"");
        target.write(source.getNpcName());
        target.write("\"");
        target.write(NL);

        target.write("race = ");
        target.write(source.getNpcRace().name());
        target.write(NL);

        target.write("sex = ");
        target.write(source.getNpcSex().name());
        target.write(NL);

        target.write("position = ");
        target.write(Integer.toString(source.getNpcPos().getScX(), 0));
        target.write(", ");
        target.write(Integer.toString(source.getNpcPos().getScY(), 0));
        target.write(", ");
        target.write(Integer.toString(source.getNpcPos().getScZ(), 0));
        target.write(NL);

        target.write("direction = ");
        target.write(source.getNpcDir().name());
        target.write(NL);

        target.write("affiliation = \"");
        target.write(source.getAffiliation().name());
        target.write("\"");
        target.write(NL);

        target.write("job = \"");
        target.write(source.getJob());
        target.write("\"");
        target.write(NL);

        target.write(NL);
        final CharacterLanguage[] languages = source.getLanguages();
        for (final CharacterLanguage lang : languages) {
            target.write("language = ");
            target.write(lang.name());
            target.write(NL);
        }
        target.write("defaultLanguage = ");
        target.write(source.getDefaultLanguage().name());
        target.write(NL);
        target.write("autointroduce = ");
        target.write(source.getAutoIntroduce().getEasyNpc());
        target.write(NL);

        target.write(NL);
        final String[] authors = source.getAuthors();
        if (authors.length == 0) {
            target.write("author = \"not set\"");
            target.write(NL);
        } else {
            for (final String author : authors) {
                target.write("author = \"");
                target.write(author);
                target.write("\"");
                target.write(NL);
            }
        }
        target.write(NL);

        target.write("lookatDE = \"");
        target.write(source.getGermanLookat());
        target.write("\"");
        target.write(NL);

        target.write("lookatUS = \"");
        target.write(source.getEnglishLookat());
        target.write("\"");
        target.write(NL);

        target.write("useMsgDE = \"");
        target.write(source.getGermanUse());
        target.write("\"");
        target.write(NL);

        target.write("useMsgUS = \"");
        target.write(source.getEnglishUse());
        target.write("\"");
        target.write(NL);

        target.write("wrongLangDE = \"");
        target.write(source.getGermanWrongLang());
        target.write("\"");
        target.write(NL);

        target.write("wrongLangUS = \"");
        target.write(source.getEnglishWrongLang());
        target.write("\"");
        target.write(NL);
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
    private void writeIntro(final ParsedNpc source, final Writer target,
        final WritingStage stage) throws IOException {

        switch (stage) {
            case header:
                target.write(AC_HEADER);

                target.write(String.format("-- %1$-10s%2$-49s%3$15s --%n",
                    "NPC Name:", source.getNpcName(), source.getAffiliation()
                        .name()));
                target.write(String.format("-- %1$-10s%2$-64s --%n",
                    "NPC Job:", source.getJob()));

                final String freeLine = String.format("-- %1$74s --%n", "");
                target.write(freeLine);

                final String positionString =
                    Integer.toString(source.getNpcPos().getScX()) + ", "
                        + Integer.toString(source.getNpcPos().getScY()) + ", "
                        + Integer.toString(source.getNpcPos().getScZ());
                target.write(String.format("-- %1$-37s%2$-37s --",
                    "NPC Race: " + source.getNpcRace().name(),
                    "NPC Position:  " + positionString));
                target.write(NL);

                target.write(String.format("-- %1$-37s%2$-37s --",
                    "NPC Sex:  " + source.getNpcSex().name(),
                    "NPC Direction: " + source.getNpcDir().name()));
                target.write(NL);

                target.write(freeLine);

                final String[] authors = source.getAuthors();
                final String authorFormat = "-- %1$-10s%2$-64s --%n";
                if (authors.length == 0) {
                    target.write(String.format(authorFormat, "Author:",
                        "not set"));
                } else if (authors.length == 1) {
                    target.write(String.format(authorFormat, "Author:",
                        authors[0]));
                } else {
                    target.write(String.format(authorFormat, "Authors:",
                        authors[0]));

                    for (int i = 1; i < authors.length; ++i) {
                        target.write(String.format(authorFormat, "",
                            authors[i]));
                    }
                }

                target.write(freeLine);

                final Calendar cal = Calendar.getInstance();
                final SimpleDateFormat sdf =
                    new SimpleDateFormat("MMMMM dd, yyyy", Locale.ENGLISH);
                target.write(String.format("-- %1$-47s%2$27s --%n",
                    "Last parsing: " + sdf.format(cal.getTime()),
                    illarion.easynpc.Parser.FULLNAME));

                target
                    .write("--------------------------------------------------------------------------------");
                target.write(NL);
                target.write(NL);

                break;
            case cycleTexts:
                target.write(NL);
                target.write(AC_HEADER);
                target
                    .write("-- Cycle Texts - Messages spoken automatically in random intervals."
                        + NL);
                target
                    .write("-- Every NPC should contain at least 10 of those messages."
                        + NL);
                target
                    .write("-- Emotes are possible also starting with \"#me ....\"."
                        + NL);
                target.write(NL);
                break;
            case clothes:
                break;
            case color:
                break;
            case talking:
                break;
            case hair:
                break;
        }
    }

    /**
     * Write a given stage of the full NPC data.
     * 
     * @param source the parsed NPC that is the data source
     * @param target the writer that is the target
     * @param stage the current stage that is supposed to be processed
     * @throws IOException thrown in case the writing operations fail
     */
    private void writeStage(final ParsedNpc source, final Writer target,
        final WritingStage stage) throws IOException {
        final int count = source.getDataCount();

        EasyNpcWritable writeable = null;
        for (int i = 0; i < count; ++i) {
            writeable = source.getEasyNpcData(i);
            writeable.writeEasyNpc(target, stage);
        }
    }
}
