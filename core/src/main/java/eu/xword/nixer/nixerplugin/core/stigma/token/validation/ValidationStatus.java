package eu.xword.nixer.nixerplugin.core.stigma.token.validation;

/**
 * Created on 2019-05-31.
 *
 * @author gcwiak
 */
public enum ValidationStatus {

    // One and only valid result
    VALID(true, true),

    // Invalid results
    MISSING,
    MISSING_STIGMA,
    EXPIRED(false, true), // FIXME to be removed as expiration is checked against DB
    INVALID_PAYLOAD(false, true),
    PARSING_ERROR,
    WRONG_ENC,
    WRONG_ALG,
    NOT_ENCRYPTED,
    DECRYPTION_ERROR,
    PAYLOAD_PARSING_ERROR,

    // Invalid result reserved for unknown failure reasons, e.g. unexpected exceptions
    UNEXPECTED_VALIDATION_ERROR;

    private final boolean isValid;
    private final boolean isReadable;

    ValidationStatus(final boolean isValid, final boolean isReadable) {
        this.isValid = isValid;
        this.isReadable = isReadable;
    }

    ValidationStatus() {
        this(false, false);
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isReadable() {
        return isReadable;
    }
}
