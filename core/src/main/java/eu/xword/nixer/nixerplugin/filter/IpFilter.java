package eu.xword.nixer.nixerplugin.filter;

import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.ip.IpLookup;
import eu.xword.nixer.nixerplugin.ip.IpMetadata;
import eu.xword.nixer.nixerplugin.ip.net.IpAddress;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * NixerFilter that matches request IP with IP ranges, executing action on match.
 * In addition it augments request with {@link IpMetadata}.
 */
public class IpFilter extends MetadataFilter {

    private final Log logger = LogFactory.getLog(getClass());

    private IpLookup ipLookup;

    public IpFilter(final IpLookup ipLookup) {
        this.ipLookup = ipLookup;
    }

    //TODO should be apply only for login request or should it be generic and applied with request matcher.

    @Override
    protected void apply(final HttpServletRequest request) {
        final String ip = request.getRemoteAddr();
        final IpAddress address = ipLookup.lookup(ip);

        if (address != null) {
            request.setAttribute(RequestAugmentation.IP_METADATA, new IpMetadata(true));
        }
    }
}
