package io.nixer.nixerplugin.core.stigma.token.validation;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.stream.Stream;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import io.nixer.nixerplugin.core.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.core.stigma.domain.Stigma;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.nixer.nixerplugin.core.stigma.token.StigmaTokenConstants.STIGMA_VALUE_FIELD_NAME;
import static io.nixer.nixerplugin.core.stigma.token.StigmaTokenConstants.SUBJECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Created on 2019-05-22.
 *
 * @author gcwiak
 */
@ExtendWith(MockitoExtension.class)
class StigmaTokenValidatorTest {

    private static final Date ISSUE_TIME = Date.from(LocalDateTime.of(2019, 5, 19, 12, 45, 56).toInstant(ZoneOffset.UTC));
    private static final Stigma STIGMA = new Stigma("random-stigma-value");

    @Mock
    private JwtValidator jwtValidator;

    @Captor
    private ArgumentCaptor<JWT> jwtCaptor;

    @InjectMocks
    private StigmaTokenValidator stigmaTokenValidator;

    @Test
    void should_pass_validation() throws ParseException {
        // given
        final JWTClaimsSet givenClaims = new JWTClaimsSet.Builder()
                .subject(SUBJECT)
                .issueTime(ISSUE_TIME)
                .claim(STIGMA_VALUE_FIELD_NAME, STIGMA.getValue()).build();

        final JWT jwt = new PlainJWT(givenClaims);
        final RawStigmaToken stigmaToken = new RawStigmaToken(jwt.serialize());

        given(jwtValidator.validate(any(JWT.class))).willReturn(ValidationResult.valid(STIGMA));

        // when
        final ValidationResult result = stigmaTokenValidator.validate(stigmaToken);

        // then
        assertThat(result).isEqualTo(ValidationResult.valid(STIGMA));

        verify(jwtValidator).validate(jwtCaptor.capture());
        assertThat(jwtCaptor.getValue().getJWTClaimsSet()).isEqualTo(givenClaims);
    }

    @ParameterizedTest
    @MethodSource("notParsableTokenExamples")
    void should_fail_validation_on_parsing_error(final RawStigmaToken notParsableToken) {
        // when
        final ValidationResult result = stigmaTokenValidator.validate(notParsableToken);

        // then
        assertThat(result.getStatus()).isEqualTo(ValidationStatus.PARSING_ERROR);
    }

    static Stream<RawStigmaToken> notParsableTokenExamples() {
        return Stream.of(
                new RawStigmaToken("not parsable JWE"),
                new RawStigmaToken(""),
                new RawStigmaToken(" "),
                new RawStigmaToken("   ")
        );
    }

}
