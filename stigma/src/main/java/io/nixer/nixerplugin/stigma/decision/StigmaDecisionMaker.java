package io.nixer.nixerplugin.stigma.decision;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.domain.Stigma;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import io.nixer.nixerplugin.stigma.token.create.StigmaTokenFactory;
import io.nixer.nixerplugin.stigma.token.read.StigmaExtractor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static io.nixer.nixerplugin.stigma.decision.StigmaEvent.TOKEN_BAD_LOGIN_FAIL;
import static io.nixer.nixerplugin.stigma.decision.StigmaEvent.TOKEN_BAD_LOGIN_SUCCESS;
import static io.nixer.nixerplugin.stigma.decision.StigmaEvent.TOKEN_GOOD_LOGIN_FAIL;
import static io.nixer.nixerplugin.stigma.decision.StigmaEvent.TOKEN_GOOD_LOGIN_SUCCESS;

/**
 * Created on 2019-04-29.
 *
 * @author gcwiak
 */
public class StigmaDecisionMaker {

    private static final Log logger = LogFactory.getLog(StigmaDecisionMaker.class);

    private final StigmaExtractor stigmaExtractor;

    private final StigmaService stigmaService;

    private final StigmaTokenFactory stigmaTokenFactory;

    private final StigmaValidator stigmaValidator;

    public StigmaDecisionMaker(final StigmaExtractor stigmaExtractor,
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
     * Consumes the currently used raw stigma token (might be null or empty)
     * and returns {@link StigmaRefreshDecision} to be used for further actions.
     */
    @Nonnull
    public StigmaRefreshDecision onLoginSuccess(@Nullable final RawStigmaToken originalToken) {

        final StigmaDetails stigmaDetails = findStigmaDetails(originalToken);

        final StigmaRefreshDecision decision = isStigmaValid(stigmaDetails)
                ? new StigmaRefreshDecision(originalToken, TOKEN_GOOD_LOGIN_SUCCESS)
                : new StigmaRefreshDecision(newStigmaToken(), TOKEN_BAD_LOGIN_SUCCESS);

        if (logger.isDebugEnabled()) {
            logger.debug("Decision after successful login: " + decision);
        }

        writeToMetrics(decision);

        return decision;
    }

    /**
     * To be called after failed login attempt.
     * Consumes the currently used raw stigma token (might be null or empty)
     * and returns {@link StigmaRefreshDecision} to be used for further actions.
     */
    @Nonnull
    public StigmaRefreshDecision onLoginFail(@Nullable final RawStigmaToken originalToken) {

        final StigmaDetails stigmaDetails = findStigmaDetails(originalToken);

        final StigmaRefreshDecision decision;
        if (isStigmaValid(stigmaDetails)) {
            stigmaService.revokeStigma(stigmaDetails.getStigma());
            decision = new StigmaRefreshDecision(newStigmaToken(), TOKEN_GOOD_LOGIN_FAIL);
        } else {
            decision = new StigmaRefreshDecision(newStigmaToken(), TOKEN_BAD_LOGIN_FAIL);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Decision after failed login: " + decision);
        }

        writeToMetrics(decision);

        return decision;
    }

    @Nullable
    private StigmaDetails findStigmaDetails(@Nullable final RawStigmaToken stigmaToken) {

        if (stigmaToken != null) {
            final Stigma stigma = extractStigma(stigmaToken);

            return stigma != null
                    ? stigmaService.findStigmaDetails(stigma)
                    : null;
        } else {
            return null;
        }
    }

    private Stigma extractStigma(final RawStigmaToken originalToken) {
        return stigmaExtractor.extractStigma(originalToken);
    }

    private boolean isStigmaValid(final StigmaDetails stigmaDetails) {
        return stigmaValidator.isValid(stigmaDetails);
    }

    private RawStigmaToken newStigmaToken() {
        final StigmaDetails newStigma = stigmaService.getNewStigma();

        return stigmaTokenFactory.getToken(newStigma.getStigma());
    }

    private void writeToMetrics(final StigmaRefreshDecision decision) {
        // Not implemented yet.
    }
}
