package eu.xword.nixer.nixerplugin.pwned;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created on 24/09/2019.
 *
 * @author gcwiak
 */
@ConfigurationProperties(prefix = "nixer.pwned.check")
@Component
public class PwnedCheckProperties {

    static final boolean ENABLED_BY_DEFAULT = true;

    private boolean enabled = ENABLED_BY_DEFAULT;

    private String pwnedFilePath;

    public String getPwnedFilePath() {
        return pwnedFilePath;
    }

    public void setPwnedFilePath(final String pwnedFilePath) {
        this.pwnedFilePath = pwnedFilePath;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
