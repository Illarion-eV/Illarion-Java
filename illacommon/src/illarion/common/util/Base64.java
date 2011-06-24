/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Common Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Common Library. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Class to handle base64 strings. This class allows decoding and encoding such
 * strings. Base64 can be used easily to store binary values as a string in text
 * files.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public final class Base64 {
    /**
     * Table of the sixty-four characters that are used as the Base64 alphabet:
     * [a-z0-9A-Z+/]
     */
    protected static final byte[] base64Chars = { 'A', 'B', 'C', 'D', 'E',
        'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
        'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
        'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
        'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
        '9', '+', '/', };

    /**
     * Reverse lookup table for the Base64 alphabet. reversebase64Chars[byte]
     * gives n for the n th Base64 character or negative if a character is not a
     * Base64 character.
     */
    protected static final byte[] reverseBase64Chars = new byte[0x100];

    /**
     * Symbol that represents the end of an input stream
     */
    private static final int END_OF_INPUT = -1;

    /**
     * A character that is not a valid base 64 character.
     */
    private static final int NON_BASE_64 = -1;

    /**
     * A character that is not a valid base 64 character.
     */
    private static final int NON_BASE_64_PADDING = -3;

    /**
     * A character that is not a valid base 64 character.
     */
    private static final int NON_BASE_64_WHITESPACE = -2;

    static {
        // Fill in NON_BASE_64 for all characters to start with
        for (int i = 0; i < reverseBase64Chars.length; i++) {
            reverseBase64Chars[i] = NON_BASE_64;
        }
        // For characters that are base64Chars, adjust
        // the reverse lookup table.
        for (byte i = 0; i < base64Chars.length; i++) {
            reverseBase64Chars[base64Chars[i]] = i;
        }
        reverseBase64Chars[' '] = NON_BASE_64_WHITESPACE;
        reverseBase64Chars['\n'] = NON_BASE_64_WHITESPACE;
        reverseBase64Chars['\r'] = NON_BASE_64_WHITESPACE;
        reverseBase64Chars['\t'] = NON_BASE_64_WHITESPACE;
        reverseBase64Chars['\f'] = NON_BASE_64_WHITESPACE;
        reverseBase64Chars['='] = NON_BASE_64_PADDING;
    }

    /**
     * This class need not be instantiated, all methods are static.
     */
    private Base64() {
        // should not be called
    }

    /**
     * Decode Base64 encoded bytes. Characters that are not part of the Base64
     * alphabet are ignored in the input.
     * 
     * @param bytes The data to decode.
     * @return Decoded bytes.
     */
    public static byte[] decode(final byte[] bytes) {
        final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        // calculate the length of the resulting output.
        // in general it will be at most 3/4 the size of the input
        // but the input length must be divisible by four.
        // If it isn't the next largest size that is divisible
        // by four is used.
        int mod;
        int length = bytes.length;
        if ((mod = length % 4) != 0) {
            length += 4 - mod;
        }
        length = (length * 3) / 4;
        final ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        try {
            decode(in, out, false);
        } catch (final IOException x) {
            // This can't happen.
            // The input and output streams were constructed
            // on memory structures that don't actually use IO.
            throw new RuntimeException(x);
        }
        return out.toByteArray();
    }

    /**
     * Decode Base64 encoded bytes to the an OutputStream. Characters that are
     * not part of the Base64 alphabet are ignored in the input.
     * 
     * @param bytes The data to decode.
     * @param out Stream to which to write decoded data.
     * @throws IOException if an IO error occurs.
     */
    public static void decode(final byte[] bytes, final OutputStream out)
        throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        decode(in, out, false);
    }

    /**
     * Decode Base64 encoded data from the InputStream to the OutputStream.
     * Characters in the Base64 alphabet, white space and equals sign are
     * expected to be in url encoded data. The presence of other characters
     * could be a sign that the data is corrupted.
     * 
     * @param in Stream from which to read data that needs to be decoded.
     * @param out Stream to which to write decoded data.
     * @throws IOException if an IO error occurs.
     */
    public static void decode(final InputStream in, final OutputStream out)
        throws IOException {
        decode(in, out, true);
    }

    /**
     * Decode Base64 encoded data from the InputStream to the OutputStream.
     * Characters in the Base64 alphabet, white space and equals sign are
     * expected to be in url encoded data. The presence of other characters
     * could be a sign that the data is corrupted.
     * 
     * @param in Stream from which to read data that needs to be decoded.
     * @param out Stream to which to write decoded data.
     * @param throwExceptions Whether to throw exceptions when unexpected data
     *            is encountered.
     * @throws IOException if an IO error occurs.
     */
    public static void decode(final InputStream in, final OutputStream out,
        final boolean throwExceptions) throws IOException {
        // Base64 decoding converts four bytes of input to three bytes of output
        final int[] inBuffer = new int[4];

        // read bytes unmapping them from their ASCII encoding in the process
        // we must read at least two bytes to be able to output anything
        boolean done = false;
        while (!done
            && ((inBuffer[0] = readBase64(in, throwExceptions)) != END_OF_INPUT)
            && ((inBuffer[1] = readBase64(in, throwExceptions)) != END_OF_INPUT)) {
            // Fill the buffer
            inBuffer[2] = readBase64(in, throwExceptions);
            inBuffer[3] = readBase64(in, throwExceptions);

            // Calculate the output
            // The first two bytes of our in buffer will always be valid
            // but we must check to make sure the other two bytes
            // are not END_OF_INPUT before using them.
            // The basic idea is that the four bytes will get reconstituted
            // into three bytes along these lines:
            // [xxAAAAAA] [xxBBBBBB] [xxCCCCCC] [xxDDDDDD]
            // [AAAAAABB] [BBBBCCCC] [CCDDDDDD]
            // bytes are considered to be zero when absent.

            // six A and two B
            out.write((inBuffer[0] << 2) | (inBuffer[1] >> 4));
            if (inBuffer[2] != END_OF_INPUT) {
                // four B and four C
                out.write((inBuffer[1] << 4) | (inBuffer[2] >> 2));
                if (inBuffer[3] != END_OF_INPUT) {
                    // two C and six D
                    out.write((inBuffer[2] << 6) | inBuffer[3]);
                } else {
                    done = true;
                }
            } else {
                done = true;
            }
        }
        out.flush();
    }

    /**
     * Decode a Base64 encoded String. Characters that are not part of the
     * Base64 alphabet are ignored in the input. The String is converted to and
     * from bytes according to the platform's default character encoding.
     * 
     * @param string The data to decode.
     * @return A decoded String.
     */
    public static String decode(final String string) {
        return new String(decode(string.getBytes()));
    }

    /**
     * Decode a Base64 encoded String. Characters that are not part of the
     * Base64 alphabet are ignored in the input.
     * 
     * @param string The data to decode.
     * @param enc Character encoding to use when converting to and from bytes.
     * @throws UnsupportedEncodingException if the character encoding specified
     *             is not supported.
     * @return A decoded String.
     */
    public static String decode(final String string, final String enc)
        throws UnsupportedEncodingException {
        return new String(decode(string.getBytes(enc)), enc);
    }

    /**
     * Decode a Base64 encoded String. Characters that are not part of the
     * Base64 alphabet are ignored in the input.
     * 
     * @param string The data to decode.
     * @param encIn Character encoding to use when converting input to bytes
     *            (should not matter because Base64 data is designed to survive
     *            most character encodings)
     * @param encOut Character encoding to use when converting decoded bytes to
     *            output.
     * @throws UnsupportedEncodingException if the character encoding specified
     *             is not supported.
     * @return A decoded String.
     */
    public static String decode(final String string, final String encIn,
        final String encOut) throws UnsupportedEncodingException {
        return new String(decode(string.getBytes(encIn)), encOut);
    }

    /**
     * Decode Base64 encoded bytes. Characters that are not part of the Base64
     * alphabet are ignored in the input.
     * 
     * @param bytes The data to decode.
     * @return Decoded bytes.
     */
    public static byte[] decodeToBytes(final byte[] bytes) {
        return decode(bytes);
    }

    /**
     * Decode Base64 encoded data from the InputStream to a byte array.
     * Characters that are not part of the Base64 alphabet are ignored in the
     * input.
     * 
     * @param in Stream from which to read data that needs to be decoded.
     * @return decoded data.
     * @throws IOException if an IO error occurs.
     */
    public static byte[] decodeToBytes(final InputStream in)
        throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        decode(in, out, false);
        return out.toByteArray();
    }

    /**
     * Decode a Base64 encoded String. Characters that are not part of the
     * Base64 alphabet are ignored in the input. The String is converted from
     * bytes according to the platform's default character encoding.
     * 
     * @param string The data to decode.
     * @return decoded data.
     */
    public static byte[] decodeToBytes(final String string) {
        return decode(string.getBytes());
    }

    /**
     * Decode a Base64 encoded String. Characters that are not part of the
     * Base64 alphabet are ignored in the input.
     * 
     * @param string The data to decode.
     * @param enc Character encoding to use when converting from bytes.
     * @throws UnsupportedEncodingException if the character encoding specified
     *             is not supported.
     * @return decoded data.
     */
    public static byte[] decodeToBytes(final String string, final String enc)
        throws UnsupportedEncodingException {
        return decode(string.getBytes(enc));
    }

    /**
     * Decode Base64 encoded bytes to the an OutputStream. Characters that are
     * not part of the Base64 alphabet are ignored in the input.
     * 
     * @param bytes The data to decode.
     * @param out Stream to which to write decoded data.
     * @throws IOException if an IO error occurs.
     */
    public static void decodeToStream(final byte[] bytes,
        final OutputStream out) throws IOException {
        final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        decode(in, out, false);
    }

    /**
     * Decode a Base64 encoded String to an OutputStream. Characters that are
     * not part of the Base64 alphabet are ignored in the input. The String is
     * converted from bytes according to the platform's default character
     * encoding.
     * 
     * @param string The data to decode.
     * @param out Stream to which to write decoded data.
     * @throws IOException if an IO error occurs.
     */
    public static void decodeToStream(final String string,
        final OutputStream out) throws IOException {
        decode(new ByteArrayInputStream(string.getBytes()), out);
    }

    /**
     * Decode a Base64 encoded String to an OutputStream. Characters that are
     * not part of the Base64 alphabet are ignored in the input.
     * 
     * @param string The data to decode.
     * @param enc Character encoding to use when converting to and from bytes.
     * @param out Stream to which to write decoded data.
     * @throws UnsupportedEncodingException if the character encoding specified
     *             is not supported.
     * @throws IOException if an IO error occurs.
     */
    public static void decodeToStream(final String string, final String enc,
        final OutputStream out) throws UnsupportedEncodingException,
        IOException {
        decode(new ByteArrayInputStream(string.getBytes(enc)), out);
    }

    /**
     * Decode Base64 encoded bytes. Characters that are not part of the Base64
     * alphabet are ignored in the input. The String is converted to bytes
     * according to the platform's default character encoding.
     * 
     * @param bytes The data to decode.
     * @return A decoded String.
     */
    public static String decodeToString(final byte[] bytes) {
        return new String(decode(bytes));
    }

    /**
     * Decode Base64 encoded bytes. Characters that are not part of the Base64
     * alphabet are ignored in the input.
     * 
     * @param bytes The data to decode.
     * @param enc Character encoding to use when converting to and from bytes.
     * @throws UnsupportedEncodingException if the character encoding specified
     *             is not supported.
     * @return A decoded String.
     */
    public static String decodeToString(final byte[] bytes, final String enc)
        throws UnsupportedEncodingException {
        return new String(decode(bytes), enc);
    }

    /**
     * Decode Base64 encoded data from the InputStream to a String. Characters
     * that are not part of the Base64 alphabet are ignored in the input. Bytes
     * are converted to characters in the output String according to the
     * platform's default character encoding.
     * 
     * @param in Stream from which to read data that needs to be decoded.
     * @return decoded data.
     * @throws IOException if an IO error occurs.
     */
    public static String decodeToString(final InputStream in)
        throws IOException {
        return new String(decodeToBytes(in));
    }

    /**
     * Decode Base64 encoded data from the InputStream to a String. Characters
     * that are not part of the Base64 alphabet are ignored in the input.
     * 
     * @param in Stream from which to read data that needs to be decoded.
     * @param enc Character encoding to use when converting bytes to characters.
     * @return decoded data.
     * @throws IOException if an IO error occurs.Throws:
     * @throws UnsupportedEncodingException if the character encoding specified
     *             is not supported.
     */
    public static String decodeToString(final InputStream in, final String enc)
        throws IOException {
        return new String(decodeToBytes(in), enc);
    }

    /**
     * Decode a Base64 encoded String. Characters that are not part of the
     * Base64 alphabet are ignored in the input. The String is converted to and
     * from bytes according to the platform's default character encoding.
     * 
     * @param string The data to decode.
     * @return A decoded String.
     */
    public static String decodeToString(final String string) {
        return new String(decode(string.getBytes()));
    }

    /**
     * Decode a Base64 encoded String. Characters that are not part of the
     * Base64 alphabet are ignored in the input.
     * 
     * @param string The data to decode.
     * @param enc Character encoding to use when converting to and from bytes.
     * @throws UnsupportedEncodingException if the character encoding specified
     *             is not supported.
     * @return A decoded String.
     */
    public static String decodeToString(final String string, final String enc)
        throws UnsupportedEncodingException {
        return new String(decode(string.getBytes(enc)), enc);
    }

    /**
     * Decode a Base64 encoded String. Characters that are not part of the
     * Base64 alphabet are ignored in the input.
     * 
     * @param string The data to decode.
     * @param encIn Character encoding to use when converting input to bytes
     *            (should not matter because Base64 data is designed to survive
     *            most character encodings)
     * @param encOut Character encoding to use when converting decoded bytes to
     *            output.
     * @throws UnsupportedEncodingException if the character encoding specified
     *             is not supported.
     * @return A decoded String.
     */
    public static String decodeToString(final String string,
        final String encIn, final String encOut)
        throws UnsupportedEncodingException {
        return new String(decode(string.getBytes(encIn)), encOut);
    }

    /**
     * Encode bytes in Base64. No line breaks or other white space are inserted
     * into the encoded data.
     * 
     * @param bytes The data to encode.
     * @return Encoded bytes.
     */
    public static byte[] encode(final byte[] bytes) {
        return encode(bytes, false);
    }

    /**
     * Encode bytes in Base64.
     * 
     * @param bytes The data to encode.
     * @param lineBreaks Whether to insert line breaks every 76 characters in
     *            the output.
     * @return Encoded bytes.
     */
    public static byte[] encode(final byte[] bytes, final boolean lineBreaks) {
        final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        // calculate the length of the resulting output.
        // in general it will be 4/3 the size of the input
        // but the input length must be divisible by three.
        // If it isn't the next largest size that is divisible
        // by three is used.
        int mod;
        int length = bytes.length;
        if ((mod = length % 3) != 0) {
            length += 3 - mod;
        }
        length = (length * 4) / 3;
        final ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        try {
            encode(in, out, lineBreaks);
        } catch (final IOException x) {
            // This can't happen.
            // The input and output streams were constructed
            // on memory structures that don't actually use IO.
            throw new RuntimeException(x);
        }
        return out.toByteArray();
    }

    /**
     * Encode data from the InputStream to the OutputStream in Base64. Line
     * breaks are inserted every 76 characters in the output.
     * 
     * @param in Stream from which to read data that needs to be encoded.
     * @param out Stream to which to write encoded data.
     * @throws IOException if there is a problem reading or writing.
     */
    public static void encode(final InputStream in, final OutputStream out)
        throws IOException {
        encode(in, out, true);
    }

    /**
     * Encode data from the InputStream to the OutputStream in Base64.
     * 
     * @param in Stream from which to read data that needs to be encoded.
     * @param out Stream to which to write encoded data.
     * @param lineBreaks Whether to insert line breaks every 76 characters in
     *            the output.
     * @throws IOException if there is a problem reading or writing.
     */
    public static void encode(final InputStream in, final OutputStream out,
        final boolean lineBreaks) throws IOException {
        // Base64 encoding converts three bytes of input to
        // four bytes of output
        final int[] inBuffer = new int[3];
        int lineCount = 0;

        boolean done = false;
        while (!done && ((inBuffer[0] = in.read()) != END_OF_INPUT)) {
            // Fill the buffer
            inBuffer[1] = in.read();
            inBuffer[2] = in.read();

            // Calculate the out Buffer
            // The first byte of our in buffer will always be valid
            // but we must check to make sure the other two bytes
            // are not END_OF_INPUT before using them.
            // The basic idea is that the three bytes get split into
            // four bytes along these lines:
            // [AAAAAABB] [BBBBCCCC] [CCDDDDDD]
            // [xxAAAAAA] [xxBBBBBB] [xxCCCCCC] [xxDDDDDD]
            // bytes are considered to be zero when absent.
            // the four bytes are then mapped to common ASCII symbols

            // A's: first six bits of first byte
            out.write(base64Chars[inBuffer[0] >> 2]);
            if (inBuffer[1] != END_OF_INPUT) {
                // B's: last two bits of first byte, first four bits of second
                // byte
                out.write(base64Chars[((inBuffer[0] << 4) & 0x30)
                    | (inBuffer[1] >> 4)]);
                if (inBuffer[2] != END_OF_INPUT) {
                    // C's: last four bits of second byte, first two bits of
                    // third byte
                    out.write(base64Chars[((inBuffer[1] << 2) & 0x3c)
                        | (inBuffer[2] >> 6)]);
                    // D's: last six bits of third byte
                    out.write(base64Chars[inBuffer[2] & 0x3F]);
                } else {
                    // C's: last four bits of second byte
                    out.write(base64Chars[((inBuffer[1] << 2) & 0x3c)]);
                    // an equals sign for a character that is not a Base64
                    // character
                    out.write('=');
                    done = true;
                }
            } else {
                // B's: last two bits of first byte
                out.write(base64Chars[((inBuffer[0] << 4) & 0x30)]);
                // an equal signs for characters that is not a Base64 characters
                out.write('=');
                out.write('=');
                done = true;
            }
            lineCount += 4;
            if (lineBreaks && (lineCount >= 76)) {
                out.write('\n');
                lineCount = 0;
            }
        }
        if (lineBreaks && (lineCount >= 1)) {
            out.write('\n');
            lineCount = 0;
        }
        out.flush();
    }

    /**
     * Encode a String in Base64. The String is converted to and from bytes
     * according to the platform's default character encoding. No line breaks or
     * other white space are inserted into the encoded data.
     * 
     * @param string The data to encode.
     * @return An encoded String.
     */
    public static String encode(final String string) {
        return new String(encode(string.getBytes()));
    }

    /**
     * Encode a String in Base64. No line breaks or other white space are
     * inserted into the encoded data.
     * 
     * @param string The data to encode.
     * @param enc Character encoding to use when converting to and from bytes.
     * @throws UnsupportedEncodingException if the character encoding specified
     *             is not supported.
     * @return An encoded String.
     */
    public static String encode(final String string, final String enc)
        throws UnsupportedEncodingException {
        return new String(encode(string.getBytes(enc)), enc);
    }

    /**
     * Encode bytes in Base64. No line breaks or other white space are inserted
     * into the encoded data.
     * 
     * @param bytes The data to encode.
     * @return String with Base64 encoded data.
     */
    public static String encodeToString(final byte[] bytes) {
        return encodeToString(bytes, false);
    }

    /**
     * Encode bytes in Base64.
     * 
     * @param bytes The data to encode.
     * @param lineBreaks Whether to insert line breaks every 76 characters in
     *            the output.
     * @return String with Base64 encoded data.
     */
    public static String encodeToString(final byte[] bytes,
        final boolean lineBreaks) {
        try {
            return new String(encode(bytes, lineBreaks), "ASCII"); //$NON-NLS-1$
        } catch (final UnsupportedEncodingException iex) {
            // ASCII should be supported
            throw new RuntimeException(iex);
        }
    }

    /**
     * Determines if the byte array is in base64 format.
     * <p>
     * Data will be considered to be in base64 format if it contains only base64
     * characters and whitespace with equals sign padding on the end so that the
     * number of base64 characters is divisible by four.
     * <p>
     * It is possible for data to be in base64 format but for it to not meet
     * these stringent requirements. It is also possible for data to meet these
     * requirements even though decoding it would not make any sense. This
     * method should be used as a guide but it is not authoritative because of
     * the possibility of these false positives and false negatives.
     * <p>
     * Additionally, extra data such as headers or footers may throw this method
     * off the scent and cause it to return false.
     * 
     * @param bytes data that could be in base64 format.
     * @return true if the array appears to be in base64 format
     */
    public static boolean isBase64(final byte[] bytes) {
        try {
            return isBase64(new ByteArrayInputStream(bytes));
        } catch (final IOException x) {
            // This can't happen.
            // The input and output streams were constructed
            // on memory structures that don't actually use IO.
            return false;
        }
    }

    /**
     * Determines if the File is in base64 format.
     * <p>
     * Data will be considered to be in base64 format if it contains only base64
     * characters and whitespace with equals sign padding on the end so that the
     * number of base64 characters is divisible by four.
     * <p>
     * It is possible for data to be in base64 format but for it to not meet
     * these stringent requirements. It is also possible for data to meet these
     * requirements even though decoding it would not make any sense. This
     * method should be used as a guide but it is not authoritative because of
     * the possibility of these false positives and false negatives.
     * <p>
     * Additionally, extra data such as headers or footers may throw this method
     * off the scent and cause it to return false.
     * 
     * @param fIn File that may be in base64 format.
     * @return Best guess as to whether the data is in base64 format.
     * @throws IOException if an IO error occurs.
     */
    public static boolean isBase64(final File fIn) throws IOException {
        return isBase64(new BufferedInputStream(new FileInputStream(fIn)));
    }

    /**
     * Reads data from the stream and determines if it is in base64 format.
     * <p>
     * Data will be considered to be in base64 format if it contains only base64
     * characters and whitespace with equals sign padding on the end so that the
     * number of base64 characters is divisible by four.
     * <p>
     * It is possible for data to be in base64 format but for it to not meet
     * these stringent requirements. It is also possible for data to meet these
     * requirements even though decoding it would not make any sense. This
     * method should be used as a guide but it is not authoritative because of
     * the possibility of these false positives and false negatives.
     * <p>
     * Additionally, extra data such as headers or footers may throw this method
     * off the scent and cause it to return false.
     * 
     * @param in Stream from which to read data to be tested.
     * @return Best guess as to whether the data is in base64 format.
     * @throws IOException if an IO error occurs.
     */
    public static boolean isBase64(final InputStream in) throws IOException {
        long numBase64Chars = 0;
        int numPadding = 0;
        int read;

        while ((read = in.read()) != -1) {
            read = reverseBase64Chars[read];
            if (read == NON_BASE_64) {
                return false;
            } else if (read == NON_BASE_64_WHITESPACE) {
                // ignore white space
            } else if (read == NON_BASE_64_PADDING) {
                numPadding++;
                numBase64Chars++;
            } else if (numPadding > 0) {
                return false;
            } else {
                numBase64Chars++;
            }
        }
        if (numBase64Chars == 0) {
            return false;
        }
        if ((numBase64Chars % 4) != 0) {
            return false;
        }
        return true;
    }

    /**
     * Determines if the String is in base64 format. The String is converted to
     * and from bytes according to the platform's default character encoding.
     * <p>
     * Data will be considered to be in base64 format if it contains only base64
     * characters and whitespace with equals sign padding on the end so that the
     * number of base64 characters is divisible by four.
     * <p>
     * It is possible for data to be in base64 format but for it to not meet
     * these stringent requirements. It is also possible for data to meet these
     * requirements even though decoding it would not make any sense. This
     * method should be used as a guide but it is not authoritative because of
     * the possibility of these false positives and false negatives.
     * <p>
     * Additionally, extra data such as headers or footers may throw this method
     * off the scent and cause it to return false.
     * 
     * @param string String that may be in base64 format.
     * @return Best guess as to whether the data is in base64 format.
     */
    public static boolean isBase64(final String string) {
        return isBase64(string.getBytes());
    }

    /**
     * Determines if the String is in base64 format.
     * <p>
     * Data will be considered to be in base64 format if it contains only base64
     * characters and whitespace with equals sign padding on the end so that the
     * number of base64 characters is divisible by four.
     * <p>
     * It is possible for data to be in base64 format but for it to not meet
     * these stringent requirements. It is also possible for data to meet these
     * requirements even though decoding it would not make any sense. This
     * method should be used as a guide but it is not authoritative because of
     * the possibility of these false positives and false negatives.
     * <p>
     * Additionally, extra data such as headers or footers may throw this method
     * off the scent and cause it to return false.
     * 
     * @param string String that may be in base64 format.
     * @param enc Character encoding to use when converting to bytes.
     * @return Best guess as to whether the data is in base64 format.
     * @throws UnsupportedEncodingException if the character encoding specified
     *             is not supported.
     */
    public static boolean isBase64(final String string, final String enc)
        throws UnsupportedEncodingException {
        return isBase64(string.getBytes(enc));
    }

    /**
     * Reads the next (decoded) Base64 character from the input stream. Non
     * Base64 characters are skipped.
     * 
     * @param in Stream from which bytes are read.
     * @param throwExceptions Throw an exception if an unexpected character is
     *            encountered.
     * @return the next Base64 character from the stream or -1 if there are no
     *         more Base64 characters on the stream.
     * @throws IOException if an IO Error occurs.
     * @throws IllegalStateException if unexpected data is encountered when
     *             throwExceptions is specified.
     * @since ostermillerutils 1.00.00
     */
    private static int readBase64(final InputStream in,
        final boolean throwExceptions) throws IOException {
        int read;
        int numPadding = 0;
        do {
            read = in.read();
            if (read == END_OF_INPUT) {
                return END_OF_INPUT;
            }
            read = reverseBase64Chars[(byte) read];
            if (throwExceptions
                && ((read == NON_BASE_64) || ((numPadding > 0) && (read > NON_BASE_64)))) {
                throw new IllegalStateException("Unexpected character"); //$NON-NLS-1$
            }
            if (read == NON_BASE_64_PADDING) {
                numPadding++;
            }
        } while (read <= NON_BASE_64);
        return read;
    }
}
