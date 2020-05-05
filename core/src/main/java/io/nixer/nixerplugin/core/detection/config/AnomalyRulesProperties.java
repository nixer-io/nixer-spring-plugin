package io.nixer.nixerplugin.core.detection.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Central class for rules properties
 */
@ConfigurationProperties(prefix = "nixer.rules")
public class AnomalyRulesProperties {

    /**
     * Maps rule properties by name that they correspond to
     */
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
        ip,
        fingerprint
    }
}
