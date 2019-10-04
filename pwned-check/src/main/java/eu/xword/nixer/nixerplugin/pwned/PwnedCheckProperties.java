package eu.xword.nixer.nixerplugin.pwned;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created on 24/09/2019.
 *
 * @author gcwiak
 */
@ConfigurationProperties(prefix = "nixer.pwned.check")
public class PwnedCheckProperties {

    private String pwnedFilePath;

    public String getPwnedFilePath() {
        return pwnedFilePath;
    }

    public void setPwnedFilePath(final String pwnedFilePath) {
        this.pwnedFilePath = pwnedFilePath;
    }

}
