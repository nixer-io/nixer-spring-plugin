package io.nixer.nixerplugin.stigma.token.validation;

import java.text.ParseException;
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

    private static final Stigma STIGMA = new Stigma("random-stigma-value");

    private final StigmaTokenPayloadValidator validator = new StigmaTokenPayloadValidator();

    @Test
    void should_pass_validation() {
        // given
        final JWT stigmaToken = givenPlainToken(
                with -> with.subject(SUBJECT)
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
        );

        // when
        final ValidationResult result = validator.validate(stigmaToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.MISSING_STIGMA);
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
