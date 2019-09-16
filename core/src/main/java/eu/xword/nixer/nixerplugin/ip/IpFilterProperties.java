package eu.xword.nixer.nixerplugin.ip;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "nixer.filter.ip")
@Component
public class IpFilterProperties {

    private boolean enabled;

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
