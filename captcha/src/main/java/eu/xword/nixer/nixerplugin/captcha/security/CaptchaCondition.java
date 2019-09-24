package eu.xword.nixer.nixerplugin.captcha.security;

import javax.servlet.http.HttpServletRequest;

import static eu.xword.nixer.nixerplugin.captcha.Marker.CAPTCHA_ENABLED;

public enum CaptchaCondition {
    AUTOMATIC {
        @Override
        public boolean test(HttpServletRequest request) {
            final Object captchaMitigation = request.getAttribute(CAPTCHA_ENABLED);
            return Boolean.TRUE.equals(captchaMitigation);
        }

    },
    ALWAYS {
        @Override
        public boolean test(final HttpServletRequest request) {
            return true;
        }
    },
    NEVER {
        @Override
        public boolean test(final HttpServletRequest request) {
            return false;
        }
    };

    public abstract boolean test(HttpServletRequest request);
}
