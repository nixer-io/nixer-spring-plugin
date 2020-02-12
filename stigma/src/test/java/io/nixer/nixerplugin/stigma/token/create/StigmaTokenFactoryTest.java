package io.nixer.nixerplugin.stigma.token.create;

import java.text.ParseException;
import java.util.Map;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import io.nixer.nixerplugin.stigma.StigmaConstants;
import io.nixer.nixerplugin.stigma.crypto.DirectEncrypterFactory;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
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
    void setUp() {
        jwk = new OctetSequenceKey.Builder(new Base64URL("2zUFSf5c0m_E1MjSSiS5iwZ9lzQY-9sqQXHnU1KqW8w"))
                .algorithm(JWEAlgorithm.DIR)
                .keyID("key-id-1")
                .build();

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
