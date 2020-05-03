package io.nixer.nixerplugin.stigma;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nixer.stigma")
public class StigmaProperties {

    /**
     * Name of HTTP cookie to be used for storing Stigma Token.
     */
    private String cookieName = "stgtk";

    /**
     * Amount of time after Stigma is considered expired.
     */
    private Duration stigmaLifetime = Duration.ofDays(365);

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
