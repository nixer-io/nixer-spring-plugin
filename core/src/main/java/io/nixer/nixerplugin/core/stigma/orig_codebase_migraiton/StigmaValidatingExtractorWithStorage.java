package io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import io.nixer.nixerplugin.core.stigma.token.validation.StigmaTokenValidator;
import io.nixer.nixerplugin.core.stigma.token.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created on 2019-06-28.
 *
 * @author gcwiak
 */
public class StigmaValidatingExtractorWithStorage implements StigmaExtractor {

    // TODO split this class

    private static final Logger LOGGER = LoggerFactory.getLogger(StigmaValidatingExtractorWithStorage.class);

    @Nonnull
    private final StigmaTokenValidator stigmaTokenValidator;

    @Nonnull
    private final StigmaTokenStorage stigmaTokenStorage;

    public StigmaValidatingExtractorWithStorage(@Nonnull final StigmaTokenValidator stigmaTokenValidator,
                                                @Nonnull final StigmaTokenStorage stigmaTokenStorage) {
        this.stigmaTokenValidator = Preconditions.checkNotNull(stigmaTokenValidator, "stigmaTokenValidator");
        this.stigmaTokenStorage = Preconditions.checkNotNull(stigmaTokenStorage, "stigmaTokenStorage");
    }

    @Override
    @Nullable
    public StigmaData tryExtractingStigma(@Nullable final String rawToken) {

        // TODO split validating and extracting/fetching stigma

        final ValidationResult tokenValidationResult = stigmaTokenValidator.validate(rawToken);

        try {
            return tryExtractingStigma(rawToken, tokenValidationResult);
        } catch (Exception e) {
            LOGGER.error("Could not extract stigma from raw token: '{}'", rawToken, e);
            return null;
        }
    }

    @Nullable
    private StigmaData tryExtractingStigma(@Nullable final String rawToken, final ValidationResult tokenValidationResult) {

        if (tokenValidationResult.isValid() || tokenValidationResult.isReadable()) {

            final Stigma stigma = new Stigma(tokenValidationResult.getStigmaValue());

            final StigmaData stigmaValueData = stigmaTokenStorage.findStigmaValueData(stigma);

            if (stigmaValueData != null) {
                stigmaTokenStorage.recordStigmaObservation(stigmaValueData);
            } else {
                stigmaTokenStorage.recordSpottingUnknownStigma(stigma);
            }

            return stigmaValueData;

        } else {

            if (StringUtils.hasText(rawToken)) {
                // TODO record validation result as well
                stigmaTokenStorage.recordUnreadableToken(rawToken);
            } // TODO record missing token????

            return null;
        }
    }
}
