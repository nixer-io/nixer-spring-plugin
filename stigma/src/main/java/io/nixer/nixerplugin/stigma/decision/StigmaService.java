package io.nixer.nixerplugin.stigma.decision;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import io.nixer.nixerplugin.stigma.storage.StigmaStorage;
import io.nixer.nixerplugin.stigma.generate.StigmaGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
public class StigmaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StigmaService.class);

    @Nonnull
    private final StigmaStorage stigmaStorage;

    @Nonnull
    private final StigmaGenerator stigmaGenerator;

    public StigmaService(@Nonnull final StigmaStorage stigmaStorage,
                         @Nonnull final StigmaGenerator stigmaGenerator) {
        this.stigmaStorage = Preconditions.checkNotNull(stigmaStorage, "stigmaStorage");
        this.stigmaGenerator = Preconditions.checkNotNull(stigmaGenerator, "stigmaGenerator");
    }

    @Nullable
    public StigmaDetails findStigmaDetails(@Nonnull final Stigma stigma) {
        Assert.notNull(stigma, "stigma must not be null");
        try {
            return findStigmaDetailsInStorage(stigma);
        } catch (Exception e) {
            LOGGER.error("Could not obtain stigma details for stigma: '{}'", stigma, e);
            return null;
        }
    }

    private StigmaDetails findStigmaDetailsInStorage(final Stigma stigma) {

        final StigmaDetails stigmaDetails = stigmaStorage.findStigmaDetails(stigma);

        if (stigmaDetails != null) {
            stigmaStorage.recordStigmaObservation(stigmaDetails);
        } else {
            stigmaStorage.recordSpottingUnknownStigma(stigma);
        }

        return stigmaDetails;
    }

    public void revokeStigma(@Nonnull final Stigma stigma) {
        Assert.notNull(stigma, "stigma must not be null");
        try {
            stigmaStorage.updateStatus(stigma, StigmaStatus.REVOKED);
        } catch (Exception e) {
            LOGGER.error("Could not revoke stigma: '{}'", stigma, e);
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
            LOGGER.error("Could not store stigma: '{}'", stigmaDetails, e);
        }
    }
}
