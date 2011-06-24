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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Segment;

import jsyntaxpane.Token;
import jsyntaxpane.TokenType;

import org.apache.log4j.Logger;

import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.gui.Config;
import illarion.easynpc.parser.NpcType;

import illarion.common.util.Crypto;
import illarion.common.util.TableLoader;

/**
 * This class parses a easyNPC script that contains the plain script data to a
 * parsed script that contains the analyzed script data.
 * 
 * @author Martin Karing
 * @since 1.00
 * @version 1.01
 */
public final class Parser implements DocuEntry {
    /**
     * The name of this application.
     */
    @SuppressWarnings("nls")
    public static final String APPLICATION = "Illarion easyNPC";

    /**
     * The full name of the application. (Name and Version)
     */
    @SuppressWarnings("nls")
    public static final String FULLNAME = "easyNPC Parser v1.02";

    /**
     * The version and name of this parser.
     */
    @SuppressWarnings("nls")
    public static final String VERSION = "1.02";

    /**
     * The singleton instance of this class.
     */
    private static final Parser INSTANCE = new Parser();

    /**
     * The logger instance that takes care for the logging output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Parser.class);

    /**
     * This pattern is used to find a string in the text.
     */
    @SuppressWarnings("nls")
    private static final Pattern STRING_ENTRY = Pattern.compile(
        "(\"[^\"]*\")", Pattern.MULTILINE);

    /**
     * The list of NPC types this parser knows.
     */
    private final NpcType[] types;

    /**
     * The private constructor to avoid any instances but the singleton
     * instance. This also prepares the list that are required to work and the
     * registers the parsers working in this parser.
     */
    @SuppressWarnings("nls")
    private Parser() {
        final Crypto crypt = new Crypto();
        crypt.loadPublicKey();
        TableLoader.setCrypto(crypt);

        final List<NpcType> typeList = new ArrayList<NpcType>();
        typeList.add(new illarion.easynpc.parser.NpcComment());
        typeList.add(new illarion.easynpc.parser.NpcBasics());
        typeList.add(new illarion.easynpc.parser.NpcColors());
        typeList.add(new illarion.easynpc.parser.NpcHair());
        typeList.add(new illarion.easynpc.parser.NpcEquipment());
        typeList.add(new illarion.easynpc.parser.NpcTalk());
        typeList.add(new illarion.easynpc.parser.NpcEmpty());
        typeList.add(new illarion.easynpc.parser.NpcCycleText());
        typeList.add(new illarion.easynpc.parser.NpcWalk());

        types = typeList.toArray(new NpcType[typeList.size()]);
    }

    /**
     * Get the singleton instance of this parser.
     * 
     * @return the singleton instance of this class
     */
    public static Parser getInstance() {
        return INSTANCE;
    }

    /**
     * This function starts the parser without GUI and is used to parse some
     * scripts directly.
     * 
     * @param args the path to the script or the folder with the scripts to
     *            parse
     * @throws IOException in case the script can't be read
     */
    @SuppressWarnings("nls")
    public static void main(final String[] args) throws IOException {
        Config.getInstance().init();

        if (args.length == 0) {
            System.out
                .println("You need to set a script to parse, else nothing can be done.");
        }

        for (final String arg : args) {
            final File sourceFile = new File(arg);
            if (sourceFile.exists() && sourceFile.isFile()) {
                parseScript(sourceFile);
            } else if (sourceFile.exists() && sourceFile.isDirectory()) {
                final String[] fileNames =
                    sourceFile.list(new FilenameFilter() {
                        private static final String fileEnding = ".npc";

                        @Override
                        public boolean accept(final File dir, final String name) {
                            return name.endsWith(fileEnding);
                        }
                    });

                for (final String fileName : fileNames) {
                    final File targetFile =
                        new File(sourceFile.getPath() + File.separator
                            + fileName);
                    parseScript(targetFile);
                }
            }
        }
    }

