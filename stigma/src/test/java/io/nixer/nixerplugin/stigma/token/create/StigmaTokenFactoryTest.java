package io.nixer.nixerplugin.stigma.token.create;

import java.io.File;
import java.text.ParseException;
import java.util.Map;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import io.nixer.nixerplugin.stigma.crypto.DirectEncrypterFactory;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.StigmaConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Created on 2019-05-20.
 *
 * @author gcwiak
 */
class StigmaTokenFactoryTest {

    private static final Stigma STIGMA = new Stigma("123456789000");

    private OctetSequenceKey jwk;

    private StigmaTokenFactory stigmaTokenFactory;

    @BeforeEach
    void setUp() throws Exception {
        JWKSet jwkSet = JWKSet.load(new File("src/test/resources/stigma-jwk.json"));
        assertThat(jwkSet.getKeys()).hasSize(1);

        jwk = (OctetSequenceKey) jwkSet.getKeys().get(0);

        stigmaTokenFactory = new StigmaTokenFactory(new DirectEncrypterFactory(jwk));
    }

    @Test
    void should_create_encrypted_token() throws JOSEException, ParseException {
        // when
        final RawStigmaToken rawStigmaToken = stigmaTokenFactory.getToken(STIGMA);

        // then
        final JWT token = JWTParser.parse(rawStigmaToken.getValue());
        assertThat(token).isNotNull().isInstanceOf(EncryptedJWT.class);

        final EncryptedJWT jwe = (EncryptedJWT) token;
        assertThat(jwe.getState()).isEqualTo(JWEObject.State.ENCRYPTED);
        assertThat(jwe.getHeader().getKeyID()).isEqualTo(jwk.getKeyID());

        jwe.decrypt(new DirectDecrypter(jwk.toSecretKey()));
        assertThat(jwe.getState()).isEqualTo(JWEObject.State.DECRYPTED);

        final Map<String, Object> claims = token.getJWTClaimsSet().getClaims();
        assertThat(claims).contains(
                entry("sub", StigmaConstants.SUBJECT),
                entry(StigmaConstants.STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
        );
    }
}
