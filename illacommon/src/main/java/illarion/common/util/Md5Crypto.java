/*
 * This file is part of the Illarion Common Library.
 *
 * Copyright © 2013 - Illarion e.V.
 *
 * The Illarion Common Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Common Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Common Library.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.common.util;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * This class is able to calculate a salted MD5 that is compatible to the MD5 created by the Unix crypt command.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Md5Crypto {
    /**
     * This is the digest used to calculate the MD5 stuff.
     */
    @Nonnull
    private final MessageDigest md5Digest;

    /**
     * Create a new md5 cryptographic instance.
     */
    public Md5Crypto() {
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (@Nonnull final NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }


    @Nonnull
    public String crypt(@Nonnull final String message, @Nonnull final String salt) {
        return crypt(message, salt, "$1$");
    }

    @Nonnull
    public String crypt(@Nonnull final String message, @Nonnull final String salt, @Nonnull final String magic) {
        String cleanedSalt;


        if (salt.startsWith(magic)) {
            cleanedSalt = salt.substring(magic.length());
        } else {
            cleanedSalt = salt;
        }

        if (cleanedSalt.indexOf('$') != -1) {
            cleanedSalt = cleanedSalt.substring(0, cleanedSalt.indexOf('$'));
        }
        if (cleanedSalt.length() > 8) {
            cleanedSalt = cleanedSalt.substring(0, 8);
        }

        final Charset charset = Charset.forName("ISO-8859-1");
        final byte[] messageBytes = message.getBytes(charset);
        final byte[] magicBytes = magic.getBytes(charset);
        final byte[] saltBytes = cleanedSalt.getBytes(charset);

        md5Digest.reset();
        md5Digest.update(messageBytes);
        md5Digest.update(saltBytes);
        md5Digest.update(messageBytes);
        final byte[] currentDigest = md5Digest.digest();

        md5Digest.reset();
        md5Digest.update(messageBytes);
        md5Digest.update(magicBytes);
        md5Digest.update(saltBytes);

        for (int messageLength = message.length(); messageLength > 0; messageLength -= 16) {
            md5Digest.update(currentDigest, 0, Math.min(messageLength, 16));
        }

        Arrays.fill(currentDigest, (byte) 0);

        for (int i = message.length(); i != 0; i >>>= 1) {
            if ((i & 1) == 0) {
                md5Digest.update(messageBytes, 0, 1);
            } else {
                md5Digest.update(currentDigest, 0, 1);
            }
        }

        byte[] currentEncPassword = md5Digest.digest();

        for (int i = 0; i < 1000; i++) {
            md5Digest.reset();

            if ((i & 1) == 0) {
                md5Digest.update(currentEncPassword, 0, 16);
            } else {
                md5Digest.update(messageBytes);
            }

            if ((i % 3) != 0) {
                md5Digest.update(saltBytes);
            }

            if ((i % 7) != 0) {
                md5Digest.update(messageBytes);
            }

            if ((i & 1) == 0) {
                md5Digest.update(messageBytes);
            } else {
                md5Digest.update(currentEncPassword, 0, 16);
            }

            currentEncPassword = md5Digest.digest();
        }

        final StringBuilder result = new StringBuilder();

        result.append(magic);
        result.append(salt);
        result.append('$');

        final byte[] pw = currentEncPassword;
        result.append(to64((bytes2u(pw[0]) << 16) | (bytes2u(pw[6]) << 8) | bytes2u(pw[12]), 4));
        result.append(to64((bytes2u(pw[1]) << 16) | (bytes2u(pw[7]) << 8) | bytes2u(pw[13]), 4));
        result.append(to64((bytes2u(pw[2]) << 16) | (bytes2u(pw[8]) << 8) | bytes2u(pw[14]), 4));
        result.append(to64((bytes2u(pw[3]) << 16) | (bytes2u(pw[9]) << 8) | bytes2u(pw[15]), 4));
        result.append(to64((bytes2u(pw[4]) << 16) | (bytes2u(pw[10]) << 8) | bytes2u(pw[5]), 4));
        result.append(to64(bytes2u(pw[11]), 2));

        return result.toString();
    }

    /**
     * This is the string of characters used to perform the ITOA 64 encoding of a value.
     */
    private static final String ITOA_64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * Convert a 64bit integer value to a string character.
     *
     * @param v    the long value
     * @param size the size of the resulting text in characters
     * @return the generated text
     */
    @Nonnull
    private static String to64(long v, int size) {
        final StringBuilder result = new StringBuilder(size);

        while (--size >= 0) {
            result.append(ITOA_64.charAt((int) (v & 0x3f)));
            v >>>= 6;
        }

        return result.toString();
    }

    /**
     * Convert a byte to a unsigned value.
     *
     * @param inp the byte value
     * @return the unsigned byte value
     */
    private static int bytes2u(final byte inp) {
        return (int) inp & 0xff;
    }
}
