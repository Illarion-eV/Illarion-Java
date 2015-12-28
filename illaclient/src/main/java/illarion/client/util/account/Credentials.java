package illarion.client.util.account;

import illarion.common.config.Config;
import illarion.common.util.DirectoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * This class is the way to store and access the credentials for a specific server.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public class Credentials {

    @Nonnull
    private final AccountSystemEndpoint endpoint;

    @Nonnull
    private final Config cfg;

    @Nullable
    private static Reference<SecretKey> secretKeyRef;

    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(Credentials.class);

    @Nullable
    private String userName;

    @Nullable
    private String password;

    private boolean storePassword;

    public Credentials(@Nonnull AccountSystemEndpoint endpoint, @Nonnull Config cfg) {
        this.endpoint = endpoint;
        this.cfg = cfg;

        loadFromConfig();
    }

    private void loadFromConfig() {
        userName = cfg.getString(endpoint.getConfigSubKey("login.name"));
        String encryptedPassword = cfg.getString(endpoint.getConfigSubKey("login.password"));
        if (encryptedPassword != null && !encryptedPassword.isEmpty()) {
            password = getDecryptedPassword(encryptedPassword);
        }
        if ((userName == null || userName.isEmpty()) && (password == null || password.isEmpty())) {
            userName = endpoint.getDefaultUserName();
            password = endpoint.getDefaultPassword();
        }
        storePassword = cfg.getBoolean(endpoint.getConfigSubKey("login.storePassword"));
    }

    public void storeCredentials() {
        if (userName != null && !userName.isEmpty()) {
            cfg.set(endpoint.getConfigSubKey("login.name"), userName);
        } else {
            cfg.remove(endpoint.getConfigSubKey("login.name"));
        }

        if (storePassword && password != null && !password.isEmpty()) {
            String encryptedPassword = getEncryptedPassword(password);
            if (encryptedPassword != null) {
                cfg.set(endpoint.getConfigSubKey("login.password"), encryptedPassword);
            } else {
                cfg.remove(endpoint.getConfigSubKey("login.password"));
            }
        } else {
            cfg.remove(endpoint.getConfigSubKey("login.password"));
        }

        cfg.set(endpoint.getConfigSubKey("login.storePassword"), storePassword);
    }

    @Nonnull
    public AccountSystemEndpoint getEndpoint() {
        return endpoint;
    }

    @Nonnull
    public String getUserName() {
        return userName == null ? "" : userName;
    }

    public void setUserName(@Nullable String userName) {
        this.userName = userName;
    }

    @Nonnull
    public String getPassword() {
        return password == null ? "" : password;
    }

    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    public boolean isStorePassword() {
        return storePassword;
    }

    public void setStorePassword(boolean storePassword) {
        this.storePassword = storePassword;
    }

    /**
     * Create the secret key that is required to encrypt and decrypt the password.
     *
     * @return the secret key, either newly create or a cashed instance
     * @throws GeneralSecurityException in case generating the key fails
     */
    @Nonnull
    private static SecretKey getSecretKey() throws GeneralSecurityException {
        if (secretKeyRef != null) {
            SecretKey oldKey = secretKeyRef.get();
            if (oldKey != null) {
                return oldKey;
            }
        }

        Charset usedCharset = Charset.forName("UTF-8");
        Path userDir = DirectoryManager.getInstance().getDirectory(DirectoryManager.Directory.User);
        KeySpec keySpec = new DESKeySpec(userDir.toAbsolutePath().toString().getBytes(usedCharset));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey newKey = keyFactory.generateSecret(keySpec);
        secretKeyRef = new SoftReference<>(newKey);
        return newKey;
    }

    @Nullable
    private static String getDecryptedPassword(@Nonnull String encryptedPassword) {
        try {
            Charset usedCharset = Charset.forName("UTF-8");
            Cipher cipher = Cipher.getInstance("DES");
            SecretKey key = getSecretKey();

            Base64.Decoder decoder = Base64.getDecoder();
            byte[] encryptedPadBytes = decoder.decode(encryptedPassword.getBytes(usedCharset));
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedPadBytes = cipher.doFinal(encryptedPadBytes);

            return new String(decryptedPadBytes, usedCharset);
        } catch (@Nonnull GeneralSecurityException e) {
            log.warn("Error while decrypting password.", e);
        }
        return null;
    }

    @Nullable
    private static String getEncryptedPassword(@Nonnull String clearPassword) {
        try {
            Charset usedCharset = Charset.forName("UTF-8");
            Cipher cipher = Cipher.getInstance("DES");
            SecretKey key = getSecretKey();

            Base64.Encoder encoder = Base64.getEncoder();
            byte[] cleartext = clearPassword.getBytes(usedCharset);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new String(encoder.encode(cipher.doFinal(cleartext)), usedCharset);
        } catch (@Nonnull GeneralSecurityException e) {
            log.warn("Error while encrypting password.", e);
        }
        return null;
    }
}
