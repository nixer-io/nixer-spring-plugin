package eu.xword.nixer.nixerplugin.blocking.policies;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

@Order(1)
public abstract class BlockingPolicy extends OncePerRequestFilter {

    // TODO consolidate blocking policy to run all from same filter
    // TODO apply just for login request

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        apply(request, response);

        filterChain.doFilter(request, response);
    }

    public abstract void apply(final HttpServletRequest request, final HttpServletResponse response) throws IOException;

}
