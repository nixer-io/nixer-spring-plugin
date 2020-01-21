package io.nixer.nixerplugin.core.stigma.token;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.function.Supplier;

import io.nixer.nixerplugin.core.stigma.domain.Stigma;
import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.core.stigma.storage.StigmaData;

/**
 * Created on 06/12/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmaValuesGenerator {

    private final SecureRandom random;

    private final Supplier<Instant> nowSource;

    public StigmaValuesGenerator(final Supplier<Instant> nowSource) {
        this(new SecureRandom(), nowSource);
    }

    public StigmaValuesGenerator(final SecureRandom random, final Supplier<Instant> nowSource) {
        this.random = random;
        this.nowSource = nowSource;
    }

    public StigmaData newStigma() {
        final String stigmaValue = String.valueOf(random.nextLong());

        return new StigmaData(
                new Stigma(stigmaValue),
                StigmaStatus.ACTIVE,
                nowSource.get()
        );
    }
}
