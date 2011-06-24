/*
 * This file is part of the Illarion Build Utility.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Build Utility is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Build Utility is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Build Utility. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.build.pack200;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * This class is basically a copy of the default GZIPOutputStream. It just adds
 * a option that allows to set the compression level that is applied to the
 * deflater.
 * 
 * @author Martin Karing
 * @version 1.00
 * @since 1.00
 */
public final class GZIPOutputStreamEx extends DeflaterOutputStream {
    /**
     * GZIP header magic number.
     */
    private final static int GZIP_MAGIC = 0x8b1f;

    /**
     * CRC-32 of uncompressed data.
     */
    protected CRC32 crc = new CRC32();

    /**
     * Creates a new output stream with a default buffer size.
     * 
     * @param output the output stream
     * @exception IOException If an I/O error has occurred.
     */
    public GZIPOutputStreamEx(final OutputStream output) throws IOException {
        this(output, 512, Deflater.DEFAULT_COMPRESSION);
    }

    /**
     * Creates a new output stream with a default buffer size.
     * 
     * @param output the output stream
     * @param compression the compression level that is passed to the deflater
     * @exception IOException If an I/O error has occurred.
     */
    public GZIPOutputStreamEx(final OutputStream output, final int compression)
        throws IOException {
        this(output, 512, compression);
    }

    /**
     * Creates a new output stream with the specified buffer size.
     * 
     * @param output the output stream
     * @param size the output buffer size
     * @param compression the compression level that is passed to the deflater
     * @exception IOException If an I/O error has occurred.
     * @exception IllegalArgumentException if size is <= 0
     */
    public GZIPOutputStreamEx(final OutputStream output, final int size,
        final int compression) throws IOException {
        super(output, new Deflater(compression, true), size);
        writeHeader();
        crc.reset();
    }

    /**
     * Writes remaining compressed data to the output stream and closes the
     * underlying stream.
     * 
     * @exception IOException if an I/O error has occurred
     */
    @Override
    public void close() throws IOException {
        finish();
        out.close();
    }

    /**
     * Finishes writing compressed data to the output stream without closing the
     * underlying stream. Use this method when applying multiple filters in
     * succession to the same output stream.
     * 
     * @exception IOException if an I/O error has occurred
     */
    @Override
    public void finish() throws IOException {
        if (!def.finished()) {
            def.finish();
            while (!def.finished()) {
                deflate();
            }
            writeTrailer();
        }
    }

    /**
     * Writes array of bytes to the compressed output stream. This method will
     * block until all the bytes are written.
     * 
     * @param buffer the data to be written
     * @param offset the start offset of the data
     * @param length the length of the data
     * @exception IOException If an I/O error has occurred.
     */
    @Override
    public synchronized void write(final byte[] buffer, final int offset,
        final int length) throws IOException {
        super.write(buffer, offset, length);
        crc.update(buffer, offset, length);
    }

    /**
     * Writes GZIP member header.
     */
    private void writeHeader() throws IOException {
        writeShort(GZIP_MAGIC); // Magic number
        out.write(Deflater.DEFLATED); // Compression method (CM)
        out.write(0); // Flags (FLG)
        writeInt(0); // Modification time (MTIME)
        out.write(0); // Extra flags (XFL)
        out.write(0); // Operating system (OS)
    }

    /**
     * Writes integer in Intel byte order.
     */
    private void writeInt(final int i) throws IOException {
        writeShort(i & 0xffff);
        writeShort((i >> 16) & 0xffff);
    }

    /**
     * Writes short integer in Intel byte order.
     */
    private void writeShort(final int s) throws IOException {
        out.write(s & 0xff);
        out.write((s >> 8) & 0xff);
    }

    /**
     * Writes GZIP member trailer.
     */
    private void writeTrailer() throws IOException {
        writeInt((int) crc.getValue()); // CRC-32 of uncompressed data
        writeInt(def.getTotalIn()); // Number of uncompressed bytes
    }
}
