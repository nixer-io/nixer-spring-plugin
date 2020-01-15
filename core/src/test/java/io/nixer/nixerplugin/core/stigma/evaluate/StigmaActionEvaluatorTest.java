package io.nixer.nixerplugin.core.stigma.evaluate;

import io.nixer.nixerplugin.core.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.core.stigma.domain.Stigma;
import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.core.stigma.storage.StigmaData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.nixer.nixerplugin.core.stigma.evaluate.StigmaActionType.TOKEN_BAD_LOGIN_FAIL;
import static io.nixer.nixerplugin.core.stigma.evaluate.StigmaActionType.TOKEN_BAD_LOGIN_SUCCESS;
import static io.nixer.nixerplugin.core.stigma.evaluate.StigmaActionType.TOKEN_GOOD_LOGIN_FAIL;
import static io.nixer.nixerplugin.core.stigma.evaluate.StigmaActionType.TOKEN_GOOD_LOGIN_SUCCESS;
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

    private static final RawStigmaToken VALID_TOKEN = new RawStigmaToken("valid-token");
    private static final RawStigmaToken INVALID_TOKEN = new RawStigmaToken("invalid-token");
    private static final RawStigmaToken REFRESHED_TOKEN = new RawStigmaToken("refreshed-token");

    private static final StigmaData REVOKED_STIGMA = new StigmaData(new Stigma("revoked-stigma"), StigmaStatus.REVOKED);
    private static final StigmaData ACTIVE_STIGMA = new StigmaData(new Stigma("active-stigma"), StigmaStatus.ACTIVE);

    @Mock
    private StigmaTokenService stigmaTokenService;

    @InjectMocks
    private StigmaActionEvaluator actionEvaluator;

    @BeforeEach
    void setUp() {
        lenient().when(stigmaTokenService.newStigmaToken()).thenReturn(REFRESHED_TOKEN);
    }

    @Test
    void should_get_action_on_login_success_and_valid_stigma() {
        // given
        given(stigmaTokenService.findStigmaData(VALID_TOKEN)).willReturn(ACTIVE_STIGMA);

        // when
        final StigmaAction action = actionEvaluator.onLoginSuccess(VALID_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(VALID_TOKEN, TOKEN_GOOD_LOGIN_SUCCESS));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_GOOD_LOGIN_SUCCESS);
    }

    @Test
    void should_get_action_on_login_success_and_invalid_token() {
        // given
        given(stigmaTokenService.findStigmaData(INVALID_TOKEN)).willReturn(null);

        // when
        final StigmaAction action = actionEvaluator.onLoginSuccess(INVALID_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(REFRESHED_TOKEN, TOKEN_BAD_LOGIN_SUCCESS));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_SUCCESS);
    }

    @Test
    void should_get_action_on_login_success_and_not_active_stigma() {
        // given
        given(stigmaTokenService.findStigmaData(VALID_TOKEN)).willReturn(REVOKED_STIGMA);

        // when
        final StigmaAction action = actionEvaluator.onLoginSuccess(VALID_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(REFRESHED_TOKEN, TOKEN_BAD_LOGIN_SUCCESS));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_SUCCESS);
    }

    @Test
    void should_get_action_on_login_failure_and_valid_token() {
        // given
        given(stigmaTokenService.findStigmaData(VALID_TOKEN)).willReturn(ACTIVE_STIGMA);

        // when
        final StigmaAction action = actionEvaluator.onLoginFail(VALID_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(REFRESHED_TOKEN, TOKEN_GOOD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_GOOD_LOGIN_FAIL);
    }

    @Test
    void should_get_action_on_login_failure_and_invalid_token() {
        // given
        given(stigmaTokenService.findStigmaData(INVALID_TOKEN)).willReturn(null);

        // when
        final StigmaAction action = actionEvaluator.onLoginFail(INVALID_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(REFRESHED_TOKEN, TOKEN_BAD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_FAIL);
    }

    @Test
    void should_get_action_on_login_failure_and_not_active_stigma() {
        // given
        given(stigmaTokenService.findStigmaData(VALID_TOKEN)).willReturn(REVOKED_STIGMA);

        // when
        final StigmaAction action = actionEvaluator.onLoginFail(VALID_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(REFRESHED_TOKEN, TOKEN_BAD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_FAIL);
    }
}
