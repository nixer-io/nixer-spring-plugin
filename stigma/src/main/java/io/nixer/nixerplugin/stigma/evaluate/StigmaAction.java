package io.nixer.nixerplugin.stigma.evaluate;

import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;

/**
 * DTO representing action related to a StigmaToken.
 *
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
@Immutable
public class StigmaAction {

    @Nonnull
    private final RawStigmaToken stigmaToken;

    @Nonnull
    private final StigmaActionType type;

    public StigmaAction(@Nonnull final RawStigmaToken stigmaToken, @Nonnull final StigmaActionType type) {
        this.stigmaToken = Preconditions.checkNotNull(stigmaToken, "stigmaToken");
        this.type = Preconditions.checkNotNull(type, "type");
    }

    @Nonnull
    public RawStigmaToken getStigmaToken() {
        return stigmaToken;
    }

    @Nonnull
    public StigmaActionType getType() {
        return type;
    }

    public boolean isTokenRefreshRequired() {
        return type.isTokenRefreshRequired;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final StigmaAction that = (StigmaAction) o;

        if (!stigmaToken.equals(that.stigmaToken)) return false;
        return type == that.type;

    }

    @Override
    public int hashCode() {
        int result = stigmaToken.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StigmaAction.class.getSimpleName() + "[", "]")
                .add("stigmaToken='" + stigmaToken + "'")
                .add("type=" + type)
                .toString();
    }
}
