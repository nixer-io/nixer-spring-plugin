package io.nixer.nixerplugin.stigma.token.read;

import java.text.ParseException;
import java.util.function.Consumer;

import com.nimbusds.jwt.JWTClaimsSet;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.nixer.nixerplugin.stigma.StigmaConstants.STIGMA_VALUE_FIELD_NAME;
import static io.nixer.nixerplugin.stigma.StigmaConstants.SUBJECT;
import static io.nixer.nixerplugin.stigma.token.read.ParsedToken.ParsingStatus;
import static io.nixer.nixerplugin.stigma.token.read.ParsedToken.valid;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Created on 10/12/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
class TokenParserTest {

    private static final Stigma STIGMA = new Stigma("random-stigma-value");

    private final TokenParser tokenParser = new TokenParser();

    @Test
    void should_parse_token() throws ParseException {
        // given
        final DecryptedToken decryptedToken = givenDecryptedToken(
                with -> with.subject(SUBJECT)
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
        );

        // when
        final ParsedToken result = tokenParser.parse(decryptedToken);

        // then
        assertThat(result).isEqualTo(valid(STIGMA));
    }

    @Test
    void should_fail_parsing_on_incorrect_subject() throws ParseException {
        // given
        final DecryptedToken decryptedToken = givenDecryptedToken(
                with -> with.subject("INVALID_SUBJECT")
                        .claim(STIGMA_VALUE_FIELD_NAME, STIGMA.getValue())
        );

        // when
        final ParsedToken result = tokenParser.parse(decryptedToken);

        // then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getStatus()).isEqualTo(ParsingStatus.INVALID_PAYLOAD);
    }

    @Test
    void should_fail_parsing_on_missing_stigma() throws ParseException {
        // given
        final DecryptedToken decryptedToken = givenDecryptedToken(
                with -> with.subject(SUBJECT)
        );

        // when
        final ParsedToken result = tokenParser.parse(decryptedToken);

        // then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getStatus()).isEqualTo(ParsingStatus.MISSING_STIGMA);
    }

    @Test
    void should_fail_parsing_on_payload_parsing_error() throws ParseException {
        // given
        final DecryptedToken decryptedToken = Mockito.mock(DecryptedToken.class);
        given(decryptedToken.isValid()).willReturn(true);
        given(decryptedToken.getPayload()).willThrow(ParseException.class);

        // when
        final ParsedToken result = tokenParser.parse(decryptedToken);

        // then
        assertThat(result.isValid()).isFalse();
        assertThat(result.getStatus()).isEqualTo(ParsingStatus.PAYLOAD_PARSING_ERROR);
    }

    private static DecryptedToken givenDecryptedToken(final Consumer<JWTClaimsSet.Builder> claimsAssigner) throws ParseException {
        final JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
        claimsAssigner.accept(builder);
        final JWTClaimsSet claims = builder.build();

        final DecryptedToken decryptedToken = Mockito.mock(DecryptedToken.class);
        given(decryptedToken.isValid()).willReturn(true);
        given(decryptedToken.getPayload()).willReturn(claims);

        return decryptedToken;
    }
}
