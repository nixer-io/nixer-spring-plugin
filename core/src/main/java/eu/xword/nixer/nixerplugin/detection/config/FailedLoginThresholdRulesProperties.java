package eu.xword.nixer.nixerplugin.detection.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nixer.rules")
public class FailedLoginThresholdRulesProperties {

    //todo consider string to enum
    private Map<Name, RuleProperties> failedLoginThreshold = new HashMap<>();

    public Map<Name, RuleProperties> getFailedLoginThreshold() {
        return failedLoginThreshold;
    }

    public void setFailedLoginThreshold(final Map<Name, RuleProperties> failedLoginThreshold) {
        this.failedLoginThreshold = failedLoginThreshold;
    }

    public enum Name {
        username,
        useragent,
        ip
    }
}
