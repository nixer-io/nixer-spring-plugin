package io.nixer.nixerplugin.core.detection.filter.login;

import javax.servlet.http.HttpServletRequest;

import io.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.detection.registry.IpOverLoginThresholdRegistry;
import org.springframework.util.Assert;

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

        request.setAttribute(RequestMetadata.IP_FAILED_LOGIN_OVER_THRESHOLD, isIpOverThreshold);
    }
}
