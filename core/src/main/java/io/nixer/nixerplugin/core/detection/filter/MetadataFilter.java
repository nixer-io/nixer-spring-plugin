package io.nixer.nixerplugin.core.detection.filter;

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
public abstract class MetadataFilter extends OncePerRequestFilter {

    private final Log logger = LogFactory.getLog(getClass());

    protected abstract void apply(final HttpServletRequest request);

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            this.apply(request);
        } catch (Exception e) {
            logger.error("Failed to execute filter", e);
        }
        filterChain.doFilter(request, response);
    }
}
