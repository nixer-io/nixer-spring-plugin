package eu.xword.nixer.nixerplugin.filter;

import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.registry.BlockedIpRegistry;
import org.springframework.stereotype.Component;

@Component
public class TemporalIpFilter extends MetadataFilter {

    private BlockedIpRegistry blockedIpRegistry;

    public TemporalIpFilter(final BlockedIpRegistry blockedIpRegistry) {
        this.blockedIpRegistry = blockedIpRegistry;
    }

    @Override
    protected void apply(final HttpServletRequest request) {
        final String ip = request.getRemoteAddr();
        if (blockedIpRegistry.isBlocked(ip)) {
            request.setAttribute(RequestAugmentation.IP_BLOCKED, true);
        }
    }
}
