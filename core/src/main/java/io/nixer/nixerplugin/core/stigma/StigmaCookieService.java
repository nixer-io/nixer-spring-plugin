package io.nixer.nixerplugin.core.stigma;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

public class StigmaCookieService {

    @Value("${nixer.stigma.cookie}")
    private String stigmaCookieName;

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
