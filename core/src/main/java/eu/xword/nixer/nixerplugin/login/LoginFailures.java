package eu.xword.nixer.nixerplugin.login;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static eu.xword.nixer.nixerplugin.login.LoginFailureType.BAD_PASSWORD;
import static eu.xword.nixer.nixerplugin.login.LoginFailureType.DISABLED;
import static eu.xword.nixer.nixerplugin.login.LoginFailureType.EXPIRED;
import static eu.xword.nixer.nixerplugin.login.LoginFailureType.LOCKED;
import static eu.xword.nixer.nixerplugin.login.LoginFailureType.OTHER;
import static eu.xword.nixer.nixerplugin.login.LoginFailureType.UNKNOWN_USER;

public class LoginFailures {

    private final Map<Class<? extends AuthenticationException>, LoginFailureType> failureTypeByException = new HashMap<>();

    {
        failureTypeByException.put(BadCredentialsException.class, BAD_PASSWORD);
        failureTypeByException.put(UsernameNotFoundException.class, UNKNOWN_USER); // TODO hidden as BadCredentialsException, requires hideUserNotFoundExceptions
        failureTypeByException.put(LockedException.class, LOCKED);
        failureTypeByException.put(AccountExpiredException.class, EXPIRED);
        failureTypeByException.put(DisabledException.class, DISABLED);
        // TODO separated exception for credentials and account expired/disabled/locked
    }

    public LoginFailureType fromException(AuthenticationException ex) {
        return failureTypeByException.getOrDefault(ex.getClass(), OTHER);
    }

    public LoginFailures addMapping(Class<? extends AuthenticationException> clazz, LoginFailureType loginFailureType) {
        failureTypeByException.put(clazz, loginFailureType);
        return this;
    }

}
