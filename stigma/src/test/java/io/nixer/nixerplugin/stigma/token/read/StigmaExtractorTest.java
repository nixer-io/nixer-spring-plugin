package io.nixer.nixerplugin.stigma.token.read;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Created on 19/01/2020.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
@ExtendWith(MockitoExtension.class)
class StigmaExtractorTest {

    // plain JWT for simplicity, as we only need it to be parsable, contents or encryption are irrelevant here
    private static final com.nimbusds.jwt.JWT JWT = new PlainJWT(new JWTClaimsSet.Builder().build());

    private static final RawStigmaToken STIGMA_TOKEN = new RawStigmaToken(JWT.serialize());

    private static final Stigma STIGMA = new Stigma("stigma-value");

    @Mock
    private DecryptedToken decryptedToken;

    @Mock
    private ParsedToken parsedToken;

    @Mock
    private TokenDecrypter tokenDecrypter;

    @Mock
    private TokenParser tokenParser;

    @InjectMocks
    private StigmaExtractor stigmaExtractor;

    @Test
    void should_extract_stigma_from_valid_token() {
        // given
        given(tokenDecrypter.decrypt(any(com.nimbusds.jwt.JWT.class))).willReturn(decryptedToken);
        given(decryptedToken.isValid()).willReturn(true);
        given(tokenParser.parse(decryptedToken)).willReturn(parsedToken);
        given(parsedToken.isValid()).willReturn(true);
        given(parsedToken.getStigma()).willReturn(STIGMA);

        // when
        final Stigma stigma = stigmaExtractor.extractStigma(STIGMA_TOKEN);

        // then
        assertThat(stigma).isEqualTo(STIGMA);
    }

    @Test
    void should_return_no_result_on_parsing_failure() {
        // given
        given(tokenDecrypter.decrypt(any(com.nimbusds.jwt.JWT.class))).willReturn(decryptedToken);
        given(decryptedToken.isValid()).willReturn(true);
        given(tokenParser.parse(decryptedToken)).willReturn(parsedToken);
        given(parsedToken.isValid()).willReturn(false);

        // when
        final Stigma stigma = stigmaExtractor.extractStigma(STIGMA_TOKEN);

        // then
        assertThat(stigma).isNull();
    }

    @Test
    void should_return_no_result_on_decrypting_failure() {
        // given
        given(tokenDecrypter.decrypt(any(com.nimbusds.jwt.JWT.class))).willReturn(decryptedToken);
        given(decryptedToken.isValid()).willReturn(false);

        // when
        final Stigma stigma = stigmaExtractor.extractStigma(STIGMA_TOKEN);

        // then
        assertThat(stigma).isNull();
        verifyNoInteractions(tokenParser);
    }

    @Test
    void should_return_no_result_on_JWT_parsing_failure() {
        // given
        final RawStigmaToken stigmaToken = new RawStigmaToken("not-parsable-to-JWT");

        // when
        final Stigma stigma = stigmaExtractor.extractStigma(stigmaToken);

        // then
        assertThat(stigma).isNull();
        verifyNoInteractions(tokenDecrypter);
        verifyNoInteractions(tokenParser);
    }
}
