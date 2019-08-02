package eu.xword.nixer.nixerplugin.stigma;

public class StigmaProperties {
    private String cookie;

    private String tokenLifetime;

    private String decryptionKeyFile;
    private String encryptionKeyFile;

    public String getCookie() {
        return cookie;
    }

    public void setCookie(final String cookie) {
        this.cookie = cookie;
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
