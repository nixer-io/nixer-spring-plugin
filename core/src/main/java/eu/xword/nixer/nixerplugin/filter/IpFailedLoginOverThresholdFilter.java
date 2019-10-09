package eu.xword.nixer.nixerplugin.filter;

import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.registry.IpFailedLoginOverThresholdRegistry;
import org.springframework.stereotype.Component;

/**
 * Appends information if request ip is over threshold for failed login.
 */
@Component
public class IpFailedLoginOverThresholdFilter extends MetadataFilter {

    private final IpFailedLoginOverThresholdRegistry ipFailedLoginOverThresholdRegistry;

    public IpFailedLoginOverThresholdFilter(final IpFailedLoginOverThresholdRegistry ipFailedLoginOverThresholdRegistry) {
        this.ipFailedLoginOverThresholdRegistry = ipFailedLoginOverThresholdRegistry;
    }

    @Override
    protected void apply(final HttpServletRequest request) {
        final String ip = request.getRemoteAddr();
        if (ipFailedLoginOverThresholdRegistry.contains(ip)) {
            request.setAttribute(RequestAugmentation.IP_FAILED_LOGIN_OVER_THRESHOLD, true);
        }
    }
}
