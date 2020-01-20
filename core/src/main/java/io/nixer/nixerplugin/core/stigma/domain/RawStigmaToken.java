package io.nixer.nixerplugin.core.stigma.domain;

import javax.annotation.Nonnull;

import com.google.common.base.Objects;
import org.springframework.util.Assert;

/**
 * Represents raw, not parsed, stigma token, e.g. serialized JWT.
 */
public class RawStigmaToken {

    @Nonnull
    private final String value;

    public RawStigmaToken(final String value) {
        Assert.notNull(value, "value must not be null");
        this.value = value;
    }

    @Nonnull
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
