package io.nixer.nixerplugin.stigma.token.read;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import javax.annotation.Nonnull;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;
import io.nixer.nixerplugin.stigma.crypto.DirectDecrypterFactory;
import io.nixer.nixerplugin.stigma.crypto.DirectEncrypterFactory;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.token.create.StigmaTokenFactory;
import io.nixer.nixerplugin.stigma.token.read.DecryptedToken.DecryptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.nixer.nixerplugin.stigma.StigmaConstants.STIGMA_VALUE_FIELD_NAME;
import static io.nixer.nixerplugin.stigma.StigmaConstants.SUBJECT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 10/12/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
class TokenDecrypterTest {

    private static final Stigma STIGMA = new Stigma("random-stigma-value");
    private static final Date ISSUE_TIME = Date.from(LocalDateTime.of(2019, 5, 19, 12, 45, 56).toInstant(ZoneOffset.UTC));

    private OctetSequenceKey jwk;

    private TokenDecrypter tokenDecrypter;

    @BeforeEach
    void setUp() {
        jwk = new OctetSequenceKey.Builder(new Base64URL("2zUFSf5c0m_E1MjSSiS5iwZ9lzQY-9sqQXHnU1KqW8w"))
                .algorithm(JWEAlgorithm.DIR)
                .keyID("key-id-1")
                .build();

        tokenDecrypter = new TokenDecrypter(new DirectDecrypterFactory(new ImmutableJWKSet(new JWKSet(jwk))));
    }

    @Test
    void should_decrypt_token() throws ParseException {
        // given
        final JWT stigmaToken = givenEncryptedToken(jwk);

        // when
        final DecryptedToken result = tokenDecrypter.decrypt(stigmaToken);

        // then
        assertThat(result.isValid()).isTrue();
        assertThat(result.getPayload().getClaim(STIGMA_VALUE_FIELD_NAME))
                .isEqualTo(STIGMA.getValue());
    }

    @Test
    void should_fail_decrypting_on_missing_key_id() throws ParseException {
        // given
        final StigmaTokenFactory stigmaTokenFactory = new StigmaTokenFactory(
                new DirectEncrypterFactory(jwk) {
                    @Override
                    public String getKeyId() {
                        return null;
                    }
                }
        );

        final JWT stigmaToken = JWTParser.parse(stigmaTokenFactory.getToken(STIGMA).getValue());

        // when
        final DecryptedToken result = tokenDecrypter.decrypt(stigmaToken);

        // then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getStatus()).isEqualTo(DecryptionStatus.MISSING_KEY_ID);
    }

    @Test
    void should_fail_decrypting_on_incorrect_encryption_method() throws ParseException {
        // given
        final StigmaTokenFactory stigmaTokenFactory = new StigmaTokenFactory(
                new DirectEncrypterFactory(jwk) {
                    @Nonnull
                    @Override
                    public EncryptionMethod getEncryptionMethod() {
                        return EncryptionMethod.A256GCM;
                    }
                }
        );

        final JWT stigmaToken = JWTParser.parse(stigmaTokenFactory.getToken(STIGMA).getValue());

        // when
        final DecryptedToken result = tokenDecrypter.decrypt(stigmaToken);

        // then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getStatus()).isEqualTo(DecryptionStatus.WRONG_ENC);
    }

    @Test
    void should_fail_decrypting_on_incorrect_encryption_algorithm() throws ParseException {
        // given
        tokenDecrypter = new TokenDecrypter(
                new DirectDecrypterFactory(new ImmutableJWKSet(new JWKSet(jwk))) {
                    @Nonnull
                    @Override
                    public JWEAlgorithm getAlgorithm() {
                        return JWEAlgorithm.A128KW;
                    }
                }
        );

        final JWT stigmaToken = givenEncryptedToken(jwk);

        // when
        final DecryptedToken result = tokenDecrypter.decrypt(stigmaToken);

        // then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getStatus()).isEqualTo(DecryptionStatus.WRONG_ALG);
    }

    @Test
    void should_fail_decrypting_on_not_encrypted_token() {
        // given
        final PlainJWT plainJWT = new PlainJWT(new JWTClaimsSet.Builder()
                .subject(SUBJECT)
                .issueTime(ISSUE_TIME)
                .claim(STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
                .build());

        // when
        final DecryptedToken result = tokenDecrypter.decrypt(plainJWT);

        // then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getStatus()).isEqualTo(DecryptionStatus.NOT_ENCRYPTED);
    }

    @Test
    void should_fail_decrypting_on_decryption_operation_error() throws JOSEException, ParseException {
        // given
        final OctetSequenceKey anotherKeyWithMatchingID = new OctetSequenceKeyGenerator(256).keyID(jwk.getKeyID()).generate();

        final JWT stigmaToken = givenEncryptedToken(anotherKeyWithMatchingID);

        // when
        final DecryptedToken result = tokenDecrypter.decrypt(stigmaToken);

        // then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getStatus()).isEqualTo(DecryptionStatus.DECRYPTION_ERROR);
    }

    private JWT givenEncryptedToken(final OctetSequenceKey encryptionKey) throws ParseException {
        final StigmaTokenFactory stigmaTokenFactory = new StigmaTokenFactory(new DirectEncrypterFactory(encryptionKey));

        return JWTParser.parse(stigmaTokenFactory.getToken(STIGMA).getValue());
    }
}
