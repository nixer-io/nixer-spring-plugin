package io.nixer.nixerplugin.stigma.decision;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import org.springframework.util.Assert;

/**
 * Represents decision whether a Stigma should be refreshed or not.
 * If the refresh is required, by means of the {@link #event}, the carried {@link #tokenForRefresh} is to be used.
 *
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
@Immutable
public class StigmaRefreshDecision {

    @Nullable
    private final RawStigmaToken tokenForRefresh;

    @Nonnull
    private final StigmaEvent event;

    public static StigmaRefreshDecision refresh(@Nonnull final RawStigmaToken tokenForRefresh, @Nonnull final StigmaEvent event) {
        Assert.notNull(tokenForRefresh, "tokenForRefresh must not be null");
        Assert.notNull(event, "event must not be null");
        Assert.isTrue(event.requiresStigmaRefresh, "event must require Stigma refresh");

        return new StigmaRefreshDecision(tokenForRefresh, event);
    }

    public static StigmaRefreshDecision noRefresh(@Nonnull final StigmaEvent event) {
        Assert.notNull(event, "event must not be null");
        Assert.isTrue(!event.requiresStigmaRefresh, "event must not require Stigma refresh");

        return new StigmaRefreshDecision(null, event);
    }

    private StigmaRefreshDecision(@Nullable final RawStigmaToken tokenForRefresh, @Nonnull final StigmaEvent event) {
        this.tokenForRefresh = tokenForRefresh;
        Assert.notNull(event, "event must not be null");
        this.event = event;
    }

    /**
     * Applies decision by executing the given action if token refresh is required or ignoring it otherwise.
     *
     * @param refreshAction to be executed, never null.
     */
    public void apply(@Nonnull final Consumer<RawStigmaToken> refreshAction) {
        Assert.notNull(refreshAction, "refreshAction must not be null");
        if (event.requiresStigmaRefresh) {
            refreshAction.accept(tokenForRefresh);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StigmaRefreshDecision that = (StigmaRefreshDecision) o;
        return Objects.equals(tokenForRefresh, that.tokenForRefresh) &&
                event == that.event;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenForRefresh, event);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", StigmaRefreshDecision.class.getSimpleName() + "[", "]")
                .add("tokenForRefresh='" + tokenForRefresh + "'")
                .add("event=" + event)
                .toString();
    }
}
