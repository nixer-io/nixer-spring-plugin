package io.nixer.nixerplugin.stigma;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.nixer.nixerplugin.stigma.StigmaConstants.DEFAULT_STIGMA_LIFETIME;

@ConfigurationProperties(prefix = "nixer.stigma")
public class StigmaProperties {

    private static final String DEFAULT_STIGMA_COOKIE_NAME = "stgtk";

    /**
     * Name of HTTP cookie to be used for storing Stigma Token.
     */
    private String cookieName = DEFAULT_STIGMA_COOKIE_NAME;

    /**
     * Amount of time after Stigma is considered expired.
     */
    private Duration stigmaLifetime = DEFAULT_STIGMA_LIFETIME;

    /**
     * Location of file resource with decryption keys to be used for reading Stigma Tokens.
     * Can be either a "classpath:" pseudo URL, a "file:" URL, or a plain file path.
     */
    private String decryptionKeyFile;

    /**
     * Location of file resource with encryption key to be used for creating Stigma Tokens.
     * Can be either a "classpath:" pseudo URL, a "file:" URL, or a plain file path.
     */
    private String encryptionKeyFile;

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(final String cookieName) {
        this.cookieName = cookieName;
    }

    public Duration getStigmaLifetime() {
        return stigmaLifetime;
    }

    public void setStigmaLifetime(final Duration stigmaLifetime) {
        this.stigmaLifetime = stigmaLifetime;
    }

    public String getDecryptionKeyFile() {
        return decryptionKeyFile;
    }

    public void setDecryptionKeyFile(final String decryptionKeyFile) {
        this.decryptionKeyFile = decryptionKeyFile;
    }

    public String getEncryptionKeyFile() {
        return encryptionKeyFile;
    }

    public void setEncryptionKeyFile(final String encryptionKeyFile) {
        this.encryptionKeyFile = encryptionKeyFile;
    }
}
