package io.nixer.nixerplugin.core.detection.filter.fingerprint;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import io.nixer.nixerplugin.core.detection.filter.MetadataFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

public class FingerprintFilter extends MetadataFilter {

    private static final String FINGERPRINT_TOKEN = "fingerprintToken";
    private final String fingerprintCookieName;

    public FingerprintFilter(final String fingerprintCookieName) {
        this.fingerprintCookieName = fingerprintCookieName;
    }

    @Override
    protected void apply(final HttpServletRequest request) {
        // 1. Verify Token
        // 2. setValid(true/false)
        // 3. generateIfNonExistent




        //request matcher from config

        final Cookie fingerprintCookie = WebUtils.getCookie(request, this.fingerprintCookieName);

        //todo: put object into attribute holding information about these 3 cases

        if (fingerprintCookie == null) {

            request.setAttribute(FINGERPRINT_TOKEN, null);
        } else if (!StringUtils.hasText(fingerprintCookie.getValue())) {

            request.setAttribute(FINGERPRINT_TOKEN, null);

        } else {

            request.setAttribute(FINGERPRINT_TOKEN, fingerprintCookie);
        }


        request.setAttribute(FINGERPRINT_TOKEN, fingerprintCookie);
    }

    // todo: if cookie not present attach it to http response
//    private void assignNewFingerprint(final HttpServletRequest request) {
//        request
//    }
}
