package eu.xword.nixer.nixerplugin.pwned.metrics;

import eu.xword.nixer.nixerplugin.metrics.MetricsLookupId;

/**
 * Created on 17/10/2019.
 *
 * @author gcwiak
 */
public enum PwnedCheckMetrics implements MetricsLookupId {

    PWNED_PASSWORD(
            "Password is pwned",
            "pwned_password"
    ),

    NOT_PWNED_PASSWORD(
            "Password is not pwned",
            "not_pwned_password"
    );

    public final String metricName = "pwned_check";
    public final String resultTag = "result";
    public final String description;
    public final String result;

    @Override
    public String lookupId() {
        return name();
    }

    PwnedCheckMetrics(final String description, final String result) {
        this.description = description;
        this.result = result;
    }
}
