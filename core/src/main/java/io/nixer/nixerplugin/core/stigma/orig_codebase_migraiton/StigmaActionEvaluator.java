package io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;

import static io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton.StigmaActionType.TOKEN_BAD_LOGIN_FAIL;
import static io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton.StigmaActionType.TOKEN_BAD_LOGIN_SUCCESS;
import static io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton.StigmaActionType.TOKEN_GOOD_LOGIN_FAIL;
import static io.nixer.nixerplugin.core.stigma.orig_codebase_migraiton.StigmaActionType.TOKEN_GOOD_LOGIN_SUCCESS;

/**
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
public class StigmaActionEvaluator {

    @Autowired // TODO inject via constructor
    private StigmaTokenStore stigmaTokenStore;

//    @Nonnull
//    private final StigmaMetricsService stigmaMetricsService;

//    public StigmaActionEvaluator(@Nonnull final StigmaTokenStore stigmaTokenStore,
//                                 @Nonnull final StigmaMetricsService stigmaMetricsService) {
//        this.stigmaTokenStore = Preconditions.checkNotNull(stigmaTokenStore, "stigmaTokenStore");
//        this.stigmaMetricsService = Preconditions.checkNotNull(stigmaMetricsService, "stigmaMetricsService");
//    }

    @Nonnull
    public StigmaAction onLoginSuccess(@Nullable final String token) {

        final StigmaTokenFetchResult tokenFetchResult = stigmaTokenStore.fetchTokenOnLoginSuccess(token);

        final StigmaAction stigmaAction = tokenFetchResult.isOriginalTokenValid()
                ? new StigmaAction(token, TOKEN_GOOD_LOGIN_SUCCESS)
                : new StigmaAction(tokenFetchResult.getFetchedToken(), TOKEN_BAD_LOGIN_SUCCESS);

        writeToMetrics(stigmaAction);

        return stigmaAction;
    }

    @Nonnull
    public StigmaAction onLoginFail(@Nullable final String originalToken) {

        final StigmaTokenFetchResult tokenFetchResult = stigmaTokenStore.fetchTokenOnLoginFail(originalToken);

        final StigmaAction stigmaAction = tokenFetchResult.isOriginalTokenValid()
                ? new StigmaAction(tokenFetchResult.getFetchedToken(), TOKEN_GOOD_LOGIN_FAIL)
                : new StigmaAction(tokenFetchResult.getFetchedToken(), TOKEN_BAD_LOGIN_FAIL);

        writeToMetrics(stigmaAction);

        return stigmaAction;
    }

    @Nonnull
    public StigmaAction onLoginResultUnknown(@Nullable final String token) {
        // FIXME is this applicable?

        writeToMetrics(StigmaAction.STIGMA_ACTION_NOOP);

        return StigmaAction.STIGMA_ACTION_NOOP;
    }

    private void writeToMetrics(final StigmaAction stigmaAction) {
        // TODO implement!
//        stigmaMetricsService.rememberStigmaActionType(stigmaAction.getType());
    }
}
