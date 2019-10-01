package eu.xword.nixer.nixerplugin.filter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nixer.filters")
public class FilterProperties {

    private boolean dryRun;

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;
    }
}
