package io.nixer.nixerplugin.stigma.login;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

public class StigmaCookieService {

    private final String stigmaCookieName;

    public StigmaCookieService(final String stigmaCookieName) {
        Assert.notNull(stigmaCookieName, "stigmaCookieName must not be null");
        this.stigmaCookieName = stigmaCookieName;
    }

    public RawStigmaToken readStigmaToken(final HttpServletRequest request) {
        final Cookie stigmaCookie = WebUtils.getCookie(request, this.stigmaCookieName);

        return stigmaCookie != null
                ? emptyToNull(stigmaCookie.getValue())
                : null;
    }

    private RawStigmaToken emptyToNull(String value) {
        return StringUtils.hasText(value)
                ? new RawStigmaToken(value)
                : null;
    }

    public void writeStigmaToken(final HttpServletResponse response, final RawStigmaToken stigma) {
        if (stigma != null) {
            response.addCookie(new Cookie(stigmaCookieName, stigma.getValue()));
        }
    }
}
