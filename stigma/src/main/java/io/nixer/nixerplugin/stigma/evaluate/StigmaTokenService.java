package io.nixer.nixerplugin.stigma.evaluate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.storage.StigmaData;
import io.nixer.nixerplugin.stigma.storage.StigmaTokenStorage;
import io.nixer.nixerplugin.stigma.token.StigmaValuesGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
public class StigmaTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StigmaTokenService.class);

    @Nonnull
    private final StigmaTokenStorage stigmaTokenStorage;

    @Nonnull
    private final StigmaValuesGenerator stigmaValuesGenerator;

    public StigmaTokenService(@Nonnull final StigmaTokenStorage stigmaTokenStorage,
                              @Nonnull final StigmaValuesGenerator stigmaValuesGenerator) {
        this.stigmaTokenStorage = Preconditions.checkNotNull(stigmaTokenStorage, "stigmaTokenStorage");
        this.stigmaValuesGenerator = Preconditions.checkNotNull(stigmaValuesGenerator, "stigmaValuesGenerator");
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

        final StigmaData stigmaValueData = stigmaTokenStorage.findStigmaData(stigma);

        if (stigmaValueData != null) {
            stigmaTokenStorage.recordStigmaObservation(stigmaValueData);
        } else {
            stigmaTokenStorage.recordSpottingUnknownStigma(stigma);
        }

        return stigmaValueData;
    }

    public void revokeStigma(@Nonnull final Stigma stigma) {
        Assert.notNull(stigma, "stigma must not be null");
        try {
            stigmaTokenStorage.updateStatus(stigma, StigmaStatus.REVOKED);
        } catch (Exception e) {
            LOGGER.error("Could not revoke stigma: '{}'", stigma, e);
        }
    }

    @Nonnull
    public StigmaData getNewStigma() {

        final StigmaData newStigma = stigmaValuesGenerator.newStigma();

        storeStigma(newStigma);

        return newStigma;
    }

    private void storeStigma(final StigmaData stigmaData) {
        try {
            stigmaTokenStorage.saveStigma(stigmaData);
        } catch (Exception e) {
            LOGGER.error("Could not store stigma for stigma value: '{}'", stigmaData, e);
        }
    }
}
