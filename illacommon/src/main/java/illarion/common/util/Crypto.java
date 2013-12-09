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

import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.*;
import java.io.*;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

/**
 * Class to handle the encryption of the files that are stored by the client.
 * The encryption created by this class bases on a private and a public key.
 *
 * @author Nop
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@SuppressWarnings({"ClassNamingConvention", "SpellCheckingInspection"})
public final class Crypto {
    /**
     * The error and debug logger of the client.
     */
    private static final Logger LOGGER = Logger.getLogger(Crypto.class);

    /**
     * The filename of the private key.
     */
    private static final String PRIVATE_KEY = "private.key";

    /**
     * The filename of the public key.
     */
    private static final String PUBLIC_KEY = "public.key";

    /**
     * String for the transformation name "RSA"
     */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * The size of the buffer used to transfer the data during encryption and decryption.
     */
    private static final int TRANSFER_BUFFER_SIZE = 4096;

    /**
     * The private key instance that was loaded into the this class.
     */
    @Nullable
    private PrivateKey privateKey;

    /**
     * The public key instance that was loaded into the this class.
     */
    @Nullable
    private PublicKey publicKey;

    /**
     * Decrypt a file by using the public key that was prepared with the class
     * constructor.
     *
     * @param src the crypt source file
     * @param dst the decrypted file
     */
    @SuppressWarnings("nls")
    public void decrypt(@Nonnull final InputStream src, @Nonnull final OutputStream dst) throws CryptoException {
        if (!hasPublicKey()) {
            throw new IllegalStateException("No keys loaded");
        }

        try {
            transferBytes(getDecryptedStream(src), dst);
        } catch (@Nonnull final Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Get a stream that delivers the decrypted data.
     *
     * @param src the stream that delivers the encryted data
     * @return the stream that provides the decrypted data
     * @throws CryptoException
     */
    @Nonnull
    public InputStream getDecryptedStream(@Nonnull final InputStream src) throws CryptoException {
        if (!hasPublicKey()) {
            throw new IllegalStateException("No keys loaded");
        }

        try {
            final DataInputStream dIn = new DataInputStream(src);
            final int keyLength = dIn.readInt();
            final byte[] wrappedKey = new byte[keyLength];

            int n = 0;
            while (n < keyLength) {
                n += dIn.read(wrappedKey, n, keyLength - n);
            }

            @Nonnull final Cipher wrappingCipher = Cipher.getInstance(KEY_ALGORITHM);
            wrappingCipher.init(Cipher.UNWRAP_MODE, publicKey);
            final Key encryptionKey = wrappingCipher.unwrap(wrappedKey, "DES", Cipher.SECRET_KEY);

            @Nonnull final Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey);

            return new CipherInputStream(src, cipher);
        } catch (@Nonnull final Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Transfer all bytes from a input stream to a output stream.
     *
     * @param in the source input stream
     * @param out the target output stream
     * @throws IOException in case reading from the source stream or writing to the target stream fails
     */
    private static void transferBytes(@Nonnull final InputStream in, @Nonnull final OutputStream out)
            throws IOException {
        try {
            final byte[] buffer = new byte[TRANSFER_BUFFER_SIZE];
            int n = in.read(buffer);
            while(n > -1) {
                out.write(buffer, 0, n);
                n = in.read(buffer);
            }
            out.flush();
        } catch (@Nonnull final OutOfMemoryError e) {
            throw new IOException(e);
        }
    }

    /**
     * Encrypt a data stream with the the private key. This is only possible in
     * case the configuration tool constructed this class with both keys, the
     * public key and the private key.
     *
     * @param src data to encrypt
     * @param dst target stream for encryption
     */
    @SuppressWarnings("nls")
    public void encrypt(@Nonnull final InputStream src, @Nonnull final OutputStream dst) throws CryptoException {
        if (!hasPrivateKey()) {
            throw new IllegalStateException("No keys loaded");
        }

        OutputStream cOutStream = null;
        try {
            cOutStream = getEncryptedStream(new NonClosingOutputStream(dst));
            transferBytes(src, cOutStream);
        } catch (@Nonnull final Exception e) {
            throw new CryptoException(e);
        } finally {
            if (cOutStream != null) {
                try {
                    cOutStream.close();
                    dst.flush();
                } catch (@Nonnull final IOException ignored) {}
            }
        }
    }

    /**
     * Get a stream that takes unencrypted data and forwards it encrypted.
     *
     * @param dst the stream that is supposed to recieve the encrypted data
     * @return the system that will receive the unencrypted data
     * @throws CryptoException
     */
    @Nonnull
    public OutputStream getEncryptedStream(@Nonnull final OutputStream dst) throws CryptoException {
        if (!hasPrivateKey()) {
            throw new IllegalStateException("No keys loaded");
        }

        try {
            // generate a random DES key
            final KeyGenerator keygen = KeyGenerator.getInstance("DES");
            final SecureRandom random = new SecureRandom();
            keygen.init(random);
            final SecretKey key = keygen.generateKey();

            // wrap with RSA public key
            @Nonnull final Cipher wrappingCipher = Cipher.getInstance(KEY_ALGORITHM);
            wrappingCipher.init(Cipher.WRAP_MODE, privateKey);

            final byte[] wrappedKey = wrappingCipher.wrap(key);
            final DataOutputStream out = new DataOutputStream(dst);
            out.writeInt(wrappedKey.length);
            out.write(wrappedKey);

            // encrypt data
            @Nonnull final Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return new CipherOutputStream(out, cipher);
        } catch (@Nonnull final Exception e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Check if this class has a private key that can be used to encrypt data.
     *
     * @return {@code true} in case there is a private key loaded
     */
    public boolean hasPrivateKey() {
        return privateKey != null;
    }

    /**
     * Check if this class has a public key that can be used to decrypt data.
     *
     * @return {@code true} in case there is a public key loaded
     */
    public boolean hasPublicKey() {
        return publicKey != null;
    }

    /**
     * Get a file from the resources embedded in the classpath.
     *
     * @param name the name of the file
     * @return the stream to read the file or {@code null} in case the file was not found
     */
    @Nullable
    private static InputStream getResourceAsStream(@Nonnull final String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
    }

    /**
     * Load the private key from any source available.
     */
    public void loadPrivateKey() {
        privateKey = loadKeyImpl(PRIVATE_KEY);

        if (hasPrivateKey()) {
            return;
        }

        LOGGER.error("Loading the private key failed.");
    }

    /**
     * Load the private key from a input stream.
     *
     * @param in the input stream the load the private key from
     */
    public void loadPrivateKey(final InputStream in) {
        privateKey = loadKeyImpl(in);

        if (hasPrivateKey()) {
            return;
        }

        LOGGER.error("Loading the private key failed.");
    }

    /**
     * Load the public key from any source available.
     */
    public void loadPublicKey() {
        publicKey = loadKeyImpl(PUBLIC_KEY);

        if (hasPublicKey()) {
            return;
        }

        LOGGER.error("Loading the public key failed.");
    }

    /**
     * Load the public key from a input stream.
     *
     * @param in the input stream the load the public key from
     */
    public void loadPublicKey(final InputStream in) {
        publicKey = loadKeyImpl(in);

        if (hasPublicKey()) {
            return;
        }

        LOGGER.error("Loading the public key failed.");
    }

    /**
     * Load a key from a string reference. This function will be check both the file system and the class path for
     * this file.
     *
     * @param keyFile the reference to the key file
     * @param <T>     the type of the key that is expected
     * @return        the loaded key or {@code null} in case the key was not found
     */
    @Nullable
    private static <T extends Key> T loadKeyImpl(@Nonnull final String keyFile) {
        final T resourceKey = loadKeyImpl(getResourceAsStream(keyFile));
        if (resourceKey != null) {
            return resourceKey;
        }

        final File fileRef = new File(keyFile);
        if (fileRef.exists() && fileRef.isFile() && fileRef.canRead()) {
            InputStream keyIn = null;
            try {
                keyIn = new BufferedInputStream(new FileInputStream(keyFile));
                final T fileKey = loadKeyImpl(keyIn);
                if (fileKey != null) {
                    return fileKey;
                }
            } catch (@Nonnull final Exception ignored) {
                // loading the key failed
            } finally {
                if (keyIn != null) {
                    try {
                        keyIn.close();
                    } catch (@Nonnull final IOException ignored) {}
                }
            }
        }
        return null;
    }

    /**
     * Load the key from a input stream.
     *
     * @param in the input stream the load the private key from
     * @return {@code true} in case the key was loaded successfully
     */
    @Nullable
    private static <T extends Key> T loadKeyImpl(@Nullable final InputStream in) {
        if (in != null) {
            try {
                final ObjectInputStream keyIn = new ObjectInputStream(in);
                final Object keyObject = keyIn.readObject();
                //noinspection unchecked
                return (T) keyObject;
            } catch (@Nonnull final Exception ignored) {
                // loading the key failed
            }
        }
        return null;
    }
}
