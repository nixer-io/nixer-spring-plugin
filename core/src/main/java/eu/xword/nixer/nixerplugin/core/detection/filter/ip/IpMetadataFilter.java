package eu.xword.nixer.nixerplugin.core.detection.filter.ip;

import javax.servlet.http.HttpServletRequest;

import eu.xword.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import eu.xword.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import eu.xword.nixer.nixerplugin.core.domain.ip.IpLookup;
import eu.xword.nixer.nixerplugin.core.domain.ip.net.IpAddress;
import org.springframework.util.Assert;

/**
 * NixerFilter that matches request IP with IP ranges, executing action on match.
 * In addition it augments request with {@link IpMetadata}.
 */
public class IpMetadataFilter extends MetadataFilter {

    private final IpLookup ipLookup;

    public IpMetadataFilter(final IpLookup ipLookup) {
        Assert.notNull(ipLookup, "IpLookup must not be null");
        this.ipLookup = ipLookup;
    }

    //TODO should be apply only for login request or should it be generic and applied with request matcher.

    @Override
    protected void apply(final HttpServletRequest request) {
        final String ip = request.getRemoteAddr();
        final IpAddress address = ipLookup.lookup(ip);

        if (address != null) {
            request.setAttribute(RequestMetadata.IP_METADATA, new IpMetadata(true));
        }
    }
}
