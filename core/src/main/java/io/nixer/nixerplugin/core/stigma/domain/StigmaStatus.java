package io.nixer.nixerplugin.core.stigma.domain;

/**
 * Represents possible statuses of stigma value.
 */
public enum StigmaStatus {
    ACTIVE,         // Stigma in usage
    REVOKED,        // Revoked status is set by software algorithm
    BANNED,         // Banned status is set by admin user manually
    UNKNOWN,        // Stigma extracted successfully from incoming token but not found in database
    NONEXISTENT     // To be used in history records as status before creating a stigma
}
