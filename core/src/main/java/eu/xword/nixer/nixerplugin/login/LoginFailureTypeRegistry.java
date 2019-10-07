package eu.xword.nixer.nixerplugin.login;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
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

/**
 * Maps {@link AuthenticationException} to {@link LoginFailureType}
 */
public class LoginFailureTypeRegistry {

    private final Map<Class<? extends AuthenticationException>, String> failureTypeByException = new HashMap<>();

    {
        addMapping(BadCredentialsException.class, BAD_PASSWORD);
        addMapping(UsernameNotFoundException.class, UNKNOWN_USER); // TODO hidden as BadCredentialsException, requires hideUserNotFoundExceptions
        addMapping(LockedException.class, LOCKED);
        addMapping(AccountExpiredException.class, EXPIRED);
        addMapping(DisabledException.class, DISABLED);
        // TODO separated exception for credentials and account expired/disabled/locked
    }

    public String fromException(AuthenticationException ex) {
        return failureTypeByException.getOrDefault(ex.getClass(), OTHER.name());
    }

    public LoginFailureTypeRegistry addMapping(Class<? extends AuthenticationException> clazz, LoginFailureType loginFailureType) {
        return addMapping(clazz, loginFailureType.name());
    }

    public LoginFailureTypeRegistry addMapping(Class<? extends AuthenticationException> clazz, String loginFailureType) {
        failureTypeByException.put(clazz, loginFailureType);
        return this;
    }

    public Set<String> getReasons() {
        return ImmutableSet.copyOf(failureTypeByException.values());
    }
}
