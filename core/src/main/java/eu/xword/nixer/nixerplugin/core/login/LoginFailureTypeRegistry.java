package eu.xword.nixer.nixerplugin.core.login;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import static eu.xword.nixer.nixerplugin.core.login.LoginFailureType.BAD_PASSWORD;
import static eu.xword.nixer.nixerplugin.core.login.LoginFailureType.DISABLED;
import static eu.xword.nixer.nixerplugin.core.login.LoginFailureType.EXPIRED;
import static eu.xword.nixer.nixerplugin.core.login.LoginFailureType.LOCKED;
import static eu.xword.nixer.nixerplugin.core.login.LoginFailureType.OTHER;
import static eu.xword.nixer.nixerplugin.core.login.LoginFailureType.UNKNOWN_USER;

/**
 * Keeps registry of {@link AuthenticationException} to {@link LoginFailureType} mappings
 */
public class LoginFailureTypeRegistry {

    private final Map<Class<? extends AuthenticationException>, LoginFailureType> failureTypeByException;

    private LoginFailureTypeRegistry(final Map<Class<? extends AuthenticationException>, LoginFailureType> mapping) {
        Assert.notNull(mapping, "Mapping must not be null");
        this.failureTypeByException = Collections.unmodifiableMap(mapping);
    }

    public LoginFailureType fromException(AuthenticationException ex) {
        return failureTypeByException.getOrDefault(ex.getClass(), OTHER);
    }

    public Collection<LoginFailureType> getReasons() {
        return Collections.unmodifiableCollection(failureTypeByException.values());
    }

    public static Builder builder() {
        return new Builder();
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

        private Builder() {
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
