package io.nixer.nixerplugin.core.detection.filter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nixer.filters")
public class FilterProperties {

    /**
     * Whether Nixer servlet filters should execute behaviors in dry-run mode.
     */
    private boolean dryRun;

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;
    }
}
