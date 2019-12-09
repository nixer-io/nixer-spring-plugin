package io.nixer.nixerplugin.core.stigma.evaluate;

import com.nimbusds.jwt.JWT;
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

    private static final String RAW_TOKEN = "raw-token";
    private static final String NEW_RAW_TOKEN = "new-raw-token";

    private static final String STIGMA_VALUE = "stigma-value";

    private static final StigmaData VALID_STIGMA_VALUE_DATA = new StigmaData(
            STIGMA_VALUE,
            StigmaStatus.ACTIVE
    );

    private static final StigmaData INVALID_STIGMA_VALUE_DATA = new StigmaData(
            STIGMA_VALUE,
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
        final String newStigmaValue = "new-stigma-value";
        given(stigmaValuesGenerator.newStigma()).willReturn(newStigmaValue);
        given(stigmaTokenStorage.createStigma(newStigmaValue, StigmaStatus.ACTIVE)).willReturn(new Stigma(newStigmaValue));

        final JWT jwtToken = Mockito.mock(JWT.class);
        given(stigmaTokenProvider.getToken(newStigmaValue)).willReturn(jwtToken);

        given(jwtToken.serialize()).willReturn(NEW_RAW_TOKEN);
    }

    @Test
    void should_fetch_token_on_login_success_and_original_token_valid() {
        // given
        given(stigmaTokenValidator.validate(RAW_TOKEN)).willReturn(ValidationResult.valid(STIGMA_VALUE));
        given(stigmaTokenStorage.findStigmaData(new Stigma(STIGMA_VALUE))).willReturn(VALID_STIGMA_VALUE_DATA);

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
    }

    @Test
    void should_fetch_token_on_login_success_and_original_stigma_invalid() {
        //given
        given(stigmaTokenValidator.validate(RAW_TOKEN)).willReturn(ValidationResult.valid(STIGMA_VALUE));
        given(stigmaTokenStorage.findStigmaData(new Stigma(STIGMA_VALUE))).willReturn(INVALID_STIGMA_VALUE_DATA);

        // when
        final StigmaTokenFetchResult stigmaTokenFetchResult = stigmaTokenService.fetchTokenOnLoginSuccess(RAW_TOKEN);

        // then
        assertThat(stigmaTokenFetchResult).isEqualTo(new StigmaTokenFetchResult(NEW_RAW_TOKEN, false));
        verify(stigmaTokenStorage).recordStigmaObservation(INVALID_STIGMA_VALUE_DATA);
    }

    @Test
    void should_fetch_token_on_login_success_and_original_stigma_unknown() {
        //given
        given(stigmaTokenValidator.validate(RAW_TOKEN)).willReturn(ValidationResult.valid(STIGMA_VALUE));
        given(stigmaTokenStorage.findStigmaData(new Stigma(STIGMA_VALUE))).willReturn(null);

        // when
        final StigmaTokenFetchResult stigmaTokenFetchResult = stigmaTokenService.fetchTokenOnLoginSuccess(RAW_TOKEN);

        // then
        assertThat(stigmaTokenFetchResult).isEqualTo(new StigmaTokenFetchResult(NEW_RAW_TOKEN, false));
        verify(stigmaTokenStorage).recordSpottingUnknownStigma(new Stigma(STIGMA_VALUE));
        verify(stigmaTokenStorage, never()).recordStigmaObservation(any());
    }

    @Test
    void should_fetch_token_on_login_fail_and_original_token_valid() {
        // given
        given(stigmaTokenValidator.validate(RAW_TOKEN)).willReturn(ValidationResult.valid(STIGMA_VALUE));
        given(stigmaTokenStorage.findStigmaData(new Stigma(STIGMA_VALUE))).willReturn(VALID_STIGMA_VALUE_DATA);

        // when
        final StigmaTokenFetchResult stigmaTokenFetchResult = stigmaTokenService.fetchTokenOnLoginFail(RAW_TOKEN);

        // then
        assertThat(stigmaTokenFetchResult).isEqualTo(new StigmaTokenFetchResult(NEW_RAW_TOKEN, true));
        verify(stigmaTokenStorage).recordStigmaObservation(VALID_STIGMA_VALUE_DATA);
        verify(stigmaTokenStorage).revokeStigma(VALID_STIGMA_VALUE_DATA.getStigmaValue());
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
    }

    @Test
    void should_fetch_token_on_login_fail_and_original_stigma_invalid() {
        //given
        given(stigmaTokenValidator.validate(RAW_TOKEN)).willReturn(ValidationResult.valid(STIGMA_VALUE));
        given(stigmaTokenStorage.findStigmaData(new Stigma(STIGMA_VALUE))).willReturn(INVALID_STIGMA_VALUE_DATA);

        // when
        final StigmaTokenFetchResult stigmaTokenFetchResult = stigmaTokenService.fetchTokenOnLoginFail(RAW_TOKEN);

        // then
        assertThat(stigmaTokenFetchResult).isEqualTo(new StigmaTokenFetchResult(NEW_RAW_TOKEN, false));
        verify(stigmaTokenStorage).recordStigmaObservation(INVALID_STIGMA_VALUE_DATA);
    }
}
