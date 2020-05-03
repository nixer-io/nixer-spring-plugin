package io.nixer.nixerplugin.stigma.decision;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.generate.StigmaGenerator;
import io.nixer.nixerplugin.stigma.storage.StigmaStorage;
import io.nixer.nixerplugin.stigma.token.read.StigmaExtractor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
public class StigmaService {

    private static final Log logger = LogFactory.getLog(StigmaService.class);

    @Nonnull
    private final StigmaStorage stigmaStorage;

    @Nonnull
    private final StigmaExtractor stigmaExtractor;

    @Nonnull
    private final StigmaGenerator stigmaGenerator;

    public StigmaService(@Nonnull final StigmaStorage stigmaStorage,
                         @Nonnull final StigmaExtractor stigmaExtractor,
                         @Nonnull final StigmaGenerator stigmaGenerator) {
        this.stigmaStorage = stigmaStorage;
        this.stigmaExtractor = stigmaExtractor;
        this.stigmaGenerator = stigmaGenerator;
    }

    @Nullable
    public StigmaDetails findStigmaDetails(@Nullable final RawStigmaToken stigmaToken) {

        if (stigmaToken != null) {
            final Stigma stigma = extractStigma(stigmaToken);

            return stigma != null
                    ? findStigmaDetails(stigma)
                    : null;
        } else {
            return null;
        }
    }

    private Stigma extractStigma(final RawStigmaToken originalToken) {
        return stigmaExtractor.extractStigma(originalToken);
    }

    @Nullable
    private StigmaDetails findStigmaDetails(@Nonnull final Stigma stigma) {
        Assert.notNull(stigma, "stigma must not be null");
        try {
            return findStigmaDetailsInStorage(stigma);
        } catch (Exception e) {
            logger.error("Could not obtain stigma details for stigma: " + stigma, e);
            return null;
        }
    }

    private StigmaDetails findStigmaDetailsInStorage(final Stigma stigma) {

        final StigmaDetails stigmaDetails = stigmaStorage.findStigmaDetails(stigma);

        if (stigmaDetails != null) {

            if (logger.isTraceEnabled()) {
                logger.trace("Found stigma details: " + stigmaDetails);
            }

            stigmaStorage.recordStigmaObservation(stigmaDetails);

        } else {

            if (logger.isWarnEnabled()) {
                logger.warn(String.format("Details for stigma '%s' not found in storage.", stigma));
            }

            stigmaStorage.recordSpottingUnknownStigma(stigma);
        }

        return stigmaDetails;
    }

    public void revokeStigma(@Nonnull final Stigma stigma) {
        Assert.notNull(stigma, "stigma must not be null");
        try {
            stigmaStorage.updateStatus(stigma, StigmaStatus.REVOKED);
        } catch (Exception e) {
            logger.error("Could not revoke stigma: " + stigma, e);
        }
    }

    @Nonnull
    public StigmaDetails getNewStigma() {

        final StigmaDetails newStigma = stigmaGenerator.newStigma();

        store(newStigma);

        return newStigma;
    }

    private void store(final StigmaDetails stigmaDetails) {
        try {
            stigmaStorage.save(stigmaDetails);
        } catch (Exception e) {
            logger.error("Could not store stigma: " + stigmaDetails, e);
        }
    }
}
