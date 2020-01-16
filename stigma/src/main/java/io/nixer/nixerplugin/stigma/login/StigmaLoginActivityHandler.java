package io.nixer.nixerplugin.stigma.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.nixer.nixerplugin.core.login.LoginActivityHandler;
import io.nixer.nixerplugin.core.login.LoginContext;
import io.nixer.nixerplugin.core.login.LoginResult;
import io.nixer.nixerplugin.stigma.domain.RawStigmaToken;
import io.nixer.nixerplugin.stigma.evaluate.StigmaAction;
import io.nixer.nixerplugin.stigma.evaluate.StigmaActionEvaluator;

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
    private final StigmaActionEvaluator stigmaActionEvaluator;

    public StigmaLoginActivityHandler(final HttpServletRequest request,
                                      final HttpServletResponse response,
                                      final StigmaCookieService stigmaCookieService,
                                      final StigmaActionEvaluator stigmaActionEvaluator) {
        this.request = request;
        this.response = response;
        this.stigmaCookieService = stigmaCookieService;
        this.stigmaActionEvaluator = stigmaActionEvaluator;
    }

    @Override
    public void handle(final LoginResult loginResult, final LoginContext ignored) {

        final RawStigmaToken receivedStigmaToken = stigmaCookieService.readStigmaToken(request);

        final StigmaAction action = loginResult.isSuccess()
                ? stigmaActionEvaluator.onLoginSuccess(receivedStigmaToken)
                : stigmaActionEvaluator.onLoginFail(receivedStigmaToken);

        if (action.isTokenRefreshRequired()) {
            stigmaCookieService.writeStigmaToken(response, action.getStigmaToken());
        }
    }
}
