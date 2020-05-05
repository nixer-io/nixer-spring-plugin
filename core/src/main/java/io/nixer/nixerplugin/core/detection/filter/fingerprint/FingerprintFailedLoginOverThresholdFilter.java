package io.nixer.nixerplugin.core.detection.filter.fingerprint;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import io.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.detection.registry.FingerprintFailedLoginOverThresholdRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

public class FingerprintFailedLoginOverThresholdFilter extends MetadataFilter {

    private final String fingerprintCookieName;
    private final FingerprintFailedLoginOverThresholdRegistry registry;

    public FingerprintFailedLoginOverThresholdFilter(final String fingerprintCookieName,
                                                     final FingerprintFailedLoginOverThresholdRegistry registry) {
        this.fingerprintCookieName = fingerprintCookieName;
        this.registry = registry;
    }

    @Override
    protected void apply(final HttpServletRequest request) {

        final Cookie fingerprintCookie = WebUtils.getCookie(request, fingerprintCookieName);

        if (fingerprintCookie != null && StringUtils.hasText(fingerprintCookie.getValue())) {

            final String fingerprint = fingerprintCookie.getValue();

            boolean isFailedLoginThresholdExceeded = registry.contains(fingerprint);

            request.setAttribute(RequestMetadata.FINGERPRINT_VALUE, fingerprint);
            request.setAttribute(RequestMetadata.FINGERPRINT_FAILED_LOGIN_OVER_THRESHOLD, isFailedLoginThresholdExceeded);

        } else {
            request.setAttribute(RequestMetadata.FINGERPRINT_FAILED_LOGIN_OVER_THRESHOLD, false);
        }
    }

}
