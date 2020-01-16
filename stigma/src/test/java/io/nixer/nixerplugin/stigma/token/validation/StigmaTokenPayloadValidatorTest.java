package io.nixer.nixerplugin.stigma.token.validation;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.function.Consumer;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.nixer.nixerplugin.stigma.token.StigmaTokenConstants.STIGMA_VALUE_FIELD_NAME;
import static io.nixer.nixerplugin.stigma.token.StigmaTokenConstants.SUBJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Created on 10/12/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
class StigmaTokenPayloadValidatorTest {

    private static final Date ISSUE_TIME = Date.from(LocalDateTime.of(2019, 5, 19, 12, 45, 56).toInstant(ZoneOffset.UTC));
    private static final Duration TOKEN_LIFETIME = Duration.ofDays(30);
    private static final Instant NOW = LocalDateTime.of(2019, 5, 20, 13, 50, 15).toInstant(ZoneOffset.UTC);
    private static final Stigma STIGMA = new Stigma("random-stigma-value");

    private final StigmaTokenPayloadValidator validator = new StigmaTokenPayloadValidator(() -> NOW, TOKEN_LIFETIME);

    @Test
    void should_pass_validation() {
        // given
        final JWT stigmaToken = givenPlainToken(
                with -> with.subject(SUBJECT)
                        .issueTime(ISSUE_TIME)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
        );

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result).isEqualTo(ValidationResult.valid(STIGMA));
    }

    @Test
    void should_fail_validation_on_incorrect_subject() {
        // given
        final JWT stigmaToken = givenPlainToken(
                with -> with.subject("INVALID_SUBJECT")
                        .issueTime(ISSUE_TIME)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
        );

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.INVALID_PAYLOAD);
    }

    @Test
    void should_fail_validation_on_missing_random_stigma() {
        // given
        final JWT stigmaToken = givenPlainToken(
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
        final JWT stigmaToken = givenPlainToken(
                with -> with.subject(SUBJECT)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
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

        final JWT stigmaToken = givenPlainToken(
                with -> with.subject(SUBJECT)
                        .issueTime(expiredIssueTime)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
        );

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.EXPIRED);
    }

    @Test
    void should_fail_validation_on_payload_parsing_error() throws ParseException {
        // given
        final JWT stigmaToken = Mockito.mock(JWT.class);
        given(stigmaToken.getJWTClaimsSet()).willThrow(ParseException.class);

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.PAYLOAD_PARSING_ERROR);
    }

    private JWT givenPlainToken(final Consumer<JWTClaimsSet.Builder> claimsAssigner) {
        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        claimsAssigner.accept(builder);

        return new PlainJWT(builder.build());
    }
}
