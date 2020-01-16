package io.nixer.nixerplugin.stigma.token;

import java.time.Instant;
import java.util.Date;
import java.util.function.Supplier;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import io.nixer.nixerplugin.stigma.domain.Stigma;

/**
 * Creates StigmaToken as a plain JSON Web Token (JWT).
 * The token is not signed and not encrypted.
 *
 * Created on 2019-05-20.
 *
 * @author gcwiak
 */
public class PlainStigmaTokenProvider implements StigmaTokenProvider {

    private final Supplier<Instant> nowSource;

    public PlainStigmaTokenProvider(final Supplier<Instant> nowSource) {
        this.nowSource = nowSource;
    }

    @Override
    public JWT getToken(final Stigma stigma) {

        final JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(StigmaTokenConstants.SUBJECT)
                .issueTime(Date.from(nowSource.get()))
                .claim(StigmaTokenConstants.STIGMA_VALUE_FIELD_NAME, stigma.getValue())
                .build();

        return new PlainJWT(claimsSet);
    }
}
