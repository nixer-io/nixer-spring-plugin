package io.nixer.nixerplugin.stigma.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.nixer.nixerplugin.core.login.LoginActivityHandler;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.LoginResult;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.decision.StigmaDecision;
import io.nixer.nixerplugin.stigma.decision.StigmaDecisionMaker;

/**
 * Entry point for StigmaToken-based credential stuffing protection mechanism.
 *
 * Created on 28/11/2019.
 *
 * @author Grzegorz Cwiak (gcwiak)
 */
public class StigmaLoginActivityHandler implements LoginActivityHandler {

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
    public void handle(final LoginResult loginResult, final LoginContext ignored) {

        final RawStigmaToken receivedStigmaToken = stigmaCookieService.readStigmaToken(request);

        final StigmaDecision decision = loginResult.isSuccess()
                ? stigmaDecisionMaker.onLoginSuccess(receivedStigmaToken)
                : stigmaDecisionMaker.onLoginFail(receivedStigmaToken);

        decision.apply(
                stigmaToken -> stigmaCookieService.writeStigmaToken(response, stigmaToken)
        );
    }
}
