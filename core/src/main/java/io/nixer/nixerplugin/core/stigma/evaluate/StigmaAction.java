package io.nixer.nixerplugin.core.stigma.evaluate;

import java.util.StringJoiner;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;

/**
 * DTO representing action related to a StigmaToken.
 *
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
@Immutable
public class StigmaAction {

    // FIXME is this action applicable at all?
    public static final StigmaAction STIGMA_ACTION_NOOP = new StigmaAction("", StigmaActionType.SKIP_ACTION) {
        @Nonnull
        @Override
        public String getStigmaToken() {
            throw new UnsupportedOperationException("getStigmaToken not supported on action of type SKIP_ACTION.");
        }
    };

    @Nonnull
    private final String stigmaToken;

    @Nonnull
    private final StigmaActionType type;

    public StigmaAction(@Nonnull final String stigmaToken, @Nonnull final StigmaActionType type) {
        this.stigmaToken = Preconditions.checkNotNull(stigmaToken, "stigmaToken");
        this.type = Preconditions.checkNotNull(type, "type");
    }

    @Nonnull
    public String getStigmaToken() {
        return stigmaToken;
    }

    @Nonnull
    StigmaActionType getType() {
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
