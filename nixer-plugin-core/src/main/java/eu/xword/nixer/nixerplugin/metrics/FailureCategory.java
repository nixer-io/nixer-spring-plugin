package eu.xword.nixer.nixerplugin.metrics;

public enum FailureCategory {
    UNKNOWN_USER,
    BAD_PASSWORD,
    INVALID_CAPTCHA,
    MFA,
    LOCKED,
    EXPIRED,
    DISABLED,
    OTHER
}
