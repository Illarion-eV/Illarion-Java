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

import illarion.easynpc.gui.Editor;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * This class contains the script data to a easyNPC script. It contains the
 * plain unparsed script split in logical groups.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("nls")
public final class EasyNpcScript {
    /**
     * The representation of each line in the easyNPC script. It stores the line number and the text of the lines.
     */
    public static final class Line {
        /**
         * The text of this line.
         */
        private final String line;

        /**
         * The line number of this line.
         */
        private final int lineNumber;

        /**
         * Create a new line representation with the line number and the text of
         * the line stored in it.
         *
         * @param number the line number
         * @param text the text of the line
         */
        protected Line(final int number, final String text) {
            lineNumber = number;
            line = text;
        }

        /**
         * The text of this script line.
         *
         * @return the text of the line
         */
        public String getLine() {
            return line;
        }

        /**
         * Get the line number of this script line.
         *
         * @return the line number
         */
        public int getLineNumber() {
            return lineNumber;
        }
    }

    /**
     * The default encodings to be used to decode the script. In case those
     * fail, it will be tried to decode the script with more rare encodings.
     */
    @Nonnull
    private static final Charset[] ENCODINGS;

    /**
     * The leading characters for Lua comments.
     */
    private static final String LUA_COMMENT_LEAD = "--";

    /**
     * Default new line string to be used in all scripts.
     */
    private static final String NEW_LINE = "\n";

    static {
        final ArrayList<Charset> possibleEncodings = new ArrayList<>();

        String charsetName = "UTF-8";
        if (Charset.isSupported(charsetName)) {
            possibleEncodings.add(Charset.forName(charsetName));
        }

        charsetName = "ISO-8859-1";
        if (Charset.isSupported(charsetName)) {
            possibleEncodings.add(Charset.forName(charsetName));
        }

        charsetName = "UTF-16LE";
        if (Charset.isSupported(charsetName)) {
            possibleEncodings.add(Charset.forName(charsetName));
        }

        charsetName = "UTF-16BE";
        if (Charset.isSupported(charsetName)) {
            possibleEncodings.add(Charset.forName(charsetName));
        }

        boolean defaultCharsetIsIn = false;
        for (final Charset testCharset : possibleEncodings) {
            if (testCharset.equals(Charset.defaultCharset())) {
                defaultCharsetIsIn = true;
            }
        }

        if (!defaultCharsetIsIn) {
            possibleEncodings.add(Charset.defaultCharset());
        }

        ENCODINGS = possibleEncodings.toArray(new Charset[possibleEncodings.size()]);
    }

    /**
     * The original encoding used by this script.
     */
    private Charset encoding;

    /**
     * This list contains all entries in this script written in the order as
     * they are written in the script.
     */
    @Nonnull
    private final List<EasyNpcScript.Line> entries;

    /**
     * The file that script was load from.
     */
    private File sourceScriptFile;

    /**
     * The editor this easyNPC script was generated from.
     */
    private Editor sourceEditor;

    /**
     * Create a new, empty EasyNPC Script. This scripts waits to be filled with
     * new entries.
     */
    public EasyNpcScript() {
        entries = new ArrayList<>();
    }

    /**
     * Read a EasyNPC Script from a specified file. This writes this script into
     * the data storage of this class and prepares it to be handled by the
     * parser.
     *
     * @param sourceFile the file that is read to get the easyNPC data
     * @throws IOException thrown in case anything goes wrong while reading this
     * file.
     */
    public EasyNpcScript(@Nonnull final File sourceFile) throws IOException {
        this();

        readFromInputStream(sourceFile);
    }

    /**
     * Get one entry load from the script file.
     *
     * @param index The index of the entry
     * @return the entry from the script file on the given index
     */
    public EasyNpcScript.Line getEntry(final int index) {
        return entries.get(index);
    }

    /**
     * Get the amount of entries currently load into this easyNPC script.
     *
     * @return the amount of entries load in this script.
     */
    public int getEntryCount() {
        return entries.size();
    }

    /**
     * Get the encoding of this script.
     *
     * @return the encoding of this script
     */
    public Charset getScriptEncoding() {
        return encoding;
    }

    /**
     * Get the file this script was load from in case there is any.
     *
     * @return the script this file was load from
     */
    public File getSourceScriptFile() {
        return sourceScriptFile;
    }

    /**
     * Get the source editor that supplied the data for this script.
     *
     * @return the source editor of this script
     */
    public Editor getSourceEditor() {
        return sourceEditor;
    }

