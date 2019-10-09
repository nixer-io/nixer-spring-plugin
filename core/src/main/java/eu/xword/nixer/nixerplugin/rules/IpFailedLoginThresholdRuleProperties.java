package eu.xword.nixer.nixerplugin.rules;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nixer.rules.ip-failed-login-threshold")
public class IpFailedLoginThresholdRuleProperties extends RuleProperties {

    public static final int DEFAULT_THRESHOLD = 5;

    /**
     * Defines at what metric value rule will trigger
     */
    private int threshold = DEFAULT_THRESHOLD;

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(final int threshold) {
        this.threshold = threshold;
    }
}
