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
package illarion.easynpc;

import illarion.common.util.CopyrightHeader;
import illarion.easynpc.gui.Editor;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This class contains the script data to a easyNPC script. It contains the
 * plain unparsed script split in logical groups.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings("nls")
public final class EasyNpcScript {
    /**
     * The default charset that is used to read and write the easyNPC script files.
     */
    @Nonnull
    public static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");

    /**
     * The copyright header of the easyNPC writer.
     */
    @Nonnull
    public static final CopyrightHeader COPYRIGHT_HEADER = new CopyrightHeader(80, null, null, "-- ", null);

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
     * The leading characters for Lua comments.
     */
    private static final String LUA_COMMENT_LEAD = "--";

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
    private Path sourceScriptFile;

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
    public EasyNpcScript(@Nonnull final Path sourceFile) throws IOException {
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
    public Path getSourceScriptFile() {
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
    public void readFromInputStream(@Nonnull final Path sourceFile) throws IOException {
        if (Files.isDirectory(sourceFile) || !Files.isReadable(sourceFile)) {
            throw new FileNotFoundException(sourceFile.toString());
        }

        if (readNPCScript(sourceFile)) {
            sourceScriptFile = sourceFile;
            return;
        }

        throw new IOException("Can't read file: " + sourceFile);
    }

    /**
     * Read the NPC script from a string source.
     *
     * @param source The entire script to read
     */
    public void readNPCScript(@Nonnull final String source) {
        final String[] lines = source.split("\n");

        readNPCScript(Arrays.asList(lines));
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
    public void writeNPCScript(@Nonnull final Path targetFile) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(targetFile, DEFAULT_CHARSET)) {
            for (final EasyNpcScript.Line line : entries) {
                writer.write(line.getLine());
                writer.newLine();
            }
            writer.flush();
        }
    }

    /**
     * Read the easyNPC script from a file.
     *
     * @param sourceFile the file to read
     * @return <code>true</code> in case everything worked
     * @throws IOException thrown in case the reading operation failed
     */
    private boolean readNPCScript(@Nonnull final Path sourceFile) throws IOException {
        try {
            final List<String> lineList = Files.readAllLines(sourceFile, DEFAULT_CHARSET);
            encoding = DEFAULT_CHARSET;
            readNPCScript(lineList);
            return true;
        } catch (IOException e) {
            // nothing
        }

        for (Charset set : Charset.availableCharsets().values()) {
            try {
                final List<String> lineList = Files.readAllLines(sourceFile, set);
                encoding = set;
                readNPCScript(lineList);
                return true;
            } catch (IOException e) {
                // nothing
            }
        }
        return false;
    }

    /**
     * Read a NPC script from a set of lines. The lines need to be stored on by one in a array of strings.
     *
     * @param lines The array of lines
     */
    private void readNPCScript(@Nonnull final Collection<String> lines) {
        boolean currentlyCommentBlock = false;
        boolean currentlyEmptyBlock = false;
        int lineNumber = 0;
        for (final String orgLine : lines) {
            final String line = orgLine.trim();
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
                    String newLine = lastLine.getLine() + "\n" + line;
                    if (COPYRIGHT_HEADER.isLicenseText(newLine)) {
                        currentlyCommentBlock = false;
                        currentlyEmptyBlock = false;
                        continue;
                    } else {
                        entries.add(new EasyNpcScript.Line(lastLine.getLineNumber(), lastLine.getLine() + "\n" + line));
                    }
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
        if (!currentlyEmptyBlock) {
            entries.add(new EasyNpcScript.Line(lineNumber + 1, ""));
        }
    }
}
