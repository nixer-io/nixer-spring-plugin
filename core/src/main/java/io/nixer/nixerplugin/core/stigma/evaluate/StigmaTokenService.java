package io.nixer.nixerplugin.core.stigma.evaluate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.nimbusds.jwt.JWT;
import io.nixer.nixerplugin.core.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.core.stigma.domain.Stigma;
import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.core.stigma.storage.StigmaData;
import io.nixer.nixerplugin.core.stigma.storage.StigmaTokenStorage;
import io.nixer.nixerplugin.core.stigma.token.StigmaTokenProvider;
import io.nixer.nixerplugin.core.stigma.token.StigmaValuesGenerator;
import io.nixer.nixerplugin.core.stigma.token.validation.StigmaTokenValidator;
import io.nixer.nixerplugin.core.stigma.token.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
public class StigmaTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StigmaTokenService.class);

    @Nonnull
    private final StigmaTokenProvider stigmaTokenProvider;

    @Nonnull
    private final StigmaTokenStorage stigmaTokenStorage;

    @Nonnull
    private final StigmaValuesGenerator stigmaValuesGenerator;

    @Nonnull
    private final StigmaTokenValidator stigmaTokenValidator;

    public StigmaTokenService(@Nonnull final StigmaTokenProvider stigmaTokenProvider,
                              @Nonnull final StigmaTokenStorage stigmaTokenStorage,
                              @Nonnull final StigmaValuesGenerator stigmaValuesGenerator,
                              @Nonnull final StigmaTokenValidator stigmaTokenValidator) {
        this.stigmaTokenProvider = Preconditions.checkNotNull(stigmaTokenProvider, "stigmaTokenProvider");
        this.stigmaTokenStorage = Preconditions.checkNotNull(stigmaTokenStorage, "stigmaTokenStorage");
        this.stigmaValuesGenerator = Preconditions.checkNotNull(stigmaValuesGenerator, "stigmaValuesGenerator");
        this.stigmaTokenValidator = Preconditions.checkNotNull(stigmaTokenValidator, "stigmaTokenValidator");
    }


    @Nullable
    public StigmaData findStigmaData(@Nullable final RawStigmaToken originalToken) {

        return tryObtainingStigmaData(originalToken);
    }


    @Nullable
    private StigmaData tryObtainingStigmaData(@Nullable final RawStigmaToken originalToken) {

        try {
            return obtainStigmaData(originalToken);

        } catch (Exception e) {
            LOGGER.error("Could not obtain stigma data for raw token: '{}'", originalToken, e);
            return null;
        }
    }

    @Nullable
    private StigmaData obtainStigmaData(@Nullable final RawStigmaToken originalToken) {

        final ValidationResult tokenValidationResult = stigmaTokenValidator.validate(originalToken);

        if (tokenValidationResult.isValid() || tokenValidationResult.isReadable()) {

            return findStigmaDataInStorage(tokenValidationResult.getStigma());

        } else {

            if (hasAnyValue(originalToken)) {
                // TODO record validation result as well
                stigmaTokenStorage.recordUnreadableToken(originalToken);
            } // TODO record missing token????

            return null;
        }
    }

    private boolean hasAnyValue(final RawStigmaToken originalToken) {
        return originalToken != null && StringUtils.hasText(originalToken.getValue());
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
        try {
            stigmaTokenStorage.updateStatus(stigma, StigmaStatus.REVOKED);
        } catch (Exception e) {
            LOGGER.error("Could not revoke stigma: '{}'", stigma, e);
        }
    }

    @Nonnull
    public RawStigmaToken newStigmaToken() {

        final Stigma newStigma = stigmaValuesGenerator.newStigma();

        storeActiveStigma(newStigma);

        final JWT token = stigmaTokenProvider.getToken(newStigma);

        return new RawStigmaToken(token.serialize());
    }

    private void storeActiveStigma(final Stigma newStigma) {
        try {
            stigmaTokenStorage.saveStigma(newStigma, StigmaStatus.ACTIVE);
        } catch (Exception e) {
            LOGGER.error("Could not store active stigma for stigma value: '{}'", newStigma, e);
        }
    }
}
