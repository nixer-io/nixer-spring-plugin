package io.nixer.nixerplugin.core.stigma.evaluate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.core.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.core.stigma.domain.StigmaStatus;
import io.nixer.nixerplugin.core.stigma.storage.StigmaData;

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

    /**
     * To be called after successful login attempt.
     * Consumes the currently used raw stigma token (might be null or empty) and returns a token for further usage (might be the same one)
     * with information about validity of the original token.
     */
    @Nonnull
    public StigmaAction onLoginSuccess(@Nullable final RawStigmaToken originalToken) {

        final StigmaData stigmaData = stigmaTokenService.findStigmaData(originalToken);

        final StigmaAction stigmaAction = isStigmaActive(stigmaData)
                ? new StigmaAction(originalToken, TOKEN_GOOD_LOGIN_SUCCESS)
                : new StigmaAction(stigmaTokenService.newStigmaToken(), TOKEN_BAD_LOGIN_SUCCESS);

        writeToMetrics(stigmaAction);

        return stigmaAction;
    }

    /**
     * To be called after failed login attempt.
     * Consumes the currently used raw stigma token (might be null or empty) and returns a new token for further usage
     * with information about validity of the original token.
     */
    @Nonnull
    public StigmaAction onLoginFail(@Nullable final RawStigmaToken originalToken) {

        final StigmaData stigmaData = stigmaTokenService.findStigmaData(originalToken);

        final StigmaAction stigmaAction;

        if (isStigmaActive(stigmaData)) {
            stigmaTokenService.revokeStigma(stigmaData.getStigma());
            stigmaAction = new StigmaAction(stigmaTokenService.newStigmaToken(), TOKEN_GOOD_LOGIN_FAIL);
        } else {
            stigmaAction = new StigmaAction(stigmaTokenService.newStigmaToken(), TOKEN_BAD_LOGIN_FAIL);
        }

        writeToMetrics(stigmaAction);

        return stigmaAction;
    }

    private boolean isStigmaActive(@Nullable final StigmaData stigmaData) {
        return stigmaData != null
                && stigmaData.getStatus() == StigmaStatus.ACTIVE;
    }

    private void writeToMetrics(final StigmaAction stigmaAction) {
        // TODO implement!
        // stigmaMetricsService.rememberStigmaActionType(stigmaAction.getType());
    }
}
