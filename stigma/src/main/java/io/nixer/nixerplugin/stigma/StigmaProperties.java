package io.nixer.nixerplugin.stigma;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static io.nixer.nixerplugin.stigma.token.StigmaTokenConstants.DEFAULT_STIGMA_LIFETIME;

@ConfigurationProperties(prefix = "nixer.stigma")
public class StigmaProperties {

    private static final String DEFAULT_STIGMA_COOKIE_NAME = "stgtk";

    private String cookieName = DEFAULT_STIGMA_COOKIE_NAME;

    private Duration stigmaLifetime = DEFAULT_STIGMA_LIFETIME;

    private String decryptionKeyFile;
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
