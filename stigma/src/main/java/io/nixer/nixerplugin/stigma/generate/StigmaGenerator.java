package io.nixer.nixerplugin.stigma.generate;

import java.security.SecureRandom;
import javax.annotation.Nonnull;

import io.nixer.nixerplugin.core.util.NowSource;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.storage.StigmaData;
import io.nixer.nixerplugin.stigma.token.create.StigmaTokenFactory;

/**
 * Generates values of Stigma to be used as content of Stigma Tokens.
 * <br>
 * See {@link RawStigmaToken} and {@link StigmaTokenFactory}.
 *
 * Created on 06/12/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmaGenerator {

    private final SecureRandom random;

    private final NowSource nowSource;

    public StigmaGenerator(final NowSource nowSource) {
        this(new SecureRandom(), nowSource);
    }

    public StigmaGenerator(final SecureRandom random, final NowSource nowSource) {
        this.random = random;
        this.nowSource = nowSource;
    }

    /**
     * Creates a fresh Stigma to be used in Stigma Token.
     */
    @Nonnull
    public StigmaData newStigma() {
        final String stigmaValue = String.valueOf(random.nextLong());

        return new StigmaData(
                new Stigma(stigmaValue),
                StigmaStatus.ACTIVE,
                nowSource.now()
        );
    }
}
