package io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton.StigmaActionType.TOKEN_BAD_LOGIN_FAIL;
import static io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton.StigmaActionType.TOKEN_BAD_LOGIN_SUCCESS;
import static io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton.StigmaActionType.TOKEN_GOOD_LOGIN_FAIL;
import static io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton.StigmaActionType.TOKEN_GOOD_LOGIN_SUCCESS;
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

    @Mock
    private StigmaTokenService stigmaTokenService;

    @InjectMocks
    private StigmaActionEvaluator actionEvaluator;

    @Test
    void should_get_action_on_login_success_and_valid_token() {
        // given
        final String validToken = "valid-token";
        given(stigmaTokenService.fetchTokenOnLoginSuccess(validToken))
                .willReturn(new StigmaTokenFetchResult(validToken, true));

        // when
        final StigmaAction action = actionEvaluator.onLoginSuccess(validToken);

        // then
        assertThat(action).isEqualTo(new StigmaAction(validToken, TOKEN_GOOD_LOGIN_SUCCESS));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_GOOD_LOGIN_SUCCESS);
    }

    @Test
    void should_get_action_on_login_success_and_invalid_token() {
        // given
        final String invalidToken = "invalid-token";
        final String newToken = "new-token";
        given(stigmaTokenService.fetchTokenOnLoginSuccess(invalidToken))
                .willReturn(new StigmaTokenFetchResult(newToken, false));

        // when
        final StigmaAction action = actionEvaluator.onLoginSuccess(invalidToken);

        // then
        assertThat(action).isEqualTo(new StigmaAction(newToken, TOKEN_BAD_LOGIN_SUCCESS));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_SUCCESS);
    }

    @Test
    void should_get_action_on_login_failure_and_valid_token() {
        // given
        final String originalValidToken = "valid-token";
        final String newToken = "new-valid-token";

        given(stigmaTokenService.fetchTokenOnLoginFail(originalValidToken))
                .willReturn(new StigmaTokenFetchResult(newToken, true));

        // when
        final StigmaAction action = actionEvaluator.onLoginFail(originalValidToken);

        // then
        assertThat(action).isEqualTo(new StigmaAction(newToken, TOKEN_GOOD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_GOOD_LOGIN_FAIL);
    }

    @Test
    void should_get_action_on_login_failure_and_invalid_token() {
        // given
        final String invalidToken = "invalid-token";
        final String newToken = "new-token";

        given(stigmaTokenService.fetchTokenOnLoginFail(invalidToken))
                .willReturn(new StigmaTokenFetchResult(newToken, false));

        // when
        final StigmaAction action = actionEvaluator.onLoginFail(invalidToken);

        // then
        assertThat(action).isEqualTo(new StigmaAction(newToken, TOKEN_BAD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_FAIL);
    }

    @Test
    void should_get_action_on_unknown_login_result() { // TODO is this necessary?
        // given
        final String token = "token";

        // when
        final StigmaAction action = actionEvaluator.onLoginResultUnknown(token);

        // then
        assertThat(action).isEqualTo(StigmaAction.STIGMA_ACTION_NOOP);
        //        verify(stigmaMetricsService).rememberStigmaActionType(SKIP_ACTION);
    }
}
