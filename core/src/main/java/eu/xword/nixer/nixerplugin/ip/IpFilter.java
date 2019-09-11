package eu.xword.nixer.nixerplugin.ip;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class IpFilter extends OncePerRequestFilter {

    private final Log logger = LogFactory.getLog(getClass());

    private IpLookup ipLookup;

    public IpFilter(final IpLookup ipLookup) {
        this.ipLookup = ipLookup;

    }

    //TODO should be apply only for login request or should it be generic and applied with request matcher.

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        final String requestIp = request.getRemoteAddr();

        try {
            final IpAddress address = ipLookup.lookup(requestIp);

            if (address != null) {
                response.sendError(403, "Forbidden");
                logger.info("Got ip match " + address);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        filterChain.doFilter(request, response);
    }

}
