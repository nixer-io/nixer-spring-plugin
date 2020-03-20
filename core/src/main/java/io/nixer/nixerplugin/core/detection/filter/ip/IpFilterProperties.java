package io.nixer.nixerplugin.core.detection.filter.ip;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nixer.filter.ip")
public class IpFilterProperties {

    /**
     * Whether matching requests IP addresses to defined IP ranges is enabled.
     */
    private boolean enabled;

    /**
     * Location of file resource with the IP ranges.
     * Can be either a "classpath:" pseudo URL, a "file:" URL, or a plain file path.
     */
    private String ipPrefixesPath;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getIpPrefixesPath() {
        return ipPrefixesPath;
    }

    public void setIpPrefixesPath(final String ipPrefixesPath) {
        this.ipPrefixesPath = ipPrefixesPath;
    }
}
