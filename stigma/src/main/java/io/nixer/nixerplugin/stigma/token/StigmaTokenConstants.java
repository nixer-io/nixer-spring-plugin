package io.nixer.nixerplugin.stigma.token;

import java.time.Duration;

/**
 * Created on 2019-05-27.
 *
 * @author gcwiak
 */
public abstract class StigmaTokenConstants {

    public static final String SUBJECT = "stigma-token";
    public static final String STIGMA_VALUE_FIELD_NAME = "stigma-value";

    public static final Duration DEFAULT_TOKEN_LIFETIME = Duration.ofDays(365); // TODO consider using Period instead of Duration

    private StigmaTokenConstants() {
    }
}
