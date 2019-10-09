package eu.xword.nixer.nixerplugin.rules;

import java.time.Duration;

public class RuleProperties {

    /**
     * Whether rule is enabled
     */
    private boolean enabled;

    /**
     * Window size in minutes that will be used to calculate metric.
     */
    private Duration window = WindowSize.WINDOW_5M;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public Duration getWindow() {
        return window;
    }

    public void setWindow(final String window) {
        this.window = WindowSize.fromString(window);
    }
}
