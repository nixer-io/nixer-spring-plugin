package io.nixer.nixerplugin.stigma.token;

import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.token.validation.StigmaTokenValidator;
import io.nixer.nixerplugin.stigma.token.validation.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.nixer.nixerplugin.stigma.token.validation.ValidationStatus.PARSING_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Created on 19/01/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
@ExtendWith(MockitoExtension.class)
class StigmaExtractorTest {

    private static final RawStigmaToken STIGMA_TOKEN = new RawStigmaToken("raw-value");
    private static final Stigma STIGMA = new Stigma("stigma-value");

    @Mock
    private StigmaTokenValidator stigmaTokenValidator;

    @InjectMocks
    private StigmaExtractor stigmaExtractor;

    @Test
    void should_extract_stigma_from_valid_token() {
        // given
        given(stigmaTokenValidator.validate(STIGMA_TOKEN)).willReturn(ValidationResult.valid(STIGMA));

        // when
        final Stigma stigma = stigmaExtractor.extractStigma(STIGMA_TOKEN);

        // then
        assertThat(stigma).isEqualTo(STIGMA);
    }

    @Test
    void should_return_no_result_on_invalid_token() {
        // given
        given(stigmaTokenValidator.validate(STIGMA_TOKEN)).willReturn(ValidationResult.invalid(PARSING_ERROR, "Not parsable token"));

        // when
        final Stigma stigma = stigmaExtractor.extractStigma(STIGMA_TOKEN);

        // then
        assertThat(stigma).isNull();
    }
}
