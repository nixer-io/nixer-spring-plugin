package io.nixer.nixerplugin.stigma.decision;

import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.util.NowSource;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created on 21/01/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmaValidator {

    private static final Log logger = LogFactory.getLog(StigmaValidator.class);

    private final NowSource nowSource;

    private final Duration stigmaLifetime;

    public StigmaValidator(final NowSource nowSource, final Duration stigmaLifetime) {
        this.nowSource = nowSource;
        this.stigmaLifetime = stigmaLifetime;
    }

    public boolean isValid(@Nullable final StigmaDetails stigmaDetails) {
        final Instant now = nowSource.now();

        if (stigmaDetails == null) {
            logger.trace("Invalid stigma - missing.");
            return false;
        }

        if (stigmaDetails.getStatus() != StigmaStatus.ACTIVE) {
            if (logger.isTraceEnabled()) {
                logger.trace("Invalid stigma - not active: " + stigmaDetails);
            }
            return false;
        }

        if (isExpired(stigmaDetails, now)) {
            if (logger.isTraceEnabled()) {
                logger.trace(String.format("Invalid stigma - expired: %s. Check time: %s", stigmaDetails, now));
            }
            return false;
        }

        logger.trace("Stigma is valid.");
        return true;
    }

    private boolean isExpired(final StigmaDetails stigmaDetails, final Instant now) {
        return now.isAfter(stigmaDetails.getCreationDate().plus(stigmaLifetime));
    }
}
