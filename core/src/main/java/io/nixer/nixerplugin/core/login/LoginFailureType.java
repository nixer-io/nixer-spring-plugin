package io.nixer.nixerplugin.core.login;

/**
 * Defines reason of login failure.
 */
public enum LoginFailureType {
    UNKNOWN_USER,
    BAD_PASSWORD,
    INVALID_CAPTCHA,
    LOCKED,
    EXPIRED,
    DISABLED,
    OTHER;
}
