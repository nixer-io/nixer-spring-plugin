package io.nixer.nixerplugin.core.fingerprint;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class FingerprintAssuringFilter extends OncePerRequestFilter {

    private final String fingerprintCookieName;
    private final FingerprintGenerator fingerprintGenerator;

    public FingerprintAssuringFilter(final String fingerprintCookieName, final FingerprintGenerator fingerprintGenerator) {
        this.fingerprintCookieName = fingerprintCookieName;
        this.fingerprintGenerator = fingerprintGenerator;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        try {
            assureFingerprint(request, response);
        } catch (Exception e) {
            logger.error("Failed to execute filter", e);
        }
        filterChain.doFilter(request, response);
    }

    private void assureFingerprint(final HttpServletRequest request, final HttpServletResponse response) {

        final Cookie fingerprintCookie = WebUtils.getCookie(request, this.fingerprintCookieName);

        if (fingerprintCookie == null || !StringUtils.hasText(fingerprintCookie.getValue())) {
            assignNewFingerprint(response);
        }
    }

    private void assignNewFingerprint(final HttpServletResponse response) {
        response.addCookie(new Cookie(fingerprintCookieName, fingerprintGenerator.newFingerprint()));
    }
}
