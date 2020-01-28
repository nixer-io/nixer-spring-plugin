package io.nixer.nixerplugin.stigma.evaluate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.storage.StigmaData;
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
    public StigmaData findStigmaData(@Nonnull final Stigma stigma) {
        Assert.notNull(stigma, "stigma must not be null");
        try {
            return findStigmaDataInStorage(stigma);
        } catch (Exception e) {
            LOGGER.error("Could not obtain stigma data for stigma: '{}'", stigma, e);
            return null;
        }
    }

    private StigmaData findStigmaDataInStorage(final Stigma stigma) {

        final StigmaData stigmaValueData = stigmaStorage.findStigmaData(stigma);

        if (stigmaValueData != null) {
            stigmaStorage.recordStigmaObservation(stigmaValueData);
        } else {
            stigmaStorage.recordSpottingUnknownStigma(stigma);
        }

        return stigmaValueData;
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
    public StigmaData getNewStigma() {

        final StigmaData newStigma = stigmaGenerator.newStigma();

        storeStigma(newStigma);

        return newStigma;
    }

    private void storeStigma(final StigmaData stigmaData) {
        try {
            stigmaStorage.saveStigma(stigmaData);
        } catch (Exception e) {
            LOGGER.error("Could not store stigma: '{}'", stigmaData, e);
        }
    }
}
