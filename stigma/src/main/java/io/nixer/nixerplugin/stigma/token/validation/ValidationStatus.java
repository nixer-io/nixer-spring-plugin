package io.nixer.nixerplugin.stigma.token.validation;

/**
 * Created on 2019-05-31.
 *
 * @author gcwiak
 */
public enum ValidationStatus {

    // One and only valid result
    VALID(true),

    // Invalid results
    MISSING_STIGMA,
    INVALID_PAYLOAD,
    PARSING_ERROR,
    WRONG_ENC,
    WRONG_ALG,
    NOT_ENCRYPTED,
    DECRYPTION_ERROR,
    PAYLOAD_PARSING_ERROR,

    // Invalid result reserved for unknown failure reasons, e.g. unexpected exceptions
    UNEXPECTED_VALIDATION_ERROR;

    private final boolean isValid;

    ValidationStatus(final boolean isValid) {
        this.isValid = isValid;
    }

    ValidationStatus() {
        this(false);
    }

    public boolean isValid() {
        return isValid;
    }
}