    /**
     * Parse one script.
     * 
     * @param file the file of the script to parse
     * @throws IOException in case reading the script fails
     */
    @SuppressWarnings("nls")
    public static void parseScript(final File file) throws IOException {
        final EasyNpcScript script = new EasyNpcScript(file);
        final ParsedNpc parsedNPC = Parser.getInstance().parse(script);

        System.out.print("File \"" + file.getName() + "\" parsed - Encoding: "
            + script.getScriptEncoding().name() + " - Errors: ");
        if (parsedNPC.hasErrors()) {
            System.out.println(Integer.toString(parsedNPC.getErrorCount()));
            final int errorCount = parsedNPC.getErrorCount();
            for (int i = 0; i < errorCount; ++i) {
                final ParsedNpc.Error error = parsedNPC.getError(i);
                System.out.println("\tLine "
                    + Integer.toString(error.getLine().getLineNumber()) + ": "
                    + error.getMessage());
            }
            System.out.println();
            return;
        }

        System.out.println("0");

        final ScriptWriter writer = new ScriptWriter();
        writer.setTargetLanguage(ScriptWriter.TARGET_LUA);
        writer.setSource(parsedNPC);
        final File luaTargetFile =
            new File(file.getParentFile().getParent() + File.separator
                + parsedNPC.getLuaFilename());
        Writer outputWriter =
            new OutputStreamWriter(new FileOutputStream(luaTargetFile),
                "ISO-8859-1");
        writer.setWritingTarget(outputWriter);
        writer.write();
        outputWriter.close();

        writer.setTargetLanguage(ScriptWriter.TARGET_EASY);
        outputWriter =
            new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        writer.setWritingTarget(outputWriter);
        writer.write();
        outputWriter.close();
    }

    @SuppressWarnings("nls")
    @Override
    public DocuEntry getChild(final int index) {
        if ((index < 0) || (index >= types.length)) {
            throw new IllegalArgumentException("Index out of range.");
        }
        return types[index];
    }

    @Override
    public int getChildCount() {
        return types.length;
    }

    @SuppressWarnings("nls")
    @Override
    public String getDescription() {
        return Lang.getMsg(getClass(), "Docu.description");
    }

    @Override
    public String getExample() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @SuppressWarnings("nls")
    @Override
    public String getTitle() {
        return Lang.getMsg(getClass(), "Docu.title");
    }

    /**
     * Parse the NPC and return the parsed version of the NPC.
     * 
     * @param source the easyNPC source script that is supposed to be parsed.
     * @return the parsed version of the NPC
     */
    @SuppressWarnings("nls")
    public ParsedNpc parse(final EasyNpcScript source) {
        final long start = System.currentTimeMillis();
        final int count = source.getEntryCount();
        final ParsedNpc resultNpc = ParsedNpc.getInstance();

        boolean lineParsed = false;
        EasyNpcScript.Line line = null;
        for (int i = 0; i < count; i++) {
            line = source.getEntry(i);
            lineParsed = false;
            for (int parserIdx = 0; parserIdx < types.length; ++parserIdx) {
                if (types[parserIdx].canParseLine(line)) {
                    types[parserIdx].parseLine(line, resultNpc);
                    lineParsed = true;
                    break;
                }
            }

            if (!lineParsed) {
                resultNpc.addError(line,
                    "No parser seems to know what to do with this line.");
            }
        }

        LOGGER.debug("Parsing the script took "
            + Long.toString(System.currentTimeMillis() - start) + "ms");

        return resultNpc;
    }

    /**
     * Parse a segment of the script. That is used to get the highlighted parts
     * for the syntax highlighting.
     * 
     * @param segment the segment to parse
     * @param offset the offset of the segment
     * @param tokens the list of tokens
     */
    public void parseSegment(final Segment segment, final int offset,
        final List<Token> tokens) {

        final Matcher matcher = STRING_ENTRY.matcher(segment);
        while (matcher.find()) {
            tokens.add(new Token(TokenType.STRING, matcher.start(1) + offset,
                matcher.end(1) - matcher.start(1)));
        }

        for (int parserIdx = 0; parserIdx < types.length; ++parserIdx) {
            types[parserIdx].parseSegment(segment, offset, tokens);
        }
    }
}
