package io.nixer.nixerplugin.core.stigma.token;

import java.time.Duration;

/**
 * Created on 2019-05-27.
 *
 * @author gcwiak
 */
public abstract class StigmaTokenConstants {

    public static final String SUBJECT = "stigma-token";
    public static final String STIGMA_VALUE_FIELD_NAME = "stigma-value";

    public static final Duration DEFAULT_STIGMA_LIFETIME = Duration.ofDays(365);

    private StigmaTokenConstants() {
    }
}
