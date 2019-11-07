package eu.xword.nixer.nixerplugin.core.stigma;

import com.google.common.base.Objects;

public class StigmaToken {

    private final String value;

    public StigmaToken(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StigmaToken that = (StigmaToken) o;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
