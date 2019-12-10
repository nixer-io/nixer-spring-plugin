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

    @Mock
    private StigmaTokenService stigmaTokenService;

    @InjectMocks
    private StigmaActionEvaluator actionEvaluator;

    @Test
    void should_get_action_on_login_success_and_valid_token() {
        // given
        final RawStigmaToken validToken = new RawStigmaToken("valid-token");
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
        final RawStigmaToken invalidToken = new RawStigmaToken("invalid-token");
        final RawStigmaToken newToken = new RawStigmaToken("new-token");
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
        final RawStigmaToken originalValidToken = new RawStigmaToken("valid-token");
        final RawStigmaToken newToken = new RawStigmaToken("new-valid-token");

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
        final RawStigmaToken invalidToken = new RawStigmaToken("invalid-token");
        final RawStigmaToken newToken = new RawStigmaToken("new-token");

        given(stigmaTokenService.fetchTokenOnLoginFail(invalidToken))
                .willReturn(new StigmaTokenFetchResult(newToken, false));

        // when
        final StigmaAction action = actionEvaluator.onLoginFail(invalidToken);

        // then
        assertThat(action).isEqualTo(new StigmaAction(newToken, TOKEN_BAD_LOGIN_FAIL));
        //        verify(stigmaMetricsService).rememberStigmaActionType(TOKEN_BAD_LOGIN_FAIL);
    }
}
