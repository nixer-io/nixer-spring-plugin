package eu.xword.nixer.nixerplugin.detection.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nixer.rules")
public class AnomalyRulesProperties {

    private Map<Name, WindowThresholdRuleProperties> failedLoginThreshold = new HashMap<>();

    public Map<Name, WindowThresholdRuleProperties> getFailedLoginThreshold() {
        return failedLoginThreshold;
    }

    public void setFailedLoginThreshold(final Map<Name, WindowThresholdRuleProperties> failedLoginThreshold) {
        this.failedLoginThreshold = failedLoginThreshold;
    }

    public enum Name {
        username,
        useragent,
        ip
    }
}
