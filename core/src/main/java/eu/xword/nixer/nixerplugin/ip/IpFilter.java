package eu.xword.nixer.nixerplugin.ip;

import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.blocking.policies.NixerFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IpFilter extends NixerFilter {

    private final Log logger = LogFactory.getLog(getClass());

    private IpResolver ipResolver = IpResolvers.REMOTE_ADDRESS;

    private IpLookup ipLookup;

    public IpFilter(final IpLookup ipLookup) {
        this.ipLookup = ipLookup;
    }

    //TODO should be apply only for login request or should it be generic and applied with request matcher.

    @Override
    protected boolean applies(final HttpServletRequest request) {
        final String ip = ipResolver.resolve(request);
        final IpAddress address = ipLookup.lookup(ip);

        return address != null;
    }
}
