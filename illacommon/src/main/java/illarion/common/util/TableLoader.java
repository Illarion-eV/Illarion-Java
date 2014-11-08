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
package illarion.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
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
     * The crypto instance that is used by the table loader to decrypt the
     * tables that are read and parsed.
     */
    private static Crypto crypto;

    /**
     * The error and debug logger of the client.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TableLoader.class);

    /**
     * The delimiter that is used at this table.
     */
    private final String delim;

    /**
     * The tokes of the last line that was read encoded as strings.
     */
    @Nonnull
    private final ArrayList<String> tokens;

    /**
     * Construct a table loader that loads the table from the file system. With
     * this constructor the table loader takes a <code>,</code> as delimiter.
     * <p/>
     * <b>Important:</b> The first line is assumed as header line and thrown
     * away at the reading operation.
     * <p/>
     *
     * @param table the file that is the source for this table loader
     * @param callback the call back class that is allowed to parse the values
     * this table loader reads
     */
    @SuppressWarnings("nls")
    public <T extends TableLoader> TableLoader(@Nonnull final File table, @Nonnull final TableLoaderSink<T> callback) {
        this(table, callback, ",");
    }

    /**
     * Construct a table loader that loads the table from the file system.
     * <p/>
     * <b>Important:</b> The first line is assumed as header line and thrown
     * away at the reading operation.
     * <p/>
     *
     * @param table the file that is the source for this table loader
     * @param callback the callback class that is allowed to parse the values
     * this table loader reads
     * @param tableDelim the delimiter of the table, so the table columns are
     * seperated by this string
     */
    @SuppressWarnings("nls")
    public <T extends TableLoader> TableLoader(
            @Nonnull final File table, @Nonnull final TableLoaderSink<T> callback, final String tableDelim) {
        this(tableDelim);

        // ignore missing tables
        if (!table.exists()) {
            return;
        }

        InputStream is = null;
        try {
            is = new FileInputStream(table);
            loadTable(is, false, callback);
        } catch (@Nonnull final FileNotFoundException e) {
            // it's ok, just ignore it
        } catch (@Nonnull final IOException e) {
            LOGGER.error("Unable to read data file " + table.getPath(), e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (@Nonnull final IOException e) {
                LOGGER.error("Unable to close data file " + table.getPath(), e);
            }
        }
    }

    /**
     * Load a table from a unencrypted input stream. The table loader loads the
     * data directly from the input stream and closes the input stream after the
     * reading operation. The table is read until no more item is provided.
     * <p/>
     * <b>Important:</b> The first line is assumed as header line and thrown
     * away at the reading operation.
     * <p/>
     *
     * @param resource the input stream the table loader shall read
     * @param ndsc true in case the table that shall be loaded is a NDSC table,
     * false if its a simple CSV file
     * @param callback the call back class that is allowed to parse the values
     * this table loader reads
     * @param tableDelim the delimiter of the table, so the table columns are
     * separated by this string
     */
    @SuppressWarnings("nls")
    public <T extends TableLoader> TableLoader(
            @Nonnull final InputStream resource,
            final boolean ndsc,
            @Nonnull final TableLoaderSink<T> callback,
            final String tableDelim) {
        this(tableDelim);
        try {
            loadTable(resource, ndsc, callback);
        } catch (@Nonnull final IOException e) {
            LOGGER.error("Error reading the resource stream.", e);
            throw new NoResourceException("Error reading resource stream.");
        }
    }

    /**
     * Load a table from the jar file resources. The table needs to be in the
     * resources and its file name ending is <code>.dat</code>. The file
     * is taken as encrypted and is decrypted using
     * {@link illarion.common.util.Crypto}.
     * <p/>
     * <b>Important:</b> The first line is assumed as header line and thrown
     * away at the reading operation.
     * <p/>
     *
     * @param table the name of the table that shall be loaded
     * @param ndsc true in case the table that shall be loaded is a NDSC table,
     * false if its a simple CSV file
     * @param callback the call back class that is allowed to parse the values
     * this table loader reads
     * @param tableDelim the delimiter of the table, so the table columns are
     * separated by this string
     */
    @SuppressWarnings("nls")
    public <T extends TableLoader> TableLoader(
            final String table,
            final boolean ndsc,
            @Nonnull final TableLoaderSink<T> callback,
            final String tableDelim) {
        this(tableDelim);

        // read table via class loader
        final InputStream rsc = Thread.currentThread().getContextClassLoader().getResourceAsStream(table + ".dat");
        if (rsc == null) {
            throw new NoResourceException("Missing table " + table);
        }
        try {
            // load data
            final InputStream decryptedStream = crypto.getDecryptedStream(rsc);
            loadTable(decryptedStream, ndsc, callback);
        } catch (@Nonnull final IOException e) {
            LOGGER.error("Error reading table " + table, e);
            throw new NoResourceException("Error reading table " + table, e);
        } catch (@Nonnull final CryptoException e) {
            LOGGER.error("Error decrypting table " + table, e);
            throw new NoResourceException("Error reading table " + table, e);
        } finally {
            try {
                rsc.close();
            } catch (@Nonnull final IOException ignored) {
            }
        }
    }

    /**
     * Load a table from the jar file resources. The table needs to be in the
     * resources and its file name ending is <code>.dat</code>. The
     * table is loaded as a NDSC table that was created by the config tool and
     * its delimiter is <code>,</code>. Also the file is taken as encrypted and
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
    @SuppressWarnings("nls")
    public <T extends TableLoader> TableLoader(final String table, @Nonnull final TableLoaderSink<T> callback) {
        this(table, true, callback, ",");
    }

    /**
     * Basic constructor that just instantiates the final values. This
     * constructor is called by all other constructors.
     *
     * @param newDelim the delimiter used by this table loader
     */
    private TableLoader(final String newDelim) {
        tokens = new ArrayList<>();
        delim = newDelim;
    }

    /**
     * Set the Crypto instance that is used to decrypt the tables from the
     * resources. This crypto instance needs to be fully set up so the table
     * loader can use it right away. All instances of the table loader will use
     * this crypto class.
     *
     * @param newCrypto the crypto instance that shall be used by all table
     * loaders
     */
    public static void setCrypto(final Crypto newCrypto) {
        crypto = newCrypto;
    }

    /**
     * Return the string representation of a token that was read in the last
     * line with a given index.
     *
     * @param index the index of the token that shall be read
     * @return the token as string or the string <code>&lt;missing&gt;</code>
     */
    @SuppressWarnings("nls")
    public String get(final int index) {
        if (index < tokens.size()) {
            return tokens.get(index);
        }

        LOGGER.error("Missing element in line at " + tokens.get(0));

        return "<missing>";
    }

    /**
     * Return the boolean representation of a token that was read with the last
     * line at a given index. The token is true for all contents but
     * <code>0</code>.
     *
     * @param index the index of the token that shall be read
     * @return the boolean value of the token
     */
    @SuppressWarnings("nls")
    public boolean getBoolean(final int index) {
        final String tokenValue = get(index);
        return !tokenValue.equals("0");
    }

    /**
     * Return the integer representation of a token that was read with the last
     * line at a given index.
     *
     * @param index the index of the token that shall be read
     * @return the integer value of the token
     */
    public int getInt(final int index) {
        final String tokenValue = get(index);
        return Integer.parseInt(tokenValue);
    }

    /**
     * Return the long representation of a token that was read with the last
     * line at a given index.
     *
     * @param index the index of the token that shall be read
     * @return the long value of the token
     */
    public long getLong(final int index) {
        final String tokenValue = get(index);
        return Long.parseLong(tokenValue);
    }

    /**
     * Return the string representation of a token that was read in the last
     * line with a given index.
     *
     * @param index the index of the token that shall be read
     * @return the token as string or the string <code>&lt;missing&gt;</code>
     */
    public String getString(final int index) {
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
    @SuppressWarnings({"nls", "unchecked"})
    private <T extends TableLoader> void loadTable(
            @Nonnull final InputStream rsc, final boolean ndsc, @Nonnull final TableLoaderSink<T> callback) throws IOException {

        final InputStreamReader inRead = new InputStreamReader(rsc);
        final BufferedReader in = new BufferedReader(inRead);

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
        in.close();
    }

    /**
     * Parse all the tokens delimited by the set delimiter ({@link #delim}) from
     * one line into the tokens array ({@link #tokens}). The tokens need to be
     * read by the callback function after this funtion is done in order to
     * clean the tokens array again.
     *
     * @param line the string line that shall be parsed for the tokens
     * @param ndsc true for ndsc tables. For ndsc tables the first two tokens
     * are ignored
     */
    private void parseTokens(@Nonnull final String line, final boolean ndsc) {
        int pos = 0;
        int endPos;
        // skip table id and color
        if (ndsc) {
            pos = line.indexOf(delim);
            pos = line.indexOf(delim, pos + 1);
            pos++;
        }

        boolean running = true;
        while (running) {
            // find end of token
            endPos = line.indexOf(delim, pos);
            if (endPos < 0) {
                if (line.endsWith(delim)) {
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
