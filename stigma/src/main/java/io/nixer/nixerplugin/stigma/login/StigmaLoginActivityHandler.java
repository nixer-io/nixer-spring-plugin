package io.nixer.nixerplugin.stigma.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.nixer.nixerplugin.core.login.LoginActivityHandler;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.stigma.StigmaConstants;
import io.nixer.nixerplugin.stigma.decision.StigmaDecisionMaker;
import io.nixer.nixerplugin.stigma.decision.StigmaRefreshDecision;
import io.nixer.nixerplugin.stigma.domain.StigmaDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * Entry point for StigmaToken-based credential stuffing protection mechanism.
 *
 * Created on 28/11/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmaLoginActivityHandler implements LoginActivityHandler {

    private static final Log logger = LogFactory.getLog(StigmaLoginActivityHandler.class);

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    private final StigmaCookieService stigmaCookieService;
    private final StigmaDecisionMaker stigmaDecisionMaker;

    public StigmaLoginActivityHandler(final HttpServletRequest request,
                                      final HttpServletResponse response,
                                      final StigmaCookieService stigmaCookieService,
                                      final StigmaDecisionMaker stigmaDecisionMaker) {
        this.request = request;
        this.response = response;
        this.stigmaCookieService = stigmaCookieService;
        this.stigmaDecisionMaker = stigmaDecisionMaker;
    }

    @Override
    public void handle(final LoginContext context) {
        Assert.notNull(context, "LoginContext can not be null");
        Assert.state(context.getLoginResult() != null, "LoginResult from LoginContext can not be null");

        final StigmaDetails receivedStigmaDetails = (StigmaDetails) request.getAttribute(StigmaConstants.STIGMA_METADATA_ATTRIBUTE);

        if (logger.isTraceEnabled()) {
            logger.trace(String.format("Handling login attempt with result='%s' and stigma='%s'", context.getLoginResult(), receivedStigmaDetails));
        }

        final StigmaRefreshDecision refreshDecision = context.getLoginResult().isSuccess()
                ? stigmaDecisionMaker.onLoginSuccess(receivedStigmaDetails)
                : stigmaDecisionMaker.onLoginFail(receivedStigmaDetails);

        refreshDecision.apply(
                newStigmaToken -> stigmaCookieService.writeStigmaToken(response, newStigmaToken)
        );
    }
}
