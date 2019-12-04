package io.nixer.nixerplugin.core.stigma.evaluate;

import java.util.Objects;
import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;

/**
 * Represents value of a stigma which is a part of a stigma token.
 *
 * Created on 2019-06-17.
 *
 * @author gcwiak
 */
@Immutable
public class Stigma {

    @Nonnull
    private final String value;

    public Stigma(@Nonnull final String value) {
        this.value = Preconditions.checkNotNull(value, "value");
    }

    @Nonnull
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Stigma stigma = (Stigma) o;
        return value.equals(stigma.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Stigma.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .toString();
    }
}
