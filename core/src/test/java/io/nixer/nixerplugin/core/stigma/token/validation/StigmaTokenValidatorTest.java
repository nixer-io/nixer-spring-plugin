package io.nixer.nixerplugin.core.stigma.token.validation;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

import com.google.common.collect.Iterables;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import io.nixer.nixerplugin.core.stigma.crypto.DirectDecrypterFactory;
import io.nixer.nixerplugin.core.stigma.crypto.DirectEncrypterFactory;
import io.nixer.nixerplugin.core.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.core.stigma.token.EncryptedStigmaTokenProvider;
import io.nixer.nixerplugin.core.stigma.token.StigmaTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import static io.nixer.nixerplugin.core.stigma.token.StigmaTokenConstants.STIGMA_VALUE_FIELD_NAME;
import static io.nixer.nixerplugin.core.stigma.token.StigmaTokenConstants.SUBJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Created on 2019-05-22.
 *
 * @author gcwiak
 */
class StigmaTokenValidatorTest {

    private static final Date ISSUE_TIME = Date.from(LocalDateTime.of(2019, 5, 19, 12, 45, 56).toInstant(ZoneOffset.UTC));
    private static final Duration TOKEN_LIFETIME = Duration.ofDays(30);
    private static final Instant NOW = LocalDateTime.of(2019, 5, 20, 13, 50, 15).toInstant(ZoneOffset.UTC);
    private static final String STIGMA_VALUE = "random-stigma-value";

    private OctetSequenceKey jwk;

    private StigmaTokenValidator validator;

    /**
     * FIXME This test class covers EncryptedJwtValidator and StigmaTokenPayloadValidator as well and should be split.
     */

    @BeforeEach
    void setUp() throws Exception {
        JWKSet jwkSet = JWKSet.load(new File("src/test/resources/stigma-jwk.json"));
        jwk = (OctetSequenceKey) Iterables.getOnlyElement(jwkSet.getKeys());

        final JwtValidator jwtValidator = new EncryptedJwtValidator(
                new DirectDecrypterFactory(new ImmutableJWKSet(jwkSet)),
                new StigmaTokenPayloadValidator(() -> NOW, TOKEN_LIFETIME)
        );

        validator = new StigmaTokenValidator(jwtValidator);
    }

    @ParameterizedTest
    @MethodSource("missingTokenExamples")
    void should_fail_validation_on_missing_token(final RawStigmaToken missingToken) {
        // when
        final ValidationResult result = validator.validate(missingToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.MISSING);
    }

    static Stream<RawStigmaToken> missingTokenExamples() {
        return Stream.of(
                null,
                new RawStigmaToken(""),
                new RawStigmaToken(" "),
                new RawStigmaToken("   ")
        );
    }

