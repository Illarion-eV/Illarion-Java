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
package illarion.common.data;

import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * This class is used to access the custom SSL factory that is required to establish a secure connection to the
 * Illarion server.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class IllarionSSLSocketFactory {
    /**
     * The singleton instance of this factory.
     */
    private static final IllarionSSLSocketFactory INSTANCE = new IllarionSSLSocketFactory();

    /**
     * Get the factory itself.
     *
     * @return the factory itself
     */
    @Nullable
    public static SSLSocketFactory getFactory() {
        return INSTANCE.sslFactory;
    }

    /**
     * The factory instance.
     */
    @Nullable
    private SSLSocketFactory sslFactory;

    /**
     * The logger used to show the error output of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(IllarionSSLSocketFactory.class);

    public IllarionSSLSocketFactory() {
        try {
            final KeyStore keyStore = KeyStore.getInstance("JKS");

            InputStream keyStoreInput = null;
            try {
                keyStoreInput = Thread.currentThread().getContextClassLoader().getResourceAsStream("keystore.jks");
                keyStore.load(keyStoreInput, "jcFv8XQxRN".toCharArray());
            } catch (@Nonnull final CertificateException e) {
                LOGGER.error("Failed to load keystore.", e);
            } catch (@Nonnull final NoSuchAlgorithmException e) {
                LOGGER.error("Failed to load keystore.", e);
            } catch (@Nonnull final IOException e) {
                LOGGER.error("Failed to load keystore.", e);
            } finally {
                if (keyStoreInput != null) {
                    keyStoreInput.close();
                }
            }

            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "jcFv8XQxRN".toCharArray());
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            final SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            sslFactory = ctx.getSocketFactory();
        } catch (@Nonnull final KeyStoreException e) {
            LOGGER.error("Failed to read keystore.", e);
        } catch (@Nonnull final KeyManagementException e) {
            LOGGER.error("Failed to use keystore.", e);
        } catch (@Nonnull final NoSuchAlgorithmException e) {
            LOGGER.error("Failed to decode keystore.", e);
        } catch (@Nonnull final IOException e) {
            LOGGER.error("Failed to load keystore.", e);
        } catch (UnrecoverableKeyException e) {
            LOGGER.error("Failed to open keystore.", e);
        }
    }
}
