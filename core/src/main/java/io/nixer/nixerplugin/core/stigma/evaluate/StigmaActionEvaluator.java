package io.nixer.nixerplugin.core.stigma.evaluate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.stigma.domain.RawStigmaToken;

import static io.nixer.nixerplugin.core.stigma.evaluate.StigmaActionType.TOKEN_BAD_LOGIN_FAIL;
import static io.nixer.nixerplugin.core.stigma.evaluate.StigmaActionType.TOKEN_BAD_LOGIN_SUCCESS;
import static io.nixer.nixerplugin.core.stigma.evaluate.StigmaActionType.TOKEN_GOOD_LOGIN_FAIL;
import static io.nixer.nixerplugin.core.stigma.evaluate.StigmaActionType.TOKEN_GOOD_LOGIN_SUCCESS;

/**
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
public class StigmaActionEvaluator {

    private final StigmaTokenService stigmaTokenService;

    public StigmaActionEvaluator(final StigmaTokenService stigmaTokenService) {
        this.stigmaTokenService = stigmaTokenService;
    }

    @Nonnull
    public StigmaAction onLoginSuccess(@Nullable final RawStigmaToken originalToken) {

        final StigmaTokenFetchResult tokenFetchResult = stigmaTokenService.fetchTokenOnLoginSuccess(originalToken);

        final StigmaAction stigmaAction = tokenFetchResult.isOriginalTokenValid()
                ? new StigmaAction(originalToken, TOKEN_GOOD_LOGIN_SUCCESS)
                : new StigmaAction(tokenFetchResult.getFetchedToken(), TOKEN_BAD_LOGIN_SUCCESS);

        writeToMetrics(stigmaAction);

        return stigmaAction;
    }

    @Nonnull
    public StigmaAction onLoginFail(@Nullable final RawStigmaToken originalToken) {

        final StigmaTokenFetchResult tokenFetchResult = stigmaTokenService.fetchTokenOnLoginFail(originalToken);

        final StigmaAction stigmaAction = tokenFetchResult.isOriginalTokenValid()
                ? new StigmaAction(tokenFetchResult.getFetchedToken(), TOKEN_GOOD_LOGIN_FAIL)
                : new StigmaAction(tokenFetchResult.getFetchedToken(), TOKEN_BAD_LOGIN_FAIL);

        writeToMetrics(stigmaAction);

        return stigmaAction;
    }

    private void writeToMetrics(final StigmaAction stigmaAction) {
        // TODO implement!
        // stigmaMetricsService.rememberStigmaActionType(stigmaAction.getType());
    }
}
