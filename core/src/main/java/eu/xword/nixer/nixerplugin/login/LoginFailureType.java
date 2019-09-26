package eu.xword.nixer.nixerplugin.login;

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
