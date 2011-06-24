/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute i and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The Illarion Client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Client. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import javolution.text.TextBuilder;

import org.apache.log4j.Logger;

import gnu.trove.map.hash.TIntObjectHashMap;

import illarion.client.IllaClient;
import illarion.client.guiNG.GUI;

import illarion.common.util.TableLoader;
import illarion.common.util.TableLoaderSink;

/**
 * This class loads the book table and has the ability to load books from the
 * resources.
 * 
 * @author Blay09
 * @since 1.22
 */
public final class BookFactory implements TableLoaderSink {

    /**
     * The singleton instance of this class.
     */
    private static final BookFactory INSTANCE = new BookFactory();

    /**
     * The logger of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(BookFactory.class);

    /**
     * The bookfile name of the table entry.
     */
    private static final int TB_BOOKFILE = 1;

    /**
     * The book ID of the table entry.
     */
    private static final int TB_ID = 0;

    /**
     * This HashMap stores all book ids and the name of the bookfiles assigned
     * to them.
     */
    private final TIntObjectHashMap<String> bookList =
        new TIntObjectHashMap<String>();
    /**
     * This byte buffer is used for reading strings from the book's InputStream.
     */
    private byte[] buffer;

    /**
     * Creates a new instance of the BookFactory.
     */
    private BookFactory() {
        super();

        new TableLoader("Books", this);
    }

    /**
     * Returns the singleton instance of this class.
     * 
     * @return the singleton instance
     */
    public static BookFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the book file assigned to that ID.
     * 
     * @param ID the ID of the book file
     * @return The name of the book file. If that book id does not exist, null
     *         is returned.
     */
    public String getBookFile(final int ID) {
        if (bookList.containsKey(ID)) {
            return bookList.get(ID);
        }
        return null;
    }

    /**
     * Loads the book text from the resources and shows the book widget
     * afterwards.
     * 
     * @param ID the ID of the book
     */
    public boolean loadBook(final int ID) {
        TextBuilder tb = TextBuilder.newInstance();
        tb.append("data/books/");
        tb.append(getBookFile(ID));
        tb.append(".book");
        InputStream inputstream = IllaClient.getResource(tb.toString());
        TextBuilder.recycle(tb);
        if (inputstream == null) {
            tb = TextBuilder.newInstance();
            tb.append("Could not load book ");
            tb.append(ID);
            tb.append(". (BookFile: ");
            tb.append("data/books/");
            tb.append(getBookFile(ID));
            tb.append(".book)");
            LOGGER.error(tb.toString());
            TextBuilder.recycle(tb);
            return false;
        }
        try {
            inputstream =
                new BufferedInputStream(new GZIPInputStream(inputstream));
            String title;
            if (Lang.getInstance().isEnglish()) {
                title = decodeString(inputstream);
                final int germanLength = decodeInteger(inputstream);
                inputstream.skip(germanLength);
            } else {
                final int englishLength = decodeInteger(inputstream);
                inputstream.skip(englishLength);
                title = decodeString(inputstream);
            }

            final int pageCount = decodeInteger(inputstream);

            final ArrayList<String> finalBookText = new ArrayList<String>();
            for (int i = 0; i < pageCount; i++) {
                String pageText;
                if (Lang.getInstance().isEnglish()) {
                    pageText = decodeString(inputstream);
                    final int germanLength = decodeInteger(inputstream);
                    inputstream.skip(germanLength);
                } else {
                    final int englishLength = decodeInteger(inputstream);
                    inputstream.skip(englishLength);
                    pageText = decodeString(inputstream);
                }
                finalBookText.add(pageText);
            }
            inputstream.close();

            GUI.getInstance().getBook().setBookTitle(title);
            GUI.getInstance().getBook().setBookText(finalBookText);
            GUI.getInstance().getBook().setVisible(true);
            GUI.getInstance().getBookWindow().setVisible(true);
        } catch (final IOException ex) {
            tb = TextBuilder.newInstance();
            tb.append("Could not load book ");
            tb.append(ID);
            tb.append(": ");
            tb.append(ex.toString());
            LOGGER.error(tb.toString());
            TextBuilder.recycle(tb);
            return false;
        }
        return true;
    }

    /**
     * Read all entries of the table and fill the book list.
     */
    @Override
    public boolean processRecord(final int line, final TableLoader loader) {
        final int ID = loader.getInt(TB_ID);
        final String bookFile = loader.getString(TB_BOOKFILE);
        bookList.put(ID, bookFile);
        return true;
    }

    /**
     * Read four bytes from the buffer and handle them as a single signed value.
     * 
     * @param input the InputStream that is being read from
     * @return the integer that was read
     * @throws IOException if there were more bytes read than there actually are
     */
    private int decodeInteger(final InputStream input) throws IOException {
        int temp = input.read();
        temp <<= Byte.SIZE;
        temp += input.read();
        temp <<= Byte.SIZE;
        temp += input.read();
        temp <<= Byte.SIZE;
        temp += input.read();
        return temp;
    }

    /**
     * Read a string from the input stream
     * 
     * @param input the InputStream that is being read from
     * @return the decoded string
     * @throws IOException if there were more bytes read than there actually are
     */
    private String decodeString(final InputStream input) throws IOException {
        final int len = decodeInteger(input);
        getByteBuffer(len);
        int totalRead = 0;
        int read = 0;
        while (0 < (read = input.read(buffer, totalRead, len - totalRead))) {
            totalRead += read;
        }
        return new String(buffer, 0, totalRead);
    }

    /**
     * Returns a byte buffer with a specific length.
     * 
     * @param length The length of the new byte buffer. If this value is less
     *            than the length of the current byte buffer, it will be
     *            returned instead of a new one.
     * @return the byte buffer with the requested length
     */
    private byte[] getByteBuffer(final int length) {
        if ((buffer == null) || (buffer.length < length)) {
            buffer = new byte[length];
        }
        return buffer;
    }
}
