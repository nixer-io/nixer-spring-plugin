package io.nixer.nixerplugin.stigma.token;

import java.text.ParseException;
import java.util.Map;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.PlainJWT;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Created on 2019-05-20.
 *
 * @author gcwiak
 */
class PlainStigmaTokenProviderTest {

    private static final Stigma STIGMA = new Stigma("1234567890");

    private PlainStigmaTokenProvider stigmaTokenProvider = new PlainStigmaTokenProvider();

    @Test
    void should_generate_stigma_token_as_plain_unsigned_JWT() throws ParseException {
        // given
        // when
        final JWT token = stigmaTokenProvider.getToken(STIGMA);

        // then
        assertThat(token).isNotNull().isInstanceOf(PlainJWT.class);

        final Map<String, Object> claims = token.getJWTClaimsSet().getClaims();
        assertThat(claims).contains(
                entry("sub", StigmaTokenConstants.SUBJECT),
                entry(StigmaTokenConstants.STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
        );

        assertThat(token.getHeader().getAlgorithm()).isEqualTo(Algorithm.NONE);
    }
}
