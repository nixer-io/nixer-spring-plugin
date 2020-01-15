package io.nixer.nixerplugin.core.stigma.evaluate;

import com.nimbusds.jwt.JWT;
import io.nixer.nixerplugin.core.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.core.stigma.domain.Stigma;
import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.core.stigma.storage.StigmaData;
import io.nixer.nixerplugin.core.stigma.storage.StigmaTokenStorage;
import io.nixer.nixerplugin.core.stigma.token.StigmaTokenProvider;
import io.nixer.nixerplugin.core.stigma.token.StigmaValuesGenerator;
import io.nixer.nixerplugin.core.stigma.token.validation.StigmaTokenValidator;
import io.nixer.nixerplugin.core.stigma.token.validation.ValidationResult;
import io.nixer.nixerplugin.core.stigma.token.validation.ValidationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created on 2019-06-25.
 *
 * @author gcwiak
 */
@ExtendWith(MockitoExtension.class)
// TODO refactor tests so lenient setting is not necessary
@MockitoSettings(strictness = Strictness.LENIENT)
class StigmaTokenServiceTest {

    private static final RawStigmaToken RAW_TOKEN = new RawStigmaToken("raw-token");
    private static final RawStigmaToken NEW_RAW_TOKEN = new RawStigmaToken("new-raw-token");

    private static final Stigma STIGMA = new Stigma("stigma-value");
    private static final Stigma NEW_STIGMA = new Stigma("new-stigma-value");

    private static final StigmaData VALID_STIGMA_VALUE_DATA = new StigmaData(
            STIGMA,
            StigmaStatus.ACTIVE
    );

    @Mock
    private StigmaTokenProvider stigmaTokenProvider;

    @Mock
    private StigmaTokenValidator stigmaTokenValidator;

    @Mock
    private StigmaTokenStorage stigmaTokenStorage;

    @Mock
    private StigmaValuesGenerator stigmaValuesGenerator;

    private StigmaTokenService stigmaTokenService;

    @BeforeEach
    void setUp() {
        stigmaTokenService = new StigmaTokenService(
                stigmaTokenProvider,
                stigmaTokenStorage,
                stigmaValuesGenerator,
                stigmaTokenValidator
        );

        fetchNewTokenSetUp();
    }

    private void fetchNewTokenSetUp() {
        given(stigmaValuesGenerator.newStigma()).willReturn(NEW_STIGMA);

        final JWT jwtToken = Mockito.mock(JWT.class);
        given(stigmaTokenProvider.getToken(NEW_STIGMA)).willReturn(jwtToken);

        given(jwtToken.serialize()).willReturn(NEW_RAW_TOKEN.getValue());
    }

    @Test
    void should_find_stigma_data_for_valid_token() {
        // given
        given(stigmaTokenValidator.validate(RAW_TOKEN)).willReturn(ValidationResult.valid(STIGMA));
        given(stigmaTokenStorage.findStigmaData(STIGMA)).willReturn(VALID_STIGMA_VALUE_DATA);

        // when
        final StigmaData stigmaData = stigmaTokenService.findStigmaData(RAW_TOKEN);

        // then
        assertThat(stigmaData).isEqualTo(VALID_STIGMA_VALUE_DATA);
        verify(stigmaTokenStorage).recordStigmaObservation(VALID_STIGMA_VALUE_DATA);
    }

    @Test
    void should_return_empty_result_when_stigma_not_found_in_storage() {
        // given
        given(stigmaTokenValidator.validate(RAW_TOKEN)).willReturn(ValidationResult.valid(STIGMA));
        given(stigmaTokenStorage.findStigmaData(STIGMA)).willReturn(null);

        // when
        final StigmaData stigmaData = stigmaTokenService.findStigmaData(RAW_TOKEN);

        // then
        assertThat(stigmaData).isNull();
        verify(stigmaTokenStorage).recordSpottingUnknownStigma(STIGMA);
    }

    @Test
    void should_return_empty_result_for_invalid_token() {
        // given
        given(stigmaTokenValidator.validate(RAW_TOKEN))
                .willReturn(ValidationResult.invalid(ValidationStatus.DECRYPTION_ERROR, "invalid-token-details"));

        // when
        final StigmaData stigmaData = stigmaTokenService.findStigmaData(RAW_TOKEN);

        // then
        assertThat(stigmaData).isNull();
        verify(stigmaTokenStorage).recordUnreadableToken(RAW_TOKEN);
        verifyNoMoreInteractions(stigmaTokenStorage);
    }

    @Test
    void should_return_empty_result_for_missing_token() {
        // given
        given(stigmaTokenValidator.validate(null))
                .willReturn(ValidationResult.invalid(ValidationStatus.MISSING, "invalid-token-details"));

        // when
        final StigmaData stigmaData = stigmaTokenService.findStigmaData(null);

        // then
        assertThat(stigmaData).isNull();
        verifyNoInteractions(stigmaTokenStorage);
    }

    @Test
    void should_revoke_stigma() {
        // when
        stigmaTokenService.revokeStigma(STIGMA);

        // then
        verify(stigmaTokenStorage).updateStatus(STIGMA, StigmaStatus.REVOKED);
    }

    @Test
    void should_generate_new_stigma_token() {
        // given
        final Stigma stigma = new Stigma("new-stigma-value");
        given(stigmaValuesGenerator.newStigma()).willReturn(stigma);

        final JWT token = Mockito.mock(JWT.class);
        final String serializedToken = "serialized-token";
        given(token.serialize()).willReturn(serializedToken);

        given(stigmaTokenProvider.getToken(stigma)).willReturn(token);

        // when
        final RawStigmaToken newStigmaToken = stigmaTokenService.newStigmaToken();

        // then
        assertThat(newStigmaToken).isEqualTo(new RawStigmaToken(serializedToken));
        verify(stigmaTokenStorage).saveStigma(stigma, StigmaStatus.ACTIVE);
    }
}
