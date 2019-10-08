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

    private final Map<Class<? extends AuthenticationException>, LoginFailureType> failureTypeByException;

    public LoginFailureTypeRegistry(final Map<Class<? extends AuthenticationException>, LoginFailureType> mapping) {
        this.failureTypeByException = mapping;
    }

    public LoginFailureType fromException(AuthenticationException ex) {
        return failureTypeByException.getOrDefault(ex.getClass(), OTHER);
    }

    public Set<LoginFailureType> getReasons() {
        return ImmutableSet.copyOf(failureTypeByException.values());
    }

    public static class Builder {

        private final Map<Class<? extends AuthenticationException>, LoginFailureType> mappings = new HashMap<>();

        {
            addMapping(BadCredentialsException.class, BAD_PASSWORD);
            addMapping(UsernameNotFoundException.class, UNKNOWN_USER); // TODO hidden as BadCredentialsException, requires hideUserNotFoundExceptions
            addMapping(LockedException.class, LOCKED);
            addMapping(AccountExpiredException.class, EXPIRED);
            addMapping(DisabledException.class, DISABLED);
            // TODO separated exception for credentials and account expired/disabled/locked
        }

        public Builder addMapping(Class<? extends AuthenticationException> clazz, LoginFailureType loginFailureType) {
            mappings.put(clazz, loginFailureType);
            return this;
        }

        public LoginFailureTypeRegistry build() {
            return new LoginFailureTypeRegistry(mappings);
        }
    }

    public interface Contributor {
        void contribute(Builder builder);
    }

}
