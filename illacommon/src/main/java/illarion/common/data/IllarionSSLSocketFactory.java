/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2016 - Illarion e.V.
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
package illarion.common.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
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
     * The logger used to show the error output of this class.
     */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(IllarionSSLSocketFactory.class);
    /**
     * The singleton instance of this factory.
     */
    @Nullable
    @SuppressWarnings("RedundantFieldInitialization")
    private static volatile IllarionSSLSocketFactory instance = null;
    /**
     * The factory instance.
     */
    @Nullable
    private SSLSocketFactory sslFactory;

    public IllarionSSLSocketFactory() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");

            ClassLoader cls = Thread.currentThread().getContextClassLoader();
            try (InputStream keyStoreInput = cls.getResourceAsStream("keystore.jks")) {
                keyStore.load(keyStoreInput, "jcFv8XQxRN".toCharArray());
            } catch (@Nonnull CertificateException | IOException | NoSuchAlgorithmException e) {
                log.error("Failed to load keystore.", e);
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "jcFv8XQxRN".toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            sslFactory = ctx.getSocketFactory();
        } catch (@Nonnull KeyStoreException e) {
            log.error("Failed to read keystore.", e);
        } catch (@Nonnull KeyManagementException e) {
            log.error("Failed to use keystore.", e);
        } catch (@Nonnull NoSuchAlgorithmException e) {
            log.error("Failed to decode keystore.", e);
        } catch (UnrecoverableKeyException e) {
            log.error("Failed to open keystore.", e);
        }
    }

    /**
     * Get the factory itself.
     *
     * @return the factory itself
     */
    @Nullable
    public static SSLSocketFactory getFactory() {
        IllarionSSLSocketFactory ref = instance;
        if (ref == null) {
            synchronized (IllarionSSLSocketFactory.class) {
                ref = instance;
                if (ref == null) {
                    ref = new IllarionSSLSocketFactory();
                    instance = ref;
                }
            }
        }
        return ref.sslFactory;
    }
}
