package io.nixer.nixerplugin.core.fingerprint.store_draft;

import java.util.Objects;
import java.util.StringJoiner;

public class FingerprintMetadata {

    private final String fingerprint;

    public FingerprintMetadata(final String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final FingerprintMetadata that = (FingerprintMetadata) o;
        return Objects.equals(fingerprint, that.fingerprint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fingerprint);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FingerprintMetadata.class.getSimpleName() + "[", "]")
                .add("fingerprint='" + fingerprint + "'")
                .toString();
    }
}
