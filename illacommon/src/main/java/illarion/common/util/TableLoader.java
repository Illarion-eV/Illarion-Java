/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
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
package illarion.common.util;

import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;

/**
 * Class for loading data tables with different delimiters and also the special
 * NDSC table type that is created by the config tool. The data is tokenized and
 * distributed to a callback class that is allowed to parse the values by the
 * functions offered by this class line by line.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class TableLoader {
    /**
     * The crypto instance that is used by the table loader to decrypt the tables that are read and parsed.
     */
    @SuppressWarnings("RedundantFieldInitialization")
    @Nullable
    private static Crypto crypto = null;

    /**
     * The error and debug logger of the client.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(TableLoader.class);

    /**
     * The delimiter that is used at this table.
     */
    @Nonnull
    private final String delimiter;

    /**
     * The tokes of the last line that was read encoded as strings.
     */
    @Nonnull
    private final ArrayList<String> tokens;

    /**
     * Construct a table loader that loads the table from the file system. With
     * this constructor the table loader takes a {@code ,} as delimiter.
     * <p/>
     * <b>Important:</b> The first line is assumed as header line and thrown
     * away at the reading operation.
     * <p/>
     *
     * @param table the file that is the source for this table loader
     * @param callback the call back class that is allowed to parse the values
     * this table loader reads
     */
    public <T extends TableLoader> TableLoader(@Nonnull File table, @Nonnull TableLoaderSink<T> callback) {
        this(table, callback, ",");
    }

    /**
     * Construct a table loader that loads the table from the file system.
     * <p/>
     * <b>Important:</b> The first line is assumed as header line and thrown away at the reading operation.
     * <p/>
     *
     * @param table the file that is the source for this table loader
     * @param callback the callback class that is allowed to parse the values this table loader reads
     * @param tableDelimiter the delimiter of the table, so the table columns are separated by this string
     */
    public <T extends TableLoader> TableLoader(
            @Nonnull File table, @Nonnull TableLoaderSink<T> callback, @Nonnull String tableDelimiter) {
        this(tableDelimiter);

        // ignore missing tables
        if (!table.exists()) {
            return;
        }

        InputStream is = null;
        try {
            is = new FileInputStream(table);
            loadTable(is, false, callback);
        } catch (@Nonnull FileNotFoundException e) {
            // it's ok, just ignore it
        } catch (@Nonnull IOException e) {
            LOGGER.error("Unable to read data file {}", table.getPath(), e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (@Nonnull IOException e) {
                LOGGER.error("Unable to close data file {}", table.getPath(), e);
            }
        }
    }

    /**
     * Load a table from a unencrypted input stream. The table loader loads the data directly from the input stream
     * and closes the input stream after the reading operation. The table is read until no more item is provided.
     * <p/>
     * <b>Important:</b> The first line is assumed as header line and thrown away at the reading operation.
     * <p/>
     *
     * @param resource the input stream the table loader shall read
     * @param ndsc true in case the table that shall be loaded is a NDSC table, false if its a simple CSV file
     * @param callback the call back class that is allowed to parse the values this table loader reads
     * @param tableDelimiter the delimiter of the table, so the table columns are separated by this string
     */
    public <T extends TableLoader> TableLoader(
            @Nonnull InputStream resource,
            boolean ndsc,
            @Nonnull TableLoaderSink<T> callback,
            @Nonnull String tableDelimiter) {
        this(tableDelimiter);
        try {
            loadTable(resource, ndsc, callback);
        } catch (@Nonnull IOException e) {
            LOGGER.error("Error reading the resource stream.", e);
            throw new NoResourceException("Error reading resource stream.");
        }
    }

    /**
     * Load a table from the jar file resources. The table needs to be in the resources and its file name ending is
     * {@code .dat}. The file is taken as encrypted and is decrypted using {@link illarion.common.util.Crypto}.
     * <p/>
     * <b>Important:</b> The first line is assumed as header line and thrown away at the reading operation.
     * <p/>
     *
     * @param table the name of the table that shall be loaded
     * @param ndsc true in case the table that shall be loaded is a NDSC table, false if its a simple CSV file
     * @param callback the call back class that is allowed to parse the values this table loader reads
     * @param tableDelimiter the delimiter of the table, so the table columns are separated by this string
     */
    public <T extends TableLoader> TableLoader(
            @Nonnull String table,
            boolean ndsc,
            @Nonnull TableLoaderSink<T> callback,
            @Nonnull String tableDelimiter) {
        this(tableDelimiter);

        if (crypto == null) {
            throw new IllegalStateException("This constructor requires a Cryptography instance to be present.");
        }

        // read table via class loader
        InputStream rsc = Thread.currentThread().getContextClassLoader().getResourceAsStream(table + ".dat");
        if (rsc == null) {
            throw new NoResourceException("Missing table " + table);
        }
        try {
            // load data
            InputStream decryptedStream = crypto.getDecryptedStream(rsc);
            loadTable(decryptedStream, ndsc, callback);
        } catch (@Nonnull IOException e) {
            LOGGER.error("Error reading table {}", table, e);
            throw new NoResourceException("Error reading table " + table, e);
        } catch (@Nonnull CryptoException e) {
            LOGGER.error("Error decrypting table {}", table, e);
            throw new NoResourceException("Error reading table " + table, e);
        } finally {
            try {
                rsc.close();
            } catch (@Nonnull IOException ignored) {
            }
        }
    }

    /**
     * Load a table from the jar file resources. The table needs to be in the
     * resources and its file name ending is {@code .dat}. The
     * table is loaded as a NDSC table that was created by the config tool and
     * its delimiter is {@code ,}. Also the file is taken as encrypted and
     * is decrypted using {@link illarion.common.util.Crypto}.
     * <p/>
     * <b>Important:</b> The first line is assumed as header line and thrown
     * away at the reading operation.
     * <p/>
     *
     * @param table the name of the table that shall be loaded
     * @param callback the call back class that is allowed to parse the values
     * this table loader reads
     */
    public <T extends TableLoader> TableLoader(@Nonnull String table, @Nonnull TableLoaderSink<T> callback) {
        this(table, true, callback, ",");
    }

    /**
     * Basic constructor that just instantiates the final values. This
     * constructor is called by all other constructors.
     *
     * @param newDelimiter the delimiter used by this table loader
     */
    private TableLoader(@Nonnull String newDelimiter) {
        tokens = new ArrayList<>();
        delimiter = newDelimiter;
    }

    /**
     * Set the Crypto instance that is used to decrypt the tables from the resources. This crypto instance needs to
     * be fully set up so the table loader can use it right away. All instances of the table loader will use this
     * crypto class.
     *
     * @param newCrypto the crypto instance that shall be used by all table
     * loaders
     */
    public static void setCrypto(@Nullable Crypto newCrypto) {
        crypto = newCrypto;
    }

    /**
     * Return the string representation of a token that was read in the last
     * line with a given index.
     *
     * @param index the index of the token that shall be read
     * @return the token as string or the string {@code &lt;missing&gt;}
     */
    @Nonnull
    @Contract(pure = true)
    public String get(int index) {
        if (index < tokens.size()) {
            String token = tokens.get(index);
            if (token != null) {
                return token;
            }
        }

        LOGGER.error("Missing element in line at {}", tokens.get(0));

        return "<missing>";
    }

    /**
     * Return the boolean representation of a token that was read with the last
     * line at a given index. The token is true for all contents but
     * {@code 0}.
     *
     * @param index the index of the token that shall be read
     * @return the boolean value of the token
     */
    @Contract(pure = true)
    public boolean getBoolean(int index) {
        String tokenValue = get(index);
        return !"0".equals(tokenValue);
    }

    /**
     * Return the integer representation of a token that was read with the last
     * line at a given index.
     *
     * @param index the index of the token that shall be read
     * @return the integer value of the token
     */
    @Contract(pure = true)
    public int getInt(int index) {
        String tokenValue = get(index);
        return Integer.parseInt(tokenValue);
    }

    /**
     * Return the string representation of a token that was read in the last
     * line with a given index.
     *
     * @param index the index of the token that shall be read
     * @return the token as string or the string {@code &lt;missing&gt;}
     */
    @Nonnull
    @Contract(pure = true)
    public String getString(int index) {
        return get(index);
    }

    /**
     * Load a table from the stream and close the ressource stream after the
     * reading operations.
     * <p/>
     * <b>Important:</b> The first line is assumed as header line and thrown
     * away at the reading operation.
     * <p/>
     *
     * @param rsc the resource stream that provides the table data
     * @param ndsc true for NDSC table, that causes the first two tokes ignored
     * @param callback the callback class that is allowed to parse the values
     * this table loader reads
     * @throws IOException in case there is something wrong with the ressource
     * stream
     */
    @SuppressWarnings({"unchecked"})
    private <T extends TableLoader> void loadTable(
            @Nonnull InputStream rsc, boolean ndsc, @Nonnull TableLoaderSink<T> callback) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(rsc, "UTF-8"))) {
            String line;
            int lineCount = 0;

            // skip header
            in.readLine();
            // read all lines
            while ((line = in.readLine()) != null) {
                // skip comments and empty lines
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                tokens.clear();

                // find tokens
                parseTokens(line, ndsc);

                if (!callback.processRecord(lineCount, (T) this)) {
                    break;
                }

                lineCount++;
            }
        }
    }

    /**
     * Parse all the tokens delimited by the set delimiter ({@link #delimiter}) from one line into the tokens array
     * ({@link #tokens}). The tokens need to be read by the callback function after this function is done in order to
     * clean the tokens array again.
     *
     * @param line the string line that shall be parsed for the tokens
     * @param ndsc true for ndsc tables. For ndsc tables the first two tokens
     * are ignored
     */
    private void parseTokens(@Nonnull String line, boolean ndsc) {
        int pos = 0;
        // skip table id and color
        if (ndsc) {
            pos = line.indexOf(delimiter);
            pos = line.indexOf(delimiter, pos + 1);
            pos++;
        }

        boolean running = true;
        while (running) {
            // find end of token
            int endPos = line.indexOf(delimiter, pos);
            if (endPos < 0) {
                if (line.endsWith(delimiter)) {
                    break;
                }
                endPos = line.length();
                running = false;
            }

            // it's a string
            if (line.charAt(pos) == '"') {
                tokens.add(line.substring(pos + 1, endPos - 1));
            } else { // copy other data directly
                tokens.add(line.substring(pos, endPos));
            }

            // move onward one entry
            pos = endPos + 1;
        }
    }
}
