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
package illarion.compile;

import illarion.compile.impl.Compile;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * This the the main class for the compiler. It determines the kind of compiler required for the set file and performs
 * the compiling operation.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Compiler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Compiler.class);
    private static Map<CompilerType, Path> storagePaths;

    public static void main(String[] args) {
        ByteArrayOutputStream stdOutBuffer = new ByteArrayOutputStream();
        PrintStream orgStdOut = System.out;
        System.setOut(new PrintStream(stdOutBuffer));

        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        Options options = new Options();

        Option npcDir = new Option("n", "npc-dir", true, "The place where the compiled NPC files are stored.");
        npcDir.setArgs(1);
        npcDir.setArgName("directory");
        npcDir.setRequired(false);
        options.addOption(npcDir);

        Option questDir = new Option("q", "quest-dir", true,
                                           "The place where the compiled Quest files are stored.");
        questDir.setArgs(1);
        questDir.setArgName("directory");
        questDir.setRequired(false);
        options.addOption(questDir);

        Option type = new Option("t", "type", true,
                                       "This option is used to set what kind of parser is supposed to be used in case" +
                                               " the content of standard input is processed."
        );
        type.setArgs(1);
        type.setArgName("type");
        type.setRequired(false);
        options.addOption(type);

        Option jobs = new Option("j", "jobs", true,
                                 "This option defines how many jobs may run in parallel. Default is 1. 'Auto' is a " +
                                         "possible value to leave it to the VM how many execution threads are used.");
        jobs.setArgs(1);
        jobs.setArgName("jobs");
        jobs.setRequired(false);
        options.addOption(jobs);

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            String[] files = cmd.getArgs();
            if (files.length > 0) {
                System.setOut(orgStdOut);
                stdOutBuffer.writeTo(orgStdOut);

                processFileMode(cmd);
            } else {
                System.setOut(orgStdOut);
                processStdIn(cmd);
            }
        } catch (ParseException e) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("java -jar compiler.jar [Options] File", options, true);
            System.exit(-1);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
            System.exit(-1);
        }
    }

    private static void processFileMode(@Nonnull CommandLine cmd) throws IOException {
        storagePaths = new EnumMap<>(CompilerType.class);
        String npcPath = cmd.getOptionValue('n');
        if (npcPath != null) {
            storagePaths.put(CompilerType.easyNPC, Paths.get(npcPath));
        }
        String questPath = cmd.getOptionValue('q');
        if (questPath != null) {
            storagePaths.put(CompilerType.easyQuest, Paths.get(questPath));
        }

        String jobsOption = cmd.getOptionValue('j');
        ExecutorService executor;
        if (jobsOption == null) {
            executor = Executors.newSingleThreadExecutor();
        } else if ("auto".equalsIgnoreCase(jobsOption)) {
            executor = Executors.newCachedThreadPool();
        } else {
            try {
                int numberOfJobs = Integer.parseInt(jobsOption);
                executor = Executors.newFixedThreadPool(numberOfJobs);
            } catch (NumberFormatException e) {
                LOGGER.error("Invalid value for jobs option: {}", jobsOption);
                executor = Executors.newSingleThreadExecutor();
            }
        }

        final List<Future<Integer>> results = new ArrayList<>();
        for (String file : cmd.getArgs()) {
            Path path = Paths.get(file);
            if (Files.isDirectory(path)) {
                final ExecutorService finalExecutor = executor;
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        FileVisitResult result = super.visitFile(file, attrs);
                        if (result == FileVisitResult.CONTINUE) {
                            results.add(processPath(finalExecutor, file));
                            return FileVisitResult.CONTINUE;
                        }
                        return result;
                    }
                });
            } else {
                results.add(processPath(executor, path));
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1L, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            LOGGER.error("Interruption received.", e);
        }

        int returnCode = 3;
        for (Future<Integer> result : results) {
            try {
                returnCode = Math.min(returnCode, result.get());
            } catch (InterruptedException e) {
                LOGGER.error("Interruption received.", e);
            } catch (ExecutionException e) {
                LOGGER.error("Error while performing parsing.", e);
            }
        }

        System.exit(returnCode);
    }

    private static void processStdIn(@Nonnull CommandLine cmd) throws IOException {
        String dataType = cmd.getOptionValue('t');

        CompilerType usedType = null;
        if (dataType != null) {
            switch (dataType) {
                case "npc":
                    usedType = CompilerType.easyNPC;
                    break;
                case "quest":
                    usedType = CompilerType.easyQuest;
                    break;
            }
        }
        if (usedType == null) {
            LOGGER.error("Standard input mode requires a valid definition of the type option.");
            System.exit(-1);
        }

        Compile compile = usedType.getImplementation();
        System.exit(compile.compileStream(System.in, System.out));
    }

    private static Future<Integer> processPath(
            @Nonnull ExecutorService executor, @Nonnull final Path path) throws IOException {
        if (Files.isDirectory(path)) {
            return new CompletedFuture<>(0);
        }

        for (CompilerType type : CompilerType.values()) {
            if (type.isValidFile(path)) {
                final Compile compile = type.getImplementation();
                if (path.isAbsolute()) {
                    if (storagePaths.containsKey(type)) {
                        compile.setTargetDir(storagePaths.get(type));
                    } else {
                        compile.setTargetDir(path.getParent());
                    }
                } else {
                    if (storagePaths.containsKey(type)) {
                        Path parent = path.getParent();
                        if (parent == null) {
                            compile.setTargetDir(storagePaths.get(type));
                        } else {
                            compile.setTargetDir(storagePaths.get(type).resolve(parent));
                        }
                    } else {
                        Path parent = path.getParent();
                        if (parent == null) {
                            compile.setTargetDir(path.toAbsolutePath().getParent());
                        } else {
                            compile.setTargetDir(parent);
                        }
                    }
                }

                return executor.submit(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        int result = compile.compileFile(path.toAbsolutePath());
                        if (result == 1) {
                            LOGGER.info("Skipped file: {}", path.getFileName());
                            return -2;
                        }
                        return result;
                    }
                });
            }
        }
        return new CompletedFuture<>(-2);
    }

    private static final class CompletedFuture<T> implements Future<T> {
        private final T result;

        private CompletedFuture(T result) {
            this.result = result;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            return result;
        }

        @Override
        public T get(long timeout, @Nonnull TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return result;
        }
    }
}
