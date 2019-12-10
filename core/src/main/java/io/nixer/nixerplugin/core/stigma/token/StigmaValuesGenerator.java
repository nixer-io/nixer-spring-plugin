package io.nixer.nixerplugin.core.stigma.token;

import java.security.SecureRandom;

import io.nixer.nixerplugin.core.stigma.domain.Stigma;

/**
 * Created on 06/12/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmaValuesGenerator {

    private final SecureRandom random;

    public StigmaValuesGenerator() {
        this.random = new SecureRandom();
    }

    public StigmaValuesGenerator(final SecureRandom random) {
        this.random = random;
    }

    public Stigma newStigma() {
        final String stigmaValue = String.valueOf(random.nextLong());

        return new Stigma(stigmaValue);
    }
}
