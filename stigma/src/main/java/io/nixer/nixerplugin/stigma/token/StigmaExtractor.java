package io.nixer.nixerplugin.stigma.token;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.token.validation.StigmaTokenValidator;
import io.nixer.nixerplugin.stigma.token.validation.ValidationResult;
import org.springframework.util.Assert;

/**
 * Created on 19/01/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmaExtractor {

    @Nonnull
    private final StigmaTokenValidator stigmaTokenValidator;

    public StigmaExtractor(@Nonnull final StigmaTokenValidator stigmaTokenValidator) {
        Assert.notNull(stigmaTokenValidator, "stigmaTokenValidator must not be null");
        this.stigmaTokenValidator = stigmaTokenValidator;
    }

    @Nullable
    public Stigma extractStigma(@Nonnull final RawStigmaToken stigmaToken) {
        Assert.notNull(stigmaToken, "stigmaToken must not be null");

        final ValidationResult validationResult = stigmaTokenValidator.validate(stigmaToken);

        return validationResult.isValid()
                ? validationResult.getStigma()
                : null;
    }
}
