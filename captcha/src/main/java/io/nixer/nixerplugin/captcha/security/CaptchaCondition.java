package io.nixer.nixerplugin.captcha.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static io.nixer.nixerplugin.captcha.CaptchaBehavior.CAPTCHA_CHALLENGE_SESSION_ATTR;

public enum CaptchaCondition {

    SESSION_CONTROLLED {
        @Override
        public boolean test(HttpServletRequest request) {
            final HttpSession session = request.getSession(false);
            return session != null && Boolean.TRUE.equals(session.getAttribute(CAPTCHA_CHALLENGE_SESSION_ATTR));
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
