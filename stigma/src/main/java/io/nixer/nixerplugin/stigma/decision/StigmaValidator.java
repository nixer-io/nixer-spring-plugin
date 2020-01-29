package io.nixer.nixerplugin.stigma.decision;

import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.util.NowSource;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;

/**
 * Created on 21/01/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmaValidator {

    private final NowSource nowSource;

    private final Duration stigmaLifetime;

    public StigmaValidator(final NowSource nowSource, final Duration stigmaLifetime) {
        this.nowSource = nowSource;
        this.stigmaLifetime = stigmaLifetime;
    }

    public boolean isValid(@Nullable final StigmaDetails stigmaDetails) {
        final Instant now = nowSource.now();

        if (stigmaDetails == null) {
            return false;
        }

        if (stigmaDetails.getStatus() != StigmaStatus.ACTIVE) {
            return false;
        }

        if (isExpired(stigmaDetails, now)) {
            return false;
        }

        return true;
    }

    private boolean isExpired(final StigmaDetails stigmaDetails, final Instant now) {
        return now.isAfter(stigmaDetails.getCreationDate().plus(stigmaLifetime));
    }
}
