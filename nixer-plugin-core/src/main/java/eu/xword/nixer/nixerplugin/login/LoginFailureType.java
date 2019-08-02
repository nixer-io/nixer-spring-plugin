package eu.xword.nixer.nixerplugin.login;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import eu.xword.nixer.nixerplugin.blocking.policies.BadCaptchaException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public enum LoginFailureType {
    UNKNOWN_USER,
    BAD_PASSWORD,
    INVALID_CAPTCHA,
    MFA,
    LOCKED,
    EXPIRED,
    DISABLED,
    OTHER;

    private static final Map<Class<? extends AuthenticationException>, LoginFailureType> FAILURE_TYPE_BY_EXCEPTION;

    static {
        FAILURE_TYPE_BY_EXCEPTION = ImmutableMap.<Class<? extends AuthenticationException>, LoginFailureType>builder()
                .put(BadCredentialsException.class, BAD_PASSWORD)
                .put(UsernameNotFoundException.class, UNKNOWN_USER) // TODO hidden as BadCredentialsException, requires hideUserNotFoundExceptions
                .put(LockedException.class, LOCKED)
                .put(AccountExpiredException.class, EXPIRED)
                .put(DisabledException.class, DISABLED)
                .put(BadCaptchaException.class, INVALID_CAPTCHA) // TODO reported as BAD_PASSWORD
                .build(); // TODO separated exception for credentials and account expired/disabled/locked
    }

    public static LoginFailureType fromException(AuthenticationException ex) {
        return FAILURE_TYPE_BY_EXCEPTION.getOrDefault(ex.getClass(), OTHER);
    }
}
