package eu.xword.nixer.nixerplugin.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.xword.nixer.nixerplugin.filter.strategy.MitigationStrategy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public abstract class NixerFilter extends OncePerRequestFilter {

    protected MitigationStrategy mitigationStrategy;

    // TODO consolidate blocking policy to run all from same filter
    // TODO apply just for login request

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        if (applies(request)) {
            act(request, response);
        }

        filterChain.doFilter(request, response);
    }

    protected void act(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        mitigationStrategy.handle(request, response);
    }

    protected abstract boolean applies(final HttpServletRequest request) throws IOException;

    public void setMitigationStrategy(final MitigationStrategy mitigationStrategy) {
        this.mitigationStrategy = mitigationStrategy;
    }
}
