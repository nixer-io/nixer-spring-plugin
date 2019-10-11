package eu.xword.nixer.nixerplugin.detection.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nixer.rules")
public class FailedLoginThresholdRulesProperties {

    //todo consider string to enum
    private Map<String, RuleProperties> failedLoginThreshold = new HashMap<>();

    public Map<String, RuleProperties> getFailedLoginThreshold() {
        return failedLoginThreshold;
    }

    public void setFailedLoginThreshold(final Map<String, RuleProperties> failedLoginThreshold) {
        this.failedLoginThreshold = failedLoginThreshold;
    }

}
