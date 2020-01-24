package io.nixer.nixerplugin.stigma.token;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;

import com.google.common.collect.Iterables;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import io.nixer.nixerplugin.stigma.crypto.DirectEncrypterFactory;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Created on 2019-05-20.
 *
 * @author gcwiak
 */
class EncryptedStigmaTokenProviderTest {

    private static final Stigma STIGMA = new Stigma("123456789000");

    private StigmaTokenProvider delegateProvider = Mockito.mock(StigmaTokenProvider.class);

    private OctetSequenceKey jwk;

    private EncryptedStigmaTokenProvider stigmaTokenProvider;

    @BeforeEach
    void setUp() throws Exception {
        JWKSet jwkSet = JWKSet.load(new File("src/test/resources/stigma-jwk.json"));
        jwk = (OctetSequenceKey) Iterables.getOnlyElement(jwkSet.getKeys());

        stigmaTokenProvider = new EncryptedStigmaTokenProvider(delegateProvider, new DirectEncrypterFactory(jwk));
    }

    @Test
    void should_create_encrypted_token() throws JOSEException {
        // given
        final JWTClaimsSet unencryptedPayload = new JWTClaimsSet.Builder()
                .subject(StigmaTokenConstants.SUBJECT)
                .issueTime(Date.from(LocalDateTime.of(2019, 5, 20, 13, 50, 15).toInstant(UTC)))
                .claim(StigmaTokenConstants.STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
                .build();

        given(delegateProvider.getToken(STIGMA)).willReturn(new PlainJWT(unencryptedPayload));

        // when
        final JWT token = stigmaTokenProvider.getToken(STIGMA);

        // then
        assertThat(token).isNotNull().isInstanceOf(EncryptedJWT.class);

        final EncryptedJWT jwe = (EncryptedJWT) token;
        assertThat(jwe.getState()).isEqualTo(JWEObject.State.ENCRYPTED);
        assertThat(jwe.getHeader().getKeyID()).isEqualTo(jwk.getKeyID());

        jwe.decrypt(new DirectDecrypter(jwk.toSecretKey()));
        assertThat(jwe.getState()).isEqualTo(JWEObject.State.DECRYPTED);
        assertThat(jwe.getPayload().toJSONObject()).isEqualTo(unencryptedPayload.toJSONObject());
    }
}
