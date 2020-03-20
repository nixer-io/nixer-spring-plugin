package io.nixer.nixerplugin.core.detection.events.log;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nixer.events.log")
public class EventLogProperties {

    /**
     * Whether anomaly events logging is enabled.
     */
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
