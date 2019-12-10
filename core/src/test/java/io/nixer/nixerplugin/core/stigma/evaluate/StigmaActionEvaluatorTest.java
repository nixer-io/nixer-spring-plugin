package io.nixer.nixerplugin.core.stigma.evaluate;

import io.nixer.nixerplugin.core.stigma.domain.RawStigmaToken;
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

    @Mock
    private StigmaTokenService stigmaTokenService;

    @InjectMocks
    private StigmaActionEvaluator actionEvaluator;

    @Test
    void should_get_action_on_login_success_and_valid_token() {
        // given
        given(stigmaTokenService.fetchTokenOnLoginSuccess(VALID_TOKEN))
                .willReturn(new StigmaTokenFetchResult(VALID_TOKEN, true));

        // when
        final StigmaAction action = actionEvaluator.onLoginSuccess(VALID_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(VALID_TOKEN, TOKEN_GOOD_LOGIN_SUCCESS));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_GOOD_LOGIN_SUCCESS);
    }

    @Test
    void should_get_action_on_login_success_and_invalid_token() {
        // given
        given(stigmaTokenService.fetchTokenOnLoginSuccess(INVALID_TOKEN))
                .willReturn(new StigmaTokenFetchResult(VALID_TOKEN, false));

        // when
        final StigmaAction action = actionEvaluator.onLoginSuccess(INVALID_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(VALID_TOKEN, TOKEN_BAD_LOGIN_SUCCESS));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_SUCCESS);
    }

    @Test
    void should_get_action_on_login_failure_and_valid_token() {
        // given
        final RawStigmaToken newValidToken = new RawStigmaToken("new-valid-token");

        given(stigmaTokenService.fetchTokenOnLoginFail(VALID_TOKEN))
                .willReturn(new StigmaTokenFetchResult(newValidToken, true));

        // when
        final StigmaAction action = actionEvaluator.onLoginFail(VALID_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(newValidToken, TOKEN_GOOD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_GOOD_LOGIN_FAIL);
    }

    @Test
    void should_get_action_on_login_failure_and_invalid_token() {
        // given
        given(stigmaTokenService.fetchTokenOnLoginFail(INVALID_TOKEN))
                .willReturn(new StigmaTokenFetchResult(VALID_TOKEN, false));

        // when
        final StigmaAction action = actionEvaluator.onLoginFail(INVALID_TOKEN);

        // then
        assertThat(action).isEqualTo(new StigmaAction(VALID_TOKEN, TOKEN_BAD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_FAIL);
    }
}