    @Test
    void should_pass_validation() {
        // given
        final RawStigmaToken stigmaToken = givenEncryptedToken(
                with -> with.subject(SUBJECT)
                        .issueTime(ISSUE_TIME)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA_VALUE)
        );

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result).isEqualTo(ValidationResult.valid(STIGMA_VALUE));
    }

    @Test
    void should_fail_validation_on_incorrect_encryption_method() {
        // given
        final PlainJWT plainJWT = new PlainJWT(new JWTClaimsSet.Builder()
                .subject(SUBJECT)
                .issueTime(ISSUE_TIME)
                .claim(STIGMA_VALUE_FIELD_NAME, STIGMA_VALUE)
                .build());
        final StigmaTokenProvider plainTokenProvider = Mockito.mock(StigmaTokenProvider.class);
        given(plainTokenProvider.getToken(STIGMA_VALUE)).willReturn(plainJWT);

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

        final RawStigmaToken stigmaToken = new RawStigmaToken(encryptedTokenProvider.getToken(STIGMA_VALUE).serialize());

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

        final JwtValidator jwtValidator = new EncryptedJwtValidator(
                new DirectDecrypterFactory(new ImmutableJWKSet(jwkSet)) {
                    @Nonnull
                    @Override
                    public JWEAlgorithm getAlgorithm() {
                        return JWEAlgorithm.A128KW;
                    }
                },
                new StigmaTokenPayloadValidator(() -> NOW, TOKEN_LIFETIME)
        );

        validator = new StigmaTokenValidator(jwtValidator);

        final RawStigmaToken stigmaToken = givenEncryptedToken(
                with -> with.subject(SUBJECT)
                        .issueTime(ISSUE_TIME)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA_VALUE)
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
                .claim(STIGMA_VALUE_FIELD_NAME, STIGMA_VALUE)
                .build());

        // when
        final ValidationResult result = validator.validate(new RawStigmaToken(plainJWT.serialize()));

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.NOT_ENCRYPTED);
    }

    @Test
    void should_fail_validation_on_incorrect_subject() {
        // given
        final RawStigmaToken stigmaToken = givenEncryptedToken(
                with -> with.subject("INVALID_SUBJECT")
                        .issueTime(ISSUE_TIME)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA_VALUE)
        );

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.INVALID_PAYLOAD);
    }

    @Test
    void should_fail_validation_on_missing_random_stigma() {
        // given
        final RawStigmaToken stigmaToken = givenEncryptedToken(
                with -> with.subject(SUBJECT)
                        .issueTime(ISSUE_TIME)
        );

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.MISSING_STIGMA);
    }

    @Test
    void should_fail_validation_on_missing_issue_time() {
        // given
        final RawStigmaToken stigmaToken = givenEncryptedToken(
                with -> with.subject(SUBJECT)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA_VALUE)
        );

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.INVALID_PAYLOAD);
    }

    @Test
    void should_fail_validation_on_expired_token() {
        // given
        final Date expiredIssueTime = Date.from(ISSUE_TIME.toInstant().minus(TOKEN_LIFETIME.plusDays(1)));

        final RawStigmaToken stigmaToken = givenEncryptedToken(
                with -> with.subject(SUBJECT)
                        .issueTime(expiredIssueTime)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA_VALUE)
        );

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.EXPIRED);
    }

    @Test
    void should_fail_validation_on_parsing_error() {
        // when
        final ValidationResult result = validator.validate(new RawStigmaToken("not parsable JWE"));

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.PARSING_ERROR);
    }

    @Test
    void should_fail_validation_on_payload_parsing_error() throws JOSEException {
        // given
        final Payload payload = new Payload("not parsable payload");

        final DirectEncrypterFactory encryptionSpec = new DirectEncrypterFactory(this.jwk);

        final JWEHeader header = new JWEHeader.Builder(encryptionSpec.getAlgorithm(), encryptionSpec.getEncryptionMethod())
                .keyID(encryptionSpec.getKeyId()).build();

        final JWEObject jweObject = new JWEObject(header, payload);
        jweObject.encrypt(encryptionSpec.encrypter());

        final RawStigmaToken stigmaToken = new RawStigmaToken(jweObject.serialize());

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.PAYLOAD_PARSING_ERROR);
    }

    @Test
    void should_fail_validation_on_decryption_error() throws JOSEException {
        // given
        final OctetSequenceKey anotherKeyWithMatchingID = new OctetSequenceKeyGenerator(256).keyID(jwk.getKeyID()).generate();

        final RawStigmaToken stigmaToken = givenEncryptedToken(anotherKeyWithMatchingID,
                with -> with.subject(SUBJECT)
                        .issueTime(ISSUE_TIME)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA_VALUE)
        );

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.DECRYPTION_ERROR);
    }

    private RawStigmaToken givenEncryptedToken(final OctetSequenceKey encryptionKey, final Consumer<JWTClaimsSet.Builder> claimsAssigner) {
        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        claimsAssigner.accept(builder);
        final PlainJWT plainJWT = new PlainJWT(builder.build());

        final StigmaTokenProvider plainTokenProvider = Mockito.mock(StigmaTokenProvider.class);
        given(plainTokenProvider.getToken(STIGMA_VALUE)).willReturn(plainJWT);

        final EncryptedStigmaTokenProvider encryptedTokenProvider =
                new EncryptedStigmaTokenProvider(plainTokenProvider, new DirectEncrypterFactory(encryptionKey));

        return new RawStigmaToken(encryptedTokenProvider.getToken(STIGMA_VALUE).serialize());
    }

    private RawStigmaToken givenEncryptedToken(final Consumer<JWTClaimsSet.Builder> claimsAssigner) {
        return givenEncryptedToken(this.jwk, claimsAssigner);
    }
}
