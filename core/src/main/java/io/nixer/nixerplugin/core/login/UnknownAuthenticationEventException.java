package io.nixer.nixerplugin.core.login;

import org.springframework.security.authentication.event.AbstractAuthenticationEvent;

/**
 * Thrown when {@link AbstractAuthenticationEvent} is not parsed as expected.
 */
public class UnknownAuthenticationEventException extends Exception {

    UnknownAuthenticationEventException(final String message) {
        super(message);
    }
}
