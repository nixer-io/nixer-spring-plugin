package io.nixer.nixerplugin.core.stigma;

import com.google.common.base.Objects;

/**
 * Represents raw, not parsed, stigma token.
 */
public class RawStigmaToken {

    private final String value;

    public RawStigmaToken(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RawStigmaToken that = (RawStigmaToken) o;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
