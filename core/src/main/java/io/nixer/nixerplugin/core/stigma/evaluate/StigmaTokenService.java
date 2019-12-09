package io.nixer.nixerplugin.core.stigma.evaluate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import io.nixer.nixerplugin.core.stigma.storage.StigmaData;
import io.nixer.nixerplugin.core.stigma.storage.StigmaStatus;
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

    /**
     * To be called after successful login attempt.
     * Consumes the currently used raw stigma token (might be null or empty) and returns a new token for further usage
     * with information about validity of the original token.
     */
    @Nonnull
    public StigmaTokenFetchResult fetchTokenOnLoginSuccess(@Nullable final String originalRawToken) {

        @Nullable final StigmaData stigmaData = tryObtainingStigma(originalRawToken);

        if (isStigmaActive(stigmaData)) {
            // TODO do we need this recording?
            // covered by observed: active->active
            // stigmaTokenStorage.recordLoginSuccessTokenValid(stigmaData.getStigmaValue());

            return new StigmaTokenFetchResult(originalRawToken, true);

        } else {
            // TODO do we need this recording?
            // covered by observed event or recording incoming_unreadable_token
            // stigmaTokenStorage.recordLoginSuccessTokenInvalid(originalRawToken, stigmaValueData.toString());

            return new StigmaTokenFetchResult(newStigmaToken(), false);
        }
    }

    /**
     * To be called after failed login attempt.
     * Consumes the currently used raw stigma token (might be null or empty) and returns a new token for further usage
     * with information about validity of the original token.
     */
    @Nonnull
    public StigmaTokenFetchResult fetchTokenOnLoginFail(@Nullable final String originalRawToken) {

        @Nullable final StigmaData stigmaData = tryObtainingStigma(originalRawToken);

        if (isStigmaActive(stigmaData)) {

            revokeStigma(stigmaData);

            return new StigmaTokenFetchResult(newStigmaToken(), true);

        } else {
            // TODO do we need this recording?
            // covered by observe event or recording incoming_unreadable_token
            // stigmaTokenStorage.recordLoginFailTokenInvalid(originalRawToken, stigmaTokenCheckResult.toString());

            return new StigmaTokenFetchResult(newStigmaToken(), false);
        }
    }

    @Nullable
    private StigmaData tryObtainingStigma(@Nullable final String rawToken) {

        try {
            return obtainStigma(rawToken);

        } catch (Exception e) {
            LOGGER.error("Could not extract stigma from raw token: '{}'", rawToken, e);
            return null;
        }
    }

    @Nullable
    private StigmaData obtainStigma(@Nullable final String rawToken) {

        final ValidationResult tokenValidationResult = stigmaTokenValidator.validate(rawToken);

        if (tokenValidationResult.isValid() || tokenValidationResult.isReadable()) {

            return lookForStigmaInStorage(tokenValidationResult.getStigmaValue());

        } else {

            if (StringUtils.hasText(rawToken)) {
                // TODO record validation result as well
                stigmaTokenStorage.recordUnreadableToken(rawToken);
            } // TODO record missing token????

            return null;
        }
    }

    private StigmaData lookForStigmaInStorage(final String stigmaValue) {

        final Stigma stigma = new Stigma(stigmaValue);

        final StigmaData stigmaValueData = stigmaTokenStorage.findStigmaData(stigma);

        if (stigmaValueData != null) {
            stigmaTokenStorage.recordStigmaObservation(stigmaValueData);
        } else {
            stigmaTokenStorage.recordSpottingUnknownStigma(stigma);
        }

        return stigmaValueData;
    }

    private boolean isStigmaActive(@Nullable final StigmaData stigmaData) {
        return stigmaData != null
                && stigmaData.getStatus() == StigmaStatus.ACTIVE;
    }

    private void revokeStigma(final StigmaData stigmaData) {
        try {
            stigmaTokenStorage.revokeStigma(stigmaData.getStigmaValue());
        } catch (Exception e) {
            LOGGER.error("Could not revoke stigma for stigma value data: '{}'", stigmaData, e);
        }
    }

    @Nonnull
    private String newStigmaToken() {

        final String newStigmaValue = stigmaValuesGenerator.newStigma();

        final String stigmaValue = stigmaTokenStorage.createStigma(newStigmaValue, StigmaStatus.ACTIVE).getValue();

        return stigmaTokenProvider.getToken(stigmaValue).serialize();
    }
}