    /**
     * Read the NPC script from a physical file.
     *
     * @param sourceFile the file that is read
     * @throws IOException error thrown in case reading failed
     */
    public void readFromInputStream(@Nonnull final File sourceFile) throws IOException {
        if (!sourceFile.exists() || !sourceFile.isFile() || !sourceFile.canRead()) {
            throw new FileNotFoundException(sourceFile.getAbsolutePath());
        }

        for (final Charset charset : ENCODINGS) {
            final CharsetDecoder decoder = charset.newDecoder();
            decoder.onMalformedInput(CodingErrorAction.REPORT);
            decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
            if (readNPCScript(sourceFile, decoder)) {
                sourceScriptFile = sourceFile;
                return;
            }
        }

        for (final Entry<String, Charset> entry : Charset.availableCharsets().entrySet()) {
            final Charset charset = entry.getValue();
            final CharsetDecoder decoder = charset.newDecoder();
            decoder.onMalformedInput(CodingErrorAction.REPORT);
            decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
            if (readNPCScript(sourceFile, decoder)) {
                sourceScriptFile = sourceFile;
                return;
            }
        }

        throw new IOException("Can't read file: " + sourceFile.getAbsolutePath());
    }

    /**
     * Read the NPC script from a string source.
     *
     * @param source The entire script to read
     */
    public void readNPCScript(@Nonnull final String source) {
        final String[] lines = source.split("\n");

        readNPCScript(lines);
    }

    /**
     * Read the NPC script data from a editor.
     *
     * @param editor the editor that supplies the script data
     */
    public void readFromEditor(@Nonnull final Editor editor) {
        sourceEditor = editor;

        readNPCScript(editor.getScriptText());
    }

    /**
     * Write the content of this script in the proper format to the disk.
     *
     * @param targetFile the file that is supposed to store the new written
     * script
     * @throws IOException thrown in case there is a problem while writing
     */
    public void writeNPCScript(@Nonnull final File targetFile) throws IOException {
        final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(targetFile), "ISO-8859-1"));

        for (final EasyNpcScript.Line line : entries) {
            writer.write(line.getLine());
            writer.write(NEW_LINE);
        }
        writer.flush();
        writer.close();
    }

    /**
     * Read the easyNPC script from a file.
     *
     * @param sourceFile the file to read
     * @param decoder the CharsetDecoder used to read the script
     * @return <code>true</code> in case everything worked
     * @throws IOException thrown in case the reading operation failed
     */
    private boolean readNPCScript(
            final File sourceFile, @Nonnull final CharsetDecoder decoder) throws IOException {
        BufferedReader reader = null;

        final List<String> lineList = new ArrayList<>();

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), decoder));
            while (true) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }

                lineList.add(line);
            }
        } catch (@Nonnull final UnmappableCharacterException ex) {
            return false;
        } catch (@Nonnull final MalformedInputException ex) {
            return false;
        } catch (@Nonnull final FileNotFoundException ex) {
            return false;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        encoding = decoder.charset();

        readNPCScript(lineList.toArray(new String[lineList.size()]));

        return true;
    }

    /**
     * Read a NPC script from a set of lines. The lines need to be stored on by one in a array of strings.
     *
     * @param lines The array of lines
     */
    private void readNPCScript(@Nonnull final String[] lines) {
        boolean currentlyCommentBlock = false;
        boolean currentlyEmptyBlock = false;
        int lineNumber = 0;
        String line;
        for (final String orgLine : lines) {
            line = orgLine.trim();
            lineNumber++;
            if (line.isEmpty()) {
                if (!currentlyEmptyBlock) {
                    entries.add(new EasyNpcScript.Line(lineNumber, line));
                }
                currentlyEmptyBlock = true;
                currentlyCommentBlock = false;
            } else if (line.startsWith(LUA_COMMENT_LEAD)) {
                if (currentlyCommentBlock) {
                    final EasyNpcScript.Line lastLine = entries.remove(entries.size() - 1);
                    entries.add(new EasyNpcScript.Line(lastLine.getLineNumber(), lastLine.getLine() + NEW_LINE + line));
                } else {
                    entries.add(new EasyNpcScript.Line(lineNumber, line));
                }
                currentlyCommentBlock = true;
                currentlyEmptyBlock = false;
            } else {
                entries.add(new EasyNpcScript.Line(lineNumber, line));
                currentlyCommentBlock = false;
                currentlyEmptyBlock = false;
            }
        }
    }
}
