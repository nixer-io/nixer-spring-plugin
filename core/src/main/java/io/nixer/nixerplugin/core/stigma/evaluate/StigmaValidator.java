package io.nixer.nixerplugin.core.stigma.evaluate;

import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.core.stigma.storage.StigmaData;
import io.nixer.nixerplugin.core.util.NowSource;

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

    public boolean isValid(@Nullable final StigmaData stigmaData) {
        final Instant now = nowSource.now();

        if (stigmaData == null) {
            return false;
        }

        if (stigmaData.getStatus() != StigmaStatus.ACTIVE) {
            return false;
        }

        if (isExpired(stigmaData, now)) {
            return false;
        }

        return true;
    }

    private boolean isExpired(final StigmaData stigmaData, final Instant now) {
        return now.isAfter(stigmaData.getCreationDate().plus(stigmaLifetime));
    }
}
