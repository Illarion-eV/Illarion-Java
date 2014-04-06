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

import illarion.common.util.AppIdent;
import illarion.common.util.Crypto;
import illarion.common.util.TableLoader;
import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.grammar.EasyNpcLexer;
import illarion.easynpc.grammar.EasyNpcParser;
import illarion.easynpc.gui.Config;
import illarion.easynpc.parser.ParsedNpcVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses a easyNPC script that contains the plain script data to a parsed script that contains the
 * analyzed script data.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Parser implements DocuEntry {
    /**
     * The identifier of this application.
     */
    public static final AppIdent APPLICATION = new AppIdent("Illarion easyNPC Editor");

    /**
     * The singleton instance of this class.
     */
    private static final Parser INSTANCE = new Parser();

    /**
     * The private constructor to avoid any instances but the singleton instance. This also prepares the list that
     * are required to work and the registers the parsers working in this parser.
     */
    private Parser() {
        Crypto crypt = new Crypto();
        crypt.loadPublicKey();
        TableLoader.setCrypto(crypt);
    }

    /**
     * Get the singleton instance of this parser.
     *
     * @return the singleton instance of this class
     */
    @Nonnull
    public static Parser getInstance() {
        return INSTANCE;
    }

    private static boolean verbose;
    private static boolean quiet;

    /**
     * This function starts the parser without GUI and is used to parse some
     * scripts directly.
     *
     * @param args the path to the script or the folder with the scripts to parse
     * @throws IOException in case the script can't be read
     */
    public static void main(@Nonnull String[] args) throws IOException {
        Config.getInstance().init();

        if (args.length == 0) {
            System.out.println("You need to set a script to parse, else nothing can be done.");
        }

        for (String arg : args) {
            switch (arg) {
                case "-v":
                case "--verbose":
                    verbose = true;
                    continue;
                case "-q":
                case "--quiet":
                    quiet = true;
                    continue;
                default:
            }
            Path sourceFile = Paths.get(arg);
            if (!Files.isDirectory(sourceFile) && Files.isReadable(sourceFile)) {
                parseScript(sourceFile);
            } else if (Files.isDirectory(sourceFile)) {
                final ExecutorService executor = Executors.newCachedThreadPool();
                Files.walkFileTree(sourceFile, EnumSet.noneOf(FileVisitOption.class), 1, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(final Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.toUri().toString().endsWith(".npc")) {
                            executor.submit(new Callable<Void>() {
                                @Override
                                public Void call() throws Exception {
                                    parseScript(file);
                                    return null;
                                }
                            });
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
                executor.shutdown();
                try {
                    executor.awaitTermination(20, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static ParsedNpc parseScript(@Nonnull CharStream stream) {
        EasyNpcLexer lexer = new EasyNpcLexer(stream);
        EasyNpcParser parser = new EasyNpcParser(new CommonTokenStream(lexer));

        ParsedNpcVisitor visitor = new ParsedNpcVisitor();
        EasyNpcParser.ScriptContext context = parser.script();

        context.accept(visitor);

        return visitor.getParsedNpc();
    }

    private static void parseScript(@Nonnull Path file) throws IOException {
        try (Reader stream = Files.newBufferedReader(file, EasyNpcScript.DEFAULT_CHARSET)) {
            ParsedNpc parsedNPC = parseScript(new ANTLRInputStream(stream));

            StringBuilder output = new StringBuilder();
            output.append("File \"").append(file.getFileName()).append("\" parsed - Encoding: ")
                    .append(EasyNpcScript.DEFAULT_CHARSET.name()).append(" - Errors: ");
            if (parsedNPC.hasErrors()) {
                output.append(parsedNPC.getErrorCount()).append('\n');
                int errorCount = parsedNPC.getErrorCount();
                for (int i = 0; i < errorCount; ++i) {
                    ParsedNpc.Error error = parsedNPC.getError(i);
                    output.append("\tLine ").append(Integer.toString(error.getLine())).append(": ")
                            .append(error.getMessage()).append('\n');
                }
                if (!quiet) {
                    output.setLength(output.length() - 1);
                    System.err.println(output);
                }
                System.exit(-1);
            }
            if (verbose) {
                output.append("done");
                System.out.println(output);
            }

            ScriptWriter writer = new ScriptWriter();
            writer.setSource(parsedNPC);
            Path luaTargetFile = file.getParent().resolveSibling(parsedNPC.getLuaFilename());
            try (Writer outputWriter = Files.newBufferedWriter(luaTargetFile, EasyNpcScript.DEFAULT_CHARSET)) {
                writer.setWritingTarget(outputWriter);
                writer.write();
                outputWriter.flush();
            }
        }
    }

    @Override
    public DocuEntry getChild(int index) {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String getDescription() {
        return Lang.getMsg(getClass(), "Docu.description");
    }

    @Nullable
    @Override
    public String getExample() {
        return null;
    }

    @Nullable
    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getTitle() {
        return Lang.getMsg(getClass(), "Docu.title");
    }

    /**
     * Parse the NPC and return the parsed version of the NPC.
     *
     * @param source the string containing the text of the script
     * @return the parsed version of the NPC
     */
    @Nonnull
    public static ParsedNpc parse(@Nonnull String source) {
        return parseScript(new ANTLRInputStream(source));
    }

    /**
     * Parse the NPC and return the parsed version of the NPC.
     *
     * @param source the reader supplying the script data
     * @return the parsed version of the NPC
     */
    @Nonnull
    public static ParsedNpc parse(@Nonnull Reader source) throws IOException {
        return parseScript(new ANTLRInputStream(source));
    }

    /**
     * Parse the NPC and return the parsed version of the NPC.
     *
     * @param source the path holding the script file
     * @return the parsed version of the NPC
     */
    @Nonnull
    public static ParsedNpc parse(@Nonnull Path source) throws IOException {
        try (Reader reader = Files.newBufferedReader(source, EasyNpcScript.DEFAULT_CHARSET)) {
            return parseScript(new ANTLRInputStream(reader));
        }
    }

    public static void enlistHighlightedWords(TokenMap map) {
        Pattern tokenPattern = Pattern.compile("'([a-zA-Z]+)'");
        for (String token : EasyNpcLexer.tokenNames) {
            Matcher matcher = tokenPattern.matcher(token);
            if (matcher.matches()) {
                map.put(matcher.group(1), Token.RESERVED_WORD);
            }
        }
    }
}
