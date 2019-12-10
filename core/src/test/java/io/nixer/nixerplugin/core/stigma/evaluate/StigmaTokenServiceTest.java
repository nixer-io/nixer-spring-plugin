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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

    private static final StigmaData INVALID_STIGMA_VALUE_DATA = new StigmaData(
            STIGMA,
            StigmaStatus.REVOKED
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
    void should_fetch_token_on_login_success_and_original_token_valid() {
        // given
        given(stigmaTokenValidator.validate(RAW_TOKEN)).willReturn(ValidationResult.valid(STIGMA));
        given(stigmaTokenStorage.findStigmaData(STIGMA)).willReturn(VALID_STIGMA_VALUE_DATA);

        // when
        final StigmaTokenFetchResult stigmaTokenFetchResult = stigmaTokenService.fetchTokenOnLoginSuccess(RAW_TOKEN);

        // then
        assertThat(stigmaTokenFetchResult).isEqualTo(new StigmaTokenFetchResult(RAW_TOKEN, true));
        verify(stigmaTokenStorage).recordStigmaObservation(VALID_STIGMA_VALUE_DATA);
        verify(stigmaTokenStorage, never()).recordSpottingUnknownStigma(any());
        verify(stigmaTokenStorage, never()).createStigma(any(), any());
    }

    @Test
    void should_fetch_token_on_login_success_and_original_token_invalid() {
        // given
        given(stigmaTokenValidator.validate(RAW_TOKEN))
                .willReturn(ValidationResult.invalid(ValidationStatus.DECRYPTION_ERROR, "invalid-token-details"));

        // when
        final StigmaTokenFetchResult stigmaTokenFetchResult = stigmaTokenService.fetchTokenOnLoginSuccess(RAW_TOKEN);

        // then
        assertThat(stigmaTokenFetchResult).isEqualTo(new StigmaTokenFetchResult(NEW_RAW_TOKEN, false));
        verify(stigmaTokenStorage).recordUnreadableToken(RAW_TOKEN);
        verify(stigmaTokenStorage).createStigma(NEW_STIGMA, StigmaStatus.ACTIVE);
    }

    @Test
    void should_fetch_token_on_login_success_and_original_token_missing() {
        // given
        given(stigmaTokenValidator.validate(null))
                .willReturn(ValidationResult.invalid(ValidationStatus.MISSING, "invalid-token-details"));

        // when
        final StigmaTokenFetchResult stigmaTokenFetchResult = stigmaTokenService.fetchTokenOnLoginSuccess(null);

        // then
        assertThat(stigmaTokenFetchResult).isEqualTo(new StigmaTokenFetchResult(NEW_RAW_TOKEN, false));
        verify(stigmaTokenStorage, never()).recordUnreadableToken(any());
        verify(stigmaTokenStorage).createStigma(NEW_STIGMA, StigmaStatus.ACTIVE);
    }

    @Test
    void should_fetch_token_on_login_success_and_original_stigma_invalid() {
        //given
        given(stigmaTokenValidator.validate(RAW_TOKEN)).willReturn(ValidationResult.valid(STIGMA));
        given(stigmaTokenStorage.findStigmaData(STIGMA)).willReturn(INVALID_STIGMA_VALUE_DATA);

        // when
        final StigmaTokenFetchResult stigmaTokenFetchResult = stigmaTokenService.fetchTokenOnLoginSuccess(RAW_TOKEN);

        // then
        assertThat(stigmaTokenFetchResult).isEqualTo(new StigmaTokenFetchResult(NEW_RAW_TOKEN, false));
        verify(stigmaTokenStorage).recordStigmaObservation(INVALID_STIGMA_VALUE_DATA);
        verify(stigmaTokenStorage).createStigma(NEW_STIGMA, StigmaStatus.ACTIVE);
    }

    @Test
    void should_fetch_token_on_login_success_and_original_stigma_unknown() {
        //given
        given(stigmaTokenValidator.validate(RAW_TOKEN)).willReturn(ValidationResult.valid(STIGMA));
        given(stigmaTokenStorage.findStigmaData(STIGMA)).willReturn(null);

        // when
        final StigmaTokenFetchResult stigmaTokenFetchResult = stigmaTokenService.fetchTokenOnLoginSuccess(RAW_TOKEN);

        // then
        assertThat(stigmaTokenFetchResult).isEqualTo(new StigmaTokenFetchResult(NEW_RAW_TOKEN, false));
        verify(stigmaTokenStorage).recordSpottingUnknownStigma(STIGMA);
        verify(stigmaTokenStorage, never()).recordStigmaObservation(any());
        verify(stigmaTokenStorage).createStigma(NEW_STIGMA, StigmaStatus.ACTIVE);
    }

    @Test
    void should_fetch_token_on_login_fail_and_original_token_valid() {
        // given
        given(stigmaTokenValidator.validate(RAW_TOKEN)).willReturn(ValidationResult.valid(STIGMA));
        given(stigmaTokenStorage.findStigmaData(STIGMA)).willReturn(VALID_STIGMA_VALUE_DATA);

        // when
        final StigmaTokenFetchResult stigmaTokenFetchResult = stigmaTokenService.fetchTokenOnLoginFail(RAW_TOKEN);

        // then
        assertThat(stigmaTokenFetchResult).isEqualTo(new StigmaTokenFetchResult(NEW_RAW_TOKEN, true));
        verify(stigmaTokenStorage).recordStigmaObservation(VALID_STIGMA_VALUE_DATA);
        verify(stigmaTokenStorage).updateStatus(VALID_STIGMA_VALUE_DATA.getStigma(), StigmaStatus.REVOKED);
        verify(stigmaTokenStorage).createStigma(NEW_STIGMA, StigmaStatus.ACTIVE);
    }

    @Test
    void should_fetch_token_on_login_fail_and_original_token_invalid() {
        // given
        given(stigmaTokenValidator.validate(RAW_TOKEN))
                .willReturn(ValidationResult.invalid(ValidationStatus.DECRYPTION_ERROR, "invalid-token-details"));

        // when
        final StigmaTokenFetchResult stigmaTokenFetchResult = stigmaTokenService.fetchTokenOnLoginFail(RAW_TOKEN);

        // then
        assertThat(stigmaTokenFetchResult).isEqualTo(new StigmaTokenFetchResult(NEW_RAW_TOKEN, false));
        verify(stigmaTokenStorage).recordUnreadableToken(RAW_TOKEN);
        verify(stigmaTokenStorage).createStigma(NEW_STIGMA, StigmaStatus.ACTIVE);
    }

    @Test
    void should_fetch_token_on_login_fail_and_original_token_missing() {
        // given
        given(stigmaTokenValidator.validate(null))
                .willReturn(ValidationResult.invalid(ValidationStatus.MISSING, "invalid-token-details"));

        // when
        final StigmaTokenFetchResult stigmaTokenFetchResult = stigmaTokenService.fetchTokenOnLoginFail(null);

        // then
        assertThat(stigmaTokenFetchResult).isEqualTo(new StigmaTokenFetchResult(NEW_RAW_TOKEN, false));
        verify(stigmaTokenStorage, never()).recordUnreadableToken(any());
        verify(stigmaTokenStorage).createStigma(NEW_STIGMA, StigmaStatus.ACTIVE);
    }

    @Test
    void should_fetch_token_on_login_fail_and_original_stigma_invalid() {
        //given
        given(stigmaTokenValidator.validate(RAW_TOKEN)).willReturn(ValidationResult.valid(STIGMA));
        given(stigmaTokenStorage.findStigmaData(STIGMA)).willReturn(INVALID_STIGMA_VALUE_DATA);

        // when
        final StigmaTokenFetchResult stigmaTokenFetchResult = stigmaTokenService.fetchTokenOnLoginFail(RAW_TOKEN);

        // then
        assertThat(stigmaTokenFetchResult).isEqualTo(new StigmaTokenFetchResult(NEW_RAW_TOKEN, false));
        verify(stigmaTokenStorage).recordStigmaObservation(INVALID_STIGMA_VALUE_DATA);
        verify(stigmaTokenStorage).createStigma(NEW_STIGMA, StigmaStatus.ACTIVE);
    }
}
