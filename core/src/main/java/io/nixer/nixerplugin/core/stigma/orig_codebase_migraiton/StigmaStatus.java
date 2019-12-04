package io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton;

/**
 * Represents possible statuses of stigma value.
 */
public enum StigmaStatus {
    ISSUED,         // Stigma sent to proxy, but not yet used (not yet attached to HTTP traffic with set-cookie)
    ACTIVE,         // Stigma in usage
    REVOKED,        // Revoked status is set by software algorithm
    BANNED,         // Banned status is set by admin user manually
    EXPIRED,        // Stigma can expire due to defined timeout, e.g. based on issue date
    UNKNOWN,        // Stigma extracted successfully from incoming token but not found in database
    NONEXISTENT     // To be used in history records as status before creating a stigma
}
