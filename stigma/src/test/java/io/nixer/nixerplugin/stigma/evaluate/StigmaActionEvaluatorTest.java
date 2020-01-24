package io.nixer.nixerplugin.stigma.evaluate;

import java.time.Instant;

import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.stigma.storage.StigmaData;
import io.nixer.nixerplugin.stigma.token.StigmaExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.nixer.nixerplugin.stigma.evaluate.StigmaActionType.TOKEN_BAD_LOGIN_FAIL;
import static io.nixer.nixerplugin.stigma.evaluate.StigmaActionType.TOKEN_BAD_LOGIN_SUCCESS;
import static io.nixer.nixerplugin.stigma.evaluate.StigmaActionType.TOKEN_GOOD_LOGIN_FAIL;
import static io.nixer.nixerplugin.stigma.evaluate.StigmaActionType.TOKEN_GOOD_LOGIN_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

/**
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
@ExtendWith(MockitoExtension.class)
class StigmaActionEvaluatorTest {

    // TODO verify metrics when implemented

    private static final RawStigmaToken STIGMA_TOKEN = new RawStigmaToken("valid-token");
    private static final RawStigmaToken REFRESHED_STIGMA_TOKEN = new RawStigmaToken("refreshed-token");

    private static final Stigma STIGMA = new Stigma("stigma-value");
    private static final StigmaData STIGMA_DATA = new StigmaData(STIGMA, StigmaStatus.ACTIVE, Instant.parse("2020-01-21T10:25:43.511Z"));

    @Mock
    private StigmaExtractor stigmaExtractor;

    @Mock
    private StigmaTokenService stigmaTokenService;

    @Mock
    private StigmaValidator stigmaValidator;

    @InjectMocks
    private StigmaActionEvaluator actionEvaluator;

    @BeforeEach
    void setUp() {
        lenient().when(stigmaTokenService.newStigmaToken()).thenReturn(REFRESHED_STIGMA_TOKEN);
    }

    @Test
    void should_get_action_on_login_success_and_valid_stigma() {
        // given
        given(stigmaExtractor.extractStigma(STIGMA_TOKEN)).willReturn(STIGMA);
        given(stigmaTokenService.findStigmaData(STIGMA)).willReturn(STIGMA_DATA);
        given(stigmaValidator.isValid(STIGMA_DATA)).willReturn(true);

        // when
        final StigmaAction action = actionEvaluator.onLoginSuccess(STIGMA_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(STIGMA_TOKEN, TOKEN_GOOD_LOGIN_SUCCESS));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_GOOD_LOGIN_SUCCESS);
    }

    @Test
    void should_get_action_on_login_success_and_invalid_token() {
        // given
        given(stigmaExtractor.extractStigma(STIGMA_TOKEN)).willReturn(null);

        // when
        final StigmaAction action = actionEvaluator.onLoginSuccess(STIGMA_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(REFRESHED_STIGMA_TOKEN, TOKEN_BAD_LOGIN_SUCCESS));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_SUCCESS);
    }

    @Test
    void should_get_action_on_login_success_and_not_active_stigma() {
        // given
        given(stigmaExtractor.extractStigma(STIGMA_TOKEN)).willReturn(STIGMA);
        given(stigmaTokenService.findStigmaData(STIGMA)).willReturn(STIGMA_DATA);
        given(stigmaValidator.isValid(STIGMA_DATA)).willReturn(false);

        // when
        final StigmaAction action = actionEvaluator.onLoginSuccess(STIGMA_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(REFRESHED_STIGMA_TOKEN, TOKEN_BAD_LOGIN_SUCCESS));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_SUCCESS);
    }

    @Test
    void should_get_action_on_login_failure_and_valid_token() {
        // given
        given(stigmaExtractor.extractStigma(STIGMA_TOKEN)).willReturn(STIGMA);
        given(stigmaTokenService.findStigmaData(STIGMA)).willReturn(STIGMA_DATA);
        given(stigmaValidator.isValid(STIGMA_DATA)).willReturn(true);

        // when
        final StigmaAction action = actionEvaluator.onLoginFail(STIGMA_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(REFRESHED_STIGMA_TOKEN, TOKEN_GOOD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_GOOD_LOGIN_FAIL);
    }

    @Test
    void should_get_action_on_login_failure_and_invalid_token() {
        // given
        given(stigmaExtractor.extractStigma(STIGMA_TOKEN)).willReturn(null);

        // when
        final StigmaAction action = actionEvaluator.onLoginFail(STIGMA_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(REFRESHED_STIGMA_TOKEN, TOKEN_BAD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_FAIL);
    }

    @Test
    void should_get_action_on_login_failure_and_not_active_stigma() {
        // given
        given(stigmaExtractor.extractStigma(STIGMA_TOKEN)).willReturn(STIGMA);
        given(stigmaTokenService.findStigmaData(STIGMA)).willReturn(STIGMA_DATA);
        given(stigmaValidator.isValid(STIGMA_DATA)).willReturn(false);

        // when
        final StigmaAction action = actionEvaluator.onLoginFail(STIGMA_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(REFRESHED_STIGMA_TOKEN, TOKEN_BAD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_FAIL);
    }
}
