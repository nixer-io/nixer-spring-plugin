package io.nixer.nixerplugin.stigma.evaluate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.storage.StigmaData;
import io.nixer.nixerplugin.stigma.token.validation.StigmaExtractor;
import io.nixer.nixerplugin.stigma.token.StigmaTokenFactory;

import static io.nixer.nixerplugin.stigma.evaluate.StigmaActionType.TOKEN_BAD_LOGIN_FAIL;
import static io.nixer.nixerplugin.stigma.evaluate.StigmaActionType.TOKEN_BAD_LOGIN_SUCCESS;
import static io.nixer.nixerplugin.stigma.evaluate.StigmaActionType.TOKEN_GOOD_LOGIN_FAIL;
import static io.nixer.nixerplugin.stigma.evaluate.StigmaActionType.TOKEN_GOOD_LOGIN_SUCCESS;

/**
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
public class StigmaActionEvaluator {

    private final StigmaExtractor stigmaExtractor;

    private final StigmaService stigmaService;

    private final StigmaTokenFactory stigmaTokenFactory;

    private final StigmaValidator stigmaValidator;

    public StigmaActionEvaluator(final StigmaExtractor stigmaExtractor,
                                 final StigmaService stigmaService,
                                 final StigmaTokenFactory stigmaTokenFactory,
                                 final StigmaValidator stigmaValidator) {
        this.stigmaExtractor = stigmaExtractor;
        this.stigmaService = stigmaService;
        this.stigmaTokenFactory = stigmaTokenFactory;
        this.stigmaValidator = stigmaValidator;
    }

    /**
     * To be called after successful login attempt.
     * Consumes the currently used raw stigma token (might be null or empty) and returns a token for further usage (might be the same one)
     * with information about validity of the original token.
     */
    @Nonnull
    public StigmaAction onLoginSuccess(@Nullable final RawStigmaToken originalToken) {

        final StigmaData stigmaData = findStigmaData(originalToken);

        final StigmaAction stigmaAction = isStigmaValid(stigmaData)
                ? new StigmaAction(originalToken, TOKEN_GOOD_LOGIN_SUCCESS)
                : new StigmaAction(newStigmaToken(), TOKEN_BAD_LOGIN_SUCCESS);

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

        final StigmaData stigmaData = findStigmaData(originalToken);

        final StigmaAction stigmaAction;
        if (isStigmaValid(stigmaData)) {
            stigmaService.revokeStigma(stigmaData.getStigma());
            stigmaAction = new StigmaAction(newStigmaToken(), TOKEN_GOOD_LOGIN_FAIL);
        } else {
            stigmaAction = new StigmaAction(newStigmaToken(), TOKEN_BAD_LOGIN_FAIL);
        }

        writeToMetrics(stigmaAction);

        return stigmaAction;
    }

    @Nullable
    private StigmaData findStigmaData(@Nullable final RawStigmaToken stigmaToken) {

        if (stigmaToken != null) {
            final Stigma stigma = extractStigma(stigmaToken);

            return stigma != null
                    ? stigmaService.findStigmaData(stigma)
                    : null;
        } else {
            return null;
        }
    }

    private Stigma extractStigma(final RawStigmaToken originalToken) {
        return stigmaExtractor.extractStigma(originalToken);
    }

    private boolean isStigmaValid(final StigmaData stigmaData) {
        return stigmaValidator.isValid(stigmaData);
    }

    private RawStigmaToken newStigmaToken() {
        final StigmaData newStigma = stigmaService.getNewStigma();

        return stigmaTokenFactory.getToken(newStigma.getStigma());
    }

    private void writeToMetrics(final StigmaAction stigmaAction) {
        // TODO implement!
        // stigmaMetricsService.rememberStigmaActionType(stigmaAction.getType());
    }
}
