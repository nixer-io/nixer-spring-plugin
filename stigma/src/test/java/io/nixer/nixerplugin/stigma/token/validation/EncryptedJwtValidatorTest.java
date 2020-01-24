package io.nixer.nixerplugin.stigma.token.validation;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

import com.google.common.collect.Iterables;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import io.nixer.nixerplugin.stigma.crypto.DirectDecrypterFactory;
import io.nixer.nixerplugin.stigma.crypto.DirectEncrypterFactory;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.token.EncryptedStigmaTokenProvider;
import io.nixer.nixerplugin.stigma.token.StigmaTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.nixer.nixerplugin.stigma.token.StigmaTokenConstants.STIGMA_VALUE_FIELD_NAME;
import static io.nixer.nixerplugin.stigma.token.StigmaTokenConstants.SUBJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Created on 10/12/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
@ExtendWith(MockitoExtension.class)
class EncryptedJwtValidatorTest {

    private static final Stigma STIGMA = new Stigma("random-stigma-value");
    private static final Date ISSUE_TIME = Date.from(LocalDateTime.of(2019, 5, 19, 12, 45, 56).toInstant(ZoneOffset.UTC));

    @Mock
    private JwtValidator delegateValidator;

    private OctetSequenceKey jwk;

    private EncryptedJwtValidator validator;

    @BeforeEach
    void setUp() throws IOException, ParseException {
        JWKSet jwkSet = JWKSet.load(new File("src/test/resources/stigma-jwk.json"));
        jwk = (OctetSequenceKey) Iterables.getOnlyElement(jwkSet.getKeys());

        validator = new EncryptedJwtValidator(
                new DirectDecrypterFactory(new ImmutableJWKSet(jwkSet)),
                delegateValidator
        );
    }

    @Test
    void should_pass_validation() {
        // given
        final JWT stigmaToken = givenEncryptedToken(
                with -> with.subject(SUBJECT)
                        .issueTime(ISSUE_TIME)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
        );
        given(delegateValidator.validate(any(JWT.class))).willReturn(ValidationResult.valid(STIGMA));

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result).isEqualTo(ValidationResult.valid(STIGMA));
    }

    @Test
    void should_fail_validation_on_incorrect_encryption_method() {
        // given
        final PlainJWT plainJWT = new PlainJWT(new JWTClaimsSet.Builder()
                .subject(SUBJECT)
                .issueTime(ISSUE_TIME)
                .claim(STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
                .build());
        final StigmaTokenProvider plainTokenProvider = Mockito.mock(StigmaTokenProvider.class);
        given(plainTokenProvider.getToken(STIGMA)).willReturn(plainJWT);

        final EncryptedStigmaTokenProvider encryptedTokenProvider = new EncryptedStigmaTokenProvider(
                plainTokenProvider,
                new DirectEncrypterFactory(this.jwk) {
                    @Nonnull
                    @Override
                    public EncryptionMethod getEncryptionMethod() {
                        return EncryptionMethod.A256GCM;
                    }
                }
        );

        final JWT stigmaToken = encryptedTokenProvider.getToken(STIGMA);

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.WRONG_ENC);
    }

    @Test
    void should_fail_validation_on_incorrect_encryption_algorithm() throws IOException, ParseException {
        // given
        JWKSet jwkSet = JWKSet.load(new File("src/test/resources/stigma-jwk.json"));
        jwk = (OctetSequenceKey) Iterables.getOnlyElement(jwkSet.getKeys());

        final JwtValidator validator = new EncryptedJwtValidator( // TODO try reusing the instance field
                new DirectDecrypterFactory(new ImmutableJWKSet(jwkSet)) {
                    @Nonnull
                    @Override
                    public JWEAlgorithm getAlgorithm() {
                        return JWEAlgorithm.A128KW;
                    }
                },
                delegateValidator
        );

        final JWT stigmaToken = givenEncryptedToken(
                with -> with.subject(SUBJECT)
                        .issueTime(ISSUE_TIME)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
        );

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.WRONG_ALG);
    }

    @Test
    void should_fail_validation_on_not_encrypted_token() {
        // given
        final PlainJWT plainJWT = new PlainJWT(new JWTClaimsSet.Builder()
                .subject(SUBJECT)
                .issueTime(ISSUE_TIME)
                .claim(STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
                .build());

        // when
        final ValidationResult result = validator.validate(plainJWT);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.NOT_ENCRYPTED);
    }

    @Test
    void should_fail_validation_on_decryption_error() throws JOSEException {
        // given
        final OctetSequenceKey anotherKeyWithMatchingID = new OctetSequenceKeyGenerator(256).keyID(jwk.getKeyID()).generate();

        final JWT stigmaToken = givenEncryptedToken(anotherKeyWithMatchingID,
                with -> with.subject(SUBJECT)
                        .issueTime(ISSUE_TIME)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
        );

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.DECRYPTION_ERROR);
    }

    private JWT givenEncryptedToken(final OctetSequenceKey encryptionKey, final Consumer<JWTClaimsSet.Builder> claimsAssigner) {
        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        claimsAssigner.accept(builder);
        final PlainJWT plainJWT = new PlainJWT(builder.build());

        final StigmaTokenProvider plainTokenProvider = Mockito.mock(StigmaTokenProvider.class);
        given(plainTokenProvider.getToken(STIGMA)).willReturn(plainJWT);

        final EncryptedStigmaTokenProvider encryptedTokenProvider =
                new EncryptedStigmaTokenProvider(plainTokenProvider, new DirectEncrypterFactory(encryptionKey));

        return encryptedTokenProvider.getToken(STIGMA);
    }

    private JWT givenEncryptedToken(final Consumer<JWTClaimsSet.Builder> claimsAssigner) {
        return givenEncryptedToken(this.jwk, claimsAssigner);
    }
}
