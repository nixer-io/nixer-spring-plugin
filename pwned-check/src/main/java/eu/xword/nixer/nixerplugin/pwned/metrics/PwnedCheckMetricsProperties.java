package eu.xword.nixer.nixerplugin.pwned.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created on 17/10/2019.
 *
 * @author gcwiak
 */
@ConfigurationProperties(prefix = "nixer.pwned.check.metrics")
public class PwnedCheckMetricsProperties {

    /**
     * Indicates pwned-check metrics functionality is enabled.
     * Used in {@link PwnedCheckMetricsConfiguration}, kept here for documentation purposes.
     */
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
