package io.nixer.nixerplugin.stigma;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nixer.stigma")
public class StigmaProperties {

    private static final String DEFAULT_STIGMA_COOKIE_NAME = "stgtk";

    private String cookieName = DEFAULT_STIGMA_COOKIE_NAME;

    private String tokenLifetime;

    private String decryptionKeyFile;
    private String encryptionKeyFile;

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(final String cookieName) {
        this.cookieName = cookieName;
    }

    public String getTokenLifetime() {
        return tokenLifetime;
    }

    public void setTokenLifetime(final String tokenLifetime) {
        this.tokenLifetime = tokenLifetime;
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
