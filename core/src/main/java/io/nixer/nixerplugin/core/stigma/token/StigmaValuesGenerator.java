package io.nixer.nixerplugin.core.stigma.token;

import java.security.SecureRandom;

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

    public String newStigma() {
        return String.valueOf(random.nextLong());
    }
}
