package eu.xword.nixer.nixerplugin.core.filter;

import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.core.registry.IpOverLoginThresholdRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static eu.xword.nixer.nixerplugin.core.filter.RequestAugmentation.IP_FAILED_LOGIN_OVER_THRESHOLD;

/**
 * Appends information if request ip is over threshold for failed login.
 */
public class IpFailedLoginOverThresholdFilter extends MetadataFilter {

    private final IpOverLoginThresholdRegistry ipOverLoginThresholdRegistry;

    public IpFailedLoginOverThresholdFilter(final IpOverLoginThresholdRegistry ipOverLoginThresholdRegistry) {
        Assert.notNull(ipOverLoginThresholdRegistry, "IpOverLoginThresholdRegistry must not be null");
        this.ipOverLoginThresholdRegistry = ipOverLoginThresholdRegistry;
    }

    @Override
    protected void apply(final HttpServletRequest request) {
        final String ip = request.getRemoteAddr();
        final boolean isIpOverThreshold = ipOverLoginThresholdRegistry.contains(ip);
        request.setAttribute(IP_FAILED_LOGIN_OVER_THRESHOLD, isIpOverThreshold);
    }
}
