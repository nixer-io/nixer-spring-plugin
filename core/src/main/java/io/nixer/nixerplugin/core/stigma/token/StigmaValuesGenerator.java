package io.nixer.nixerplugin.core.stigma.token;

import java.security.SecureRandom;

import io.nixer.nixerplugin.core.stigma.domain.Stigma;
import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.core.stigma.storage.StigmaData;
import io.nixer.nixerplugin.core.util.NowSource;

/**
 * Created on 06/12/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmaValuesGenerator {

    private final SecureRandom random;

    private final NowSource nowSource;

    public StigmaValuesGenerator(final NowSource nowSource) {
        this(new SecureRandom(), nowSource);
    }

    public StigmaValuesGenerator(final SecureRandom random, final NowSource nowSource) {
        this.random = random;
        this.nowSource = nowSource;
    }

    public StigmaData newStigma() {
        final String stigmaValue = String.valueOf(random.nextLong());

        return new StigmaData(
                new Stigma(stigmaValue),
                StigmaStatus.ACTIVE,
                nowSource.now()
        );
    }
}
